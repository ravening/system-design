package splitwise.model;

import java.util.Objects;
import java.util.UUID;

public record UserId(String value) {
    public UserId { Objects.requireNonNull(value, "UserId value cannot be null"); }
    public static UserId generate() { return new UserId(UUID.randomUUID().toString()); }
    @Override public String toString() { return value.substring(0, 8) + ".."; }
}
