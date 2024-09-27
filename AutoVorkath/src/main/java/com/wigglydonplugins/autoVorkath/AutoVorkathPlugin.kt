package com.wigglydonplugins.autoVorkath

import com.example.EthanApiPlugin.Collections.*
import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.InteractionApi.*
import com.example.Packets.MousePackets
import com.example.Packets.MovementPackets
import com.example.Packets.NPCPackets
import com.example.Packets.WidgetPackets
import com.google.inject.Provides
import com.piggyplugins.PiggyUtils.API.SpellUtil
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler
import net.runelite.api.*
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.*
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.NpcLootReceived
import net.runelite.client.game.ItemManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import java.awt.event.KeyEvent
import javax.inject.Inject
import kotlin.math.abs


@PluginDescriptor(
    name = "<html><font color=\"#FF0000\">[WD]</font> Auto Vorkath </html>",
    description = "WD - Auto vorkath",
    tags = ["vorkath", "auto", "auto prayer"],
    enabledByDefault = false
)
class AutoVorkathPlugin : Plugin() {
    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var breakHandler: ReflectBreakHandler

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var autoVorkathOverlay: AutoVorkathOverlay

    @Inject
    private lateinit var config: AutoVorkathConfig

    @Inject
    private lateinit var itemManager: ItemManager

    @Provides
    fun getConfig(configManager: ConfigManager): AutoVorkathConfig {
        return configManager.getConfig(AutoVorkathConfig::class.java)
    }

    var botState: State? = null
    var tickDelay: Int = 0
    var totalKills = 0
    var startTime = System.currentTimeMillis()
    var elapsedTime = 0L
    private var vorkathHpPercent : Int = 100;
    private var running = false
    private val rangeProjectileId = 1477
    private val magicProjectileId = 393
    private val purpleProjectileId = 1471
    private val blueProjectileId = 1479
    private val redProjectileId = 1481
    private val acidProjectileId = 1483
    private val acidRedProjectileId = 1482
    private val whiteProjectileId = 395

    private var isPrepared = false
    private var drankAntiFire = false
    private var drankRangePotion = false
    private var drankAntiVenom = false
    private var lastDrankAntiFire: Long = 0
    private var lastDrankRangePotion: Long = 0
    private var lastDrankAntiVenom: Long = 0

    private val lootList: MutableSet<Int> = mutableSetOf()
    private var lootIds: MutableSet<Int> = mutableSetOf()

    private var acidPools: HashSet<WorldPoint> = hashSetOf()

    private var initialAcidMove = false

    private var redBallLocation: WorldPoint = WorldPoint(0, 0, 0)

    private val bankArea: WorldArea = WorldArea(2096, 3911, 20, 11, 0)
    private val bankLocation: WorldPoint = WorldPoint(2099, 3919, 0)
    private val fremennikArea: WorldArea = WorldArea(2627, 3672, 24, 30, 0)

    enum class State {
        TESTING,
        WALKING_TO_BANK,
        BANKING,
        WALKING_TO_VORKATH,
        POKE,
        FIGHTING,
        ACID,
        SPAWN,
        RED_BALL,
        LOOTING,
        THINKING,
        NONE
    }

    override fun startUp() {
        println("Auto Vorkath Plugin Activated")
        startTime = System.currentTimeMillis()
        botState = State.THINKING
        running = client.gameState == GameState.LOGGED_IN
        breakHandler.registerPlugin(this)
        breakHandler.startPlugin(this)
        overlayManager.add(autoVorkathOverlay)
    }

    override fun shutDown() {
        println("Auto Vorkath Plugin Deactivated")
        totalKills = 0
        running = false
        botState = null
        drankAntiFire = false
        drankRangePotion = false
        drankAntiVenom = false
        lastDrankAntiFire = 0
        lastDrankRangePotion = 0
        lastDrankAntiVenom = 0
        lootList.clear()
        acidPools.clear()
        breakHandler.stopPlugin(this)
        breakHandler.unregisterPlugin(this)
        overlayManager.remove(autoVorkathOverlay)
    }

