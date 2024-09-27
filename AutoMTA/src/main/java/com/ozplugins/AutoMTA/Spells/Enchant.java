package com.ozplugins.AutoMTA.Spells;

import lombok.Getter;

@Getter
public enum Enchant {
    LVL_1(14286861),
    LVL_2(14286872),
    LVL_3(14286885),
    LVL_4(14286894),
    LVL_5(14286909),
    LVL_6(14286922),
    LVL_7(14286922);

    private final int widgetID;

    Enchant(int widgetID) {
        this.widgetID = widgetID;
    }
}
