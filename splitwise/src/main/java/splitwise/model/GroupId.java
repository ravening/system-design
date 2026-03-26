package splitwise.model;

import java.util.Objects;
import java.util.UUID;

public record GroupId(String value) {
    public GroupId { Objects.requireNonNull(value, "GroupId value cannot be null"); }
    public static GroupId generate() { return new GroupId(UUID.randomUUID().toString()); }
    @Override public String toString() { return value.substring(0, 8) + ".."; }
}
