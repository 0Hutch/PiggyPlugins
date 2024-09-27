package com.ozplugins.AutoThiever;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.*;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.ozplugins.AutoThiever.AutoThieverState.*;

@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
@PluginDescriptor(
        name = "<html><font color=\"#0394FC\">[OZ]</font> Auto Thiever</font></html>",
        enabledByDefault = false,
        description = "Steals shit from stuff for you..",
        tags = {"Oz"}
)
@Slf4j
public class AutoThieverPlugin extends Plugin {
    protected static final Random random = new Random();
    Instant botTimer;
    boolean enablePlugin;

    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    AutoThieverConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private AutoThieverOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    AutoThieverState state;
    int timeout = 0;
    int pouches = 0;
    int hitpoints = 0;
    int prayer = 0;
    double successRate = 0;
    double successfulThieves = 0;
    double failedThieves = 0;
    private int nextRunEnergy;
    public int nextPouchOpen;
    boolean isOutOfDodgy = false;
    boolean isOutOfAncientBrews = false;
    public int shadowVeilCooldown = 0;
    boolean isShadowVeilActive = false;
    String npcToThieve;
    WorldArea LINDIR_HOUSE = new WorldArea(new WorldPoint(3242, 6069, 0), 4, 3);
    WorldArea ARDY_EAST_BANK = new WorldArea(new WorldPoint(2649, 3280, 0), 9, 8);
    List<Integer> ITEMS_TO_DEPOSIT = List.of(ItemID.DEATH_RUNE, ItemID.JUG_OF_WINE, ItemID.NATURE_RUNE,
            ItemID.FIRE_ORB, ItemID.DIAMOND, ItemID.ENHANCED_CRYSTAL_TELEPORT_SEED, ItemID.GOLD_ORE, ItemID.JUG);

