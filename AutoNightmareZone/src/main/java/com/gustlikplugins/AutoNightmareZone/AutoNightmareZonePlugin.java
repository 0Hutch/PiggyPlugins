package com.gustlikplugins.AutoNightmareZone;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Provides;
import com.gustlikplugins.AutoNightmareZone.States.DrinkAbsorptionState;
import com.gustlikplugins.AutoNightmareZone.States.NotInNmzState;
import com.gustlikplugins.AutoNightmareZone.States.PrayerState;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import net.runelite.client.util.HotkeyListener;
import net.runelite.client.input.KeyManager;

import javax.inject.Inject;


@PluginDescriptor(
        name = "<html><font color=\"#9dFFF9\">[GP]</font> NightmareZone</html>",
        description = "Farms nmz",
        enabledByDefault = false,
        tags = {"nightmare zone"}
)
public class AutoNightmareZonePlugin extends Plugin {

    @Inject
    public Client client;

    @Inject
    public AutoNightmareZoneConfig config;

    @Inject
    private AutoNightmareZoneOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ReflectBreakHandler breakHandler;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ClientThread clientThread;

    boolean started;
    public State state;
    //public Instant lastOverload;




    @Provides
    private AutoNightmareZoneConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoNightmareZoneConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        //TODO add breaking and nmz rentry
        //breakHandler.registerPlugin(this);
        overlayManager.add(overlay);
        keyManager.registerKeyListener(toggle);
        state = new NotInNmzState(this);
    }

    @Override
    protected void shutDown() throws Exception {
        //breakHandler.unregisterPlugin(this);
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        clientThread.invoke(() ->
        {
            ((PrayerState) state.getStateNonrecursive(PrayerState.class)).disablePrayers();
        });
    }



    public void toggle() {
        if (!EthanApiPlugin.loggedIn()) {
            return;
        }
        started = !started;
        if(!started){
            ((DrinkAbsorptionState) state.getStateNonrecursive(DrinkAbsorptionState.class)).resetAbsorption();
            clientThread.invoke(() ->
            {
                ((PrayerState) state.getStateNonrecursive(PrayerState.class)).disablePrayers();
            });
            breakHandler.stopPlugin(this);
        }else{
            breakHandler.startPlugin(this);
        }
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || !started || breakHandler.isBreakActive(this)) {
            // We do an early return if the user isn't logged in
            return;
        }
        state = state.initialState.getState();
        state.onGameTick();
    }


    @Subscribe
    private void onConfigChanged(ConfigChanged event){
        if(!event.getKey().equals("PrayerFlickMode"))
            return;

        if(config.prayerFlickMode() == PrayerFlickMode.OFF && config.strategy() == NightmareZoneStrategy.ABSORPTION)
            clientThread.invoke(() ->
            {
                ((PrayerState) state.getStateNonrecursive(PrayerState.class)).disablePrayers();
            });
    }

}
