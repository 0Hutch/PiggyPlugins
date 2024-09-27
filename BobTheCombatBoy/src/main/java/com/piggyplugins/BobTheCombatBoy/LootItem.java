package com.piggyplugins.BobTheCombatBoy;

public class LootItem {

    public int Value;

    private int minValue;
    private int minStackValue;
    private boolean Stackable;

    public LootItem(int value, int minVal, int minStackVal, boolean stackable) {
        Value = value;
        minValue = minVal;
        minStackValue = minStackVal;
        Stackable = stackable;
    }

    public boolean Loot() {
        return Stackable ? Value > minStackValue : Value > minValue;
    }
}
