/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.piggyplugins.autoLeviathanPrayers

import com.example.InteractionApi.PrayerInteraction
import net.runelite.api.NpcID
import net.runelite.api.Prayer
import net.runelite.api.events.GraphicsObjectCreated
import net.runelite.api.events.NpcSpawned
import net.runelite.api.events.ProjectileMoved
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import java.awt.AWTException

@PluginDescriptor(
    name = "<html><font color=\"#9ddbff\">[GS]</font> Auto Leviathan Prayers",
    description = "Auto Leviathan Prayer plugin",
    tags = ["Leviathan", "prayer", "jr", "dt2", "auto", "boss"],
    enabledByDefault = false
)
class AutoLeviathanPrayer : Plugin() {
    var meleeProjectile: Int = 2488
    var mageProjectile: Int = 2489
    var rangeProjectile: Int = 2487

    @Throws(AWTException::class)
    override fun startUp() {
        println("Leviathan plugin started!")
    }

    override fun shutDown() {
        println("Leviathan plugin stopped!")
        PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MELEE, false)
        PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MAGIC, false)
        PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MISSILES, false)
    }

    @Subscribe
    fun onGraphicsObjectCreated(event: GraphicsObjectCreated) {
        println("Graphics object created")
        println(event.graphicsObject.id)
    }

    @Subscribe
    fun onProjectileMoved(event: ProjectileMoved) {
        val projectile = event.projectile

        println(projectile.id)

        if (event.projectile.remainingCycles < 10) {
            when (projectile.id) {
                meleeProjectile -> PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MELEE, true)
                mageProjectile -> PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MAGIC, true)
                rangeProjectile -> PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MISSILES, true)
            }
        }
    }

    @Subscribe
    fun onNpcSpawned(event: NpcSpawned) {
        println("npc spawned created")
        println(event.npc.id)
        if (event.npc.id == NpcID.ABYSSAL_PATHFINDER) {
            println("pathfinder found!")
        }
    }
}