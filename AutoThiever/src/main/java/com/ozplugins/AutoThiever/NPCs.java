package com.ozplugins.AutoThiever;

import lombok.Getter;
@Getter
public enum NPCs {
        MASTER_FARMER( "Master Farmer"),
        ARDOUGNE_KNIGHT("Knight of Ardougne"),
        PRIF_LINDIR("Lindir"),
        CUSTOM("Custom");

    private final String npcName;

    NPCs(String npcName) {
        this.npcName = npcName;
        }
}