    @Subscribe
    fun onChatMessage(e: ChatMessage) {
        if (e.message.contains("Oh dear, you are dead!")) {
            drankAntiFire = false
            drankRangePotion = false
            drankAntiVenom = false
            isPrepared = false
            activateProtectPrayer(false)
            activateRigour(false)
            EthanApiPlugin.stopPlugin(this)
        }
        if (e.message.contains("Your Vorkath kill count is:")) {
            totalKills ++
            activateProtectPrayer(false)
            activateRigour(false)
            drankAntiFire = false
            drankRangePotion = false
            drankAntiVenom = false
            isPrepared = false
        }
        if (e.message.contains("There is no ammo left in your quiver.")) {
            teleToHouse()
            EthanApiPlugin.sendClientMessage("No ammo, stopping plugin.")
            drankAntiFire = false
            drankRangePotion = false
            drankAntiVenom = false
            isPrepared = false
            activateProtectPrayer(false)
            activateRigour(false)
            EthanApiPlugin.stopPlugin(this)
        }
    }

    @Subscribe
    fun onNpcLootReceived(event: NpcLootReceived) {
        if (!running) return
        val items = event.items
        val vorkathLootIds = items.asSequence()
            .map { it }
            .filter { itemManager.getItemPrice(it.id) * it.quantity > config.MIN_PRICE() } //price filter
            .map { it.id }
            .toSet()

        lootList.addAll(vorkathLootIds)

        changeStateTo(State.LOOTING)
    }

    @Subscribe
    fun onNpcDespawned(e: NpcDespawned) {
        if (e.npc.name == "Zombified Spawn") {
            if (Inventory.search().nameContains(config.CROSSBOW().toString()).result().isNotEmpty()) {
                InventoryInteraction.useItem(config.CROSSBOW().toString(), "Wield")
            }
            if (isVorkathAsleep()) {

                changeStateTo(State.LOOTING)
            }
            else {
                changeStateTo(State.FIGHTING)
            }
        }
    }

    @Subscribe
    fun onActorDeath(e: ActorDeath) {
        if (e.actor.name == "Zombified Spawn") {
            if (!isVorkathAsleep()) {
                changeStateTo(State.FIGHTING, 2);
            }
        }
        if (e.actor.name == "Vorkath") {
            val vorkath = NPCs.search().nameContains("Vorkath").first().get().worldLocation
            val middle = WorldPoint(vorkath.x + 3, vorkath.y - 1, 0)
            activateProtectPrayer(false)
            activateRigour(false)
            if (Inventory.search().nameContains("Ruby dragon bolts (e)").result().isNotEmpty()) {
                InventoryInteraction.useItem("Ruby dragon bolts (e)", "Wield")
            }
            if (client.localPlayer.worldLocation != middle) {
                MousePackets.queueClickPacket()
                MovementPackets.queueMovement(middle)
            }
        }
    }

    @Subscribe
    fun onProjectileMoved(e: ProjectileMoved) {
        when (e.projectile.id) {
            acidProjectileId -> {
                acidPools.add(WorldPoint.fromLocal(client, e.position))
                changeStateTo(State.ACID)
            }

            whiteProjectileId -> changeStateTo(State.SPAWN)
            acidRedProjectileId -> changeStateTo(State.ACID)
            rangeProjectileId, magicProjectileId, purpleProjectileId, blueProjectileId -> {
                activateProtectPrayer(true)
                activateRigour(true)
            }
            redProjectileId -> {
                redBallLocation = WorldPoint.fromLocal(client, e.position)
                changeStateTo(State.RED_BALL)
            }
        }
    }

    @Subscribe
    fun onGameObjectDespawned(e: GameObjectDespawned) {
        if (e.gameObject.id == 32000) {
            acidPools.clear()
            changeStateTo(State.FIGHTING)
        }
    }

