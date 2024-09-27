package com.piggyplugins.AerialFishing;

import com.example.EthanApiPlugin.Collections.*;
import com.example.InteractionApi.*;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import com.piggyplugins.AerialFishing.Enums.AerialStates;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.sql.SQLOutput;
import java.time.Instant;
import java.util.List;

import static com.piggyplugins.AerialFishing.Enums.AerialStates.*;

@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> Aerial Fishing</html>",
        description = "Stinky rat killer",
        enabledByDefault = false,
        tags = {"bn", "plugins"}
)

public class AerialPlugin extends Plugin {
    @Inject
    Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public AerialOverlay overlay;
    @Inject
    AerialConfig config;
    @Inject
    public ItemManager itemManager;
    private Instant startTime;
    public int timeout;
    public AerialStates currentState = STARTING;
    private final int runOrbWidget = WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getPackedId();
    private List<Integer> fish = List.of(ItemID.BLUEGILL,ItemID.COMMON_TENCH,ItemID.MOTTLED_EEL,ItemID.GREATER_SIREN);


    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startTime = null;
    }

    public AerialStates getCurrentState() {
        return currentState;
    }

    public Instant getStartTime() {
        return startTime;
    }


    @Provides
    AerialConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AerialConfig.class);
    }

    @Subscribe
    private void onChatMessage(ChatMessage chatMessage) {
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
//        System.out.println(Equipment.search().withId(ItemID.CORMORANTS_GLOVE).first().isPresent());
//        System.out.println(Equipment.search().withId(ItemID.CORMORANTS_GLOVE_22817).first().isPresent());
        if (timeout > 0) {
            timeout--;
            return;
        }

        handleState();
        switch (currentState) {
            case STARTING:
                case CATCHING_FISH:
                    catchFish();
                    break;
                case CUTTING_FISH:
                    cutFish();
                    break;
        }
    }

    private void handleState() {
        if (hasBird()) {
            currentState = CATCHING_FISH;
            return;
        }
        if (!hasBird() && inventoryHasFish()) {
            currentState = CUTTING_FISH;
            return;
        }
    }

    private void catchFish() {
        if (hasBird()) {
            NPCs.search().withId(8523).nearestToPlayer().ifPresent(npc -> {
                NPCInteraction.interact(npc, "Catch");
                System.out.println("Fishing");
            });
        }
    }


    private void cutFish() {
        Inventory.search().withId(ItemID.KNIFE).first().ifPresent(knife -> {
            Inventory.search().idInList(fish).first().ifPresent(fish -> {
                MousePackets.queueClickPacket();
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(knife, fish);
            });
        });
    }


    private boolean hasBird() {
        return Equipment.search().withId(ItemID.CORMORANTS_GLOVE_22817).first().isPresent();
    }

    private boolean inventoryHasNoFish() {
        return Inventory.search().idInList(fish).first().isEmpty();
    }

    private boolean inventoryHasFish() {
        return Inventory.search().idInList(fish).first().isPresent();
    }
}


