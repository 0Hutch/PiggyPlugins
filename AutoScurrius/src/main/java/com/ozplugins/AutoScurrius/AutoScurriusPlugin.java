package com.ozplugins.AutoScurrius;


import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.EthanApiPlugin.Collections.query.QuickPrayer;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Utility.WorldAreaUtility;
import com.example.InteractionApi.*;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
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
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.example.PathingTesting.PathingTesting.walkTo;
import static com.ozplugins.AutoScurrius.AutoScurriusState.*;

@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto Scurrius</html>",
        description = "Kills Scurrius",
        enabledByDefault = false,
        tags = {"Oz"}
)
@Slf4j
public class AutoScurriusPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    public AutoScurriusConfig config;
    @Inject
    private AutoScurriusOverlay overlay;
    @Inject
    private AutoScurriusTileOverlay tileOverlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    public ItemManager itemManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    public boolean enablePlugin = false;
    public boolean looting = false;
    public int timeout = 0;
    public int[] sewerRegions = {13210, 12954};

    public WorldPoint lootTile = null;
    Instant botTimer;
    WorldPoint goalTile;
    List<WorldPoint> safeTiles = new ArrayList<>();
    List<WorldPoint> enemyArea = new ArrayList<>();
    public Queue<ItemStack> lootReceived = new LinkedList<>();

    public Player player;

    private int prayerPoints = 0;
    private int rangedLevel = 0;
    private int hp = 0;
    private int attack = 0;
    private int strength = 0;
    private int defence = 0;
    private int nextRunEnergy;

    public String alchables = "Rune med helm,Earth battlestaff,Mystic eath staff,Rune warhammer," +
            "Rune chainbody,Runite ore,Rune sq shield,Rune full helm,Rune battleaxe,Adamant platebody";

    boolean forceTab = false;
    boolean depositedAll = false;
    public final WorldArea VARROCK_AREA = new WorldArea(new WorldPoint(3140, 3382, 0), 143, 100);
    AutoScurriusState state;
    private int projectile;
    int specialAttack = 0;
    boolean specialAttackEnabled = false;
    public int kc = 0;

    @Provides
    private AutoScurriusConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoScurriusConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        overlayManager.add(tileOverlay);
        timeout = 0;
        nextRunEnergy = 0;
        projectile = 0;
        kc = 0;
        state = null;
        safeTiles.clear();
        enemyArea.clear();
        lootReceived.clear();
        depositedAll = false;
        botTimer = Instant.now();
        clientThread.invoke(() -> {
            prayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
            rangedLevel = client.getBoostedSkillLevel(Skill.RANGED);
            hp = client.getBoostedSkillLevel(Skill.HITPOINTS);
            attack = client.getBoostedSkillLevel(Skill.ATTACK);
            strength = client.getBoostedSkillLevel(Skill.STRENGTH);
            defence = client.getBoostedSkillLevel(Skill.DEFENCE);
            specialAttack = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
            specialAttackEnabled = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1;
        });
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        overlayManager.remove(tileOverlay);
        timeout = 0;
        nextRunEnergy = 0;
        projectile = 0;
        kc = 0;
        looting = false;
        lootTile = null;
        depositedAll = false;
        state = null;
        safeTiles.clear();
        enemyArea.clear();
        lootReceived.clear();
        prayerPoints = 0;
        rangedLevel = 0;
        hp = 0;
        attack = 0;
        strength = 0;
        defence = 0;
        player = null;
        botTimer = null;
    }

    public AutoScurriusState getState() {
        Player player = client.getLocalPlayer();

        if (player == null) {
            return UNHANDLED_STATE;
        }

        if (EthanApiPlugin.isMoving() && !isInFightRoom()) {
            return MOVING;
        }

        if (timeout > 0) {
            return TIMEOUT;
        }
        if (isBankPinOpen()) {
            return BANK_PIN;
        }

        if (isBankOpen() && shouldBank()) {
            return HANDLE_RESTOCK;
        }

        if (!shouldBank()) {
            if (inSewer()) {
                return GO_TO_ROOM;
            }

            if (isInFightRoom()) {
                return HANDLE_FIGHT;
            }

            if (inVarrock()) {
                return ENTER_SEWER;
            }
        } else {
            return HANDLE_BANK;
        }

        return UNHANDLED_STATE;
    }

    public boolean inVarrock() {
        return client.getLocalPlayer().getWorldArea().intersectsWith(VARROCK_AREA);
    }

    public boolean isInFightRoom() {
        return client.isInInstancedRegion();
    }

    private void handleFight() {
        Optional<NPC> scurrius = NPCs.search().alive().walkable().withName("Scurrius").withAction("Attack").nearestByPath();
        Optional<NPC> rats = NPCs.search().alive().walkable().withName("Giant rat").withAction("Attack").nearestByPath();

        handlePrayers();

        scurrius.ifPresentOrElse(x -> {
            safeTiles = WorldAreaUtility.objectInteractableTiles(x);
            if (config.oneTickFlick()) {
                handlePrayFlick();
            }
        }, this::turnOffPrayers); //Else turn off prayers

        if (consumableCheck()) { //Handle any consumable
            handleConsumable();
        }

        getRockfall();
        safeTiles.removeAll(enemyArea);
        if (isStandingOnEnemytile()) {
            goalTile = safeTiles.stream()
                    .filter(x -> safeTiles.contains(x) && distanceBetweenListAndPoint(x, enemyArea) >= 1)
                    .collect(Collectors.toList()).stream()
                    .min(Comparator.comparingInt(x -> x.distanceTo(EthanApiPlugin.playerPosition())))
                    .orElse(null);

            overlay.infoStatus = "Dodging rockfall";
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(goalTile);
            return;
        }

        goalTile = null;
        if (rats.isPresent()) {
            toggleGear(getGearNames(config.MainWeapons()));
            overlay.infoStatus = "Attack Rats";
            NPCInteraction.interact(rats.get(), "Attack");
            return;
        }

        scurrius.ifPresent(x -> {
            if (config.enableSpec()) {
                if (specialAttack >= config.specAtAmount()) {
                    toggleGear(getGearNames(config.SpecWeapons())); // Handle Special Attack. Switch to spec gear first.
                    if (!specialAttackEnabled) {
                        MousePackets.queueClickPacket();
                        WidgetPackets.queueWidgetActionPacket(1, 38862885, -1, -1);
                    }
                } else {
                    //Swap back to main weapons if not enough spec attack
                    toggleGear(getGearNames(config.MainWeapons()));
                }
            }

            if (!player.isInteracting()) {
                overlay.infoStatus = "Attack Scurrius";
                NPCInteraction.interact(x, "Attack");
            }
        });

        if (scurrius.isEmpty()) {
            overlay.infoStatus = "Waiting for boss";
            projectile = 0;
            toggleGear(getGearNames(config.MainWeapons()));

            if (shouldEatFoodPile()) {
                TileObjects.search().nameContains("Food Pile").withAction("Eat").nearestByPath().ifPresent(x -> {
                    overlay.infoStatus = "Eating cheese";
                    TileObjectInteraction.interact(x, "Eat");
                });
                return;
            }

            if (config.LootItems() && needsToLoot()) {
                handleLoot();
                return;
            }

            if (config.AlchItems() && hasItemToAlch()) {
                handleAlch();
                return;
            }
            return;
        }
        overlay.infoStatus = "Fighting";
    }

    private boolean handleInstanceDialogue() { //Ty Poly
        Optional<Widget> gimInstance = Widgets.search().withTextContains("What type of instance").hiddenState(false).first();
        Optional<Widget> firstInstance = Widgets.search().withTextContains("sure you want to climb through the broken").hiddenState(false).first();
        if (gimInstance.isPresent()) {
            WidgetPackets.queueResumePause(14352385, 1);
            return true;
        }
        if (firstInstance.isPresent()) {
            WidgetPackets.queueResumePause(14352385, 2);
            return true;
        }
        return false;
    }

    public void toggleGear(List<String> gearNames) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        swapGear(gearNames);
    }

    public void swapGear(List<String> gearNames) {
        for (String gearName : gearNames) {
            nameContainsNoCase(gearName).first().ifPresent(item -> {
                InventoryInteraction.useItem(item, "Equip", "Wield", "Wear");
            });
        }
    }

    public List<String> getGearNames(String gear) {
        return Arrays.stream(gear.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public void handlePrayers() {
        if (projectile == 0) {
            if (!EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MELEE)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 14); //quickPrayer melee
            }
            return;
        }

        if (projectile == 2640 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MAGIC)) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 12); //quickPrayer magic
            return;
        }

        if (projectile == 2642 && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MISSILES)) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 13); //quickPrayer range
        }
    }

    private boolean consumableCheck() {
        if (prayerPoints <= config.PrayerLowAmount() && config.PrayerLowAmount() > 0) {
            return true;
        }

        if (hp <= config.healthLowAmount() && config.healthLowAmount() > 0) {
            return true;
        }

        if (attack <= config.AttackDrinkAmount() && config.AttackDrinkAmount() > 0) {
            return true;
        }

        if (strength <= config.StrengthDrinkAmount() && config.StrengthDrinkAmount() > 0) {
            return true;
        }

        if (defence <= config.DefenceDrinkAmount() && config.DefenceDrinkAmount() > 0) {
            return true;
        }

        if (rangedLevel <= config.RangedDrinkAmount() && config.RangedDrinkAmount() > 0) {
            return true;
        }

        return false;
    }

    public void depositAll() {
        overlay.infoStatus = "Depositing inventory";
        Widget depositInventory = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(depositInventory, "Deposit", "Deposit inventory");
        depositedAll = true;
    }

    private void handleConsumable() {
        if (prayerPoints <= config.PrayerLowAmount() && config.PrayerLowAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*prayer potion*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Prayer Potion";
                InventoryInteraction.useItem(x, "Drink");
            });
        }

        if (hp <= config.healthLowAmount() && config.healthLowAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*" + config.foodName() + "*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Eating";
                InventoryInteraction.useItem(x, "Eat", "Drink");
            });
        }

        if (attack <= config.AttackDrinkAmount() && config.AttackDrinkAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*attack*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Attack potion";
                InventoryInteraction.useItem(x, "Drink");
            });
        }

        if (strength <= config.StrengthDrinkAmount() && config.StrengthDrinkAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*strength*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Strength potion";
                InventoryInteraction.useItem(x, "Drink");
            });
        }

        if (defence <= config.DefenceDrinkAmount() && config.DefenceDrinkAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*defence*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Defence potion";
                InventoryInteraction.useItem(x, "Drink");
            });
        }

        if (rangedLevel <= config.RangedDrinkAmount() && config.RangedDrinkAmount() > 0) {
            Inventory.search().matchesWildCardNoCase("*ranging potion*").onlyUnnoted().first().ifPresent(x -> {
                overlay.infoStatus = "Drinking Ranged potion";
                InventoryInteraction.useItem(x, "Drink");
            });
        }
    }

    private boolean hasItemToAlch() { //TODO can probably redo this entirely but works for now kek
        boolean hasAlchable = false;

        for (String alchItem : getListNames(config.alchItemName().toLowerCase())) {
            hasAlchable = nameContainsNoCase(Text.removeTags(alchItem.toLowerCase())).first().isPresent();
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

    private void handleRestock() {
        timeout = 1;
        CloseWithdrawXWidget();
        if (!depositedAll) {
            depositAll();
            return;
        }

        if (config.AlchItems()
                && BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*Nature rune*").empty()) {
            if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*Nature rune*").empty()) {
                Bank.search().matchesWildCardNoCase("*Nature rune*").first().ifPresent(x -> {
                    BankInteraction.withdrawX(x, 100);
                });
            }
            if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*Fire rune*").empty()) {
                Bank.search().matchesWildCardNoCase("*Fire rune*").first().ifPresent(x -> {
                    BankInteraction.withdrawX(x, 500);
                });
            }
        }

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("arrock teleport").empty() && config.varrockTravel() == VarrockTele.TELETAB) {
            overlay.infoStatus = "Withdrawing teletabs";
            Bank.search().matchesWildCardNoCase("*arrock teleport*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, 10);
            });
        } else if (BankInventory.search().matchesWildCardNoCase("*une pouch*").first().isEmpty() && config.varrockTravel() == VarrockTele.SPELL) {
            overlay.infoStatus = "Withdrawing Rune Pouch";
            Bank.search().matchesWildCardNoCase("*une pouch*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, 1);
            });
        }

        getGearNames(config.SpecWeapons()).forEach(x -> {
            if (BankInventory.search().matchesWildCardNoCase("*" + x + "*").empty()) {
                Bank.search().matchesWildCardNoCase("*" + x + "*").first().ifPresent(y -> {
                    overlay.infoStatus = "Withdrawing Spec Gear";
                    BankInteraction.withdrawX(y, 1);
                });
            }
        });

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*rayer potion*").empty() && config.prayerAmount() > 0) {
            overlay.infoStatus = "Withdrawing Prayer Potions";
            Bank.search().matchesWildCardNoCase("*rayer potion*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, config.prayerAmount());
            });
            return;
        }

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*ttack potion*").empty() && config.attackAmount() > 0) {
            overlay.infoStatus = "Withdrawing attack potion";
            Bank.search().matchesWildCardNoCase("*ttack potion*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, config.attackAmount());
            });
            return;
        }

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*trength potion*").empty() && config.strengthAmount() > 0) {
            overlay.infoStatus = "Withdrawing strength potion";
            Bank.search().matchesWildCardNoCase("*trength potion*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, config.strengthAmount());
            });
            return;
        }

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*efence potion*").empty() && config.defenceAmount() > 0) {
            overlay.infoStatus = "Withdrawing defence potions";
            Bank.search().matchesWildCardNoCase("*efence potion*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, config.defenceAmount());
            });
            return;
        }

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*uper attack*").empty() && config.superAttackAmount() > 0) {
            overlay.infoStatus = "Withdrawing attack potions";
            Bank.search().matchesWildCardNoCase("*uper attack*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, config.superAttackAmount());
            });
            return;
        }

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*uper strength*").empty() && config.superStrengthAmount() > 0) {
            overlay.infoStatus = "Withdrawing strength potions";
            Bank.search().matchesWildCardNoCase("*uper strength*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, config.superStrengthAmount());
            });
            return;
        }

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*uper defence*").empty() && config.superDefenceAmount() > 0) {
            overlay.infoStatus = "Withdrawing defence potions";
            Bank.search().matchesWildCardNoCase("*uper defence*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, config.superDefenceAmount());
            });
            return;
        }

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*anging potion*").empty() && config.rangingAmount() > 0) {
            overlay.infoStatus = "Withdrawing ranging potions";
            Bank.search().matchesWildCardNoCase("*anging potion*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, config.rangingAmount());
            });
            return;
        }

        if (BankInventory.search().onlyUnnoted().matchesWildCardNoCase("*" + config.foodName() + "*").empty()) {
            overlay.infoStatus = "Withdrawing :" + config.foodName();
            Bank.search().matchesWildCardNoCase("*" + config.foodName() + "*").first().ifPresent(x -> {
                BankInteraction.withdrawX(x, config.foodAmount());
            });
            return;
        }

    }

    private void handleGoToBank() {
        toggleGear(getGearNames(config.MainWeapons()));
        safeTiles.clear();
        goalTile = null;
        enemyArea.clear();
        depositedAll = false;
        projectile = 0;
        turnOffPrayers();

        if (inVarrock()) {
            TileObjects.search().withAction("Bank").nearestByPath().ifPresentOrElse(x -> {
                overlay.infoStatus = "Opening bank";
                TileObjectInteraction.interact(x, "Bank");
            }, () -> {
                overlay.infoStatus = "Walking to east bank";
                walkTo(new WorldPoint(3253, 3421, 0));
            });
            timeout = 3;
            return;
        }

        switch (config.varrockTravel()) {
            case TELETAB:
                Inventory.search().withAction("Varrock").withName("Varrock teleport").first().ifPresentOrElse(x -> {
                    overlay.infoStatus = "Teleporting to Varrock";
                    InventoryInteraction.useItem(x, "Varrock");
                    timeout = 4;
                }, () -> {
                    overlay.infoStatus = "Out of teleports";
                });
                break;

            case SPELL:
                Widget varrockTeleportWidget = client.getWidget(WidgetInfoExtended.SPELL_VARROCK_TELEPORT.getPackedId());
                if (varrockTeleportWidget != null) {
                    overlay.infoStatus = "Teleporting to Varrock";
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetAction(varrockTeleportWidget, "Cast");
                }
                break;
        }
    }

    public List<WorldPoint> getRockfall() {
        enemyArea.clear();
        client.getGraphicsObjects().forEach(r -> {
            if (r.getId() == 2644) {
                enemyArea.add(WorldPoint.fromLocal(client, r.getLocation()));
            }
        });
        return enemyArea;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !enablePlugin) {
            forceTab = false;
            return;
        }
        prayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
        rangedLevel = client.getBoostedSkillLevel(Skill.RANGED);
        hp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        attack = client.getBoostedSkillLevel(Skill.ATTACK);
        strength = client.getBoostedSkillLevel(Skill.STRENGTH);
        defence = client.getBoostedSkillLevel(Skill.DEFENCE);
        specialAttack = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
        specialAttackEnabled = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1;
        player = client.getLocalPlayer();
        state = getState();

        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case HANDLE_RESTOCK:
                handleRestock();
                break;

            case HANDLE_BANK:
                handleGoToBank();
                break;

            case GO_TO_ROOM:
                goToScurriusRoom();
                break;

            case ENTER_SEWER:
                enterSewer();
                break;

            case HANDLE_FIGHT:
                handleFight();
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "Ded";
                break;

            case BANK_PIN:
            case MOVING:
            case ANIMATING:
            case IDLE:
                CloseWithdrawXWidget();
                timeout = 1;
                break;
        }

    }

    private void goToScurriusRoom() {
        if (handleInstanceDialogue()) {
            return;
        }
        TileObjects.search().withAction("Climb-through (private)").withName("Broken bars").nearestByPath().ifPresentOrElse(x -> {
            overlay.infoStatus = "Going to boss room";
            TileObjectInteraction.interact(x, "Climb-through (private)");
        }, () -> {
            walkTo(new WorldPoint(3280, 9870, 0));
        });
    }

    public boolean isStandingOnEnemytile() {
        return enemyArea.contains(EthanApiPlugin.playerPosition());
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event) { //THANKS LUNATIK
        final Projectile proj = event.getProjectile();
        final int MAGIC_ATTACK = 2640;
        final int RANGED_ATTACK = 2642;
        boolean isMagicAttack = proj.getId() == MAGIC_ATTACK;
        boolean isRangedAttack = proj.getId() == RANGED_ATTACK;

        if (!(isMagicAttack || isRangedAttack)) {
            return;
        }

        if (proj.getRemainingCycles() >= 35 && proj.getRemainingCycles() <= 50) {
            if (isMagicAttack) {
                projectile = MAGIC_ATTACK;
            }
            if (isRangedAttack) {
                projectile = RANGED_ATTACK;
            }
        } else if (proj.getRemainingCycles() <= 15) {
            projectile = 0;
        }
    }

    public boolean shouldEatFoodPile() {
        return client.getVarbitValue(9581) == 0 && hp < config.healthLowAmount();
    }

    private void handleAlch() {
        Widget alch_spell = client.getWidget(14286892);

        for (String alchItem : getListNames(config.alchItemName())) {
            nameContainsNoCase(alchItem).first().ifPresent(item -> {
                overlay.infoStatus = "Alching: " + Text.removeTags(item.getName());
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(alch_spell, item);
            });
        }
    }

    private void handleLoot() {
        ItemStack item_to_loot = lootReceived.peek();
        ItemComposition item_comp = itemManager.getItemComposition(item_to_loot.getId());
        String item_name = item_comp.getName();
        //WorldPoint item_worldpoint = WorldPoint.fromLocal(client, item_to_loot.getLocation());

        if (item_comp.isStackable() && Inventory.search().withId(item_to_loot.getId()).first().isPresent()) {
            TileItems.search().matchesWildCardNoCase("*" + item_name + "*").nearestByPath().ifPresent(x -> {
                overlay.infoStatus = "Looting: " + item_name;
                TileItemPackets.queueTileItemAction(x, false);
                lootReceived.remove();
                timeout = 4;
            });
            return;
        }

        if (Inventory.full()) {
            if (config.AlchItems() && hasItemToAlch()) {
                handleAlch();
                return;
            }

            if ((hasFood() && config.EatMakeSpace())
                    || TileItems.search().itemsMatchingWildcardsNoCase("spine").nearestByPath().isPresent()) { //In case inventory is full, we always want spine
                Inventory.search().withAction("Eat").onlyUnnoted().first().ifPresent(x -> {
                    overlay.infoStatus = "Eating";
                    InventoryInteraction.useItem(x, "Eat", "Drink");
                });
            }
            return;
        }

        TileItems.search().matchesWildCardNoCase("*" + item_name + "*").nearestByPath().ifPresent(x -> {
            overlay.infoStatus = "Looting: " + item_name;
            TileItemPackets.queueTileItemAction(x, false);
            lootReceived.remove();
            timeout = 4;
        });
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event) {
        if (!enablePlugin || !config.LootItems()) return;
        Collection<ItemStack> items = event.getItems();
        items.stream().filter(item -> {
            ItemComposition comp = itemManager.getItemComposition(item.getId());
            return getLootNames(Text.removeTags(config.lootList().toLowerCase())).contains(Text.removeTags(comp.getName().toLowerCase()));
        }).forEach(it -> {
            log.info("Adding to lootQueue: " + it.getId());
            lootReceived.add(it);
        });
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (event.getNpc().getId() == 7222 && isInFightRoom()) {
            kc++;
        }
    }

    private boolean hasFood() {
        if (isBankOpen()) {
            return BankInventory.search().matchesWildCardNoCase("*" + config.foodName() + "*").first().isPresent();
        }
        return Inventory.search().matchesWildCardNoCase("*" + config.foodName() + "*").first().isPresent();
    }

    public void turnOffPrayers() {
        if (EthanApiPlugin.isQuickPrayerEnabled()) {
            overlay.infoStatus = "Deactivating prayer";
            InteractionHelper.togglePrayer();
        }
    }


    public List<String> getAlchables() {
        return Arrays.stream(alchables.split(",")).map(String::trim).collect(Collectors.toList());
    }

    public boolean hasAlchables() {
        for (String s : getAlchables()) {
            if (hasItem(s))
                return true;
        }
        return false;
    }

    public static boolean hasItem(String name) {
        return getItemAmount(name, false) > 0;
    }

    public static int getItemAmount(String name, boolean stacked) {
        if (stacked) {
            return nameContainsNoCase(name).first().isPresent() ? nameContainsNoCase(name).first().get().getItemQuantity() : 0;
        }
        return nameContainsNoCase(name).result().size();
    }

    private List<String> getLootNames(String loot) {
        return Arrays.stream(loot.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private void enterSewer() {
        //TODO This is trash, gotta do something different and not use Pathingtesting
        WorldPoint sewerPoint = new WorldPoint(3236, 3458, 0);
        TileObjects.search().withAction("Climb-down").withName("Manhole").nearestToPoint(sewerPoint)
                .filter(x -> x.getWorldLocation().distanceTo(player.getWorldLocation()) < 15).ifPresentOrElse(x -> {
                    overlay.infoStatus = "Entering sewer";
                    TileObjectInteraction.interact(x, "Climb-down");
                    timeout = 3;

                }, () -> {
                    TileObjects.search().withAction("Open").withName("Manhole").nearestToPoint(sewerPoint)
                            .filter(x -> x.getWorldLocation().distanceTo(player.getWorldLocation()) < 15).ifPresentOrElse(x -> {
                                overlay.infoStatus = "Opening Manhole";
                                TileObjectInteraction.interact(x, "Open");
                                timeout = 3;
                            }, () -> {
                                overlay.infoStatus = "Walking to sewer";
                                sendGameMessage("Trying to walk to sewer.");
                                walkTo(sewerPoint);
                            });
                });
    }

    public boolean inSewer() {
        return inRegion(sewerRegions);
    }

    public boolean shouldBank() {
        if (hp < config.healthLowAmount() && !hasFood()) {
            return true;
        }
        if (Inventory.search().onlyUnnoted().nameContains("rayer potion").empty() && prayerPoints <= config.PrayerLowAmount()) {
            return true;
        }
        return (Inventory.full() && !hasFood()) || (!hasFood() && !hasAlchables());
        //return false;
    }

    public boolean inRegion(int... regions) {
        for (int region : regions) {
            if (inRegion(region)) {
                return true;
            }
        }
        return false;
    }

    public boolean inRegion(int region) {
        return client.getLocalPlayer().getWorldLocation().getRegionID() == region;
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        enablePlugin = !enablePlugin;
    }

    public boolean isBankPinOpen() {
        Widget bankPinWidget = client.getWidget(WidgetInfo.BANK_PIN_CONTAINER);
        return (bankPinWidget != null);
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

    public boolean isBankOpen() {
        return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null);
    }

    public static void CloseWithdrawXWidget() {//Thanks JesusCPT
        Optional<Widget> widget = Widgets.search().withId(10616873).first();
        if (widget.isPresent()) {
            EthanApiPlugin.getClient().runScript(299, 0, 1, 0);
        }
    }

    public boolean needsToLoot() {
        return !lootReceived.isEmpty() || TileItems.search().itemsMatchingWildcardsNoCase("spine").nearestByPath().isPresent();
    }

    public int distanceBetweenListAndPoint(WorldPoint tile, List<WorldPoint> cArea) {
        List<Integer> distances = new ArrayList<>();
        for (WorldPoint w : cArea) {
            distances.add(w.distanceTo2D(tile));
        }

        int distance = Collections.min(distances);

        return distance;
    }

    public boolean isRunEnabled() {
        return client.getVarpValue(173) == 1;
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

    public void enableRun() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
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
}