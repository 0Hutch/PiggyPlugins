package com.masterkenth.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NpcItem {
    @SerializedName("id")
    @Expose
    private Integer ItemID;

    @SerializedName("q")
    @Expose
    private String Quantity;

    @SerializedName("r")
    @Expose
    private Float Rarity;

    public Integer getItemID() {
        return ItemID;
    }

    public void setItemID(Integer itemID) {
        this.ItemID = itemID;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        this.Quantity = quantity;
    }

    public Float getRarity() {
        return Rarity;
    }

    public void setRarity(Float rarity) {
        this.Rarity = rarity;
    }
}
