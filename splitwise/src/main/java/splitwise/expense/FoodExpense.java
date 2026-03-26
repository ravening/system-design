package splitwise.expense;
public record FoodExpense() implements ExpenseCategory {
    @Override public String label() { return "Food & Drinks"; }
    @Override public String icon()  { return "[FOOD]"; }
}
