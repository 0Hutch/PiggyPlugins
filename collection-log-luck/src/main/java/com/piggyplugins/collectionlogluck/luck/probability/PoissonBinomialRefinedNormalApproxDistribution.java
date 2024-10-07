package com.piggyplugins.collectionlogluck.luck.probability;

//import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.List;

// An implementation of the Poisson Binomial Distribution using the Refined Normal Approximation method based on
// A. Yu. Volkova, A refinement of the central limit theorem for
// sums of independent random indicators, Teor. Veroyatnost. i
// Primenen., 1995, Volume 40, Issue 4, 885–888
public class PoissonBinomialRefinedNormalApproxDistribution extends AbstractCustomProbabilityDistribution {

    private final double mean;
    private final double standardDeviation;
    private final double skewness;

    public PoissonBinomialRefinedNormalApproxDistribution(List<Double> probabilities) {
        super(probabilities);

        this.mean = computeMean();
        this.standardDeviation = computeStandardDeviation();
        this.skewness = computeSkewness(standardDeviation);
    }

    public double cumulativeProbability(int x) {
        if (x < 0) {
            return 0;
        }
        if (x > probabilities.size()) {
            return 1;
        }
        return Math.max(0, Math.min(1, refinedNormalApproximation(x)));
    }

    // Return the mean of the distribution (the first "moment" or "mu")
    protected double computeMean() {
        return probabilities.stream().mapToDouble(Double::doubleValue).sum();
    }

    // Return the standard deviation of the distribution (the second "moment" or "sigma")
    protected double computeStandardDeviation() {
        return Math.sqrt(
                probabilities.stream().mapToDouble(
                        p -> p * (1 - p)).sum()
        );
    }

    // Return the skewness of the distribution (the third "moment" or "gamma")
    protected double computeSkewness(double standardDeviation) {
        return Math.pow(standardDeviation, -3) * probabilities.stream().mapToDouble(
                p -> p * (1 - p) * (1 - 2 * p)).sum();
    }

    // the cumulative distribution function (CDF) of the standard normal distribution
    protected double normalCdf(double x) {
        return new NormalDistribution().cumulativeProbability(x);
    }

    // the probability density function (PDF) of the standard normal distribution
    protected double normalPdf(double x) {
        return new NormalDistribution().density(x);
    }

    protected double refinedNormalApproximation(int x) {
        return g((x + 0.5 - mean) / standardDeviation);
    }

    // function used to help compute refined normal approximation
    protected double g(double x) {
        return normalCdf(x) + skewness * (1 - x * x) * normalPdf(x) / 6.0;
    }

}
