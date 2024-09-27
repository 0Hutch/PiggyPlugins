package com.ozplugins.AutoMinnows;

import com.example.EthanApiPlugin.Collections.*;
import com.example.InteractionApi.NPCInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.ozplugins.AutoMinnows.util.Utils;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Instant;
import java.util.Objects;

import static com.ozplugins.AutoMinnows.AutoMinnowsState.*;


@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto Minnows</font></html>",
        enabledByDefault = false,
        description = "Fishes minnows for ya",
        tags = {"oz"}
)
@Slf4j
public class AutoMinnowsPlugin extends Plugin {
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    Client client;
    @Inject
    AutoMinnowsConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public AutoMinnowsOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    public Utils util;

    AutoMinnowsState state;
    public int timeout = 0;
    int minnowsHeld = 0;

    private int FLYING_FISH_GRAPHIC_ID = 1387;
    private int FISHING_SPOT_1_ID = 7732;
    private int FISHING_SPOT_2_ID = 7733;
    private int TARGET_SPOT_ID = FISHING_SPOT_1_ID;
    NPC interactingFishingSpot;

    public LocalPoint playerLocalPoint;

    @Provides
    AutoMinnowsConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoMinnowsConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        enablePlugin = false;
        botTimer = Instant.now();
        state = null;
        keyManager.registerKeyListener(pluginToggle);
        overlayManager.add(overlay);
        minnowsHeld = 0;
    }

    @Override
    protected void shutDown() {
        keyManager.unregisterKeyListener(pluginToggle);
        overlayManager.remove(overlay);
        minnowsHeld = 0;
        resetVals();
    }

    private void resetVals() {
        state = TIMEOUT;
        enablePlugin = false;
    }

    public AutoMinnowsState getState() {
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

        if (config.stopAtMinnows() != 0 && config.stopAtMinnows() <= minnowsHeld) {
            return TURN_IN_MINNOWS;
        }

        if (player.getInteracting() != null) {
            if (player.getInteracting().hasSpotAnim(FLYING_FISH_GRAPHIC_ID) && interactingFishingSpot.getId() == TARGET_SPOT_ID) {
                return HANDLE_NEW_SPOT;
            }
        }

        if (player.getInteracting() == null) {
            return START_FISHING;
        }

        if (player.getAnimation() != -1) {
            return ANIMATING;
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
        minnowsHeld = getMinnowsHeld();

        state = getState();
        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "Ded";
                break;

            case START_FISHING:
            case HANDLE_NEW_SPOT:
                handleNewFishingSpot();
                break;

            case TURN_IN_MINNOWS:
                handleTurnInMinnows();
                break;

            case ANIMATING:
                overlay.infoStatus = "Fishing";
                break;

            case MOVING:
            case BANK_PIN:
            case IDLE:
                util.handleRun(1, 1);
                timeout = 1;
                break;
        }
    }

    private int getMinnowsHeld() {
        return Inventory.search().withName("Minnow")
                .first()
                .map(Widget::getItemQuantity)
                .orElse(0);
    }

    private void handleTurnInMinnows() {
        timeout = util.tickDelay();

        if (Widgets.search().withTextContains("can give you").hiddenState(false).first().isPresent()) {
            Widgets.search().withTextContains("Click here to continue").hiddenState(false).first().ifPresent(w -> {
                overlay.infoStatus = "Handling dialogue";
                MousePackets.queueClickPacket();
                if (w.getId() == 12648448) {
                    WidgetPackets.queueResumePause(w.getId(), 1);
                } else {
                    WidgetPackets.queueResumePause(w.getId(), -1);
                }
            });
            return;
        }

        if (Widgets.search().withTextContains("many sharks").hiddenState(false).first().isPresent()) {
            overlay.infoStatus = "Turning in Minnows";
            util.sendIntValue((int) Math.floor(minnowsHeld / 40));
            return;
        }

        NPCs.search().withName("Kylie Minnow").withAction("Trade").nearestByPath().ifPresentOrElse(x ->{
            overlay.infoStatus = "Finding Kylie";
            NPCInteraction.interact(x, "Trade");
        },() -> {
            sendGameMessage("Dude not found, shutting down plugin.");
            enablePlugin = false;
        });
    }

    private void handleNewFishingSpot() {
        //TODO so messy will clean up lol
        if (interactingFishingSpot != null && client.getLocalPlayer().getInteracting() != null) {
            if (client.getLocalPlayer().getInteracting().hasSpotAnim(FLYING_FISH_GRAPHIC_ID) && interactingFishingSpot.getId() == TARGET_SPOT_ID) {
                TARGET_SPOT_ID = (TARGET_SPOT_ID == FISHING_SPOT_1_ID) ? FISHING_SPOT_2_ID : FISHING_SPOT_1_ID;
            }
        }

        NPCs.search().withId(TARGET_SPOT_ID).nearestByPath().ifPresentOrElse(x ->{
            overlay.infoStatus = "Finding Minnows";
            NPCInteraction.interact(x, "Small Net");
        },() -> {
            sendGameMessage("Fishing spot not found, shutting down plugin.");
            enablePlugin = false;
        });
        timeout = util.tickDelay();
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event) {
        if (event.getSource() == client.getLocalPlayer()
                && event.getTarget() instanceof NPC
        && !Objects.equals(event.getTarget().getName(), "Kylie Minnow")) {
            interactingFishingSpot = (NPC) event.getTarget();
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
            clientThread.invokeLater(() -> {
                EthanApiPlugin.sendClientMessage("Auto Minnows disabled.");
            });
            resetVals();
        } else {
            clientThread.invokeLater(() -> {
                EthanApiPlugin.sendClientMessage("Auto Minnows enabled.");
            });
            botTimer = Instant.now();
            keyManager.registerKeyListener(pluginToggle);
        }
    }

    private void sendGameMessage(String message) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
    }
}
