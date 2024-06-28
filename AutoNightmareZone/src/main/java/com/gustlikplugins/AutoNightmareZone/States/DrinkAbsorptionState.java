package com.gustlikplugins.AutoNightmareZone.States;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.InventoryInteraction;
import com.gustlikplugins.AutoNightmareZone.AutoNightmareZonePlugin;
import com.gustlikplugins.AutoNightmareZone.State;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;

import java.util.Optional;

public class DrinkAbsorptionState extends State {


    private int refillAbsorptionAt = (int) (Math.random() * (plugin.config.absorptionOffset() - 1)) + 1;
    private int tickDelay = (int) (Math.random() * (plugin.config.tickDelayMax() - plugin.config.tickdelayMin())) + plugin.config.tickdelayMin();

    public DrinkAbsorptionState(AutoNightmareZonePlugin plugin, State prevState) {
        super(plugin, prevState);
    }



    @Override
    public String getName() {
        return "DRINK_ABSORPTION";
    }

    @Override
    public State getState() {
        int absorptionPoints = plugin.client.getVarbitValue(Varbits.NMZ_ABSORPTION);
        if(absorptionPoints < refillAbsorptionAt)
            return this;

        return getState(DamageSelfState.class);
    }

    @Override
    public void onGameTick() {
        tickDelay--;
        if(tickDelay > 1) return;

        int absorptionPoints = plugin.client.getVarbitValue(Varbits.NMZ_ABSORPTION);
        if(absorptionPoints <= 450){
            Optional<Widget> absorptionPot = Inventory.search().matchesWildCardNoCase("Absorption*").first();
            if(absorptionPot.isPresent()){
                absorptionPot.ifPresent(widget -> InventoryInteraction.useItem(widget, "Drink"));
                refillAbsorptionAt += 50;
            }else{
                refillAbsorptionAt = -1; //ran out of absorptions
            }
        }else{
            refillAbsorptionAt = (int) (Math.random() * (plugin.config.absorptionOffset() - 1)) + 1;
            tickDelay = (int) (Math.random() * (plugin.config.tickDelayMax() - plugin.config.tickdelayMin())) + plugin.config.tickdelayMin();
        }
    }

    public void resetAbsorption(){
        refillAbsorptionAt = (int) (Math.random() * (plugin.config.absorptionOffset() - 1)) + 1;
    }
}
