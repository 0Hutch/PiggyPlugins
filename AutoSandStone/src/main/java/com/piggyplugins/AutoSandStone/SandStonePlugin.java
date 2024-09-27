package com.piggyplugins.AutoSandStone;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;

import static com.piggyplugins.AutoSandStone.SandStates.*;

@PluginDescriptor(
        name = "<html><font color=#86C43F>[B]</font> Sand Stone</html>",
        description = "",
        enabledByDefault = false,
        tags = {"bn", "plugins"}
)

public class SandStonePlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SandStoneOverlay overlay;
    @Inject
    SandStoneConfig config;
    private Instant startTime;
    public int timeout;
    public SandStates currentState = STARTING;
    boolean startup;
    public int sandStoneMined = 0;
    public int freeSlots;
    public int previousFreeSlots;


    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
        startup = true;
        currentState = IDLE;
        sandStoneMined = 0;
        freeSlots = 0;
        previousFreeSlots = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startTime = null;
        timeout = 0;
        startup = false;
        currentState = IDLE;
        sandStoneMined = 0;
        freeSlots = 0;
        previousFreeSlots = 0;
    }

    public SandStates getCurrentState() {
        return currentState;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public int sandStoneMined() {
        return sandStoneMined;
    }

    @Provides
    SandStoneConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SandStoneConfig.class);
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (timeout > 0) {
            timeout--;
            return;
        }

        freeSlots = Inventory.getEmptySlots();
        handleCounter();
        handleState();

        switch (currentState) {
            case STARTING:
                break;
            case MINING_SANDSTONE:
                mineSandStone();
                break;
            case DEPOSITING_SANDSTONE:
                depositSandstone();
                break;
            case CASTING_HUMIDIFY:
                castHumidify();
                break;
        }
    }

    private void handleState() {
        if (startup) {
            currentState = SandStates.STARTING;
            startup = false;
        }

        if (isInQuarry() && !Inventory.full() && !isCurrentlyMining())
            currentState = MINING_SANDSTONE;
        if (isInQuarry() && Inventory.full() && inventoryHasSandstone())
            currentState = DEPOSITING_SANDSTONE;
        if (isInQuarry() && shouldCastHumidify())
            currentState = CASTING_HUMIDIFY;
    }

    public void handleCounter() {
        if (freeSlots < previousFreeSlots) {
            sandStoneMined++;
        }
        previousFreeSlots = freeSlots;
    }

    public void mineSandStone() {
        if (!client.getLocalPlayer().isInteracting() && !EthanApiPlugin.isMoving()) {
            TileObjects.search().withId(11386).nearestToPlayer().ifPresent(sandStone -> {
                TileObjectInteraction.interact(sandStone, "Mine");
                timeout += 2;
            });
        }
    }

    public void depositSandstone() {
        if (!EthanApiPlugin.isMoving()) {
            TileObjects.search().withId(26199).first().ifPresent(grinder -> {
                TileObjectInteraction.interact(grinder, "Deposit");
            });
        }
    }

    public void castHumidify() {
        Widget humidify = client.getWidget(WidgetInfoExtended.SPELL_HUMIDIFY.getPackedId());
        if (!client.getLocalPlayer().isInteracting() && !isCastingHumidify()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(humidify, "Cast");
            timeout += 6;
        }
    }


    public boolean inventoryHasSandstone() {
        return Inventory.search().nameContains("Sandstone").first().isPresent();
    }

    public boolean isCurrentlyMining() {
        return client.getLocalPlayer().getAnimation() == 624;
    }

    public boolean isInQuarry() {
        return client.getLocalPlayer().getWorldLocation().getRegionID() == 12589;
    }

    public boolean shouldCastHumidify() {
        return Inventory.search().withName("Waterskin(0)").first().isPresent();
    }

    public boolean isCastingHumidify() {
        return client.getLocalPlayer().getAnimation() == 6294;
    }
}
