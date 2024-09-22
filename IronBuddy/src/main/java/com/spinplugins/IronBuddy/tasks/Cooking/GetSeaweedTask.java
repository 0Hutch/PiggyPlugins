package com.spinplugins.IronBuddy.tasks.Cooking;

import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import com.spinplugins.IronBuddy.IronBuddyConfig;
import com.spinplugins.IronBuddy.IronBuddyPlugin;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Optional;

@Slf4j
public class GetSeaweedTask extends AbstractTask<IronBuddyPlugin, IronBuddyConfig> {
    public GetSeaweedTask(IronBuddyPlugin plugin, IronBuddyConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void execute() {
        return;
    }
}