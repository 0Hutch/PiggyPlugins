package com.piggyplugins.BobTheFarmer;

public enum Allotment {
    NONE("","","",0),
    Snape_Grass("Snape grass seed", "Snape grass", "Jangerberries",10),
    Watermelon("Watermelon seed", "Watermelon", "Nasturtiums", 10),
    ;

    public final String SeedName;
    public final String PlantName;
    public final String ProtectionItem;
    public final int ProtectionItemAmount;
    Allotment(String itemName, String herbName, String protectionItem, int protectionItemAmount)
    {
        this.SeedName = itemName;
        this.PlantName = herbName;
        this.ProtectionItem = protectionItem;
        this.ProtectionItemAmount = protectionItemAmount;
    }
}
