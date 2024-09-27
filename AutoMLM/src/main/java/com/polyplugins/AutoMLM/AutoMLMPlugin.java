package com.polyplugins.AutoMLM;

import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import com.polyplugins.AutoMLM.util.Utils;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependencies;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;

@PluginDependencies({@PluginDependency(EthanApiPlugin.class), @PluginDependency(PacketUtilsPlugin.class)})
@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[GS]</font> Auto MLM</html>",
        enabledByDefault = false,
        description = "Mina un carajo en la motherlode mina para ti..",
        tags = {"ElGuason"}
)
public class AutoMLMPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AutoMLMPlugin.class);
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    private ReflectBreakHandler breakHandler;
    @Inject
    Client client;
    @Inject
    AutoMLMConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoMLMOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    public Utils util;
    AutoMLMState state;
    int timeout = 0;
    int pouches = 0;
    int idleCount = 0;
    boolean depositOres = false;
    UISettings uiSetting;
    WorldPoint MinePoint;
    private final HotkeyListener pluginToggle = new HotkeyListener(() -> {
        return this.config.toggle();
    }) {
        public void hotkeyPressed() {
            AutoMLMPlugin.this.togglePlugin();
        }
    };

    public AutoMLMPlugin() {
    }

    @Provides
    AutoMLMConfiguration provideConfig(ConfigManager configManager) {
        return (AutoMLMConfiguration)configManager.getConfig(AutoMLMConfiguration.class);
    }

    protected void startUp() {
        this.breakHandler.registerPlugin(this);
        this.timeout = 0;
        this.pouches = 0;
        this.idleCount = 0;
        this.enablePlugin = false;
        this.depositOres = false;
        this.botTimer = Instant.now();
        this.state = null;
        this.uiSetting = this.config.UISettings();
        this.keyManager.registerKeyListener(this.pluginToggle);
        this.overlayManager.add(this.overlay);
    }

    protected void shutDown() {
        this.keyManager.unregisterKeyListener(this.pluginToggle);
        this.resetVals();
    }

    private void resetVals() {
        this.state = AutoMLMState.TIMEOUT;
        this.timeout = 10;
        this.pouches = 0;
        this.idleCount = 0;
        this.enablePlugin = false;
        this.depositOres = false;
        this.breakHandler.unregisterPlugin(this);
    }

    public AutoMLMState getState() {
        Player player = this.client.getLocalPlayer();
        if (player == null) {
            return AutoMLMState.UNHANDLED_STATE;
        } else if (this.breakHandler.shouldBreak(this)) {
            return AutoMLMState.HANDLE_BREAK;
        } else if (this.timeout > 0) {
            return AutoMLMState.TIMEOUT;
        } else if (EthanApiPlugin.isMoving()) {
            return AutoMLMState.MOVING;
        } else if (this.isBankPinOpen()) {
            this.overlay.infoStatus = "Bank Pin";
            return AutoMLMState.BANK_PIN;
        } else if (this.client.getLocalPlayer().getAnimation() == 6752) {
            this.overlay.infoStatus = "Mining";
            return AutoMLMState.ANIMATING;
        } else if (this.idleCount < 3) {
            return AutoMLMState.IDLE;
        } else {
            if (this.needsToDepositOres()) {
                this.depositOres = true;
            }

            if (this.depositOres) {
                return AutoMLMState.DEPOSIT_BANK;
            } else if (!Inventory.full()) {
                return AutoMLMState.MINE;
            } else {
                return Inventory.full() ? AutoMLMState.DEPOSIT_HOPPER : AutoMLMState.UNHANDLED_STATE;
            }
        }
    }

    @Subscribe
    private void onGameTick(GameTick tick) {
        if (this.enablePlugin && !this.breakHandler.isBreakActive(this)) {
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                this.uiSetting = this.config.UISettings();
                this.idleCount = this.client.getLocalPlayer().getAnimation() == -1 ? this.idleCount + 1 : 0;
                this.MinePoint = this.config.MineArea().getMinePoint();
                this.state = this.getState();
                switch (this.state) {
                    case TIMEOUT:
                        --this.timeout;
                        break;
                    case FIND_BANK:
                        this.openNearestBank();
                        break;
                    case MINE:
                        this.handleGem();
                        this.handleMineOre();
                        break;
                    case DEPOSIT_HOPPER:
                        this.handleHopper();
                        break;
                    case DEPOSIT_BANK:
                        this.handleDepositOres();
                        break;
                    case HANDLE_BREAK:
                        this.breakHandler.startBreak(this);
                        this.timeout = 10;
                        break;
                    case UNHANDLED_STATE:
                        this.overlay.infoStatus = "Ded";
                        break;
                    case MOVING:
                    case ANIMATING:
                    case BANK_PIN:
                    case IDLE:
                        this.timeout = this.util.tickDelay();
                }

            }
        }
    }

    public void handleDepositOres() {
        if (this.isOnUpperFloor()) {
            this.handleLadder();
        } else {
            if (this.isBankOpen()) {
                this.overlay.infoStatus = "Banking ores";
                BankInventory.search().result().stream().filter((x) -> {
                    return x.getItemId() != 2347 && !x.getName().contains("pickaxe") && x.getItemId() != 12011;
                }).forEach((x) -> {
                    BankInventoryInteraction.useItem(x, new String[]{"Deposit-All"});
                });
                if (this.client.getVarbitValue(5558) <= 0) {
                    this.depositOres = false;
                    return;
                }

                this.handleSack();
            }

            if (Inventory.getItemAmount(12011) > 1) {
                this.handleHopper();
            } else if (Inventory.search().filter((x) -> {
                return x.getName().contains("ore") || x.getName().contains("Coal");
            }).first().isPresent()) {
                this.openNearestBank();
            } else {
                if (!this.isBankOpen() && Inventory.search().filter((x) -> {
                    return x.getName().contains("ore") || x.getName().contains("Coal");
                }).first().isEmpty() && this.client.getVarbitValue(5558) > 0) {
                    this.handleSack();
                }

            }
        }
    }

    public void handleSack() {
        Optional<TileObject> sack = TileObjects.search().withName("Sack").withAction("Search").nearestToPlayer();
        if (sack.isPresent() && !Inventory.full()) {
            this.overlay.infoStatus = "Searching sack";
            TileObjectInteraction.interact((TileObject)sack.get(), new String[]{"Search"});
        }

    }

    public void handleGem() {
        this.overlay.infoStatus = "Dropping gem";
        Inventory.search().filter((gem) -> {
            return gem.getName().contains("Uncut");
        }).first().ifPresent((x) -> {
            InventoryInteraction.useItem(x, new String[]{"Drop"});
        });
    }

    public void handleMineOre() {
        this.overlay.infoStatus = "Mine ore";
        if (this.config.useSpec() && this.hasSpec()) {
            this.useSpec();
        }

        if ((this.config.MineArea() != MineArea.ARRIBA_1 && this.config.MineArea() != MineArea.ARRIBA_2 || this.isOnUpperFloor()) && (this.config.MineArea() != MineArea.ABAJO_1 && this.config.MineArea() != MineArea.ABAJO_2 || !this.isOnUpperFloor())) {
            TileObjects.search().withName("Ore vein").withAction("Mine").nearestToPoint(this.MinePoint).ifPresent((x) -> {
                TileObjectInteraction.interact(x, new String[]{"Mine"});
            });
        } else {
            this.handleLadder();
        }
    }

    private void useSpec() {
        if (!Equipment.search().matchesWildCardNoCase("*Dragon pickaxe*").empty() || !Equipment.search().matchesWildCardNoCase("*infernal pickaxe*").empty()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 38862885, -1, -1);
        }

    }

    private boolean hasSpec() {
        return this.client.getVarpValue(300) == 1000;
    }

    public void handleHopper() {
        if (this.isOnUpperFloor()) {
            this.handleLadder();
        } else {
            Optional<TileObject> brokenWheel = TileObjects.search().withAction("Hammer").nearestToPlayer();
            if (brokenWheel.isPresent() && this.config.fixWheels() && Inventory.search().withId(2347).first().isPresent()) {
                this.overlay.infoStatus = "Fixing wheel";
                TileObjectInteraction.interact((TileObject)brokenWheel.get(), new String[]{"Hammer"});
            } else {
                this.overlay.infoStatus = "Deposit hopper";
                TileObjects.search().withName("Hopper").withAction("Deposit").nearestToPlayer().ifPresent((x) -> {
                    TileObjectInteraction.interact(x, new String[]{"Deposit"});
                });
            }
        }
    }

    public void handleLadder() {
        this.overlay.infoStatus = "Climb ladder";
        TileObjects.search().withName("Ladder").withAction("Climb").nearestToPlayer().ifPresent((x) -> {
            TileObjectInteraction.interact(x, new String[]{"Climb"});
        });
    }

    public boolean needsToDepositOres() {
        return this.config.Sack().getSize() - (this.client.getVarbitValue(5558) + Inventory.getItemAmount(12011)) <= 0 || Inventory.search().filter((x) -> {
            return x.getName().contains("ore") || x.getName().contains("Coal");
        }).first().isPresent();
    }

    public boolean isOnUpperFloor() {
        return this.client.getVarbitValue(2086) == 1;
    }

    public void togglePlugin() {
        this.enablePlugin = !this.enablePlugin;
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            if (!this.enablePlugin) {
                this.clientThread.invokeLater(() -> {
                    EthanApiPlugin.sendClientMessage("Auto MLM disabled.");
                });
                this.breakHandler.stopPlugin(this);
                this.resetVals();
            } else {
                this.clientThread.invokeLater(() -> {
                    EthanApiPlugin.sendClientMessage("Auto MLM enabled.");
                });
                this.botTimer = Instant.now();
                this.uiSetting = this.config.UISettings();
                this.keyManager.registerKeyListener(this.pluginToggle);
                this.breakHandler.registerPlugin(this);
                this.breakHandler.startPlugin(this);
            }

        }
    }

    public boolean isBankOpen() {
        return this.client.getWidget(WidgetInfo.BANK_CONTAINER) != null;
    }

    public boolean isBankPinOpen() {
        return this.client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null;
    }

    public void openNearestBank() {
        if (!this.isBankOpen()) {
            TileObjects.search().withName("Bank chest").nearestToPlayer().ifPresentOrElse((x) -> {
                this.overlay.infoStatus = "Banking";
                TileObjectInteraction.interact(x, new String[]{"Use"});
            }, () -> {
                this.overlay.infoStatus = "Bank not found";
            });
        }

        this.timeout = this.util.tickDelay();
    }
}

