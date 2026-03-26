package splitwise.expense;
public record EntertainmentExpense() implements ExpenseCategory {
    @Override public String label() { return "Entertainment"; }
    @Override public String icon()  { return "[ENTERTAINMENT]"; }
}
