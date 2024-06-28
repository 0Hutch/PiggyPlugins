package com.gustlikplugins.AutoNightmareZone.States;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.InventoryInteraction;
import com.gustlikplugins.AutoNightmareZone.AutoNightmareZonePlugin;
import com.gustlikplugins.AutoNightmareZone.State;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;

import java.time.Duration;
import java.util.Optional;

public class DrinkPrayerState extends State {

    public DrinkPrayerState(AutoNightmareZonePlugin plugin, State initialState) {
        super(plugin, initialState);
    }

    @Override
    public String getName() {
        return "DRINK_PRAYER";
    }


    @Override
    public State getState() {
        int currentPrayer = plugin.client.getBoostedSkillLevel(Skill.PRAYER);
        int prayerLevel = plugin.client.getRealSkillLevel(Skill.PRAYER);
        int prayerBoost = (prayerLevel / 4) + 7;
        if(currentPrayer < prayerLevel - prayerBoost)
            return this;

        return getState(IdleState.class);
    }

    @Override
    public void onGameTick() {
        Optional<Widget> prayerPot = Inventory.search().matchesWildCardNoCase("Prayer potion*").first();
        prayerPot.ifPresent(widget -> InventoryInteraction.useItem(widget, "Drink"));
    }
}
