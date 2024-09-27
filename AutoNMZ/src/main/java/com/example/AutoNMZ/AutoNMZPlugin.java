package com.example.AutoNMZ;

import com.example.AutoNMZ.util.Utils;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InteractionHelper;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
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
import net.runelite.client.plugins.*;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[GS]</font> Auto NMZ</html>",
        enabledByDefault = false,
        description = "Hace nmz por ti..",
        tags = {"ElGuason"}
)
@PluginDependencies({@PluginDependency(EthanApiPlugin.class), @PluginDependency(PacketUtilsPlugin.class)})
public class AutoNMZPlugin extends Plugin {
    protected static final Random random = new Random();
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    AutoNMZConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private AutoNMZOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    public Utils util;
    AutoNMZState state;
    int timeout = 0;
    boolean forceTab = false;
    UISettings uiSetting;
    int absorptionPoints = 0;
    int overloadRefreshRemaining = 0;
    int overloadCakeTimeout = 16;
    int hitpoints = 0;
    int overloadSipsInReserve = 0;
    int absorptionSipsInReserve = 0;
    int specialAttack = 0;
    boolean specialAttackEnabled = false;
    boolean powerSurgeEffect = false;
    boolean hasOverload = true;
    int POWER_SURGE = 26264;
    private final HotkeyListener pluginToggle = new HotkeyListener(() -> {
        return this.config.toggle();
    }) {
        public void hotkeyPressed() {
            AutoNMZPlugin.this.togglePlugin();
        }
    };

    public AutoNMZPlugin() {
    }

    @Provides
    AutoNMZConfiguration provideConfig(ConfigManager configManager) {
        return (AutoNMZConfiguration)configManager.getConfig(AutoNMZConfiguration.class);
    }

    protected void startUp() {
        this.timeout = 0;
        this.absorptionPoints = 0;
        this.overloadRefreshRemaining = 0;
        this.hitpoints = 0;
        this.overloadCakeTimeout = 16;
        this.specialAttack = 0;
        this.overloadSipsInReserve = 0;
        this.absorptionSipsInReserve = 0;
        this.specialAttackEnabled = false;
        this.powerSurgeEffect = false;
        this.hasOverload = true;
        this.enablePlugin = false;
        this.botTimer = Instant.now();
        this.state = null;
        this.uiSetting = this.config.UISettings();
        this.keyManager.registerKeyListener(this.pluginToggle);
        this.overlayManager.add(this.overlay);
    }

    protected void shutDown() {
        this.resetVals();
    }

    private void resetVals() {
        this.overlayManager.remove(this.overlay);
        this.state = null;
        this.timeout = 0;
        this.absorptionPoints = 0;
        this.overloadRefreshRemaining = 0;
        this.specialAttack = 0;
        this.overloadSipsInReserve = 0;
        this.absorptionSipsInReserve = 0;
        this.specialAttackEnabled = false;
        this.overloadCakeTimeout = 16;
        this.powerSurgeEffect = false;
        this.hasOverload = true;
        this.hitpoints = 0;
        this.enablePlugin = false;
        this.keyManager.unregisterKeyListener(this.pluginToggle);
        this.uiSetting = null;
        this.botTimer = null;
    }

    public AutoNMZState getState() {
        Player player = this.client.getLocalPlayer();
        if (player == null) {
            return AutoNMZState.UNHANDLED_STATE;
        } else {
            if (this.overloadCakeTimeout > 0) {
                --this.overloadCakeTimeout;
            }

            if (this.timeout > 0) {
                return AutoNMZState.TIMEOUT;
            } else if (this.isBankPinOpen()) {
                return AutoNMZState.BANK_PIN;
            } else if (this.inNMZ()) {
                return AutoNMZState.FIGHT;
            } else if (!this.inNMZ() && !this.config.AutomatePrep()) {
                this.overlay.infoStatus = "No estas en NMZ";
                return AutoNMZState.IDLE;
            } else {
                return AutoNMZState.HANDLE_PREP;
            }
        }
    }

