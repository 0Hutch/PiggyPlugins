package com.polyplugins.AutoWintertodt;

public enum Foods {
    SALMON(329),
    LOBSTER(379),
    BASS(365),
    SWORDFISH(373),
    MONK_FISH(7946),
    SHARK(385),
    MANTA_RAY(391),
    Cake(1891),
    ANGLER_FISH(13441);

    private final int foodID;

    public int getFoodID() {
        return this.foodID;
    }

    private Foods(int foodID) {
        this.foodID = foodID;
    }
}
