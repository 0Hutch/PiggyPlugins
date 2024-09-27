package com.ozplugins.AutoMiner.Constants;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum AmethystMineSpot {
    AMETHYST_1(new WorldPoint(3007, 9728, 0)),
    AMETHYST_2(new WorldPoint(3003, 9719, 0)),
    AMETHYST_3(new WorldPoint(3002, 9711, 0)),
    AMETHYST_4(new WorldPoint(3012, 9711, 0));

    private final WorldPoint minePoint;

    AmethystMineSpot(WorldPoint minePoint) {
        this.minePoint = minePoint;
    }
}