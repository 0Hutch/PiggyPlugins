package com.polyplugins.AutoHerblore;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[GS]</font> Auto Herblore</html>",
        description = "Automated herblore plugin"
)
public class AutoHerblorePlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AutoHerblorePlugin.class);
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoHerbloreOverlay overlay;
    @Inject
    private AutoHerbloreConfig config;
    boolean running = false;
    int tickDelay = 0;
    int lastCreated = 0;
    private Widget potionItem = null;

    public AutoHerblorePlugin() {
    }

    @Provides
    private AutoHerbloreConfig getConfig(ConfigManager configManager) {
        return (AutoHerbloreConfig)configManager.getConfig(AutoHerbloreConfig.class);
    }

    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.running = this.client.getGameState() == GameState.LOGGED_IN;
    }

    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.running = false;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (this.running && this.tickDelay <= 0) {
            if (this.lastCreated > 0) {
                --this.lastCreated;
            }

            if (this.client.getLocalPlayer().getAnimation() == 363 && !Inventory.full()) {
                this.lastCreated = 3;
            }

            Inventory.search().nameContains(this.config.BASE_POTION()).first().ifPresent((potion) -> {
                this.potionItem = potion;
            });
            if (!Inventory.search().nameContains(this.config.BASE_POTION()).empty()) {
                if (this.lastCreated == 0) {
                    Widgets.search().withAction("Make").first().ifPresentOrElse((w) -> {
                        int remainingUnfPots = Inventory.getItemAmount(this.config.BASE_POTION());
                        WidgetPackets.queueResumePause(w.getId(), remainingUnfPots);
                        this.lastCreated = 3;
                    }, () -> {
                        Inventory.search().nameContains(this.config.BASE_POTION()).first().ifPresent((potion) -> {
                            Inventory.search().nameContains(this.config.SECONDARY()).first().ifPresent((secondary) -> {
                                MousePackets.queueClickPacket();
                                MousePackets.queueClickPacket();
                                WidgetPackets.queueWidgetOnWidget(potion, secondary);
                            });
                        });
                    });
                }
            } else if (!Bank.isOpen()) {
                NPCs.search().nameContains("Banker").nearestToPlayer().ifPresent((banker) -> {
                    NPCInteraction.interact(banker, new String[]{"Bank"});
                });
            }

            this.bank();
        } else {
            --this.tickDelay;
        }
    }

    private void bank() {
        if (Bank.isOpen()) {
            Widgets.search().filter((widget) -> {
                return widget.getParentId() != 786474;
            }).withAction("Deposit inventory").first().ifPresent((button) -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(button, new String[]{"Deposit inventory"});
            });
            this.withdraw(this.config.BASE_POTION(), 14);
            this.withdraw(this.config.SECONDARY(), 14);
            this.sendKey(27);
            this.lastCreated = 0;
        }

    }

    private void withdraw(String name, int amount) {
        Bank.search().withName(name).first().ifPresent((item) -> {
            BankInteraction.withdrawX(item, amount);
        });
    }

    private void sendKey(int key) {
        this.keyEvent(401, key);
        this.keyEvent(402, key);
    }

    private void keyEvent(int id, int key) {
        KeyEvent e = new KeyEvent(this.client.getCanvas(), id, System.currentTimeMillis(), 0, key, '\uffff');
        this.client.getCanvas().dispatchEvent(e);
    }
}
