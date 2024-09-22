package com.example.GreenDrags;

import com.example.EthanApiPlugin.Collections.*;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.*;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.HotkeyListener;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[GS]</font> Green Dragons</html>",
        tags = {"pajau","green","dragon"},
        enabledByDefault = false
)
public class GreenDragsPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private GreenDragsConfig config;

    private int estado = 0;
    private int timeout = 0;
    private int contador = 0;
    private static boolean prendido = false;
    private final List<Pair<TileItem,Tile>> itemList = new ArrayList<>();
    private final Random Nrand = new Random();
    private final List<Integer> items2loot = Arrays.asList(536,13510,13511);
    private final WorldArea dragonSpot = new WorldArea(3323,3670,24,33,0);
    private boolean ringFlag = false;
    private int[] ringIDs = {2566,2564,2562,2560,2558,2556,2554,2552};

    @Provides
    GreenDragsConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(GreenDragsConfig.class);
    }

    void resetear() {
        estado = 0;
        timeout = 0;
        contador = 0;
        prendido = false;
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(botonPrendido);
        resetear();
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(botonPrendido);
        resetear();
    }

    private final KeyListener botonPrendido = new HotkeyListener(() -> new Keybind(KeyEvent.VK_F9, 0)) {
        @Override
        public void hotkeyPressed() {

            clientThread.invoke(() -> {
                prendido = !prendido;
                if (prendido) {
                    estado = 5;
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", ColorUtil.wrapWithColorTag("Encendido", Color.green), "");
                } else {
                    estado = 0;
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", ColorUtil.wrapWithColorTag("Apagado", Color.RED), "");
                    resetear();
                }

            });
        }
    };

    @Subscribe
    void onItemSpawned(ItemSpawned event) {
        if (event.getItem() == null) {
            return;
        }
        if (items2loot.contains(event.getItem().getId())) {
            itemList.add(Pair.of(event.getItem(), event.getTile()));
            log.info("Se agrego un Item, Items Size: {}", itemList.size());
            timeout = 3;
        }
    }

    @Subscribe
    void onGameTick(GameTick event) {
        if (!prendido) return;
        if (timeout > 0) {
            timeout--;
            return;
        }
        if(client.getEnergy() >35 && client.getVarpValue(173) == 0){
            WidgetPackets.queueWidgetAction(client.getWidget(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB));
            timeout=Nrand.nextInt(2);
            if(timeout!=0) return;
        }


        if (estado == 5) {  //reconociendo el estado
            log.info("Reconociendo estado");
            if (client.getLocalPlayer().getWorldLocation().isInArea(dragonSpot)) {
                estado = 10;
            } else if (client.getLocalPlayer().getWorldLocation().getRegionID() == 12600
                || client.getLocalPlayer().getWorldLocation().getRegionID() == 12344){
                if (!Inventory.search().nameContains("Dragon bones").empty()) {  //si hay d bones
                    estado = 20;
                } else if (!Inventory.search().nameContains(config.foodName()).empty()) {    //Si hay food
                    estado = 30;
                } else if (Inventory.search().nameContains(config.foodName()).empty()) {     //si no hay food
                    estado = 20;
                }
                if(Equipment.search().nameContains("dueling").empty()){
                    ringFlag = true;
                }
            }
        }
        log.info("estado = {}", estado);
        if (estado == 10) { //matar dragones
            if (Inventory.getEmptySlots() == 0 || Inventory.search().withAction("Eat").empty()) {
                if (client.getLocalPlayer().getWorldLocation().getY() >= 3680) {
                    log.info("Estamos sobre 20 wild");
                    timeout = 5;
                    MousePackets.queueClickPacket();
                    MovementPackets.queueMovement(client.getLocalPlayer().getWorldLocation().getX(), 3678, false);
                    return;
                }
                log.info("Teleportando");
                MousePackets.queueClickPacket();
                Equipment.search().nameContains("Ring").first().ifPresent(xd -> WidgetPackets.queueWidgetAction(xd, "Ferox Enclave"));
                timeout = 5;
                estado = 20;
            }
            if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= 60) {
                log.info("Comiendo");
                Optional<Widget> comida = Inventory.search().withAction("Eat").first();
                comida.ifPresent(com -> InventoryInteraction.useItem(comida.get(), "Eat"));
                timeout = 4;
                return;
            }
            if (!itemList.isEmpty()) {
                log.info("Looteando");
                //log.info("Id:{}    WL:{}",huesos.getId(),huesosTile.getWorldLocation());
                int last = itemList.size() - 1;
                MousePackets.queueClickPacket();
                TileItemPackets.queueTileItemAction(3, itemList.get(last).getLeft().getId(), itemList.get(last).getRight().getWorldLocation().getX(),
                        itemList.get(last).getRight().getWorldLocation().getY(), false);
                itemList.remove(itemList.size() - 1);
                timeout = 5;
                return;
                //todo Lootear
            }
            if (InCombat(client.getLocalPlayer())) {
                contador = 0; //Se reinicia el contador de Idle
            } else {
                contador++;
                if (contador > 4) {
                    log.info("Al atake!");
                    Optional<NPC> dragonAtakable = NPCs.search().notInteracting().nameContains("Green dragon").nearestToPlayer();
                    MousePackets.queueClickPacket();
                    dragonAtakable.ifPresent(npc -> NPCPackets.queueNPCAction(npc, "Attack"));
                    contador = 0;
                }
            }
        } else if (estado == 20) {
            //todo abrir banco y depositar

            if (client.getWidget(786442) != null) {  //El bank esta abierto
                if(BankInventory.search().nameContains("Dragon").first().isPresent()) {
                    log.info("Depositando bones");
                    BankInventory.search().nameContains("Dragon").first().ifPresent(Dbone -> {
                        WidgetPackets.queueWidgetAction(Dbone, "Deposit-All");
                    });
                } else if (!BankInventory.search().nameContains("Ensouled").empty()) {
                    log.info("Depositando Ensouled Heads");
                    BankInventory.search().nameContains("Ensouled").first().ifPresent(head -> {
                        WidgetPackets.queueWidgetAction(head,"Deposit-All");
                    });
                } else if (BankInventory.search().nameContains(config.foodName()).result().size()<4){
                    log.info("Sacando Food");
                    if(Bank.search().nameContains(config.foodName()).result().isEmpty()) {   //si no encontro food
                        log.info("No se encontro food");
                        prendido=false;
                    } else{
                        BankInteraction.useItem("<col=ff9040>" + config.foodName() + "</col>", "Withdraw-"+ config.foodAmout());
                        if(!ringFlag){
                            estado = 30;
                        }
                    }
                } else if (BankInventory.search().nameContains("dueling").empty() && ringFlag) {
                    BankInteraction.useItem(x -> x.getName().contains("dueling"), "Withdraw-1");
                    ringFlag = false;

                } else {
                    estado =30;
                    if(!BankInventory.search().nameContains("dueling").empty()){
                        log.info("Meow");
                        BankInventoryInteraction.useItem(x->x.getName().contains("dueling"),"Wear");
                    }
                }
            }
            else {
                if (client.getLocalPlayer() == null) return;
                if (Inventory.search().nameContains("dueling").first().isPresent()) {
                    InventoryInteraction.useItem( item -> item.getName().contains("dueling"),"Wear");
                    return;
                } else if (Equipment.search().nameContains("dueling").empty() && Inventory.search().nameContains("dueling").empty()) {
                    ringFlag = true;
                }
                if (isIdle(client.getLocalPlayer())) { //estoy idle
                    if (ringFlag
                            || Inventory.search().nameContains(config.foodName()).empty()
                            || !Inventory.search().nameContains("bones").empty())
                    { //se debe usar el bank, no hay food,hay huesos en el inv o debo sacar un ring
                        Optional<TileObject> banko = TileObjects.search().withAction("Use").nearestToPoint(new WorldPoint(3131, 3632, 0));
                        if (banko.isPresent()) {
                            ObjectPackets.queueObjectAction(banko.get(), false, "Use");
                        } else {
                            log.info("No se encontro el banko");
                            resetear();
                        }
                    }
                }
            }
            timeout = 2 + Nrand.nextInt(3);
//26711  Id Bank chest
        } else if (estado == 30) {//Volver a los dragones
            if (estoyEn(new WorldPoint(3131,3632,0)) || estoyEn(new WorldPoint(3130,3631,0))
                    || client.getLocalPlayer().getWorldLocation().getX() < 3153){
                TileObjects.search().withId(39653).nearestToPoint(new WorldPoint(3193,3644,0)).ifPresent(barrera -> {
                    ObjectPackets.queueObjectAction(barrera, false, "Pass-Through");
                    timeout = 6 + Nrand.nextInt(12);
                });
            }else if (estoyEn(new WorldPoint(3155, 3635, 0)) || estoyEn(new WorldPoint(3155, 3634, 0))
                    && isIdle(client.getLocalPlayer())) {
                log.info("estamos en el puente");
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3193, 3644, 0));
            } else if (estoyEn(new WorldPoint(3193,3644,0))) { //punto  1
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(3230,3650,false);
            } else if ( estoyEn(new WorldPoint(3230,3650,0)) ) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(new WorldPoint(3243,3656,0));
            } else if ( estoyEn(new WorldPoint(3243,3656,0))) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(3257,3662,false);
            } else if ( estoyEn(new WorldPoint(3257,3662,0))) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(3272,3668,false);
            } else if (estoyEn(new WorldPoint(3272,3668,0))) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(3284,3683,false);
            } else if (estoyEn(new WorldPoint(3284,3683,0))) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(3310, 3687, false);
            } else if (estoyEn(new WorldPoint(3310, 3687,0))) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(3328,3685,false);
                timeout = 10;
                estado = 10;
            }

        }
    }

    public boolean InCombat(Player yo) {
        return yo.isInteracting() || yo.getAnimation() != -1 || client.getNpcs().stream().anyMatch(mono -> {
            if (mono.getInteracting() != null) {
                return mono.getInteracting().equals(yo);
            }
            return false;
        });
    }

    public boolean isIdle(Player gamer) {
        return gamer.getIdlePoseAnimation() == gamer.getPoseAnimation();
    }

    public boolean estoyEn(WorldPoint tile){
        return client.getLocalPlayer().getWorldLocation().equals(tile);
    }
    public boolean estoyEn(WorldArea area) {
        return client.getLocalPlayer().getWorldLocation().isInArea(area);
    }
}
