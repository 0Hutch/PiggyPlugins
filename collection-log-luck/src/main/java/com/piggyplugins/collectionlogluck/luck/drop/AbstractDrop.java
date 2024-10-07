package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.model.CollectionLog;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;
import com.piggyplugins.collectionlogluck.model.CollectionLogKillCount;
import com.piggyplugins.collectionlogluck.luck.LogItemInfo;
import com.piggyplugins.collectionlogluck.luck.LogItemSourceInfo;
import com.piggyplugins.collectionlogluck.luck.RollInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Describes the probability distribution for a drop
public abstract class AbstractDrop implements DropLuck {

    public static final String INCALCULABLE_MISSING_CONFIG =
            "Collection Log Luck plugin does not support calculating another player's luck for ";

    protected final List<RollInfo> rollInfos;

    protected List<String> configOptions;

    protected String itemName;

    public AbstractDrop(List<RollInfo> rollInfos) {
        this.rollInfos = rollInfos;

        if (rollInfos.isEmpty()) {
            throw new IllegalArgumentException("At least one RollInfo is required.");
        }

        this.configOptions = new ArrayList<>();
    }

    /**
     * Any subclass may make minor modifications to its calculations by using a plugin configuration setting.
     * This helps correct inflated KC for various reasons, correct inflated # items received, etc.
     */
    public AbstractDrop withConfigOption(String configOption) {
        this.configOptions.add(configOption);
        return this;
    }

    @Override
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String getIncalculableReason(CollectionLogItem item, CollectionLogLuckConfig config) {
        // This drop needs custom behavior defined in client configs, but these are only available client-side.
        if (config == null && !configOptions.isEmpty()) {
            return INCALCULABLE_MISSING_CONFIG + item.getName();
        }
        if (configOptions.contains(CollectionLogLuckConfig.BARROWS_BOLT_RACKS_ENABLED_KEY)
                && !config.barrowsBoltRacksEnabled()
        ) {
            return "Barrows bolt racks are disabled in the config settings.";
        }
        return null;
    }

    @Override
    public String getKillCountDescription(CollectionLog collectionLog) {
        return rollInfos.stream()
                .map(roll -> roll.getDropSource().getName())
                .distinct()
                .map(collectionLog::searchForKillCount)
                // filter out nulls just in case
                .filter(Objects::nonNull)
                // sort by kc, descending
                .sorted(Comparator.comparing(CollectionLogKillCount::getAmount).reversed())
                .map(kc -> kc.getAmount() + "x " + kc.getName())
                .collect(Collectors.joining(", "));
    }

    protected int getNumTrials(CollectionLog collectionLog, CollectionLogLuckConfig config) {
        double numTrials = 0;

        for (RollInfo rollInfo : rollInfos) {
            CollectionLogKillCount killCount = collectionLog.searchForKillCount(rollInfo.getDropSource().getName());
            double rollsPerKc = getRollsPerKc(rollInfo, config);

            // filter out nulls just in case
            if (killCount == null) continue;

            int kc = killCount.getAmount();

            if (rollInfo.getDropSource().equals(LogItemSourceInfo.BARROWS_CHESTS_OPENED)
                && configOptions.contains(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY)) {
                kc -= Math.max(0, Math.min(kc, config.numInvalidBarrowsKc()));
            }

            numTrials += kc * rollsPerKc;
        }

        return (int) Math.round(numTrials);
    }

    protected int getNumSuccesses(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        int numSuccesses = item.getQuantity();

        // Note: The Abyssal Lantern is now purchasable from the shop, and it triggers the collection log unlock, but
        // purchased lanterns shouldn't count towards the luck of lanterns received from the Rewards Guardian
        if (configOptions.contains(CollectionLogLuckConfig.NUM_ABYSSAL_LANTERNS_PURCHASED_KEY)) {
            numSuccesses -= Math.max(0, Math.min(numSuccesses, config.numAbyssalLanternsPurchased()));
        }
        // Note: Crystal weapon seeds can be purchased from the LMS shop, and it appears to trigger collection log unlock,
        // but purchased seeds shouldn't count towards the luck of seeds received from the Gauntlet
        if (configOptions.contains(CollectionLogLuckConfig.NUM_CRYSTAL_WEAPON_SEEDS_PURCHASED_KEY)) {
            numSuccesses -= Math.max(0, Math.min(numSuccesses, config.numCrystalWeaponSeedsPurchased()));
        }

        return numSuccesses;
    }

