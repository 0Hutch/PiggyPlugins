package com.piggyplugins.BobTheLazyNMZ;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.*;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import net.runelite.api.*;
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
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Bob The Lazy NMZ</html>",
        description = "Bob goes NMZing",
        tags = {"ethan", "piggy", "skilling"}
)
public class BobTheLazyNMZPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    BobTheLazyNMZConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BobTheLazyNMZOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    State state;
    boolean started;
    private int timeout;

    public int BoostedStrength, Strength;
    public int BoostedHitpoints, Hitpoints;

    private boolean eat = false;
    private boolean drink = false;

    public int idleCounter = 10;
    public int abosortionCounter = 100;

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
    private BobTheLazyNMZConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BobTheLazyNMZConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {

        BoostedStrength = client.getBoostedSkillLevel(Skill.STRENGTH);
        Strength = client.getRealSkillLevel(Skill.STRENGTH);

        BoostedHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
        Hitpoints = client.getRealSkillLevel(Skill.HITPOINTS);

        if (!EthanApiPlugin.loggedIn() || !started || breakHandler.isBreakActive(this)) {
            // We do an early return if the user isn't logged in
            eat = false;
            drink = false;
            return;
        }

        if (timeout > 0)
        {
            timeout--;
            return;
        }

        if (client.getBoostedSkillLevel(Skill.HITPOINTS) == 0)
            started = false;

        if (drink && Inventory.search().nameContains("Overload").first().isPresent())
        {
            Inventory.search().nameContains("Overload").first().ifPresent(overload -> {
                MousePackets.queueClickPacket();
                InventoryInteraction.useItem(overload, "Drink");
                timeout = 15;
            });
            drink = false;
            return;

        }

        if (
                client.getRealSkillLevel(Skill.STRENGTH) == client.getBoostedSkillLevel(Skill.STRENGTH) &&
                client.getBoostedSkillLevel(Skill.HITPOINTS) > 50 &&
                Inventory.search().nameContains("Overload").first().isPresent()
        )
        {
            drink = true;
            setTimeout();
            return;
        }

        if (eat)
        {
            if (Inventory.search().nameContains("Dwarven rock cake").first().isPresent())
            {
                Inventory.search().nameContains("Dwarven rock cake").first().ifPresent(cake -> {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(cake, "Guzzle");
                });
            }
            eat = false;
        }

        if (client.getBoostedSkillLevel(Skill.HITPOINTS) > 1)
        {
            eat = true;
            setTimeout();
        }

        if (client.getLocalPlayer().getAnimation() == -1)
        {
            idleCounter--;
        }
        else
            idleCounter = 10;

        if (idleCounter <= 0)
        {
            NPCs.search().withAction("Attack").nearestToPlayer().ifPresent(npc -> {
                MousePackets.queueClickPacket();
                NPCInteraction.interact(npc, "Attack");
                idleCounter = 5;
            });
        }

        abosortionCounter--;
        if (abosortionCounter <= 0)
        {
            Optional<Widget> drink = Inventory.search().nameContains("Absorption").withAction("Drink").first();
            if (drink.isPresent())
            {
                MousePackets.queueClickPacket();
                InventoryInteraction.useItem(drink.get(), "Drink");
                abosortionCounter = RandomUtils.nextInt(config.drinkDelay() - 25, config.drinkDelay() + 25);
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
