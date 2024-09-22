package com.spinplugins.IronBuddy.tasks.Crafting;

import com.example.EthanApiPlugin.Collections.*;
import com.example.InteractionApi.BankInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.piggyplugins.PiggyUtils.API.ObjectUtil;
import com.piggyplugins.PiggyUtils.API.PlayerUtil;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;

import java.util.HashSet;
import java.util.Optional;

@Slf4j
public class GetComponentsTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public GetComponentsTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        switch (config.taskType()) {
            case CRAFTING_GLASS_ITEM: {
                return Inventory.getEmptySlots() >= 27;
            }
            case CRAFTING_GLASS:
                return Inventory.getEmptySlots() >= 14 && (Inventory.getItemAmount(ItemID.BUCKET_OF_SAND) < 14 || Inventory.getItemAmount(config.seaweedType().getId()) < 14);
        }

        return false;
    }

    @Override
    public void execute() {
        if(plugin.isSmelting || plugin.isCrafting) {
            plugin.isSmelting = false;
            plugin.isCrafting = false;
        }

        Optional<Widget> mainContinueOpt = Widgets.search().withTextContains("Click here to continue").first();

        if (mainContinueOpt.isPresent()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(mainContinueOpt.get().getId(), -1);
            return;
        }

        if(Bank.isOpen()) {
            Optional<Widget> bankItem = Optional.empty();
            int withdrawAmount = 0;

            switch (config.taskType()) {
                case CRAFTING_GLASS_ITEM: {
                    if (Inventory.getEmptySlots() == 28) {
                        log.info("Withdrawing 1 glass-blowing pipe.");

                        withdrawAmount = 1;
                        bankItem = Bank.search().withId(ItemID.GLASSBLOWING_PIPE).first();
                    } else if (BankInventory.search().withId(ItemID.GLASSBLOWING_PIPE).first().isPresent() && Bank.search().withId(ItemID.MOLTEN_GLASS).first().isPresent()) {
                        log.info("Withdrawing ALL molten glass.");

                        withdrawAmount = 28;
                        bankItem = Bank.search().withId(ItemID.MOLTEN_GLASS).first();
                    }

                    break;
                }
                case CRAFTING_GLASS: {
                    withdrawAmount = 14;

                    if (Inventory.getEmptySlots() == 28) {
                        log.info("Withdrawing 14 buckets of sand");
                        bankItem = Bank.search().withId(ItemID.BUCKET_OF_SAND).first();
                    } else if (!Inventory.full() && BankInventory.search().withId(ItemID.BUCKET_OF_SAND).first().isPresent()) {
                        log.info("Withdrawing 14 soda ash");
                        bankItem = Bank.search().withId(ItemID.SODA_ASH).first();
                    }
                }
            }

            if (bankItem.isPresent()) {
                if (withdrawAmount == 28) {
                    BankInteraction.useItem(bankItem.get(), "Withdraw-All", "Withdraw-all");
                } else {
                    BankInteraction.withdrawX(bankItem.get(), withdrawAmount);
                }
            } else {
                log.info("No items found in bank");
            }
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