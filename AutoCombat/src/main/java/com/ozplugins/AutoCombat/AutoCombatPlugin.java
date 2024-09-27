package com.ozplugins.AutoCombat;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InteractionHelper;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.ozplugins.AutoCombat.data.SlayerNpcs;
import com.ozplugins.AutoCombat.util.Utils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.game.NPCManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.ozplugins.AutoCombat.AutoCombatState.*;

@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto Combat</font></html>",
        enabledByDefault = false,
        description = "Automated combat assistant plugin..",
        tags = {"Oz"}
)
@Slf4j
public class AutoCombatPlugin extends Plugin {
    protected static final Random random = new Random();
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    public ItemManager itemManager;
    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    AutoCombatConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private AutoCombatOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private Notifier notifier;
    @Inject
    private NPCManager npcManager;
    AutoCombatState state;
    int timeout = 0;
    private int nextRunEnergy;
    boolean forceTab = false;
    WorldPoint playerLocation;

    Utils util;

    DebugTileOverlay tileOverlay;

    int hitpoints = 0;
    int strength = 0;
    int ranged = 0;
    int attack = 0;
    int defence = 0;
    int prayer = 0;
    WorldArea enemyAttackRadiusArea;
    List<WorldPoint> enemyArea = new ArrayList<>();
    List<WorldPoint> enemyAttackArea = new ArrayList<>();
    WorldPoint safespotTile;
    WorldPoint cannonSpot;
    int remainingCannonballs;
    boolean cannonFired;
    public Queue<ItemStack> lootReceived = new LinkedList<>();
    int slayerItemID;
    int specialAttack = 0;
    boolean specialAttackEnabled = false;

