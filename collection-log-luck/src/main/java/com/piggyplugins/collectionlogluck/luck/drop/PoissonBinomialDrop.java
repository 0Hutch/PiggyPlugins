package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.model.CollectionLog;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;
import com.piggyplugins.collectionlogluck.model.CollectionLogKillCount;
import com.piggyplugins.collectionlogluck.luck.LogItemSourceInfo;
import com.piggyplugins.collectionlogluck.luck.RollInfo;
import com.piggyplugins.collectionlogluck.luck.probability.PoissonBinomialDistribution;
import com.piggyplugins.collectionlogluck.luck.probability.PoissonBinomialRefinedNormalApproxDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// A drop that follows the Poisson binomial distribution (used for drops that are obtained from multiple activities
// or bosses where the drop chances are not necessarily equal).
public class PoissonBinomialDrop extends AbstractDrop {

    // If numSuccesses > this value, use the Refined Normal Approximation instead of the exact distribution.
    private static final int NORMAL_APPROX_NUM_SUCCESSES_THRESHOLD = 100;

    // If numTrials > this value, use the Refined Normal Approximation instead of the exact distribution.
    // This is necessary more for performance than accuracy.
    private static final int NORMAL_APPROX_NUM_TRIALS_THRESHOLD = 500;

    public PoissonBinomialDrop(List<RollInfo> rollInfos) {
        super(rollInfos);
    }

    // Duplicate all drop source's probabilities by the number of respective KC
    private List<Double> convertKcToProbabilities(CollectionLog collectionLog, CollectionLogLuckConfig config) {
        List<Double> probabilities = new ArrayList<>();

        for (int i = 0; i < rollInfos.size(); i++) {
            RollInfo rollInfo = rollInfos.get(i);

            CollectionLogKillCount kc = collectionLog.searchForKillCount(rollInfo.getDropSource().getName());
            if (kc != null) {
                int numRolls = (int) Math.round(kc.getAmount() * getRollsPerKc(rollInfo, config));
                numRolls = getNumRollsForCustomDrops(rollInfo, i, numRolls, config);

                double dropChance = getDropChance(rollInfo, collectionLog, config);

                probabilities.addAll(Collections.nCopies(numRolls, dropChance));
            }
        }

        return probabilities;
    }

    private double getExactOrApproxCumulativeProbability(int numSuccesses, int numTrials, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        List<Double> probabilities = convertKcToProbabilities(collectionLog, config);

        if (numSuccesses > NORMAL_APPROX_NUM_SUCCESSES_THRESHOLD
                || numTrials > NORMAL_APPROX_NUM_TRIALS_THRESHOLD) {
            return new PoissonBinomialRefinedNormalApproxDistribution(probabilities)
                    .cumulativeProbability(numSuccesses);
        } else {
            return new PoissonBinomialDistribution(probabilities)
                    .cumulativeProbability(numSuccesses);
        }
    }

    @Override
    public double calculateLuck(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        int numSuccesses = getNumSuccesses(item, collectionLog, config);
        if (numSuccesses <= 0) {
            return 0;
        }
        int numTrials = getNumTrials(collectionLog, config);
        if (numSuccesses > numTrials) {
            // this can happen if a drop source is not accounted for
            return -1;
        }

        return getExactOrApproxCumulativeProbability(numSuccesses - 1, numTrials, collectionLog, config);
    }

    @Override
    public double calculateDryness(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        int numSuccesses = getNumSuccesses(item, collectionLog, config);
        int numTrials = getNumTrials(collectionLog, config);
        if (numTrials <= 0) {
            return 0;
        }
        if (numSuccesses > numTrials) {
            // this can happen if a drop source is not accounted for
            return -1;
        }

        int maxEquivalentNumSuccesses = getMaxEquivalentNumSuccesses(item, collectionLog, config);

        return 1 - getExactOrApproxCumulativeProbability(maxEquivalentNumSuccesses, numTrials, collectionLog, config);
    }

