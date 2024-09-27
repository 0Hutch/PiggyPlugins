package com.polyplugins.AutoWintertodt;

import net.runelite.api.coords.WorldPoint;

public class RectangularArea implements Area {
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    private final int plane;

    public RectangularArea(int x1, int y1, int x2, int y2, int plane) {
        this.plane = plane;
        if (x1 <= x2) {
            this.minX = x1;
            this.maxX = x2;
        } else {
            this.minX = x2;
            this.maxX = x1;
        }

        if (y1 <= y2) {
            this.minY = y1;
            this.maxY = y2;
        } else {
            this.minY = y2;
            this.maxY = y1;
        }

    }

    public RectangularArea(int x1, int y1, int x2, int y2) {
        this(x1, y1, x2, y2, 0);
    }

    public RectangularArea(WorldPoint sw, int width, int height) {
        this(sw.getX(), sw.getY(), sw.getX() + width, sw.getY() + height, sw.getPlane());
    }

    public RectangularArea(WorldPoint sw, WorldPoint ne) {
        this(sw.getX(), sw.getY(), ne.getX(), ne.getY(), sw.getPlane());
    }

    public WorldPoint getCenter() {
        return new WorldPoint((this.minX + this.maxX) / 2, (this.minY + this.maxY) / 2, this.plane);
    }

    public boolean contains(WorldPoint worldPoint) {
        if (worldPoint.getPlane() != -1 && worldPoint.getPlane() == this.plane) {
            int x = worldPoint.getX();
            int y = worldPoint.getY();
            return x >= this.minX && y >= this.minY && x <= this.maxX && y <= this.maxY;
        } else {
            return false;
        }
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMinY() {
        return this.minY;
    }

    public int getMaxY() {
        return this.maxY;
    }

    public int getPlane() {
        return this.plane;
    }
}

