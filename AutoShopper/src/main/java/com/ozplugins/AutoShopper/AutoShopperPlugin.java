package com.ozplugins.AutoShopper;

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
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.ozplugins.AutoShopper.util.ShopUtils;
import com.ozplugins.AutoShopper.util.Utils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static com.ozplugins.AutoShopper.AutoShopperState.*;


@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto Shopper</font></html>",
        enabledByDefault = false,
        description = "Buys shit from shops for you",
        tags = {"oz"}
)
@Slf4j
public class AutoShopperPlugin extends Plugin {
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    Client client;
    @Inject
    AutoShopperConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    public AutoShopperOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ClientThread clientThread;

    @Inject
    private WorldService worldService;
    @Inject
    public Utils util;

    @Inject
    public ShopUtils shopUtils;


    AutoShopperState state;
    public int timeout = 0;
    private static final int MAX_PLAYER_COUNT = 1950;

    public LocalPoint playerLocalPoint;

    boolean needsToHopWorld = false;

    @Provides
    AutoShopperConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoShopperConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        enablePlugin = false;
        needsToHopWorld = false;
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
        needsToHopWorld = false;
    }

    public AutoShopperState getState() {
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
        if (needsToHopWorld) {
            return HANDLE_HOP_WORLD;
        }

        if (Inventory.full() && config.EnableBanking()) {
            return HANDLE_BANK;
        }

        if (shopUtils.isShopOpen()) {
            return HANDLE_SHOP;
        }

        if (!shopUtils.isShopOpen()) {
            return FIND_SHOP;
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

        state = getState();
        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case HANDLE_BANK:
                handleBank();
                break;

            case FIND_SHOP:
                handleFindShop();
                break;

            case HANDLE_SHOP:
                handleShop();
                break;

            case HANDLE_HOP_WORLD:
                hopToNextWorld();
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

    private void handleShop() {
        if (!shopUtils.shopContainsItem(config.ItemName())) {
            overlay.infoStatus = "Item not found";
            timeout = util.tickDelay();
            return;
        }

        if (shopUtils.getShopItemAmount(config.ItemName()) > config.ItemMaximumAmount()) {
            needsToHopWorld = true;
            return;
        }

        if (shopUtils.getShopItemAmount(config.ItemName()) >= config.ItemMinimumAmount()) {
            overlay.infoStatus = "Buying: " + config.ItemName();
            shopUtils.buyXAmountFromShop(config.ItemName(), config.AmountToBuy());
            return;
        }

        //If shop has too low of the item amount you wanna buy, hop world and try again
        needsToHopWorld = true;
    }

    private void handleFindShop() {
        NPCs.search().walkable().withName(config.NPCName()).nearestToPlayer().ifPresentOrElse(
                npc -> {
                    overlay.infoStatus = "Trading: " + config.NPCName();
                    NPCInteraction.interact(npc, "Trade");
                }, () -> TileObjects.search().withName("Chest").withAction("Buy-food").nearestToPlayer().ifPresentOrElse(
                        chest -> {
                            overlay.infoStatus = "Using shop chest";
                            TileObjectInteraction.interact(chest, "Buy-food");
                        }, () -> {
                            if (client.getLocalPlayer().getWorldLocation().getRegionID() == 12597 || client.getLocalPlayer().getWorldLocation().getRegionID() == 12853) {
                                overlay.infoStatus = "Moving to NPC";
                                Optional<TileObject> closed_door = TileObjects.search().withAction("Open").atLocation(3205, 3432, 0).first();
                                if (closed_door.isPresent()) {
                                    TileObjectInteraction.interact(closed_door.get(), "Open");
                                }
                                if (closed_door.isEmpty()) {
                                    MousePackets.queueClickPacket();
                                    MovementPackets.queueMovement(3204, 3432, false);
                                    timeout = 10;
                                }
                            }
                        }));
    }

    private void handleBank() {
        if (isBankOpen()) {
            overlay.infoStatus = "Banking items";
            //TODO simplify this with a list of item ids and add deposit box support
            BankInventory.search().result().stream().filter(x -> !x.getName().contains("Coins"))
                    .forEach(x -> BankInventoryInteraction.useItem(x, "Deposit-All"));
            return;
        }
        openNearestBank();
    }

    public boolean isBankOpen() {
        return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null);
    }

    public boolean isBankPinOpen() {
        return (client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null);
    }

    public void openNearestBank() {
        if (!isBankOpen()) {
            Optional<TileObject> closed_door = TileObjects.search().withAction("Open").atLocation(3205, 3432, 0).first();
            if (client.getLocalPlayer().getWorldLocation().getRegionID() == 12853 && closed_door.isPresent() && this.client.getPlayers().stream().noneMatch(x -> x.getWorldLocation().isInArea(new WorldArea(new WorldPoint(3203, 3434, 0), 4, 4)))) {
                TileObjectInteraction.interact(closed_door.get(), "Open");
                timeout = 3;
            } else {
                TileObjects.search().withName("Bank booth").withAction("Bank").nearestToPlayer().ifPresentOrElse(x -> {
                    overlay.infoStatus = "Banking";
                    TileObjectInteraction.interact(x, "Bank");
                }, () -> TileObjects.search().withName("Chest").withAction("Bank").nearestToPlayer().ifPresentOrElse(x -> {
                    overlay.infoStatus = "Banking with chest";
                    TileObjectInteraction.interact(x, "Bank");
                }, () -> TileObjects.search().withName("Bank chest").withAction("Use").nearestToPlayer().ifPresentOrElse(x -> {
                    overlay.infoStatus = "Banking";
                    TileObjectInteraction.interact(x, "Use");
                }, () -> {
                    overlay.infoStatus = "Bank not found";
                })));
                timeout = util.tickDelay();
            }
        }
    }


    private void hopToNextWorld() {
        overlay.infoStatus = "Hopping world";
        if (shopUtils.isShopOpen()) {
            shopUtils.closeShop();
            return;
        }
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult == null) {
            return;
        }

        List<World> worlds = worldResult.getWorlds();
        if (worlds.isEmpty()) {
            return;
        }
        World currentWorld = worldResult.findWorld(client.getWorld());
        int worldIdx = worlds.indexOf(currentWorld);
        int totalLevel = client.getTotalLevel();
        EnumSet<WorldType> currentWorldTypes = currentWorld.getTypes().clone();

        World world;
        do {
            worldIdx++;
            if (worldIdx >= worlds.size()) {
                worldIdx = 0;
            }
            world = worlds.get(worldIdx);

            EnumSet<WorldType> types = world.getTypes().clone();

            types.remove(net.runelite.api.WorldType.BOUNTY);
            types.remove(net.runelite.api.WorldType.LAST_MAN_STANDING);
            if (types.contains(net.runelite.api.WorldType.SKILL_TOTAL)) {
                try {
                    int totalRequirement = Integer.parseInt(world.getActivity().substring(0, world.getActivity().indexOf(" ")));

                    if (totalLevel >= totalRequirement) {
                        types.remove(net.runelite.api.WorldType.SKILL_TOTAL);
                    }
                } catch (NumberFormatException ex) {
                    log.warn("Failed to parse total level requirement for target world", ex);
                }
            }

            if (world.getPlayers() >= MAX_PLAYER_COUNT) {
                continue;
            }

            if (world.getPlayers() < 0) {
                continue;
            }


            if (currentWorldTypes.equals(types)) {
                break;
            }
        }
        while (world != currentWorld);

        if (world != currentWorld) {
            overlay.infoStatus = "Hopping to world: " + world.getId();
            //timeout = util.tickDelay();
            hop(world);
        }
    }

    private void hop(World world) {
        final net.runelite.api.World rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (client.getGameState() == GameState.LOGIN_SCREEN) {
            client.changeWorld(rsWorld);
            return;
        }

        log.info("Hopping to random world: {}", rsWorld);
        net.runelite.api.World quickHopTargetWorld = rsWorld;
        client.openWorldHopper();
        client.hopToWorld(quickHopTargetWorld);
        needsToHopWorld = false;
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
                EthanApiPlugin.sendClientMessage("Auto Shopper disabled.");
            });
            resetVals();
        } else {
            clientThread.invokeLater(() -> {
                EthanApiPlugin.sendClientMessage("Auto Shopper enabled.");
            });
            botTimer = Instant.now();
            keyManager.registerKeyListener(pluginToggle);
        }
    }

    private void addMessage(String message) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
    }
}