    @Subscribe
    private void onGameTick(GameTick tick) {
        this.uiSetting = this.config.UISettings();
        if (this.enablePlugin) {
            if (this.client.getGameState() != GameState.LOGGED_IN) {
                this.forceTab = false;
            } else {
                this.hitpoints = this.client.getBoostedSkillLevel(Skill.HITPOINTS);
                this.specialAttack = this.client.getVarpValue(300) / 10;
                this.specialAttackEnabled = this.client.getVarpValue(301) == 1;
                this.state = this.getState();
                switch (this.state) {
                    case TIMEOUT:
                        --this.timeout;
                        break;
                    case FIGHT:
                        this.absorptionPoints = this.client.getVarbitValue(3956);
                        this.overloadRefreshRemaining = this.client.getVarbitValue(3955);
                        if (this.config.FlickPrayer()) {
                            this.handlePrayFlick();
                        } else if (EthanApiPlugin.isQuickPrayerEnabled()) {
                            this.overlay.infoStatus = "Desactivar Prayer";
                            InteractionHelper.togglePrayer();
                        }

                        if (this.absorptionPoints < this.config.AbsorptionLowAmount()) {
                            this.handleAbsorption();
                        }

                        if (this.overloadRefreshRemaining == 0 && this.hasOverload && this.hitpoints > 50) {
                            this.handleOverload();
                        } else if (this.config.RockCake() && this.hitpoints > 1) {
                            this.handleRockCake();
                        }

                        this.handleFight();
                        break;
                    case HANDLE_PREP:
                        this.handlePrep();
                        break;
                    case UNHANDLED_STATE:
                        this.overlay.infoStatus = "Ded";
                        break;
                    case BANK_PIN:
                    case MOVING:
                    case ANIMATING:
                    case IDLE:
                        this.timeout = this.util.tickDelay();
                }

            }
        }
    }

    public void handlePrep() {
        this.overloadSipsInReserve = this.client.getVarbitValue(3953);
        this.absorptionSipsInReserve = this.client.getVarbitValue(3954);
        if ((Inventory.search().matchesWildCardNoCase("*absorption*").result().size() < this.config.AbsorptionPotions() || Inventory.search().matchesWildCardNoCase("*overload*").result().size() < this.config.OverloadPotions()) && !Inventory.full()) {
            if (Inventory.search().matchesWildCardNoCase("*overload*").result().size() + this.overloadSipsInReserve / 4 < this.config.OverloadPotions()) {
                this.overlay.infoStatus = "Comprando Overloads";
                this.handleBuyOverloads();
            } else if (Inventory.search().matchesWildCardNoCase("*absorption*").result().size() + this.absorptionSipsInReserve / 4 < this.config.AbsorptionPotions()) {
                this.overlay.infoStatus = "Comprando Absorptions";
                this.handleBuyAbsorption();
            } else if (this.config.AbsorptionPotions() - Inventory.search().matchesWildCardNoCase("*absorption*").result().size() > 0) {
                this.overlay.infoStatus = "Retirándo absorption";
                if (Widgets.search().hiddenState(false).withTextContains("How many doses of absorption potion will you withdraw?").empty()) {
                    TileObjectInteraction.interact(26280, new String[]{"Take"});
                } else {
                    this.util.sendIntValue(this.config.AbsorptionPotions() * 4 - Inventory.search().matchesWildCardNoCase("*absorption*").result().size());
                }
            } else {
                if (this.config.OverloadPotions() - Inventory.search().matchesWildCardNoCase("*overload*").result().size() > 0) {
                    this.overlay.infoStatus = "Retirándo overdose";
                    if (Widgets.search().hiddenState(false).withTextContains("How many doses of overload potion will you withdraw?").empty()) {
                        TileObjectInteraction.interact(26279, new String[]{"Take"});
                        return;
                    }

                    this.util.sendIntValue(this.config.OverloadPotions() * 4 - Inventory.search().matchesWildCardNoCase("*overload*").result().size());
                }

            }
        } else {
            this.handleDream();
        }
    }

    public void handleBuyOverloads() {
        if (Widgets.search().hiddenState(false).withTextContains("Dom Onion's Reward Shop").empty() && !EthanApiPlugin.isMoving()) {
            TileObjectInteraction.interact(26273, new String[]{"Search"});
        } else {
            int amountToBuy = (this.config.OverloadPotions() - Inventory.search().matchesWildCardNoCase("*overload*").result().size() - this.absorptionSipsInReserve / 4) * 4;
            this.buyOverload(amountToBuy);
        }
    }

    public void handleBuyAbsorption() {
        if (Widgets.search().hiddenState(false).withTextContains("Dom Onion's Reward Shop").empty() && !EthanApiPlugin.isMoving()) {
            TileObjectInteraction.interact(26273, new String[]{"Search"});
        } else {
            int amountToBuy = (this.config.AbsorptionPotions() - Inventory.search().matchesWildCardNoCase("*absorption*").result().size() - this.absorptionSipsInReserve / 4) * 4;
            this.buyAbsorption(amountToBuy);
        }
    }

    public void buyOverload(int amount) {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(5, 13500422, 11733, 6);
        this.util.sendIntValue(amount);
    }

    public void buyAbsorption(int amount) {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(5, 13500422, 11733, 9);
        this.util.sendIntValue(amount);
    }

    public void handleDream() {
        if (this.isDreamReady()) {
            this.overlay.infoStatus = "Setting up dream";
            if (Widgets.search().hiddenState(false).withTextContains("Previous: Customisable Rumble (hard)").empty()) {
                NPCInteraction.interact("Dominic Onion", new String[]{"Dream"});
            } else {
                this.selectDream();
            }
        } else if (Widgets.search().hiddenState(false).withTextContains("Nazastarool").empty()) {
            this.overlay.infoStatus = "Entering dream";
            TileObjectInteraction.interact(26291, new String[]{"Drink"});
        } else {
            this.enterDream();
        }
    }

