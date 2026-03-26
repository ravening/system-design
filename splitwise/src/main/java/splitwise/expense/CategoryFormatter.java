package splitwise.expense;

public final class CategoryFormatter {

    private CategoryFormatter() {}

    /**
     * Exhaustive pattern match — compiler errors if a new permitted type is unhandled.
     * Each branch delegates to the common interface methods; extend with
     * category-specific formatting when needed.
     */
    public static String format(ExpenseCategory cat) {
        return switch (cat) {
            case FoodExpense f          -> cat.icon() + " " + cat.label();
            case TravelExpense t        -> cat.icon() + " " + cat.label();
            case UtilityExpense u       -> cat.icon() + " " + cat.label();
            case EntertainmentExpense e -> cat.icon() + " " + cat.label();
            case SettlementExpense s    -> cat.icon() + " " + cat.label();
            case CustomExpense c        -> cat.icon() + " " + cat.label();
        };
    }
}