    @Provides
    AutoCombatConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoCombatConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        nextRunEnergy = 0;
        slayerItemID = 0;
        hitpoints = 0;
        strength = 0;
        remainingCannonballs = 0;
        enemyAttackRadiusArea = null;
        enemyArea.clear();
        ranged = 0;
        lootReceived.clear();
        attack = 0;
        defence = 0;
        prayer = 0;
        enemyAttackArea = null;
        safespotTile = null;
        cannonFired = false;
        enablePlugin = false;
        specialAttack = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
        specialAttackEnabled = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1;
        botTimer = Instant.now();
        state = null;
        tileOverlay = new DebugTileOverlay(client, this, config);
        overlayManager.add(tileOverlay);
        keyManager.registerKeyListener(pluginToggle);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        resetVals();
    }

    private void resetVals() {
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);
        state = null;
        slayerItemID = 0;
        hitpoints = 0;
        strength = 0;
        ranged = 0;
        attack = 0;
        defence = 0;
        enemyAttackRadiusArea = null;
        enemyArea.clear();
        remainingCannonballs = 0;
        prayer = 0;
        timeout = 0;
        enemyAttackArea = null;
        lootReceived.clear();
        cannonFired = false;
        safespotTile = null;
        nextRunEnergy = 0;
        enablePlugin = false;
        keyManager.unregisterKeyListener(pluginToggle);
        botTimer = null;
    }

    public AutoCombatState getState() {
        Player player = client.getLocalPlayer();

        if (player == null) {
            return UNHANDLED_STATE;
        }

        if (config.FlickPrayers() && prayer > 0) { //Make sure to always pray flick, need to move this elsewhere soon.
            handlePrayFlick();
        } else {
            //disablePrayers();
        }
        if (EthanApiPlugin.isMoving()) {
            return MOVING;
        }
        if (isBankPinOpen()) {
            overlay.infoStatus = "Bank Pin";
            return BANK_PIN;
        }

        if (timeout > 0) {
            return TIMEOUT;
        }
        if (config.UseCannon() && (needsToRepairOrReloadCannon() || !cannonFired) || (!hasFood() && isCannonSetUp() && hitpoints < config.HealthLowAmount())) {
            return HANDLE_CANNON;
        }
        if (config.LootItems() && needsToLoot()) {
            return LOOT;
        }
        if (config.BuryBonesOrAshes() && hasBonesOrAshes()) {
            return HANDLE_BURY_OR_SCATTER;
        }

        return FIGHT;
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event) {
        if (client.isKeyPressed(KeyCode.KC_SHIFT)) {
            log.info("abc");
            Scene scene = client.getScene();
            Tile[][][] tiles = scene.getTiles();
            Tile tile = null;
            var mouseX = client.getMouseCanvasPosition().getX();
            var mouseY = client.getMouseCanvasPosition().getY();

            int z = client.getPlane();
            for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
                for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                    var t = tiles[z][x][y];
                    if (t == null) {
                        continue;
                    }

                    var tll = t.getLocalLocation();
                    var poly = Perspective.getCanvasTilePoly(client, tll);
                    if (poly != null && poly.contains(mouseX, mouseY)) {
                        tile = t;
                        break;
                    }
                }
                if (tile != null) {
                    break;
                }
            }

            if (tile == null) {
                return;
            }

            Tile finalTile = tile;

            client.createMenuEntry(0)
                    .setOption("Set AutoCombat Safe Spot")
                    .setType(MenuAction.RUNELITE)
                    .onClick(menuEntry -> {
                        var mouseLoc = finalTile.getWorldLocation();
                        var string = String.format("%d, %d", mouseLoc.getX(), mouseLoc.getY());
                        configManager.setConfiguration("OzAutoCombat", "SafespotCoords", string);
                    });
        }

        MenuEntry entry = event.getFirstEntry();
        NPC npc = entry.getNpc();

        if (npc == null) {
            return;
        }

        var attackEntry = Arrays.stream(event.getMenuEntries()).filter(e -> e != null && e.getOption().equals("Attack")).findFirst();
        if (attackEntry.isEmpty()) {
            return;
        }

        if (enablePlugin) {
            client.createMenuEntry(0)
                    .setOption("Stop AutoCombat")
                    .onClick(menuEntry -> {
                        togglePlugin();
                    });
        } else {
            var e = attackEntry.get();
            client.createMenuEntry(0)
                    .setOption("AutoCombat")
                    .setTarget(e.getTarget())
                    .setType(e.getType())
                    .setIdentifier(e.getIdentifier())
                    .setParam0(e.getParam0())
                    .setParam1(e.getParam1())
                    .onClick(menuEntry -> {
                        configManager.setConfiguration("OzAutoCombat", "enemyNPC", npc.getName());
                        configManager.setConfiguration("OzAutoCombat", "ignoreEnemyLevels", "");
                        togglePlugin();
                    });
        }
    }


    @Subscribe
    private void onGameTick(GameTick tick) {
        if (!enablePlugin) {
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            forceTab = false;
            return;
        }
        playerLocation = client.getLocalPlayer().getWorldLocation();
        prayer = client.getBoostedSkillLevel(Skill.PRAYER);
        hitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
        strength = client.getBoostedSkillLevel(Skill.STRENGTH);
        ranged = client.getBoostedSkillLevel(Skill.RANGED);
        attack = client.getBoostedSkillLevel(Skill.ATTACK);
        defence = client.getBoostedSkillLevel(Skill.DEFENCE);
        safespotTile = getCoords(config.SafespotCoords());
        enemyAttackRadiusArea = getEnemyAttackRadiusArea();
        enemyAttackArea = enemyAttackRadiusArea.toWorldPointList();
        cannonSpot = getCoords(config.CannonCoords());
        specialAttack = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
        specialAttackEnabled = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1;
        handleGetEnemyArea();
        state = getState();
        handleRun(5, 5);

        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case FIND_BANK:
                openNearestBank();
                break;

            case FIGHT:
                handleFight();
                break;

            case HANDLE_CANNON:
                handleCannon();
                break;

            case HANDLE_BURY_OR_SCATTER:
                handleBuryOrScatter();
                break;

            case LOOT:
                handleLoot();
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "Ded";
                break;

            case MOVING:
                break;

            case ANIMATING:
            case BANK_PIN:
            case IDLE:
                timeout = tickDelay();
                break;
        }
    }

    private void handleSlayerEnemies() {
        if (!client.getLocalPlayer().isInteracting()) {
            return;
        }
        var interacting = client.getLocalPlayer().getInteracting();
        if (interacting instanceof NPC) {
            NPC npc = (NPC) interacting;
            var hp = calculateNPCHealth(npc);
            if (hp >= 0 && hp < 5) {
                var slayerItem = Inventory.search().withId(slayerItemID).first();
                if (slayerItem.isPresent()) {
                    MousePackets.queueClickPacket();
                    MousePackets.queueClickPacket();
                    NPCPackets.queueWidgetOnNPC(npc, slayerItem.get());
                }
            }
        }
    }

    private void handleLoot() {
        if (Inventory.full()) {
            if (hasFood() && config.EatMakeSpace()) {
                Inventory.search().matchesWildCardNoCase("*" + config.FoodName() + "*").onlyUnnoted().first().ifPresent(x -> {
                    overlay.infoStatus = "Eating";
                    InventoryInteraction.useItem(x, "Eat", "Drink");
                });
            } else if (config.stopIfFull()) {
                togglePlugin();
                notifier.notify("AutoCombat ran out of space, stopping");
            } else {
                lootReceived.clear();
                notifier.notify("AutoCombat out of space");
            }
            return;
        }
        ItemStack item_to_loot = lootReceived.peek();
        WorldPoint item_worldpoint = WorldPoint.fromLocal(client, item_to_loot.getLocation()); //unsure if even needed but fuck it
        //TileItems.search().withId(item_to_loot.getId()).nearestToPoint(n).ifPresent(x -> {
        String item_name = itemManager.getItemComposition(item_to_loot.getId()).getName();

        TileItems.search().matchesWildCardNoCase("*" + item_name + "*").nearestToPoint(item_worldpoint).ifPresent(x -> {
            overlay.infoStatus = "Looting: " + item_name;
            TileItemPackets.queueTileItemAction(x, false);
            lootReceived.remove();
        });
    }

    private void handleBuryOrScatter() {
        Inventory.search().matchesWildCardNoCase("*bones*").withAction("Bury").onlyUnnoted().first().ifPresent(x -> {
            overlay.infoStatus = "Burying bones";
            InventoryInteraction.useItem(x, "Bury");
            timeout = tickDelay();
        });

        Inventory.search().matchesWildCardNoCase("*ashes*").withAction("Scatter").onlyUnnoted().first().ifPresent(x -> {
            overlay.infoStatus = "Scattering ashes";
            InventoryInteraction.useItem(x, "Scatter");
            timeout = tickDelay();
        });
    }

    private void handleGetEnemyArea() {
        enemyArea.clear();

        if (config.UseSafespotNPCRadius()) {
            NPCs.search().withAction("Attack")
                    .alive()
                    .walkable()
                    .withinWorldArea(enemyAttackRadiusArea)
                    .filter(npcs -> npcs.getName() != null && npcs.getName().equalsIgnoreCase(config.EnemyNPCName()))
                    .result().forEach(x -> {
                        if (x != null) {
                            enemyArea.addAll(x.getWorldArea().toWorldPointList());
                        }
                    });
            return;
        }
        NPCs.search().withAction("Attack")
                .alive().walkable()
                .filter(npcs -> npcs.getName() != null && npcs.getName().equalsIgnoreCase(config.EnemyNPCName()))
                .result().forEach(x -> {
                    if (x != null) {
                        enemyArea.addAll(x.getWorldArea().toWorldPointList());
                    }
                });

    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event) { //TODO GOTTA REDO, NASTY NESTED FOR LOOP AM RETART
        if (!enablePlugin || !config.LootItems()) return;
        Collection<ItemStack> items = event.getItems();
        //sendGameMessage("List of lewt: " + getLootNames(config.ItemsToLoot().toLowerCase()));
        List<String> item_names = new ArrayList<>(List.of(""));
        for (ItemStack it : items) {
            ItemComposition comp = itemManager.getItemComposition(it.getId());
            item_names.add(comp.getName().toLowerCase());
            for (int i = 0; i < getLootNames(config.ItemsToLoot().toLowerCase()).size(); i++) {
                if (WildcardMatcher.matches(comp.getName().toLowerCase(), Text.removeTags(getLootNames(config.ItemsToLoot().toLowerCase()).get(i)))) {
                    lootReceived.add(it);
                    break;
                }
            }
        }

        /*
                items.stream().filter(item -> {
            ItemComposition comp = itemManager.getItemComposition(item.getId());
            //return getLootNames(config.ItemsToLoot().toLowerCase()).contains(comp.getName().toLowerCase());
            return TileItems.search().matchesWildCardNoCase("*"+itemManager.getItemComposition(comp.getId()).getName()+"*").first().isPresent();
        }).forEach(loot -> {
            sendGameMessage("Need to loot: " + itemManager.getItemComposition(loot.getId()).getName());
            lootReceived.add(loot);
        });
         */
    }

    WorldArea getEnemyAttackRadiusArea() {
        Player player = client.getLocalPlayer();

        return new WorldArea(safespotTile.getX() - config.SafespotNPCRadius(),
                safespotTile.getY() - config.SafespotNPCRadius(),
                config.SafespotNPCRadius() * 2 + 1,
                config.SafespotNPCRadius() * 2 + 1,
                player.getWorldLocation().getPlane());
    }

    private void handleFight() {
        if (enemyIsSlayer()) {
            handleSlayerEnemies();
        }
        if (consumableCheck()) { //Handle any consumable
            handleConsumable();
        }

        if (timeout > 0) {
            timeout--;
            return;
        }

        //Fight stuff starts here
        if (config.enableSpec()) {
            if (specialAttack >= config.specAtAmount()) {
                util.toggleGear(util.getGearNames(config.SpecWeapons())); // Handle Special Attack. Switch to spec gear first.
                if (!specialAttackEnabled) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 38862884, -1, -1);
                }
            } else {
                //Swap back to main weapons if not enough spec attack
                util.toggleGear(util.getGearNames(config.MainWeapons()));
            }
        }

        if (config.UseSafespot()) {
            if (!isStandingOnSafespot()) {
                overlay.infoStatus = "Moving to safespot";
                if (safespotTile != null && !EthanApiPlugin.isMoving()) {
                    MousePackets.queueClickPacket();
                    MovementPackets.queueMovement(safespotTile);
                }
                return;
            }
            if (config.AlchItems() && hasItemToAlch()) {
                handleAlch();
            }
            handleEnemyAttack();
            return;
        }
        if (config.AlchItems() && hasItemToAlch()) {
            handleAlch();
        }
        handleEnemyAttack();
    }

    private int calculateNPCHealth(NPC target) {
        // Based on OpponentInfoOverlay HP calculation
        if (target == null || target.getName() == null) {
            return -1;
        }

        final int healthScale = target.getHealthScale();
        final int healthRatio = target.getHealthRatio();
        final Integer maxHealth = npcManager.getHealth(target.getId());

        if (healthRatio < 0 || healthScale <= 0 || maxHealth == null) {
            return -1;
        }

        return (int) ((maxHealth * healthRatio / healthScale) + 0.5f);
    }

    private boolean enemyIsSlayer() {
        if (client.getLocalPlayer().getInteracting() != null) {
            Actor intr = client.getLocalPlayer().getInteracting();
            if (intr instanceof NPC) {
                NPC npc = (NPC) intr;
                for (SlayerNpcs n : SlayerNpcs.values()) {
                    if (n.getNpcIDs().contains(npc.getId())) {
                        slayerItemID = n.getSlayerItemID();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void handleEnemyAttack() {
        Player player = client.getLocalPlayer();

        if (EthanApiPlugin.isMoving()) {
            return;
        }

        if (player.isInteracting()) {
            overlay.infoStatus = "Fighting: " + Text.removeTags(player.getInteracting().getName());
            timeout = tickDelay();
            return;
        }

        var query = NPCs.search().withAction("Attack")
                //.noOneInteractingWith()
                .filter(npcs -> npcs.getName() != null && npcs.getName().equalsIgnoreCase(config.EnemyNPCName()))
                .walkable();

        if (config.UseSafespotNPCRadius()) {
            query = query.withinWorldArea(enemyAttackRadiusArea);
        }

        if (!enemyIsSlayer()) {
            query = query.alive();
        }

        if (!config.ignoreLevels().isEmpty()) {
            List<String> levels = getListNames(config.ignoreLevels());
            query = query.filter(npcs -> {
                for (String level : levels) {
                    var lvl = 0;
                    try { // This prevented null if some weird shit or blanks were found in the 'levels' list
                        lvl = Integer.parseInt(level);
                    } catch(NumberFormatException nfe) {
                        continue;
                    }

                    if (npcs.getCombatLevel() == lvl) {
                        return false;
                    }
                }
                return true;
            });
        }

        query.noOneInteractingWith();

        query.nearestByPath()
                .ifPresentOrElse(x -> {
                    overlay.infoStatus = "Attacking: " + Text.removeTags(x.getName());
                    NPCInteraction.interact(x, "Attack");
                }, () -> {
                    overlay.infoStatus = "No NPC Present";
                    timeout = tickDelay();
                });
    }

    private boolean consumableCheck() {
        if (prayer <= config.PrayerLowAmount() && config.PrayerLowAmount() > 0) {
            return true;
        }

        if (hitpoints <= config.HealthLowAmount() && config.HealthLowAmount() > 0) {
            return true;
        }

        if (attack <= config.SuperAttackDrinkAmount() && config.SuperAttackDrinkAmount() > 0) {
            return true;
        }

        if (strength <= config.SuperStrengthDrinkAmount() && config.SuperStrengthDrinkAmount() > 0) {
            return true;
        }

        if (defence <= config.SuperDefenceDrinkAmount() && config.SuperDefenceDrinkAmount() > 0) {
            return true;
        }

        if (ranged <= config.RangedDrinkAmount() && config.RangedDrinkAmount() > 0) {
            return true;
        }

        return false;
    }

    private void handleConsumable() {
        if (prayer <= config.PrayerLowAmount() && config.PrayerLowAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*prayer potion*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Prayer Potion";
                InventoryInteraction.useItem(x, "Drink");
            });
        }

        if (hitpoints <= config.HealthLowAmount() && config.HealthLowAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*" + config.FoodName() + "*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Eating";
                InventoryInteraction.useItem(x, "Eat", "Drink");
            });
        }

        if (attack <= config.SuperAttackDrinkAmount() && config.SuperAttackDrinkAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*super attack*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Super Attack";
                InventoryInteraction.useItem(x, "Drink");
            });
        }

        if (strength <= config.SuperStrengthDrinkAmount() && config.SuperStrengthDrinkAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*super strength*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Super Strength";
                InventoryInteraction.useItem(x, "Drink");
            });
        }

        if (defence <= config.SuperDefenceDrinkAmount() && config.SuperDefenceDrinkAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*super defence*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Super Defence";
                InventoryInteraction.useItem(x, "Drink");
            });
        }

        if (ranged <= config.RangedDrinkAmount() && config.RangedDrinkAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*ranging potion*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Ranged potion";
                InventoryInteraction.useItem(x, "Drink");
            });
        }
    }

    private void handleAlch() {
        List<String> alch_whitelist = getListNames(config.alchWhitelist());
        Widget alch_spell = client.getWidget(14286892);

        for (String alchItem : getListNames(config.alchItemName())) {
            nameContainsNoCase(alchItem).filter(x -> !alch_whitelist.contains(Text.removeTags(x.getName().toLowerCase()))).first().ifPresent(item -> {
                overlay.infoStatus = "Alching: " + Text.removeTags(item.getName());
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(alch_spell, item);
                timeout = 5;
            });
        }
    }

    private boolean hasItemToAlch() { //TODO can probably redo this entirely but works for now kek
        List<String> alch_whitelist = getListNames(config.alchWhitelist());
        boolean hasAlchable = false;

        for (String alchItem : getListNames(config.alchItemName().toLowerCase())) {
            hasAlchable = nameContainsNoCase(alchItem).filter(x -> !alch_whitelist.contains(Text.removeTags(x.getName().toLowerCase()))).first().isPresent();
            if (hasAlchable) {
                break;
            }
        }
        return hasAlchable;
    }

    private List<String> getListNames(String list) {
        return Arrays.stream(list.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public static ItemQuery nameContainsNoCase(String name) {
        return Inventory.search().filter(widget -> widget.getName().toLowerCase().contains(name.toLowerCase()));
    }

    private boolean needsToLoot() {
        return !lootReceived.isEmpty();
    }

    private boolean isStandingOnSafespot() {
        if (config.safespotRoam()) {
            return playerLocation.isInArea(enemyAttackRadiusArea);
        }

        return playerLocation.distanceTo(safespotTile) == 0;
    }

    private boolean hasCannon() {
        if (!isBankOpen()) {
            return (Inventory.search().matchesWildCardNoCase("*Cannon base*").first().isPresent() &&
                    Inventory.search().matchesWildCardNoCase("*Cannon barrels*").first().isPresent() &&
                    Inventory.search().matchesWildCardNoCase("*Cannon furnace*").first().isPresent() &&
                    Inventory.search().matchesWildCardNoCase("*Cannon stand*").first().isPresent() &&
                    Inventory.search().matchesWildCardNoCase("*Cannonball*").first().isPresent());
        }
        return (BankInventory.search().matchesWildCardNoCase("*Cannon base*").first().isPresent() &&
                BankInventory.search().matchesWildCardNoCase("*Cannon barrels*").first().isPresent() &&
                BankInventory.search().matchesWildCardNoCase("*Cannon furnace*").first().isPresent() &&
                BankInventory.search().matchesWildCardNoCase("*Cannon stand*").first().isPresent() &&
                BankInventory.search().matchesWildCardNoCase("*Cannonball*").first().isPresent());
    }

    private boolean needsToRepairOrReloadCannon() {
        return TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Repair").first().isPresent() ||
                remainingCannonballs < config.CannonLowAmount();
    }

    private void handlePickUpCannon() {
        if (Inventory.getEmptySlots() >= 4) {
            TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Pick-up").first().ifPresent(x -> {
                overlay.infoStatus = "Picking up cannon";
                TileObjectInteraction.interact(x, "Pick-up");
            });
        }
    }

    private void handleCannon() {
        if (timeout > 0) {
            timeout--;
            return;
        }
        //Out of food, cannon still set up so need to pick up.
        if (isCannonSetUp() && !hasFood() && hitpoints < config.HealthLowAmount()) {
            handlePickUpCannon();
            return;
        }
        if (TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").first().isEmpty()) {
            if (playerLocation.distanceTo(cannonSpot) != 0) {
                overlay.infoStatus = "Moving to cannon spot";
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(cannonSpot);
                return;
            }
            Inventory.search().withAction("Set-up").withName("Cannon base").first().ifPresent(x -> {
                overlay.infoStatus = "Setting up cannon";
                InventoryInteraction.useItem(x, "Set-up");
                cannonFired = false;
                timeout = 13;
            });
            return;
        }
        if (!cannonFired) { // Fires cannon first time
            TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Fire").first().ifPresent(x -> {
                overlay.infoStatus = "Starting up cannon";
                TileObjectInteraction.interact(x, "Fire");
                cannonFired = true;
                timeout = 3;
            });
            return;
        }

        TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Repair").first().ifPresent(x -> {
            overlay.infoStatus = "Repairing cannon";
            TileObjectInteraction.interact(x, "Repair");
        });

        TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Fire").first().ifPresent(x -> {
            overlay.infoStatus = "Reloading cannon";
            TileObjectInteraction.interact(x, "Fire");
            timeout = 3;
        });
    }

    private boolean isCannonSetUp() {
        return TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Repair").first().isPresent() ||
                TileObjects.search().atLocation(cannonSpot).withName("Dwarf multicannon").withAction("Fire").first().isPresent();
    }

    private void disablePrayers() {
        if (EthanApiPlugin.isQuickPrayerEnabled()) {
            overlay.infoStatus = "Deactivating prayer";
            InteractionHelper.togglePrayer();
        }
    }

    private boolean hasFood() {
        if (isBankOpen()) {
            return BankInventory.search().matchesWildCardNoCase("*" + config.FoodName() + "*").first().isPresent();
        }
        return Inventory.search().matchesWildCardNoCase("*" + config.FoodName() + "*").first().isPresent();
    }

    private boolean hasBonesOrAshes() {
        if (isBankOpen()) {
            return BankInventory.search().matchesWildCardNoCase("*bones*").withAction("Bury").first().isPresent() ||
                    BankInventory.search().matchesWildCardNoCase("*ashes*").withAction("Scatter").first().isPresent();
        }
        return Inventory.search().matchesWildCardNoCase("*bones*").withAction("Bury").first().isPresent() ||
                Inventory.search().matchesWildCardNoCase("*ashes*").withAction("Scatter").first().isPresent();
    }

    private boolean hasTeleport() {
        if (isBankOpen()) {
            return BankInventory.search().matchesWildCardNoCase("*teleport*").withAction("Break").first().isPresent();
        }
        return Inventory.search().matchesWildCardNoCase("*teleport*").withAction("Break").first().isPresent();
    }

    private void handleTeleport() {
        Inventory.search().matchesWildCardNoCase("*teleport*").withAction("Break").first().ifPresentOrElse(x -> {
            InventoryInteraction.useItem(x, "Break");
            cannonFired = false;
        }, () -> {
            sendGameMessage("Out of falador teleports, shutting down");
            enablePlugin = false;
        });
    }

    private List<String> getLootNames(String loot) {
        return Arrays.stream(loot.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }


    private WorldPoint getCoords(String coords) {
        String coordz = coords.chars().filter(c -> !Character.isWhitespace(c))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());

        List<Integer> configCoords = Arrays.stream(coordz.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        WorldPoint goal = new WorldPoint(configCoords.get(0), configCoords.get(1), client.getLocalPlayer().getWorldLocation().getPlane());
        return goal;
    }

    private final HotkeyListener pluginToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            togglePlugin();
        }
    };

    public void togglePlugin() {
        enablePlugin = !enablePlugin;
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        if (!enablePlugin) {
            sendGameMessage("Auto Combat disabled.");
            enemyArea.clear();
            enemyAttackArea.clear();
        } else {
            sendGameMessage("Auto Combat enabled.");
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!enablePlugin) {
            return;
        }

        ChatMessageType chatMessageType = event.getType();

        if (chatMessageType != ChatMessageType.GAMEMESSAGE && chatMessageType != ChatMessageType.SPAM) {
            return;
        }

        if (config.StopSlayerIfTaskDone() && event.getMessage().contains("have completed your task")) {
            disablePrayers();
            if (hasTeleport() && !isCannonSetUp()) {
                handleTeleport();
            }
            if (isCannonSetUp()) {
                handlePickUpCannon();
            }
            sendGameMessage("Slayer task completed, disabling plugin.");
            clientThread.invoke(() -> {
                if (EthanApiPlugin.isMoving()) {
                    MousePackets.queueClickPacket();
                    MovementPackets.queueMovement(client.getLocalPlayer().getWorldLocation());
                }
            });

            resetVals();
            enablePlugin = false;
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarpId() == VarPlayer.CANNON_AMMO) {
            remainingCannonballs = varbitChanged.getValue();
        }
    }

    //TODO Move all of this back into API
    private int tickDelay() {
        int tickLength = (int) randomDelay(config.tickDelayWeightedDistribution(), config.tickDelayMin(), config.tickDelayMax(), config.tickDelayDeviation(), config.tickDelayTarget());
        log.debug("tick delay for {} ticks", tickLength);
        return tickLength;
    }

    public long randomDelay(boolean weightedDistribution, int min, int max, int deviation, int target) {
        if (weightedDistribution) {
            return (long) clamp((-Math.log(Math.abs(random.nextGaussian()))) * deviation + target, min, max);
        } else {
            /* generate a normal even distribution random */
            return (long) clamp(Math.round(random.nextGaussian() * deviation + target), min, max);
        }
    }

    private double clamp(double val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public void openNearestBank() {
        if (!isBankOpen()) { //Opens bank
            TileObjects.search().withName("Bank booth").nearestByPath().ifPresentOrElse(x -> {
                overlay.infoStatus = "Banking";
                TileObjectInteraction.interact(x, "Bank");
            }, () -> {
                overlay.infoStatus = "Bank not found";
            });
        }
    }

    public int getRandomIntBetweenRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public void sendGameMessage(String message) {
        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append(message)
                .build();

        chatMessageManager
                .queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());
    }

    public void handlePrayFlick() { //Thanx ethan
        if (forceTab) {
            client.runScript(915, 3);
            forceTab = false;
        }
        if (client.getWidget(5046276) == null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB), "Setup");
            forceTab = true;
        }

        if (EthanApiPlugin.isQuickPrayerEnabled()) {
            InteractionHelper.togglePrayer();
        }
        InteractionHelper.togglePrayer();
    }

    public void handleRun(int minEnergy, int randMax) {
        if (nextRunEnergy < minEnergy || nextRunEnergy > minEnergy + randMax) {
            nextRunEnergy = getRandomIntBetweenRange(minEnergy, minEnergy + getRandomIntBetweenRange(0, randMax));
        }
        if ((client.getEnergy() / 100) > nextRunEnergy ||
                client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) != 0) {
            if (!isRunEnabled()) {
                nextRunEnergy = 0;
                Widget runOrb = client.getWidget(WidgetInfo.MINIMAP_RUN_ORB);

                if (runOrb != null) {
                    enableRun();
                }
            }
        }
    }

    public boolean isRunEnabled() {
        return client.getVarpValue(173) == 1;
    }


    public void enableRun() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
    }

    public boolean isBankOpen() {
        return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null);
    }

    public boolean isBankPinOpen() {
        return (client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null);
    }

}