    @Subscribe
    fun onGameTick(e: GameTick) {
        val currentTime = System.currentTimeMillis()
        elapsedTime = currentTime - startTime
        autoVorkathOverlay.updateKillsPerHour()


        if (running) {
            if (tickDelay > 0) { // Tick delay
                tickDelay--
                return
            }

            when (botState) {
                State.TESTING -> testingState()
                State.WALKING_TO_BANK -> walkingToBankState()
                State.BANKING -> bankingState()
                State.WALKING_TO_VORKATH -> walkingToVorkathState()
                State.POKE -> pokeState()
                State.FIGHTING -> fightingState()
                State.ACID -> acidState()
                State.SPAWN -> spawnState()
                State.RED_BALL -> redBallState()
                State.LOOTING -> lootingState()
                State.THINKING -> thinkingState()
                State.NONE -> println("None State")
                null -> println("Null State")
            }
        }
    }

    @Subscribe
    fun onVarbitChanged(event: VarbitChanged) {

        if (event.varpId == VarPlayer.POISON) {
            if (event.value >= 1000000) {
                Inventory.search().nameContains("Anti-venom").first().ifPresent { potion ->
                    InventoryInteraction.useItem(potion, "Drink")
                }
            }
        }

        if (event.varbitId == Varbits.SUPER_ANTIFIRE) {
            drankAntiFire = true
            if (event.value <= 2) {
                Inventory.search().nameContains("Extended super antifire").first().ifPresent {
                        potion -> InventoryInteraction.useItem(potion, "Drink")
                }
            }
        }
        if (event.varbitId == Varbits.DIVINE_RANGING) {
            drankRangePotion = true
            if (event.value <= 10) {
                Inventory.search().nameContains("Divine ranging potion").first().ifPresent {
                        potion -> InventoryInteraction.useItem(potion, "Drink")
                }
            }
        }
    }

    private fun testingState() {

    }

    private fun lootingState() {
        var notEnoughFood = false

        if (Inventory.search().nameContains("Ruby dragon bolts (e)").result().isNotEmpty()) {
            InventoryInteraction.useItem("Ruby dragon bolts (e)", "Wield")
        }

        if (lootList.isEmpty() || TileItems.search().empty()) {
            if (Inventory.getItemAmount(config.FOOD_TYPE().foodId) < config.FOODAMOUNT().height) {
                notEnoughFood = true
            }
            if (Inventory.getItemAmount(config.FOOD_TYPE().foodId) >= config.FOODAMOUNT().height) {
                EthanApiPlugin.sendClientMessage("THINKING NUMBER 0!")
                changeStateTo(State.THINKING, 1)
                return
            }
        }

        val currentGroundItems = TileItems.search().tileItems.asSequence()
            .map { it.tileItem }
            .map { it to itemManager.getItemPrice(it.id) * it.quantity }
            .filter { (item, value) -> value > config.MIN_PRICE() || itemManager.getItemPrice(item.id) == 0 }
            .toList()

        val sortedGroundItems = currentGroundItems.sortedByDescending { (_, value) -> value }
            .map { it.first.id }
            .reversed()

        lootList.clear()
        lootList.addAll(sortedGroundItems)

        var lootedAnyItem = false

        for (itemId in lootList) {

            if (!Inventory.full()) {
                TileItems.search().withId(itemId).first().ifPresent { item: ETileItem ->
                    item.interact(false)
                    lootIds.add(itemId)
                    lootedAnyItem = true
                }
            }

            if (Inventory.full()) {
                if (Inventory.getItemAmount(config.FOOD_TYPE().foodId) > 0) {
                    InventoryInteraction.useItem(config.FOOD_TYPE().foodId, "Eat")
                    if (lootList.indexOf(itemId) < lootList.size - 1) {
                        continue
                    }
                    else {
                        if (Inventory.getItemAmount(config.FOOD_TYPE().foodId) < config.FOODAMOUNT().height) {
                            notEnoughFood = true
                        }
                    }
                } else {
                    if (lootedAnyItem) {
                        EthanApiPlugin.sendClientMessage("Inventory full, going to bank.")
                        changeStateTo(State.WALKING_TO_BANK)
                        return
                    } else {
                        EthanApiPlugin.sendClientMessage("bingbongboi")
                        changeStateTo(State.THINKING)
                        return
                    }
                }
            }
        }

        if (Inventory.full() && lootList.isEmpty() && Inventory.getItemAmount(config.FOOD_TYPE().foodId) < config.FOODAMOUNT().height || notEnoughFood) {
            EthanApiPlugin.sendClientMessage("Not enough food, teleporting away!")
            changeStateTo(State.WALKING_TO_BANK, 1)
            return
        }


        // this is primary restart kill block #prepared
        if (lootList.isEmpty() && Inventory.getItemAmount(config.FOOD_TYPE().foodId) > config.FOODAMOUNT().height) {
            changeStateTo(State.THINKING)
        }
    }


