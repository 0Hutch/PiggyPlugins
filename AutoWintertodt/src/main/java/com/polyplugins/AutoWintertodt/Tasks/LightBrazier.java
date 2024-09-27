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

public class LightBrazier implements Task {
    public LightBrazier() {
    }

    public boolean activateTask() {
        if (!Data.hasResources) {
            return false;
        } else if (!AutoWintertodtPlugin.isGameStarted()) {
            return false;
        } else {
            return Inventory.search().withId(590).first().isEmpty() ? false : TileObjects.search().withAction("Light").withinDistance(10).nearestToPoint(Data.BRAZIER_TILE).isPresent();
        }
    }

    public void runTask() {
        Optional<TileObject> inactiveBrazier = TileObjects.search().withAction("Light").withinDistance(10).nearestToPoint(Data.BRAZIER_TILE);
        if (!inactiveBrazier.isEmpty()) {
            if (!EthanApiPlugin.playerPosition().equals(Data.BRAZIER_TILE)) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(Data.BRAZIER_TILE);
            } else {
                inactiveBrazier.ifPresent((x) -> {
                    TileObjectInteraction.interact(x, new String[]{"Light"});
                });
            }
        }
    }
}
