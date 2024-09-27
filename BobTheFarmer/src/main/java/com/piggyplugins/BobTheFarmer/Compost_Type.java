package com.piggyplugins.BobTheFarmer;

//Types of compost that are supported by the bot
public enum Compost_Type {
    Compost("Compost"),
    Supercompost("Supercompost"),
    Ultracompost("Ultracompost");

    public final String Name;
    Compost_Type(String name)
    {
        this.Name = name;
    }

}
