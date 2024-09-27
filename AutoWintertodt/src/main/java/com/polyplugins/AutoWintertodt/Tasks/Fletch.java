package com.polyplugins.AutoWintertodt.Tasks;

import com.polyplugins.AutoWintertodt.AutoWintertodtPlugin;
import com.polyplugins.AutoWintertodt.Data.Data;
import com.polyplugins.AutoWintertodt.Model.Task;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;

public class Fletch implements Task {
    public Fletch() {
    }

    public boolean activateTask() {
        if (!AutoWintertodtPlugin.isGameStarted()) {
            return false;
        } else if (!Data.hasResources) {
            return false;
        } else if (!AutoWintertodtPlugin.getWintertodtConfig().doFletching()) {
            return false;
        } else if (Inventory.search().withId(946).first().isEmpty()) {
            return false;
        } else if (TileObjects.search().withAction("Fix").withinDistance(10).nearestToPoint(Data.BRAZIER_TILE).isPresent()) {
            return false;
        } else {
            return TileObjects.search().withAction("Light").withinDistance(10).nearestToPoint(Data.BRAZIER_TILE).isPresent() ? false : Inventory.search().withId(20695).first().isPresent();
        }
    }

    public void runTask() {
        if (!EthanApiPlugin.playerPosition().equals(Data.BRAZIER_TILE)) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(Data.BRAZIER_TILE);
        } else {
            if (EthanApiPlugin.getClient().getLocalPlayer().getAnimation() != -1) {
                Data.setLastAnimationMs(System.currentTimeMillis());
            }

            if (System.currentTimeMillis() - Data.lastAnimationMs >= 1000L) {
                Inventory.search().withId(946).first().ifPresent((firstItem) -> {
                    Inventory.search().withId(20695).first().ifPresent((secondItem) -> {
                        MousePackets.queueClickPacket();
                        WidgetPackets.queueWidgetOnWidget(firstItem, secondItem);
                    });
                });
            }
        }
    }
}
