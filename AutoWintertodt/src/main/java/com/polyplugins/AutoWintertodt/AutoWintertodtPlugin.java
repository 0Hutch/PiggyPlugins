package com.polyplugins.AutoWintertodt;


import com.polyplugins.AutoWintertodt.Data.Data;
import com.polyplugins.AutoWintertodt.Model.Task;
import com.polyplugins.AutoWintertodt.Tasks.Banking;
import com.polyplugins.AutoWintertodt.Tasks.ChopTree;
import com.polyplugins.AutoWintertodt.Tasks.EatFood;
import com.polyplugins.AutoWintertodt.Tasks.EnterWintertodt;
import com.polyplugins.AutoWintertodt.Tasks.FeedBrazier;
import com.polyplugins.AutoWintertodt.Tasks.FixBrazier;
import com.polyplugins.AutoWintertodt.Tasks.Fletch;
import com.polyplugins.AutoWintertodt.Tasks.LeaveWintertodt;
import com.polyplugins.AutoWintertodt.Tasks.LightBrazier;
import com.polyplugins.AutoWintertodt.Tasks.WalkToStart;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.google.inject.Provides;
import com.polyplugins.AutoWintertodt.API.State;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[GS]</font> Auto Wintertodt</html>",
        description = "Wintertodt para ti, consume comida y banquea",
        tags = {"ElGuason"}
)
public class AutoWintertodtPlugin extends Plugin {
    @Inject
    private AutoWintertodtConfig injectedConfig;
    @Inject
    Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private KeyManager keyManager;
    public State state;
    public static AutoWintertodtConfig wintertodtConfig;
    private final List<Task> tasks = new ArrayList();
    private final WorldArea areaPro = new WorldArea(1619, 3997, 4, 4, 0);
    private int timeout;
    private List<GameObject> wintertodtSnowFall = new ArrayList();
    private WorldPoint projectile1 = new WorldPoint(0, 0, 0);

    public AutoWintertodtPlugin() {
    }

    @Provides
    public AutoWintertodtConfig getConfig(ConfigManager configManager) {
        return (AutoWintertodtConfig)configManager.getConfig(AutoWintertodtConfig.class);
    }

    protected void startUp() {
        this.tasks.add(new EatFood());
        this.tasks.add(new Banking());
        this.tasks.add(new EnterWintertodt());
        this.tasks.add(new LeaveWintertodt());
        this.tasks.add(new ChopTree());
        this.tasks.add(new FixBrazier());
        this.tasks.add(new LightBrazier());
        this.tasks.add(new Fletch());
        this.tasks.add(new FeedBrazier());
        this.tasks.add(new WalkToStart());
        wintertodtConfig = this.injectedConfig;
        Data.setHasResources(false);
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved e) {
        Player player = this.client.getLocalPlayer();
        Projectile project = e.getProjectile();
        if (project.getId() == 501) {
            int x = this.client.getBaseX() + e.getPosition().getSceneX();
            int y = this.client.getBaseY() + e.getPosition().getSceneY();
            int cycles = e.getProjectile().getEndCycle() - e.getProjectile().getStartCycle();
            WorldPoint projectile1 = WorldPoint.fromLocal(this.client, e.getPosition());
            if (cycles == 120 && projectile1.isInArea(new WorldArea[]{this.areaPro}) && player.getWorldLocation().equals(new WorldPoint(1622, 3996, 0))) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(1620, 3994, 0));
            }
        }

        this.timeout = 2 + this.tickDelay();
    }

    private int tickDelay() {
        return wintertodtConfig.tickDelay() ? ThreadLocalRandom.current().nextInt(wintertodtConfig.tickDelayMin(), wintertodtConfig.tickDelayMax()) : 0;
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        Player player = this.client.getLocalPlayer();
        if (EthanApiPlugin.isMoving() || this.client.getLocalPlayer().getAnimation() != -1) {
            this.tickDelay();
            this.timeout = this.tickDelay();
        }

        if (player.getWorldLocation().equals(new WorldPoint(1620, 3994, 0))) {
            this.walk_to();
        } else {
            this.checkResources();
            this.checkInputWidget();
            Iterator var3 = this.tasks.iterator();

            while(var3.hasNext()) {
                Task task = (Task)var3.next();
                this.tickDelay();
                this.timeout = 2 + this.tickDelay();
                if (task.activateTask()) {
                    this.tickDelay();
                    this.timeout = 2 + this.tickDelay();
                    task.runTask();
                    break;
                }
            }
        }

    }

    private void checkInputWidget() {
        Optional<Widget> inputWidget = Widgets.search().withId(WidgetInfo.CHATBOX_FULL_INPUT.getId()).hiddenState(false).first();
        if (inputWidget.isPresent()) {
            EthanApiPlugin.getClient().runScript(new Object[]{138});
        }

    }

    private void checkResources() {
        if (Inventory.getItemAmount(20696) + Inventory.getItemAmount(20695) >= this.injectedConfig.maxResources()) {
            Data.hasResources = true;
        }

        if (Inventory.getItemAmount(20696) + Inventory.getItemAmount(20695) < 1) {
            Data.hasResources = false;
        }

    }

    private void walk_to() {
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(new WorldPoint(1622, 3994, 0));
        this.timeout = 2 + this.tickDelay();
    }

    public static boolean isInWintertodtRegion() {
        return EthanApiPlugin.playerPosition().getRegionID() == 6462;
    }

    public static boolean isGameStarted() {
        if (!isInWintertodtRegion()) {
            return false;
        } else {
            Widget w = EthanApiPlugin.getClient().getWidget(396, 20);
            return w != null && w.getText().contains("Energy");
        }
    }

    public static AutoWintertodtConfig getWintertodtConfig() {
        return wintertodtConfig;
    }

    List<GameObject> getWintertodtSnowFall() {
        return this.wintertodtSnowFall;
    }
}
