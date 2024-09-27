package com.piggyplugins.BobTheFarmer;

//Types of herbs that are supported by the bot
public enum Herb {
    Guam_leaf("Guam seed", "Guam leaf"),
    Marrentill("Marrentill seed", "Marrentill"),
    Tarromin("Tarromin seed", "Tarromin"),
    Harralander("Harralander seed", "Harralander"),
    Ranarr_weed("Ranarr seed", "Ranarr weed"),
    Toadflax("Toadflax seed", "Toadflax"),
    Irit_leaf("Irit seed", "Irit leaf"),
    Avantoe("Avantoe seed", "Avantoe"),
    Kwuarm("Kwuarm seed", "Kwuarm"),
    Snapdragon("Snapdragon seed", "Snapdragon"),
    Cadantine("Cadantine seed", "Cadantine"),
    Lantadyme("Lantadyme seed", "Lantadyme"),
    Dwarf_weed("Dwarf weed seed", "Dwarf weed"),
    Torstol("Torstol seed", "Torstol")
    ;

    public final String SeedName;
    public final String HerbName;
    Herb(String itemName, String herbName)
    {
        this.SeedName = itemName;
        this.HerbName = herbName;
    }
}
