package splitwise.observer;

import splitwise.balance.Settlement;
import splitwise.expense.Expense;
import splitwise.model.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class NotificationService implements ExpenseObserver {

    private final List<ExpenseObserver> observers = new CopyOnWriteArrayList<>();

    public void subscribe(ExpenseObserver o)   { observers.add(o); }
    public void unsubscribe(ExpenseObserver o)  { observers.remove(o); }

    @Override
    public void onExpenseAdded(Expense e, User by) {
        for (ExpenseObserver o : observers) {
            try {
                o.onExpenseAdded(e, by);
            } catch (Exception ex) {
                System.err.printf("Observer %s failed on onExpenseAdded: %s%n",
                        o.getClass().getSimpleName(), ex.getMessage());
            }
        }
    }

    @Override
    public void onSettlement(Settlement s) {
        for (ExpenseObserver o : observers) {
            try {
                o.onSettlement(s);
            } catch (Exception ex) {
                System.err.printf("Observer %s failed on onSettlement: %s%n",
                        o.getClass().getSimpleName(), ex.getMessage());
            }
        }
    }
}
