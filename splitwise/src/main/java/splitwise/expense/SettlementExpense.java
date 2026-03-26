package splitwise.expense;
public record SettlementExpense() implements ExpenseCategory {
    @Override public String label() { return "Settlement"; }
    @Override public String icon()  { return "[SETTLEMENT]"; }
}
