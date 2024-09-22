package com.spinplugins.IronBuddy.tasks.Bank;

import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BankPinTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public BankPinTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return plugin.getClient().getWidget(213, 0) != null;
    }

    @Override
    public void execute() {
        plugin.typeBankPin("7610");
    }
}