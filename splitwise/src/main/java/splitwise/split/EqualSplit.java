package splitwise.split;

import splitwise.model.UserId;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record EqualSplit(UserId userId) implements Split {

    @Override
    public BigDecimal computeShare(BigDecimal totalAmount, List<? extends Split> allSplits) {
        List<? extends Split> equalSplits = allSplits.stream()
                .filter(s -> s instanceof EqualSplit)
                .toList();
        long count = equalSplits.size();
        if (count == 0) throw new IllegalStateException("No EqualSplit participants found");

        BigDecimal baseShare = totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.DOWN);
        BigDecimal remainder = totalAmount.subtract(baseShare.multiply(BigDecimal.valueOf(count)));

        // Last participant absorbs the remainder to guarantee amounts sum to total
        boolean isLast = equalSplits.getLast().userId().equals(this.userId);
        return isLast ? baseShare.add(remainder) : baseShare;
    }

    @Override
    public void validate(BigDecimal totalAmount, List<? extends Split> allSplits) {
        if (allSplits.stream().noneMatch(s -> s instanceof EqualSplit))
            throw new IllegalArgumentException("Need at least one EqualSplit participant");
    }
}
