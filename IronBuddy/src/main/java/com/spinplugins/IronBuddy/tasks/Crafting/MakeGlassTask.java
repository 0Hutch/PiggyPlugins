package com.spinplugins.IronBuddy.tasks.Crafting;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
public class MakeGlassTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public MakeGlassTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return Inventory.getItemAmount(ItemID.SODA_ASH) ==
                Inventory.getItemAmount(ItemID.BUCKET_OF_SAND) &&
                Inventory.getItemAmount(ItemID.SODA_ASH) > 1;
    }

    @Override
    public void execute() {
        if(plugin.isSmelting) {
            return;
        }

        if (EthanApiPlugin.getClient().getWidget(17694734) != null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(17694734, Inventory.getItemAmount(ItemID.SODA_ASH));

            // smelting has started, don't try to smelt again until the next inventory
            plugin.isSmelting = true;
            log.info("Smelting");
        } else {
            TileObjects.search().withAction("Smelt").first().ifPresent(
                    tileObject -> {
                        TileObjectInteraction.interact(tileObject, "Smelt");
                    }
            );
        }
    }
}