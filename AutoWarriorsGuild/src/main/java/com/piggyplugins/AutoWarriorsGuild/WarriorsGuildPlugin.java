package com.piggyplugins.AutoWarriorsGuild;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.PrayerInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.piggyplugins.AutoWarriorsGuild.WarriorState.*;

@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> WG Tokens</html>",
        description = "",
        enabledByDefault = false,
        tags = {"bn", "plugins"}
)

public class WarriorsGuildPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private WarriorsGuildOverlay overlay;
    @Inject
    WarriorsGuildConfig config;
    @Inject
    private AnimatorRoom animatorRoom;
    @Inject
    public WarriorsGuildRoomOverlays tileOverlays;
    AnimatorRoom WarriorsGuildRoomOverlays;
    private Instant startTime;
    public int timeout;
    public WarriorState currentState = STARTING;
    boolean startup;
    private List<WorldPoint> roomTiles;



    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        overlayManager.add(tileOverlays);
        startTime = Instant.now();
        startup = true;
        currentState = IDLE;
        roomTiles = animatorRoom.getTilesWithinRoom();
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlays);
        startTime = null;
        timeout = 0;
        startup = false;
        currentState = IDLE;
    }

    public WarriorState getCurrentState() {
        return currentState;
    }

    public Instant getStartTime() {
        return startTime;
    }

    @Provides
    WarriorsGuildConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(WarriorsGuildConfig.class);
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        handlePrayer();
        handleState();
        stopPlugin();
        System.out.println("Is in Animator room: " + isInAnimatorRoom());
        System.out.println("Has Armor: " + hasArmor());
        System.out.println("Has items to loot: " + hasItemsToLoot());
        if (timeout > 0) {
            timeout--;
            return;
        }
        switch (currentState) {
            case STARTING:
                break;
            case DEPLOYING_ARMOR:
                useArmorOnAnimator(determineArmorSet());
                timeout = 2;
                break;
            case IN_COMBAT:
                handleAnimatedCombat();
                break;
            case LOOTING:
                handleLooting();
                break;
        }
    }


    private void handleState() {
        if (startup) {
            currentState = WarriorState.STARTING;
        }
        if (isInAnimatorRoom() && hasArmor() && !hasItemsToLoot()) {
            currentState = DEPLOYING_ARMOR;
            return;
        }
        if (isInAnimatorRoom() && getArmorInteractingWithLocal()) {
            currentState = IN_COMBAT;
            return;
        }
        if (isInAnimatorRoom() && hasItemsToLoot()) {
            currentState = LOOTING;
            return;
        }
    }

    private void stopPlugin() {
        if (hasTokenThreshold() && !isInCombatWithArmor() && hasArmor() && !hasItemsToLoot()) {
            EthanApiPlugin.sendClientMessage("Token threshold reached, stopping plugin.");
            EthanApiPlugin.stopPlugin(this);
        }
    }

    private Armors determineArmorSet() {
        return config.armorType();
    }

    private void useArmorOnAnimator(Armors armorSet) {
        String helmName = armorSet.getHelm();
        String bodyName = armorSet.getBody();
        String legsName = armorSet.getLegs();

        useItemOnAnimator(helmName);
        useItemOnAnimator(bodyName);
        useItemOnAnimator(legsName);
    }

    private void useItemOnAnimator(String ItemName) {
        if (!client.getLocalPlayer().isInteracting() && InventoryHasAllArmorPieces() && isInAnimatorRoom()) {
            Inventory.search().nameContains(ItemName).first().ifPresent(item -> {
                TileObjects.search().withName("Magical Animator").first().ifPresent(animator -> {
                    MousePackets.queueClickPacket();
                    ObjectPackets.queueWidgetOnTileObject(item, animator);
                });
            });
        }
    }

    private void handleAnimatedCombat() {
        Optional<NPC> animated = NPCs.search().nameContains("Animated").interactingWithLocal().nearestToPlayer();

        if (animated.isPresent()) {
            NPC targetNPC = animated.get();

            if (!client.getLocalPlayer().isInteracting()) {
                MousePackets.queueClickPacket();
                NPCPackets.queueNPCAction(targetNPC, "Attack");
                System.out.println("Re-engaging NPC: " + targetNPC);
            }
        }
    }

    public void handleLooting() {
        lootItem("Warrior guild token");
        Armors armorSet = determineArmorSet();
        lootItem(armorSet.getHelm());
        lootItem(armorSet.getBody());
        lootItem(armorSet.getLegs());
    }

    private void lootItem(String itemName) {
        Optional<ETileItem> item = TileItems.search().withName(itemName).nearestToPlayer();
        if (item.isPresent() && !client.getLocalPlayer().isInteracting() && !EthanApiPlugin.isMoving() && itemsToLoot()) {
            MousePackets.queueClickPacket();
            item.get().interact(false);
        }
    }


    private void handlePrayer() {
        NPC hintArrowNpc = client.getHintArrowNpc();

        if (hintArrowNpc != null && hintArrowNpc.getName().contains("Animated")) {
            PrayerInteraction.flickPrayers(Prayer.PROTECT_FROM_MELEE, config.offensivePrayer().getPrayer());
        } else {
            PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MELEE, false);
            PrayerInteraction.setPrayerState(config.offensivePrayer().getPrayer(), false);
        }
    }


    public boolean isInAnimatorRoom() {
        return roomTiles.contains(client.getLocalPlayer().getWorldLocation());
    }


    public boolean isInCombatWithArmor() {
        return client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().getName().contains("Animated");
    }

    public boolean hasArmor() {
        return Inventory.search().nameContains(determineArmorSet().getBody()).first().isPresent() &&
                Inventory.search().nameContains(determineArmorSet().getHelm()).first().isPresent() &&
                Inventory.search().nameContains(determineArmorSet().getLegs()).first().isPresent();
    }

    public boolean hasItemsToLoot() {
        return TileItems.search().withName("Warrior guild token").nearestToPlayer().isPresent() ||
                TileItems.search().withName(determineArmorSet().getHelm()).nearestToPlayer().isPresent() ||
                TileItems.search().withName(determineArmorSet().getBody()).nearestToPlayer().isPresent() ||
                TileItems.search().withName(determineArmorSet().getLegs()).nearestToPlayer().isPresent();
    }

    public boolean InventoryHasAllArmorPieces() {
        Armors armorSet = determineArmorSet();
        return Inventory.search().nameContains(armorSet.getHelm()).first().isPresent() &&
                Inventory.search().nameContains(armorSet.getBody()).first().isPresent() &&
                Inventory.search().nameContains(armorSet.getLegs()).first().isPresent();
    }

    public int tokenCount() {
        Optional<Widget> token = Inventory.search().nameContains("guild token").first();
        if (token.isPresent()) {
            return token.get().getItemQuantity();
        } else {
            return 0;
        }
    }

    public boolean hasTokenThreshold() {
        return tokenCount() >= config.tokenThreshold();
    }

    public boolean itemsToLoot() {
        String[] lootableItems = {"Black full helm", "Black platebody", "Black platelegs",
                "Mithril full helm", "Mithril platebody", "Mithril platelegs",
                "Adamant full helm", "Adamant platebody", "Adamant platelegs",
                "Rune full helm", "Rune platebody", "Rune platelegs",
                "Warrior guild token"};

        for (String itemName : lootableItems) {
            if (TileItems.search().withName(itemName).nearestToPlayer().isPresent()) {
                return true;
            }
        }
        return false;
    }

    public boolean getArmorInteractingWithLocal() {
        return NPCs.search().nameContains("Animated").interactingWithLocal().nearestToPlayer().isPresent();
    }
}



