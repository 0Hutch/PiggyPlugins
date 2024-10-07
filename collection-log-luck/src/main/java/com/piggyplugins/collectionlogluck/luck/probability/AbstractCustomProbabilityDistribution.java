package com.piggyplugins.collectionlogluck.luck.probability;

//import org.apache.commons.math3.exception.OutOfRangeException;

import java.util.List;

abstract class AbstractCustomProbabilityDistribution {

    protected final List<Double> probabilities;

    public AbstractCustomProbabilityDistribution(List<Double> probabilities) {
        for (double p : probabilities) {
            if (p < 0 || p > 1) {
                throw new OutOfRangeException(p, 0, 1);
            }
        }

        this.probabilities = probabilities;
    }

}
