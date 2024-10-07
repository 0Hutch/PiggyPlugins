package com.piggyplugins.collectionlogluck.luck;


// A wrapper for info relating to the drop of a specific item from a specific drop source
public class RollInfo {

    private final LogItemSourceInfo dropSource;
    private final double dropChancePerRoll;
    private final int rollsPerKc;

    public RollInfo(LogItemSourceInfo dropSource, double dropChancePerRoll, int rollsPerKc) {
        this.dropSource = dropSource;
        this.dropChancePerRoll = dropChancePerRoll;
        this.rollsPerKc = rollsPerKc;
    }

    public RollInfo(LogItemSourceInfo dropSource, double dropChancePerRoll) {
        this.dropSource = dropSource;
        this.dropChancePerRoll = dropChancePerRoll;
        this.rollsPerKc = 1;
    }

    public LogItemSourceInfo getDropSource() {
        return dropSource;
    }

    public double getDropChancePerRoll() {
        return dropChancePerRoll;
    }

    public int getRollsPerKc() {
        return rollsPerKc;
    }
}
