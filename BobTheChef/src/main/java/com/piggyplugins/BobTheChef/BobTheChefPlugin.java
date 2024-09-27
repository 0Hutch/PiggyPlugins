package com.piggyplugins.BobTheChef;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.*;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.TileItemPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.BankUtil;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.crowdsourcing.dialogue.PlayerDialogueData;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import com.google.inject.Inject;
import org.apache.commons.lang3.RandomUtils;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Bob The Chef</html>",
        description = "Bob goes hunting",
        tags = {"ethan", "piggy", "skilling"}
)
public class BobTheChefPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    BobTheChefConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BobTheChefOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    State state;
    boolean started;
    private boolean menuOpen = false;
    private int timeout;
    public String debug = "";

    private static final int COOK_ACTION_WIDGET = 17694735;
    private static final int OVEN_ID = 21302;
    private static final int CHEST_ID = 21301;

    TileObject cookingRangeObject = null;
    TileObject bankObject = null;

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        breakHandler.registerPlugin(this);
        this.overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        breakHandler.unregisterPlugin(this);
        this.overlayManager.remove(overlay);
    }

    @Provides
    private BobTheChefConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BobTheChefConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || !started || breakHandler.isBreakActive(this)) {
            // We do an early return if the user isn't logged in
            cookingRangeObject = null;
            bankObject = null;
            return;
        }

        state = getNextState();
        handleState();
    }

    private void handleState() {
        switch (state) {
            case ANIMATING:
                timeout = 1;
                break;
            case HANDLE_BREAK:
                breakHandler.startBreak(this);
                timeout = 10;
                break;
            case TIMEOUT:
                timeout--;
                break;
            case COOK:
                cook();
                break;
            case BANK:
                bank();
                break;
        }
    }

    private State getNextState() {
        if (EthanApiPlugin.isMoving() || client.getLocalPlayer().getAnimation() != -1 && !config.oneTick()) {
            return State.ANIMATING;
        }

        if (timeout > 0) {
            return State.TIMEOUT;
        }

        if (breakHandler.shouldBreak(this)) {
            return State.HANDLE_BREAK;
        }
        if (Inventory.search().withName(config.rawFood()).first().isEmpty()) {
            return State.BANK;
        }

        return State.COOK;


    }

    private void cook()
    {
        if (cookingRangeObject == null)
        {
            cookingRangeObject = TileObjects.search().withAction("Cook").nearestToPlayer().get();
        }

        if (cookingRangeObject == null)
        {
            started = false;
            return;
        }

        if (config.oneTick())
        {
            if (RandomUtils.nextInt(0, 100) < config.oneTickSuccessRate())
                oneTick();
            return;
        }

        if (!config.oneTick())
        {
            Widgets.search().withTextContains("then click an item").hiddenState(false).first().ifPresentOrElse(menu -> {
                debug = "Menu open";
                Widgets.search().withId(17694735).first().ifPresent(widget -> {
                    debug = "Interact";
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueResumePause(widget.getId(), Inventory.search().withName(config.rawFood()).result().size());
                    setTimeout();
                });
            }, () ->{
                debug = "Find stow";
                Inventory.search().withName(config.rawFood()).first().ifPresent(item -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(cookingRangeObject, "Cook");
                    timeout=3;
                });
            });
        }
    }

    private void oneTick()
    {
        List<Widget> karambwani = Inventory.search().withId(ItemID.RAW_KARAMBWAN).result();
        if (client.getWidget(COOK_ACTION_WIDGET) != null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(COOK_ACTION_WIDGET, 1);
        }

        if (cookingRangeObject != null) {
            if (!karambwani.isEmpty()) {
                Widget karamb = karambwani.get(karambwani.size() - 1);
                MousePackets.queueClickPacket();
                ObjectPackets.queueWidgetOnTileObject(karamb, cookingRangeObject);
            }
        }
    }
    private void bank()
    {
        if (Bank.isOpen())
        {
            List<Widget> bankInv = BankInventory.search().result();
            for (Widget item : bankInv) {
                MousePackets.queueClickPacket();
                BankInventoryInteraction.useItem(item, "Deposit-All");
            }

            Optional<Widget> bankFood = BankUtil.nameContainsNoCase(config.rawFood()).first();
            bankFood.ifPresentOrElse(widget -> BankInteraction.withdrawX(widget, 28), () -> {
                started = false;
                this.state = State.TIMEOUT;
                breakHandler.stopPlugin(this);
            });
            setTimeout();
        }
        else {
            NPCs.search().withAction("Bank").nearestToPlayer().ifPresent(npc -> {
                MousePackets.queueClickPacket();
                NPCInteraction.interact(npc.getId(), "Bank");
                return;
            });

            Optional<TileObject> bankBooth = TileObjects.search().filter(tileObject -> {
                ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(tileObject);
                return getName().toLowerCase().contains("bank") ||
                        Arrays.stream(objectComposition.getActions()).anyMatch(action -> action != null && action.toLowerCase().contains("bank"));
            }).nearestToPlayer();

            if (bankBooth.isPresent()) {

                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(bankBooth.get(), "Bank");
                return;
            }

            TileObjects.search().withName("Bank chest").nearestToPlayer().ifPresent(tileObject -> {
                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(tileObject, "Use");
                return;
            });

        }
    }

    private void setTimeout() {
        timeout = RandomUtils.nextInt(config.tickdelayMin(), config.tickDelayMax());
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;

        if (!started) {
            this.state = State.TIMEOUT;
            breakHandler.stopPlugin(this);
        } else {
            breakHandler.startPlugin(this);
        }
    }
}
