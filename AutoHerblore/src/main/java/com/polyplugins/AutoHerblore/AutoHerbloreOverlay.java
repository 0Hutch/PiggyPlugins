package com.polyplugins.AutoHerblore;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class AutoHerbloreOverlay extends OverlayPanel {
    private final Client client;
    private final AutoHerblorePlugin plugin;

    @Inject
    private AutoHerbloreOverlay(Client client, SpriteManager spriteManager, AutoHerblorePlugin plugin) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.BOTTOM_RIGHT);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    public Dimension render(Graphics2D graphics2D) {
        this.panelComponent.getChildren().clear();
        LineComponent tickDelay = this.buildLine("Tick Delay: ", Integer.toString(this.plugin.tickDelay));
        this.panelComponent.getChildren().add(tickDelay);
        LineComponent lastCreated = this.buildLine("Ultimo creado: ", Integer.toString(this.plugin.lastCreated));
        this.panelComponent.getChildren().add(lastCreated);
        return super.render(graphics2D);
    }

    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder().left(left).right(right).leftColor(Color.WHITE).rightColor(Color.cyan).build();
    }

    private String formatTime(Long timeInMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60L;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60L;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
