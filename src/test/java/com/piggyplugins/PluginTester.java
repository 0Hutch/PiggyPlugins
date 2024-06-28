package com.piggyplugins;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PrayerFlicker.EthanPrayerFlickerPlugin;
import com.gustlikplugins.AutoNightmareZone.AutoNightmareZonePlugin;
import com.piggyplugins.LeftClickBlackJack.LeftClickBlackJackPlugin;
import com.piggyplugins.PiggyUtils.PiggyUtilsPlugin;
import com.piggyplugins.strategyexample.StrategySmithPlugin;
import com.polyplugins.AutoBoner.AutoBonerPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.plugins.ChinBreakHandler.ChinBreakHandlerPlugin;
import net.runelite.client.plugins.betterprofiles.BetterProfilesPlugin;


public class PluginTester {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,
                PiggyUtilsPlugin.class// Don't remove these
        /* Add your plugins in this method when running from the IDE.
           Make sure to include them as a dependency in the build.gradle via `testImplementation` */,
                StrategySmithPlugin.class, AutoBonerPlugin.class, EthanPrayerFlickerPlugin.class, LeftClickBlackJackPlugin.class, AutoNightmareZonePlugin.class);
        RuneLite.main(args);
    }
}