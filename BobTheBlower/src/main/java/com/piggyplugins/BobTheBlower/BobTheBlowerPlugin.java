package com.piggyplugins.BobTheBlower;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.*;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.BankUtil;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import net.runelite.api.*;
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

import com.google.inject.Inject;
import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Bob The Blower</html>",
        description = "Bob goes Blowing",
        tags = {"ethan", "piggy", "skilling"}
)
public class BobTheBlowerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    BobTheBlowerConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BobTheBlowerOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    State state;
    boolean started;
    private int timeout;
    WorldPoint startTile;
    WorldPoint targetTile;
    boolean takeBreak = false;
    public String debug = "";
    public boolean blowing = false;


    boolean resetStartTile = true;

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
    private BobTheBlowerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BobTheBlowerConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || !started || breakHandler.isBreakActive(this)) {
            startTile = null;
            resetStartTile = true;
            // We do an early return if the user isn't logged in
            return;
        }

        state = getNextState();
        handleState();
    }

    private void handleState() {
        switch (state) {
            case ANIMATING:
                timeout = 5;
                break;
            case HANDLE_BREAK:
                takeBreak = false;
                breakHandler.startBreak(this);
                timeout = 10;
                break;
            case TIMEOUT:
                timeout--;
                break;
            case RESTOCK:
                blowing = false;
                Restock();
                break;
            case BLOW:
                Blow();
                break;

        }
    }

    private State getNextState() {
        if (EthanApiPlugin.isMoving() || client.getLocalPlayer().getAnimation() != -1) {
            return State.ANIMATING;
        }
        if (breakHandler.shouldBreak(this)) {
            return State.HANDLE_BREAK;
        }
        if (timeout > 0) {
            return State.TIMEOUT;
        }
        if (hasItems() && !blowing)
        {
            return State.BLOW;
        }
        else
        {
            return State.RESTOCK;
        }
    }


    private void Blow()
    {

        Widgets.search().withTextContains("then click an item").hiddenState(false).first().ifPresentOrElse(menu -> {
            debug = "Menu open";
            Widgets.search().withId(17694735).first().ifPresent(widget -> {
                debug = "Interact";
                MousePackets.queueClickPacket();
                WidgetPackets.queueResumePause(widget.getId() + config.option(), Inventory.search().withName(config.items2()).result().size());
                blowing = true;
                setTimeout();
            });
        }, () ->{
            debug = "Find stow";
            Inventory.search().withName(config.items1()).first().ifPresent(item1 -> {
                Inventory.search().withName(config.items2()).first().ifPresent(item2 -> {
                    MousePackets.queueClickPacket();
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetOnWidget(item1, item2);
                });
            });
        });
    }

    private void Restock()
    {
        if (Bank.isOpen()) {
            List<Widget> bankInv = BankInventory.search().result();
            for (Widget item : bankInv) {
                MousePackets.queueClickPacket();
                BankInventoryInteraction.useItem(item, "Deposit-All");
            }

            BankUtil.nameContainsNoCase(config.items1()).first().ifPresentOrElse(widget ->
                        BankInteraction.withdrawX(widget, config.items1Amount()),
                    () -> {
                        started = false;
                        this.state = State.TIMEOUT;
                        breakHandler.stopPlugin(this);
            });

            BankUtil.nameContainsNoCase(config.items2()).first().ifPresentOrElse(widget ->
                            BankInteraction.withdrawX(widget, config.items2Amount()),
                    () -> {
                        started = false;
                        this.state = State.TIMEOUT;
                        breakHandler.stopPlugin(this);
                    });
            setTimeout();
        }
        else {
            Optional<TileObject> bankBooth = TileObjects.search().filter(tileObject -> {
                ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(tileObject);
                return getName().toLowerCase().contains("bank") ||
                        Arrays.stream(objectComposition.getActions()).anyMatch(action -> action != null && action.toLowerCase().contains("bank"));
            }).nearestToPlayer();

            if (bankBooth.isPresent()) {

                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(bankBooth.get(), "Bank");
            }
        }
    }
    private boolean hasItems()
    {
        String[] items = { config.items1(), config.items2()};
        boolean hasItems = true;
        for (int i = 0; i < items.length; i++) {
            if (!InventoryUtil.hasItem(items[i]))
                hasItems = false;
        }
        return hasItems;
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
            resetStartTile = true;
            startTile = null;
        } else {
            breakHandler.startPlugin(this);
            startTile = client.getLocalPlayer().getWorldLocation();
        }
    }
}
