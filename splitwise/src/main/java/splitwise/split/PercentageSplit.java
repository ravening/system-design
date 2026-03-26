package splitwise.split;

import splitwise.model.UserId;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record PercentageSplit(UserId userId, BigDecimal percentage) implements Split {

    public PercentageSplit {
        if (percentage.compareTo(BigDecimal.ZERO) <= 0 ||
            percentage.compareTo(new BigDecimal("100")) > 0)
            throw new IllegalArgumentException("Percentage must be in (0, 100], got: " + percentage);
    }

    @Override
    public BigDecimal computeShare(BigDecimal totalAmount, List<? extends Split> allSplits) {
        return totalAmount.multiply(percentage)
                          .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    @Override
    public void validate(BigDecimal totalAmount, List<? extends Split> allSplits) {
        var sum = allSplits.stream()
            .filter(s -> s instanceof PercentageSplit)
            .map(s -> ((PercentageSplit) s).percentage())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sum.compareTo(new BigDecimal("100")) != 0)
            throw new IllegalArgumentException("Percentages must sum to 100, got: " + sum);
    }
}
