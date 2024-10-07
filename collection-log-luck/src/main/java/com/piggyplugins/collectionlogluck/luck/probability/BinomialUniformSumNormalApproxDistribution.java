package com.piggyplugins.collectionlogluck.luck.probability;

import com.google.common.collect.ImmutableList;
//import org.apache.commons.math3.distribution.NormalDistribution;

// This distribution approximates the distribution of summing N identical uniform distributions after succeeding in
// N Bernoulli trials by using a normal distribution with appropriate mean and standard deviation.
// Discussion and derivation of normal approximation:
// https://math.stackexchange.com/questions/4759137/probability-distribution-of-binomial-variable-multiplied-by-a-uniform-variable
public class BinomialUniformSumNormalApproxDistribution extends AbstractCustomProbabilityDistribution {

    private final int numTrials;
    private final double successProbability;
    private final double minRollOnSuccess;
    private final double maxRollOnSuccess;

    private final double mean;
    private final double standardDeviation;

    public BinomialUniformSumNormalApproxDistribution(int numTrials, double successProbability, double minRollOnSuccess, double maxRollOnSuccess) {
        super(ImmutableList.of(successProbability));

        this.numTrials = numTrials;
        this.successProbability = successProbability;
        this.minRollOnSuccess = minRollOnSuccess;
        this.maxRollOnSuccess = maxRollOnSuccess;

        this.mean = computeMean();
        this.standardDeviation = computeStandardDeviation();
    }

    // Return the chance of having received x or fewer items
    public double cumulativeProbability(double x) {
        if (x < 0) {
            return 0;
        }
        if (x >= numTrials * maxRollOnSuccess) {
            return 1;
        }

        double continuityCorrection = 0;
        // Rule of thumb: add "continuity correction" only if n*p and n*(1-p) are both at least 5.
        if (numTrials * successProbability > 5 && numTrials * (1 - successProbability) > 5) {
            // add an entire average "half-success"
            continuityCorrection = 0.5 * (minRollOnSuccess + maxRollOnSuccess) / 2.0;
        }
        return new NormalDistribution(mean, standardDeviation).cumulativeProbability(x + continuityCorrection);
    }

    // Return the mean of the distribution (the first "moment" or "mu")
    protected double computeMean() {
        return this.numTrials * this.successProbability * (this.minRollOnSuccess + this.maxRollOnSuccess) / 2.0;
    }

    // Return the standard deviation of the distribution (the second "moment" or "sigma")
    protected double computeStandardDeviation() {
        return Math.sqrt(
                (1.0 / 12) * this.numTrials * this.successProbability * (
                        (4.0 - 3 * this.successProbability) * this.minRollOnSuccess * this.minRollOnSuccess +
                                (4.0 - 6 * this.successProbability) * this.minRollOnSuccess * this.maxRollOnSuccess +
                                (4.0 - 3 * this.successProbability) * this.maxRollOnSuccess * this.maxRollOnSuccess
                )
        );
    }

}
