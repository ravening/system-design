package splitwise.balance;

import splitwise.model.UserId;
import java.math.BigDecimal;

public record Settlement(UserId from, UserId to, BigDecimal amount) {
    @Override public String toString() {
        return "%s  -->  %s  :  %.2f".formatted(from, to, amount);
    }
}
