/*
 * Copyright (c) 2024. By Jrod7938
 *
 */

package com.jrplugins.AutoChop

import com.example.EthanApiPlugin.Collections.*
import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.InteractionApi.BankInventoryInteraction
import com.example.InteractionApi.InventoryInteraction
import com.example.InteractionApi.NPCInteraction
import com.example.InteractionApi.TileObjectInteraction
import com.example.Packets.MousePackets
import com.example.Packets.WidgetPackets
import com.example.PathingTesting.PathingTesting
import com.google.inject.Inject
import com.google.inject.Provides
import com.jrplugins.AutoChop.enums.LogAction
import com.jrplugins.AutoChop.enums.State
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler
import net.runelite.api.*
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.GameTick
import net.runelite.api.events.NpcDespawned
import net.runelite.api.events.NpcSpawned
import net.runelite.client.config.ConfigManager
import net.runelite.client.config.Keybind
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.input.KeyManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.overlay.OverlayManager
import net.runelite.client.util.HotkeyListener
import org.lwjgl.system.linux.Stat
import java.awt.event.KeyEvent
import java.util.*
import java.util.function.Supplier

@PluginDescriptor(
    name = "<html><font color=\"#ff4d00\">[GS]</font> Auto Chop </html>",
    description = "Choppy Choppy",
    tags = ["jr", "woodcutting", "forestry"],
    enabledByDefault = false
)
class AutoChop : Plugin() {
    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var config: AutoChopConfig

    @Inject
    private lateinit var breakHandler: ReflectBreakHandler

    @Inject
    private lateinit var autoChopOverlay: AutoChopOverlay

    @Inject
    private lateinit var overlayManager: OverlayManager

    @Inject
    private lateinit var keyManager: KeyManager

    lateinit var state: State
    var started: Boolean = false

    private val circles: MutableList<NPC> = ArrayList(5)

    private lateinit var bankingArea: WorldArea
    private lateinit var treeArea: WorldArea
    private lateinit var bankDestination: WorldPoint
    private lateinit var treeDestination: WorldPoint

    var tickDelay = 0

    private val distance = 15

    @Provides
    fun getConfig(configManager: ConfigManager): AutoChopConfig {
        return configManager.getConfig(AutoChopConfig::class.java)
    }

    @Throws(Exception::class)
    override fun startUp() {
        if (client.gameState != GameState.LOGGED_IN) return
        breakHandler.registerPlugin(this);
        breakHandler.startPlugin(this);
        keyManager.registerKeyListener(toggle)
        changeStateTo(State.IDLE)
    }

    @Throws(Exception::class)
    override fun shutDown() {
        breakHandler.stopPlugin(this);
        breakHandler.unregisterPlugin(this);
        keyManager.unregisterKeyListener(toggle)
        overlayManager.remove(autoChopOverlay)
        started = false
    }

    @Subscribe
    fun onNpcSpawned(event: NpcSpawned) {
        var npc = event.npc
        var id = npc.id
        if (id >= NpcID.RITUAL_CIRCLE_GREEN && id <= NpcID.RITUAL_CIRCLE_RED_12535) {
            circles.add(npc)
        }
    }

    @Subscribe
    fun onNpcDespawned(event: NpcDespawned) {
        var npc = event.npc
        var id = npc.id
        if (id >= NpcID.RITUAL_CIRCLE_GREEN && id <= NpcID.RITUAL_CIRCLE_RED_12535) {
            circles.remove(npc)
        }
    }

