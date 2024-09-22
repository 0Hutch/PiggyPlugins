package com.spinplugins.IronBuddy.tasks.Dialogue;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExchangePlanksTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public ExchangePlanksTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    private void exchangeAll() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueResumePause(14352385, 3);
    }

    @Override
    public boolean validate() {
        return plugin.chattingWithPhials() && !Inventory.full();
    }

    @Override
    public void execute() {
        exchangeAll();
    }
}