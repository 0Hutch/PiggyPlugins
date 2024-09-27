package com.ozplugins.AutoTeleTabs;

import lombok.Getter;

@Getter
public enum TeleTab {
    FALADOR("Falador Teleport"),
    VARROCK("Varrock Teleport"),
    ARDOUGNE("Ardougne Teleport"),
    CAMELOT("Camelot Teleport"),
    HOUSE_TELEPORT("Teleport to House"),
    WATCHTOWER("Watchtower Teleport"),
    LUMBRIDGE("Lumbridge Teleport"),
    BONES_TO_BANANAS("Bones to Banana"),
    BONES_TO_PEACHES("Bones  to Peaches");

    private final String widgetStringName;

    TeleTab(String widgetStringName) {
        this.widgetStringName = widgetStringName;
    }
}
