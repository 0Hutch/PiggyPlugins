package com.spinplugins.IronBuddy.tasks;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.PacketUtils.WidgetInfoExtended;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import java.util.HashSet;
import java.util.Optional;

@Slf4j
public class MakeLarderTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public MakeLarderTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    public Optional<Widget> getFurnitureCreationWidget() {
        int creationMenuTextId = 30015489;
        log.info("Searching for creation menu");
        return Widgets.search().withId(creationMenuTextId).first();
    }

    @Override
    public boolean validate() {
        int planksNeededPerBuild = 8;
        return getFurnitureCreationWidget().isPresent()
                && InventoryUtil.getItemAmount(ItemID.OAK_PLANK) >= planksNeededPerBuild;
    }
    @Override
    public void execute() {
        plugin.imitateKeyPress('2');
        plugin.timeout = 4;
    }
}