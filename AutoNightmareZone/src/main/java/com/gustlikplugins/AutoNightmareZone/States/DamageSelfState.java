package com.gustlikplugins.AutoNightmareZone.States;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.InventoryInteraction;
import com.gustlikplugins.AutoNightmareZone.AutoNightmareZonePlugin;
import com.gustlikplugins.AutoNightmareZone.PrayerFlickMode;
import com.gustlikplugins.AutoNightmareZone.State;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;

import java.util.Optional;

public class DamageSelfState extends State {

    public DamageSelfState(AutoNightmareZonePlugin plugin, State prevState) {
        super(plugin, prevState);
    }


    @Override
    public String getName() {
        return "DAMAGE_SELF";
    }

    private int tickDelay = (int) (Math.random() * (plugin.config.tickDelayMax() - plugin.config.tickdelayMin())) + plugin.config.tickdelayMin();

    @Override
    public State getState() {
        if(plugin.client.getBoostedSkillLevel(Skill.HITPOINTS) > 1)
            return this;

        if(plugin.config.prayerFlickMode() != PrayerFlickMode.OFF)
            return getState(PrayerState.class);

        return getState(IdleState.class);
    }

    @Override
    public void onGameTick() {
        tickDelay--;
        if(tickDelay > 1 && plugin.client.getBoostedSkillLevel(Skill.HITPOINTS) == 2) return;
        tickDelay = (int) (Math.random() * (plugin.config.tickDelayMax() - plugin.config.tickdelayMin())) + plugin.config.tickdelayMin();

        switch(plugin.config.healthReductionitem()){
            case LOCATOR_ORB: {
                Optional<Widget> locatorOrb = Inventory.search().matchesWildCardNoCase("Locator orb").first();
                locatorOrb.ifPresent(widget -> InventoryInteraction.useItem(widget, "Feel"));
            }break;
            case DWARWEN_ROCK_CAKE: {

                Optional<Widget> dwarvenRockCake = Inventory.search().matchesWildCardNoCase("Dwarven rock cake").first();
                dwarvenRockCake.ifPresent(widget -> InventoryInteraction.useItem(widget, "Guzzle"));
            }break;
        }


    }
}
