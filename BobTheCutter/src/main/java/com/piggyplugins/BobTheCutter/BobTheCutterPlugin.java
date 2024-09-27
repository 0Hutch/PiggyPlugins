package com.piggyplugins.BobTheCutter;

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
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Bob The Cutter</html>",
        description = "Bob goes Templateing",
        tags = {"ethan", "piggy", "skilling"}
)
public class BobTheCutterPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    BobTheCutterConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BobTheCutterOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    State state;
    boolean started;
    private int timeout;
    boolean takeBreak = false;
    public String debug = "";

    public int moveToTree = 0;
    public int moveToBank = 0;

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
    private BobTheCutterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BobTheCutterConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || !started || breakHandler.isBreakActive(this)) {
            // We do an early return if the user isn't logged in
            return;
        }

        state = getNextState();
        handleState();
    }

    private void handleState() {
        if (state != State.ANIMATING && state != State.TIMEOUT)
        {
            if (state != State.MOVE_TO_BANK)
                moveToBank = 0;
            if (state != State.MOVE_TO_TREE)
                moveToTree = 0;
        }

        switch (state) {
            case ANIMATING:
                setTimeout();
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
            case CUT:
                Cut();
                break;
            case MOVE_TO_BANK:
                MoveToBank();
                break;
            case MOVE_TO_TREE:
                MoveToTree();
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
        if (!Inventory.full() && TileObjects.search().nameContains("Mahogany tree").withinDistance(10).first().isPresent())
            return State.CUT;
        if (!Inventory.full() && !TileObjects.search().nameContains("Mahogany tree").withinDistance(10).first().isPresent())
            return State.MOVE_TO_TREE;
        if (!TileObjects.search().nameContains("Bank").withinDistance(10).first().isPresent())
            return State.MOVE_TO_BANK;
        return State.RESTOCK;
    }


    private void Cut()
    {
        TileObjects.search().withName("Mahogany tree").withAction("Chop down").nearestToPlayer().ifPresent(tree -> {
            MousePackets.queueClickPacket();
            TileObjectInteraction.interact(tree, "Chop down");
        });
    }

    private void MoveToBank()
    {
        switch (moveToBank)
        {
            case 0:
                TileObjects.search().withName("Hole").withAction("Climb through").nearestToPlayer().ifPresent(tileObject -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject, "Climb through");
                });
                moveToBank++;
                break;
            case 1:
                if (MoveToTile(new WorldPoint(3741, 3805, 0)))
                    moveToBank++;
                break;

        }

    }

    private void MoveToTree()
    {
        switch (moveToTree)
        {
            case 0:
                if (MoveToTile(new WorldPoint(3716, 3815, 0)))
                    moveToTree++;
                break;
            case 1:
                TileObjects.search().withName("Hole").withAction("Climb through").nearestToPlayer().ifPresent(tileObject -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject, "Climb through");
                });
                moveToTree++;
                break;
        }

    }

    private boolean MoveToTile(WorldPoint targetTile)
    {
        if (client.getLocalPlayer().getWorldLocation().getX() == targetTile.getX() && client.getLocalPlayer().getWorldLocation().getY() == targetTile.getY())
            return true;

        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(targetTile);
        return false;
    }

    private void Restock()
    {
        if (Bank.isOpen()) {
            List<Widget> bankInv = BankInventory.search().result();
            for (Widget item : bankInv) {
                MousePackets.queueClickPacket();
                BankInventoryInteraction.useItem(item, "Deposit-All");
            }
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

            TileObjects.search().withName("Bank chest").nearestToPlayer().ifPresent(tileObject -> {
                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(tileObject, "Use");
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
