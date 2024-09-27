package com.polyplugins.BobTheBuilder;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.BankUtil;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ObjectComposition;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import org.apache.commons.lang3.RandomUtils;

@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[GS]</font> Auto Construction</html>",
        description = "Sube tu construction",
        tags = {"construction", "El Guason", "skilling"}
)
public class BobTheBuilderPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    BobTheBuilderConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BobTheBuilderOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    State state;
    boolean started;
    private int timeout;
    WorldPoint startTile;
    WorldPoint targetTile;
    boolean takeBreak = false;
    public String debug = "";
    public boolean atPOH = false;
    boolean resetStartTile = true;
    private final HotkeyListener toggle = new HotkeyListener(() -> {
        return this.config.toggle();
    }) {
        public void hotkeyPressed() {
            BobTheBuilderPlugin.this.toggle();
        }
    };

    public BobTheBuilderPlugin() {
    }

    protected void startUp() throws Exception {
        this.keyManager.registerKeyListener(this.toggle);
        this.breakHandler.registerPlugin(this);
        this.overlayManager.add(this.overlay);
    }

    protected void shutDown() throws Exception {
        this.keyManager.unregisterKeyListener(this.toggle);
        this.breakHandler.unregisterPlugin(this);
        this.overlayManager.remove(this.overlay);
    }

    @Provides
    private BobTheBuilderConfig getConfig(ConfigManager configManager) {
        return (BobTheBuilderConfig)configManager.getConfig(BobTheBuilderConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (EthanApiPlugin.loggedIn() && this.started && !this.breakHandler.isBreakActive(this)) {
            TileObjects.search().nameContains(this.config.build()).nearestToPlayer().ifPresentOrElse((tileObject) -> {
                this.atPOH = true;
            }, () -> {
                TileObjects.search().nameContains(this.config.remove()).nearestToPlayer().ifPresentOrElse((tileObject) -> {
                    this.atPOH = true;
                }, () -> {
                    this.atPOH = false;
                });
            });
            this.debug = this.atPOH ? "At home" : "Not at home";
            this.state = this.getNextState();
            this.handleState();
        } else {
            this.startTile = null;
            this.resetStartTile = true;
        }
    }

    private void handleState() {
        switch (this.state) {
            case HANDLE_BREAK:
                this.takeBreak = false;
                this.breakHandler.startBreak(this);
                this.timeout = 10;
                break;
            case TIMEOUT:
                --this.timeout;
                break;
            case RESTOCK:
                this.Restock();
                break;
            case TELEPORT_TO_HOUSE:
                this.TeleportToHouse();
                this.setTimeout();
                break;
            case TELEPORT_TO_BANK:
                this.TeleportToVarrock();
                this.setTimeout();
                break;
            case BUILD:
                this.Build();
                break;
            case REMOVE:
                this.Remove();
        }

    }

    private State getNextState() {
        if (EthanApiPlugin.isMoving()) {
            return State.ANIMATING;
        } else {
            if (this.breakHandler.shouldBreak(this)) {
                this.takeBreak = true;
            }

            if (this.timeout > 0) {
                return State.TIMEOUT;
            } else if (this.hasItems()) {
                if (!this.atPOH) {
                    return State.TELEPORT_TO_HOUSE;
                } else {
                    TileObjectQuery tileObjectQuery = TileObjects.search().nameContains(this.config.build());
                    TileObject tileObject;
                    ObjectComposition objectComposition;
                    List actions;
                    if (tileObjectQuery.nearestToPlayer().isPresent()) {
                        tileObject = (TileObject)tileObjectQuery.nearestToPlayer().get();
                        objectComposition = TileObjectQuery.getObjectComposition(tileObject);
                        actions = Arrays.asList(objectComposition.getActions());
                        if (actions.contains("Build")) {
                            return State.BUILD;
                        }

                        this.debug = "Found " + this.config.build();
                    } else {
                        this.debug = "Can't find " + this.config.build();
                    }

                    tileObjectQuery = TileObjects.search().nameContains(this.config.remove());
                    if (tileObjectQuery.nearestToPlayer().isPresent()) {
                        tileObject = (TileObject)tileObjectQuery.nearestToPlayer().get();
                        objectComposition = TileObjectQuery.getObjectComposition(tileObject);
                        actions = Arrays.asList(objectComposition.getActions());
                        if (actions.contains("Remove")) {
                            return State.REMOVE;
                        }

                        this.debug = "Found " + this.config.remove();
                    } else {
                        this.debug = "Can't find " + this.config.remove();
                    }

                    return State.TIMEOUT;
                }
            } else {
                return this.atPOH ? State.TELEPORT_TO_BANK : State.RESTOCK;
            }
        }
    }

    private void Restock() {
        Optional bankBooth;
        if (Bank.isOpen()) {
            bankBooth = BankUtil.nameContainsNoCase(this.config.items()).first();
            bankBooth.ifPresent((widget) -> {
                BankInteraction.withdrawX((Widget) widget, 24);
            });
            this.setTimeout();
        } else {
            bankBooth = TileObjects.search().filter((tileObject) -> {
                ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(tileObject);
                return this.getName().toLowerCase().contains("bank") || Arrays.stream(objectComposition.getActions()).anyMatch((action) -> {
                    return action != null && action.toLowerCase().contains("bank");
                });
            }).nearestToPlayer();
            if (bankBooth.isPresent()) {
                MousePackets.queueClickPacket();
                TileObjectInteraction.interact((TileObject)bankBooth.get(), new String[]{"Bank"});
                return;
            }

            TileObjects.search().withName("Bank chest").nearestToPlayer().ifPresent((tileObject) -> {
                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(tileObject, new String[]{"Use"});
            });
        }

    }

    private void TeleportToHouse() {
        Optional<Widget> teleportSpellIcon = Widgets.search().withId(WidgetInfoExtended.SPELL_TELEPORT_TO_HOUSE.getPackedId()).first();
        if (teleportSpellIcon.isPresent()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction((Widget)teleportSpellIcon.get(), new String[]{"Cast"});
        }

    }

    private void TeleportToVarrock() {
        InventoryUtil.nameContainsNoCase("Crafting cape").first().ifPresentOrElse((item) -> {
            MousePackets.queueClickPacket();
            InventoryInteraction.useItem(item, new String[]{"Teleport"});
        }, () -> {
            TileObjects.search().nameContains("Amulet of Glory").nearestToPlayer().ifPresentOrElse((tileObject) -> {
                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(tileObject, new String[]{"Edgeville"});
            }, () -> {
                Optional<Widget> teleportSpellIcon = Widgets.search().withId(WidgetInfoExtended.SPELL_VARROCK_TELEPORT.getPackedId()).first();
                if (teleportSpellIcon.isPresent()) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetAction((Widget)teleportSpellIcon.get(), new String[]{"Cast"});
                }

            });
        });
    }

    private void Build() {
        this.debug = "build";
        Widgets.search().withTextContains("Furniture Creation Menu").hiddenState(false).first().ifPresentOrElse((menu) -> {
            this.debug = "Found menu";
            Widgets.search().withId(30015490).first().ifPresent((option) -> {
                this.debug = Integer.toString(option.getId());
                MousePackets.queueClickPacket();
                WidgetPackets.queueResumePause(option.getId(), this.config.option());
                this.setTimeout();
            });
        }, () -> {
            TileObjects.search().nameContains(this.config.build()).nearestToPlayer().ifPresent((tileObject) -> {
                ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(tileObject);
                List<String> actions = Arrays.asList(objectComposition.getActions());
                if (actions.contains("Build")) {
                    this.debug = "Open menu";
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject, new String[]{"Build"});
                }

            });
        });
    }

    private void Remove() {
        this.debug = "remove";
        Widgets.search().withTextContains("Really remove it").hiddenState(false).first().ifPresentOrElse((menu) -> {
            this.debug = "Found menu";
            Widgets.search().withTextContains("Yes").hiddenState(false).first().ifPresent((option) -> {
                this.debug = "Interact";
                MousePackets.queueClickPacket();
                WidgetPackets.queueResumePause(option.getId(), 1);
                this.setTimeout();
            });
        }, () -> {
            TileObjects.search().nameContains(this.config.remove()).nearestToPlayer().ifPresent((tileObject) -> {
                ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(tileObject);
                List<String> actions = Arrays.asList(objectComposition.getActions());
                if (actions.contains("Remove")) {
                    this.debug = "Open menu";
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject, new String[]{"Remove"});
                }

            });
        });
    }

    private boolean playerAtTarget() {
        return this.client.getLocalPlayer().getWorldLocation().getX() == this.targetTile.getX() && this.client.getLocalPlayer().getWorldLocation().getY() == this.targetTile.getY();
    }

    private boolean hasItems() {
        String[] items = this.config.items().split(",");
        boolean hasItems = true;

        for(int i = 0; i < items.length; ++i) {
            if (!InventoryUtil.hasItem(items[i])) {
                hasItems = false;
            }
        }

        return hasItems;
    }

    private void setTimeout() {
        this.timeout = RandomUtils.nextInt(this.config.tickdelayMin(), this.config.tickDelayMax());
    }

    public void toggle() {
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.started = !this.started;
            if (!this.started) {
                this.state = State.TIMEOUT;
                this.breakHandler.stopPlugin(this);
                this.resetStartTile = true;
                this.startTile = null;
            } else {
                this.breakHandler.startPlugin(this);
                this.startTile = this.client.getLocalPlayer().getWorldLocation();
            }

        }
    }
}
