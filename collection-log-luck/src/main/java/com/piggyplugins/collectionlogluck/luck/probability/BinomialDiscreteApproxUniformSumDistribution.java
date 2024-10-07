package com.piggyplugins.collectionlogluck.luck.probability;

import com.google.common.collect.ImmutableList;
//import org.apache.commons.math3.distribution.BinomialDistribution;

// This class approximates the distribution of receiving a given amount of loot when there is both a drop chance and a
// quantity range. The PDF is computed by multiplying the binomial chance of every possible number of successes X
// by a continuity-corrected continuous uniform sum distribution with n = X, using the number of items received as a
// fraction of the maximum possible number of items received.
// https://math.stackexchange.com/questions/4759137/probability-distribution-of-binomial-variable-multiplied-by-a-uniform-variable
public class BinomialDiscreteApproxUniformSumDistribution extends AbstractCustomProbabilityDistribution {

    private final int numTrials;
    private final double successProbability;
    private final double minRollOnSuccess;
    private final double maxRollOnSuccess;

    public BinomialDiscreteApproxUniformSumDistribution(int numTrials, double successProbability, double minRollOnSuccess, double maxRollOnSuccess) {
        super(ImmutableList.of(successProbability));

        if (maxRollOnSuccess == minRollOnSuccess) {
            throw new IllegalArgumentException("minRoll and maxRoll cannot be equal. Consider using a different distribution" +
                    " if this is intended.");
        }
        if (maxRollOnSuccess == 0) {
            throw new IllegalArgumentException("maxRoll must be > 0");
        }

        this.numTrials = numTrials;
        this.successProbability = successProbability;
        this.minRollOnSuccess = minRollOnSuccess;
        this.maxRollOnSuccess = maxRollOnSuccess;
    }

    // Return the chance of having received x or fewer items
    public double cumulativeProbability(double numReceived) {
        if (numReceived < 0) {
            return 0;
        }
        if (numReceived == 0 && numTrials == 0) {
            return 1;
        }

        BinomialDistribution binomialDist = new BinomialDistribution(numTrials, successProbability);

        // The cumulative probability = sum across this PDF * the uniform sum distribution cumulative probability of x
        double cumProb = 0;

        int maxPossibleSuccesses = numTrials;
        // if minRollOnSuccess = 0, the number of successes could be arbitrarily large while still receiving 0 items
        if (minRollOnSuccess > 0) {
            maxPossibleSuccesses = (int) Math.floor(numReceived / minRollOnSuccess);
        }
        int minPossibleSuccesses = (int) Math.ceil(numReceived / maxRollOnSuccess);

        // There is no need to compute the binomial PDF * uniform sum distribution CDF for all
        // numSuccesses < minPossibleSuccesses, because the uniform sum distribution's cumulativeProbability will = 1
        cumProb += binomialDist.cumulativeProbability(minPossibleSuccesses - 1);

        for (int numSuccesses = minPossibleSuccesses; numSuccesses <= maxPossibleSuccesses; numSuccesses++) {
            // Exit early if the cumulative probability is already maxed out.
            if (cumProb >= 0.99999999999999) {
                return 1;
            }
            if (numSuccesses == 0) {
                // no need to multiply by uniform sum distribution since no successful roll was performed
                cumProb += binomialDist.probability(0);
                continue;
            }
            // It should be impossible for this to be <= 0 or > 1, because that would imply > max rolls every time, or
            // < min rolls every time.
            double rollFraction = (numReceived - minRollOnSuccess * numSuccesses + 1) /
                    ((maxRollOnSuccess - minRollOnSuccess) * numSuccesses + 1);
            double numReceivedInUniformScale = rollFraction * numSuccesses;

            double chanceOfNumSuccesses = binomialDist.probability(numSuccesses);
            UniformSumNormalApproxDistribution uniformSumDistribution = new UniformSumNormalApproxDistribution(numSuccesses);
            double cumProbAssumingNumSuccesses = uniformSumDistribution.cumulativeProbability(numReceivedInUniformScale);

            cumProb += chanceOfNumSuccesses * cumProbAssumingNumSuccesses;
        }

        return cumProb;
    }

}
