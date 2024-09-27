package com.ozplugins.AutoMTA.Rooms;

import com.example.EthanApiPlugin.Collections.*;
import com.ozplugins.AutoMTA.AutoMTAConfiguration;
import com.ozplugins.AutoMTA.AutoMTAPlugin;
import com.ozplugins.AutoMTA.util.Utils;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.Text;

import java.util.List;
import java.util.Optional;

import static net.runelite.api.ItemID.*;


@Slf4j
public class Rooms {
    private final AutoMTAPlugin plugin;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoMTAConfiguration config;
    private boolean isDropping = false;

    @Inject
    public Utils util;

    List<Integer> alch_whitelist = List.of(ItemID.ADAMANT_KITESHIELD_6894, //Addy kite
            ItemID.ADAMANT_MED_HELM_6895, //Addy med helm
            ItemID.LEATHER_BOOTS_6893, //Leather boots
            ItemID.RUNE_LONGSWORD_6897, //Rune Longsword
            ItemID.EMERALD_6896); //Emerald

    List<String> enchantShapes = List.of("Cylinder", "Icosahedron", "Cube", "Pentamid", "Dragonstone");

    Widget enchant_spell;

    @Inject
    private Rooms(final Client client, final AutoMTAPlugin plugin, final AutoMTAConfiguration config) {
        super();
        this.plugin = plugin;
        this.config = config;
    }

    public void handleEnchantRoom() {
        if (isDropping && Inventory.search().withName("Orb").first().isPresent()) {
            handledepositEnchant();
            return;
        } else {
            isDropping = false;
        }
        if (Inventory.full() && (Inventory.search().withName("Orb").first().isPresent() || Inventory.search().withName("Dragonstone").first().isPresent())
                && Inventory.search().nameInList(enchantShapes).first().isEmpty()) {
            handledepositEnchant();
            return;
        }
        if (Inventory.full() && Inventory.search().nameInList(enchantShapes).first().isPresent() || Inventory.search().withName("Dragonstone").first().isPresent()) {
            castEnchant();
            return;
        }

        if (config.pickUpDragonstone()) {
            TileItems.search().withName("Dragonstone").nearestByPath().ifPresentOrElse(dragonstone -> {
                plugin.overlay.infoStatus = "Taking dragonstone";
                dragonstone.interact(false);
            }, () -> {
                TileObjects.search().withAction("Take-from").nearestByPath().ifPresent(x -> {
                    plugin.overlay.infoStatus = "Taking object";
                    TileObjectInteraction.interact(x, "Take-from");
                });
            });
            return;
        }
        TileObjects.search().withAction("Take-from").nearestByPath().ifPresent(x -> {
            plugin.overlay.infoStatus = "Taking object";
            TileObjectInteraction.interact(x, "Take-from");
        });
    }

    private void handledepositEnchant() {
        if (config.enchantTurnIn()) {
            TileObjects.search().withName("Hole").withAction("Deposit").nearestByPath().ifPresent(x -> {
                plugin.overlay.infoStatus = "Depositing orbs";
                TileObjectInteraction.interact(x, "Deposit");
            });
        } else {
            isDropping = true;
            plugin.overlay.infoStatus = "Dropping orbs";
            var orbs = Inventory.search().withName("Orb").result();
            for (var i = 0; i < Math.min(orbs.size(), 8); i++) {
                InventoryInteraction.useItem(orbs.get(i), "Drop");
            }
        }
    }

    private void castEnchant() {
        plugin.overlay.infoStatus = "Opening enchant menu";
        Widget open_enchant_menu = client.getWidget(14286860); //Opens enchant menu cuz this shit changed

        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(open_enchant_menu, "View");

        enchant_spell = client.getWidget(config.EnchantSpell().getWidgetID()); //Gets enchantment spell widget ID from config enum thing

        Inventory.search().nameInList(List.of("Cylinder", "Icosahedron", "Cube", "Pentamid", "Dragonstone")).first().ifPresent(item -> {
            plugin.overlay.infoStatus = "Casting enchant";
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetOnWidget(enchant_spell, item);
            plugin.timeout = 4;
        });
    }

    public void handleAlchemyRoom() {
        if (inventoryHasAlchable()) {
            handleAlch();
            //return; //Saves a tick if we don't return here
        }

        if (Inventory.search().withName("Coins").first().isPresent()) {
            if (Inventory.search().withName("Coins").first().get().getItemQuantity() >= config.minimumCoinsDeposit()) {
                handleDepositCoins();
                return;
            }
        }

        WorldPoint hintArrow = client.getHintArrowPoint();
        plugin.overlay.infoStatus = "Searching cupboard";
        if (hintArrow != null) {
            TileObjects.search().withAction("Search").withName("Cupboard").atLocation(hintArrow).first().ifPresent(x -> {
                TileObjectInteraction.interact(x, "Search");
            });
            return;
        }
        TileObjects.search().withAction("Search").withName("Cupboard").nearestByPath().ifPresent(x -> {
            TileObjectInteraction.interact(x, "Search");
        });

    }

