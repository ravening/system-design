package splitwise.expense;
public record UtilityExpense() implements ExpenseCategory {
    @Override public String label() { return "Utilities"; }
    @Override public String icon()  { return "[UTILITY]"; }
}
