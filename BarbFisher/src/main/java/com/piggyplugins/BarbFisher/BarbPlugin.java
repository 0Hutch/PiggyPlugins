package com.piggyplugins.BarbFisher;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.PathingTesting.PathingTesting;
import com.google.inject.Inject;
import com.google.inject.Provides;
import jdk.jfr.Event;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import com.piggyplugins.AutoChopper.Enums.LogAction;
import com.piggyplugins.AutoGemMiner.GemMinerState;
import com.piggyplugins.Plugin;
import com.piggyplugins.PluginDescriptor;
import com.piggyplugins.toa.Prayer.WidgetInfoExt;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;
import java.util.*;


@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> Barbarian Fisher</html>",
        description = "",
        enabledByDefault = false,
        tags = {"bn", "plugin"}
)

@Slf4j
public class BarbPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BarbOverlay overlay;
    @Inject
    BarbConfig config;
    @Inject
    EventBus eventBus;
    public BarbStates currentState = BarbStates.IDLE;
    private Instant startTime;
    public int timeout;
    boolean startup;
    boolean isDropping;
    private List<Integer> fish = List.of(ItemID.LEAPING_SALMON,ItemID.LEAPING_TROUT,ItemID.LEAPING_STURGEON);


    @Provides
    BarbConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BarbConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
        startup = true;
        timeout = 0;
        isDropping = false;
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startup = false;
        startTime = null;
        currentState = BarbStates.IDLE;
        timeout = 0;
        isDropping = false;
    }

    public BarbStates currentState() {
        return currentState;
    }

    public Instant getStartTime() {
        return startTime;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        Widgets.search().withTextContains("you just advanced a").hiddenState(false).first().ifPresent(widget -> {
            WidgetPackets.queueResumePause(15269891, -1);
        });

        if(timeout > 0) {
            timeout--;
            return;
        }

        refreshTabs();
        handleState();

        switch (currentState) {
            case FISHING:
                catchFish();
                break;
            case DROPPING:
                dropFish(config.minDrop(), config.maxDrop());
                break;
        }
    }

    private void handleState() {
        if (startup) {
            currentState = BarbStates.IDLE;
            startup = false;
        }

        if (isInFishingRegion() && !Inventory.full() && !isDropping) {
            currentState = BarbStates.FISHING;
            return;
        }
        if (isInFishingRegion() && Inventory.full() && hasFish())
            currentState = BarbStates.DROPPING;
            isDropping = true;
            return;
    }

    private void refreshTabs() {
        eventBus.post(new ItemContainerChanged(InventoryID.INVENTORY.getId(), client.getItemContainer(InventoryID.INVENTORY)));
        eventBus.post(new ItemContainerChanged(InventoryID.EQUIPMENT.getId(), client.getItemContainer(InventoryID.EQUIPMENT)));
    }

    public void catchFish() {
        if (!client.getLocalPlayer().isInteracting() && !EthanApiPlugin.isMoving() && !isFishing()) {
            NPCs.search().withId(1542).nearestToPlayer().ifPresent(npc -> {
                NPCInteraction.interact(npc, "Use-rod");
                System.out.println("Catching fish");
            });
        }
    }

    private void dropFish(int minDropAmount, int maxDropAmount) {
        Random random = new Random();
        int dropAmount = random.nextInt(maxDropAmount - minDropAmount + 1) + minDropAmount;

        List<Widget> fishInInventory = Inventory.search().idInList(fish).result();
        for (int i = 0; i < dropAmount && i < fishInInventory.size(); i++) {
            InventoryInteraction.useItem(fishInInventory.get(i), "Drop");
        }

        if (hasNoFish()) {
            isDropping = false;
        }
    }


    public boolean isInFishingRegion() {
        return client.getLocalPlayer().getWorldLocation().getRegionID() == 10038;
    }

    public boolean hasNoFish() {
        return Inventory.search().idInList(fish).result().isEmpty();
    }

    public boolean hasFish() {
        return Inventory.search().idInList(fish).first().isPresent();
    }

    public boolean isFishing() {
        return client.getLocalPlayer().getAnimation() == 9350;
    }
}