    private fun acidState() {
        if (!runIsOff()) enableRun()
        activateProtectPrayer(false)
        if (!inVorkathArea()) {
            acidPools.clear()
            changeStateTo(State.THINKING)
            return
        }

        val vorkath = NPCs.search().nameContains("Vorkath").first().get().worldLocation
        val swPoint = WorldPoint(vorkath.x + 1, vorkath.y - 8, 0)

        fun findSafeTiles(): WorldPoint? {
            val wooxWalkArea = WorldArea(swPoint, 5, 1)

            fun isTileSafe(tile: WorldPoint): Boolean = tile !in acidPools
                    && WorldPoint(tile.x, tile.y + 1, tile.plane) !in acidPools
                    && WorldPoint(tile.x, tile.y + 2, tile.plane) !in acidPools
                    && WorldPoint(tile.x, tile.y + 3, tile.plane) !in acidPools


            val safeTiles = wooxWalkArea.toWorldPointList().filter { isTileSafe(it) }

            // Find the closest safe tile by x-coordinate to the player
            return safeTiles.minByOrNull { abs(it.x - client.localPlayer.worldLocation.x) }
        }

        TileObjects.search().withId(ObjectID.ACID_POOL_32000).result().forEach { tileObject ->
            acidPools.add(tileObject.worldLocation)
        }

        TileObjects.search().withId(ObjectID.ACID_POOL).result().forEach { tileObject ->
            acidPools.add(tileObject.worldLocation)
        }

        TileObjects.search().withId(ObjectID.ACID_POOL_37991).result().forEach { tileObject ->
            acidPools.add(tileObject.worldLocation)
        }

        val safeTile: WorldPoint? = findSafeTiles()

        val playerLocation = client.localPlayer.worldLocation

        safeTile?.let {
            if (playerLocation == safeTile) {
                // Attack Vorkath if the player close to the safe tile
                NPCs.search().nameContains("Vorkath").first().ifPresent { vorkath ->
                    NPCInteraction.interact(vorkath, "Attack")
                    //println("Attacked Vorkath")
                }
            } else {
                eat(config.EATAT())
                // Move to the safe tile if the player is not close enough
                MousePackets.queueClickPacket()
                //println("Moving to safe tile: $safeTile")
                //println("Player location: $playerLocation")
                MovementPackets.queueMovement(safeTile)
            }
        } ?: run {
            EthanApiPlugin.sendClientMessage("NO SAFE TILES! TELEPORTING TF OUT!")
            teleToHouse()
            changeStateTo(State.WALKING_TO_BANK)
        }
    }

    private fun redBallState() {
        drinkPrayer()
        eat(config.EATAT())
        MousePackets.queueClickPacket()
        MovementPackets.queueMovement(WorldPoint(redBallLocation.x + 2, redBallLocation.y, redBallLocation.plane))
        changeStateTo(State.FIGHTING, 2)
    }

