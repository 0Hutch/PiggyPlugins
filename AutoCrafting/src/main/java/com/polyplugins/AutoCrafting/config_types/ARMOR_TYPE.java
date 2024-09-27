package com.polyplugins.AutoCrafting.config_types;

public enum ARMOR_TYPE {
    VAMBRACES("vambraces", 1),
    CHAPS("chaps", 2),
    BODY("body", 3),
    CUALQUIER_LEATHER("Cualquier Leather", 1);

    private final String armorType;
    private final int leatherNeeded;

    public String getArmorType() {
        return this.armorType;
    }

    public int getLeatherNeeded() {
        return this.leatherNeeded;
    }

    private ARMOR_TYPE(String armorType, int leatherNeeded) {
        this.armorType = armorType;
        this.leatherNeeded = leatherNeeded;
    }
}