    private void handleDepositCoins() {
        TileObjects.search().withName("Coin Collector").withAction("Deposit").nearestByPath().ifPresent(x -> {
            plugin.overlay.infoStatus = "Depositing coins";
            TileObjectInteraction.interact(x, "Deposit");
        });
    }

    private boolean inventoryHasAlchable() {
        return Inventory.search().filter(x -> alch_whitelist.contains(x.getItemId())).first().isPresent();
    }

    private void handleAlch() {
        Widget alch_spell = client.getWidget(config.AlchemySpell().getWidgetID());
        Inventory.search().filter(x -> alch_whitelist.contains(x.getItemId())).first().ifPresent(item -> {
            plugin.overlay.infoStatus = "Alching: " + Text.removeTags(item.getName());
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetOnWidget(alch_spell, item);
            plugin.timeout = 4;
        });
    }

    public void handleTelekineticRoom() {
        WorldPoint hintArrow = client.getHintArrowPoint();
        WorldPoint playerWorldPoint = WorldPoint.fromLocal(client, plugin.playerLocalPoint);
        Widget telegrab_spell = client.getWidget(14286875);
        Optional<NPC> maze_guardian = NPCs.search().withName("Maze Guardian").withAction("New-maze").first();

        if (maze_guardian.isPresent()) {
            plugin.overlay.infoStatus = "Starting new maze";
            NPCInteraction.interact(maze_guardian.get(), "New-maze");
            plugin.timeout = util.tickDelay();
            return;
        }
        if (hintArrow != null) {
            if (playerWorldPoint.distanceTo(hintArrow) == 0) {
                NPCs.search().withAction("Observe").nearestToPlayer().ifPresent(x -> {
                    plugin.overlay.infoStatus = "Casting Telegrab";
                    MousePackets.queueClickPacket();
                    NPCPackets.queueWidgetOnNPC(x, telegrab_spell);
                    plugin.timeout = util.tickDelay();
                });
                return;
            }
            plugin.overlay.infoStatus = "Moving to right spot";
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(hintArrow);
        } else {
            //Sometimes hint arrow bugs out, leaves room and reenters to reset
            //Honestly might not even be needed, more of a failsafe
            plugin.hintArrowWait++;

            if (plugin.hintArrowWait < 4) {
                return;
            }
            NPCs.search().withAction("Observe").nearestByPath().ifPresentOrElse(x -> {
                plugin.overlay.infoStatus = "Need to reset room";
                plugin.handleLeaveRoom();
                plugin.timeout = util.tickDelay();
            }, () -> {
                TileObjects.search().withId(10755).nearestByPath().ifPresent(x -> {
                    plugin.overlay.infoStatus = "Moving closer";
                    MousePackets.queueClickPacket();
                    MovementPackets.queueMovement(x.getWorldLocation());
                });
            });
            plugin.hintArrowWait = 0;
        }
    }

    private int getPoints(int id) {
        switch (id) {
            case ANIMALS_BONES:
                return 1;
            case ANIMALS_BONES_6905:
                return 2;
            case ANIMALS_BONES_6906:
                return 3;
            case ANIMALS_BONES_6907:
                return 4;
            default:
                return 0;
        }
    }

    private int score(Item[] items) {
        int score = 0;
        if (items == null) {
            return score;
        }
        for (Item item : items) {
            score += getPoints(item.getId());
        }
        return score;
    }

    public void handleGraveyardRoom() {
        int hp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        int hpActual = client.getRealSkillLevel(Skill.HITPOINTS);
        int score = score(client.getItemContainer(InventoryID.INVENTORY).getItems());

        if ((Inventory.full() || score >= Inventory.getEmptySlots() + 6) && Inventory.search().matchesWildCardNoCase("*bones*").first().isPresent()) {
            castBonesTo();
            return;
        }

        if (Inventory.search().matchesWildCardNoCase("*Peach*").first().isPresent()
                || Inventory.search().matchesWildCardNoCase("*Banana*").first().isPresent()) {
            if (hp <= hpActual - 8) {
                handleEat();
                return;
            }
            depositInventory();
            return;
        }

        TileObjects.search().withName("Bones").withAction("Grab").nearestByPath().ifPresent(x -> {
            plugin.overlay.infoStatus = "Grabbing bones";
            TileObjectInteraction.interact(x, "Grab");
        });

    }

    private void handleEat() {
        Inventory.search().withAction("Eat").first().ifPresent(x -> {
            plugin.overlay.infoStatus = "Eating";
            InventoryInteraction.useItem(x, "Eat");
        });
    }

    private void castBonesTo() {
        plugin.overlay.infoStatus = "Casting spell";
        Widget bones_to = client.getWidget(config.BonesToSpell().getWidgetID()); //Gets bones to spell widget ID from config enum thing
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(bones_to, "Cast");
    }

    private void depositInventory() {
        TileObjects.search().withName("Food chute").withAction("Deposit").nearestByPath().ifPresent(x -> {
            plugin.overlay.infoStatus = "Depositing";
            TileObjectInteraction.interact(x, "Deposit");
        });
    }
}
