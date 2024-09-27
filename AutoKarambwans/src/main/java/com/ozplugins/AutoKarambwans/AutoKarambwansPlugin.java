package com.ozplugins.AutoKarambwans;

import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.ObjectPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
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
import java.util.concurrent.ThreadLocalRandom;

import static com.ozplugins.AutoKarambwans.AutoKarambwansState.*;

@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto Karambwans</html>",
        enabledByDefault = false,
        description = "Fishes Kajambams for you",
        tags = {"Oz"}
)
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
public class AutoKarambwansPlugin extends Plugin {
    protected static final Random random = new Random();
    Instant timer;
    boolean enablePlugin;
    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    ObjectPackets objectPackets;
    @Inject
    AutoKarambwansConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private AutoKarambwansOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    AutoKarambwansState state;
    int timeout = 0;
    private int nextRunEnergy;
    List<WorldPoint> FISH_AREA = (new WorldArea(2893,3111, 40, 15, 0).toWorldPointList());

    @Provides
    AutoKarambwansConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoKarambwansConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        enablePlugin = false;
        timer = Instant.now();
        state = null;
        keyManager.registerKeyListener(pluginToggle);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        state = null;
        timeout = 0;
        enablePlugin = false;
        keyManager.unregisterKeyListener(pluginToggle);
        timer = null;
        //EthanApiPlugin.stopPlugin(this);
    }

    public AutoKarambwansState getState() {
        Player player = client.getLocalPlayer();

        if (player == null) {
            return UNHANDLED_STATE;
        }

        if (timeout > 0) {
            return TIMEOUT;
        }
        if (EthanApiPlugin.isMoving()) {
            overlay.infoStatus = "Moving";
            return MOVING;
        }
        if (isBankPinOpen()) {
            overlay.infoStatus = "Bank PIN";
            return BANK_PIN;
        }

        if (client.getLocalPlayer().getAnimation() != -1) {
            overlay.infoStatus = "Fishing";
            return ANIMATING;
        }

        if (!isBankOpen()) {
            if (!isInventoryFull()) {
                return FISH;
            } else {
                return FIND_BANK;
            }
        } else if (isBankOpen()) {
            if (bankInventoryHasFish()) {
                return DEPOSIT_FISH;
            }  else if (!bankInventoryFull()) {
                return FISH;
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
                overlay.infoStatus = "Banking";
                if (isInKaramja()) {
                    handleFairyRing();
                } else {

                    openNearestBank();
                }
                timeout = tickDelay();
                break;

            case DEPOSIT_FISH:
                overlay.infoStatus = "Depositing fish";
                BankInventory.search().withAction("Empty").withName("Open fish barrel").first().ifPresent(x -> {
                    BankInventoryInteraction.useItem(x, "Empty");
                });

                BankInventory.search().withName("Raw karambwan").first().ifPresent(x -> {
                    BankInventoryInteraction.useItem(x, "Deposit-All");
                });
                timeout = tickDelay();
                break;

            case FISH:
                overlay.infoStatus = "Fishing";
                if (isInKaramja()) {
                    handleFish();
                } else {
                    handleFairyRing();
                }
                timeout = tickDelay();
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "Ded";
                break;

            case MOVING:
            case BANK_PIN:
            case ANIMATING:
            case IDLE:
                timeout = tickDelay();
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
            sendGameMessage("Auto Karambwans disabled.");
            enablePlugin = false;
            //shutDown();
        } else {
            sendGameMessage("Auto Karambwans enabled.");
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!enablePlugin) {
            return;
        }
        //TODO Counter or somethin for amount of fish caught

        ChatMessageType chatMessageType = event.getType();

        if (chatMessageType != ChatMessageType.GAMEMESSAGE && chatMessageType != ChatMessageType.SPAM) {
            return;
        }

        MessageNode messageNode = event.getMessageNode();
    }

    public void handleFairyRing() {
        Optional<TileObject> fairy_ring = TileObjects.search().withName("Fairy ring").nearestByPath();
        if (fairy_ring.isPresent()) {
            overlay.infoStatus = "Using fairy ring";
            if (isInKaramja()) {
                TileObjectInteraction.interact(fairy_ring.get(), "Zanaris");
            } else {
                TileObjectInteraction.interact(fairy_ring.get(), "Last-destination (DKP)");
            }
        } else {
            overlay.infoStatus = "Finding fairy ring";
            //Failsafe in case fairy ring isn't visible or in distance. Prolly not needed tho havent really tested.
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(2395 + getRandomIntBetweenRange(0, 9),
                    4443 + getRandomIntBetweenRange(0, 9), false);
        }
    }

    public void openNearestBank() {
        Optional<TileObject> bankChest = TileObjects.search().withName("Bank chest").nearestToPlayer();
        Optional<NPC> banker = NPCs.search().withAction("Bank").nearestToPlayer();

        //Opens bank
        if (!isBankOpen()) {
            if (banker.isPresent()) {
                overlay.infoStatus = "Banking";
                NPCInteraction.interact(banker.get(), "Bank");
            } else if (bankChest.isPresent()) {
                overlay.infoStatus = "Banking";
                TileObjectInteraction.interact(bankChest.get(), "Use");
            } else {
                //Failsafe in case bank isn't visible or in distance. Prolly not needed tho.
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(2395 + getRandomIntBetweenRange(0, 9),
                        4443 + getRandomIntBetweenRange(0, 9), false);
            }
        }
    }

    public void handleFish() {
        Inventory.search().withAction("Open").withName("Fish barrel").first().ifPresent(x -> {
            BankInventoryInteraction.useItem(x, "Open");
        });

        Optional<NPC> fish = NPCs.search().withName("Fishing spot").nearestByPath();
        Optional<Widget> karambwanji = Inventory.search().withId(ItemID.RAW_KARAMBWANJI).first();
        Optional<Widget> empty_vessel = Inventory.search().withId(ItemID.KARAMBWAN_VESSEL).first();
        Optional<Widget> full_vessel = Inventory.search().withId(ItemID.KARAMBWAN_VESSEL_3159).first();

        if (karambwanji.isPresent() && (empty_vessel.isPresent() || full_vessel.isPresent())) {
            if (fish.isPresent()) {
                NPCInteraction.interact(fish.get(), "Fish");
            } else {
                sendGameMessage("Error: No fishing spot found.");
            }
        } else {
            overlay.infoStatus = "Missing items";
            sendGameMessage("Missing bait or vessel. Turning off plugin.");
            enablePlugin = false;
        }
    }

    public boolean isBankOpen() { return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null); }
    public boolean bankInventoryFull() { return (BankInventory.search().result().size() == 28); }
    public boolean bankInventoryHasFish() { return (!BankInventory.search().withId(ItemID.RAW_KARAMBWAN).empty()); }
    public boolean isRunEnabled() { return client.getVarpValue(173) == 1; }
    public boolean isInventoryFull() { return (Inventory.getEmptySlots() == 0); }
    public boolean isBankPinOpen() { return client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null; }

    public boolean isInKaramja() {
        return FISH_AREA.contains(client.getLocalPlayer().getWorldLocation());
    }

    //TODO Move all of this back into API
    private int tickDelay() {
        int tickLength = (int) randomDelay(config.tickDelayWeightedDistribution(), config.tickDelayMin(), config.tickDelayMax(), config.tickDelayDeviation(), config.tickDelayTarget());
        //log.debug("tick delay for {} ticks", tickLength);
        return tickLength;
    }

    public long randomDelay(boolean weightedDistribution, int min, int max, int deviation, int target) {
        if (weightedDistribution) {
            return (long) clamp((-Math.log(Math.abs(random.nextGaussian()))) * deviation + target, min, max);
        } else {
            /* generate a normal even distribution random */
            return (long) clamp(Math.round(random.nextGaussian() * deviation + target), min, max);
        }
    }

    private double clamp(double val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public int getRandomIntBetweenRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
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
}
