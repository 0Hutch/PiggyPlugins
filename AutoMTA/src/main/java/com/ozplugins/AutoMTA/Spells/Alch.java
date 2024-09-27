package com.ozplugins.AutoMTA.Spells;

import lombok.Getter;

@Getter
public enum Alch {
    LOW_ALCH(14286869),
    HIGH_ALCH(14286892);

    private final int widgetID;

    Alch(int widgetID) {
        this.widgetID = widgetID;
    }
}
