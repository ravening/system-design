package splitwise.expense;

/**
 * Sealed hierarchy for expense categories.
 * To add a new category: add to permits, create a record, add a case in CategoryFormatter.
 */
public sealed interface ExpenseCategory
        permits FoodExpense, TravelExpense, UtilityExpense,
                EntertainmentExpense, SettlementExpense, CustomExpense {
    String label();
    String icon();
}
