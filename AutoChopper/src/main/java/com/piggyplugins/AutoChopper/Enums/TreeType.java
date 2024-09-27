package com.piggyplugins.AutoChopper.Enums;

import net.runelite.api.Item;
import net.runelite.api.ItemID;

public enum TreeType {
    NORMAL("Tree", ItemID.LOGS),
    OAK("Oak tree", ItemID.OAK_LOGS),
    WILLOW("Willow tree", ItemID.WILLOW_LOGS),
    MAPLE("Maple tree", ItemID.MAPLE_LOGS),
    YEW("Yew tree", ItemID.YEW_LOGS),
    MAGIC("Magic tree", ItemID.MAGIC_LOGS);

    private final String treeName;
    private final int logItemId;

    private TreeType(String treeName, int logItemId) {
        this.treeName = treeName;
        this.logItemId = logItemId;
    }

    public String getTreeName() {
        return this.treeName;
    }

    public int getLogItemId() {
        return this.logItemId;
    }
}
