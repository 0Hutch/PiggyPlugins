package com.polyplugins.Chompy;


import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "<html><font color=\"#7ecbf2\">[PJ]</font>AutoChompy</html>",
        description = "Kills chompys",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class AutoChompyPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private AutoChompyConfig config;
    @Inject
    private AutoChompyOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    private boolean started = false;
    public int timeout = 0;
    private State state = State.WAITING;
    private NPCQuery swampToads;
    private NPCQuery bloatedToads;
    private ItemQuery bloatedToadsItem;
    private NPCQuery birds;

    @Provides
    private AutoChompyConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoChompyConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        timeout = 0;
        swampToads = NPCs.search().nameContains("wamp toad").withAction("Inflate");
        bloatedToads = NPCs.search().nameContains("loated Toad");
        bloatedToadsItem = Inventory.search().nameContains("loated toad");
        birds = NPCs.search().nameContains("ompy bird").withAction("Attack");
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        timeout = 0;
        started = false;
        swampToads = null;
        bloatedToads = null;
        bloatedToadsItem = null;
        birds = null;
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }
        determineNextState();
        doChompy();
    }

    private void doChompy() {
        checkRunEnergy();
//        log.info(state.toString());
        log.info(getNearestFreeTile().toString());

        switch (state) {
            case KILL_BIRD:
                handleKillBird();
                break;
            case FILL_BELLOWS:
                handleFillBellows();
                break;
            case DROP_TOAD:
                handleDropToad();
                break;
            case INFLATE_TOAD:
                handleInflateToad();
                break;
            default:
                determineNextState();
                break;
        }
    }

    private void determineNextState() {
        if (!birds.empty()) {
            state = State.KILL_BIRD;
        } else if (Inventory.search().nameContains("bellows (").empty() && !TileObjects.search().nameContains("wamp bubble").empty()) {
            state = State.FILL_BELLOWS;
        } else if (bloatedToads.result().size() < 3 && !bloatedToadsItem.empty()) {
            state = State.DROP_TOAD;
        } else if (bloatedToadsItem.empty()) {
            state = State.INFLATE_TOAD;
        }
    }

    private void handleKillBird() {
        birds.first().ifPresent(npc -> {
            MousePackets.queueClickPacket();
            NPCPackets.queueNPCAction(npc, "Attack");
        });
    }

    private void handleFillBellows() {
        MousePackets.queueClickPacket();
        TileObjects.search().nameContains("wamp bubble").nearestToPlayer().ifPresent(tileObject -> {
            TileObjectInteraction.interact(tileObject, "Suck");
            timeout = 1;
        });
    }

    public boolean isStandingOnToad() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        return NPCs.search().nameContains("loated Toad").result().stream()
                .anyMatch(toad -> toad.getWorldLocation().equals(playerLocation));
    }

    private void handleDropToad() {
        if (isStandingOnToad()) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(getNearestFreeTile().get());
            timeout = 3;
        }
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(bloatedToadsItem.first().get(), "Drop");
    }

    private Optional<WorldPoint> getNearestFreeTile() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        List<WorldPoint> surroundingTiles = new ArrayList<>();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                if (dx != 0 || dy != 0) { // Exclude the player's current tile
                    surroundingTiles.add(playerLocation.dx(dx).dy(dy));
                }
            }
        }
        List<WorldPoint> toadLocations = NPCs.search().nameContains("loated toad")
                .result().stream().map(NPC::getWorldLocation).collect(Collectors.toList());
        List<WorldPoint> freeTiles = surroundingTiles.stream().filter(tile -> !toadLocations.contains(tile))
                .collect(Collectors.toList());
        return freeTiles.stream().min(Comparator.comparingInt(tile -> tile.distanceTo(playerLocation)));
    }

    private void handleInflateToad() {
        MousePackets.queueClickPacket();
        swampToads.nearestToPlayer().ifPresent(npc -> {
            NPCPackets.queueNPCAction(npc, "Inflate");
            timeout = 1;
        });
    }

    private void checkRunEnergy() {
        if (runIsOff() && client.getEnergy() >= 30 * 100) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    private enum State {
        FILL_BELLOWS, INFLATE_TOAD, DROP_TOAD, WAITING, KILL_BIRD
    }

    private boolean runIsOff() {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0;
    }

    private boolean isMovingOrInteracting() {
        //a-1026 fill toad & bellow
        return EthanApiPlugin.isMoving() || client.getLocalPlayer().getAnimation() == 1026 || client.getLocalPlayer().getInteracting() != null;
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;
    }
}