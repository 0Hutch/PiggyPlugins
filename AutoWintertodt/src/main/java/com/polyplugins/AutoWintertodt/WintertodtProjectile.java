package com.polyplugins.AutoWintertodt;

import java.time.Instant;

public class WintertodtProjectile {
    private final int x;
    private final int y;
    private final boolean aoe;
    private final Instant start;

    public Area getDamageArea() {
        if (this.aoe) {
            return new RectangularArea(this.x - 1, this.y - 1, this.x + 1, this.y + 1);
        } else {
            return this.x == 1638 ? new RectangularArea(this.x, this.y - 1, this.x + 3, this.y + 2) : new RectangularArea(this.x - 1, this.y - 1, this.x + 2, this.y + 2);
        }
    }

    public WintertodtProjectile(int x, int y, boolean aoe, Instant start) {
        this.x = x;
        this.y = y;
        this.aoe = aoe;
        this.start = start;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isAoe() {
        return this.aoe;
    }

    public Instant getStart() {
        return this.start;
    }
}
