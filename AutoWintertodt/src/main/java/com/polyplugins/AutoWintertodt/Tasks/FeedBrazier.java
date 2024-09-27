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

public class FeedBrazier implements Task {
    public FeedBrazier() {
    }

    public boolean activateTask() {
        if (!Data.hasResources) {
            return false;
        } else if (!AutoWintertodtPlugin.isGameStarted()) {
            return false;
        } else if (AutoWintertodtPlugin.getWintertodtConfig().doFletching() && Inventory.search().withId(20695).first().isPresent()) {
            return false;
        } else {
            return TileObjects.search().withAction("Fix").withinDistance(10).nearestToPoint(Data.BRAZIER_TILE).isPresent() ? false : TileObjects.search().withAction("Light").withinDistance(10).nearestToPoint(Data.BRAZIER_TILE).isEmpty();
        }
    }

    public void runTask() {
        Optional<TileObject> activeBrazier = TileObjects.search().withAction("Feed").withinDistance(10).nearestToPoint(Data.BRAZIER_TILE);
        if (!activeBrazier.isEmpty()) {
            if (!EthanApiPlugin.playerPosition().equals(Data.BRAZIER_TILE)) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(Data.BRAZIER_TILE);
            } else {
                if (EthanApiPlugin.getClient().getLocalPlayer().getAnimation() != -1) {
                    Data.setLastAnimationMs(System.currentTimeMillis());
                }

                if (System.currentTimeMillis() - Data.lastAnimationMs >= 1000L) {
                    activeBrazier.ifPresent((x) -> {
                        TileObjectInteraction.interact(x, new String[]{"Feed"});
                    });
                }
            }
        }
    }
}
