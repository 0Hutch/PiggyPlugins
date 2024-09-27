package com.ozplugins.AutoMTA.Spells;

import lombok.Getter;

@Getter
public enum BonesTo {
    BANANAS(14286865),
    PEACHES(14286898);

    private final int widgetID;

    BonesTo(int widgetID) {
        this.widgetID = widgetID;
    }
}