    @Provides
    AutoThieverConfiguration provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoThieverConfiguration.class);
    }

    @Override
    protected void startUp() {
        timeout = 0;
        nextRunEnergy = 0;
        nextPouchOpen = 0;
        shadowVeilCooldown = 0;
        successRate = 0;
        successfulThieves = 0;
        failedThieves = 0;
        isOutOfDodgy = false;
        isOutOfAncientBrews = false;
        isShadowVeilActive = false;
        npcToThieve = null;
        enablePlugin = false;
        botTimer = Instant.now();
        state = null;
        keyManager.registerKeyListener(pluginToggle);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        resetVals();
    }

    private void resetVals() {
        overlayManager.remove(overlay);
        state = null;
        timeout = 0;
        nextRunEnergy = 0;
        nextPouchOpen = 0;
        shadowVeilCooldown = 0;
        successRate = 0;
        successfulThieves = 0;
        failedThieves = 0;
        npcToThieve = null;
        isShadowVeilActive = false;
        isOutOfDodgy = false;
        isOutOfAncientBrews = false;
        enablePlugin = false;
        keyManager.unregisterKeyListener(pluginToggle);
        botTimer = null;
    }

    public AutoThieverState getState() {
        Player player = client.getLocalPlayer();

        if (player == null) {
            return UNHANDLED_STATE;
        }
        if (shadowVeilCooldown > 0) {
            shadowVeilCooldown--;
        } else {
            isShadowVeilActive = false;
        }
        if (timeout > 0) {
            return TIMEOUT;
        }
        if (isBankPinOpen()) {
            overlay.infoStatus = "Bank Pin";
            return IDLE;
        }
        if (client.getLocalPlayer().getGraphic() == 245) {
            return STUNNED;
        }

        hitpoints = this.client.getBoostedSkillLevel(Skill.HITPOINTS);
        prayer = this.client.getBoostedSkillLevel(Skill.PRAYER);
        if ("Custom" == config.NPCToThieve().getNpcName()) {
            npcToThieve = config.CustomNPCName();
        } else {
            npcToThieve = config.NPCToThieve().getNpcName();
        }
        if (successfulThieves + failedThieves != 0) {
            successRate = successfulThieves / (successfulThieves + failedThieves) * 100;
        }
        handlePouch(config.MinPouches(), config.MaxPouches());
        if (isBankOpen() && hasItemsToDeposit()) {
            return DEPOSIT;
        }
        if (shouldWithdrawFood()) {
            return WITHDRAW_FOOD;
        }
        if (dodgyCheck()) {
            return WITHDRAW_DODGY;
        }
        if (useAncientBrewCheck()) {
            return WITHDRAW_ANCIENT_BREW;
        }
        if (shouldEatFood()) {
            return HANDLE_FOOD;
        }
        if (shouldFindBank()) {
            return FIND_BANK;
        }
        if (config.dodgyNecklace() && !isWearingDodgyNecklace() && hasDodgy()) {
            return HANDLE_DODGY_NECKLACE;
        }
        if (shadowVeilCheck()) {
            return HANDLE_SHADOW_VEIL;
        }
        if (handleAncientBrewCheck()) {
            return HANDLE_ANCIENT_BREW;
        }
        if (shouldThieve()) {
            return THIEVE;
        }
        return UNHANDLED_STATE;
    }

    @Subscribe
    private void onGameTick(GameTick tick) {
        if (!enablePlugin) {
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        state = getState();
        switch (state) {
            case TIMEOUT:
                timeout--;
                break;

            case HANDLE_FOOD:
                handleFood();
                break;

            case FIND_BANK:
                openNearestBank();
                break;

            case WITHDRAW_FOOD:
                handleWithdrawFood();
                break;

            case WITHDRAW_DODGY:
                handleWithdrawDodgy();
                break;

            case WITHDRAW_ANCIENT_BREW:
                handleWithdrawAncientBrews();
                break;

            case HANDLE_DODGY_NECKLACE:
                handleDodgyNecklace();
                break;

            case HANDLE_SHADOW_VEIL:
                handleShadowVeil();
                break;

            case DEPOSIT:
                handleDeposit();
                break;

            case THIEVE:
                handleThieve();
                break;

            case HANDLE_ANCIENT_BREW:
                handleAncientBrew();
                break;

            case UNHANDLED_STATE:
                overlay.infoStatus = "Dead";
                break;

            case STUNNED:
                overlay.infoStatus = "Stunned";
                timeout = 7;
                break;

            case MOVING:
            case BANK_PIN:
            case IDLE:
                handleRun(30, 20);
                timeout = tickDelay();
                break;
        }
    }

    private void handleShadowVeil() {
        overlay.infoStatus = "Casting Shadow veil";
        Widget shadow_veil = client.getWidget(14287028);
        int magicLevel = client.getRealSkillLevel(Skill.MAGIC);
        int animationID = client.getLocalPlayer().getAnimation();

        if (animationID == -1) { // Prevents attempting to cast if in mid-animation
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(shadow_veil, "Cast");
        }
        if (animationID == 8979) {
            shadowVeilCooldown = (int) Math.ceil((magicLevel * .6) * 1.7) + tickDelay(); //Can be done a lot better prolly?
            isShadowVeilActive = true;
        }
    }

    public boolean hasShadowVeilRequirements() { //Gotta clean up soon
        int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
        Optional<Widget> earth_rune = Inventory.search().matchesWildCardNoCase("*Earth rune*").first();
        Optional<Widget> cosmic_rune = Inventory.search().matchesWildCardNoCase("*Cosmic rune*").first();
        Optional<Widget> fire_rune = Inventory.search().matchesWildCardNoCase("*Fire rune*").first();
        Optional<EquipmentItemWidget> lava_staff = Equipment.search().matchesWildCardNoCase("*lava*").first();
        Optional<EquipmentItemWidget> fire_staff = Equipment.search().matchesWildCardNoCase("*fire*").first();
        Optional<EquipmentItemWidget> earth_staff = Equipment.search().matchesWildCardNoCase("*earth*").first();
        boolean hasRequiredRunes =
                ((lava_staff.isPresent()
                        || (earth_rune.isPresent() && earth_rune.get().getItemQuantity() >= 5 || earth_staff.isPresent())
                        && (fire_rune.isPresent() && fire_rune.get().getItemQuantity() >= 5) || fire_staff.isPresent())
                        && cosmic_rune.isPresent() && cosmic_rune.get().getItemQuantity() >= 5
                        || (Inventory.search().matchesWildCardNoCase("Rune pouch").first().isPresent()));

        return magicLevel >= 47 && hasRequiredRunes;
    }

    private boolean hasItemsToDeposit() {
        return BankInventory.search().idInList(ITEMS_TO_DEPOSIT).first().isPresent()
                || BankInventory.search().nameContains("seed").first().isPresent();
    }

    private void handleWithdrawDodgy() {
        overlay.infoStatus = "Withdrawing dodgy necklace";
        Optional<Widget> dodgy = Bank.search().withName("Dodgy necklace").first();
        if (dodgy.isPresent()) {
            BankInteraction.withdrawX(dodgy.get(), config.dodgyAmount() - BankInventory.search().matchesWildCardNoCase("*Dodgy necklace*").result().size());
            client.runScript(299,1,0,0);
            timeout = 1;
            return;
        }
        if (!hasDodgy()) {
            sendGameMessage("Out of dodgy necklaces.");
            isOutOfDodgy = true;
            timeout = tickDelay();
        }
    }

    private void handleWithdrawAncientBrews() {
        overlay.infoStatus = "Withdrawing Ancient brews";
        Optional<Widget> brew = Bank.search().matchesWildCardNoCase("*Ancient brew*").first();
        if (brew.isPresent()) {
            BankInteraction.withdrawX(brew.get(), config.AncientBrewAmount() - BankInventory.search().matchesWildCardNoCase("*Ancient brew*").result().size());
            client.runScript(299,1,0,0);
            timeout = 1;
            return;
        }
        if (!hasAncientBrew()) {
            sendGameMessage("Out of Ancient brews");
            isOutOfAncientBrews = true;
            timeout = tickDelay();
        }
    }

    private void handleDeposit() {
        if (npcToThieve == "Master Farmer") {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 786474, -1, -1); //Deposit-all
            return;
        }
        BankInventory.search().idInList(ITEMS_TO_DEPOSIT).first().ifPresent(x ->
                BankInventoryInteraction.useItem(x, "Deposit-All"));
    }

    private void handleThieve() {
        overlay.infoStatus = "Thieving";
        switch (npcToThieve) {
            case "Lindir":
                handleLindir();
                break;

            case "Knight of Ardougne":
                if (isInArdyEastBank()) {
                    NPCs.search().withName(npcToThieve).walkable().withinWorldArea(ARDY_EAST_BANK).nearestToPlayer().ifPresent(x ->
                            NPCInteraction.interact(x, "Pickpocket"));
                } else {
                    overlay.infoStatus = "Not in ardy bank";
                    timeout = tickDelay();
                    return;
                }
                break;

            case "Custom":
                overlay.infoStatus = "Thieving: " + config.CustomNPCName();
                NPCs.search().withName(config.CustomNPCName()).walkable().nearestToPlayer().ifPresent(x ->
                        NPCInteraction.interact(x, "Pickpocket"));
                break;

            default:
                NPCs.search().withName(npcToThieve).nearestToPlayer().ifPresent(x ->
                        NPCInteraction.interact(x, "Pickpocket"));
                break;
        }
    }

    private void handleLindir() { //Very sloppy but works for now
        if (isStandingInLindirHouse()) {
            Optional<TileObject> open_door = TileObjects.search().withAction("Close").atLocation(3243, 6071, 0).first();
            //This should stop spam closing the door in case a player wants to troll
            if (open_door.isPresent() && client.getPlayers().stream().noneMatch(x -> x.getWorldLocation().isInArea(new WorldArea(new WorldPoint(3242, 6069, 0), 4, 4)))) {
                TileObjectInteraction.interact(open_door.get(), "Close");
                return;
            }
            NPCs.search().withName(npcToThieve).nearestToPlayer().ifPresent(x ->
                    NPCInteraction.interact(x, "Pickpocket"));
            return;
        }

        Optional<TileObject> closed_door = TileObjects.search().withAction("Open").atLocation(3243, 6072, 0).first();
        if (closed_door.isPresent()) {
            overlay.infoStatus = "Opening door";
            TileObjectInteraction.interact(closed_door.get(), "Open");
            return;
        }
        WorldPoint lindirHouse = new WorldPoint(3243, 6071, 0);
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(lindirHouse);
    }

    private void handleWithdrawFood() {
        overlay.infoStatus = "Withdrawing food";
        Optional<Widget> food = Bank.search().matchesWildCardNoCase("*" + config.FoodName() + "*").first();
        if (food.isPresent()) {
            BankInteraction.withdrawX(food.get(), config.FoodAmount() - BankInventory.search().matchesWildCardNoCase("*"+config.FoodName()+"*").result().size());
            client.runScript(299,1,0,0);
        } else if (!hasFood()) {
            sendGameMessage("Out of food, disabling plugin.");
            enablePlugin = false;
            return;
        }
        timeout = tickDelay();
    }

    private void handleDodgyNecklace() {
        Inventory.search().withName("Dodgy necklace").first().ifPresent(x -> {
            overlay.infoStatus = "Equipping dodgy necklace";
            InventoryInteraction.useItem(x, "Wear");});
    }

    public boolean dodgyCheck() {
        return  (config.dodgyNecklace() && !isOutOfDodgy) &&
                (isBankOpen() && BankInventory.search().matchesWildCardNoCase("*Dodgy necklace*").result().size() < config.dodgyAmount());
    }

    private boolean redemptionCheck() {
        return config.useRedemption() && !hasAncientBrew() && !isOutOfAncientBrews && prayer == 0;
    }

    private boolean foodCheck() {
        return !hasFood() && hitpoints <= config.HealthLowAmount() && !config.useRedemption();
    }

    private boolean handleAncientBrewCheck() {
        return config.useRedemption() && isStandingInLindirHouse() && (prayer == 0 && hasAncientBrew() || !isRedemptionActive() && prayer > 0);
    }

    private boolean shouldFindBank() {
        return (!hasFood() && hitpoints <= config.HealthLowAmount() && isOutOfAncientBrews) || dodgyCheck() ||
                foodCheck() || Inventory.full() || redemptionCheck();
    }

    private boolean shouldEatFood() {
        return (!config.useRedemption() || isOutOfAncientBrews && !hasAncientBrew()) && hasFood() && hitpoints <= config.HealthLowAmount();
    }

    private boolean shouldThieve() {
        return hasFood() || hitpoints >= config.HealthLowAmount() || (config.useRedemption() && !isOutOfAncientBrews) && (hasAncientBrew() || prayer > 0);
    }

    private boolean shouldWithdrawFood() {
        return isBankOpen() && (isOutOfAncientBrews || !config.useRedemption()) &&
                BankInventory.search().matchesWildCardNoCase("*"+config.FoodName()+"*").result().size() < config.FoodAmount();
    }

    public boolean shadowVeilCheck() {
        return config.shadowVeil() && shadowVeilCooldown == 0 && hasShadowVeilRequirements() && !isShadowVeilActive;
    }

    public boolean useAncientBrewCheck() {
        return config.useRedemption() && !isOutOfAncientBrews &&
                (isBankOpen() && BankInventory.search().matchesWildCardNoCase("*Ancient brew*").result().size() < config.AncientBrewAmount());
    }

    private boolean isRedemptionActive() {
        return EthanApiPlugin.getClient().isPrayerActive(Prayer.REDEMPTION);
    }


    private void handleFood() {
        overlay.infoStatus = "Eating";
        Inventory.search().matchesWildCardNoCase("*" + config.FoodName() + "*").first().ifPresent(x -> {
            InventoryInteraction.useItem(x, "Eat", "Drink");
            timeout = tickDelay();
        });
    }

    private void handleAncientBrew() {
        if (prayer == 0) {
            Inventory.search().nameContains("Ancient brew").first().ifPresent(x ->
                    InventoryInteraction.useItem(x, "Drink"));
        }
        if (!isRedemptionActive() && prayer > 0) {
            overlay.infoStatus = "Activating Redemption";
            PrayerInteraction.togglePrayer(Prayer.REDEMPTION);
        }
        //timeout = tickDelay();
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
            sendGameMessage("Auto Thiever disabled.");
        } else {
            sendGameMessage("Auto Thiever enabled.");
        }
    }

    private boolean isWearingDodgyNecklace() {
        return Equipment.search().nameContains("odgy necklace").first().isPresent();
    }

    private void handlePouch(int minPouch, int randMax) {
        if (nextPouchOpen < minPouch || nextPouchOpen > randMax) {
            nextPouchOpen = getRandomIntBetweenRange(minPouch, randMax);
        }

        Inventory.search().matchesWildCardNoCase("*Coin pouch*").first().ifPresent(x -> pouches = x.getItemQuantity());

        if (pouches >= nextPouchOpen) {
            overlay.infoStatus = "Open pouch";
            Inventory.search().withName("Coin pouch").first().ifPresent(x -> InventoryInteraction.useItem(x, "Open-all"));
            nextPouchOpen = 0;
            pouches = 0;
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

        if (event.getMessage().startsWith("You pick")) {
            successfulThieves++;
        }

        if (event.getMessage().startsWith("You fail") || event.getMessage().startsWith("Your dodgy necklace protects")) {
            failedThieves++;
        }

        if (event.getMessage().startsWith("You need to empty your")) {
            Inventory.search().withName("Coin pouch").first().ifPresent(x -> InventoryInteraction.useItem(x, "Open-all"));
            nextPouchOpen = 0;
        }
    }


    public void openNearestBank() {
        Optional<TileObject> bank = TileObjects.search().withName("Bank booth").withAction("Bank").nearestToPlayer();

        if (!isBankOpen() && !EthanApiPlugin.isMoving()) {
            Optional<TileObject> door = TileObjects.search().withAction("Open").atLocation(3243, 6072, 0).first();
            if (isStandingInLindirHouse() && door.isPresent()) {
                overlay.infoStatus = "Opening door";
                TileObjectInteraction.interact(door.get(), "Open");
                return;
            }

            if (bank.isPresent()) {
                overlay.infoStatus = "Banking";
                TileObjectInteraction.interact(bank.get(), "Bank");
            } else {
                sendGameMessage("No bank found, disabling plugin.");
                enablePlugin = false;
            }
        }
        if (isRedemptionActive()) {
            PrayerInteraction.togglePrayer(Prayer.REDEMPTION);
        }
        timeout = tickDelay();
    }

    private boolean isStandingInLindirHouse() {
        return LINDIR_HOUSE.contains(client.getLocalPlayer().getWorldLocation());
    }

    private boolean isInArdyEastBank() {
        return ARDY_EAST_BANK.contains(client.getLocalPlayer().getWorldLocation());
    }


    public boolean isBankOpen() {
        return (client.getWidget(WidgetInfo.BANK_CONTAINER) != null);
    }

    public boolean hasFood() {
        if (isBankOpen()) {
            return BankInventory.search().matchesWildCardNoCase("*" + config.FoodName() + "*").first().isPresent();
        }
        return Inventory.search().matchesWildCardNoCase("*" + config.FoodName() + "*").first().isPresent();
    }

    public boolean hasAncientBrew() {
        if (isBankOpen()) {
            return BankInventory.search().matchesWildCardNoCase("*Ancient brew*").first().isPresent();
        }
        return Inventory.search().matchesWildCardNoCase("*Ancient brew*").first().isPresent();
    }

    public boolean hasDodgy() {
        if (isBankOpen()) {
            return BankInventory.search().withName("Dodgy necklace").first().isPresent()
                    || isWearingDodgyNecklace();
        }
        return Inventory.search().withName("Dodgy necklace").first().isPresent()
                || isWearingDodgyNecklace();
    }

    public boolean isRunEnabled() {
        return client.getVarpValue(173) == 1;
    }

    public boolean isBankPinOpen() {
        return (client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null);
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

}
