package splitwise.expense;

import java.util.Objects;

public record CustomExpense(String label, String icon) implements ExpenseCategory {
    public CustomExpense {
        Objects.requireNonNull(label, "Label cannot be null");
        Objects.requireNonNull(icon, "Icon cannot be null");
        if (label.isBlank()) throw new IllegalArgumentException("Label cannot be blank");
    }
}
