package com.piggyplugins.BobTheFarmer;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.Dictionary;
import java.util.Hashtable;

public class AllotmentPatch {
    public String Name = "";
    public String[] Tools = {};
    public AllotmentPatchState State;
    public Dictionary<String, WorldPoint[]> Paths = new Hashtable<>();
    public WorldArea AllotmentPatchArea = null;
    public int PathIndex = 0;
    public String NPCInteractionWord;

    public AllotmentPatch(String name, WorldArea worldArea, String npcInteractionWord, String[] tools)
    {
        this.Name = name;
        this.Tools = tools;
        this.State = AllotmentPatchState.NOT_STARTED;
        this.AllotmentPatchArea = worldArea;
        this.NPCInteractionWord = npcInteractionWord;
        this.PathIndex = 0;
    }
    public void SetPath(WorldPoint[] path, String key)
    {
        Paths.put(key, path);
    }
}
