package com.polyplugins.AutoCrafting.config_types;

public enum LEATHER_TYPE {
    LEATHER(1741),
    GREEN_DRAGON_LEATHER(1745),
    BLUE_DRAGON_LEATHER(2505),
    RED_DRAGON_LEATHER(2507),
    BLACK_DRAGON_LEATHER(2509);

    private final int leatherType;

    public int getLeatherType() {
        return this.leatherType;
    }

    private LEATHER_TYPE(int leatherType) {
        this.leatherType = leatherType;
    }
}
