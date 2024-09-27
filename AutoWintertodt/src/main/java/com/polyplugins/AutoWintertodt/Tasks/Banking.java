package com.polyplugins.AutoWintertodt.Tasks;

import com.polyplugins.AutoWintertodt.AutoWintertodtPlugin;
import com.polyplugins.AutoWintertodt.Data.Data;
import com.polyplugins.AutoWintertodt.Model.Task;
import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import java.util.Optional;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

public class Banking implements Task {
    public Banking() {
    }

    public boolean activateTask() {
        if (AutoWintertodtPlugin.isInWintertodtRegion()) {
            return false;
        } else {
            Optional<Widget> supplyCrate = Inventory.search().withId(20703).first();
            if (supplyCrate.isPresent()) {
                return true;
            } else {
                return Inventory.getItemAmount(AutoWintertodtPlugin.wintertodtConfig.foodsToEat().getFoodID()) < AutoWintertodtPlugin.wintertodtConfig.minFood();
            }
        }
    }

    public void runTask() {
        int foodLeft = Inventory.getItemAmount(AutoWintertodtPlugin.wintertodtConfig.foodsToEat().getFoodID());
        Optional<TileObject> bankChest = TileObjects.search().withAction("Bank").nearestToPlayer();
        if (bankChest.isEmpty()) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(Data.BANK_TILE);
        } else if (!Bank.isOpen()) {
            bankChest.ifPresent((x) -> {
                TileObjectInteraction.interact(x, new String[]{"Bank"});
            });
        } else {
            BankInventory.search().withId(20703).first().ifPresent((x) -> {
                BankInventoryInteraction.useItem(x, new String[]{"Deposit-All"});
            });
            BankInventory.search().withId(AutoWintertodtPlugin.wintertodtConfig.foodsToEat().getFoodID()).first().ifPresent((x) -> {
                BankInventoryInteraction.useItem(x, new String[]{"Deposit-All"});
            });
            Bank.search().withId(AutoWintertodtPlugin.wintertodtConfig.foodsToEat().getFoodID()).first().ifPresent((x) -> {
                BankInteraction.withdrawX(x, AutoWintertodtPlugin.getWintertodtConfig().foodAmount());
            });
        }
    }
}
