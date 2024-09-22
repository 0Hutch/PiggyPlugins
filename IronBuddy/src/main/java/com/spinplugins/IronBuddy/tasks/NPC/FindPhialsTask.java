package com.spinplugins.IronBuddy.tasks.NPC;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import com.spinplugins.IronBuddy.data.Const;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.widgets.Widget;

import java.util.Optional;

@Slf4j
public class FindPhialsTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public FindPhialsTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    private int getCoins() {
        if (Inventory.search().withName("Coins").first().isPresent()) {
            return Inventory.search().withName("Coins").first().get().getItemQuantity();
        }
        return 0;
    }

    public boolean hasCoins() {
        log.info("Coins: " + getCoins() + " / " + Const.plankPrice);
        return getCoins() > Const.plankPrice;
    }

    public boolean canMakePlanks() {
        Optional<Widget> planks = InventoryUtil.getItemNameContains("plank", false);
        return planks.filter(widget -> Inventory.search().isNoted(widget)).isPresent();
    }

    public boolean findPhials() {
        return NPCs.search().withId(NpcID.PHIALS).first().isPresent();
    }

    public void usePlanksOnPhials() {
        Optional<Widget> planks = InventoryUtil.getItemNameContains("plank", false);
        Optional<NPC> phials = NPCs.search().withId(NpcID.PHIALS).first();

        if (phials.isPresent() && planks.isPresent()) {
            MousePackets.queueClickPacket();
            NPCPackets.queueWidgetOnNPC(
                    phials.get(),
                    planks.get()
            );
        }
    }

    @Override
    public boolean validate() {
        return !Inventory.full() &&
                !plugin.chattingWithPhials() &&
                canMakePlanks() &&
                hasCoins() &&
                findPhials();
    }

    @Override
    public void execute() {
        usePlanksOnPhials();
    }
}