    public void enterDream() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueResumePause(8454150, -1);
    }

    public void selectDream() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueResumePause(14352385, 4);
        MousePackets.queueClickPacket();
        WidgetPackets.queueResumePause(15138821, -1);
        MousePackets.queueClickPacket();
        WidgetPackets.queueResumePause(14352385, 1);
    }

    public void handleFight() {
        if (this.config.PowerSurge()) {
            Optional<TileObject> power = TileObjects.search().withId(this.POWER_SURGE).nearestToPlayer();
            if (!this.powerSurgeEffect && power.isPresent()) {
                this.getPowerSurge();
                return;
            }

            if (this.powerSurgeEffect) {
                this.util.toggleGear(this.util.getGearNames(this.config.SpecWeapons()));
                if (!this.specialAttackEnabled && this.specialAttack > 20) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 38862885, -1, -1);
                }
            } else {
                this.util.toggleGear(this.util.getGearNames(this.config.MainWeapons()));
            }
        }

        if (!EthanApiPlugin.isMoving() && !this.client.getLocalPlayer().isInteracting()) {
            NPCs.search().nearestToPlayer().ifPresent((x) -> {
                this.overlay.infoStatus = "Attacking";
                NPCInteraction.interact(x, new String[]{"Attack"});
            });
        } else {
            this.overlay.infoStatus = "Fighting";
        }

    }

    public void getPowerSurge() {
        TileObjects.search().withId(this.POWER_SURGE).nearestToPlayer().ifPresent((x) -> {
            this.overlay.infoStatus = "Grabbing power surge";
            TileObjectInteraction.interact(x, new String[]{"Activate"});
        });
    }

    public void handleRockCake() {
        if (this.overloadCakeTimeout == 0 || !this.hasOverload) {
            Inventory.search().matchesWildCardNoCase("Dwarven*").first().ifPresent((x) -> {
                this.overlay.infoStatus = "Mordiendo el puto cake de piedra";
                InventoryInteraction.useItem(x, new String[]{"Guzzle"});
            });
        }

    }

    public void handleOverload() {
        Inventory.search().matchesWildCardNoCase("*Overload*").first().ifPresentOrElse((x) -> {
            this.overlay.infoStatus = "Tomando overload";
            this.hasOverload = true;
            this.overloadCakeTimeout = 16;
            InventoryInteraction.useItem(x, new String[]{"Drink"});
        }, () -> {
            this.hasOverload = false;
        });
    }

    public void handleAbsorption() {
        Inventory.search().matchesWildCardNoCase("*absorption*").first().ifPresent((x) -> {
            this.overlay.infoStatus = "Tomando absorption";
            InventoryInteraction.useItem(x, new String[]{"Drink"});
        });
    }

    public void handlePrayFlick() {
        if (this.forceTab) {
            this.client.runScript(new Object[]{915, 3});
            this.forceTab = false;
        }

        if (this.client.getWidget(5046276) == null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(this.client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB), new String[]{"Setup"});
            this.forceTab = true;
        }

        if (EthanApiPlugin.isQuickPrayerEnabled()) {
            InteractionHelper.togglePrayer();
        }

        InteractionHelper.togglePrayer();
    }

    public boolean inNMZ() {
        return Arrays.stream(this.client.getMapRegions()).anyMatch((x) -> {
            return x == 9033;
        });
    }

    public boolean isDreamReady() {
        return this.client.getVarbitValue(3946) == 0;
    }

    public boolean isBankPinOpen() {
        Widget bankPinWidget = this.client.getWidget(WidgetInfo.BANK_PIN_CONTAINER);
        return bankPinWidget != null;
    }

    public void togglePlugin() {
        this.enablePlugin = !this.enablePlugin;
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            if (!this.enablePlugin) {
                this.sendGameMessage("Auto NMZ apagado.");
            } else {
                this.sendGameMessage("Auto NMZ encendido.");
            }

        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (this.enablePlugin) {
            ChatMessageType chatMessageType = event.getType();
            if (chatMessageType == ChatMessageType.GAMEMESSAGE || chatMessageType == ChatMessageType.SPAM) {
                if (event.getMessage().endsWith(" special attack power!")) {
                    this.powerSurgeEffect = true;
                }

                if (event.getMessage().endsWith(" power has ended.")) {
                    this.powerSurgeEffect = false;
                }

            }
        }
    }

    public void sendGameMessage(String message) {
        String chatMessage = (new ChatMessageBuilder()).append(ChatColorType.HIGHLIGHT).append(message).build();
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(chatMessage).build());
    }
}
