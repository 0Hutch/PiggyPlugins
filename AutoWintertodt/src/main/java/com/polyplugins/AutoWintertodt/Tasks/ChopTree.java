package com.polyplugins.AutoWintertodt.Tasks;

import com.polyplugins.AutoWintertodt.AutoWintertodtPlugin;
import com.polyplugins.AutoWintertodt.Data.Data;
import com.polyplugins.AutoWintertodt.Model.Task;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import java.util.Optional;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.TileObject;

public class ChopTree implements Task {
    @Inject
    private Client client;

    public ChopTree() {
    }

    public boolean activateTask() {
        if (!AutoWintertodtPlugin.isGameStarted()) {
            return false;
        } else {
            return !Data.hasResources;
        }
    }

    public boolean hasDragonAxe() {
        return InventoryUtil.hasItem(6739);
    }

    public void runTask() {
        Optional<TileObject> tree = TileObjects.search().withAction("Chop").withinDistance(10).first();
        if (!tree.isEmpty()) {
            if (!EthanApiPlugin.playerPosition().equals(Data.CHOP_TILE)) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(Data.CHOP_TILE);
            } else if (EthanApiPlugin.getClient().getLocalPlayer().getAnimation() == -1) {
                tree.ifPresent((x) -> {
                    TileObjectInteraction.interact(x, new String[]{"Chop"});
                });
            }
        }
    }
}