    // the max number of successes that a player could have and still be considered "in the same boat" as you, luck-wise
    // In the vast majority of cases, this is equal to getNumSuccesses.
    protected int getMaxEquivalentNumSuccesses(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        return getNumSuccesses(item, collectionLog, config);
    }

    protected double getRollsPerKc(RollInfo rollInfo, CollectionLogLuckConfig config) {
        double rollsPerKc = rollInfo.getRollsPerKc();

        if (rollInfo.getDropSource().equals(LogItemSourceInfo.WINTERTODT_KILLS)
                && configOptions.contains(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY)) {
            rollsPerKc *= config.numRollsPerWintertodtCrate();
        }

        return rollsPerKc;
    }

    protected double getDropChance(RollInfo rollInfo, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        double dropChance = rollInfo.getDropChancePerRoll();

        // Check both the drop source as well as the item name or modifier name, since checking the drop source is
        // necessary for multi-source drops where each drop source behaves differently (e.g. Nightmare and Phosani's Nightmare)
        if (rollInfo.getDropSource().equals(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS)
            && configOptions.contains(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)) {
            dropChance *= getCoxUniqueChanceFromPoints(config.avgPersonalCoxPoints());
        }
        else if (rollInfo.getDropSource().equals(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS)
            && configOptions.contains(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY)) {
            dropChance *= getCoxUniqueChanceFromPoints(config.avgPersonalCoxCmPoints());
        }
        else if (rollInfo.getDropSource().equals(LogItemSourceInfo.THEATRE_OF_BLOOD_COMPLETIONS)
            && configOptions.contains(CollectionLogLuckConfig.AVG_PERSONAL_TOB_POINTS_KEY)) {
            dropChance *= clampContribution(config.avgPersonalTobPointFraction());
        }
        else if (rollInfo.getDropSource().equals(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS)
                && configOptions.contains(CollectionLogLuckConfig.AVG_PERSONAL_TOB_HM_POINTS_KEY)) {
            dropChance *= clampContribution(config.avgPersonalTobHmPointFraction());
        }
        else if (
            rollInfo.getDropSource().equals(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS)
                    && configOptions.contains(CollectionLogLuckConfig.ENTRY_TOA_UNIQUE_CHANCE_KEY)) {
            dropChance *= getToAUniqueChance(config.entryToaUniqueChance());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS)
                        && configOptions.contains(CollectionLogLuckConfig.REGULAR_TOA_UNIQUE_CHANCE_KEY)) {
            dropChance *= getToAUniqueChance(config.regularToaUniqueChance());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS)
                        && configOptions.contains(CollectionLogLuckConfig.EXPERT_TOA_UNIQUE_CHANCE_KEY)) {
            dropChance *= getToAUniqueChance(config.expertToaUniqueChance());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS)
                        && configOptions.contains(LogItemInfo.TUMEKENS_GUARDIAN_27352.getItemName())) {
            return getToAPetChance(config.entryToaUniqueChance());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS)
                        && configOptions.contains(LogItemInfo.TUMEKENS_GUARDIAN_27352.getItemName())) {
            return getToAPetChance(config.regularToaUniqueChance());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS)
                        && configOptions.contains(LogItemInfo.TUMEKENS_GUARDIAN_27352.getItemName())) {
            return getToAPetChance(config.expertToaUniqueChance());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.NIGHTMARE_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.AVG_NIGHTMARE_TEAM_SIZE_KEY)
                        && configOptions.contains(CollectionLogLuckConfig.AVG_NIGHTMARE_REWARDS_FRACTION_KEY)
        ) {
            dropChance *= getNightmareUniqueShare(config.avgNightmareTeamSize(), config.avgNightmareRewardsFraction());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.NIGHTMARE_KILLS)
                        && configOptions.contains(LogItemInfo.JAR_OF_DREAMS_24495.getItemName())
        ) {
            dropChance *= getNightmareJarModifier(config.avgNightmareTeamSize());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.NIGHTMARE_KILLS)
                        && configOptions.contains(LogItemInfo.LITTLE_NIGHTMARE_24491.getItemName())
        ) {
            dropChance *= getNightmarePetShare(config.avgNightmareTeamSize());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.NEX_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.AVG_NEX_REWARDS_FRACTION_KEY)
        ) {
            // It isn't very clear whether MVP chance is 10% more additively or multiplicatively. This assumes multiplicatively
            // and the user is instructed to increase the rewardsFraction by 10% if they always MVP, so no additional
            // calculation based on team size etc. is necessary.
            dropChance *= clampContribution(config.avgNexRewardsFraction());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.ZALCANO_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.AVG_ZALCANO_REWARDS_FRACTION_KEY)
        ) {
            dropChance *= clampContribution(config.avgZalcanoRewardsFraction());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.ZALCANO_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.AVG_ZALCANO_POINTS_KEY)
        ) {
            dropChance *= getZalcanoShardContributionBoost(config.avgZalcanoPoints());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.CALLISTO_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.AVG_CALLISTO_REWARDS_FRACTION_KEY)
        ) {
            dropChance *= clampContribution(config.avgCallistoRewardsFraction());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.VENENATIS_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.AVG_VENENATIS_REWARDS_FRACTION_KEY)
        ) {
            dropChance *= clampContribution(config.avgVenenatisRewardsFraction());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.VETION_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.AVG_VETION_REWARDS_FRACTION_KEY)
        ) {
            dropChance *= clampContribution(config.avgVetionRewardsFraction());
        }
        else if (
                rollInfo.getDropSource().equals(LogItemSourceInfo.SCURRIUS_KILLS)
                        && configOptions.contains(CollectionLogLuckConfig.AVG_SCURRIUS_MVP_RATE_KEY)
        ) {
            dropChance *= clampContribution(config.avgScurriusMvpRate());
        }

        return dropChance;
    }

    private double getCoxUniqueChanceFromPoints(int points) {
        // max point cap
        int effectivePoints = Math.min(570_000, points);
        return effectivePoints / 867_600.0;
    }

    private double clampContribution(double fraction) {
        return Math.max(0, Math.min(1, fraction));
    }

    private double getToAUniqueChance(double uniqueChance) {
        // max unique rate.
        return Math.max(0, Math.min(0.55, uniqueChance));
    }

    // Unique chance can be used to estimate pet chance without the user having to plug in both.
    // Fit online using wiki calculator and quadratic fit. Regions < 50 or > 550 invo may be inaccurate.
    // This is also slightly inaccurate if you are getting many more or fewer points than average in a large
    // team raid.
    private double getToAPetChance(double rawUniqueChance) {
        // max unique rate. This equation will be inaccurate by this point, anyway.
        double uniqueChance = Math.max(0, Math.min(0.55, rawUniqueChance));
        double a = 9.266e-02;
        double b = 2.539e-02;
        double c = 1.269e-04;
        double x = uniqueChance;

        return a*x*x + b*x + c;
    }

    // The fraction of Nightmare contribution is used rather than MVP rate since having both options would be a bit
    // overkill, and contribution could vary more or have a higher affect on unique rates than MVP rate. For example,
    // in a mixed group, a player with max gear could do 1.5x the DPS of others in the group, while the MVP rate
    // is only a 5% boost even if they MVP every time.
    // Also, the user is instructed to increase the rewardsFraction by 5% if they always MVP, so it is still possible
    // to correct the calculation in these cases.
    private double getNightmareUniqueShare(double partySize, double rawRewardsFraction) {
        // chance for additional drop in large parties
        double uniqueChance = 1 + Math.max(0, Math.min(75, partySize - 5)) / 100.0;

        double rewardsFraction = Math.max(0, Math.min(1, rawRewardsFraction));

        return uniqueChance * rewardsFraction;
    }

    private double getNightmareJarModifier(double partySize) {
        double clampedPartySize = Math.max(1, Math.min(5, partySize));
        // Just assume average MVP rate - This is not really worth an entire config option to make it slightly more
        // accurate.
        double avgMvpRate = 1.0 / clampedPartySize;

        // If you always MVP, you get the full 5% bonus. Scales linearly.
        return 1 + avgMvpRate * 0.05;
    }

    private double getNightmarePetShare(double partySize) {
        double clampedPartySize = Math.max(1, Math.min(5, partySize));

        return 1.0 / clampedPartySize;
    }

    // We don't actually know the formula, so I'll guess that it's the min drop rate at the min point threshold
    // and max drop rate at the max point threshold
    private double getZalcanoShardContributionBoost(int numPoints) {
        double pointFraction = (numPoints - 150.0) / (1000 - 150);
        double boost = 1 + Math.max(0, Math.min(1, pointFraction));

        return boost;
    }

}
