package com.piggyplugins.BobTheBaller;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.*;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.BankUtil;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Bob The Baller</html>",
        description = "Bob goes Templateing",
        tags = {"ethan", "piggy", "skilling"}
)
public class BobTheBallerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    BobTheBallerConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BobTheBallerOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    State state;
    boolean started;
    private int timeout;
    boolean takeBreak = false;
    public String debug = "";
    private BankState bankState = BankState.DEPOSIT;



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
    private BobTheBallerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BobTheBallerConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || !started || breakHandler.isBreakActive(this)) {
            // We do an early return if the user isn't logged in
            bankState = BankState.DEPOSIT;
            return;
        }

        state = getNextState();
        handleState();
    }

    private void handleState() {
        switch (state) {
            case ANIMATING:
                timeout = 3;
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
                Restock();
                break;
            case BALLS:
                MakeBalls();
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

        if (Inventory.search().withName("Steel bar").first().isPresent() && Inventory.search().withName("Ammo mould").first().isPresent())
            return State.BALLS;

        return State.RESTOCK;
    }

    private void MakeBalls()
    {
        Widgets.search().withId(17694734).hiddenState(false).first().ifPresentOrElse(widget -> {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(17694734, Inventory.search().withName("Steel bar").result().size());
        }, () -> {
            TileObjects.search().withAction("Smelt").withName("Furnace").nearestToPlayer().ifPresent(tileObject -> {
                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(tileObject, "Smelt");
            });
        });
    }

    private boolean CompareItem(List<String> keepItems, String compItem) {
        for (String item : keepItems)
        {
            if (compItem.contains(item))
                return true;
        }
        return false;
    }
    private TileObject BankCloseBy(int distance) {
        Optional<TileObject> bankBooth = null;
        bankBooth = TileObjects.search().withAction("Bank").withinDistance(distance).nearestToPlayer();

        Optional<TileObject> bankChest = null;
        bankChest = TileObjects.search().withName("Bank chest").withinDistance(distance).nearestToPlayer();

        if (bankBooth.isPresent() && bankChest.isPresent())
        {
            WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();

            int bbDist = CalculateDistance(playerPos.getX(), playerPos.getY(), bankBooth.get().getX(), bankBooth.get().getY());
            int bcDist = CalculateDistance(playerPos.getX(), playerPos.getY(), bankChest.get().getX(), bankChest.get().getY());

            return bbDist < bcDist ? bankBooth.get() : bankChest.get();
        }
        if (bankBooth.isPresent())
            return bankBooth.get();
        if (bankChest.isPresent())
            return bankChest.get();
        return null;
    }
    private int CalculateDistance(int x1, int y1, int x2, int y2) {
        return (int)Math.round(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
    }
    private String GetBankInteraction(TileObject bankObject) {
        ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(bankObject);
        return Arrays.stream(objectComposition.getActions()).anyMatch(action -> action != null && action.toLowerCase().contains("bank")) ? "Bank" : "Use";
    }
    private boolean WithdrawItemFromBank(String item, int amount) {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        BankUtil.nameContainsNoCase(item).first().ifPresentOrElse(widget ->
            {
                int localAmount = amount -
                    (
                        Inventory.search().withName(item).first().isPresent() ?
                            (
                                Inventory.search().isStackable(Inventory.search().withName(item).first().get()) ?
                                        Inventory.search().withName(item).first().get().getItemQuantity() :
                                        Inventory.search().withName(item).result().size()
                            ) : 0
                    );

                if (amount < 0)
                    localAmount = BankUtil.getItemAmount(widget.getItemId());

                if (localAmount > 0)
                {
                    MousePackets.queueClickPacket();
                    BankInteraction.withdrawX(widget, localAmount);
                }

                succeeded.set(true);
            },
            () -> {
                succeeded.set(false);
            });

        if (Inventory.search().withName(item).first().isPresent())
            succeeded.set(true);

        return succeeded.get();
    }
    private void Restock()
    {
        if (Bank.isOpen()) {
            ArrayList<String> keepItems = new ArrayList<>();
            keepItems.add("Ammo mould");

            switch (bankState)
            {
                case DEPOSIT:
                    List<Widget> bankInv = BankInventory.search().filter(widget -> !CompareItem(keepItems, widget.getName())).result();
                    for (Widget item : bankInv) {
                        MousePackets.queueClickPacket();
                        BankInventoryInteraction.useItem(item, "Deposit-All");
                    }
                    bankState = BankState.WITHDRAW;
                    setTimeout();
                    return;
                case WITHDRAW:

                    if (!WithdrawItemFromBank("Ammo mould", 1))
                    {
                        started = false;
                    }
                    if (!WithdrawItemFromBank("Steel bar", 27))
                    {
                        started = false;
                    }
                    bankState = BankState.DONE;
                    setTimeout();
                    return;
                case DONE:
                    bankState = BankState.DEPOSIT;
                    break;
            }

            setTimeout();
        }
        else {
            TileObject bankBooth = BankCloseBy(20);
            if (bankBooth != null) {
                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(bankBooth, GetBankInteraction(bankBooth));
            }
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
