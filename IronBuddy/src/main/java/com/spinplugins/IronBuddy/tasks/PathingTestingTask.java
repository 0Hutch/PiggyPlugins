package com.spinplugins.IronBuddy.tasks;

import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

@Slf4j
public class PathingTestingTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    private boolean onTheWay = false;

    public PathingTestingTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return plugin.getPathFinder().goal != null || plugin.getDestination() != null;
    }

    @Override
    public void execute() {
        if(plugin.getPathFinder().hasFinishedPathing()) {
            log.info("Pathing has finished");
            onTheWay = false;
            return;
        }

        if(!onTheWay) {
            log.info("Path is null or empty, starting pathing to destination");

            onTheWay = true;
            return;
        }

        if(plugin.getPathFinder().path != null) {
            log.info("Path is not null or empty, walking to next tile");
            plugin.getPathFinder().path();
        }
    }
}