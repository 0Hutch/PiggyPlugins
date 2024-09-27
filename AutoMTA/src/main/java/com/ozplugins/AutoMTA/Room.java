package com.ozplugins.AutoMTA;

import lombok.Getter;

@Getter
public enum Room {
    TELEKINETIC(23673),
    GRAVEYARD(23676),
    ENCHANTING(23674),
    ALCHEMY(23675);

    private final int objectID;

    Room(int objectID) {
        this.objectID = objectID;
    }
}
