package splitwise.observer;

import splitwise.balance.Settlement;
import splitwise.expense.Expense;
import splitwise.model.User;

public interface ExpenseObserver {
    void onExpenseAdded(Expense expense, User addedBy);
    void onSettlement(Settlement settlement);
}
