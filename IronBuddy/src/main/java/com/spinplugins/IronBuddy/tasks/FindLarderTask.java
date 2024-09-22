package com.spinplugins.IronBuddy.tasks;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;

import java.util.Optional;

@Slf4j
public class FindLarderTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public FindLarderTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    public Optional<TileObject> getUnbuiltLarder() {
        log.info("Searching for unbuilt larder");
        return TileObjects.search().withId(ObjectID.LARDER_SPACE).first();
    }

    private void openCreationMenu() {
        getUnbuiltLarder().ifPresent(tileObject -> interactObject(tileObject, "Build"));
    }

    @Override
    public boolean validate() {
        int planksNeededPerBuild = 8;
        return getUnbuiltLarder().isPresent() && InventoryUtil.getItemAmount(ItemID.OAK_PLANK) >= planksNeededPerBuild;
    }
    @Override
    public void execute() {
        openCreationMenu();
    }
}