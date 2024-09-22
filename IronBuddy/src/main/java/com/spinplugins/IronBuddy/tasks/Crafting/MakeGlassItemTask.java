package com.spinplugins.IronBuddy.tasks.Crafting;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;

import java.util.HashSet;
import java.util.Optional;

@Slf4j
public class MakeGlassItemTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public MakeGlassItemTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return Inventory.full() && Inventory.getItemAmount(ItemID.MOLTEN_GLASS) > 1 && !plugin.isCrafting;
    }

    @Override
    public void execute() {
        if (Widgets.search().withTextContains("How many do you wish to make?").first().isEmpty()) {
            Optional<Widget> glassPipe = Inventory.search().withId(ItemID.GLASSBLOWING_PIPE).first();
            Optional<Widget> moltenGlass = Inventory.search().withId(ItemID.MOLTEN_GLASS).first();

            if (glassPipe.isPresent() && moltenGlass.isPresent()) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(glassPipe.get(), moltenGlass.get());
            }
        } else {
            plugin.isCrafting = true;
            WidgetPackets.queueResumePause(17694739, Inventory.getItemAmount(ItemID.MOLTEN_GLASS));
        }
    }
}