package splitwise.expense;

import splitwise.model.ExpenseId;
import splitwise.model.GroupId;
import splitwise.model.Money;
import splitwise.model.UserId;
import splitwise.split.Split;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Expense {
    private final ExpenseId id;
    private final String description;
    private final Money amount;
    private final UserId paidBy;
    private final List<Split> splits;
    private final ExpenseCategory category;
    private final Optional<GroupId> groupId;
    private final LocalDateTime createdAt;
    private final AtomicBoolean settled;

    private Expense(Builder b) {
        this.id          = b.id;
        this.description = b.description;
        this.amount      = b.amount;
        this.paidBy      = b.paidBy;
        this.splits      = List.copyOf(b.splits);
        this.category    = b.category;
        this.groupId     = b.groupId;
        this.createdAt   = LocalDateTime.now();
        this.settled     = new AtomicBoolean(false);
        validateSplits();
    }

    private void validateSplits() {
        if (splits.isEmpty())
            throw new IllegalStateException("Expense must have at least one split");
        // Collective validation: delegate to each unique split type's validate()
        // to cover mixed-type scenarios and avoid relying on a single element
        splits.stream()
                .map(Split::getClass)
                .distinct()
                .forEach(clazz -> splits.stream()
                        .filter(s -> s.getClass().equals(clazz))
                        .findFirst()
                        .ifPresent(s -> s.validate(amount.amount(), splits)));
    }

    /** Convenience: what does this user owe for this expense? */
    public BigDecimal shareFor(UserId uid) {
        return splits.stream()
            .filter(s -> s.userId().equals(uid))
            .findFirst()
            .map(s -> s.computeShare(amount.amount(), splits))
            .orElse(BigDecimal.ZERO);
    }

    public ExpenseId id()              { return id; }
    public String description()        { return description; }
    public Money amount()              { return amount; }
    public UserId paidBy()             { return paidBy; }
    public List<Split> splits()        { return splits; }
    public ExpenseCategory category()  { return category; }
    public Optional<GroupId> groupId() { return groupId; }
    public LocalDateTime createdAt()   { return createdAt; }
    public boolean isSettled()         { return settled.get(); }
    public boolean settle()            { return settled.compareAndSet(false, true); }

    @Override public String toString() {
        return "Expense[%s | %s | paidBy=%s | settled=%s]"
            .formatted(description, amount, paidBy, settled.get());
    }

    // ── Builder ───────────────────────────────────────────────────────────────
    public static final class Builder {
        private final ExpenseId id = ExpenseId.generate();
        private String description;
        private Money amount;
        private UserId paidBy;
        private List<Split> splits       = List.of();
        private ExpenseCategory category = new CustomExpense("Other", "[OTHER]");
        private Optional<GroupId> groupId = Optional.empty();

        public Builder description(String d)      { this.description = d; return this; }
        public Builder amount(Money a)             { this.amount = a;      return this; }
        public Builder paidBy(UserId u)            { this.paidBy = u;      return this; }
        public Builder splits(List<Split> s)       { this.splits = s;      return this; }
        public Builder category(ExpenseCategory c) { this.category = c;    return this; }
        public Builder group(GroupId g)            { this.groupId = Optional.of(g); return this; }

        public Expense build() {
            Objects.requireNonNull(description, "Description is required");
            Objects.requireNonNull(amount,      "Amount is required");
            Objects.requireNonNull(paidBy,      "PaidBy is required");
            if (splits.isEmpty()) throw new IllegalStateException("At least one split is required");
            return new Expense(this);
        }
    }
}
