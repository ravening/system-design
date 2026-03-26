package splitwise.expense;
public record TravelExpense() implements ExpenseCategory {
    @Override public String label() { return "Travel"; }
    @Override public String icon()  { return "[TRAVEL]"; }
}
