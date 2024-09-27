package com.piggyplugins.BobTheFarmer;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.*;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.*;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.BankUtil;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import com.google.inject.Inject;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;




@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Bob The Farmer</html>",
        description = "Bob goes Farming",
        tags = {"ethan", "piggy", "skilling"}
)
public class BobTheFarmerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    BobTheFarmerConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BobTheFarmerOverlay overlay;
    @Inject
    ItemManager itemManager;

    //------------------------------------- General variables -------------------------------------
    State state;
    WorldPoint debugPoint = new WorldPoint(0,0,0);
    boolean started;
    private int timeout;
    public String debug = "";
    public String string_state = "";

    //------------------------------------- Herb variables -------------------------------------
    private final String BottomlessCompostBucket = "Bottomless compost bucket";
    boolean herbRun = false;
    private final String[] HerbTools =  {"Magic secateurs", "Spade", "Rake", "Seed dibber" };
    private BankState herbBankState = BankState.DEPOSIT;
    HerbPatch HerbPatchStateDisplay = null;
    private HerbPatch ArdougneHerbPatch = null;
    private HerbPatch CatherbyHerbPatch = null;
    private HerbPatch CivitasIllaFortisHerbPatch = null;
    private HerbPatch FaladorHerbPatch = null;
    private HerbPatch FarmingGuildHerbPatch = null;
    private HerbPatch HarmonyIslandHerbPatch = null;
    private HerbPatch HosidiusHerbPatch = null;
    private HerbPatch PortPhasmatysHerbPatch = null;
    private HerbPatch TrollStrongholdHerbPatch = null;
    private HerbPatch WeissHerbPatch = null;

    //------------------------------------- Allotment pathces -------------------------------------
    AllotmentPatch AllotmentPatchStateDisplay = null;
    private AllotmentPatch ArdougneNorthAllotmentPatch = null;
    private AllotmentPatch ArdougneSouthAllotmentPatch = null;
    private AllotmentPatch CatherbyNorthAllotmentPatch = null;
    private AllotmentPatch CatherbySouthAllotmentPatch = null;
    private AllotmentPatch FaladorNorthAllotmentPatch = null;
    private AllotmentPatch FaladorSouthAllotmentPatch = null;
    private AllotmentPatch FarmingGuildNorthAllotmentPatch = null;
    private AllotmentPatch FarmingGuildSouthAllotmentPatch = null;
    private AllotmentPatch HosidiusNorthAllotmentPatch = null;
    private AllotmentPatch HosidiusSouthAllotmentPatch = null;
    private AllotmentPatch PortPhasmatysNorthAllotmentPatch = null;
    private AllotmentPatch PortPhasmatysSouthAllotmentPatch = null;

    //------------------------------------- Tree variables -------------------------------------
    boolean treeRun = false;
    private final String[] TreeTools = {"Spade", "Rake"};
    private BankState treeBankState = BankState.DEPOSIT;
    TreePatch TreePatchStateDisplay = null;
    private TreePatch FaladorTreePatch = null;
    private TreePatch FarmingGuildTreePatch = null;
    private TreePatch LumbridgeTreePatch = null;
    private TreePatch TaverleyTreePatch = null;
    private TreePatch GnomeStrongholdTreePatch = null;
    private TreePatch VarrockTreePatch = null;



    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(hotkeyListenerToggle);
        keyManager.registerKeyListener(hotkeyListenerHerbRun);
        keyManager.registerKeyListener(hotkeyListenerTreeRun);
        keyManager.registerKeyListener(hotkeyListenerDebug);
        this.overlayManager.add(overlay);
    }
    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(hotkeyListenerToggle);
        keyManager.unregisterKeyListener(hotkeyListenerHerbRun);
        keyManager.unregisterKeyListener(hotkeyListenerTreeRun);
        keyManager.unregisterKeyListener(hotkeyListenerDebug);
        this.overlayManager.remove(overlay);
    }
    @Provides
    private BobTheFarmerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BobTheFarmerConfig.class);
    }
    @Subscribe
    private void onChatMessage(ChatMessage message) {
        if (message.getType() == ChatMessageType.GAMEMESSAGE)
        {
            //Handle failed teleports
            if (message.getMessage().contains("Try again tomorrow when the cape"))
            {
                ArdougneHerbPatch.PathIndex = 2;
            }
            if (message.getMessage().contains("Try again tomorrow whilst the ring"))
            {
                FaladorHerbPatch.PathIndex = 10;
            }
        }
    }
    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || !started) {
            // We do an early return if the user isn't logged in
            herbBankState = BankState.TELEPORT;
            treeBankState = BankState.TELEPORT;
            herbRun = false;
            treeRun = false;
            return;
        }

        //Reset herb run data if not on a herb run
        if (!herbRun)
        {
            ResetHerbPatchStates();
            ResetAllotmentPatchStates();
        }

        //Reset tree run data if not on a tree run
        if (!treeRun)
            ResetTreePatchStates();



        //Get the next step
        state = getNextState();
        string_state = state.name();
        handleState();
    }

    //Resets all herb run data to its default state
    private void ResetHerbPatchStates() {
        ArdougneHerbPatch = new HerbPatch("Ardougne", new String[] {});
        ArdougneHerbPatch.SetPath(Paths.ArdougneHerbTeleportPath1, "Teleport");

        CatherbyHerbPatch = new HerbPatch("Catherby", new String[] {});
        CatherbyHerbPatch.SetPath(Paths.CatherbyHerbTeleportPath1, "Camelot");

        CivitasIllaFortisHerbPatch = new HerbPatch("Civitas", new String[] {});

        FaladorHerbPatch = new HerbPatch("Falador", new String[] {});
        FaladorHerbPatch.SetPath(Paths.FaladorHerbTeleportPath1, "Teleport1");
        FaladorHerbPatch.SetPath(Paths.FaladorHerbTeleportPath2, "Teleport2");

        FarmingGuildHerbPatch = new HerbPatch("Farming guild", new String[] {});

        HarmonyIslandHerbPatch = new HerbPatch("Harmony", new String[] {});

        HosidiusHerbPatch = new HerbPatch("Hosidius", new String[] {});
        HosidiusHerbPatch.SetPath(Paths.HosidiusHomeTeleport, "Home Teleport");

        PortPhasmatysHerbPatch = new HerbPatch("Port Phasmatys", new String[] {"Ectophial"});
        PortPhasmatysHerbPatch.SetPath(Paths.PortPhasmatysEctophial, "Ectophial");

        TrollStrongholdHerbPatch = new HerbPatch("Troll Stronghold", new String[] {});

        WeissHerbPatch = new HerbPatch("Weiss", new String[] {});
    }
    //Resets all allotment run data to its default state
    private void ResetAllotmentPatchStates() {
        ArdougneNorthAllotmentPatch = new AllotmentPatch(
                "Ardougne north",
                new WorldArea(new WorldPoint(2671, 3377, 0), 1, 2),
                "Pay (north)",
                new String[] {});
        ArdougneSouthAllotmentPatch = new AllotmentPatch(
                "Ardougne south",
                new WorldArea(new WorldPoint(2671, 3372, 0), 1, -2),
                "Pay (south)",
                new String[] {});
        CatherbyNorthAllotmentPatch = new AllotmentPatch(
                "Catherby north",
                new WorldArea(new WorldPoint(2814, 3466, 0), 1, 2),
                "Pay (north)",
                new String[] {});
        CatherbySouthAllotmentPatch = new AllotmentPatch(
                "Catherby south",
                new WorldArea(new WorldPoint(2814, 3461, 0), 1, -2),
                "Pay (south)",
                new String[] {});
        FaladorNorthAllotmentPatch = new AllotmentPatch(
                "Falador north",
                new WorldArea(new WorldPoint(3052, 3307, 0), -2, 1),
                "Pay (north-west)",
                new String[] {});
        FaladorSouthAllotmentPatch = new AllotmentPatch(
                "Falador south",
                new WorldArea(new WorldPoint(3054, 3304, 0), 2, 1),
                "Pay (south-east)",
                new String[] {});
        FarmingGuildNorthAllotmentPatch = new AllotmentPatch(
                "Farming Guild north",
                new WorldArea(new WorldPoint(1267, 3731, 0), 1, 2),
                "Pay (north)",
                new String[] {});
        FarmingGuildSouthAllotmentPatch = new AllotmentPatch(
                "Farming Guild south",
                new WorldArea(new WorldPoint(1267, 3728, 0), 1, -2),
                "Pay (south)",
                new String[] {});
        HosidiusNorthAllotmentPatch = new AllotmentPatch(
                "Hosidius north",
                new WorldArea(new WorldPoint(1738, 3553, 0), 1, 2),
                "Pay (north-east)",
                new String[] {});
        HosidiusSouthAllotmentPatch = new AllotmentPatch(
                "Hosidius south",
                new WorldArea(new WorldPoint(1735, 3552, 0), 1, -2),
                "Pay (south-west)",
                new String[] {});
        PortPhasmatysNorthAllotmentPatch = new AllotmentPatch(
                "Port Phasmatys north",
                new WorldArea(new WorldPoint(3598, 3524, 0), 1, 2),
                "Pay (north-west)",
                new String[] {});
        PortPhasmatysSouthAllotmentPatch = new AllotmentPatch(
                "Port Phasmatys south",
                new WorldArea(new WorldPoint(3602, 3523, 0), 1, -2),
                "Pay (south-east)",
                new String[] {});
    }
    //Resets all tree run data to its default state
    private void ResetTreePatchStates() {
        FaladorTreePatch = new TreePatch("Falador", new WorldArea(3002,3371,4,4,0), new String[] {});
        FaladorTreePatch.SetPath(Paths.FaladorTreeTeleportPath, "Teleport");

        FarmingGuildTreePatch = new TreePatch("Farming Guild", new WorldArea(1230,3734,4,4,0), new String[] {});

        LumbridgeTreePatch = new TreePatch("Lumbridge", new WorldArea(3191,3229,4,4,0), new String[] {});
        LumbridgeTreePatch.SetPath(Paths.LumbridgeTreeTeleportPath, "Teleport");

        TaverleyTreePatch = new TreePatch("Taverley", new WorldArea(2934,3436,4,4,0), new String[] {});
        TaverleyTreePatch.SetPath(Paths.TaverleyTreeTeleportPath1, "Teleport1");
        TaverleyTreePatch.SetPath(Paths.TaverleyTreeTeleportPath2, "Teleport2");

        GnomeStrongholdTreePatch = new TreePatch("Gnome Strongold", new WorldArea(2434,3413,4,4,0), new String[] {});
        GnomeStrongholdTreePatch.SetPath(Paths.GnomeStrongholdTreeTeleportPath1, "Teleport1");
        GnomeStrongholdTreePatch.SetPath(Paths.GnomeStrongholdTreeTeleportPath2, "Teleport2");

        VarrockTreePatch = new TreePatch("Varrock", new WorldArea(3227,3457,4,4,0), new String[] {});
        VarrockTreePatch.SetPath(Paths.VarrockTreeTeleportPath, "Teleport");
    }

    //------------------------------------- State machine -------------------------------------
    //This is the overall state machine for the plugin patches are handeld seperatly
    //This state machine does banking and traveling
    private void handleState() {
        if (state == null)
            return;

        switch (state) {
            case ANIMATING:
                break;
            case TIMEOUT:
                timeout--;
                break;

            //HERBS
            case RESTOCK_HERB:
                RestockHerb();
                string_state = herbBankState.name();
                break;
            case HERB_TRAVEL_ARDOUGNE:
                TravelToArdougneHerbPatch(ArdougneHerbPatch);
                break;
            case HERB_ARDOUGNE:
                FarmHerbs(ArdougneHerbPatch);
                break;
            case ALLOTMENT_ARDOUGNE_1:
                FarmAllotment(ArdougneNorthAllotmentPatch);
                break;
            case ALLOTMENT_ARDOUGNE_2:
                FarmAllotment(ArdougneSouthAllotmentPatch);
                break;

            case HERB_TRAVEL_CATHERBY:
                TravelToCatherbyHerbPatch(CatherbyHerbPatch);
                break;
            case HERB_CATHERBY:
                FarmHerbs(CatherbyHerbPatch);
                break;
            case ALLOTMENT_CATHERBY_1:
                FarmAllotment(CatherbyNorthAllotmentPatch);
                break;
            case ALLOTMENT_CATHERBY_2:
                FarmAllotment(CatherbySouthAllotmentPatch);
                break;

            case HERB_TRAVEL_FALADOR:
                TravelToFaladorHerbPatch(FaladorHerbPatch);
                break;
            case HERB_FALADOR:
                FarmHerbs(FaladorHerbPatch);
                break;
            case ALLOTMENT_FALADOR_1:
                FarmAllotment(FaladorNorthAllotmentPatch);
                break;
            case ALLOTMENT_FALADOR_2:
                FarmAllotment(FaladorSouthAllotmentPatch);
                break;

            case HERB_TRAVEL_PORT_PHASMATYS:
                TravelToPortPhasmatysHerbPatch(PortPhasmatysHerbPatch);
                break;
            case HERB_PORT_PHASMATYS:
                FarmHerbs(PortPhasmatysHerbPatch);
                break;
            case ALLOTMENT_PORT_PHASMATYS_1:
                FarmAllotment(PortPhasmatysNorthAllotmentPatch);
                break;
            case ALLOTMENT_PORT_PHASMATYS_2:
                FarmAllotment(PortPhasmatysSouthAllotmentPatch);
                break;

            case HERB_TRAVEL_HOSIDIUS:
                TravelToHosidiusHerbPatch(HosidiusHerbPatch);
                break;
            case HERB_HOSIDIUS:
                FarmHerbs(HosidiusHerbPatch);
                break;
            case ALLOTMENT_HOSIDIUS_1:
                FarmAllotment(HosidiusNorthAllotmentPatch);
                break;
            case ALLOTMENT_HOSIDIUS_2:
                FarmAllotment(HosidiusSouthAllotmentPatch);
                break;

            case HERB_TRAVEL_FARMING_GUILD:
                TravelToFarmingGuildHerbPatch(FarmingGuildHerbPatch);
                break;
            case HERB_FARMING_GUILD:
                FarmHerbs(FarmingGuildHerbPatch);
                break;
            case ALLOTMENT_FARMING_GUILD_1:
                FarmAllotment(FarmingGuildNorthAllotmentPatch);
                break;
            case ALLOTMENT_FARMING_GUILD_2:
                FarmAllotment(FarmingGuildSouthAllotmentPatch);
                break;

            //TREE
            case RESTOCK_TREE:
                RestockTree();
                string_state = treeBankState.name();
                break;
            case TREE_TRAVEL_FALADOR:
                TravelToFaladorTreePatch(FaladorTreePatch);
                break;
            case TREE_FALADOR:
                FarmTrees(FaladorTreePatch);
                break;
            case TREE_TRAVEL_TAVERLEY:
                TravelToTaverlyTreePatch(TaverleyTreePatch);
                break;
            case TREE_TAVERLEY:
                FarmTrees(TaverleyTreePatch);
                break;
            case TREE_TRAVEL_VARROCK:
                TravelToVarrockTreePatch(VarrockTreePatch);
                break;
            case TREE_VARROCK:
                FarmTrees(VarrockTreePatch);
                break;
            case TREE_TRAVEL_LUMBRIDGE:
                TravelToLumbridgeTreePatch(LumbridgeTreePatch);
                break;
            case TREE_LUMBRIDGE:
                FarmTrees(LumbridgeTreePatch);
                break;
            case TREE_TRAVEL_GNOME_STRONGHOLD:
                TravelToGnomeStrongholdTreePatch(GnomeStrongholdTreePatch);
                break;
            case TREE_GNOME_STRONGHOLD:
                FarmTrees(GnomeStrongholdTreePatch);
                break;
            case TREE_TRAVEL_FARMING_GUILD:
                TravelToFarmingGuildTreePatch(FarmingGuildTreePatch);
                break;
            case TREE_FARMING_GUILD:
                FarmTrees(FarmingGuildTreePatch);
                break;

        }
    }
    //Get the next step for the main state machine
    private State getNextState() {
        if (EthanApiPlugin.isMoving() || client.getLocalPlayer().getAnimation() != -1) {
            return State.ANIMATING;
        }
        if (timeout > 0) {
            return State.TIMEOUT;
        }
        if (!herbRun && !treeRun)
            return State.OFF;

        //Handle herb runs
        if (herbRun)
        {
            if (herbBankState != BankState.DONE && !config.debugDisableRestock())
                return State.RESTOCK_HERB;

            if (config.enableArdougne() && ArdougneHerbPatch.State.Index < 2 && !config.debugStateMachine())
                return State.HERB_TRAVEL_ARDOUGNE;
            if (config.enableArdougne() && (ArdougneHerbPatch.State.Index >= 2 || config.debugStateMachine())  &&
                    ArdougneHerbPatch.State != HerbPatchState.DONE)
                return State.HERB_ARDOUGNE;

            if (config.enableArdougne() && config.allotment() != Allotment.NONE &&
                    ArdougneNorthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(ArdougneNorthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    ArdougneNorthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_ARDOUGNE_1;
            }
            if (config.enableArdougne() && config.allotment() != Allotment.NONE &&
                    ArdougneSouthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(ArdougneSouthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    ArdougneSouthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_ARDOUGNE_2;
            }

            if (config.enableCatherby() && CatherbyHerbPatch.State.Index < 2 && !config.debugStateMachine())
                return State.HERB_TRAVEL_CATHERBY;
            if (config.enableCatherby() && (CatherbyHerbPatch.State.Index >= 2 || config.debugStateMachine()) &&
                    CatherbyHerbPatch.State != HerbPatchState.DONE)
                return State.HERB_CATHERBY;

            if (config.enableCatherby() && config.allotment() != Allotment.NONE &&
                    CatherbyNorthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(CatherbyNorthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    CatherbyNorthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_CATHERBY_1;
            }
            if (config.enableCatherby() && config.allotment() != Allotment.NONE &&
                    CatherbySouthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(CatherbySouthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    CatherbySouthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_CATHERBY_2;
            }

            if (config.enableFalador() && FaladorHerbPatch.State.Index < 2 && !config.debugStateMachine())
                return State.HERB_TRAVEL_FALADOR;
            if (config.enableFalador() && (FaladorHerbPatch.State.Index >= 2 || config.debugStateMachine()) &&
                    FaladorHerbPatch.State != HerbPatchState.DONE)
                return State.HERB_FALADOR;

            if (config.enableFalador() && config.allotment() != Allotment.NONE &&
                    FaladorNorthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(FaladorNorthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    FaladorNorthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_FALADOR_1;
            }
            if (config.enableFalador() && config.allotment() != Allotment.NONE &&
                    FaladorSouthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(FaladorSouthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    FaladorSouthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_FALADOR_2;
            }

            if (config.enablePortPhasmatys() && PortPhasmatysHerbPatch.State.Index < 2 && !config.debugStateMachine())
                return State.HERB_TRAVEL_PORT_PHASMATYS;
            if (config.enablePortPhasmatys() && (PortPhasmatysHerbPatch.State.Index >= 2 || config.debugStateMachine()) &&
                    PortPhasmatysHerbPatch.State != HerbPatchState.DONE)
                return State.HERB_PORT_PHASMATYS;

            if (config.enablePortPhasmatys() && config.allotment() != Allotment.NONE &&
                    PortPhasmatysNorthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(PortPhasmatysNorthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    PortPhasmatysNorthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_PORT_PHASMATYS_1;
            }
            if (config.enablePortPhasmatys() && config.allotment() != Allotment.NONE &&
                    PortPhasmatysSouthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(PortPhasmatysSouthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    PortPhasmatysSouthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_PORT_PHASMATYS_2;
            }

            if (config.enableHosidius() && HosidiusHerbPatch.State.Index < 2 && !config.debugStateMachine())
                return State.HERB_TRAVEL_HOSIDIUS;
            if (config.enableHosidius() && (HosidiusHerbPatch.State.Index >= 2 || config.debugStateMachine()) &&
                    HosidiusHerbPatch.State != HerbPatchState.DONE)
                return State.HERB_HOSIDIUS;

            if (config.enableHosidius() && config.allotment() != Allotment.NONE &&
                    HosidiusNorthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(HosidiusNorthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    HosidiusNorthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_HOSIDIUS_1;
            }
            if (config.enableHosidius() && config.allotment() != Allotment.NONE &&
                    HosidiusSouthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(HosidiusSouthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    HosidiusSouthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_HOSIDIUS_2;
            }

            if (config.enableFarmingGuild() && FarmingGuildHerbPatch.State.Index < 2 && !config.debugStateMachine())
                return State.HERB_TRAVEL_FARMING_GUILD;
            if (config.enableFarmingGuild() && (FarmingGuildHerbPatch.State.Index >= 2 || config.debugStateMachine()) &&
                    FarmingGuildHerbPatch.State != HerbPatchState.DONE)
                return State.HERB_FARMING_GUILD;

            if (config.enableFarmingGuild() && config.allotment() != Allotment.NONE &&
                    FarmingGuildNorthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(FarmingGuildNorthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    FarmingGuildNorthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_FARMING_GUILD_1;
            }
            if (config.enableFarmingGuild() && config.allotment() != Allotment.NONE &&
                    FarmingGuildSouthAllotmentPatch.State != AllotmentPatchState.DONE) {
                if(FarmingGuildSouthAllotmentPatch.State.Index < AllotmentPatchState.PROCESS_ALLOTMENT_PATCH.Index)
                    FarmingGuildSouthAllotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;
                return State.ALLOTMENT_FARMING_GUILD_2;
            }

            herbRun = false;
        }

        //Handle tree runs
        if (treeRun)
        {
            if (treeBankState != BankState.DONE && !config.debugDisableRestock())
                return State.RESTOCK_TREE;

            if (config.enableTreeFalador() && FaladorTreePatch.State.Index < 2 && !config.debugStateMachine())
                return State.TREE_TRAVEL_FALADOR;
            if (config.enableTreeFalador() && (FaladorTreePatch.State.Index >= 2 || config.debugStateMachine()) &&
                    FaladorTreePatch.State != TreePatchState.DONE)
                return State.TREE_FALADOR;

            if (config.enableTreeTaverley() && TaverleyTreePatch.State.Index < 2 && !config.debugStateMachine())
                return State.TREE_TRAVEL_TAVERLEY;
            if (config.enableTreeTaverley() && (TaverleyTreePatch.State.Index >= 2 || config.debugStateMachine()) &&
                    TaverleyTreePatch.State != TreePatchState.DONE)
                return State.TREE_TAVERLEY;

            if (config.enableTreeLumbridge() && LumbridgeTreePatch.State.Index < 2 && !config.debugStateMachine())
                return State.TREE_TRAVEL_LUMBRIDGE;
            if (config.enableTreeLumbridge() && (LumbridgeTreePatch.State.Index >= 2 || config.debugStateMachine()) &&
                    LumbridgeTreePatch.State != TreePatchState.DONE)
                return State.TREE_LUMBRIDGE;

            if (config.enableTreeVarrock() && VarrockTreePatch.State.Index < 2 && !config.debugStateMachine())
                return State.TREE_TRAVEL_VARROCK;
            if (config.enableTreeVarrock() && (VarrockTreePatch.State.Index >= 2 || config.debugStateMachine()) &&
                    VarrockTreePatch.State != TreePatchState.DONE)
                return State.TREE_VARROCK;

            if (config.enableTreeGnomeStronghold() && GnomeStrongholdTreePatch.State.Index < 2 && !config.debugStateMachine())
                return State.TREE_TRAVEL_GNOME_STRONGHOLD;
            if (config.enableTreeGnomeStronghold() && (GnomeStrongholdTreePatch.State.Index >= 2 || config.debugStateMachine()) &&
                    GnomeStrongholdTreePatch.State != TreePatchState.DONE)
                return State.TREE_GNOME_STRONGHOLD;

            if (config.enableTreeFarmingGuild() && FarmingGuildTreePatch.State.Index < 2 && !config.debugStateMachine())
                return State.TREE_TRAVEL_FARMING_GUILD;
            if (config.enableTreeFarmingGuild() && (FarmingGuildTreePatch.State.Index >= 2 || config.debugStateMachine()) &&
                    FarmingGuildTreePatch.State != TreePatchState.DONE)
                return State.TREE_FARMING_GUILD;

            treeRun = false;
        }
        return State.OFF;
    }

    //------------------------------------- Herbs -------------------------------------
    //Get the next herb step for the herb state machine
    private HerbPatchState FarmHerbsState(HerbPatch herbPatch) {
        //Check if there is any weeds in the inventory and drop them if there is
        if (Inventory.search().withName("Weeds").first().isPresent())
            return HerbPatchState.EMPTY_INVENTORY;

        //Check if inventory is full and note any herbs that are in the invetory
        if (Inventory.full())
        {
            if (Inventory.search().withAction("Clean").onlyUnnoted().first().isPresent() && config.cleanHerbs())
                return HerbPatchState.CLEAN_HERBS;
            if (Inventory.search().nameContains(config.herb().HerbName).onlyUnnoted().first().isPresent())
                return HerbPatchState.MANAGE_INVENTORY;
            else
                Stop("Invetory full");
        }

        //Check if the patch is not fully grown then mark the patch as done
        if (TileObjects.search().withName("Herbs").first().isPresent() && herbPatch.State != HerbPatchState.PLANTING)
            if (!Arrays.asList(TileObjectQuery.getObjectComposition(TileObjects.search().withName("Herbs").first().get()).getActions()).contains("Pick"))
                return HerbPatchState.NOTE;

        //Harvest herbs if there is any
        if (TileObjects.search().withName("Herbs").withAction("Pick").first().isPresent())
            return HerbPatchState.HARVEST;

        if (Inventory.search().withAction("Clean").onlyUnnoted().first().isPresent() && config.cleanHerbs())
            return HerbPatchState.CLEAN_HERBS;

        //Clear dead herbs
        if (TileObjects.search().withName("Dead herbs").withAction("Clear").first().isPresent())
            return HerbPatchState.CLEAR;

        //Rake herb patch
        if (TileObjects.search().nameContains("Herb patch").withAction("Rake").first().isPresent()){
            return HerbPatchState.RAKE;
        }

        //Check if herb patch is ready for planting
        if (TileObjects.search().nameContains("Herb patch").withAction("Inspect").first().isPresent()) {
            if (Inventory.search().withName(config.herb().SeedName).first().isPresent())
                return HerbPatchState.PLANTING;
        }

        //Use compost on the herbs
        if (TileObjects.search().nameContains("Herbs").withAction("Inspect").first().isPresent()) {
            if (config.bottomlessBucket())
            {
                if (Inventory.search().withName(BottomlessCompostBucket).first().isPresent())
                    return HerbPatchState.COMPOST;
            }
            else
            {
                if (Inventory.search().withName(config.compost().Name).first().isPresent())
                    return HerbPatchState.COMPOST;
            }
        }

        //PROCESS_HERB_PATCH is doesen't do anything, it just indicates to the herb state machine that it should start
        return HerbPatchState.PROCESS_HERB_PATCH;
    }

    //Execute herb state machine
    private void FarmHerbs(HerbPatch herbPatch) {
        if (config.debugStateMachine() && herbPatch.State.Index < 2)
            herbPatch.State = HerbPatchState.PROCESS_HERB_PATCH;

        if (herbPatch.State.Index < 2 || herbPatch.State == HerbPatchState.DONE)
            return;

        //Get the next state
        herbPatch.State = FarmHerbsState(herbPatch);
        string_state = herbPatch.State.name();

        //Set the display variable so the user can see whats going on
        SetDisplayStateHerb(herbPatch);

        if (config.debugStateMachine())
            return;

        //Herb state machine
        switch (herbPatch.State)
        {
            //Harvest herbs if there is any
            case HARVEST:
                TileObjects.search().withName("Herbs").withAction("Pick").first().ifPresent(herb -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(herb, "Pick");
                });
                break;
            case CLEAN_HERBS:
                List<Widget> grimyHerbs = Inventory.search().withAction("Clean").onlyUnnoted().result();
                for (int i = 0; i < config.cleanHerbsTick() && i < grimyHerbs.size(); i++) {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(grimyHerbs.get(i), "Clean");
                }
                break;
            //Clear out dead herbs
            case CLEAR:
                //Plant new herbs
                TileObjects.search().nameContains("Dead herbs").withAction("Clear").first().ifPresent(tileObject -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject, "Clear");
                });
                break;
            //Rake the herb patch
            case RAKE:
                TileObjects.search().nameContains("Herb patch").withAction("Rake").first().ifPresent(tileObject -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject, "Rake");
                });
                break;
            //Plant herbs on herb patch
            case PLANTING:
                TileObjects.search().nameContains("Herb patch").withAction("Inspect").first().ifPresent(tileObject -> {
                    Inventory.search().withName(config.herb().SeedName).first().ifPresent(item -> {
                        MousePackets.queueClickPacket();
                        MousePackets.queueClickPacket();
                        ObjectPackets.queueWidgetOnTileObject(item, tileObject);
                    });
                });
                break;
            //Use compost on the herbs
            case COMPOST:
                TileObjects.search().nameContains("Herbs").withAction("Inspect").first().ifPresent(tileObject -> {
                    if (CompostTileObject(tileObject))
                        herbPatch.State = HerbPatchState.NOTE;
                });
                break;
            //Check if there is any weeds in the inventory and drop them if there is
            case EMPTY_INVENTORY:
                Inventory.search().withName("Weeds").first().ifPresent(weeds -> {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(weeds, "Drop");
                });
                break;
            //Note herbs on Tool Leprechaun
            case NOTE:
                if (config.cleanHerbs())
                {
                    Inventory.search().withName(config.herb().HerbName).onlyUnnoted().first().ifPresentOrElse(herbs -> {
                        NPCs.search().nameContains("Tool").nearestToPlayer().ifPresent(leprechaun -> {
                            MousePackets.queueClickPacket();
                            NPCPackets.queueWidgetOnNPC(leprechaun, herbs);
                        });
                    }, () -> {
                        //After noting set the patch to done
                        herbPatch.State = HerbPatchState.DONE;
                    });
                }
                else
                {
                    Inventory.search().nameContains("Grimy").withAction("Clean").onlyUnnoted().first().ifPresentOrElse(herbs -> {
                        NPCs.search().nameContains("Tool").nearestToPlayer().ifPresent(leprechaun -> {
                            MousePackets.queueClickPacket();
                            NPCPackets.queueWidgetOnNPC(leprechaun, herbs);
                        });
                    }, () -> {
                        //After noting set the patch to done
                        herbPatch.State = HerbPatchState.DONE;
                    });
                }

                break;
            case MANAGE_INVENTORY:
                if (config.cleanHerbs())
                {
                    Inventory.search().withName(config.herb().HerbName).onlyUnnoted().first().ifPresent(herbs -> {
                        NPCs.search().nameContains("Tool").nearestToPlayer().ifPresent(leprechaun -> {
                            MousePackets.queueClickPacket();
                            NPCPackets.queueWidgetOnNPC(leprechaun, herbs);
                        });
                    });
                }
                else
                {
                    Inventory.search().nameContains("Grimy").withAction("Clean").onlyUnnoted().first().ifPresent(herbs -> {
                        NPCs.search().nameContains("Tool").nearestToPlayer().ifPresent(leprechaun -> {
                            MousePackets.queueClickPacket();
                            NPCPackets.queueWidgetOnNPC(leprechaun, herbs);
                        });
                    });
                }
                break;

        }
        //Set the random timeout when nessecery
        if (herbPatch.State != HerbPatchState.PROCESS_HERB_PATCH && herbPatch.State != HerbPatchState.EMPTY_INVENTORY)
            setTimeout();
    }

    //------------------------------------- Allotment -------------------------------------
    //Get the next allotment step for the herb state machine
    private AllotmentPatchState FarmAllotmentState(AllotmentPatch allotmentPatch, WorldPoint min, WorldPoint max) {
        //Check if there is any weeds in the inventory and drop them if there is
        if (Inventory.search().withName("Weeds").first().isPresent())
            return AllotmentPatchState.EMPTY_INVENTORY;

        //Check if inventory is full and note any allotments that are in the invetory
        if (Inventory.full())
        {
            if (Inventory.search().nameContains(config.allotment().PlantName).onlyUnnoted().first().isPresent())
                return AllotmentPatchState.MANAGE_INVENTORY;
            else
                Stop("Invetory full");
        }

        //Check if the patch is not fully grown then mark the patch as done
        if (TileObjects.search().withinBounds(min, max).withName("plant").first().isPresent() &&
                allotmentPatch.State.Index < AllotmentPatchState.PLANTING.Index)
            if (!Arrays.asList(TileObjectQuery.getObjectComposition(TileObjects.search().withName("plant").first().get()).getActions()).contains("Pick"))
                return AllotmentPatchState.NOTE;

        //Harvest allotments if there is any
        if (TileObjects.search().withinBounds(min, max).withAction("Harvest").first().isPresent())
            return AllotmentPatchState.HARVEST;

        //Clear dead allotments
        if (TileObjects.search().withinBounds(min, max).withAction("Clear").first().isPresent())
            return AllotmentPatchState.CLEAR;

        //Rake allotment patch
        if (TileObjects.search().withinBounds(min, max).withAction("Rake").first().isPresent() &&
            Inventory.search().withName(config.allotment().SeedName).first().isPresent())
            return AllotmentPatchState.RAKE;


        //Check if allotment patch is ready for planting
        if (TileObjects.search().withinBounds(min, max).nameContains("Allotment").withAction("Inspect").first().isPresent())
            if (Inventory.search().withName(config.allotment().SeedName).first().isPresent())
            {
                if (Inventory.search().withName(config.allotment().SeedName).first().get().getItemQuantity() >= 3)
                    return AllotmentPatchState.PLANTING;
            }


        //Use compost on the allotments
        if ((   TileObjects.search().withinBounds(min, max).nameContains("seedling").withAction("Inspect").first().isPresent() ||
                TileObjects.search().withinBounds(min, max).nameContains("plant").withAction("Inspect").first().isPresent()) &&
                allotmentPatch.State.Index <= AllotmentPatchState.COMPOST.Index) {
            if (config.bottomlessBucket())
            {
                if (Inventory.search().withName(BottomlessCompostBucket).first().isPresent())
                    return AllotmentPatchState.COMPOST;
            }
            else
            {
                if (Inventory.search().withName(config.compost().Name).first().isPresent())
                    return AllotmentPatchState.COMPOST;
            }
        }

        //Pay to protect
        if ((   TileObjects.search().withinBounds(min, max).nameContains("seedling").withAction("Inspect").nearestToPlayer().isPresent() ||
                TileObjects.search().withinBounds(min, max).nameContains("plant").withAction("Inspect").nearestToPlayer().isPresent()) &&
                allotmentPatch.State.Index <= AllotmentPatchState.PROTECT.Index &&
                !config.whiteLilly())
            return AllotmentPatchState.PROTECT;

        if (Inventory.search().nameContains(config.allotment().PlantName).onlyUnnoted().first().isPresent())
            return AllotmentPatchState.NOTE;

        //PROCESS_HERB_PATCH is doesen't do anything, it just indicates to the allotment state machine that it should start
        return AllotmentPatchState.DONE;
    }

    //Execute Allotment state machine
    private void FarmAllotment(AllotmentPatch allotmentPatch) {

        if (config.debugStateMachine() && allotmentPatch.State.Index < 2)
            allotmentPatch.State = AllotmentPatchState.PROCESS_ALLOTMENT_PATCH;

        if (allotmentPatch.State.Index < 2 || allotmentPatch.State == AllotmentPatchState.DONE)
            return;

        WorldPoint min = new WorldPoint(
                allotmentPatch.AllotmentPatchArea.getX(),
                allotmentPatch.AllotmentPatchArea.getY(),
                allotmentPatch.AllotmentPatchArea.getPlane());

        WorldPoint max = new WorldPoint(
                allotmentPatch.AllotmentPatchArea.getX() + allotmentPatch.AllotmentPatchArea.getWidth(),
                allotmentPatch.AllotmentPatchArea.getY() + allotmentPatch.AllotmentPatchArea.getHeight(),
                allotmentPatch.AllotmentPatchArea.getPlane());

        //Get the next state
        allotmentPatch.State = FarmAllotmentState(allotmentPatch, min, max);
        string_state = allotmentPatch.State.name();

        //Set the display variable so the user can see whats going on
        SetDisplayStateAllotment(allotmentPatch);

        if (config.debugStateMachine())
            return;

        //allotment state machine
        switch (allotmentPatch.State)
        {
            //Harvest allotments if there is any
            case HARVEST:
                TileObjects.search().withinBounds(min, max).withAction("Harvest").first().ifPresent(allotment -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(allotment, "Harvest");
                });
                break;
            //Clear out dead allotments
            case CLEAR:
                //Plant new allotments
                TileObjects.search().withinBounds(min, max).withAction("Clear").first().ifPresent(tileObject -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject, "Clear");
                });
                break;
            //Rake the allotment patch
            case RAKE:
                TileObjects.search().withinBounds(min, max).withAction("Rake").first().ifPresent(tileObject -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject, "Rake");
                });
                break;
            //Plant allotments on allotment patch
            case PLANTING:
                TileObjects.search().withinBounds(min, max).nameContains("Allotment").withAction("Inspect").first().ifPresent(tileObject -> {
                    Inventory.search().withName(config.allotment().SeedName).first().ifPresent(item -> {
                        MousePackets.queueClickPacket();
                        MousePackets.queueClickPacket();
                        ObjectPackets.queueWidgetOnTileObject(item, tileObject);
                    });
                });
                break;
            //Use compost on the allotments
            case COMPOST:
                TileObjects.search().withinBounds(min, max).nameContains("seedling").withAction("Inspect").first().ifPresent(allotment -> {
                    if (CompostTileObject(allotment))
                        allotmentPatch.State = AllotmentPatchState.PROTECT;
                });
                TileObjects.search().withinBounds(min, max).nameContains("plant").withAction("Inspect").first().ifPresent(allotment -> {
                    if (CompostTileObject(allotment))
                        allotmentPatch.State = AllotmentPatchState.PROTECT;
                });
                break;
            //Check if there is any weeds in the inventory and drop them if there is
            case EMPTY_INVENTORY:
                Inventory.search().withName("Weeds").first().ifPresent(weeds -> {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(weeds, "Drop");
                });
                break;
            case PROTECT:
                NPCs.search().withAction(allotmentPatch.NPCInteractionWord).nearestToPlayer().ifPresent(npc -> {
                    Widgets.search().withTextContains("Pay").hiddenState(false).first().ifPresentOrElse(payWidget -> {
                        MousePackets.queueClickPacket();
                        WidgetPackets.queueResumePause(payWidget.getId(), 1);
                    }, () -> {
                        MousePackets.queueClickPacket();
                        NPCInteraction.interact(npc, allotmentPatch.NPCInteractionWord);
                    });
                    Widgets.search().withTextContains("Leave it with me").hiddenState(false).first().ifPresent(widget -> {
                        allotmentPatch.State = AllotmentPatchState.DONE;
                    });
                    Widgets.search().withTextContains("already looking after that").hiddenState(false).first().ifPresent(widget -> {
                        allotmentPatch.State = AllotmentPatchState.NOTE;
                    });
                });
                break;
            //Note allotments on Tool Leprechaun
            case NOTE:
                Inventory.search().withName(config.allotment().PlantName).onlyUnnoted().first().ifPresentOrElse(herbs -> {
                    NPCs.search().nameContains("Tool").nearestToPlayer().ifPresent(leprechaun -> {
                        MousePackets.queueClickPacket();
                        NPCPackets.queueWidgetOnNPC(leprechaun, herbs);
                    });
                }, () -> {
                    //After noting set the patch to done
                    allotmentPatch.State = AllotmentPatchState.DONE;
                });
                break;
            case MANAGE_INVENTORY:
                    Inventory.search().withName(config.allotment().PlantName).onlyUnnoted().first().ifPresent(herbs -> {
                        NPCs.search().nameContains("Tool").nearestToPlayer().ifPresent(leprechaun -> {
                            MousePackets.queueClickPacket();
                            NPCPackets.queueWidgetOnNPC(leprechaun, herbs);
                        });
                    });
                break;

        }
        //Set the random timeout when nessecery
        if (allotmentPatch.State != AllotmentPatchState.PROCESS_ALLOTMENT_PATCH &&
                allotmentPatch.State != AllotmentPatchState.EMPTY_INVENTORY &&
                allotmentPatch.State != AllotmentPatchState.HARVEST)
            setTimeout();
    }

    //------------------------------------- Trees -------------------------------------
    //Get the next tree step for the herb state machine
    private TreePatchState FarmTreeState(TreePatch treePatch, WorldPoint min, WorldPoint max){
        if (Inventory.search().withName("Weeds").first().isPresent())
            return TreePatchState.EMPTY_INVENTORY;

        //Check health of tree
        if (TileObjects.search().withinBounds(min, max).withAction("Check-health").nearestToPlayer().isPresent())
            return TreePatchState.CHECK_HEALTH;
        //Pay gardener
        if (TileObjects.search().withinBounds(min, max).withAction("Chop down").nearestToPlayer().isPresent())
            return TreePatchState.PAY;
        //Rake patch
        if (TileObjects.search().withName("Tree patch").withAction("Rake").nearestToPlayer().isPresent()){
            return TreePatchState.RAKE;
        }
        //Plant sapling
        if (TileObjects.search().withName("Tree patch").withAction("Inspect").nearestToPlayer().isPresent())
            if (Inventory.search().withName(config.tree().Sapling).first().isPresent())
                return TreePatchState.PLANT;
        //Pay to protect
        if (TileObjects.search().withinBounds(min, max).nameContains("sapling").withAction("Inspect").nearestToPlayer().isPresent() ||
            TileObjects.search().withinBounds(min, max).nameContains("tree").withAction("Inspect").nearestToPlayer().isPresent())
            return TreePatchState.PROTECT;

        //PROCESS_TREE_PATCH is doesen't do anything, it just indicates to the herb state machine that it should start
        return TreePatchState.PROCESS_TREE_PATCH;
    }

    //Execute tree state machine
    private void FarmTrees(TreePatch treePatch) {
        if (config.debugStateMachine() && treePatch.State.Index < 2)
            treePatch.State = TreePatchState.PROCESS_TREE_PATCH;

        if (treePatch.State.Index < 2 || treePatch.State == TreePatchState.DONE)
            return;

        WorldPoint min = new WorldPoint(
                treePatch.TreePatchArea.getX(),
                treePatch.TreePatchArea.getY(),
                treePatch.TreePatchArea.getPlane());

        WorldPoint max = new WorldPoint(
                treePatch.TreePatchArea.getX() + treePatch.TreePatchArea.getWidth(),
                treePatch.TreePatchArea.getY() + treePatch.TreePatchArea.getHeight(),
                treePatch.TreePatchArea.getPlane());

        //Get the next state
        treePatch.State = FarmTreeState(treePatch, min, max);
        string_state = treePatch.State.name();

        //Set the display variable so the user can see whats going on
        SetDisplayStateTree(treePatch);

        if (config.debugStateMachine())
            return;

        //Tree state machine
        switch (treePatch.State)
        {
            //Empty the inventory of any weeds
            case EMPTY_INVENTORY: //Check if there is any weeds in the inventory and drop them if there is
                Inventory.search().withName("Weeds").first().ifPresent(weeds -> {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(weeds, "Drop");
                });
                break;
            //Rake the tree patch
            case RAKE:
                TileObjects.search().withName("Tree patch").withAction("Rake").nearestToPlayer().ifPresent(tileObject -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tileObject, "Rake");
                });
                break;
            //Check the health of the tree
            case CHECK_HEALTH:
                TileObjects.search().withinBounds(
                        new WorldPoint(
                                treePatch.TreePatchArea.getX(),
                                treePatch.TreePatchArea.getY(),
                                treePatch.TreePatchArea.getPlane()),
                        new WorldPoint(
                                treePatch.TreePatchArea.getX() + treePatch.TreePatchArea.getWidth(),
                                treePatch.TreePatchArea.getY() + treePatch.TreePatchArea.getHeight(),
                                treePatch.TreePatchArea.getPlane())
                ).withAction("Check-health").nearestToPlayer().ifPresent(tree -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(tree, "Check-health");
                });
                break;
            //Pay to get the tree removed
            case PAY:
                NPCs.search().withAction(treePatch.Name != "Farming Guild" ? "Pay" : "Pay (tree patch)").nearestToPlayer().ifPresent(npc -> {
                    Widgets.search().withTextContains("tree chopped down").hiddenState(false).first().ifPresentOrElse(payWidget -> {
                        MousePackets.queueClickPacket();
                        WidgetPackets.queueResumePause(payWidget.getId(), 1);
                    }, () -> {
                        MousePackets.queueClickPacket();
                        NPCInteraction.interact(npc, treePatch.Name != "Farming Guild" ? "Pay" : "Pay (tree patch)");
                    });
                });
                break;
            //Plant a new sapling
            case PLANT:
                TileObjects.search().withName("Tree patch").withAction("Inspect").nearestToPlayer().ifPresent(patch -> {
                    Inventory.search().nameContains(config.tree().Sapling).first().ifPresent(item -> {
                        MousePackets.queueClickPacket();
                        MousePackets.queueClickPacket();
                        ObjectPackets.queueWidgetOnTileObject(item, patch);
                    });
                });
                break;
            //Pay to have the tree protected
            case PROTECT:
                NPCs.search().withAction(treePatch.Name != "Farming Guild" ? "Pay" : "Pay (tree patch)").nearestToPlayer().ifPresent(npc -> {
                    if (Widgets.search().withTextContains("Leave it with me").hiddenState(false).first().isPresent() ||
                            Widgets.search().withTextContains("already looking after that patch").hiddenState(false).first().isPresent())
                    {
                        treePatch.State = TreePatchState.DONE;
                        return;
                    }
                    //Pay X item?
                    String cleanProtItem = config.tree().ProtectionItem.toLowerCase();
                    if (cleanProtItem.contains("("))
                        cleanProtItem.substring(0, cleanProtItem.length() - 3);
                    Widgets.search().withTextContains("Pay").withTextContains(cleanProtItem).hiddenState(false).first().ifPresentOrElse(payWidget -> {
                        MousePackets.queueClickPacket();
                        WidgetPackets.queueResumePause(payWidget.getId(), 1);
                    }, () -> {
                        MousePackets.queueClickPacket();
                        NPCInteraction.interact(npc, treePatch.Name != "Farming Guild" ? "Pay" : "Pay (tree patch)");
                    });

                });
                break;
        }

        //Set the random timeout when nessecery
        if (treePatch.State != TreePatchState.PROCESS_TREE_PATCH && treePatch.State != TreePatchState.EMPTY_INVENTORY && treePatch.State != TreePatchState.PAY && treePatch.State != TreePatchState.PROTECT)
            setTimeout();
    }

    private boolean CompostTileObject(TileObject tileObject)
    {

        if (config.bottomlessBucket())
        {
            Optional<Widget> bottemlessBucket = Inventory.search().withName(BottomlessCompostBucket).first();

            if (bottemlessBucket.isPresent())
            {
                MousePackets.queueClickPacket();
                MousePackets.queueClickPacket();
                ObjectPackets.queueWidgetOnTileObject(bottemlessBucket.get(), tileObject);
                return true;
            }
        }
        else
        {
            Optional<Widget> compost = Inventory.search().withName(config.compost().Name).first();

            if (compost.isPresent())
            {
                MousePackets.queueClickPacket();
                MousePackets.queueClickPacket();
                ObjectPackets.queueWidgetOnTileObject(compost.get(), tileObject);
                return true;
            }
        }
        return false;
    }

    //------------------------------------- Bank -------------------------------------
    //Checks if an item is in the keepItems list
    private boolean CompareItem(List<String> keepItems, String compItem) {
        for (String item : keepItems)
        {
            if (compItem.contains(item))
                return true;
        }
        return false;
    }

    //Restocks on anything that is needed for the herb run
    private void RestockHerb() {

        if (herbBankState == BankState.TELEPORT)
        {
            TeleportToBank();
            herbBankState = BankState.DEPOSIT;
            return;
        }
        //Check if bank is open
        if (Bank.isOpen()) {
            //Add all items that are needed to the keepItems List
            ArrayList<String> keepItems = new ArrayList<String>(Arrays.asList(HerbTools));
            keepItems.addAll(Arrays.asList(config.additionalItems().split(",")));
            keepItems.add(config.herb().SeedName);

            if (config.bottomlessBucket())
                keepItems.add(BottomlessCompostBucket);
            else
                keepItems.add(config.compost().Name);

            //Depending on what patch is selected differente tools and teleports might be needed
            if (config.enableArdougne())
                keepItems.addAll(Arrays.asList(ArdougneHerbPatch.Tools));
            if (config.enableCatherby())
                keepItems.addAll(Arrays.asList(CatherbyHerbPatch.Tools));
            if (config.enableCivitasIllaFortis())
                keepItems.addAll(Arrays.asList(CivitasIllaFortisHerbPatch.Tools));
            if (config.enableFalador())
                keepItems.addAll(Arrays.asList(FaladorHerbPatch.Tools));
            if (config.enableFarmingGuild())
                keepItems.addAll(Arrays.asList(FarmingGuildHerbPatch.Tools));
            if (config.enableHarmonyIsland())
                keepItems.addAll(Arrays.asList(HarmonyIslandHerbPatch.Tools));
            if (config.enableHosidius())
                keepItems.addAll(Arrays.asList(HosidiusHerbPatch.Tools));
            if (config.enablePortPhasmatys())
                keepItems.addAll(Arrays.asList(PortPhasmatysHerbPatch.Tools));
            if (config.enableTrollStronghold())
                keepItems.addAll(Arrays.asList(TrollStrongholdHerbPatch.Tools));
            if (config.enableWeiss())
                keepItems.addAll(Arrays.asList(WeissHerbPatch.Tools));

            //Count how many seeds and compost are needed
            int patches = 0;
            patches += config.enableArdougne() ? 1 : 0;
            patches += config.enableCatherby() ? 1 : 0;
            patches += config.enableCivitasIllaFortis() ? 1 : 0;
            patches += config.enableFalador() ? 1 : 0;
            patches += config.enableFarmingGuild() ? 1 : 0;
            patches += config.enableHarmonyIsland() ? 1 : 0;
            patches += config.enableHosidius() ? 1 : 0;
            patches += config.enablePortPhasmatys() ? 1 : 0;
            patches += config.enableTrollStronghold() ? 1 : 0;
            patches += config.enableWeiss() ? 1 : 0;

            //Banking state machine
            switch (herbBankState)
            {
                //Deposit items that are not needed
                case DEPOSIT:
                    List<Widget> bankInv = BankInventory.search().filter(widget -> !CompareItem(keepItems, widget.getName())).result();
                    for (Widget item : bankInv) {
                        MousePackets.queueClickPacket();
                        BankInventoryInteraction.useItem(item, "Deposit-All");
                    }
                    herbBankState = BankState.WITHDRAW;
                    break;
                //Withdraw the requierd items in non noted form
                case WITHDRAW:
                    if (!getWithdrawNotes())
                    {
                        WithdrawBankTeleport();
                        //Take out basic tools
                        for (String tool : HerbTools) {
                            if (!WithdrawItemFromBank(tool, 1)) {
                                Stop("Missing " + tool + " in bank");
                                return;
                            }
                        }

                        //Take out seeds
                        if (!WithdrawItemFromBank(config.herb().SeedName, patches)) {
                            Stop("Missing " + config.herb().name() + " in bank");
                            return;
                        }

                        if (config.allotment() != Allotment.NONE)
                        {
                            //Take out seeds
                            if (!WithdrawItemFromBank(config.allotment().SeedName, patches * 3 * 2)) {
                                Stop("Missing " + config.herb().name() + " in bank");
                                return;
                            }
                            if (!config.whiteLilly())
                                if (!WithdrawItemFromBank(config.allotment().ProtectionItem, patches * config.allotment().ProtectionItemAmount * 2))
                                {
                                    Stop("Missing " + config.herb().name() + " in bank");
                                    return;
                                }
                        }

                        //Take out compost
                        if (config.bottomlessBucket())
                        {
                            if (!WithdrawItemFromBank(BottomlessCompostBucket, 1)) {
                                Stop("Missing " + BottomlessCompostBucket + " in bank");
                                return;
                            }
                        }
                        else
                        {
                            if (!WithdrawItemFromBank(config.compost().Name, patches)) {
                                Stop("Missing " + config.compost() + " in bank");
                                return;
                            }
                        }



                        //Take out additional items
                        String[] additionalItems = config.additionalItems().split(",");
                        for (String tool : additionalItems) {
                            if (!WithdrawItemFromBank(tool, 1)) {
                                Stop("Missing " + tool + " in bank");
                                return;
                            }
                        }

                        //Take out tools that are needed for the Ardougne herb patch
                        if (config.enableArdougne())
                        {
                            for (String tool : ArdougneHerbPatch.Tools)
                            {
                                if (!WithdrawItemFromBank(tool, 1)) {
                                    Stop("Missing " + tool + " in bank");
                                    return;
                                }
                            }

                            for (int i = 4; i >= 2; i--) {
                                if (WithdrawItemFromBank("Ardougne cloak " + i, 1)) {
                                    break;
                                }
                            }
                        }

                        //Take out tools that are needed for the Catherby herb patch
                        if (config.enableCatherby())
                        {
                            for (String tool : CatherbyHerbPatch.Tools)
                            {
                                if (!WithdrawItemFromBank(tool, 1)) {
                                    Stop("Missing " + tool + " in bank");
                                    return;
                                }
                            }
                        }

                        //Take out tools that are needed for the Falador herb patch
                        if (config.enableFalador())
                        {
                            for (String tool : FaladorHerbPatch.Tools)
                            {
                                if (!WithdrawItemFromBank(tool, 1)) {
                                    Stop("Missing " + tool + " in bank");
                                    return;
                                }
                            }

                            for (int i = 4; i >= 2; i--) {
                                if (WithdrawItemFromBank("Explorer's ring " + i, 1)) {
                                    break;
                                }
                            }
                        }
                        if (config.enablePortPhasmatys())
                        {
                            for (String tool : PortPhasmatysHerbPatch.Tools)
                            {
                                if (!WithdrawItemFromBank(tool, 1)) {
                                    Stop("Missing " + tool + " in bank");
                                    return;
                                }
                            }
                        }
                        //Take out tools that are needed for the Ardougne herb patch
                        if (config.enableFarmingGuild())
                        {
                            for (int i = 1; i <= 6; i++)
                            {
                                if (WithdrawItemFromBank("Skills necklace(" + i + ")", 1))
                                {
                                    break;
                                }
                                if (i == 6)
                                    Stop("Could not find Skills necklace in bank");
                            }

                        }
                        herbBankState = BankState.CHECK;
                    }
                    else
                    {
                        setWithdrawNotes(false);
                    }
                    break;
                //Withdraw items that are needed in noted form
                case WITHDRAW_NOTED:
                    if (getWithdrawNotes())
                    {

                    }
                    else
                    {
                        setWithdrawNotes(true);
                    }
                    break;
                //Double check that we have all requierd items
                case CHECK:
                    boolean hasAllItems = true;
                    for (String item : keepItems)
                    {
                        if (Inventory.search().nameContains(item).first().isEmpty())
                        {
                            hasAllItems = false;
                            break;
                        }
                    }
                    //Set the banking state to done
                    herbBankState = hasAllItems ? BankState.DONE : BankState.DEPOSIT;
                    break;
            }
            setTimeout();
        }
        else {
            OpenBank();
        }
    }

    //Restocks on anything that is needed for the herb run
    private void RestockTree() {

        if (treeBankState == BankState.TELEPORT)
        {
            TeleportToBank();
            treeBankState = BankState.DEPOSIT;
            return;
        }

        //Check if bank is open
        if (Bank.isOpen()) {
            //Add all items that are needed to the keepItems List
            ArrayList<String> keepItems = new ArrayList<String>(Arrays.asList(TreeTools));
            keepItems.addAll(Arrays.asList(config.additionalItems().split(",")));
            keepItems.add(config.tree().Sapling);
            keepItems.add(config.tree().ProtectionItem);
            keepItems.add("Coins");

            //Depending on what patch is selected differente tools and teleports might be needed
            if (config.enableTreeFalador())
                keepItems.addAll(Arrays.asList(FaladorTreePatch.Tools));
            if (config.enableTreeLumbridge())
                keepItems.addAll(Arrays.asList(LumbridgeTreePatch.Tools));
            if (config.enableTreeTaverley())
                keepItems.addAll(Arrays.asList(TaverleyTreePatch.Tools));
            if (config.enableTreeVarrock())
                keepItems.addAll(Arrays.asList(VarrockTreePatch.Tools));
            if (config.enableTreeFarmingGuild())
                keepItems.addAll(Arrays.asList(FarmingGuildTreePatch.Tools));
            if (config.enableTreeGnomeStronghold())
                keepItems.addAll(Arrays.asList(GnomeStrongholdTreePatch.Tools));

            //Count how many saplings are needed
            int patches = 0;
            patches += config.enableTreeFalador() ? 1 : 0;
            patches += config.enableTreeLumbridge() ? 1 : 0;
            patches += config.enableTreeTaverley() ? 1 : 0;
            patches += config.enableTreeVarrock() ? 1 : 0;
            patches += config.enableTreeFarmingGuild() ? 1 : 0;
            patches += config.enableTreeGnomeStronghold() ? 1 : 0;

            //Banking state machine
            switch (treeBankState){

                //Deposit items that are not needed
                case DEPOSIT:
                    List<Widget> bankInv = BankInventory.search().filter(widget -> !CompareItem(keepItems, widget.getName())).result();
                    for (Widget item : bankInv) {
                        MousePackets.queueClickPacket();
                        BankInventoryInteraction.useItem(item, "Deposit-All");
                    }
                    treeBankState = BankState.WITHDRAW;
                    break;
                //Withdraw items in non noted form
                case WITHDRAW:
                    if (!getWithdrawNotes())
                    {
                        WithdrawBankTeleport();

                        //Take out basic tools
                        for (String tool : TreeTools) {
                            if (!WithdrawItemFromBank(tool, 1)) {
                                Stop("Missing " + tool + " in bank");
                                return;
                            }
                        }

                        //Take out seeds
                        if (!WithdrawItemFromBank(config.tree().Sapling, patches)) {
                            Stop("Missing " + config.tree().Sapling + " in bank");
                            return;
                        }

                        //Take out coins
                        if (!WithdrawItemFromBank("Coins", 200 * patches)) {
                            Stop("Missing " + 200 * patches + " coins in bank you broke fuck");
                            return;
                        }

                        //Take out additional items
                        String[] additionalItems = config.additionalItems().split(",");
                        for (String tool : additionalItems) {
                            if (!WithdrawItemFromBank(tool, 1)) {
                                Stop("Missing " + tool + " in bank");
                                return;
                            }
                        }

                        //Take out tools that are needed for the Ardougne herb patch
                        if (config.enableTreeFarmingGuild())
                        {
                            for (int i = 1; i <= 6; i++)
                            {
                                if (WithdrawItemFromBank("Skills necklace(" + i + ")", 1))
                                {
                                    break;
                                }
                                if (i == 6)
                                    Stop("Could not find Skills necklace in bank");
                            }

                        }
                        treeBankState = BankState.WITHDRAW_NOTED;
                    }
                    else
                    {
                        setWithdrawNotes(false);
                    }
                    break;
                //Withdraw items in noted form
                case WITHDRAW_NOTED:
                    if (getWithdrawNotes())
                    {
                        if (getWithdrawNotes())
                        {
                            //Take protection items
                            if (!WithdrawItemFromBank(config.tree().ProtectionItem, config.tree().AmountOfProtectionItem * patches)) {
                                Stop("Missing " + config.tree().ProtectionItem + " in bank");
                                return;
                            }
                        }
                        treeBankState = BankState.CHECK;

                    }
                    else
                    {
                        setWithdrawNotes(true);
                    }
                    break;
                //Check that we have all items
                case CHECK:
                    boolean hasAllItems = true;
                    for (String item : keepItems)
                    {
                        if (Inventory.search().nameContains(item).first().isEmpty())
                        {
                            hasAllItems = false;
                            break;
                        }
                    }
                    treeBankState = hasAllItems ? BankState.DONE : BankState.DEPOSIT;
                    break;
            }
            setTimeout();
        }
        else {
            OpenBank();
        }
    }

    //Withdraws a teleport from the bank to a bank
    private void WithdrawBankTeleport() {
        switch (config.bankTeleport())
        {
            case CRAFTING_CAPE:
                if (!WithdrawItemFromBank("Crafting cape(t)", 1))
                    if (!WithdrawItemFromBank("Crafting cape", 1))
                        return;
                break;
        }
    }

    //Teleport to the bank
    private void TeleportToBank() {
        if (BankCloseBy(20) != null)
            return;

        switch (config.bankTeleport())
        {
            case CRAFTING_CAPE:
                Inventory.search().withName("Crafting cape(t)").withAction("Teleport").first().ifPresentOrElse(trimmedCape -> {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(trimmedCape, "Teleport");

                }, () -> {
                    Inventory.search().withName("Crafting cape").withAction("Teleport").first().ifPresent(cape -> {
                        MousePackets.queueClickPacket();
                        InventoryInteraction.useItem(cape, "Teleport");
                    });
                });
                break;
        }
    }

    //Find close banks
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

    private int CalculateDistance(int x1, int y1, int x2, int y2)
    {
        return (int)Math.round(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
    }

    //Returns the interaction of a bank object
    private String GetBankInteraction(TileObject bankObject) {
        ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(bankObject);
        return Arrays.stream(objectComposition.getActions()).anyMatch(action -> action != null && action.toLowerCase().contains("bank")) ? "Bank" : "Use";
    }

    //Opens the closes bank
    private void OpenBank() {
        TileObject bankBooth = BankCloseBy(20);
        if (bankBooth != null) {
            MousePackets.queueClickPacket();
            TileObjectInteraction.interact(bankBooth, GetBankInteraction(bankBooth));
        }
    }

    //Set the bank to withdraw in either noted or unnoted form
    private boolean setWithdrawNotes(boolean noted) {
        if (!Bank.isOpen()) return false;
        if (Bank.isOpen()) {
            if (noted)
            {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 786456, -1, -1);
                return true;
            }
            else
            {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 786454, -1, -1);
                return true;
            }
        }
        return false;
    }

    //Returns if the bank is currently set to noted or unnoted
    private boolean getWithdrawNotes() {
        return client.getVarbitValue(3958) == 1;
    }

    //Take out a set amount of items from the bank
    //This double checks if you have any items in the invetory already so it dosen't withdraw too much
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

    //------------------------------------- Travel -------------------------------------
        //------------------------------------- Herbs -------------------------------------
    //Travels to the Ardougne herb patch
    private void TravelToArdougneHerbPatch(HerbPatch state) {
        ArdougneHerbPatch.State = HerbPatchState.TRAVEL;
        SetDisplayStateHerb(state);


        if (ArdougneHerbPatch.PathIndex == 0)
        {
            for (int i = 4; i >= 2; i--) {
                if (Inventory.search().withName("Ardougne cloak " + i).first().isPresent())
                {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(
                            Inventory.search().withName("Ardougne cloak " + i).first().get(),
                            "Farm Teleport");
                }
            }
            ArdougneHerbPatch.PathIndex = 1;
            timeout = 4;
            return;
        }
        if (ArdougneHerbPatch.PathIndex == 1)
        {
            ArdougneHerbPatch.State = HerbPatchState.PROCESS_HERB_PATCH;
            return;
        }
        if (ArdougneHerbPatch.PathIndex == 2)
        {
            if (CastTeleportSpell(WidgetInfoExtended.SPELL_ARDOUGNE_TELEPORT))
            {
                ResetPath();
                ArdougneHerbPatch.PathIndex = 3;
                setTimeout();
            }
            else
            {
                Stop("Couldn't teleport to Ardougne");
            }
            return;
        }
        if (ArdougneHerbPatch.PathIndex == 3)
        {
            if (TravelPath(ArdougneHerbPatch.Paths.get("Teleport")))
            {
                ArdougneHerbPatch.State = HerbPatchState.PROCESS_HERB_PATCH;
            }
        }
    }

    //Travels to the Falador herb patch
    private void TravelToFaladorHerbPatch(HerbPatch state) {
        FaladorHerbPatch.State = HerbPatchState.TRAVEL;
        SetDisplayStateHerb(state);

        if (FaladorHerbPatch.PathIndex == 0)
        {
            for (int i = 4; i >= 2; i--) {
                if (Inventory.search().withName("Explorer's ring " + i).first().isPresent())
                {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(
                            Inventory.search().withName("Explorer's ring " + i).first().get(),
                            "Teleport");
                }
            }
            FaladorHerbPatch.PathIndex = 1;
            timeout = 4;
            return;
        }
        if (FaladorHerbPatch.PathIndex == 1)
        {
            FaladorHerbPatch.State = HerbPatchState.PROCESS_HERB_PATCH;
            return;
        }
        if (FaladorHerbPatch.PathIndex == 10)
        {
            if (CastTeleportSpell(WidgetInfoExtended.SPELL_FALADOR_TELEPORT))
            {
                ResetPath();
                FaladorHerbPatch.PathIndex = 11;
                setTimeout();
            }
            else
            {
                Stop("Couldn't teleport to Falador");
            }
            return;
        }
        if (FaladorHerbPatch.PathIndex == 11)
        {
            if (TravelPath(FaladorHerbPatch.Paths.get("Teleport1")))
            {
                FaladorHerbPatch.State = HerbPatchState.PROCESS_HERB_PATCH;
            }
            return;
        }
    }

    //Travels to the Catherby herb patch
    private void TravelToCatherbyHerbPatch(HerbPatch state) {
        CatherbyHerbPatch.State = HerbPatchState.TRAVEL;
        SetDisplayStateHerb(state);

        if (CatherbyHerbPatch.PathIndex == 0)
        {
            if (CastTeleportSpell(WidgetInfoExtended.SPELL_CAMELOT_TELEPORT))
            {
                ResetPath();
                CatherbyHerbPatch.PathIndex = 1;
                setTimeout();
                return;
            }
            else
            {
                Stop("Couldn't teleport to Camelot");
            }
        }
        if (CatherbyHerbPatch.PathIndex == 1)
        {
            if (TravelPath(CatherbyHerbPatch.Paths.get("Camelot")))
            {
                CatherbyHerbPatch.PathIndex = 2;
                setTimeout();
            }
            return;
        }
        if (CatherbyHerbPatch.PathIndex == 2)
        {
            CatherbyHerbPatch.State = HerbPatchState.PROCESS_HERB_PATCH;



        }
    }

    //Travels to the Port Phasmatys herb patch
    private void TravelToPortPhasmatysHerbPatch(HerbPatch state) {
        PortPhasmatysHerbPatch.State = HerbPatchState.TRAVEL;
        SetDisplayStateHerb(state);

        if (PortPhasmatysHerbPatch.PathIndex == 0)
        {
            Inventory.search().withName("Ectophial").withAction("Empty").first().ifPresent(vial -> {
                MousePackets.queueClickPacket();
                InventoryInteraction.useItem(vial, "Empty");

                ResetPath();
                PortPhasmatysHerbPatch.PathIndex = 1;
                setTimeout();
            });

            return;
        }
        if (PortPhasmatysHerbPatch.PathIndex == 1)
        {
            if (TravelPath(PortPhasmatysHerbPatch.Paths.get("Ectophial")))
            {
                PortPhasmatysHerbPatch.PathIndex = 2;
                setTimeout();
            }
            return;
        }
        if (PortPhasmatysHerbPatch.PathIndex == 2)
        {
            PortPhasmatysHerbPatch.State = HerbPatchState.PROCESS_HERB_PATCH;
        }
    }

    //Travels to the Hosidius herb patch
    private void TravelToHosidiusHerbPatch(HerbPatch state) {
        HosidiusHerbPatch.State = HerbPatchState.TRAVEL;
        SetDisplayStateHerb(state);

        if (HosidiusHerbPatch.PathIndex == 0)
            HosidiusHerbPatch.PathIndex = 10;

        if (HosidiusHerbPatch.PathIndex == 10)
        {
            if (CastTeleportSpell(WidgetInfoExtended.SPELL_TELEPORT_TO_HOUSE, "Outside"))
            {
                ResetPath();
                HosidiusHerbPatch.PathIndex = 11;
                setTimeout();
                return;
            }
            else
            {
                Stop("Couldn't teleport to Hosidius");
            }
        }
        if (HosidiusHerbPatch.PathIndex == 11)
        {
            if (TravelPath(HosidiusHerbPatch.Paths.get("Home Teleport")))
            {
                HosidiusHerbPatch.PathIndex = 12;
                setTimeout();
            }
            return;
        }
        if (HosidiusHerbPatch.PathIndex == 12)
        {
            HosidiusHerbPatch.State = HerbPatchState.PROCESS_HERB_PATCH;
        }
    }

    //Travels to the Farming guild herb patch
    private void TravelToFarmingGuildHerbPatch(HerbPatch state) {
        FarmingGuildHerbPatch.State = HerbPatchState.TRAVEL;
        SetDisplayStateHerb(state);

        if (FarmingGuildHerbPatch.PathIndex == 0)
        {
            Inventory.search().nameContains("Skills necklace").withAction("Rub").first().ifPresent(necklace -> {
                Widgets.search().withTextContains("here would you like to").hiddenState(false).first().ifPresentOrElse(menu -> {
                    Widgets.search().withTextContains("Fishing Guild").hiddenState(false).first().ifPresent(farmingGuild -> {
                        MousePackets.queueClickPacket();
                        WidgetPackets.queueResumePause(farmingGuild.getId(), 5);
                        FarmingGuildHerbPatch.PathIndex = 1;
                    });
                }, () -> {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(necklace, "Rub");
                });

            });
        }
        if (FarmingGuildHerbPatch.PathIndex == 1)
        {
            FarmingGuildHerbPatch.State = HerbPatchState.PROCESS_HERB_PATCH;
            setTimeout();
        }
    }
        //------------------------------------- Trees -------------------------------------

    //Travels to the Falador tree patch
    private void TravelToFaladorTreePatch(TreePatch state) {
            FaladorTreePatch.State = TreePatchState.TRAVEL;
            SetDisplayStateTree(state);

            if (FaladorTreePatch.PathIndex == 0)
            {
                if (CastTeleportSpell(WidgetInfoExtended.SPELL_FALADOR_TELEPORT))
                {
                    ResetPath();
                    FaladorTreePatch.PathIndex = 1;
                    setTimeout();
                    return;
                }
                else
                {
                    Stop("Couldn't teleport to Falador");
                }
            }
            if (FaladorTreePatch.PathIndex == 1)
            {
                if (TravelPath(FaladorTreePatch.Paths.get("Teleport")))
                {
                    FaladorTreePatch.PathIndex = 2;
                    setTimeout();
                }
                return;
            }
            if (FaladorTreePatch.PathIndex == 2)
            {
                FaladorTreePatch.State = TreePatchState.PROCESS_TREE_PATCH;
            }
        }

    //Travels to the Lumbridge tree patch
    private void TravelToLumbridgeTreePatch(TreePatch state) {
            LumbridgeTreePatch.State = TreePatchState.TRAVEL;
            SetDisplayStateTree(state);

            if (LumbridgeTreePatch.PathIndex == 0)
            {
                if (CastTeleportSpell(WidgetInfoExtended.SPELL_LUMBRIDGE_TELEPORT))
                {
                    ResetPath();
                    LumbridgeTreePatch.PathIndex = 1;
                    setTimeout();
                    return;
                }
                else
                {
                    Stop("Couldn't teleport to Lumbridge");
                }
            }
            if (LumbridgeTreePatch.PathIndex == 1)
            {
                if (TravelPath(LumbridgeTreePatch.Paths.get("Teleport")))
                {
                    LumbridgeTreePatch.PathIndex = 2;
                    setTimeout();
                }
                return;
            }
            if (LumbridgeTreePatch.PathIndex == 2)
            {
                LumbridgeTreePatch.State = TreePatchState.PROCESS_TREE_PATCH;
            }
        }

    //Travels to the Taverly tree patch
    private void TravelToTaverlyTreePatch(TreePatch state) {
        TaverleyTreePatch.State = TreePatchState.TRAVEL;
        SetDisplayStateTree(state);

        if (TaverleyTreePatch.PathIndex == 0)
        {
            if (CastTeleportSpell(WidgetInfoExtended.SPELL_FALADOR_TELEPORT))
            {
                ResetPath();
                TaverleyTreePatch.PathIndex = 1;
                setTimeout();
            }
            else
            {
                Stop("Couldn't teleport to Falador");
            }
            return;
        }
        if (TaverleyTreePatch.PathIndex == 1)
        {
            if (TravelPath(TaverleyTreePatch.Paths.get("Teleport1")))
            {
                TaverleyTreePatch.PathIndex = 2;
                setTimeout();
            }
            return;
        }
        if (TaverleyTreePatch.PathIndex == 2)
        {
            TileObjects.search()
                    .nameContains("Gate")
                    .withAction("Open")
                    .withinDistance(10)
                    .nearestToPlayer()
                    .ifPresent(gate ->{
                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(gate, "Open");

            });
            ResetPath();
            TaverleyTreePatch.PathIndex = 3;
            return;
        }
        if (TaverleyTreePatch.PathIndex == 3)
        {
            if (TravelPath(TaverleyTreePatch.Paths.get("Teleport2")))
            {
                TaverleyTreePatch.PathIndex = 4;
                setTimeout();
            }
            return;
        }
        if (TaverleyTreePatch.PathIndex == 4)
        {
            TaverleyTreePatch.State = TreePatchState.PROCESS_TREE_PATCH;
        }
    }

    //Travels to the Varrock tree patch
    private void TravelToVarrockTreePatch(TreePatch state) {
        VarrockTreePatch.State = TreePatchState.TRAVEL;
        SetDisplayStateTree(state);

        if (VarrockTreePatch.PathIndex == 0)
        {
            if (CastTeleportSpell(WidgetInfoExtended.SPELL_VARROCK_TELEPORT))
            {
                ResetPath();
                VarrockTreePatch.PathIndex = 1;
                setTimeout();
                return;
            }
            else
            {
                Stop("Couldn't teleport to Varrock");
            }
        }
        if (VarrockTreePatch.PathIndex == 1)
        {
            if (TravelPath(VarrockTreePatch.Paths.get("Teleport")))
            {
                VarrockTreePatch.PathIndex = 2;
                setTimeout();
            }
            return;
        }
        if (VarrockTreePatch.PathIndex == 2)
        {
            VarrockTreePatch.State = TreePatchState.PROCESS_TREE_PATCH;
        }
    }

    //Travels to the Gnome Stronghold tree patch
    private void TravelToGnomeStrongholdTreePatch(TreePatch state) {
        GnomeStrongholdTreePatch.State = TreePatchState.TRAVEL;
        SetDisplayStateTree(state);

        if (GnomeStrongholdTreePatch.PathIndex == 0)
        {
            if (!config.enableTreeVarrock())
            {
                if (CastTeleportSpell(WidgetInfoExtended.SPELL_VARROCK_TELEPORT))
                {
                    ResetPath();
                    GnomeStrongholdTreePatch.PathIndex = 1;
                    setTimeout();
                    return;
                }
                else
                {
                    Stop("Couldn't teleport to Varrock");
                }
            }
            else
            {
                ResetPath();
                GnomeStrongholdTreePatch.PathIndex = 1;
                setTimeout();
            }
        }
        if (GnomeStrongholdTreePatch.PathIndex == 1)
        {
            if (TravelPath(GnomeStrongholdTreePatch.Paths.get("Teleport1")))
            {
                GnomeStrongholdTreePatch.PathIndex = 2;
                setTimeout();
            }
            return;
        }
        if (GnomeStrongholdTreePatch.PathIndex == 2)
        {
            TileObjects.search().withName("Spirit tree").withAction("Travel").nearestToPlayer().ifPresentOrElse(spiritTree -> {
                Widgets.search().withTextContains("irit Tree Locat").hiddenState(false).first().ifPresentOrElse(travelWidget -> {
                    Widgets.search().withTextContains("Tree Gnome Village").hiddenState(false).first().ifPresent(tree -> {

                        MousePackets.queueClickPacket();
                        WidgetPackets.queueResumePause(tree.getId(),1);

                        ResetPath();
                        GnomeStrongholdTreePatch.PathIndex = 3;
                        setTimeout();
                    });
                }, () -> {
                    MousePackets.queueClickPacket();
                    TileObjectInteraction.interact(spiritTree, "Travel");
                });
            }, () -> {
                Stop("Where the fuck is spirit tree?!");
            });
            return;
        }
        if (GnomeStrongholdTreePatch.PathIndex == 3)
        {
            if (TravelPath(GnomeStrongholdTreePatch.Paths.get("Teleport2")))
            {
                GnomeStrongholdTreePatch.PathIndex = 4;
                setTimeout();
            }
            return;
        }
        if (GnomeStrongholdTreePatch.PathIndex == 4)
        {
            GnomeStrongholdTreePatch.State = TreePatchState.PROCESS_TREE_PATCH;
        }
    }

    //Travels to the Farming guild tree patch
    private void TravelToFarmingGuildTreePatch(TreePatch state) {
        FarmingGuildTreePatch.State = TreePatchState.TRAVEL;
        SetDisplayStateTree(state);

        if (FarmingGuildTreePatch.PathIndex == 0)
        {
            Inventory.search().nameContains("Skills necklace").withAction("Rub").first().ifPresent(necklace -> {
                Widgets.search().withTextContains("here would you like to").hiddenState(false).first().ifPresentOrElse(menu -> {
                    Widgets.search().withTextContains("Fishing Guild").hiddenState(false).first().ifPresent(farmingGuild -> {
                        MousePackets.queueClickPacket();
                        WidgetPackets.queueResumePause(farmingGuild.getId(), 5);
                        FarmingGuildTreePatch.PathIndex = 1;
                    });
                }, () -> {
                    MousePackets.queueClickPacket();
                    InventoryInteraction.useItem(necklace, "Rub");
                });

            });
        }
        if (FarmingGuildTreePatch.PathIndex == 1)
        {
            FarmingGuildTreePatch.State = TreePatchState.PROCESS_TREE_PATCH;
            setTimeout();
        }
    }

    //------------------------------------- Travel functions -------------------------------------
    private int pathIndex = 0;

    //Reset the pathIndex
    private void ResetPath() {
        pathIndex = 0;
    }

    //Iterates through a WorldPoint array and moves the player to next point
    private boolean TravelPath(WorldPoint[] path) {
        if (EthanApiPlugin.isMoving())
            return false;

        if (playerAtTarget(path[pathIndex]))
            pathIndex++;

        if (pathIndex < path.length)
        {
            //Move the player to the trap location
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(path[pathIndex]);
        }
        else
            return true;
        return false;
    }

    //Check is the player is on the targetTile
    private boolean playerAtTarget(WorldPoint targetTile) {
        return client.getLocalPlayer().getWorldLocation().getX() == targetTile.getX() && client.getLocalPlayer().getWorldLocation().getY() == targetTile.getY();
    }

    //Casts a spell and returns true if the spell has been casted
    private boolean CastTeleportSpell(WidgetInfoExtended spell) {
        Optional<Widget> teleportSpellIcon = Widgets.search().withId(spell.getPackedId()).first();
        if (teleportSpellIcon.isPresent()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(teleportSpellIcon.get(), "Cast");
            return true;
        }
        return false;
    }

    //Casts a spell and returns true if the spell has been casted
    private boolean CastTeleportSpell(WidgetInfoExtended spell, String Cast) {
        Optional<Widget> teleportSpellIcon = Widgets.search().withId(spell.getPackedId()).first();
        if (teleportSpellIcon.isPresent()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(teleportSpellIcon.get(), Cast);
            return true;
        }
        return false;
    }

    //------------------------------------- General functions -------------------------------------

    //Set the herb display for the user
    private void SetDisplayStateHerb(HerbPatch state) {
        HerbPatchStateDisplay = state;
    }

    //Set the allotment display for the user
    private void SetDisplayStateAllotment(AllotmentPatch state) {
        AllotmentPatchStateDisplay = state;
    }

    //Set the tree display for the user
    private void SetDisplayStateTree(TreePatch state) {
        TreePatchStateDisplay = state;
    }

    //Sets a random timeout
    //This intrduces random delays to the bot
    private void setTimeout() {
        timeout = RandomUtils.nextInt(config.tickDelayMin(), config.tickDelayMax());
    }

    //Stop is called when something is wrong and the bot can not continue
    //An error message is shown to the user
    public void Stop(String reason) {
        started = false;
        debug = reason;
        this.state = State.TIMEOUT;
        herbRun = false;
        treeRun = false;
    }

    //------------------------------------- Hotkey listeners -------------------------------------
    private final HotkeyListener hotkeyListenerToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };
    private final HotkeyListener hotkeyListenerHerbRun = new HotkeyListener(() -> config.doHerbRun()) {
        @Override
        public void hotkeyPressed() {
            herbRun();
        }
    };
    private final HotkeyListener hotkeyListenerTreeRun = new HotkeyListener(() -> config.doTreeRun()) {
        @Override
        public void hotkeyPressed() {
            treeRun();
        }
    };
    private final HotkeyListener hotkeyListenerDebug = new HotkeyListener(() -> config.debugKey()) {
        @Override
        public void hotkeyPressed() {
            Debug();
        }
    };

    //------------------------------------- Hotkey functions -------------------------------------
    public void Debug() {
        debugPoint = client.getLocalPlayer().getWorldLocation();
        StringSelection stringSelection = new StringSelection("WorldPoint(" + debugPoint.getX() + ", " + debugPoint.getY() + ", " + debugPoint.getPlane() + ")");
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
    public void herbRun() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        herbRun = true;
    }
    public void treeRun() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        treeRun = true;
    }
    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;

        if (!started) {
            this.state = State.OFF;
        }
    }
}
