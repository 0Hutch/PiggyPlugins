package com.spinplugins.IronBuddy.tasks.Cooking;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import com.spinplugins.IronBuddy.data.Const;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

import java.util.Optional;

@Slf4j
public class BurnSeaweedTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public BurnSeaweedTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return (plugin.getClient().getLocalPlayer().getWorldLocation().isInArea(Const.portPhasRangeArea) ||
                TileObjects.search().withId(Const.portPhasDoorIDClose).first().isPresent())
                && Inventory.getEmptySlots() >= 6
                && Inventory.getItemAmount(config.seaweedType().getId()) >= 1;
    }

    @Override
    public void execute() {
        if (EthanApiPlugin.getClient().getWidget(17694735) != null) {
            int seaweedAmount = Inventory.getItemAmount(config.seaweedType().getId());
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(17694734, seaweedAmount);
        } else {
            Optional<Widget> seaweed = Inventory.search().withId(config.seaweedType().getId()).first();
            Optional<TileObject> cookingRange = TileObjects.search().withId(16641).first();

            MousePackets.queueClickPacket();
            ObjectPackets.queueWidgetOnTileObject(seaweed.get(), cookingRange.get());
        }
    }
}