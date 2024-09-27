package com.bankmemory;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RuneLiteRunner {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(BankMemoryPlugin.class);
        RuneLite.main(args);
    }
}
