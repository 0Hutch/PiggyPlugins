package com.ozplugins.AutoTeleTabs;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
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
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.ozplugins.AutoTeleTabs.AutoTeleTabsState.*;

@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto Tele Tabs</font></html>",
        enabledByDefault = false,
        description = "Makes teletabs for you..",
        tags = {"Oz"}
)
@Slf4j
public class AutoTeleTabsPlugin extends Plugin {
    protected static final Random random = new Random();
    Instant botTimer;
    boolean enablePlugin;

    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    AutoTeleTabsConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private AutoTeleTabsOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    AutoTeleTabsState state;
    int timeout = 0;
    private int nextRunEnergy;


    @Provides
    AutoTeleTabsConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoTeleTabsConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        nextRunEnergy = 0;
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
        nextRunEnergy = 0;
        enablePlugin = false;
        keyManager.unregisterKeyListener(pluginToggle);
        botTimer = null;
    }

    public AutoTeleTabsState getState() {
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
        if (isBankPinOpen()) {
            overlay.infoStatus = "Bank Pin";
            return BANK_PIN;
        }

        if (client.getLocalPlayer().getAnimation() != -1) {
            return ANIMATING;
        }
        if (!isInPoh() || isInPoh() && !hasUnnotedClay()) {
            return UNNOTE_CLAY;
        }
        if (isTeleTabWidgetOpen()) {
            return MAKE_TAB;
        }
        if (isInPoh() && hasUnnotedClay()) {
            return FIND_LECTERN;
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
        handleRun(5, 5);

        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case FIND_BANK:
                openNearestBank();
                break;

            case FIND_LECTERN:
                handleFindLectern();
                break;

            case MAKE_TAB:
                handleMakeTab();
                break;

            case UNNOTE_CLAY:
                handleUnnoteClay();
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "ded";
                break;

            case ANIMATING:
                timeout = 3;
                break;

            case MOVING:
            case BANK_PIN:
            case IDLE:
                timeout = tickDelay();
                break;
        }
    }

    private boolean hasUnnotedClay() {
        return Inventory.search().matchesWildCardNoCase("*soft clay*").onlyUnnoted().first().isPresent();
    }

    private void handleUnnoteClay() {
        if (!isInPoh()) {
            if (hasUnnotedClay()) {
                TileObjects.search().withAction("Home").withName("Portal").nearestToPlayer().ifPresent(x -> {
                    overlay.infoStatus = "Using home portal";
                    TileObjectInteraction.interact(x, "Home");
                });
                return;
            }
            if (Widgets.search().withTextContains("Select an Option").first().isPresent()) {
                overlay.infoStatus = "Phials dialogue";
                MousePackets.queueClickPacket();
                WidgetPackets.queueResumePause(14352385, 3);
                return;
            }
            NPCs.search().withName("Phials").nearestToPlayer().ifPresent(phials -> {
                Inventory.search().matchesWildCardNoCase("*soft clay*").onlyNoted().first().ifPresent(clay -> {
                    overlay.infoStatus = "Using clay on Phials";
                    NPCPackets.queueWidgetOnNPC(phials, clay);
                });
            });
            return;
        }
        TileObjects.search().withAction("Enter").withName("Portal").nearestToPlayer().ifPresent(x -> {
            overlay.infoStatus = "Using home portal";
            TileObjectInteraction.interact(x, "Enter");
        });
    }

    private void handleMakeTab() {
        if (isConfirmButtonWidgetPresent()) {
            Widgets.search().withText("Create").first().ifPresent(x -> {
                overlay.infoStatus = "Creating Teletab";
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, x.getId(), -1, -1);
            });
            timeout = tickDelay();
            return;
        }

        Widgets.search().withText(config.Teletab().getWidgetStringName()).first().ifPresent(x -> {
            overlay.infoStatus = "Selecting Teletab";
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, x.getId(), -1, -1);
        });
        timeout = tickDelay();
    }

    private void handleFindLectern() {
        TileObjects.search().nameContains("Lectern").nearestToPlayer().ifPresent(x -> {
         TileObjectInteraction.interact(x, "Study");
        });
    }

    private boolean isInHome() {
        return TileObjects.search().withName("Lectern").first().isPresent();
    }

    private boolean isInPoh() { //thanx whoever this was from
        Set<Integer> POH_REGIONS = Set.of(7257, 7513, 7514, 7769, 7770, 8025, 8026);
        for (int elem : client.getMapRegions()) {
            if (POH_REGIONS.contains(elem)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTeleTabWidgetOpen() {
        return Widgets.search().withText("Spell Tablets").first().isPresent();
    }

    private boolean isConfirmButtonWidgetPresent() {
        return Widgets.search().withText("Create").first().isPresent();
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
            sendGameMessage("Auto Tele Tabs disabled.");
        } else {
            sendGameMessage("Auto Tele Tabs enabled.");
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!enablePlugin) {
            return;
        }

        ChatMessageType chatMessageType = event.getType();

        if (chatMessageType != ChatMessageType.GAMEMESSAGE && chatMessageType != ChatMessageType.SPAM) {
            return;
        }
    }

    //TODO Move all of this back into API
    private int tickDelay() {
        int tickLength = (int) randomDelay(config.tickDelayWeightedDistribution(), config.tickDelayMin(), config.tickDelayMax(), config.tickDelayDeviation(), config.tickDelayTarget());
        log.debug("tick delay for {} ticks", tickLength);
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

    public void openNearestBank() {
        Optional<TileObject> bank = TileObjects.search().withName("Bank chest").nearestToPlayer();

        //Opens bank
        if (!isBankOpen()) {
            if (bank.isPresent()) {
                overlay.infoStatus = "Banking";
                TileObjectInteraction.interact(bank.get(), "Use");
            } else {
                overlay.infoStatus = "Bank not found";
            }
        }
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

    public void handleRun(int minEnergy, int randMax) {
        if (nextRunEnergy < minEnergy || nextRunEnergy > minEnergy + randMax) {
            nextRunEnergy = getRandomIntBetweenRange(minEnergy, minEnergy + getRandomIntBetweenRange(0, randMax));
        }
        if ((client.getEnergy() / 100) > nextRunEnergy ||
                client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) != 0) {
            if (!isRunEnabled()) {
                nextRunEnergy = 0;
                Widget runOrb = client.getWidget(WidgetInfo.MINIMAP_RUN_ORB);

                if (runOrb != null) {
                    enableRun();
                }
            }
        }
    }

    public boolean isRunEnabled() {
        return client.getVarpValue(173) == 1;
    }


    public void enableRun() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
    }

    public boolean isBankOpen() {
        return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null);
    }

    public boolean isBankPinOpen() {
        return (client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null);
    }

}
