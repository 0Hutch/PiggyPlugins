package com.spinplugins.IronBuddy.tasks.Crafting;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.piggyplugins.PiggyUtils.API.BankUtil;
import com.piggyplugins.PiggyUtils.API.ObjectUtil;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import com.spinplugins.IronBuddy.data.Const;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;

import java.util.HashSet;
import java.util.Optional;

@Slf4j
public class ReturnToBankTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public ReturnToBankTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return (Inventory.full() && Inventory.getItemAmount(ItemID.UNPOWERED_ORB) >= 27)
                || (Inventory.getItemAmount(ItemID.MOLTEN_GLASS) != 0
                && Inventory.getItemAmount(ItemID.GLASSBLOWING_PIPE) == 0
                && Inventory.getItemAmount(ItemID.BUCKET_OF_SAND) == 0
                && Inventory.getItemAmount(ItemID.SODA_ASH) == 0);
    }

    @Override
    public void execute() {
        Optional<Widget> mainContinueOpt = Widgets.search().withTextContains("Click here to continue").first();

        if (mainContinueOpt.isPresent()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(mainContinueOpt.get().getId(), -1);
            return;
        }

        if(config.taskType() == Const.BuddyTasks.CRAFTING_GLASS_ITEM) {
            if(plugin.isCrafting) {
                plugin.isCrafting = false;
            }

            if(Bank.isOpen()) {
                log.info("Depositing items");
                BankInventoryInteraction.useItem(ItemID.UNPOWERED_ORB, "Deposit-All", "Deposit-all");
                return;
            }

            TileObjects.search().withName("Bank booth").nearestToPlayer().ifPresentOrElse(
                    tileObject -> {
                        log.info("Attempting to bank");
                        interactObject(tileObject, "Bank");
                    }
                    , () -> log.info("No bank found")
            );

            return;
        }

        if(plugin.isSmelting) {
            plugin.isSmelting = false;
        }

        if(Bank.isOpen()) {
            log.info("Depositing items");
            BankUtil.depositAll();
            return;
        }

        TileObjects.search().withName("Bank booth").nearestToPlayer().ifPresentOrElse(
                tileObject -> {
                    log.info("Attempting to bank");
                    interactObject(tileObject, "Bank");
                }
                , () -> log.info("No bank found")
        );
    }
}