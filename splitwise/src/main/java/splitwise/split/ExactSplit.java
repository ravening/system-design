package splitwise.split;

import splitwise.model.UserId;
import java.math.BigDecimal;
import java.util.List;

public record ExactSplit(UserId userId, BigDecimal amount) implements Split {

    public ExactSplit {
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Exact amount cannot be negative, got: " + amount);
    }

    @Override
    public BigDecimal computeShare(BigDecimal totalAmount, List<? extends Split> allSplits) {
        return amount;
    }

    @Override
    public void validate(BigDecimal totalAmount, List<? extends Split> allSplits) {
        var sumExact = allSplits.stream()
            .filter(s -> s instanceof ExactSplit)
            .map(s -> ((ExactSplit) s).amount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumExact.compareTo(totalAmount) != 0)
            throw new IllegalArgumentException(
                "Exact amounts (%s) must equal total (%s)".formatted(sumExact, totalAmount));
    }
}
