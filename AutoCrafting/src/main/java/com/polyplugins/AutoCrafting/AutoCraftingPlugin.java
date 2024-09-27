package com.polyplugins.AutoCrafting;

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
        name = "<html><font color=\"#ff4d00\">[GS]</font> Auto Craft Leather</html>",
        description = "Automated crafting plugin"
)
public class AutoCraftingPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AutoCraftingPlugin.class);
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoCraftingOverlay overlay;
    @Inject
    private AutoCraftingConfig config;
    boolean running = false;
    int tickDelay = 0;
    int lastCrafted = 0;
    private Widget leatherItem = null;

    public AutoCraftingPlugin() {
    }

    @Provides
    private AutoCraftingConfig getConfig(ConfigManager configManager) {
        return (AutoCraftingConfig)configManager.getConfig(AutoCraftingConfig.class);
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
            if (this.lastCrafted > 0) {
                --this.lastCrafted;
            }

            if (this.client.getLocalPlayer().getAnimation() == 1249 && !Inventory.full()) {
                this.lastCrafted = 3;
            }

            Inventory.search().withId(this.config.LEATHER_TYPE().getLeatherType()).first().ifPresent((leather) -> {
                this.leatherItem = leather;
            });
            if (this.config.ARMOR_TYPE().getLeatherNeeded() <= Inventory.getItemAmount(this.config.LEATHER_TYPE().getLeatherType())) {
                if (Inventory.search().withId(this.config.LEATHER_TYPE().getLeatherType()).result().size() >= this.config.ARMOR_TYPE().getLeatherNeeded() && this.lastCrafted == 0) {
                    Widgets.search().withTextContains("Enter amount:").first().ifPresent((w) -> {
                        this.client.runScript(new Object[]{299, 1, 0, 0});
                    });
                    Widgets.search().withAction("Make").nameContains(this.config.ARMOR_TYPE().getArmorType()).first().ifPresentOrElse((w) -> {
                        int remainingLeather = Inventory.getItemAmount(this.config.LEATHER_TYPE().getLeatherType());
                        WidgetPackets.queueResumePause(w.getId(), remainingLeather);
                        this.lastCrafted = 3;
                    }, () -> {
                        Inventory.search().nameContains("Needle").first().ifPresent((needle) -> {
                            MousePackets.queueClickPacket();
                            MousePackets.queueClickPacket();
                            WidgetPackets.queueWidgetOnWidget(needle, this.leatherItem);
                        });
                    });
                }
            } else if (!Bank.isOpen()) {
                NPCs.search().withAction("Bank").nearestToPlayer().ifPresent((banker) -> {
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
            this.withdrawString("Needle", 1);
            this.withdrawString("Thread", 10);
            this.withdrawId(this.config.LEATHER_TYPE().getLeatherType(), 26);
            this.sendKey(27);
            this.lastCrafted = 0;
        }

    }

    private void withdrawId(int id, int amount) {
        Bank.search().withId(id).first().ifPresent((item) -> {
            BankInteraction.withdrawX(item, amount);
        });
    }

    private void withdrawString(String name, int amount) {
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