    private fun spawnState() {
        if (!inVorkathArea()) {
            changeStateTo(State.THINKING)
            return
        }
        activateProtectPrayer(false)
        activateRigour(false) //stop attack
        if (config.SLAYERSTAFF().toString() != "Rune pouch") {
            if (Equipment.search().nameContains(config.SLAYERSTAFF().toString()).result().isEmpty()) {
                Inventory.search().nameContains(config.SLAYERSTAFF().toString()).first().ifPresent { staff ->
                    InventoryInteraction.useItem(staff, "Wield")
                }
                return
            } else {
                NPCs.search().nameContains("Zombified Spawn").first().ifPresent { spawn ->
                    NPCInteraction.interact(spawn, "Attack")
                }
            }
        }

        else if (config.SLAYERSTAFF().toString() == "Rune pouch") {
            val crumbleUndead = SpellUtil.getSpellWidget(client, "Crumble Undead");
            MousePackets.queueClickPacket()
            MovementPackets.queueMovement(client.localPlayer.worldLocation)
            NPCs.search().nameContains("Zombified Spawn").first().ifPresent { spawn ->
                NPCPackets.queueWidgetOnNPC(spawn, crumbleUndead);
            }
        }
    }
    private fun fightingState() {
        if (runIsOff()) enableRun()
        activateProtectPrayer(vorkathHpPercent != 0)
        activateRigour(vorkathHpPercent != 0)
        acidPools.clear()
        if (!inVorkathArea()) {
            EthanApiPlugin.sendClientMessage("FIGHTING state change to THINKING")
            changeStateTo(State.THINKING)
            return
        } else {
            val vorkathNpc: NPC = NPCs.search().nameContains("Vorkath").first().get();
            val vorkathLocation = vorkathNpc.worldLocation;
            val middle = WorldPoint(vorkathLocation.x + 3, vorkathLocation.y - 5, 0)

            fun getHpPercentValue(ratio: Float, scale: Float): Int {
                return Math.round((ratio / scale) * 100f);
            }
            fun updateNpcHp(npc: NPC) {
                val currentHp: Int = getHpPercentValue(npc.getHealthRatio().toFloat(), npc.getHealthScale().toFloat());

                if (currentHp < vorkathHpPercent && currentHp > -1) {
                    vorkathHpPercent = currentHp;
                }
                if (currentHp == 0 && vorkathHpPercent == 0) {
                    vorkathHpPercent = 100;
                }
            }
            updateNpcHp(vorkathNpc);

            if (vorkathHpPercent <= 35 && vorkathHpPercent > 0) {
                if (Inventory.search().nameContains("Diamond dragon bolts (e)").result().isNotEmpty()) {
                    InventoryInteraction.useItem("Diamond dragon bolts (e)", "Wield")
                }
            }
            if (vorkathHpPercent > 35 || vorkathHpPercent == 0) {
                if (Inventory.search().nameContains("Ruby dragon bolts (e)").result().isNotEmpty()) {
                    InventoryInteraction.useItem("Ruby dragon bolts (e)", "Wield")
                }
            }

            if (client.localPlayer.interacting == null) {
                NPCs.search().nameContains("Vorkath").first().ifPresent { vorkath ->
                    NPCInteraction.interact(vorkath, "Attack")
                }
                return
            }
            if (client.localPlayer.worldLocation != middle) {
                if (!isMoving()) {
                    MousePackets.queueClickPacket()
                    MovementPackets.queueMovement(middle)
                }
            }
            eat(config.EATAT())
            drinkPrayer()
            if (Inventory.search().nameContains(config.CROSSBOW().toString()).result().isNotEmpty()) {
                InventoryInteraction.useItem(config.CROSSBOW().toString(), "Wield")
            }

        }
    }

    private fun pokeState() {
        if (isVorkathAsleep()) {
            acidPools.clear()
            if (!isMoving()) {
                NPCs.search().withAction("Poke").first().ifPresent { sleepingVorkath ->
                    NPCInteraction.interact(sleepingVorkath, "Poke")
                }
            }
        } else {
            val vorkath = NPCs.search().nameContains("Vorkath").first().get().worldLocation
            val middle = WorldPoint(vorkath.x + 3, vorkath.y - 5, 0)
            MousePackets.queueClickPacket()
            MovementPackets.queueMovement(middle)
            changeStateTo(State.FIGHTING)
            return
        }
    }