    @Subscribe
    fun onGameTick(e: GameTick) {

        if (config.displayOverlay()) { // Toggle overlay
            overlayManager.add(autoChopOverlay)
        } else {
            overlayManager.remove(autoChopOverlay)
        }

        if (breakHandler.shouldBreak(this)) { // Break handler
            breakHandler.startBreak(this)
        }

        if (client.gameState != GameState.LOGGED_IN || !started) { // Check if logged in and Started
            return
        }

        if (!hasAxe()) {
            EthanApiPlugin.sendClientMessage("No se encontró ningUn AXE, deteniendo el plugin")
            EthanApiPlugin.stopPlugin(this)
        }
        if (tickDelay > 0) { // Tick delay
            tickDelay--
            return
        }

        // Set up areas and destinations
        bankingArea = WorldArea(
            config.TREEANDLOCATION().bankAreaXY().width,
            config.TREEANDLOCATION().bankAreaXY().height,
            config.TREEANDLOCATION().bankAreaWH().width,
            config.TREEANDLOCATION().bankAreaWH().height,
            client.plane
        )
        treeArea = WorldArea(
            config.TREEANDLOCATION().treeAreaXY().width,
            config.TREEANDLOCATION().treeAreaXY().height,
            config.TREEANDLOCATION().treeAreaWH().width,
            config.TREEANDLOCATION().treeAreaWH().height,
            client.plane
        )
        bankDestination = WorldPoint(
            config.TREEANDLOCATION().bankWalkLocation().width,
            config.TREEANDLOCATION().bankWalkLocation().height,
            0
        )
        treeDestination = WorldPoint(
            config.TREEANDLOCATION().treeWalkLocation().width,
            config.TREEANDLOCATION().treeWalkLocation().height,
            0
        )


        when (state) { // State machine
            State.IDLE -> handleIdleState()
            State.SEARCHING -> handleSearchingState()
            State.CUTTING -> handleCuttingState()
            State.WALKING_TO_BANK -> handleWalkingToBankState()
            State.BANKING -> handleBankingState()
            State.WALKING_TO_TREES -> handleWalkingToTreesState()
            State.BURN_LOGS -> handleBurnLogsState()
            State.TREE_ROOT -> handleTreeRootState()
            State.FOX_TRAP -> handleFoxTrapState()
            State.RAINBOW -> handleRainbowState()
            State.BEE_HIVE -> handleBeeHiveState()
            State.PHEASANT -> handlePheasantState()
            State.RITUAL_CIRCLES -> handleRitualCirclesState()
            State.ENTLING -> handleEntlingState()
            State.DROP_LOGS -> handleDropLogsState()
        }
    }

