package com.piggyplugins.AutoRogues;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PacketUtils.WidgetID;
import com.example.Packets.*;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import okhttp3.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@PluginDescriptor(name = "<html><font color=86C43F>[JD]</font> Auto Rogues</html>",
        description = "Auto Rogues by Jarromie",
        enabledByDefault = false,
        tags = {"jarromie","plugin","rogues","den","rogue"})
@Slf4j
public class AutoRoguesPlugin extends Plugin {
    @Getter
    public boolean started = false;

    @Getter
    private State state = State.CHILLIN;
    @Getter
    private boolean shortcut = false;

    private Hashtable<String,Integer> currentLevels = null;

    private int rogueskits = 0;
    private int roguescrate = 0;

    private boolean debug = false;
    private boolean flashed = false;


    @Inject
    AutoRoguesConfig config;
    @Inject
    Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OkHttpClient okHttpClient;
    @Inject
    private DrawManager drawManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoRoguesOverlay overlay;
    @Inject
    private KeyManager keyManager;

    @Provides
    public AutoRoguesConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoRoguesConfig.class);
    }


    @Override
    protected void startUp() throws Exception {
        started = false;
        timer = Instant.now();
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        started = false;
        timer = null;
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn()) return;
        if (!started) return;

        if(client.getEnergy() < config.chillLower() * 100){
            Inventory.search().withName("Flash powder").first().ifPresentOrElse(item -> {
                if(item.getItemQuantity() == 5){
                    state = getNextState();
                }
            }, () -> {
                state = State.CHILLIN;
            });
        }else{
            state = getNextState();
        }
        if(started) handleState(state);
    }

    private void handleState(State state){
        switch(state){
            case ENTER_DEN:
                TileObjects.search().withName("Doorway").atLocation(3056, 4991, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("No entry doorway found");
                        }));
                flashed = false;
                break;
            case OBSTACLE_1:
                TileObjects.search().withName("Contortion Bars").atLocation(3049, 4997, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Enter");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find first obstacle: Contortion Bars");
                        }));
                flashed = false;
                break;
            case OBSTACLE_2:
                // walk to WorldPoint "3039,4999,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3039, 4999, 1));
                break;
            case OBSTACLE_3:
                // walk to WorldPoint "3029,5003,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3029, 5003, 1));
                break;
            case OBSTACLE_4:
                TileObjects.search().withName("Grill").atLocation(3024, 5001, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find fourth obstacle: Grill");
                        }));
                break;
            case OBSTACLE_5:
                // walk to WorldPoint "3011,5005,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3011, 5005, 1));
                break;
            case OBSTACLE_6:
                // walk to WorldPoint "3004,5003,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3004, 5003, 1));
                break;
            case OBSTACLE_7: // maybe try .nearestToPlayer() instead of .atLocation() here
                TileObjects.search().withName("Ledge").atLocation(2993, 5004, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Climb");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find seventh obstacle: Ledge");
                        }));
                break;
            case OBSTACLE_8:
                // walk to WorldPoint "2969,5016,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2969, 5016, 1));
                break;
            case OBSTACLE_9:
                TileObjects.search().withName("Ledge").atLocation(2958, 5031, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Climb");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find ninth obstacle: Ledge");
                        }));
                break;
            case OBSTACLE_10:
                // walk to WorldPoint "2962,5050,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2962, 5050, 1));
                break;
            case OBSTACLE_11:
                // run to WorldPoint "2963,5056,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2963, 5056, 1));
                break;
            case OBSTACLE_12_HIGH:
                TileObjects.search().withName("Door").atLocation(2967, 5061, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Pick-lock");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 12th obstacle (over 80 thieving): Door");
                        }));
                break;
            case OBSTACLE_13_HIGH:
                TileObjects.search().withName("Contortion Bars").atLocation(2974, 5060, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Enter");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 13th obstacle (over 80 thieving): Contortion Bars");
                        }));
                break;
            case OBSTACLE_14_HIGH:
                TileObjects.search().withName("Grill").atLocation(2989, 5057, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 14th obstacle (over 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_12_LOW:
                TileObjects.search().withName("Passageway").atLocation(2957, 5069, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Enter");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 12th obstacle (under 80 thieving): Passageway");
                        }));
                break;
            case OBSTACLE_13_LOW:
                // run to "2957,5076,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2957, 5076, 1));
                break;
            case OBSTACLE_14_LOW:
                TileObjects.search().withName("Passageway").atLocation(2955, 5095, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Enter");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 13th obstacle (under 80 thieving): Passageway");
                        }));
                break;
            case OBSTACLE_15_LOW:
                // run to "2963,5105,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2963, 5105, 1));
                break;
            case OBSTACLE_16_LOW:
                TileObjects.search().withName("Passageway").atLocation(2972, 5097, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Enter");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 16th obstacle (under 80 thieving): Passageway");
                        }));
                break;
            case OBSTACLE_17_LOW:
                TileObjects.search().withName("Grill").atLocation(2972, 5094, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 17th obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_18_LOW:
                TileObjects.search().withName("Ledge").atLocation(2983, 5087, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Climb");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 18th obstacle (under 80 thieving): Ledge");
                        }));
                break;
            case OBSTACLE_19_LOW:
                TileObjects.search().withName("Wall").atLocation(2993, 5087, 1).first().ifPresentOrElse(
                        (tileObject -> {
//                            int count = 0;
//                            for (String string : TileObjectQuery.getObjectComposition(tileObject).getActions()){
//                                EthanApiPlugin.sendClientMessage("Action " + count + ": " + string);
//                                count++;
//                            }
                            MousePackets.queueClickPacket();
                            ObjectPackets.queueObjectAction(5, tileObject.getId(), tileObject.getWorldLocation().getX(), tileObject.getWorldLocation().getY(), false);
                            //TileObjectInteraction.interact(tileObject, "Search");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find Wall");
                        }));
                break;
            case OBSTACLE_20_LOW:
                // run to "2997,5088,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2997, 5088, 1));
                break;
            case OBSTACLE_21_LOW:
                // run to "3006,5088,1"
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3006, 5088, 1));
                break;
            case OBSTACLE_22_LOW:
                TileItems.search().withName("Tile").first().ifPresentOrElse((eTileItem -> {
                    MousePackets.queueClickPacket();
                    TileItemPackets.queueTileItemAction(eTileItem, false);
                }), (() -> {
                    EthanApiPlugin.sendClientMessage("Cannot find Tile item on ground");
                }));
                break;
            case OBSTACLE_23_LOW:
                TileObjects.search().withName("Door").atLocation(3023, 5082, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 23rd obstacle (under 80 thieving): Door");
                        }));
                break;
            case OBSTACLE_24_LOW:
                Widgets.search().withId(45088773).hiddenState(false).first().ifPresentOrElse(
                        (widget -> {
                            MousePackets.queueClickPacket();
                            WidgetPackets.queueWidgetAction(widget, "Select");
                        }),
                        (() -> {
                            //EthanApiPlugin.sendClientMessage("Cannot find 24th obstacle (under 80 thieving): Tile Widget");
                        }));
                break;
            case OBSTACLE_25_LOW:
                TileObjects.search().withName("Grill").atLocation(3030, 5079, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 25th obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_26_LOW:
                TileObjects.search().withName("Grill").atLocation(3032, 5078, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 26th obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_27_LOW:
                TileObjects.search().withName("Grill").atLocation(3036, 5076, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 27th obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_28_LOW:
                TileObjects.search().withName("Grill").atLocation(3039, 5079, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 28th obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_29_LOW:
                TileObjects.search().withName("Grill").atLocation(3042, 5076, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 29th obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_30_LOW:
                TileObjects.search().withName("Grill").atLocation(3044, 5069, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 30th obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_31_LOW:
                TileObjects.search().withName("Grill").atLocation(3041, 5068, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 31st obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_32_LOW:
                TileObjects.search().withName("Grill").atLocation(3040, 5070, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 32nd obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_33_LOW:
                TileObjects.search().withName("Grill").atLocation(3038, 5069, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 33rd obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_34_LOW:
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3028, 5034, 1));
                break;
            case OBSTACLE_35_LOW:
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3024, 5034, 1));
            case OBSTACLE_36_LOW:
                TileObjects.search().withName("Grill").atLocation(3015, 5033, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 36th obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_37_LOW:
                TileObjects.search().withName("Grill").atLocation(3010, 5033, 1).first().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Open");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find 37th obstacle (under 80 thieving): Grill");
                        }));
                break;
            case OBSTACLE_38_LOW:
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3000, 5034, 1));
                break;
            case OBSTACLE_39_LOW:
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2992, 5045, 1));
                break;
            case OBSTACLE_40_LOW:
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2992, 5053, 1));
                break;
            case OBSTACLE_41_LOW:
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2992, 5067, 1));
                break;
            case OBSTACLE_42_LOW:
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(2992, 5075, 1));
                break;
            case OBSTACLE_43_LOW:
                TileItems.search().nameContains("powder").first().ifPresentOrElse((eTileItem -> {
                    //EthanApiPlugin.sendClientMessage(eTileItem.);
                    //MousePackets.queueClickPacket();
                    //TileItemPackets.queueTileItemAction(eTileItem, false);
                    eTileItem.interact(false);
                }), (() -> {
                    EthanApiPlugin.sendClientMessage("Cannot find Flash powder item on ground");
                }));
                break;
            case OBSTACLE_44_LOW:
                // use flash powder on npc
                Inventory.search().withName("Flash powder").first().ifPresent((powder ->
                {
                    switch(powder.getItemQuantity()){
                        case 5:
                            NPCs.search().withName("Rogue Guard").first().ifPresent((guard -> {
                                MousePackets.queueClickPacket();
                                NPCPackets.queueWidgetOnNPC(guard, powder);
                            }));
                            break;
                        case 4:
                            MousePackets.queueClickPacket();
                            MovementPackets.queueMovement(new WorldPoint(3028,5056,1));
                            flashed = true;
                            break;
                    }
                }));
                break;
            case OBSTACLE_45_LOW:
                if(runEnabled()) toggleRunEnergy();
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3028, 5047, 1));
                break;
            case OBSTACLE_46_LOW:
                if(!runEnabled()) toggleRunEnergy();
                TileObjects.search().withName("Wall safe").nearestToPlayer().ifPresentOrElse(
                        (tileObject -> {
                            TileObjectInteraction.interact(tileObject, "Crack");
                        }),
                        (() -> {
                            EthanApiPlugin.sendClientMessage("Cannot find the Wall safe to finish run");
                        }));
                break;
            case RUNNIN:
                Inventory.search().nameContains("powder").first().ifPresent((powder) ->
                {
                    if(!flashed && powder.getItemQuantity() == 4){
                        handleState(State.OBSTACLE_44_LOW);
                    }
                });
                break;
            case CHILLIN:
                if(client.getEnergy() >= (config.chillUpper() * 100)){
                    if(!runEnabled()) toggleRunEnergy();
                }
                break;
            case BANKIN:
                if(client.getLocalPlayer().getWorldLocation().getX() == 3056){
                    Inventory.search().nameContains("Rogue").first().ifPresentOrElse(item -> {
                        EthanApiPlugin.sendClientMessage("Attempting to bank");
                        NPCs.search().withName("Emerald Benedict").first().ifPresentOrElse((banker) -> {
                            MousePackets.queueClickPacket();
                            NPCPackets.queueNPCAction(banker, "Bank");
                        }, () -> {
                            TileObjects.search().withName("Bank chest").first().ifPresentOrElse(chest -> {
                                TileObjectInteraction.interact(chest, "Use");
                            }, () -> {
                                EthanApiPlugin.sendClientMessage("Can't find banker");
                            });
                        });
                    }, () -> {
                        handleState(State.ENTER_DEN);
                    });
                }else{
                    Inventory.search().nameContains("Rogue").first().ifPresentOrElse(crate -> {
                        Widgets.search().withId(786474).hiddenState(false).first().ifPresent(deposit -> {
                            MousePackets.queueClickPacket();
                            WidgetPackets.queueWidgetAction(deposit, "Deposit inventory");
                        });
                    }, () -> {
                        handleState(State.ENTER_DEN);
                    });
                }
                break;
        }
    }

    private State getNextState(){
        Player player = client.getLocalPlayer();
        if (EthanApiPlugin.isMoving() || player.getAnimation() != -1) {
            // this is to prevent clicks while animating/moving.
            return State.RUNNIN;
        }
        if(state == State.CHILLIN){
            if(client.getEnergy() < (config.chillUpper() * 100)){
                return State.CHILLIN;
            }
        }
        if(!runEnabled()) toggleRunEnergy();

        WorldPoint worldpoint = player.getWorldLocation();
        boolean highLevel = false;
        switch(worldpoint.getX() + "," + worldpoint.getY() + "," + worldpoint.getPlane()){
            case "3056,4988,1":
            case "3040,4969,1":
            case "3043,4972,1":
                return State.BANKIN;
            case "3056,4992,1":
                return State.OBSTACLE_1;
            case "3048,4997,1":
                return State.OBSTACLE_2;
            case "3039,4999,1":
                return State.OBSTACLE_3;
            case "3029,5003,1":
                return State.OBSTACLE_4;
            case "3023,5001,1":
                return State.OBSTACLE_5;
            case "3011,5005,1":
                return State.OBSTACLE_6;
            case "3004,5003,1":
                return State.OBSTACLE_7;
            case "2988,5004,1":
                return State.OBSTACLE_8;
            case "2967,5016,1":
                return State.OBSTACLE_9;
            case "2958,5028,1":
                return State.OBSTACLE_9;
            case "2958,5035,1":
                return State.OBSTACLE_10;
            case "2962,5050,1":
                return State.OBSTACLE_11;
            case "2963,5056,1":
                return ((config.shortcut() && ((client.getRealSkillLevel(Skill.THIEVING)) >= 80)) ? State.OBSTACLE_12_HIGH : State.OBSTACLE_12_LOW);
            case "2968,5061,1":
                return State.OBSTACLE_13_HIGH;
            case "2974,5059,1":
                return State.OBSTACLE_14_HIGH;
            case "2957,5072,1":
                return State.OBSTACLE_13_LOW;
            case "2957,5076,1":
                return State.OBSTACLE_14_LOW;
            case "2955,5098,1":
                return State.OBSTACLE_15_LOW;
            case "2963,5105,1":
                return State.OBSTACLE_16_LOW;
            case "2972,5094,1":
                return State.OBSTACLE_17_LOW;
            case "2972,5093,1":
                return State.OBSTACLE_18_LOW;
            case "2991,5087,1":
            case "2992,5088,1":
                return State.OBSTACLE_19_LOW;
            case "2993,5088,1":
                return State.OBSTACLE_20_LOW;
            case "2997,5088,1":
                return State.OBSTACLE_21_LOW;
            case "3006,5088,1":
                return State.OBSTACLE_22_LOW;
            case "3018,5080,1":
                return State.OBSTACLE_23_LOW;
            case "3023,5082,1":
                return State.OBSTACLE_24_LOW;
            case "3024,5082,1":
                return State.OBSTACLE_25_LOW;
            case "3031,5079,1":
                return State.OBSTACLE_26_LOW;
            case "3032,5077,1":
                return State.OBSTACLE_27_LOW;
            case "3037,5076,1":
                return State.OBSTACLE_28_LOW;
            case "3040,5079,1":
                return State.OBSTACLE_29_LOW;
            case "3043,5076,1":
                return State.OBSTACLE_30_LOW;
            case "3044,5068,1":
                return State.OBSTACLE_31_LOW;
            case "3041,5069,1":
                return State.OBSTACLE_32_LOW;
            case "3039,5070,1":
                return State.OBSTACLE_33_LOW;
            case "3038,5068,1":
                return State.OBSTACLE_34_LOW;
            case "3028,5034,1":
                return State.OBSTACLE_35_LOW;
            case "3024,5034,1":
                return State.OBSTACLE_36_LOW;
            case "3014,5033,1":
                return State.OBSTACLE_37_LOW;
            case "3009,5033,1":
                return State.OBSTACLE_38_LOW;
            case "3000,5034,1":
                return State.OBSTACLE_39_LOW;
            case "2992,5045,1":
                return State.OBSTACLE_40_LOW;
            case "2992,5053,1":
            case "2990,5057,1":
                return State.OBSTACLE_41_LOW;
            case "2992,5067,1":
                return State.OBSTACLE_42_LOW;
            case "2992,5075,1":
                return State.OBSTACLE_43_LOW;
            case "3009,5063,1":
                return State.OBSTACLE_44_LOW;
            case "3028,5056,1":
            case "3028,5051,1":
                return State.OBSTACLE_45_LOW;
            case "3028,5047,1":
                return State.OBSTACLE_46_LOW;
        }

        return State.TIMEOUT;
    };

    private boolean staminaIsActive() { return client.getVarbit(Varbits.RUN_SLOWED_DEPLETION_ACTIVE).equals(1); }

    private boolean runEnabled() { return !(EthanApiPlugin.getClient().getVarpValue(173) == 0); }

    private void toggleRunEnergy() {
        clientThread.invokeLater(() -> {
            EthanApiPlugin.sendClientMessage("Turning run energy " + (runEnabled() ? "off" : "on"));
        });
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
    }

    private Instant timer = null;
    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            if(debug){
                printData();
            }else{
                if(!started){
                    start();
                }else{
                    stop();
                }
            }
        }
    };

    private void start(){
        started = true;
        timer = Instant.now();
    }

    private void stop(){
        started = false;
    }

    public String getElapsedTime() {
        if (!started) {
            return "00:00:00";
        }
        Duration duration = Duration.between(timer, Instant.now());
        long durationInMillis = duration.toMillis();
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public void printData() {
        Player player = client.getLocalPlayer();
        WorldPoint location = player.getWorldLocation();
        clientThread.invokeLater(() -> {
            EthanApiPlugin.sendClientMessage("WorldPoint X:" + location.toString());
            EthanApiPlugin.sendClientMessage("Current State:" + getNextState().toString());
        });
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event){
        int groupId = event.getGroupId();

        if(groupId == WidgetID.INVENTORY_GROUP_ID){
            Widgets.search().withName("Rogue equipment crate").first().ifPresent(item -> {
                roguescrate++;
                switch(roguescrate){
                    case 5:
                    case 25:
                    case 50:
                    case 100:
                    case 250:
                    case 500:
                    case 750:
                    case 1000:
                        clientThread.invokeLater(() -> {
                            DiscordWebhookBody discordWebHookBody = new DiscordWebhookBody();
                            discordWebHookBody.setContent(config.discordAchievementsName() + " has reached " + roguescrate + " Rogue's crates");
                            sendMessage(discordWebHookBody);
                        });
                        return;
                    default:
                        return;
                }
            });
            Widgets.search().withName("Rogue's kit").first().ifPresent(item -> {
                rogueskits++;
                switch (rogueskits){
                    case 10:
                    case 25:
                    case 50:
                    case 100:
                    case 250:
                    case 500:
                    case 750:
                    case 1000:
                        clientThread.invokeLater(() -> {
                            DiscordWebhookBody discordWebhookBody = new DiscordWebhookBody();
                            discordWebhookBody.setContent(config.discordAchievementsName() + " has reached " + rogueskits + " Rogue's kits");
                            sendMessage(discordWebhookBody);
                        });
                        return;
                    default:
                        return;
                }
            });
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if (!config.discordAchievements()) return;
        String skill = statChanged.getSkill().getName();
        if (skill.equals("Thieving" ) || skill.equals("Agility" )) {
            int newLevel = statChanged.getLevel();
            Integer previousLevel = currentLevels.get(skill);
            if (previousLevel == null || previousLevel == 0) {
                currentLevels.put(skill, newLevel);
                return;
            }else{
                previousLevel = currentLevels.get(skill);
            }
            if (previousLevel < newLevel) {
                switch (newLevel) {
                    case 75:
                    case 80:
                    case 90:
                    case 95:
                    case 96:
                    case 97:
                    case 98:
                    case 99:
                        clientThread.invokeLater(() -> {
                            DiscordWebhookBody discordWebhookBody = new DiscordWebhookBody();
                            discordWebhookBody.setContent(config.discordAchievementsName() + " has reached level " + newLevel + " in " + skill);
                            sendMessage(discordWebhookBody);
                        });
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private void sendMessage(DiscordWebhookBody discordWebhookBody){
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", GSON.toJson(discordWebhookBody));
        sendWebhook(discordWebhookBody, config.discordAchievementsScreenshot(), requestBuilder);
    }

    private void sendWebhook(DiscordWebhookBody discordWebhookBody, boolean sendScreenshot, MultipartBody.Builder bodyBuilder)
    {
        String configUrl = config.discordAchievementsWebhook();
        if (Strings.isNullOrEmpty(configUrl)) { return; }

        List<String> webhookUrls =
                Arrays.asList(configUrl.split("\n"))
                        .stream()
                        .filter(u -> u.length() > 0)
                        .map(u -> u.trim())
                        .collect(Collectors.toList());

        for (String webhookUrl : webhookUrls)
        {
            HttpUrl url = HttpUrl.parse(webhookUrl);
            bodyBuilder
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("payload_json", GSON.toJson(discordWebhookBody));

            if (sendScreenshot)
            {
                sendWebhookWithScreenshot(url, bodyBuilder);
            }
            else
            {
                buildRequestAndSend(url, bodyBuilder);
            }
        }
    }

    private void sendWebhookWithScreenshot(HttpUrl url, MultipartBody.Builder requestBodyBuilder)
    {
        drawManager.requestNextFrameListener(
                image ->
                {
                    BufferedImage bufferedImage = (BufferedImage) image;
                    byte[] imageBytes;
                    try
                    {
                        imageBytes = convertImageToByteArray(bufferedImage);
                    }
                    catch (IOException e)
                    {
                        log.warn("Error converting image to byte array", e);
                        return;
                    }

                    requestBodyBuilder.addFormDataPart(
                            "file",
                            "image.png",
                            RequestBody.create(
                                    MediaType.parse("image/png"),
                                    imageBytes
                            )
                    );

                    buildRequestAndSend(url, requestBodyBuilder);
                }
        );
    }
    private void buildRequestAndSend(HttpUrl url, MultipartBody.Builder requestBodyBuilder)
    {
        RequestBody requestBody = requestBodyBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        sendRequest(request);
    }

    private void sendRequest(Request request)
    {
        okHttpClient.newCall(request).enqueue(
                new Callback()
                {
                    @Override
                    public void onFailure(Call call, IOException e)
                    {
                        log.debug("Error submitting webhook", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException
                    {
                        response.close();
                    }
                }
        );
    }
    private static byte[] convertImageToByteArray(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
