package com.ozplugins.K1Tick;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.ozplugins.K1Tick.K1TickState.*;

@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> K1T</font></html>",
        enabledByDefault = false,
        description = "1 Tick cooks Karambwan at Rogue's Den.",
        tags = {"Oz"}
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
public class K1TickPlugin extends Plugin {
    protected static final Random random = new Random();
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    ObjectPackets objectPackets;
    @Inject
    K1TickConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private K1TickOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    EthanApiPlugin api;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    K1TickState state;
    int timeout = 0;
    private static final int cookActionWidget = 17694734;

    @Provides
    K1TickConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(K1TickConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        enablePlugin = false;
        botTimer = Instant.now();
        state = null;
        keyManager.registerKeyListener(pluginToggle);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        resetVals();
    }

    private void resetVals() {
        overlayManager.remove(overlay);
        state = null;
        timeout = 0;
        enablePlugin = false;
        keyManager.unregisterKeyListener(pluginToggle);
        botTimer = null;
    }

    public K1TickState getState() {
        Player player = client.getLocalPlayer();

        if (player == null) {
            return UNHANDLED_STATE;
        }
        if (EthanApiPlugin.isMoving()) {
            overlay.infoStatus = "Moving";
            return MOVING;
        }
        if (isBankPinOpen()) {
            overlay.infoStatus = "Bank PIN";
            return BANK_PIN;
        }

        if (!isBankOpen()) {
            if (inventoryHasRawKarambwan()) {
                return COOK;
            } else {
                return FIND_BANK;
            }
        } else if (isBankOpen()) {
            if (bankInventoryHasFish()) {
                return DEPOSIT_FISH;
            } else if (!bankInventoryHasRawKarambwan()) {
                return WITHDRAW_FISH;
            } else {
                return COOK;
            }
        }

        return UNHANDLED_STATE;
    }

    @Subscribe
    private void onGameTick(GameTick tick) {

        if (!enablePlugin) {
            return;
        }

        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        state = getState();

        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case FIND_BANK:
                openNearestBank();
                break;

            case DEPOSIT_FISH:
                overlay.infoStatus = "Depositing karambwan";
                BankInventory.search().withId(ItemID.COOKED_KARAMBWAN).first().ifPresent(x -> {
                    BankInventoryInteraction.useItem(x, "Deposit-All");
                });
                BankInventory.search().withId(ItemID.BURNT_KARAMBWAN).first().ifPresent(x -> {
                    BankInventoryInteraction.useItem(x, "Deposit-All");
                });
                break;

            case WITHDRAW_FISH:
                Bank.search().withId(ItemID.RAW_KARAMBWAN).first().ifPresent(x -> {
                    overlay.infoStatus = "Withdrawing karambwan";
                    BankInteraction.useItem(x, "Withdraw-All");
                });
                break;

            case COOK:
                handleCook();
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "dead";
                break;

            case MOVING:
            case BANK_PIN:
            case ANIMATING:
            case IDLE:
                break;
        }
    }

    private final HotkeyListener pluginToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            togglePlugin();
        }
    };

    public void togglePlugin() {
        enablePlugin = !enablePlugin;
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        if (!enablePlugin) {
            sendGameMessage("K1Tick disabled.");
        } else {
            sendGameMessage("K1Tick enabled.");
        }
    }

    public void openNearestBank() {
        Optional<TileObject> bankChest = TileObjects.search().withName("Bank chest").nearestToPlayer();
        Optional<NPC> banker = NPCs.search().withAction("Bank").nearestToPlayer();

        //Opens bank
        if (!isBankOpen()) {
            overlay.infoStatus = "Banking";
            if (banker.isPresent()) {
                NPCInteraction.interact(banker.get(), "Bank");
            } else if (bankChest.isPresent()) {
                TileObjectInteraction.interact(bankChest.get(), "Use");
            }
        }
    }

    public void handleCook() {
        Optional<TileObject> fire = TileObjects.search().withName("Fire").nearestToPlayer();

        Optional<Widget> raw = Inventory.search().withId(ItemID.RAW_KARAMBWAN).first();
        List<Widget> karambwan = Inventory.search().withId(ItemID.RAW_KARAMBWAN).result();

        if (client.getWidget(cookActionWidget) != null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(cookActionWidget, karambwan.size());
        }
        if (raw.isPresent()) {
            if (fire.isPresent() && karambwan.size() > 0) {
                overlay.infoStatus = "Start cooking";
                Widget karamb = karambwan.get(karambwan.size() - 1);
                MousePackets.queueClickPacket();
                ObjectPackets.queueWidgetOnTileObject(karamb, fire.get());
            } else {
                sendGameMessage("Error: fire not found.");
            }
        } else {
            overlay.infoStatus = "Missing items";
            sendGameMessage("Missing raw karambwan. Turning off plugin.");
            enablePlugin = false;
        }

    }

    public void sendGameMessage(String message) {
        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append(message)
                .build();

        chatMessageManager
                .queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());
    }

    public boolean isBankOpen() { return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null); }
    public boolean bankInventoryHasFish() {
        return (!BankInventory.search().withId(ItemID.BURNT_KARAMBWAN).empty() || !BankInventory.search().withId(ItemID.COOKED_KARAMBWAN).empty()); } //TODO Check for other fish
    public boolean inventoryHasRawKarambwan() { return (!Inventory.search().withId(ItemID.RAW_KARAMBWAN).empty()); }
    public boolean bankInventoryHasRawKarambwan() { return (!BankInventory.search().withId(ItemID.RAW_KARAMBWAN).empty()); }

    public boolean isRunEnabled() { return client.getVarpValue(173) == 1; }

    public boolean isBankPinOpen() {
        Widget bankPinWidget = client.getWidget(WidgetInfo.BANK_PIN_CONTAINER);
        return (bankPinWidget != null);
    }

}
