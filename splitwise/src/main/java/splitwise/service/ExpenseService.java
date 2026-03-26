package splitwise.service;

import splitwise.balance.BalanceLedger;
import splitwise.balance.Settlement;
import splitwise.expense.Expense;
import splitwise.expense.ExpenseCategory;
import splitwise.model.GroupId;
import splitwise.model.Money;
import splitwise.model.User;
import splitwise.model.UserId;
import splitwise.observer.NotificationService;
import splitwise.split.Split;
import splitwise.split.SplitStrategyFactory;
import splitwise.split.SplitType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ExpenseService {

    private final Map<splitwise.model.ExpenseId, Expense> store = new ConcurrentHashMap<>();
    private final List<Settlement> settlements = new java.util.concurrent.CopyOnWriteArrayList<>();
    private final BalanceLedger ledger;
    private final NotificationService notifications;

    public ExpenseService(BalanceLedger ledger, NotificationService notifications) {
        this.ledger        = Objects.requireNonNull(ledger, "Ledger cannot be null");
        this.notifications = Objects.requireNonNull(notifications, "NotificationService cannot be null");
    }

    public Expense addExpense(String description,
                               Money amount,
                               User paidBy,
                               List<User> participants,
                               SplitType splitType,
                               Map<UserId, Object> params,
                               ExpenseCategory category,
                               Optional<GroupId> groupId) {

        var ids    = participants.stream().map(User::id).toList();
        var splits = SplitStrategyFactory.create(splitType, ids, amount.amount(), params);

        var builder = new Expense.Builder()
            .description(description)
            .amount(amount)
            .paidBy(paidBy.id())
            .splits(splits)
            .category(category);
        groupId.ifPresent(builder::group);

        var expense = builder.build();
        store.put(expense.id(), expense);
        ledger.recordExpense(expense);
        notifications.onExpenseAdded(expense, paidBy);
        return expense;
    }

    public Settlement settle(User from, User to, Money amount) {
        Objects.requireNonNull(from, "From user cannot be null");
        Objects.requireNonNull(to, "To user cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        ledger.recordSettlement(from.id(), to.id(), amount.amount());
        var settlement = new Settlement(from.id(), to.id(), amount.amount());
        settlements.add(settlement);
        notifications.onSettlement(settlement);
        return settlement;
    }

    public List<Expense> getGroupExpenses(GroupId gid) {
        return store.values().stream()
            .filter(e -> e.groupId().filter(gid::equals).isPresent())
            .sorted(Comparator.comparing(Expense::createdAt))
            .toList();
    }

    public List<Settlement> getSettlements() {
        return Collections.unmodifiableList(settlements);
    }

    public BalanceLedger ledger() { return ledger; }
}
