/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.piggyplugins.autoWhispererPrayers

import com.example.InteractionApi.PrayerInteraction
import net.runelite.api.Prayer
import net.runelite.api.events.ProjectileMoved
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import java.awt.AWTException

@PluginDescriptor(
    name = "<html><font color=\"#ff4d00\">[GS]</font> Auto Whisperer Prayers",
    description = "Auto Whisperer Prayer plugin",
    tags = ["Whisperer", "prayer", "jr", "dt2", "auto", "boss"],
    enabledByDefault = false
)
class AutoWhispererPrayer : Plugin() {
    private var meleeProjectile: Int = 2467
    private var mageProjectile: Int = 2445
    private var rangeProjectile: Int = 2444

    @Throws(AWTException::class)
    override fun startUp() {
        println("¡Se inició el plugin Whisperer!")
    }

    override fun shutDown() {
        println("¡El plugin Whisperer se detuvo!")
        PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MELEE, false)
        PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MAGIC, false)
        PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MISSILES, false)
    }

    @Subscribe
    fun onProjectileMoved(event: ProjectileMoved) {
        val projectile = event.projectile

        //println(projectile.id)

        if (event.projectile.remainingCycles < 10) {
            when (projectile.id) {
                meleeProjectile -> PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MELEE, true)
                mageProjectile -> PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MAGIC, true)
                rangeProjectile -> PrayerInteraction.setPrayerState(Prayer.PROTECT_FROM_MISSILES, true)
            }
        }
    }
}