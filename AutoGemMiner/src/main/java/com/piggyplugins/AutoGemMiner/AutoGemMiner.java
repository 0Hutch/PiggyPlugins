package com.piggyplugins.AutoGemMiner;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.PathingTesting.PathingTesting;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> Gem Miner</html>",
        description = "",
        enabledByDefault = false,
        tags = {"bn", "plugin"}
)

@Slf4j
public class AutoGemMiner extends Plugin {
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoGemMinerOverlay overlay;
    @Inject
    AutoGemMinerConfig config;
    public GemMinerState currentState = GemMinerState.IDLE;
    private Instant startTime;
    int gemCounter;
    public int freeSlots;
    public int previousFreeSlots;
    public int timeout;
    boolean startup;
    WorldPoint BankTile = new WorldPoint(2851, 2955, 0);
    WorldPoint GemMine = new WorldPoint(2820, 2997, 0);
    @Provides
    AutoGemMinerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoGemMinerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
        startup = true;
        gemCounter = 0;
        freeSlots = 0;
        timeout = 0;
        previousFreeSlots = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startup = false;
        startTime = null;
        currentState = GemMinerState.IDLE;
        freeSlots = 0;
        previousFreeSlots = 0;
        timeout = 0;
    }

    public GemMinerState currentState() {
        return currentState;
    }

    public int getGemCounter() {
        return gemCounter;
    }

    public Instant getStartTime() {
        return startTime;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }


        if(timeout > 0) {
            timeout--;
            return;
        }


        freeSlots = Inventory.getEmptySlots();
        handleCounter();
        handleState();

        switch (currentState) {
            case WALKING_TO_MINE:
                walkToMine();
                break;
            case MINING_GEMS:
                mineGems();
                break;
            case WALKING_TO_BANK:
                walkToBank();
                break;
            case OPENING_BANK:
                openBank();
                break;
            case DEPOSIT_GEMS:
                depositGems();
                break;
            case CLOSE_BANK:
                closeBank();
                break;
        }

    }

    private void handleState() {
        if (startup) {
            currentState = GemMinerState.STARTING;
            startup = false;
        }
        if (isAtShiloVillage() && !Inventory.full() && !isInGemArea())
            currentState = GemMinerState.WALKING_TO_MINE;
        if (!Inventory.full() && isInGemArea())
            currentState = GemMinerState.MINING_GEMS;
        if (Inventory.full() && !isAtBankArea())
            currentState = GemMinerState.WALKING_TO_BANK;
        if (isAtBankArea() && Inventory.full())
            currentState = GemMinerState.OPENING_BANK;
        if (Bank.isOpen() && Inventory.full())
            currentState = GemMinerState.DEPOSIT_GEMS;
        if (isAtBankArea() && Bank.isOpen() && !Inventory.full())
            currentState = GemMinerState.CLOSE_BANK;
    }

    public void handleCounter() {
        if (freeSlots < previousFreeSlots) {
            gemCounter++;
        }
        previousFreeSlots = freeSlots;
    }

    public void walkToMine() {
        WorldPoint gemMine = new WorldPoint(2820, 2997, 0);
        PathingTesting.walkTo(gemMine);
    }

    public void mineGems() {
        if (!isMining() && !EthanApiPlugin.isMoving()) {
            TileObjects.search().withName("Gem rocks").nearestToPlayer().ifPresent(gemRocks -> {
                TileObjectInteraction.interact(gemRocks, "Mine");
                timeout += 2;
            });
        }
    }

    public void walkToBank() {
        if (!EthanApiPlugin.isMoving()) {
            WorldPoint bankLocation = new WorldPoint(2851, 2955, 0);
            PathingTesting.walkTo(bankLocation);
        }
    }

    public void openBank() {
        if (!EthanApiPlugin.isMoving() && !client.getLocalPlayer().isInteracting()) {
            NPCs.search().withName("Banker").nearestToPlayer().ifPresent(bankNpc -> {
                NPCInteraction.interact(bankNpc, "Bank");
                timeout += 1;
            });
        }
    }


    public void depositGems() {
        if (Bank.isOpen()) {
            Widget depositInventory = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
            if (depositInventory != null) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(depositInventory, "Deposit inventory");
            }
        }
    }

    public void closeBank() {
        timeout += 2;
        MousePackets.queueClickPacket();
        client.runScript((29));
    }

    public Set<WorldPoint> gemArea() {
        return new HashSet<>(new WorldArea(new WorldPoint(2818, 2996, 0), 11, 10).toWorldPointList());
    }

    public Set<WorldPoint> bankArea() {
        return new HashSet<>(new WorldArea(new WorldPoint(2850, 2952, 0), 5, 6).toWorldPointList());
    }

    public boolean isAtBankArea() {
        return bankArea().contains(client.getLocalPlayer().getWorldLocation());
    }

    public boolean isInGemArea() {
        return gemArea().contains(client.getLocalPlayer().getWorldLocation());
    }

    public boolean isAtShiloVillage() {
        return client.getLocalPlayer().getWorldLocation().getRegionID() == 11310;
    }

    public boolean isMining() {
        return client.getLocalPlayer().getAnimation() == AnimationID.MINING_RUNE_PICKAXE;
    }
}