    private fun walkingToVorkathState() {
        if (runIsOff()) enableRun()
        activateProtectPrayer(false)
        activateRigour(false)
        if (Inventory.search().nameContains("Ruby dragon bolts (e)").result().isNotEmpty()) {
            InventoryInteraction.useItem("Ruby dragon bolts (e)", "Wield")
        }

        if (!isMoving()) {
            if (bankArea.contains(client.localPlayer.worldLocation)) {
                if (Widgets.search().withTextContains("Click here to continue").result().isNotEmpty()) {
                    sendKey(KeyEvent.VK_SPACE)
                    return
                }
                if (client.localPlayer.worldLocation != bankLocation) {
                    MousePackets.queueClickPacket()
                    MovementPackets.queueMovement(bankLocation)
                } else {
                    NPCs.search().nameContains("Sirsal Banker").nearestToPlayer().ifPresent { banker ->
                        NPCInteraction.interact(banker, "Talk-to")
                    }
                }
            } else {
                if (inVorkathArea()) {
                    drankRangePotion = false
                    drankAntiFire = false
                    changeStateTo(State.THINKING, 3)
                    return
                }
                if (fremennikArea.contains(client.localPlayer.worldLocation)) {
                    TileObjects.search().withId(29917).withAction("Travel").nearestToPlayer().ifPresent { boat ->
                        TileObjectInteraction.interact(boat, "Travel")
                    }
                } else {
                    if (TileObjects.search().withId(31990).result().isNotEmpty()) {
                        if (!drankAntiFire) {
                            Inventory.search().nameContains("Extended super antifire").first().ifPresent {
                                    potion -> InventoryInteraction.useItem(potion, "Drink")
                            }
                            tickDelay = 2
                            return
                        }
                        if (!drankRangePotion) {
                            Inventory.search().nameContains("Divine ranging potion").first().ifPresent {
                                    potion -> InventoryInteraction.useItem(potion, "Drink")
                            }
                            tickDelay = 2
                            return
                        }
                        TileObjects.search().withId(31990).first().ifPresent { iceChunk ->
                            TileObjectInteraction.interact(iceChunk, "Climb-over")
                        }
                    } else {
                        EthanApiPlugin.sendClientMessage("WALKING_TO_VORKATH -> -> WALKING_TO_BANK")
                        changeStateTo(State.WALKING_TO_BANK)
                    }
                }
            }
        }
    }

    private fun bankingState() {
        activateProtectPrayer(false)
        activateRigour(false)
        if (bankArea.contains(client.localPlayer.worldLocation)) {
            if (!isMoving()) {
                if (!Bank.isOpen()) {
                    if (client.localPlayer.worldLocation != bankLocation) {
                        MousePackets.queueClickPacket()
                        MovementPackets.queueMovement(bankLocation)
                    } else {
                        NPCs.search().nameContains("Jack").nearestToPlayer().ifPresent { bank ->
                            NPCInteraction.interact(bank, "Bank")
                        }
                        tickDelay = 3
                    }
                } else {
                    bank()
                }
            }
        } else {
            changeStateTo(State.THINKING)
        }
    }

    private fun walkingToBankState() {
        if (runIsOff()) enableRun()
        activateProtectPrayer(false)
        activateRigour(false)
        if (Inventory.search().nameContains("Ruby dragon bolts (e)").result().isNotEmpty()) {
            InventoryInteraction.useItem("Ruby dragon bolts (e)", "Wield")
        }
        if (breakHandler.shouldBreak(this)) { // Break handler
            breakHandler.startBreak(this)
        }
        if (!isMoving()) {
            if (bankArea.contains(client.localPlayer.worldLocation)) {
                changeStateTo(State.THINKING)
                return
            }
            if (!inHouse()) {
                teleToHouse()
                return
            }
            if (client.getBoostedSkillLevel(Skill.HITPOINTS) < config.POOLDRINK().width || client.getBoostedSkillLevel(
                    Skill.PRAYER
                ) < config.POOLDRINK().height
            ) {
                TileObjects.search().nameContains("pool of").withAction("Drink").first().ifPresent { pool ->
                    TileObjectInteraction.interact(pool, "Drink")
                }
                return
            }
            if (inHouse()) {
                TileObjects.search().nameContains(config.PORTAL().toString()).first().ifPresent { portal ->
                    TileObjectInteraction.interact(portal, config.PORTAL().action())
                }
                return
            }
        }
    }

