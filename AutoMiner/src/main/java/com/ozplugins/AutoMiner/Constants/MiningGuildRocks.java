package com.ozplugins.AutoMiner.Constants;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum MiningGuildRocks {
    IRON_1("Iron", new WorldPoint(3021, 9721, 0)),
    IRON_2("Iron", new WorldPoint(3029, 9720, 0)),
    COAL("Coal", new WorldPoint(3019, 9716, 0)),
    MITHRIL("Mithril", new WorldPoint(3038, 9718, 0)), //TODO
    ADAMANT("Adamantite", new WorldPoint(3035, 9723, 0));

    private final WorldPoint minePoint;
    private final String rockName;

    MiningGuildRocks(String rockName, WorldPoint minePoint) {
        this.rockName = rockName;
        this.minePoint = minePoint;
    }
}