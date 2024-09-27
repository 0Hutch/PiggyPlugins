package com.piggyplugins.AutoChopper;


import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.*;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
//import com.example.PathingTesting.PathingTesting;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import com.piggyplugins.AutoChopper.Enums.ChopperStates;
import com.piggyplugins.AutoChopper.Enums.LogAction;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.piggyplugins.AutoChopper.Enums.ChopperStates.*;

@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> Auto Chopper</html>",
        description = "Stinky rat killer",
        enabledByDefault = false,
        tags = {"bn", "plugins"}
)

public class ChopperPlugin extends Plugin {
    @Inject
    Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public ChopperOverlay overlay;
    @Inject
    ChopperConfig config;
    @Inject
    EventBus eventBus;
    @Inject
    public ItemManager itemManager;
    private Instant startTime;
    public int timeout;
    public ChopperStates currentState = STARTING;
    private final int runOrbWidget = WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getPackedId();
    private List<Integer> fish = List.of(ItemID.BLUEGILL,ItemID.COMMON_TENCH,ItemID.MOTTLED_EEL,ItemID.GREATER_SIREN);
    private final Set<WorldPoint> fireLocations = new HashSet<>();
    private final List<Integer> fireId = List.of(26185);
    boolean canBurnOnTile;
    boolean isBurnable;
    boolean isDropping;
    boolean isBurning;
    boolean startup;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
        isDropping = false;
        isBurning = false;
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startTime = null;
        isDropping = false;
        isBurning = false;
    }

    public ChopperStates getCurrentState() {
        return currentState;
    }

    public Instant getStartTime() {
        return startTime;
    }


    @Provides
    ChopperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ChopperConfig.class);
    }


    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (fireId.contains(gameObject.getId())) {
            fireLocations.add(gameObject.getWorldLocation());
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        if (fireId.contains(gameObject.getId())) {
            fireLocations.remove(gameObject.getWorldLocation());
        }
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

        refreshTabs();
        handleState();

        switch (currentState) {
            case STARTING:
            case CHOPPING_LOGS:
                chopTree();
                break;
            case BURNING_LOGS:
                burn();
                break;
            case DROPPING_LOGS:
                dropLogs();
                break;
            case BANKING_LOGS:
                handleBanking();
                break;
            case OPENING_BANK:
                openBank();
                break;
            case CLOSING_BANK:
                closeBank();
                break;
            case USING_SPECIAL_ATTACK:
                useDragonAxeSpec();
                break;
        }
    }

    private void handleState() {
        if (startup) {
            currentState = STARTING;
            startup = false;
        }
        if (Inventory.full() && config.logAction() == LogAction.DROP) {
            currentState = DROPPING_LOGS;
            isDropping = true;
            return;
        }
        if (Inventory.full() && config.logAction() == LogAction.BURN) {
            currentState = BURNING_LOGS;
            isBurning = true;
            return;
        }
        if (!Bank.isOpen() && Inventory.full() && config.logAction() == LogAction.BANK) {
            currentState = OPENING_BANK;
            return;
        }
        if (Bank.isOpen() && Inventory.full() && config.logAction() == LogAction.BANK) {
            currentState = BANKING_LOGS;
            return;
        }
        if (!Inventory.full() && !isPlayerAnimating() && !isDropping && !isBurning) {
            currentState = CHOPPING_LOGS;
            return;
        }
        if (!isDropping && !isBurning && fullSpecialAttack()) {
            currentState = USING_SPECIAL_ATTACK;
            return;
        }
    }

    private void refreshTabs() {
        eventBus.post(new ItemContainerChanged(InventoryID.INVENTORY.getId(), client.getItemContainer(InventoryID.INVENTORY)));
        eventBus.post(new ItemContainerChanged(InventoryID.EQUIPMENT.getId(), client.getItemContainer(InventoryID.EQUIPMENT)));
    }

    private void chopTree() {
        if (!isPlayerAnimating() && !EthanApiPlugin.isMoving()) {
            TileObjects.search().withName(config.treeType().getTreeName()).nearestToPlayer().ifPresent(tree -> {
                TileObjectInteraction.interact(tree, "Chop down");
                System.out.println("Chopping tree");
            });
        }
    }

    private void dropLogs() {
        if (config.logAction() == LogAction.DROP) {
            List<Widget> logs = Inventory.search().withId(config.treeType().getLogItemId()).result();
            int count = 0;
            for (Widget log : logs) {
                if (count >= 2) break; // Adjust the "2" to any number to drop per tick
                InventoryInteraction.useItem(log, "Drop");
                count++;
            }
            if (logsAreEmpty()) {
                isDropping = false;
            }
        }
    }

    private void burn() {
        Optional<Widget> logs = Inventory.search().withId(config.treeType().getLogItemId()).first();
        if (logs.isEmpty()) {
            currentState = ChopperStates.CHOPPING_LOGS;

            return;
        }

        if (isPlayerAnimating() || EthanApiPlugin.isMoving()) {
            return;
        }

        WorldPoint currentPlayerPosition = EthanApiPlugin.playerPosition();
        Optional<TileObject> tileObject = TileObjects.search().atLocation(currentPlayerPosition).first();
        if (tileObject.isEmpty() || isBurnable(tileObject.get())) {
            Optional<Widget> tinderbox = Inventory.search().withName("Tinderbox").first();
            if (tinderbox.isPresent()) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(tinderbox.get(), logs.get());
            }

            return;
        }

        WorldPoint nextFireMakingTile = null;
        for (WorldPoint currentTile : EthanApiPlugin.reachableTiles().stream().sorted(Comparator.comparingInt(point -> point.distanceTo(EthanApiPlugin.playerPosition()))).collect(Collectors.toList())) {
            Optional<TileObject> nextPotentialTile = TileObjects.search().atLocation(currentTile).first();
            boolean canBurnOnTile = false;
            if (nextPotentialTile.isPresent()) {
                canBurnOnTile = isBurnable(tileObject.get());
            }

            if ((nextPotentialTile.isEmpty() || canBurnOnTile) && currentTile.getX() < currentPlayerPosition.getX()) {
                nextFireMakingTile = currentTile;

                break;
            }
        }

        if (nextFireMakingTile != null && nextFireMakingTile.distanceTo(currentPlayerPosition) >= 1) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(nextFireMakingTile);
        }
        if (logsAreEmpty()) {
            isBurning = false;
        }
    }

    private void handleBanking() {
        if (Bank.isOpen()) {
            BankInventory.search().nameContains("logs").first().ifPresent(item -> {
                BankInteraction.useItem(item, "Deposit-All");
            });
            BankInventory.search().nameContains("Bird nest").first().ifPresent(item -> {
                BankInteraction.useItem(item, "Deposit-All");
            });
        }
    }

    private void useDragonAxeSpec() {
            Equipment.search().withId(ItemID.DRAGON_AXE).first().ifPresent(axe -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, WidgetInfoExtended.COMBAT_SPECIAL_ATTACK_CLICKBOX.getPackedId(), -1, -1);
            });
    }

    private void openBank() {
        if (!Bank.isOpen() && !EthanApiPlugin.isMoving()) {
        Optional<TileObject> chest = TileObjects.search().withName("Bank chest").nearestToPlayer();
        Optional<NPC> banker = NPCs.search().withAction("Bank").nearestToPlayer();
        Optional<TileObject> booth = TileObjects.search().withAction("Bank").nearestToPlayer();
        if (chest.isPresent()) {
            TileObjectInteraction.interact(chest.get(), "Use");
            return;
        }
        if (booth.isPresent()) {
            TileObjectInteraction.interact(booth.get(), "Bank");
            return;
        }
        if (banker.isPresent()) {
            NPCInteraction.interact(banker.get(), "Bank");
            return;
        }
        if (!chest.isPresent() && !booth.isPresent() && !banker.isPresent()) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "No bank nearby, please relocate.", null);
            EthanApiPlugin.stopPlugin(this);
            }
        }
    }

    private void closeBank() {
        MousePackets.queueClickPacket();
        client.runScript((29));
    }

    private boolean isBurnable(TileObject object) {
        ObjectComposition composition = client.getObjectDefinition(object.getId());
        return composition.getName() == null || composition.getName().isEmpty() || composition.getName().equalsIgnoreCase("null");
    }

    private boolean isPlayerAnimating() {
        return client.getLocalPlayer().getAnimation() != AnimationID.IDLE;
    }

    private boolean fullSpecialAttack() {
        return client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000;

    }

    private boolean logsAreEmpty() {
        return Inventory.search().withId(config.treeType().getLogItemId()).first().isEmpty();
    }
}

