package com.gustlikplugins.AutoNightmareZone.States;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.InventoryInteraction;
import com.gustlikplugins.AutoNightmareZone.AutoNightmareZonePlugin;
import com.gustlikplugins.AutoNightmareZone.NightmareZoneStrategy;
import com.gustlikplugins.AutoNightmareZone.State;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class DrinkOverloadState extends State {

    static final Duration OVERLOAD_DURATION = Duration.ofMinutes(5);
    private Instant lastOverload = Instant.now().minus(Duration.ofMinutes(10));

    private int tickDelay = (int) (Math.random() * (plugin.config.tickDelayMax() - plugin.config.tickdelayMin())) + plugin.config.tickdelayMin();

    public DrinkOverloadState(AutoNightmareZonePlugin plugin, State prevState) {
        super(plugin, prevState);
    }


    @Override
    public String getName() {
        return "DRINK_ABSORPTION";
    }

    @Override
    public State getState() {
        //How tf do I get health??
        if (Instant.now().isAfter(lastOverload.plus(OVERLOAD_DURATION)) && plugin.client.getBoostedSkillLevel(Skill.HITPOINTS) > 50) {
            Optional<Widget> overloadPot = Inventory.search().matchesWildCardNoCase("Overload*").first();
            if (overloadPot.isPresent())
                return this;
        }

        //Wait for the overload to stop damaging us
        if(!Instant.now().isAfter(lastOverload.plus(Duration.ofSeconds(8)))) //Increased overload timeout
            return getState(IdleState.class);

        if(plugin.config.strategy() == NightmareZoneStrategy.ABSORPTION)
            return getState(DrinkAbsorptionState.class);

        return getState(PrayerState.class);
    }

    @Override
    public void onGameTick() {
        tickDelay--;
        if(tickDelay > 1) return;
        tickDelay = (int) (Math.random() * (plugin.config.tickDelayMax() - plugin.config.tickdelayMin())) + plugin.config.tickdelayMin();
        Optional<Widget> overloadPot = Inventory.search().matchesWildCardNoCase("Overload*").first();
        overloadPot.ifPresent(widget -> InventoryInteraction.useItem(widget, "Drink"));
        lastOverload = Instant.now();
    }

    public void resetOverloadTimer(){
        lastOverload = Instant.now().minus(Duration.ofMinutes(10));
    }
}
