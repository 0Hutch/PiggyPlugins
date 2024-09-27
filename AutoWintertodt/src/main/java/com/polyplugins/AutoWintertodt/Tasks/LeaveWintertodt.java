package com.polyplugins.AutoWintertodt.Tasks;

import com.polyplugins.AutoWintertodt.AutoWintertodtPlugin;
import com.polyplugins.AutoWintertodt.Data.Data;
import com.polyplugins.AutoWintertodt.Model.Task;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import java.util.Optional;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.WidgetInfo;

public class LeaveWintertodt implements Task {
    public LeaveWintertodt() {
    }

    public boolean activateTask() {
        if (!AutoWintertodtPlugin.isInWintertodtRegion()) {
            return false;
        } else if (Inventory.search().withId(20703).first().isPresent()) {
            return true;
        } else {
            return Inventory.getItemAmount(AutoWintertodtPlugin.wintertodtConfig.foodsToEat().getFoodID()) < AutoWintertodtPlugin.getWintertodtConfig().minFood();
        }
    }

    public void runTask() {
        if (Widgets.search().withId(WidgetInfo.DIALOG_OPTION_OPTIONS.getId()).first().isPresent()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(WidgetInfo.DIALOG_OPTION_OPTIONS.getId(), 1);
        } else {
            Optional<TileObject> bigDoor = TileObjects.search().withAction("Enter").first();
            if (!bigDoor.isEmpty()) {
                if (EthanApiPlugin.playerPosition().distanceTo(((TileObject)bigDoor.get()).getWorldLocation()) > 5) {
                    MousePackets.queueClickPacket();
                    MovementPackets.queueMovement(Data.DOOR_TILE_INSIDE);
                } else {
                    bigDoor.ifPresent((x) -> {
                        TileObjectInteraction.interact(x, new String[]{"Enter"});
                    });
                }
            }
        }
    }
}
