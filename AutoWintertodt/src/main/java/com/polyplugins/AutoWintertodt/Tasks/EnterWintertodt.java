package com.polyplugins.AutoWintertodt.Tasks;

import com.polyplugins.AutoWintertodt.AutoWintertodtPlugin;
import com.polyplugins.AutoWintertodt.Data.Data;
import com.polyplugins.AutoWintertodt.Model.Task;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import java.util.Optional;
import net.runelite.api.TileObject;

public class EnterWintertodt implements Task {
    public EnterWintertodt() {
    }

    public boolean activateTask() {
        if (Inventory.getItemAmount(AutoWintertodtPlugin.wintertodtConfig.foodsToEat().getFoodID()) < AutoWintertodtPlugin.getWintertodtConfig().minFood()) {
            return false;
        } else if (Inventory.search().withId(20703).first().isPresent()) {
            return false;
        } else {
            return !AutoWintertodtPlugin.isInWintertodtRegion();
        }
    }

    public void runTask() {
        Optional<TileObject> bigDoor = TileObjects.search().withAction("Enter").first();
        if (!bigDoor.isEmpty()) {
            if (EthanApiPlugin.playerPosition().distanceTo(Data.DOOR_TILE_OUTSIDE) > 5) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(Data.DOOR_TILE_OUTSIDE);
            } else {
                bigDoor.ifPresent((x) -> {
                    TileObjectInteraction.interact(x, new String[]{"Enter"});
                });
            }
        }
    }
}