    private int getNumRollsForCustomDrops(RollInfo rollInfo, int rollInfoIndex, int numRolls, CollectionLogLuckConfig config) {
        if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.TZTOK_JAD_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.NUM_FIRE_CAPES_SACRIFICED_KEY)
        ) {
            // Only the first KC should be at non-slayer task drop chance
            if (rollInfoIndex == 0) {
                return Math.min(1, numRolls);
            }
            // All other kc should be at slayer task probability
            else if (rollInfoIndex == 1) {
                return numRolls - Math.min(1, numRolls);
            }
            // add probabilities for cape sacrifices
            else if (rollInfoIndex == 2) {
                // The player cannot have sacrificed more capes than they have KC
                return Math.max(0, Math.min(numRolls, config.numFireCapesSacrificed()));
            }
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.TZKAL_ZUK_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.NUM_INFERNAL_CAPES_SACRIFICED_KEY)
        ) {
            // Only the first KC should be at non-slayer task drop chance
            if (rollInfoIndex == 0) {
                return Math.min(1, numRolls);
            }
            // All other kc should be at slayer task probability
            else if (rollInfoIndex == 1) {
                return numRolls - Math.min(1, numRolls);
            }
            // add probabilities for cape sacrifices
            else if (rollInfoIndex == 2) {
                // The player cannot have sacrificed more capes than they have KC
                return Math.max(0, Math.min(numRolls, config.numInfernalCapesSacrificed()));
            }
        } else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.SKOTIZO_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.SKOTIZO_KC_PRE_BUFF_KEY)) {
            // jar of darkness pre-buff
            if (rollInfoIndex == 0) {
                // The player cannot have more pre-buff KC than they have KC
                return Math.max(0, Math.min(numRolls, config.skotizoKcPreBuff()));
            }
            // jar of darkness post-buff
            else if (rollInfoIndex == 1) {
                return numRolls - Math.max(0, Math.min(numRolls, config.skotizoKcPreBuff()));
            }
        } else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.KALPHITE_QUEEN_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.KQ_KC_PRE_D_PICK_BUFF_KEY)) {
            return numRolls - Math.max(0, Math.min(numRolls, config.kqKcPreDPickBuff()));
        } else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.KING_BLACK_DRAGON_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.KBD_KC_PRE_D_PICK_BUFF_KEY)) {
            // d pick kc pre-buff
            if (rollInfoIndex == 0) {
                // The player cannot have more pre-buff KC than they have KC
                return Math.max(0, Math.min(numRolls, config.kbdKcPreDPickBuff()));
            }
            // d pick kc post-buff
            else if (rollInfoIndex == 1) {
                return numRolls - Math.max(0, Math.min(numRolls, config.kbdKcPreDPickBuff()));
            }
        } else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.NIGHTMARE_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.NIGHTMARE_KC_PRE_BUFF_KEY)) {
            // Nightmare kc pre-buff
            if (rollInfoIndex == 0) {
                // The player cannot have more pre-buff KC than they have KC
                return Math.max(0, Math.min(numRolls, config.nightmareKcPreBuff()));
            }
            // Nightmare kc post-buff
            else if (rollInfoIndex == 1) {
                return numRolls - Math.max(0, Math.min(numRolls, config.nightmareKcPreBuff()));
            }
        } else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.PHOSANIS_NIGHTMARE_KC_PRE_BUFF_KEY)) {
            // Phosani's Nightmare kc pre-buff.
            if (rollInfoIndex == 2) {
                // The player cannot have more pre-buff KC than they have KC
                return Math.max(0, Math.min(numRolls, config.phosanisNightmareKcPreBuff()));
            }
            // Phosani's Nightmare kc post-buff
            else if (rollInfoIndex == 3) {
                return numRolls - Math.max(0, Math.min(numRolls, config.phosanisNightmareKcPreBuff()));
            }
        }

        return numRolls;
    }

}
