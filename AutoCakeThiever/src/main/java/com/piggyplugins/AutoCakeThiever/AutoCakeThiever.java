package com.piggyplugins.AutoCakeThiever;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.PathingTesting.PathingTesting;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
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
import java.util.Optional;

@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> Cake Thiever </html>",
        description = "",
        enabledByDefault = false,
        tags = {"bn", "plugins"}
)

public class AutoCakeThiever extends Plugin {
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoCakeThieverOverlay overlay;
    @Inject
    net.runelite.client.plugins.AutoCakeThiever.AutoCakeThieverConfig config;
    private Instant startTime;
    public int timeout;
    public net.runelite.client.plugins.AutoCakeThiever.State currentState = net.runelite.client.plugins.AutoCakeThiever.State.STARTING;
    boolean startup;
    boolean shouldThieve = false;
    WorldPoint BankTile = new WorldPoint(2655, 3283, 0);
    WorldPoint StallTile = new WorldPoint(2669, 3310, 0);
    private static final int STALL_WITH_CAKE = 11730;
    private static final int EMPTY_STALL = 634;


    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
        startup = true;
        currentState = net.runelite.client.plugins.AutoCakeThiever.State.IDLE;
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startTime = null;
        timeout = 0;
        startup = false;
        currentState = net.runelite.client.plugins.AutoCakeThiever.State.IDLE;
    }

    public net.runelite.client.plugins.AutoCakeThiever.State getCurrentState() {
        return currentState;
    }

    public Instant getStartTime() {
        return startTime;
    }

    @Provides
    net.runelite.client.plugins.AutoCakeThiever.AutoCakeThieverConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(net.runelite.client.plugins.AutoCakeThiever.AutoCakeThieverConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        handleAutoEat();

        if (timeout > 0) {
            timeout--;
            return;
        }

        handleState();

        switch (currentState) {
            case STARTING:
                handleState();
                break;
            case WALKING_TO_STALL:
                walkToStall();
                break;
            case THIEVING_CAKE:
                thieveCakes();
                break;
            case DROPPING_REST:
                dropBreadAndSlices();
                break;
            case WALKING_TO_BANK:
                walkToBank();
                break;
            case OPENING_BANK:
                openBank();
                break;
            case DEPOSIT_CAKES:
                depositCakes();
                break;
            case CLOSE_BANK:
                closeBank();
                break;
            case IDLE:
                Idle();
                break;
        }
    }

    private void handleState() {
        if (startup) {
            currentState = net.runelite.client.plugins.AutoCakeThiever.State.STARTING;
            startup = false;
        }
        if (isInArdougneMarketArea() && !isAtStallTile())
            currentState = net.runelite.client.plugins.AutoCakeThiever.State.WALKING_TO_STALL;
        if (!isInventoryFull() && isAtStallTile() && shouldThieve)
            currentState = net.runelite.client.plugins.AutoCakeThiever.State.THIEVING_CAKE;
        if (shouldDropItems())
            currentState = net.runelite.client.plugins.AutoCakeThiever.State.DROPPING_REST;
        if (Inventory.full())
            currentState = net.runelite.client.plugins.AutoCakeThiever.State.WALKING_TO_BANK;
        if (isAtBank() && isInventoryFull())
            currentState = net.runelite.client.plugins.AutoCakeThiever.State.OPENING_BANK;
        if (Bank.isOpen() && isInventoryFull())
            currentState = net.runelite.client.plugins.AutoCakeThiever.State.DEPOSIT_CAKES;
        if (isAtBank() && Bank.isOpen() && inventoryIsEmpty())
            currentState = net.runelite.client.plugins.AutoCakeThiever.State.CLOSE_BANK;
    }

    public void thieveCakes() {
        TileObjects.search().withName("Baker's stall").withId(STALL_WITH_CAKE).withinDistance(5).nearestToPlayer().ifPresent(stall -> {
            TileObjectInteraction.interact(stall, "Steal-from");
            timeout += 2;
        });
    }

    public void openBank() {
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

    public void depositCakes() {
        if (Bank.isOpen()) {
            timeout += 2;
            Widget depositInventory = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
            if (depositInventory != null) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(depositInventory, "Deposit inventory");
            }
        }
    }

    public void closeBank() {
        MousePackets.queueClickPacket();
        client.runScript((29));
    }


    public void handleAutoEat() {
        int currentHealth = client.getBoostedSkillLevel(Skill.HITPOINTS);
        int eatThreshold = config.getHealthThreshold();
        String foodName = config.getFoodName(); // Assuming foodName is the name or ID of the food item

        if (currentHealth < eatThreshold) {
            Optional<Widget> foodItem = Inventory.search().matchesWildCardNoCase(foodName).first();
            if (foodItem.isPresent()) {
                InventoryInteraction.useItem(foodItem.get(), "Eat");
            }
        }
    }

    public void dropBreadAndSlices() {
        Optional<Widget> bread = Inventory.search().withName("Bread").first();
        Optional<Widget> chocolateSlice = Inventory.search().withName("Chocolate slice").first();
        Optional<Widget> sliceOfCake = Inventory.search().withName("Slice of cake").first();
        Optional<Widget> twoThirdsCake = Inventory.search().withName("2/3 cake").first();

        bread.ifPresent(b -> InventoryInteraction.useItem(b, "Drop"));
        chocolateSlice.ifPresent((c -> InventoryInteraction.useItem(c, "Drop")));
        sliceOfCake.ifPresent(s -> InventoryInteraction.useItem(s, "Drop"));
        twoThirdsCake.ifPresent((t -> InventoryInteraction.useItem(t, "Drop")));
    }

    public void walkToBank() {
        if (!EthanApiPlugin.isMoving()) {
            WorldPoint bankLocation = new WorldPoint(2655, 3283, 0);
            PathingTesting.walkTo(bankLocation);
        }
    }

    public void walkToStall() {
        if (!EthanApiPlugin.isMoving()) {
            WorldPoint stall = new WorldPoint(2669, 3310, 0);
            PathingTesting.walkTo(stall);
        }
    }


    public boolean hasBreadAndSlices() {
        boolean hasBread = Inventory.search().withName("Bread").first().isPresent();
        boolean hasChocolateSlice = Inventory.search().withName("Chocolate slice").first().isPresent();
        boolean hasSliceOfCake = Inventory.search().withName("Slice of cake").first().isPresent();
        boolean hasTwoThirdsCake = Inventory.search().withName("2/3 cake").first().isPresent();

        return hasBread || hasChocolateSlice || hasSliceOfCake || hasTwoThirdsCake;
    }


    public void Idle() {
        // Implement logic to idle
    }

    public boolean isInventoryFull() {
        if(Inventory.full()) {
            shouldThieve = false;
            return true;
        }
        return false;
    }

    public boolean isAtStallTile() {
        if(client.getLocalPlayer().getWorldLocation().equals(StallTile)) {
            shouldThieve = true;
        }
        return client.getLocalPlayer().getWorldLocation().equals(StallTile);
    }
    public boolean isAtBank() {
        return client.getLocalPlayer().getWorldLocation().distanceTo(BankTile) <= 6;
    }

    public boolean isInArdougneMarketArea() {
        return client.getLocalPlayer().getWorldLocation().getRegionID() == 10547;
    }

    public boolean inventoryIsEmpty() {
        return Inventory.getEmptySlots() == 28;
    }

    public boolean shouldDropItems() {
        return config.dropBreadAndSlices() && hasBreadAndSlices();
    }

    public boolean shouldBank() {
        return isInventoryFull() && (!config.dropBreadAndSlices() || inventoryHasOnlyCakes());
    }

    public boolean inventoryHasOnlyCakes() {
        return Inventory.search().withId(ItemID.CAKE).result().size() == 28;
    }
}