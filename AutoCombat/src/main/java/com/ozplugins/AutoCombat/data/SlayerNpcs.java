package com.ozplugins.AutoCombat.data;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;

import java.util.List;

public enum SlayerNpcs {
    ROCK_SLUG(List.of(NpcID.ROCKSLUG, NpcID.ROCKSLUG_422, NpcID.GIANT_ROCKSLUG),
            ItemID.BAG_OF_SALT),
    LIZARDS(List.of(NpcID.LIZARD, NpcID.SMALL_LIZARD, NpcID.SMALL_LIZARD_463, NpcID.DESERT_LIZARD, NpcID.DESERT_LIZARD_460, NpcID.DESERT_LIZARD_461),
            ItemID.ICE_COOLER),
    GARGOYLE(List.of(NpcID.GARGOYLE, NpcID.GARGOYLE_1543, NpcID.GARGOYLE_413, NpcID.MARBLE_GARGOYLE, NpcID.MARBLE_GARGOYLE_7408),
            ItemID.ROCK_HAMMER);

    @Getter
    private final List<Integer> NpcIDs;
    @Getter
    private final int slayerItemID;

    SlayerNpcs(List<Integer> NpcIDs, int slayerItemID) {
        this.NpcIDs = NpcIDs;
        this.slayerItemID = slayerItemID;
    }
}
