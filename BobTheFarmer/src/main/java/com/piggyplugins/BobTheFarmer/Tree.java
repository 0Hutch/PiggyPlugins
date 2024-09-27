package com.piggyplugins.BobTheFarmer;

//Types of trees that are supported by the bot
public enum Tree {
    Oak("Oak sapling", "Oak tree", "Tomatoes(5)", 1),
    Willow("Willow sapling", "Willow tree","Apples(5)", 1),
    Maple("Maple sapling", "Maple tree","Oranges(5)", 1),
    Yew("Yew sapling", "Yew tree","Cactus spine", 10),
    Magic("Magic sapling", "Magic tree","Coconut", 25);

    public final String Sapling;
    public final String Tree;
    public final String ProtectionItem;
    public final int AmountOfProtectionItem;
    Tree(String sapling, String tree, String protectionItem, int amountOfProtectionItem)
    {
        this.Sapling = sapling;
        this.Tree = tree;
        this.ProtectionItem = protectionItem;
        this.AmountOfProtectionItem = amountOfProtectionItem;
    }
}