    private fun thinkingState() {
        if (Inventory.search().nameContains("Ruby dragon bolts (e)").result().isNotEmpty()) {
            InventoryInteraction.useItem("Ruby dragon bolts (e)", "Wield")
        }
        if (!inVorkathArea()) { // Check if player is not in Vorkath area
            if (readyToFight()) { // Check if player has all potions and food
                changeStateTo(State.WALKING_TO_VORKATH) // Player is prepared, walk to Vorkath
                return
            } else {
                if (bankArea.contains(client.localPlayer.worldLocation)) { // Player is in bank area
                    changeStateTo(State.BANKING)
                    return
                } else { // Player is not in bank area
                    changeStateTo(State.WALKING_TO_BANK)
                    return
                }
            }
        } else { // Player is already in Vorkath's area
            changeStateTo(State.POKE) // Proceed with the fight
            return
        }
    }

    private fun drinkPrayer() {
        if (needsToDrinkPrayer()) {
            if (Inventory.search().nameContains(config.PRAYERPOTION().toString()).result().isNotEmpty()) {
                Inventory.search().nameContains(config.PRAYERPOTION().toString()).first().ifPresent { prayerPotion ->
                    InventoryInteraction.useItem(prayerPotion, "Drink")
                }
                tickDelay = 2
                return
            } else {
                isPrepared = false
                drankRangePotion = false
                drankAntiFire = false
                drankAntiVenom = false
                EthanApiPlugin.sendClientMessage("NO MORE PRAYER")
                teleToHouse()
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun bank() {
        lootIds.forEach { id ->
            if (BankInventory.search().withId(id).result().isNotEmpty()) {
                BankInventoryInteraction.useItem(id, "Deposit-All")
            } else {
                lootIds.remove(id)
            }
        }

        val itemsToDeposit = listOf("Divine ranging potion(1)", "Extended super antifire(1)", "Anti-venom+(1)", "Prayer potion(1)")
        var itemDeposited = false

        for (item in itemsToDeposit) {
            if (BankInventory.search().nameContains(item).result().size > 0) {
                BankInventoryInteraction.useItem(item, "Deposit-All")
                itemDeposited = true
            }
        }

        if (itemDeposited) {
            changeStateTo(State.THINKING)
            return
        }

        if (!hasItem(config.TELEPORT().toString())) {
            withdraw(config.TELEPORT().toString(), 1)
        }
        if (!hasItem(config.SLAYERSTAFF().toString())) {
            withdraw(config.SLAYERSTAFF().toString(), 1)
        }
        if (!hasItem("Rune pouch")) {
            withdraw("Rune pouch", 1)
        }
        if (!hasItem("Diamond dragon bolts (e)")) {
                BankInteraction.useItem("Diamond dragon bolts (e)", "Withdraw-All")
            }
        if (BankInventory.search().nameContains(config.RANGEPOTION().toString()).result().size < 1) {
            withdraw(config.RANGEPOTION().toString(), 1)
        }
        if (BankInventory.search().nameContains(config.ANTIFIRE().toString()).result().size < 1) {
            withdraw(config.ANTIFIRE().toString(), 1)
        }
        if (BankInventory.search().nameContains(config.ANTIVENOM().toString()).result().size < 1) {
            withdraw(config.ANTIVENOM().toString(), 1)
        }

        val prayerPotionsInInventory = BankInventory.search().nameContains(config.PRAYERPOTION().toString()).result().size

        if (prayerPotionsInInventory < config.PRAYER_POTION_AMOUNT()) {
            for (i in 1..config.PRAYER_POTION_AMOUNT() - prayerPotionsInInventory) {
                withdraw(config.PRAYERPOTION().toString(), 1)
            }
        }
        if (!Inventory.full()) {
            for (i in 1..config.FOODAMOUNT().width - Inventory.getItemAmount(config.FOOD_TYPE().foodId)) {
                withdrawId(config.FOOD_TYPE().foodId, 1)
            }
        }
        lootList.clear()
        lootIds.clear()
        changeStateTo(State.THINKING)
    }

    private fun inVorkathArea(): Boolean =
        NPCs.search().nameContains("Vorkath").result().isNotEmpty() && client.isInInstancedRegion

    private fun isVorkathAsleep(): Boolean = NPCs.search().withId(8059).result().isNotEmpty()
    private fun inHouse(): Boolean = TileObjects.search().nameContains(config.PORTAL().toString()).result().isNotEmpty()

    private fun isMoving(): Boolean = EthanApiPlugin.isMoving() || client.localPlayer.animation != -1
    private fun needsToDrinkPrayer(): Boolean = client.getBoostedSkillLevel(Skill.PRAYER) <= config.DRINKPRAYERAT();
    private fun readyToFight(): Boolean =
        (Inventory.search().withId(config.FOOD_TYPE().foodId).result().size >= config.FOODAMOUNT().height
                && Inventory.search().nameContains(config.SLAYERSTAFF().toString()).result().isNotEmpty()
                && Inventory.search().nameContains(config.TELEPORT().toString()).result().isNotEmpty()
                && Inventory.search().nameContains("Rune pouch").result().isNotEmpty()
                && Inventory.search().nameContains(config.ANTIVENOM().toString()).result().isNotEmpty()
                && Inventory.search().nameContains(config.ANTIFIRE().toString()).result().isNotEmpty()
                && Inventory.search().nameContains(config.RANGEPOTION().toString()).result().isNotEmpty()
                )

    private fun needsToEat(at: Int): Boolean = client.getBoostedSkillLevel(Skill.HITPOINTS) <= at

    private fun eat(at: Int) {
        if (needsToEat(at)) {
            if (Inventory.search().withAction("Eat").result().isNotEmpty()) {
                Inventory.search().withAction("Eat").first().ifPresent { food ->
                    InventoryInteraction.useItem(food, "Eat")
                }
            } else {
                isPrepared = false
                drankRangePotion = false
                drankAntiFire = false
                drankAntiVenom = false
                initialAcidMove = false
                teleToHouse()
                EthanApiPlugin.sendClientMessage("NO MORE FOOD!!!")
                changeStateTo(State.WALKING_TO_BANK)
                return
            }
        }
    }

    private fun sendKey(key: Int) {
        keyEvent(KeyEvent.KEY_PRESSED, key)
        keyEvent(KeyEvent.KEY_RELEASED, key)
    }

    private fun keyEvent(id: Int, key: Int) {
        val e = KeyEvent(
            client.canvas,
            id,
            System.currentTimeMillis(),
            0,
            key,
            KeyEvent.CHAR_UNDEFINED
        )
        client.canvas.dispatchEvent(e)
    }

    fun hasItem(name: String): Boolean = Inventory.search().nameContains(name).result().isNotEmpty()
    fun withdraw(name: String, amount: Int) {
        Bank.search().nameContains(name).first().ifPresent { item ->
            BankInteraction.withdrawX(item, amount)
        }
    }

    fun withdrawId(id: Int, amount: Int) {
        Bank.search().withId(id).first().ifPresent { item ->
            BankInteraction.withdrawX(item, amount)
        }
    }

    private fun runIsOff(): Boolean = EthanApiPlugin.getClient().getVarpValue(173) == 0

    private fun enableRun() {
        MousePackets.queueClickPacket()
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1)
    }

    private fun activateProtectPrayer(on: Boolean) {
        PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MISSILES, on)
    }
    private fun activateRigour(on: Boolean) {
        PrayerInteraction.setPrayerState(Prayer.RIGOUR, on)
    }

    private fun teleToHouse() {
        if (config.TELEPORT().toString() == "Rune pouch") {
            val houseTele = SpellUtil.getSpellWidget(client, "Teleport to House");
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(houseTele, "Cast");
        }
        else {
            Inventory.search().nameContains(config.TELEPORT().toString()).first().ifPresent { teleport ->
                InventoryInteraction.useItem(teleport, config.TELEPORT().action())
            }
        }
    }

    private fun changeStateTo(stateName: State, ticksToDelay: Int = 0) {
        botState = stateName
        tickDelay = ticksToDelay
    }
}
