package com.piggyplugins.collectionlogluck.luck.probability;

//import org.apache.commons.math3.distribution.NormalDistribution;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

// This distribution computes the Uniform Sum distribution (a.k.a. Irwin-Hall distribution), computing exactly
// for low values of n and approximating using a normal distribution for higher n. The Normal distribution is a good
// approximation (to within ~0.2%) at n >= 12.
public class UniformSumNormalApproxDistribution {

    // The max number of distributions to calculate the exact CDF rather than using a normal distribution approximation
    private static final int EXACT_CDF_MAX_N_CUTOFF = 11;

    // the number of uniform random variables summed
    private final int n;

    public UniformSumNormalApproxDistribution(int n) {
        this.n = n;
    }

    // Return the chance of having received x or fewer items
    public double cumulativeProbability(double x) {
        if (x < 0) {
            return 0;
        }
        // This also covers the case n = 0
        if (x >= n) {
            return 1;
        }

        if (n <= EXACT_CDF_MAX_N_CUTOFF) {
            return computeExactCdf(x);
        }
        return computeApproxCdf(x);
    }

    private double computeExactCdf(double x) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int k = 0; k <= (int) x; k++) {
            // positive for even k, negative for odd k
            BigDecimal sign = BigDecimal.valueOf(k % 2 == 0 ? 1 : -1);
            BigDecimal nChooseK = new BigDecimal(binomialCoefficient(n, k));
            BigDecimal term3 = BigDecimal.valueOf(x - k).pow(n);
            sum = sum.add(sign.multiply(nChooseK).multiply(term3));
        }
        // 64 bit precision to match Java double precision
        return sum.divide(factorial(n), MathContext.DECIMAL64).doubleValue();
    }

    private double computeApproxCdf(double x) {
        double mean = n / 2.0;
        double stdDev = Math.sqrt(n / 12.0);
        NormalDistribution normalDistribution = new NormalDistribution(mean, stdDev);
        return normalDistribution.cumulativeProbability(x);
    }

    private static BigInteger binomialCoefficient(int N, int K) {
        BigInteger ret = BigInteger.ONE;
        for (int k = 0; k < K; k++) {
            ret = ret.multiply(BigInteger.valueOf(N-k))
                    .divide(BigInteger.valueOf(k+1));
        }
        return ret;
    }

    private static BigDecimal factorial(int n) {
        return factorial(BigDecimal.valueOf(n), BigDecimal.valueOf(n));
    }

    private static BigDecimal factorial(BigDecimal n, BigDecimal acc) {
        if (n.equals(BigDecimal.ONE)) {
            return acc;
        }
        BigDecimal nMinusOne = n.subtract(BigDecimal.ONE);
        return factorial(nMinusOne, acc.multiply(nMinusOne));
    }

}
