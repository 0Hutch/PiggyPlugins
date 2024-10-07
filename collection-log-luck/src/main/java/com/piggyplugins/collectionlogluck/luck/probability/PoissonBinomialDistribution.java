package com.piggyplugins.collectionlogluck.luck.probability;

import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// An implementation of the Poisson Binomial Distribution which is performant up to several hundred trials
// NOTE: This implementation returns all NaNs if any probability equals 1!
public class PoissonBinomialDistribution extends AbstractCustomProbabilityDistribution {

    private List<Double> probabilityMassFunction;
    private List<Double> cumulativeDensityFunction;

    public PoissonBinomialDistribution(List<Double> probabilities) {
        super(probabilities);
    }

    // Return the probability of numSuccesses == x
    public double probability(int x) {
        if (x < 0 || x > probabilities.size()) {
            return 0;
        }
        return getPmf(x).get(x);
    }

    // Return the probability of numSuccesses <= x
    public double cumulativeProbability(int x) {
        if (x < 0) {
            return 0;
        }
        if (x > probabilities.size()) {
            return 1;
        }
        return getCdf(x).get(x);
    }

    // Return the probability of numSuccesses == x for all possible values of x <= maxX, (re)calculating if necessary
    public List<Double> getPmf(int maxX) {
        if (probabilityMassFunction == null || probabilityMassFunction.size() <= maxX) {
            probabilityMassFunction = calculatePmf(maxX);
        }
        return probabilityMassFunction;
    }

    // Return the probability of numSuccesses <= x for all possible values of x <= maxX, (re)calculating if necessary
    public List<Double> getCdf(int maxX) {
        if (cumulativeDensityFunction == null || cumulativeDensityFunction.size() <= maxX) {
            cumulativeDensityFunction = calculateCdf(maxX);
        }
        return cumulativeDensityFunction;
    }

    // Simply accumulate the PDF to get the CDF
    protected List<Double> calculateCdf(int maxX) {
        Double[] pmf = getPmf(maxX).toArray(new Double[0]);
        Arrays.parallelPrefix(pmf, Double::sum);
        return Arrays.asList(pmf);
    }

    protected List<Double> calculatePmf(int maxX) {
        if (maxX < 0) {
            return Collections.emptyList();
        }
        if (probabilities.isEmpty()) {
            // the probability of the sum of 0 numbers equaling 0 is 1
            return ImmutableList.of(1.0);
        }

        List<BigDecimal> w = probabilities.stream()
                .map(BigDecimal::new)
                // temporarily replace p with 0 if p was equal to 1, and add back at the very end to avoid errors
                .map(p -> p.divide(BigDecimal.ONE.subtract(p), MathContext.DECIMAL128))
                .collect(Collectors.toList());

        BigDecimal z = w.stream().reduce(BigDecimal.ONE,
                (subtotal, p) -> subtotal.divide(BigDecimal.ONE.add(p), MathContext.DECIMAL128));
        List<BigDecimal> pmf = new ArrayList<>(Collections.nCopies(w.size() + 1, BigDecimal.ONE));
        pmf.set(probabilities.size(), z);

        int maxXRequired = Math.min(maxX, probabilities.size());
        for (int i = 1; i <= maxXRequired; i++) {
            BigDecimal s = BigDecimal.ZERO;
            int m = probabilities.size() - i;
            int k = i - 1;

            for (int j = 0; j <= m; j++) {
                s = s.add(pmf.get(j).multiply(w.get(k + j)));
                pmf.set(j, s);
            }

            BigDecimal newR = pmf.get(m).multiply(z);
            pmf.set(m, newR);
        }

        Collections.reverse(pmf);

        // return only values <= maxX
        return pmf.subList(0, maxX + 1).stream()
                .mapToDouble(BigDecimal::doubleValue)
                .boxed()
                .collect(Collectors.toList());
    }

}
