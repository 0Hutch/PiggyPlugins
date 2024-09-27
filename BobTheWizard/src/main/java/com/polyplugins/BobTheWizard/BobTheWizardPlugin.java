package com.polyplugins.BobTheWizard;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.GameState;
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
        name = "<html><font color=\"#ff4d00\">[GS]</font> Auto Alch+Tele </html>",
        description = "Magic goes brrr",
        tags = {"ethan", "elkondo", "skilling"}
)
public class BobTheWizardPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    BobTheWizardConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BobTheWizardOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    State state;
    boolean started;
    private int timeout;
    private int teleportTimeout;
    private boolean alced;
    private final int TELEPORT_ANIMATION_ID = 714;
    private final int HIGH_ALC_ANIMATION_ID = 713;
    private final String Dodgy_Necklace = "Dodgy necklace";
    private final HotkeyListener toggle = new HotkeyListener(() -> {
        return this.config.toggle();
    }) {
        public void hotkeyPressed() {
            BobTheWizardPlugin.this.toggle();
        }
    };

    public BobTheWizardPlugin() {
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
    private BobTheWizardConfig getConfig(ConfigManager configManager) {
        return (BobTheWizardConfig)configManager.getConfig(BobTheWizardConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (EthanApiPlugin.loggedIn() && this.started && !this.breakHandler.isBreakActive(this)) {
            this.state = this.getNextState();
            this.handleState();
        }
    }

    private void handleState() {
        switch (this.state) {
            case HANDLE_BREAK:
                this.breakHandler.startBreak(this);
                this.timeout = 10;
                break;
            case TIMEOUT:
                --this.timeout;
                break;
            case HIGH_ALCING:
                --this.teleportTimeout;
                break;
            case TELEPORTING:
                this.alced = false;
                break;
            case CAST_HIGH_ALC:
                if (!this.alced) {
                    this.alc();
                    this.setTeleportTimeout();
                    this.alced = true;
                }
                break;
            case CAST_TELEPORT:
                this.teleport();
                this.setTimeout();
        }

    }

    private State getNextState() {
        if (EthanApiPlugin.isMoving()) {
            return State.ANIMATING;
        } else if (this.client.getLocalPlayer().getAnimation() == 714) {
            return State.TELEPORTING;
        } else if (this.client.getLocalPlayer().getAnimation() == 713 && this.teleportTimeout > 0) {
            return State.HIGH_ALCING;
        } else if (this.timeout > 0) {
            return State.TIMEOUT;
        } else if (this.breakHandler.shouldBreak(this)) {
            return State.HANDLE_BREAK;
        } else {
            return !this.alced && !this.config.alc().isEmpty() ? State.CAST_HIGH_ALC : State.CAST_TELEPORT;
        }
    }

    private void alc() {
        String[] itemsToAlch = this.config.alc().replace(", ", ",").split(",");
        Widget highAlch = this.client.getWidget(WidgetInfoExtended.SPELL_HIGH_LEVEL_ALCHEMY.getPackedId());
        if (itemsToAlch.length > 0 && highAlch != null) {
            Inventory.search().onlyStackable().matchesWildCardNoCase(itemsToAlch[0]).first().ifPresentOrElse((item) -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(highAlch, item);
            }, (Runnable)null);
        }

    }

    private void teleport() {
        WidgetInfoExtended teleportEnum = null;
        switch (this.config.teleport()) {
            case VARROCK:
                teleportEnum = WidgetInfoExtended.SPELL_VARROCK_TELEPORT;
                break;
            case LUMBRIDGE:
                teleportEnum = WidgetInfoExtended.SPELL_LUMBRIDGE_TELEPORT;
                break;
            case FALADOR:
                teleportEnum = WidgetInfoExtended.SPELL_FALADOR_TELEPORT;
                break;
            case CAMELOT:
                teleportEnum = WidgetInfoExtended.SPELL_CAMELOT_TELEPORT;
                break;
            case ARDOUGNE:
                teleportEnum = WidgetInfoExtended.SPELL_ARDOUGNE_TELEPORT;
                break;
            case WATCHTOWER:
                teleportEnum = WidgetInfoExtended.SPELL_WATCHTOWER_TELEPORT;
                break;
            case TROLLHEIM:
                teleportEnum = WidgetInfoExtended.SPELL_TROLLHEIM_TELEPORT;
                break;
            case APE_ATOLL:
                teleportEnum = WidgetInfoExtended.SPELL_APE_ATOLL_TELEPORT;
                break;
            case KOUREND:
                teleportEnum = WidgetInfoExtended.SPELL_TELEPORT_TO_KOUREND;
        }

        if (teleportEnum != null) {
            Optional<Widget> teleportSpellIcon = Widgets.search().withId(teleportEnum.getPackedId()).first();
            if (teleportSpellIcon.isPresent()) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction((Widget)teleportSpellIcon.get(), new String[]{"Cast"});
            }

        }
    }

    private void setTimeout() {
        this.timeout = RandomUtils.nextInt(this.config.tickdelayMin(), this.config.tickDelayMax());
    }

    private void setTeleportTimeout() {
        this.teleportTimeout = RandomUtils.nextInt(this.config.teleportTickDelayMin(), this.config.teleportTickDelayMax());
    }

    public void toggle() {
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.started = !this.started;
            if (!this.started) {
                this.state = State.TIMEOUT;
                this.breakHandler.stopPlugin(this);
            } else {
                this.breakHandler.startPlugin(this);
            }

        }
    }
}
