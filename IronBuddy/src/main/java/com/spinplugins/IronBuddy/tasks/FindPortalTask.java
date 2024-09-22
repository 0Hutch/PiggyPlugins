package com.spinplugins.IronBuddy.tasks;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import com.spinplugins.IronBuddy.data.Const;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;

import java.util.Optional;

@Slf4j
public class FindPortalTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public FindPortalTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    private boolean hasPlanks() {
        return Inventory.full() &&
                Inventory.search().withId(ItemID.OAK_PLANK).onlyUnnoted().first().isPresent();
    }

    private Optional<TileObject> findHousePortal() {
        return TileObjects.search().withId(Const.rimmingtonPortalID).first();
    }

    private Optional<TileObject> findExitPortal() {
        return TileObjects.search().withId(Const.POHPortalID).first();
    }

    private void enterPortal() {
        if(findHousePortal().isPresent()) {
            log.info("Entering house by portal...");
            interactObject(findHousePortal().get(), "Build mode");
        }

        if(findExitPortal().isPresent()) {
            log.info("Leaving house by portal...");
            interactObject(findExitPortal().get(), "Enter");
        }
    }

    @Override
    public boolean validate() {
        return (hasPlanks() && findHousePortal().isPresent()) ||
                (findExitPortal().isPresent() && !hasPlanks());
    }

    @Override
    public void execute() {
        enterPortal();
    }
}