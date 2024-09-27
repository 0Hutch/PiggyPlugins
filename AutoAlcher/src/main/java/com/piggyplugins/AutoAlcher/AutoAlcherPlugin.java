package com.piggyplugins.AutoAlcher;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Set;

@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> Auto Alcher </html>",
        description = "Automatically alchs specified items",
        tags = {"alch", "magic", "automation"},
        enabledByDefault = false
)
public class AutoAlcherPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    AutoAlcherOverlay overlay;
    @Inject
    OverlayManager overlayManager;
    @Inject
    AutoAlchConfig config;
    private Instant startTime;
    public AutoAlcherStates currentState = AutoAlcherStates.STARTING;

    private boolean alchingEnabled = false;
    private int alchTimeout = 0;
    private Set<String> itemsToAlch;
    private String currentSubState = "";

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startTime = null;
        itemsToAlch = null;
    }

    @Provides
    AutoAlchConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoAlchConfig.class);
    }

    public AutoAlcherStates getCurrentState() {
        return currentState;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public String getCurrentSubState() {
        return currentSubState;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        castHighAlch(config.itemsToAlch());
    }


    public void castHighAlch(String items) {
        Widget alch = client.getWidget(WidgetInfoExtended.SPELL_HIGH_LEVEL_ALCHEMY.getPackedId());
        if (!isAlching()) {
            String[] itemsToAlch = config.itemsToAlch().split("\\s*,\\s*");
            for (String itemToAlch : itemsToAlch) {
                Inventory.search().withName(itemToAlch).first().ifPresent(item -> {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetOnWidget(alch, item);
                    currentState = AutoAlcherStates.ALCHING;
                });
            }
        }
    }

    public boolean isAlching() {
        return client.getLocalPlayer().getAnimation() == 713;
    }


    public boolean missingKnife() {
        return Inventory.search().withId(ItemID.TINDERBOX).empty();
    }

    public boolean missingHammer() {
        return Inventory.search().withId(ItemID.HAMMER).empty();
    }

    public boolean missingAxe() {
        return Equipment.search().nameContainsNoCase("axe").empty();
    }
}
