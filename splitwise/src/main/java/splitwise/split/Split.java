package splitwise.split;

import splitwise.model.UserId;
import java.math.BigDecimal;
import java.util.List;

/**
 * Sealed contract for all split strategies.
 * Adding a new strategy = add record + add to permits + add case in SplitStrategyFactory.
 */
public sealed interface Split permits EqualSplit, PercentageSplit, ExactSplit, ShareSplit {

    UserId userId();

    /**
     * Compute this user's monetary share from the total expense amount.
     * @param totalAmount the full expense amount
     * @param allSplits   all splits in this expense (needed for context)
     */
    BigDecimal computeShare(BigDecimal totalAmount, List<? extends Split> allSplits);

    /**
     * Validate consistency across all splits (e.g., percentages sum to 100).
     * Called once before the expense is persisted.
     */
    void validate(BigDecimal totalAmount, List<? extends Split> allSplits);
}
