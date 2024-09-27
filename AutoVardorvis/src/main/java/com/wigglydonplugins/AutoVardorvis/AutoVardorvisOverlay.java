package com.wigglydonplugins.AutoVardorvis;

import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;

import com.google.inject.Inject;
import net.runelite.client.ui.overlay.components.LineComponent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class AutoVardorvisOverlay extends OverlayPanel {

    private final Client client;
    private final SpriteManager spriteManager;
    private final AutoVardorvisPlugin plugin;

    private double killsPerHour = 0.0;




    @Inject
    private AutoVardorvisOverlay(Client client, SpriteManager spriteManager, AutoVardorvisPlugin plugin) {
        super(plugin);
        this.client = client;
        this.spriteManager = spriteManager;
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        String killsText;
        if (killsPerHour != 0.0) {
            killsText = String.format("%.1f", killsPerHour);
        } else {
            killsText = "0.0";
        }
        panelComponent.getChildren().clear();

        LineComponent botState = buildLine("State: ", plugin.botState.toString());
        LineComponent elapsedTime = buildLine("Runtime: ", formatTime(plugin.elapsedTime));
        LineComponent kills = buildLine("Kills: ", plugin.totalKills + " (" + killsText + " p/h)");
        LineComponent tickDelay = buildLine("Tick Delay: ", Integer.toString(plugin.tickDelay));

        panelComponent.getChildren().add(botState);
        panelComponent.getChildren().add(tickDelay);
        panelComponent.getChildren().add(elapsedTime);
        panelComponent.getChildren().add(kills);

        return super.render(graphics2D);
    }

    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder()
                .left(left)
                .right(right)
                .leftColor(Color.WHITE)
                .rightColor(Color.YELLOW)
                .build();
    }

    private String formatTime(Long timeInMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void updateKillsPerHour() {
        if (plugin.elapsedTime > 0) {
            killsPerHour = plugin.totalKills / (plugin.elapsedTime / 3600000.0);
        } else {
            killsPerHour = 0.0;
        }
    }
}
