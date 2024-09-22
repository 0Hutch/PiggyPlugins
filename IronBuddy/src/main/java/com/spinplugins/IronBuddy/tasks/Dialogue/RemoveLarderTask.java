package com.spinplugins.IronBuddy.tasks.Dialogue;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

import java.util.Optional;

@Slf4j
public class RemoveLarderTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public RemoveLarderTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    public Optional<TileObject> getLarder() {
        log.info("Searching for larder");
        return TileObjects.search().withId(ObjectID.LARDER_13566).first();
    }

    private void rightClickRemove() {
        getLarder().ifPresent(tileObject -> interactObject(tileObject, "Remove"));
    }

    @Override
    public boolean validate() {
        return getLarder().isPresent()
                && getLarder().get().getWorldLocation().distanceTo(EthanApiPlugin.playerPosition()) <= 2;
    }

    @Override
    public void execute() {
        Optional<Widget> removeWidget = Widgets.search().withTextContains("Really remove it?").first();
        removeWidget.ifPresentOrElse(
            widget -> {
                log.info("Removing larder");
                plugin.imitateKeyPress('1');
            },
            () -> {
                log.info("Right clicking larder");
                rightClickRemove();
            }
        );
    }
}