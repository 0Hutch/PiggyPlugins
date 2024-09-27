package com.piggyplugins.BobTheFarmer;

import net.runelite.api.coords.WorldPoint;

import java.util.Dictionary;
import java.util.Hashtable;

//Herb patch data structure
//This is to allow the bot the know where it is by knowing where it isn't
public class HerbPatch
{
    public String Name = "";
    public String[] Tools = {};
    public HerbPatchState State;
    public Dictionary<String, WorldPoint[]> Paths = new Hashtable<>();
    public int PathIndex = 0;

    public HerbPatch(String name, String[] tools)
    {
        this.Name = name;
        this.Tools = tools;
        this.State = HerbPatchState.NOT_STARTED;
        this.PathIndex = 0;
    }
    public void SetPath(WorldPoint[] path, String key)
    {
        Paths.put(key, path);
    }
}
