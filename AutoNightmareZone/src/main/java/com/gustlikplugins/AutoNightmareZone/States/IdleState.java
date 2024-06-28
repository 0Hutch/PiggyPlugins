package com.gustlikplugins.AutoNightmareZone.States;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.gustlikplugins.AutoNightmareZone.AutoNightmareZonePlugin;
import com.gustlikplugins.AutoNightmareZone.NightmareZoneStrategy;
import com.gustlikplugins.AutoNightmareZone.State;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class IdleState extends State {

    public IdleState(AutoNightmareZonePlugin plugin, State initialState) {
        super(plugin, initialState);
    }

    @Override
    public String getName() {
        return "IDLE_STATE";
    }


    @Override
    public State getState() {
        return this;
    }

    @Override
    public void onGameTick() {

    }
}
