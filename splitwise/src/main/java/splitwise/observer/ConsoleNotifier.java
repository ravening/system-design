package splitwise.observer;

import splitwise.balance.Settlement;
import splitwise.expense.CategoryFormatter;
import splitwise.expense.Expense;
import splitwise.model.User;

public final class ConsoleNotifier implements ExpenseObserver {

    @Override
    public void onExpenseAdded(Expense e, User by) {
        System.out.printf("[NOTIFY] %s added '%s' (%s)  category: %s%n",
            by.name(), e.description(), e.amount(), CategoryFormatter.format(e.category()));
    }

    @Override
    public void onSettlement(Settlement s) {
        System.out.printf("[NOTIFY] Settlement: %s%n", s);
    }
}