    private fun handleDropLogsState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (Inventory.search().nameContains(config.TREEANDLOCATION().logName()).result().isEmpty()) {
                changeStateTo(State.IDLE)
            } else {
                Inventory.search().nameContains(config.TREEANDLOCATION().logName()).result().forEach { log ->
                    InventoryInteraction.useItem(log, "Drop")
                }
            }
        }
        checkEvents() // Check for events
    }

    private fun handleEntlingState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (entlingExists()) {
                val (entling, action) = findEntlingToPrune() ?: return
                NPCInteraction.interact(entling, action)
                return
            } else {
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handleRitualCirclesState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (ritualCircleExists()) {
                if (client.localPlayer.worldLocation != solveCircles()!!.worldLocation) {
                    PathingTesting.walkTo(solveCircles()!!.worldLocation)
                    tickDelay = 1
                    return
                }
            } else {
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handlePheasantState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (Inventory.search().nameContains("Pheasant egg").result().isNotEmpty()) {
                if (NPCs.search().nameContains("Freaky Forester").result().isEmpty()) {
                    changeStateTo(State.IDLE)
                }
                NPCs.search().nameContains("Freaky Forester").nearestToPlayer().ifPresent { forester ->
                    NPCInteraction.interact(forester, "Talk-to")
                }
                return
            }
            if (pheasantExists()) {
                val pheasantNPCLocations = NPCs.search().withName("Pheasant").result().map { it.worldLocation }
                TileObjects.search().nameContains("Pheasant Nest").withAction("Retrieve-egg").withinDistance(distance)
                    .filter { nest -> !pheasantNPCLocations.contains(nest.worldLocation) }
                    .nearestToPlayer().ifPresent { pheasant ->
                        TileObjectInteraction.interact(pheasant, "Retrieve-egg")
                    }
                return
            } else {
                changeStateTo(State.IDLE, 3)
            }
        }
    }

    private fun handleBeeHiveState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (beeHiveExists() && Inventory.search().nameContains("ogs").result().isNotEmpty()) {
                if (Widgets.search().withTextContains("How many logs would you like to add").result().isNotEmpty()) {
                    sendKey(KeyEvent.VK_SPACE)
                    tickDelay = 3
                    return
                }
                NPCs.search().nameContains("nfinished Beehive").withAction("Build").nearestToPlayer()
                    .ifPresent { beeHive ->
                        NPCInteraction.interact(beeHive, "Build")
                    }
                return
            } else if (sturdyBeeHiveExists()) {
                TileObjects.search().nameContains("Sturdy beehive").withAction("Take").nearestToPlayer()
                    .ifPresent { sturdyBeeHive ->
                        TileObjectInteraction.interact(sturdyBeeHive, "Take")
                    }
            } else {
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handleRainbowState() {
        if (rainbowExists()) {
            if (client.localPlayer.worldLocation != rainbowLocation()) {
                PathingTesting.walkTo(rainbowLocation())
                tickDelay = 1
                return
            }
        } else {
            changeStateTo(State.IDLE)
        }
    }

    private fun handleFoxTrapState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (foxTrapExists()) {
                NPCs.search().nameContains("Fox trap").withAction("Disarm").nearestToPlayer().ifPresent { foxTrap ->
                    NPCInteraction.interact(foxTrap, "Disarm")
                }
                return
            } else if (!foxPoacherExists()) {
                changeStateTo(State.IDLE, 4)
            }
        }
    }

    private fun handleTreeRootState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (treeRootExists()) {
                useSpecial()
                TileObjects.search().nameContains("infused Tree root").withAction("Chop down").withinDistance(distance)
                    .nearestToPlayer().ifPresent { treeRoot ->
                        TileObjectInteraction.interact(treeRoot, "Chop down")
                    }
                return
            } else {
                changeStateTo(State.IDLE)
            }
        }
    }

    private fun handleBurnLogsState() {
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (Widgets.search().withTextContains("What would you like to burn").result().isNotEmpty()) {
                sendKey(KeyEvent.VK_SPACE)
                return
            }
            if (Inventory.search().nameContains(config.TREEANDLOCATION().logName()).result().isNotEmpty()) {
                val campFire =
                    TileObjects.search().nameContains("Campfire").withAction("Tend-to").withinDistance(distance)
                        .nearestToPlayer()
                if (campFire.isPresent) {
                    TileObjectInteraction.interact(campFire.get(), "Tend-to")
                    return
                } else {
                    changeStateTo(State.WALKING_TO_BANK)
                }
            }
            if (Inventory.search().nameContains(config.TREEANDLOCATION().logName()).result().isEmpty()) {
                changeStateTo(State.IDLE)
            }
        }
        checkEvents() // Check for events
    }

    private fun handleWalkingToTreesState() {
        if (treeArea.contains(client.localPlayer.worldLocation)) {
            changeStateTo(State.IDLE)
        } else {
            if (!EthanApiPlugin.isMoving() || !treeArea.contains(client.localPlayer.worldLocation)) {
                PathingTesting.walkTo(treeDestination)
            }
        }
    }

    private fun handleWalkingToBankState() {
        if (bankingArea.contains(client.localPlayer.worldLocation)) {
            changeStateTo(State.BANKING)
        } else {
            if (!EthanApiPlugin.isMoving()
                || EthanApiPlugin.playerPosition().distanceTo(bankDestination) > 3
                && !bankingArea.contains(client.localPlayer.worldLocation)
            ) {
                PathingTesting.walkTo(bankDestination)
            }
            if (bankingArea.contains(client.localPlayer.worldLocation) && !EthanApiPlugin.isMoving()) {
                changeStateTo(State.BANKING)
            }
        }
    }

    private fun handleBankingState() {
        if (!Bank.isOpen() && !EthanApiPlugin.isMoving() && Inventory.full()) {
            var bank = TileObjects.search().nameContains("Bank").withAction("Bank").nearestToPlayer()
            if (bank.isPresent) {
                TileObjectInteraction.interact(bank.get(), "Bank")
            } else {
                TileObjects.search().nameContains("Bank").withAction("Use").nearestToPlayer().ifPresent { bank ->
                    TileObjectInteraction.interact(bank, "Use")
                }
            }
            tickDelay = 1
            return
        }
        if (Bank.isOpen()) {
            BankInventory.search().nameContains("ogs").withAction("Deposit-All").first().ifPresent { log ->
                BankInventoryInteraction.useItem(log, "Deposit-All")
            }
            changeStateTo(State.IDLE)
        }
    }

    private fun handleCuttingState() {
        checkEvents() // Check for events
        if (!EthanApiPlugin.isMoving() && client.localPlayer.animation == -1) {
            if (Inventory.full()) {
                when (config.LOGACTION()) {
                    LogAction.Drop -> changeStateTo(State.DROP_LOGS)
                    LogAction.Burn -> changeStateTo(State.BURN_LOGS)
                    LogAction.Bank -> changeStateTo(State.WALKING_TO_BANK)
                }
            } else {
                changeStateTo(State.IDLE) // Change state to idle if inventory is not full
            }
        }
    }

    private fun handleSearchingState() {
        TileObjects.search().nameContains(config.TREEANDLOCATION().treeName())
            .withAction(config.TREEANDLOCATION().treeAction())
            .withinDistance(distance).nearestToPoint(getObjectWMostPlayers()).ifPresent { tree ->
                useSpecial()
                TileObjectInteraction.interact(tree, config.TREEANDLOCATION().treeAction())
                changeStateTo(State.CUTTING)
            }
    }

    private fun handleIdleState() {
        if (runIsOff() && client.energy >= 20 * 100) {
            MousePackets.queueClickPacket()
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1)
        }

        if (Inventory.full()) {
            when (config.LOGACTION()) {
                LogAction.Burn -> if (campFireExists()) changeStateTo(State.BURN_LOGS) else changeStateTo(State.WALKING_TO_BANK)
                LogAction.Drop -> changeStateTo(State.DROP_LOGS)
                LogAction.Bank -> if (bankingArea.contains(client.localPlayer.worldLocation)) changeStateTo(State.BANKING) else changeStateTo(State.WALKING_TO_BANK)
            }
        } else {
            if (!treeArea.contains(client.localPlayer.worldLocation)) changeStateTo(State.WALKING_TO_TREES) // Walk to trees if not in tree area
            else if (!checkEvents()) changeStateTo(State.SEARCHING) // Check for events and change state to searching if none
        }
    }

    private fun getObjectWMostPlayers(): WorldPoint {
        val objectName: String = config.TREEANDLOCATION().treeName()
        val playerCounts: MutableMap<WorldPoint, Int> = HashMap()
        var mostPlayersTile: WorldPoint? = null
        var highestCount = 0
        val objects = TileObjects.search().withName(objectName).result()

        val players = Players.search().notLocalPlayer().result()

        for (tree in objects) {
            for (player in players) {
                if (player.worldLocation.distanceTo(tree.worldLocation) <= 2) {
                    val playerTile = player.worldLocation
                    playerCounts[playerTile] = playerCounts.getOrDefault(playerTile, 0) + 1
                    if (playerCounts[playerTile]!! > highestCount) {
                        highestCount = playerCounts[playerTile]!!
                        mostPlayersTile = playerTile
                    }
                }
            }
        }

        return mostPlayersTile ?: client.localPlayer.worldLocation
    }

    fun solveCircles(): NPC? {
        if (circles.size != 5) {
            return null
        }

        var s = 0
        for (npc in circles) {
            val off = npc.id - NpcID.RITUAL_CIRCLE_GREEN
            val shape = off / 4
            val color = off % 4
            val id = (16 shl shape) or (1 shl color)
            s = s xor id
        }
        for (npc in circles) {
            val off = npc.id - NpcID.RITUAL_CIRCLE_GREEN
            val shape = off / 4
            val color = off % 4
            val id = (16 shl shape) or (1 shl color)
            if ((id and s) == id) {
                return npc
            }
        }
        return null
    }

    private fun foxTrapExists(): Boolean = NPCs.search().nameContains("Fox trap").result().isNotEmpty()
    private fun treeRootExists(): Boolean =
        TileObjects.search().nameContains("infused Tree root").withinDistance(distance).result().isNotEmpty()

    private fun rainbowExists(): Boolean =
        TileObjects.search().nameContains("ainbow").withinDistance(distance).result().isNotEmpty()

    private fun beeHiveExists(): Boolean =
        NPCs.search().nameContains("nfinished Beehive").result().isNotEmpty()

    private fun sturdyBeeHiveExists(): Boolean =
        TileObjects.search().nameContains("Sturdy beehive").result().isNotEmpty()

    private fun rainbowLocation(): WorldPoint =
        TileObjects.search().nameContains("ainbow").withinDistance(distance).nearestToPlayer().get().worldLocation

    private fun campFireExists(): Boolean =
        TileObjects.search().nameContains("Campfire").withinDistance(distance).result().isNotEmpty()

    private fun pheasantExists(): Boolean =
        TileObjects.search().nameContains("Pheasant Nest").withAction("Retrieve-egg").withinDistance(distance).result()
            .isNotEmpty()

    private fun ritualCircleExists(): Boolean = circles.isNotEmpty()
    private fun entlingExists(): Boolean = NPCs.search().nameContains("Entling").result().isNotEmpty()
    private fun foxPoacherExists(): Boolean = NPCs.search().nameContains("Poacher").result().isNotEmpty()
    private fun hasAxe(): Boolean = !Equipment.search().nameContains("axe").empty()
            || !Inventory.search().nameContains("axe").empty()

    private fun runIsOff(): Boolean = EthanApiPlugin.getClient().getVarpValue(173) == 0


    private fun checkEvents(): Boolean {
        if (treeRootExists()) {
            breakPlayersAnimation()
            changeStateTo(State.TREE_ROOT)
            return true
        }
        if (foxTrapExists()) {
            breakPlayersAnimation()
            changeStateTo(State.FOX_TRAP)
            return true
        }
        if (rainbowExists()) {
            breakPlayersAnimation()
            changeStateTo(State.RAINBOW)
            return true
        }
        if (beeHiveExists() && Inventory.search().nameContains("ogs").result().count() >= 2) {
            breakPlayersAnimation()
            changeStateTo(State.BEE_HIVE)
            return true
        }
        if (pheasantExists()) {
            breakPlayersAnimation()
            changeStateTo(State.PHEASANT)
            return true
        }
        if (ritualCircleExists()) {
            breakPlayersAnimation()
            changeStateTo(State.RITUAL_CIRCLES, 1)
            return true
        }
        if (findEntlingToPrune() != null) {
            breakPlayersAnimation()
            changeStateTo(State.ENTLING)
            return true
        }
        return false
    }

    data class PruningAction(val entling: NPC, val action: String)

    private fun findEntlingToPrune(): PruningAction? {
        val playerLocation = client.localPlayer.worldLocation

        val entlings = NPCs.search().nameContains("Entling").result()
            .filter { it.overheadText != null }
            .sortedBy { it.worldLocation.distanceTo(playerLocation) }

        for (entling in entlings) {
            val request = entling.overheadText

            val action = when (request) {
                "Breezy at the back!" -> "Prune-back"
                "A leafy mullet!" -> "Prune-top"
                "Short back and sides!" -> "Prune-back"
                "Short on top!" -> "Prune-top"
                else -> continue
            }

            return PruningAction(entling, action)
        }

        return null // No entling found that needs pruning
    }


    private fun breakPlayersAnimation(): Boolean {
        if (EthanApiPlugin.isMoving() || client.localPlayer.animation != -1) {
            if (Inventory.search().nameContains("ogs").result().isNotEmpty()) {
                Inventory.search().nameContains("ogs").first().ifPresent { log ->
                    InventoryInteraction.useItem(log, "Drop")
                }
                return true
            }
        }
        return false
    }

    private fun useSpecial() {
        if (client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000) {
            if (!Equipment.search().matchesWildCardNoCase("*Dragon axe*").empty()
                || !Equipment.search().matchesWildCardNoCase("*infernal axe*").empty()
                || !Equipment.search().matchesWildCardNoCase("*Dragon felling axe*").empty()
            ) {
                MousePackets.queueClickPacket()
                WidgetPackets.queueWidgetActionPacket(1, 38862884, -1, -1)
            }
        }
    }

    private fun changeStateTo(stateName: State, ticksToDelay: Int = 0) {
        state = stateName
        tickDelay = ticksToDelay
        // println("State : $stateName")
    }

    private val toggle: HotkeyListener = object : HotkeyListener(Supplier<Keybind> { config.toggle() }) {
        override fun hotkeyPressed() {
            toggle()
        }
    }

    fun toggle() {
        if (client.gameState != GameState.LOGGED_IN) {
            return
        }
        started = !started
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

}
