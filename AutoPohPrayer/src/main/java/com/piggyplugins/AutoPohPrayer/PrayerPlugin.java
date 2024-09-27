package com.piggyplugins.AutoPohPrayer;


import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;
import java.util.Optional;

@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> POH Prayer</html>",
        description = "",
        enabledByDefault = false,
        tags = {"bn", "plugins"}
)

public class PrayerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public PrayerOverlay overlay;
    @Inject
    PrayerConfig config;
    @Inject
    EventBus eventBus;
    private Instant startTime;
    public int timeout;
    public PrayerStates currentState = PrayerStates.STARTING;
    boolean startup;


    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        startTime = Instant.now();
        startup = true;
        currentState = PrayerStates.IDLE;
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        startTime = null;
        timeout = 0;
        startup = false;
        currentState = PrayerStates.IDLE;

    }

    public PrayerStates getCurrentState() {
        return currentState;
    }

    public Instant getStartTime() {
        return startTime;
    }


    @Provides
    PrayerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PrayerConfig.class);
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            String message = event.getMessage();
            if (message.contains("You haven't visited anyone this session.")) {
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>You need to enter a house with an Altar first. Stopping plugin.", null);
                EthanApiPlugin.stopPlugin(this);
            }
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        refreshTabs();

        if (timeout > 0) {
            timeout--;
            return;
        }

        handleState();
        safeGuards();

        switch (currentState) {
            case ENTERING_HOUSE:
                enterHouse();
                break;
            case LEAVING_HOUSE:
                leaveHouse();
                break;
            case UNNOTING_BONES:
                useNoteOnPhials();
                break;
            case HANDLING_DIALOG:
                unnoteBones();
                break;
            case USING_BONES_ON_ALTAR:
                useBonesOnAltar();
                break;
            case RESTORING_STAMINA:
                drinkPool();
                break;
        }
    }

    private void handleState() {
        if (startup) {
            currentState = PrayerStates.STARTING;
            startup = false;
        }
        if (!altarInVicinity() && hasUnnotedBonesInInventory())
            currentState = PrayerStates.ENTERING_HOUSE;
        if (hasUnnotedBonesInInventory() && altarInVicinity())
            currentState = PrayerStates.USING_BONES_ON_ALTAR;
        if (!Inventory.full() && isInInstance() && noBonesLeft())
            currentState = PrayerStates.LEAVING_HOUSE;
        if (!Inventory.full() && noBonesLeft() && isOutsideHouse())
            currentState = PrayerStates.UNNOTING_BONES;
        if (isDialogOpen())
            currentState = PrayerStates.HANDLING_DIALOG;
        if (isInInstance() && config.drinkPool() && poolIsPresent() && lowRunEnergy()) {
            currentState = PrayerStates.RESTORING_STAMINA;
        }
    }

    private void refreshTabs() {
        eventBus.post(new ItemContainerChanged(InventoryID.INVENTORY.getId(), client.getItemContainer(InventoryID.INVENTORY)));
        eventBus.post(new ItemContainerChanged(InventoryID.EQUIPMENT.getId(), client.getItemContainer(InventoryID.EQUIPMENT)));
    }


    public void enterHouse() {
        if (!client.getLocalPlayer().isInteracting() && !EthanApiPlugin.isMoving()) {
        TileObjects.search().withId(29091).nearestToPlayer().ifPresent(house -> {
            TileObjectInteraction.interact(house, "Visit-Last");
        });
        }
    }


    private void leaveHouse() {
        if (!EthanApiPlugin.isMoving()) {
            TileObjects.search().nameContains("Portal").nearestToPlayer().ifPresent(portal -> {
                TileObjectInteraction.interact(portal, "Enter");
            });
        }
    }

    private void drinkPool() {
        TileObjects.search().nameContains("Pool").withAction("Drink").nearestToPlayer().ifPresent(pool -> {
            TileObjectInteraction.interact(pool, "Drink");
        });
    }

    private void useNoteOnPhials() {
        NPCs.search().withName("Phials").nearestToPlayer().ifPresent(phials -> {
            Inventory.search().withName(config.nameOfBones()).first().ifPresent(bones -> {
                MousePackets.queueClickPacket();
                NPCPackets.queueWidgetOnNPC(phials, bones);
            });
        });
    }

    private void unnoteBones() {
        Widget exchangeAll = Widgets.search().withTextContains("Exchange All").first().get();
        if (!isDialogOpen() && !EthanApiPlugin.isMoving() && !exchangeAll.isHidden())
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(14352385, 3);
    }



    private void useBonesOnAltar() {
        Inventory.search().onlyUnnoted().nameContains(config.nameOfBones()).first().ifPresent(bone -> {
            TileObjects.search().withName(config.nameOfAltar()).first().ifPresent(altar -> {
                MousePackets.queueClickPacket();
                ObjectPackets.queueWidgetOnTileObject(bone, altar);
//                currentSubState = "Using Bones on Altar..";
            });
        });
    }

    private void safeGuards() {
        Optional<Widget> coins = Inventory.search().withName("Coins").first();
        Optional<Widget> bones = Inventory.search().withName(config.nameOfBones()).first();
        if (coins.isEmpty() || bones.isEmpty()) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>You are out of coins or noted bones. Stopping plugin.", null);
            EthanApiPlugin.stopPlugin(this);
        }
    }

    public boolean hasUnnotedBonesInInventory() {
        return Inventory.search().onlyUnnoted().nameContains(config.nameOfBones()).first().isPresent();
    }

    public boolean lowRunEnergy() {
        return client.getEnergy() < 2000;
    }

    public boolean noBonesLeft() {
        return Inventory.search().onlyUnnoted().nameContains(config.nameOfBones()).empty();}

    public boolean isDialogOpen() {
        return (!Widgets.search().withId(14352384).empty());
    }

    public boolean poolIsPresent() {
        return TileObjects.search().nameContains("Pool").withAction("Drink").nearestToPlayer().isPresent();
    }

    public boolean isInInstance() {
        return client.getLocalPlayer().getWorldLocation().isInScene(client);
    }

    public boolean altarInVicinity() {
        return TileObjects.search().withinDistance(10).withName(config.nameOfAltar()).nearestToPlayer().isPresent();
    }

    public boolean isOutsideHouse() {
        return TileObjects.search().withId(29091).nearestToPlayer().isPresent();
    }
}

