package com.gustlikplugins.AutoNightmareZone.States;

import com.gustlikplugins.AutoNightmareZone.AutoNightmareZonePlugin;
import com.gustlikplugins.AutoNightmareZone.NightmareZoneStrategy;
import com.gustlikplugins.AutoNightmareZone.State;

import java.util.Arrays;

public class NotInNmzState extends State {

    public NotInNmzState(AutoNightmareZonePlugin plugin) { //One argument constructor is only required in the first State
        super(plugin);
    }

    public NotInNmzState(AutoNightmareZonePlugin plugin, State prevState) {
        super(plugin, prevState);
    }

    @Override
    public String getName() {
        return "NOT_IN_NIGHTMAREZONE";
    }

    static final int[] NMZ_MAP_REGION = {9033};

    @Override
    public State getState() {

        if(!isInNightmareZone())
            return this;

        if(plugin.config.useOverloads())
            return getState(DrinkOverloadState.class);

        if(plugin.config.strategy() == NightmareZoneStrategy.ABSORPTION)
            return getState(DrinkAbsorptionState.class);


        return getState(PrayerState.class);

    }

    @Override
    public void onGameTick() {
        ((DrinkOverloadState) getStateNonrecursive(DrinkOverloadState.class)).resetOverloadTimer();
        return;
    }

    public boolean isInNightmareZone()
    {
        if (plugin.client.getLocalPlayer() == null)
        {
            return false;
        }

        // NMZ and the KBD lair uses the same region ID but NMZ uses planes 1-3 and KBD uses plane 0
        return plugin.client.getLocalPlayer().getWorldLocation().getPlane() > 0 && Arrays.equals(plugin.client.getMapRegions(), NMZ_MAP_REGION);
    }
}
