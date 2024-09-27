package com.polyplugins.AutoWintertodt;

import java.util.Arrays;
import net.runelite.api.coords.WorldPoint;

public interface Area {
    boolean contains(WorldPoint var1);

    static Area union(Area... areas) {
        return (point) -> {
            return Arrays.stream(areas).anyMatch((a) -> {
                return a.contains(point);
            });
        };
    }

    static Area intersection(Area... areas) {
        return (point) -> {
            return Arrays.stream(areas).allMatch((a) -> {
                return a.contains(point);
            });
        };
    }

    default Area minus(Area other) {
        return (point) -> {
            return this.contains(point) && !other.contains(point);
        };
    }

    default WorldPoint getRandomTile() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
