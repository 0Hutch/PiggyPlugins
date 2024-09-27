/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package com.jrplugins.AutoChop

import net.runelite.api.Client
import net.runelite.client.ui.overlay.Overlay
import net.runelite.client.ui.overlay.OverlayLayer
import net.runelite.client.ui.overlay.OverlayPosition
import net.runelite.client.ui.overlay.components.LineComponent
import net.runelite.client.ui.overlay.components.PanelComponent
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import javax.inject.Inject

class AutoChopOverlay @Inject private constructor(private val client: Client, plugin: AutoChop) : Overlay() {
    private val panelComponent = PanelComponent()
    private val slPanel = PanelComponent()
    private val plugin: AutoChop = plugin

    init {
        position = OverlayPosition.BOTTOM_LEFT
        layer = OverlayLayer.ABOVE_SCENE
        isDragTargetable = true
    }

    override fun render(graphics: Graphics2D): Dimension {
        panelComponent.children.clear()
        slPanel.children.clear()

        val state = buildLine("State: ", plugin.state.toString())
        val started = buildLine("Started: ", plugin.started.toString())
        val tickDelay = buildLine("Tick Delay: ", plugin.tickDelay.toString())

        panelComponent.children.addAll(listOf(state))
        panelComponent.children.addAll(listOf(started))
        panelComponent.children.addAll(listOf(tickDelay))

        return panelComponent.render(graphics)
    }

    /**
     * Builds a line component with the given left and right text
     *
     * @param left
     * @param right
     * @return Returns a built line component with White left text and Yellow right text
     */
    private fun buildLine(left: String, right: String): LineComponent {
        return LineComponent.builder()
            .left(left)
            .right(right)
            .leftColor(Color.WHITE)
            .rightColor(Color.YELLOW)
            .build()
    }
}