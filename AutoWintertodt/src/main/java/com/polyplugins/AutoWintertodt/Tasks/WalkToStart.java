package com.polyplugins.AutoWintertodt.Tasks;

import com.polyplugins.AutoWintertodt.AutoWintertodtPlugin;
import com.polyplugins.AutoWintertodt.Data.Data;
import com.polyplugins.AutoWintertodt.Model.Task;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;

public class WalkToStart implements Task {
    public WalkToStart() {
    }

    public boolean activateTask() {
        if (AutoWintertodtPlugin.isGameStarted()) {
            return false;
        } else if (!AutoWintertodtPlugin.isInWintertodtRegion()) {
            return false;
        } else if (Inventory.search().withId(20703).first().isPresent()) {
            return false;
        } else {
            return Inventory.getItemAmount(AutoWintertodtPlugin.wintertodtConfig.foodsToEat().getFoodID()) > AutoWintertodtPlugin.wintertodtConfig.minFood();
        }
    }

    public void runTask() {
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(Data.CHOP_TILE);
    }
}
