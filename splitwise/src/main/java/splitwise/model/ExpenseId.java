package splitwise.model;

import java.util.Objects;
import java.util.UUID;

public record ExpenseId(String value) {
    public ExpenseId { Objects.requireNonNull(value, "ExpenseId value cannot be null"); }
    public static ExpenseId generate() { return new ExpenseId(UUID.randomUUID().toString()); }
    @Override public String toString() { return value.substring(0, 8) + ".."; }
}
