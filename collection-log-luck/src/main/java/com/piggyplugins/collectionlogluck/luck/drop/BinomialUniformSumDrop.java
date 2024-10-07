package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.model.CollectionLog;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;
import com.piggyplugins.collectionlogluck.luck.RollInfo;
import com.piggyplugins.collectionlogluck.luck.probability.BinomialDiscreteApproxUniformSumDistribution;
import com.piggyplugins.collectionlogluck.luck.probability.BinomialUniformSumNormalApproxDistribution;
import com.google.common.collect.ImmutableList;

import java.util.List;

// A drop that has both drop chance and a quantity range. For example, a boss may have a chance to drop a stack of coins
// of variable size, and you would like to know the chance of having received <= X coins in N kills.
public class BinomialUniformSumDrop extends BinomialDrop {

    protected static final int NORMAL_APPROX_NUM_SUCCESSES_THRESHOLD = 100;

    private final long minRollOnSuccess;
    private final long maxRollOnSuccess;

    public BinomialUniformSumDrop(RollInfo rollInfo, int minRollOnSuccess, int maxRollOnSuccess) {
        this(ImmutableList.of(rollInfo), minRollOnSuccess, maxRollOnSuccess);
    }

    public BinomialUniformSumDrop(List<RollInfo> rollInfos, int minRollOnSuccess, int maxRollOnSuccess) {
        super(rollInfos);

        this.minRollOnSuccess = minRollOnSuccess;
        this.maxRollOnSuccess = maxRollOnSuccess;
    }

    private double getExactOrApproxCumulativeProbability(long numReceived, double dropChance, int numTrials) {
        double expectedSuccesses = numTrials * dropChance;

        if (expectedSuccesses > NORMAL_APPROX_NUM_SUCCESSES_THRESHOLD) {
            return new BinomialUniformSumNormalApproxDistribution(numTrials, dropChance, minRollOnSuccess, maxRollOnSuccess)
                    .cumulativeProbability(numReceived);
        } else {
            return new BinomialDiscreteApproxUniformSumDistribution(numTrials, dropChance, minRollOnSuccess, maxRollOnSuccess)
                    .cumulativeProbability(numReceived);
        }
    }

    @Override
    public double calculateLuck(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        long numReceived = item.getQuantity();
        if (numReceived <= 0) {
            return 0;
        }
        long numTrials = getNumTrials(collectionLog, config);
        if (numReceived > numTrials * maxRollOnSuccess || numReceived == Integer.MAX_VALUE) {
            // this can happen if a drop source is not accounted for
            return -1;
        }

        double dropChance = getDropChance(rollInfos.get(0), collectionLog, config);

        return getExactOrApproxCumulativeProbability(numReceived - 1,
                dropChance,
                (int) numTrials);
    }

    @Override
    public double calculateDryness(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        long numReceived = item.getQuantity();
        long numTrials = getNumTrials(collectionLog, config);
        if (numTrials <= 0) {
            return 0;
        }
        if (numReceived > numTrials * maxRollOnSuccess || numReceived == Integer.MAX_VALUE) {
            // this can happen if a drop source is not accounted for
            return -1;
        }

        double dropChance = getDropChance(rollInfos.get(0), collectionLog, config);

        return 1 - getExactOrApproxCumulativeProbability(numReceived,
                dropChance,
                (int) numTrials);
    }

}
