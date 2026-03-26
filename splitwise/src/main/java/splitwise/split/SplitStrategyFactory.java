package splitwise.split;

import splitwise.model.UserId;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SplitStrategyFactory {

    private SplitStrategyFactory() {}

    /**
     * Creates splits for the given type and participants.
     *
     * params contract:
     *   EQUAL      -> empty map (ignored)
     *   PERCENTAGE -> Map<UserId, BigDecimal> (percentage per user, must sum to 100)
     *   EXACT      -> Map<UserId, BigDecimal> (exact amount per user, must sum to total)
     *   SHARE      -> Map<UserId, Integer>    (share weight per user)
     */
    public static List<Split> create(SplitType type,
                                     List<UserId> participants,
                                     BigDecimal totalAmount,
                                     Map<UserId, Object> params) {
        return switch (type) {
            case EQUAL -> participants.stream()
                .<Split>map(EqualSplit::new)
                .toList();

            case PERCENTAGE -> participants.stream()
                .<Split>map(uid -> new PercentageSplit(uid,
                    (BigDecimal) Objects.requireNonNull(params.get(uid),
                        "Missing percentage for: " + uid)))
                .toList();

            case EXACT -> participants.stream()
                .<Split>map(uid -> new ExactSplit(uid,
                    (BigDecimal) Objects.requireNonNull(params.get(uid),
                        "Missing amount for: " + uid)))
                .toList();

            case SHARE -> participants.stream()
                .<Split>map(uid -> new ShareSplit(uid,
                    (Integer) Objects.requireNonNull(params.get(uid),
                        "Missing shares for: " + uid)))
                .toList();
        };
    }
}
