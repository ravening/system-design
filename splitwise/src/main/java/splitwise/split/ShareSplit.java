package splitwise.split;

import splitwise.model.UserId;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record ShareSplit(UserId userId, int shares) implements Split {

    public ShareSplit {
        if (shares <= 0) throw new IllegalArgumentException("Shares must be > 0, got: " + shares);
    }

    @Override
    public BigDecimal computeShare(BigDecimal totalAmount, List<? extends Split> allSplits) {
        int totalShares = allSplits.stream()
            .filter(s -> s instanceof ShareSplit)
            .mapToInt(s -> ((ShareSplit) s).shares())
            .sum();
        if (totalShares == 0) throw new IllegalStateException("Total shares cannot be zero");
        return totalAmount.multiply(BigDecimal.valueOf(shares))
                          .divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);
    }

    @Override
    public void validate(BigDecimal totalAmount, List<? extends Split> allSplits) {
        // Valid as long as each share > 0, enforced in compact constructor
    }
}
