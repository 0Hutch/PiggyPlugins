package com.ozplugins.AutoMTA;

import com.ozplugins.AutoMTA.Rooms.Rooms;
import com.ozplugins.AutoMTA.util.Utils;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;
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
import net.runelite.client.plugins.mta.MTAPlugin;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Instant;

import static com.ozplugins.AutoMTA.AutoMTAState.*;


@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto MTA</font></html>",
        enabledByDefault = false,
        description = "Does any room in the MTA for you.",
        tags = {"oz"}
)
@Slf4j
public class AutoMTAPlugin extends Plugin {
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    Client client;
    @Inject
    AutoMTAConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public AutoMTAOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    public Utils util;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    public Rooms rooms;
    @Inject
    PluginManager pluginManager;
    AutoMTAState state;
    public int timeout = 0;
    Room room;
    public int hintArrowWait = 0;

    public LocalPoint playerLocalPoint;
    WorldArea MTA_START_AREA = new WorldArea(new WorldPoint(3360, 3298, 0), 7, 23);

    @Provides
    AutoMTAConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoMTAConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        room = config.Room();
        enablePlugin = false;
        botTimer = Instant.now();
        state = null;
        keyManager.registerKeyListener(pluginToggle);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        keyManager.unregisterKeyListener(pluginToggle);
        overlayManager.remove(overlay);
        resetVals();
    }

    private void resetVals() {
        state = TIMEOUT;
        enablePlugin = false;
    }

    public AutoMTAState getState() {
        Player player = client.getLocalPlayer();

        if (player == null) {
            return UNHANDLED_STATE;
        }
        if (timeout > 0) {
            return TIMEOUT;
        }
        if (EthanApiPlugin.isMoving()) {
            return MOVING;
        }

        if (isInGraveyard()) {
            return HANDLE_GRAVEYARD;
        }

        if (isInAlch()) {
            return HANDLE_ALCHEMY;
        }

        if (isInEnchant()) {
            return HANDLE_ENCHANTMENT;
        }

        if (isAtMTA()) {
            return HANDLE_GO_IN_ROOM;
        }

        if (isInTelekinetic()) {
            return HANDLE_TELEKINETIC;
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
        playerLocalPoint = client.getLocalPlayer().getLocalLocation();
        room = config.Room();

        if (config.stopAtPoints() > 0) {
            if (getRoomPoints() >= config.stopAtPoints()) {
                log.info("" + getRoomPoints());
                addMessage("Reached required points, stopping!");
                handleLeaveRoom();
                resetVals();
                return;
            }
        }

        state = getState();
        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case HANDLE_BREAK:
                if (isAtMTA()) {
                    timeout = 10;
                } else {
                    handleLeaveRoom();
                }
                break;

            case HANDLE_ALCHEMY:
                rooms.handleAlchemyRoom();
                break;

            case HANDLE_GRAVEYARD:
                rooms.handleGraveyardRoom();
                break;

            case HANDLE_ENCHANTMENT:
                rooms.handleEnchantRoom();
                break;

            case HANDLE_TELEKINETIC:
                rooms.handleTelekineticRoom();
                break;

            case HANDLE_GO_IN_ROOM:
                handleGoInRoom();
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "Ded";
                break;

            case MOVING:
            case ANIMATING:
            case BANK_PIN:
            case IDLE:
                util.handleRun(1, 1);
                timeout = 1;
                break;
        }
    }

    private int getRoomPoints() {
        Widget pointWidget;
        if (room == Room.ALCHEMY) {
            pointWidget = client.getWidget(InterfaceID.MTA_ALCHEMY, 6);
        } else if (room == Room.TELEKINETIC) {
            pointWidget = client.getWidget(InterfaceID.MTA_TELEKINETIC, 6);
        } else if (room == Room.ENCHANTING) {
            pointWidget = client.getWidget(InterfaceID.MTA_ENCHANT, 6);
        } else {
            pointWidget = client.getWidget(InterfaceID.MTA_GRAVEYARD, 6);
        }

        if (pointWidget == null) {
            return Integer.MIN_VALUE;
        }

        return Integer.parseInt(pointWidget.getText());
    }

    private void handleGoInRoom() {
        TileObjects.search().withAction("Enter").withId(config.Room().getObjectID()).nearestToPlayer().ifPresent(x -> {
            overlay.infoStatus = "Entering room";
            TileObjectInteraction.interact(x, "Enter");
            timeout = util.tickDelay() + 3;
        });
    }

    public void handleLeaveRoom() {
        TileObjects.search().withAction("Enter").nearestToPlayer().ifPresent(x -> {
            overlay.infoStatus = "Exiting room";
            TileObjectInteraction.interact(x, "Enter");
            timeout = util.tickDelay();
        });
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
            clientThread.invokeLater(() -> {
                EthanApiPlugin.sendClientMessage("Auto MTA disabled.");
            });
            resetVals();
        } else {
            clientThread.invokeLater(() -> {
                MTAPlugin mta_plugin = RuneLite.getInjector().getInstance(MTAPlugin.class);

                if (mta_plugin == null) {
                    sendGameMessage("Runelite Mage Training Arena plugin is not installed, turning off Auto MTA.");
                    sendGameMessage("Please install the MTA plugin from RuneLite plugin hub.");
                    return;
                }

                if (!pluginManager.isPluginEnabled(mta_plugin)) {
                    //pluginManager.startPlugin(plugin);
                    pluginManager.setPluginEnabled(mta_plugin, true);
                }

                EthanApiPlugin.sendClientMessage("Auto MTA enabled.");
            });
            botTimer = Instant.now();
            keyManager.registerKeyListener(pluginToggle);
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
    public boolean isAtMTA() {
        return MTA_START_AREA.contains(client.getLocalPlayer().getWorldLocation());
    }

    private boolean isInTelekinetic() {
        return !Widgets.search().hiddenState(false).withTextContains("Telekinetic").empty();
    }

    private boolean isInEnchant() {
        return TileObjects.search().withAction("Take-from").nearestToPlayer().isPresent();
    }

    private boolean isInAlch() {
        return TileObjects.search().withAction("Search").withName("Cupboard").nearestToPlayer().isPresent();
    }

    private boolean isInGraveyard() {
        return TileObjects.search().withAction("Grab").nearestToPlayer().isPresent();
    }

    private void addMessage(String message) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
    }
}
