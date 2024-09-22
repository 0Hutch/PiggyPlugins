package com.spinplugins.IronBuddy.tasks.Cooking;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import com.spinplugins.IronBuddy.data.Const;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.TileObject;

import java.util.List;
import java.util.Optional;

@Slf4j
public class FindRangeTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public FindRangeTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return !plugin.getClient().getLocalPlayer().getWorldLocation().isInArea(Const.portPhasRangeArea) || TileObjects.search()
                .idInList(List.of(Const.portPhasDoorIDOpen)).first().isEmpty();
    }

    @Override
    public void execute() {
        Optional<TileObject> door = TileObjects.search()
                .idInList(List.of(Const.portPhasDoorIDOpen))
                .nearestToPoint(Const.portPhasRangeDoor);

        if(door.isEmpty()) {
            // go to range
            return;
        }

        Integer playerDistanceToDoor = door.get().getWorldLocation().distanceTo(plugin.getClient().getLocalPlayer().getWorldLocation());

        if(playerDistanceToDoor <= 1) {
            interactObject(door.get(), "Open");
        } else {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(Const.portPhasRangeDoor);
        }
    }
}