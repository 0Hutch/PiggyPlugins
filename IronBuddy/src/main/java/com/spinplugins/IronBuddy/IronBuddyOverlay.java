package com.spinplugins.IronBuddy;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class IronBuddyOverlay extends OverlayPanel {
    private final Client client;
    private final IronBuddyConfig config;
    private final IronBuddyPlugin plugin;

    @Inject
    private IronBuddyOverlay(Client client, IronBuddyPlugin plugin, IronBuddyConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(240, 360));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("IronBuddy")
                .color(new Color(214, 143, 49))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Duration:")
                .right(String.format("%d:%d:%d",
                        plugin.runningDuration.toHours(),
                        plugin.runningDuration.toMinutes(),
                        plugin.runningDuration.toSeconds())
                )
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Script:")
                .right(config.taskType().name())
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Executing task:")
                .right(plugin.getActiveTaskName() == null ? "None" : plugin.getActiveTaskName())
                .build());
        if(plugin.timeout > 0) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Tick timeout:")
                    .right(Integer.toString(plugin.timeout))
                    .build());
        }

        return super.render(graphics);
    }
}