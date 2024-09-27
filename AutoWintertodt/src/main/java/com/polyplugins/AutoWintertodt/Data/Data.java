package com.polyplugins.AutoWintertodt.Data;
import net.runelite.api.coords.WorldPoint;

public class Data {
    public static final int WINTERTODT_REGION_ID = 6462;
    public static final int SPEC_ORB_ID = 10485795;
    public static final WorldPoint CHOP_TILE = new WorldPoint(1622, 3988, 0);
    public static final WorldPoint BRAZIER_TILE = new WorldPoint(1622, 3996, 0);
    public static final WorldPoint DOOR_TILE_INSIDE = new WorldPoint(1631, 3969, 0);
    public static final WorldPoint DOOR_TILE_OUTSIDE = new WorldPoint(1631, 3962, 0);
    public static final WorldPoint BANK_TILE = new WorldPoint(1633, 3946, 0);
    public static boolean hasResources;
    public static long lastAnimationMs;

    public Data() {
    }

    public static void setHasResources(boolean hasResources) {
        Data.hasResources = hasResources;
    }

    public static void setLastAnimationMs(long lastAnimationMs) {
        Data.lastAnimationMs = lastAnimationMs;
    }
}
