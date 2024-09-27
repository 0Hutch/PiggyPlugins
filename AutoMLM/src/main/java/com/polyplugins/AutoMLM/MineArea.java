package com.polyplugins.AutoMLM;

import net.runelite.api.coords.WorldPoint;

public enum MineArea {
    ARRIBA_1(new WorldPoint(3760, 5671, 0)),
    ARRIBA_2(new WorldPoint(3752, 5679, 0)),
    ABAJO_1(new WorldPoint(3746, 5652, 0)),
    ABAJO_2(new WorldPoint(3737, 5665, 0));

    private final WorldPoint minePoint;

    private MineArea(WorldPoint minePoint) {
        this.minePoint = minePoint;
    }

    public WorldPoint getMinePoint() {
        return this.minePoint;
    }
}
