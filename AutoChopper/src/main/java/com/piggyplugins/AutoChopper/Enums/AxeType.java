package com.piggyplugins.AutoChopper.Enums;

import net.runelite.api.ItemID;

public enum AxeType {
    BRONZE(ItemID.BRONZE_AXE),
    IRON(ItemID.IRON_AXE),
    STEEL(ItemID.STEEL_AXE),
    BLACK(ItemID.BLACK_AXE),
    MITHRIL(ItemID.MITHRIL_AXE),
    ADAMANT(ItemID.ADAMANT_AXE),
    RUNE(ItemID.RUNE_AXE),
    DRAGON(ItemID.DRAGON_AXE);

    private final int itemId;

    AxeType(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }
}
