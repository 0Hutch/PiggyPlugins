package com.ozplugins.AutoMiner;

import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.ozplugins.AutoMiner.Constants.AutoMinerState;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
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
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.ozplugins.AutoMiner.Constants.AutoMinerState.*;

@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto Miner</font></html>",
        enabledByDefault = false,
        description = "Miner plugin..",
        tags = {"Oz"}
)
@Slf4j
public class AutoMinerPlugin extends Plugin {
    protected static final Random random = new Random();
    Instant botTimer;
    boolean enablePlugin;

    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    AutoMinerConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private AutoMinerOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    AutoMinerState state;
    int timeout = 0;
    boolean needsToDrop;

    @Provides
    AutoMinerConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoMinerConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        enablePlugin = false;
        botTimer = Instant.now();
        state = null;
        needsToDrop = false;
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
        needsToDrop = false;
        keyManager.unregisterKeyListener(pluginToggle);
        botTimer = null;
    }

    public AutoMinerState getState() {
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

        switch (config.Mode()) {
            case MINING_GUILD:
            case AMETHYST:
                if (isBankOpen() && Inventory.full()) {
                    return HANDLE_BANK;
                }
                if (!Inventory.full() || (isBankOpen() && BankInventory.search().empty())) {
                    return MINE;
                }
                if (Inventory.full()) {
                    return FIND_BANK;
                }
                break;

            case POWERMINE:
                if (!Inventory.full()) {
                    return MINE;
                }
                if (Inventory.full()) {
                    return DROP_INVENTORY;
                }
                break;
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
        if (!needsToDrop) {
            needsToDrop = needsToDrop();
        }

        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case HANDLE_BANK:
                depositInventory();
                break;

            case DROP_INVENTORY:
                handleDropInventory();
                break;

            case MINE:
                switch (config.Mode()) {
                    case MINING_GUILD:
                        handleMiningGuild();
                        break;

                    case POWERMINE:
                        handlePowermine();
                        break;

                    case AMETHYST:
                        handleAmethyst();
                        break;
                }
                break;

            case FIND_BANK:
                openNearestBank();
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "ded";
                break;

            case MOVING:
            case ANIMATING:
            case BANK_PIN:
            case IDLE:
                timeout = tickDelay();
                break;
        }
    }

    private boolean needsToDrop() {
        return Inventory.full();
    }

    private void handlePowermine() {
        if (needsToDrop) {
            handleDropInventory();
            return;
        }

        TileObjects.search().withAction("Mine").withName(config.powermineRock() + " rocks").nearestByPath().ifPresentOrElse(x -> {
            overlay.infoStatus = "Mining " + config.powermineRock();
            TileObjectInteraction.interact(x, "Mine");
        }, () -> {
            overlay.infoStatus = "Waiting for " + config.powermineRock();
        });
        timeout = tickDelay();
    }

    private void handleAmethyst() {
        TileObjects.search().withAction("Mine").withName("Amethyst crystals").nearestToPoint(config.amethystSpot().getMinePoint()).ifPresentOrElse(x -> {
            overlay.infoStatus = "Mining Amethyst";
            TileObjectInteraction.interact(x, "Mine");
        }, () -> {
            sendGameMessage("Unable to find amethyst, shutting down.");
            enablePlugin = false;
        });
        timeout = tickDelay();
    }

    private void handleDropInventory() {
        overlay.infoStatus = "Dropping inventory";
        List<Widget> itemsToDrop = Inventory.search().result().stream()
                .filter(x -> !x.getName().toLowerCase().contains("pickaxe"))
                .collect(Collectors.toList());

        if (itemsToDrop.isEmpty()) {
            needsToDrop = false;
            return;
        }

        int numItemsToDrop = Math.min(itemsToDrop.size(), getRandomIntBetweenRange(config.minDropPerTick(), Math.min(config.maxDropPerTick(), itemsToDrop.size())));
        itemsToDrop.stream().limit(numItemsToDrop).forEach(x -> InventoryInteraction.useItem(x, "Drop"));
    }

    private void handleMiningGuild() {
        Optional<TileObject> rock = TileObjects.search().withName(config.guildRock().getRockName() + " rocks").withAction("Mine").nearestToPoint(config.guildRock().getMinePoint());
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (config.guildRock().getRockName() == "Iron" && playerLocation.distanceTo(config.guildRock().getMinePoint()) != 0) {
            overlay.infoStatus = "Moving to mine spot";
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(config.guildRock().getMinePoint());
            return;
        }

        if (rock.isPresent()) {
            overlay.infoStatus = "Mining " + config.guildRock().getRockName();
            TileObjectInteraction.interact(rock.get(), "Mine");
        } else {
            overlay.infoStatus = "Waiting for " + config.guildRock().getRockName();
            timeout = tickDelay();
        }
    }

    private void depositInventory() {
        overlay.infoStatus = "Depositing inventory";
        BankInventory.search().result().stream()
                .filter(x -> !x.getName().contains("pickaxe")).forEach(x -> {
                    BankInventoryInteraction.useItem(x, "Deposit-All");
                });
    }

    /*private void depositAll() {
        overlay.infoStatus = "Depositing inventory";
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 786474, -1, -1);
    }*/


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
            sendGameMessage("Auto Miner disabled.");
        } else {
            sendGameMessage("Auto Miner enabled.");
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
        timeout = tickDelay();
    }

    public int getRandomIntBetweenRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
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

    public boolean isBankOpen() {
        return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null);
    }

    public boolean isBankPinOpen() {
        return (client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null);
    }

}
