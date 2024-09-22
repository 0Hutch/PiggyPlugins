package com.spinplugins.IronBuddy;

import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.spinplugins.IronBuddy.data.Const;
import com.spinplugins.IronBuddy.tasks.*;
import com.spinplugins.IronBuddy.tasks.Bank.BankPinTask;
import com.spinplugins.IronBuddy.tasks.Crafting.GetComponentsTask;
import com.spinplugins.IronBuddy.tasks.Crafting.MakeGlassItemTask;
import com.spinplugins.IronBuddy.tasks.Crafting.MakeGlassTask;
import com.spinplugins.IronBuddy.tasks.Crafting.ReturnToBankTask;
import com.spinplugins.IronBuddy.tasks.Dialogue.ExchangePlanksTask;
import com.spinplugins.IronBuddy.tasks.Dialogue.RemoveLarderTask;
import com.spinplugins.IronBuddy.tasks.NPC.FindPhialsTask;
import com.spinplugins.RuneUtils.PathFinding.PathFinder;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import com.piggyplugins.PiggyUtils.strategy.TaskManager;
import com.piggyplugins.PiggyUtils.strategy.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.event.KeyEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@PluginDescriptor(
        name = "IronBuddy",
        description = "",
        enabledByDefault = false,
        tags = {"spin", "plugin"}
)

public class IronBuddyPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(IronBuddyPlugin.class);
    @Getter
    @Inject
    private Client client;
    @Inject
    private IronBuddyConfig config;
    @Inject
    private IronBuddyOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;

    public Duration runningDuration = Duration.ZERO;
    private Instant timer = Instant.now();

    @Getter
    private PathFinder pathFinder = new PathFinder(this, client);
    @Getter
    private WorldPoint destination = new WorldPoint(3093, 3248, 0);

    public TaskManager taskManager = new TaskManager();
    public List<AbstractTask> tasks = List.of(new BankPinTask(this, config));

    public int idleTicks = 0;
    public int timeout = 0;
    public int bankPinIndex = 0;

    private boolean started = false;
    public boolean bankPin = false;
    public boolean isSmelting = false;
    public boolean isCrafting = false;

    @Getter
    private String activeTaskName = "None";

    @Provides
    private IronBuddyConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(IronBuddyConfig.class);
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    @Inject
    private KeypressListener keyPressListener;

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        keyManager.registerKeyListener(keyPressListener);
        overlayManager.add(overlay);
        timeout = 0;
        timer = Instant.now();
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        keyManager.unregisterKeyListener(keyPressListener);
        overlayManager.remove(overlay);
        timeout = 0;
        timer = Instant.now();
    }

    private List<AbstractTask> chooseTask() {
        Const.BuddyTasks task = config.taskType() != null ? config.taskType() : Const.BuddyTasks.CRAFTING_GLASS;

        switch (task) {
            case CRAFTING_GLASS: {
                return List.of(
                        new BankPinTask(this, config),
                        new ReturnToBankTask(this, config),
                        new MakeGlassTask(this, config),
                        new GetComponentsTask(this, config));
            }
            case CONSTRUCTION_POH: {
                return List.of(
                        new RemoveLarderTask(this, config),
                        new MakeLarderTask(this, config),
                        new FindLarderTask(this, config),
                        new FindPortalTask(this, config),
                        new ExchangePlanksTask(this, config),
                        new FindPhialsTask(this, config));
            }
            case PATHING_TESTING: {
                return List.of(
                        new PathingTestingTask(this, config));
            }
            case CRAFTING_GLASS_ITEM: {
                return List.of(
                        new BankPinTask(this, config),
                        new ReturnToBankTask(this, config),
                        new MakeGlassItemTask(this, config),
                        new GetComponentsTask(this, config));
            }
        }
        return null;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if(this.pathFinder.hasFinishedPathing()) {
            log.info("Pathing has finished");
            this.destination = null;
            return;
        }

        if(this.pathFinder.pathing()) {
            this.pathFinder.path();
            return;
        }

        if (!EthanApiPlugin.loggedIn() || !started || EthanApiPlugin.isMoving()) {
            return;
        }

        runningDuration = runningDuration.plus(Duration.between(timer, Instant.now()));
        timer = Instant.now();

        if (this.client.getLocalPlayer().getAnimation() == -1) {
            idleTicks++;
        } else {
            idleTicks = 0;
            return;
        }

        if (timeout > 0) {
            timeout--;
            return;
        }

        if (taskManager.hasTasks()) {
            for (AbstractTask t : taskManager.getTasks()) {
                if (t.validate()) {
                    log.info("Executing task: {}", t.getClass().getSimpleName());

                    activeTaskName = t.getClass().getSimpleName();
                    t.execute();

                    if(config.tickDelay()) {
                        setTimeout();
                    }
                    return;
                }
            }
        }
    }

    /**
     * This method is used to type a bank pin into the bank pin interface
     * @param bankPin the bank pin to type
     * @return true if the bank pin was accepted, false if it was not
     *
     */
    public boolean typeBankPin(String bankPin) {
        if(bankPin == null || bankPin.length() != 4) {
            log.info("Bank pin must be 4 digits long");
            return false;
        }

        if(!bankPin.matches("^[0-9]+$")) {
            log.info("Bank pin must be a number");
            return false;
        }

        Widget bankPinWidget = client.getWidget(213, 0);

        if (bankPinWidget != null) {
            imitateKeyPress(bankPin.toCharArray()[bankPinIndex]);
            log.info("Entering: " + bankPin.toCharArray()[bankPinIndex]);

            if(bankPinIndex < 3) {
                bankPinIndex++;
            } else {
                bankPinIndex = 0;
            }

            client.setVarcIntValue(VarClientInt.BLOCK_KEYPRESS, client.getGameCycle() + 1);
            return true;
        }

        return false;
    }

    public void toggle() {
        if (!EthanApiPlugin.loggedIn()) {
            return;
        }

        started = !started;

        if(config.taskType() != null) {
            tasks = chooseTask();
        }

        if (started && tasks != null) {
            for (AbstractTask t : tasks) {
                taskManager.addTask(t);
            }
        } else {
            taskManager.clearTasks();
        }
    }

    private void setTimeout() {
        Random random = new Random();
        timeout = random.nextInt(config.tickDelayMax() - config.tickDelayMin() + 1) + config.tickDelayMin();
    }

    public boolean chattingWithPhials() {
        return Widgets.search().withParentId(14352385).withTextContains("Exchange").first().isPresent();
    }

    private void sendKeyEvent(int id, char key) {
        KeyEvent keyEvent = new KeyEvent(client.getCanvas(), id, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, key);
        client.getCanvas().dispatchEvent(keyEvent);
    }

    private void pressKey(char key) {
        sendKeyEvent(401, key);
        sendKeyEvent(402, key);
        sendKeyEvent(400, key);
    }

    public void imitateKeyPress(char keyChar) {
        pressKey(keyChar);
    }

}