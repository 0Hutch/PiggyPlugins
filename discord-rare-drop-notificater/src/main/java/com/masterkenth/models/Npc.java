package com.masterkenth.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Npc {
    @SerializedName("n")
    @Expose
    private String npcName;

    @SerializedName("i")
    @Expose
    private List<NpcItem> npcItems = null;

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String name) {
        this.npcName = name;
    }

    public List<NpcItem> getItems() {
        return npcItems;
    }

    public void setItems(List<NpcItem> items) {
        this.npcItems = items;
    }
}