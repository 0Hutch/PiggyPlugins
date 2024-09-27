package com.ozplugins.AutoScurrius;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

@Slf4j
@Singleton
public class AutoScurriusOverlay extends OverlayPanel {
    private final Client client;
    private final AutoScurriusPlugin plugin;

    private final AutoScurriusConfig config;

    String timeFormat;
    public String infoStatus = "Starting...";

    @Inject
    private AutoScurriusOverlay(Client client, AutoScurriusPlugin plugin, AutoScurriusConfig config) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.BOTTOM_LEFT);
        //setDragTargetable(true);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Auto Scurrius overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (plugin.botTimer == null || !config.enableUI()) {
            return null;
        }

        if (!plugin.enablePlugin) {
            infoStatus = "Plugin disabled";
        }
        Duration duration = Duration.between(plugin.botTimer, Instant.now());
        timeFormat = (duration.toHours() < 1) ? "mm:ss" : "HH:mm:ss";
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Auto Scurrius " + config.version)
                .color(ColorUtil.fromHex("#FFE247"))
                .build());

        if (plugin.enablePlugin) {
            panelComponent.setPreferredSize(new Dimension(250, 250));
            panelComponent.setBorder(new Rectangle(5, 5, 5, 5));

            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Plugin Enabled")
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .leftColor(Color.WHITE)
                    .right(infoStatus)
                    .rightColor(Color.WHITE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("State:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.state))
                    .rightColor(Color.WHITE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Kill count:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.kc))
                    .rightColor(Color.WHITE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Should bank:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.shouldBank()))
                    .rightColor(Color.WHITE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Should loot:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.needsToLoot()))
                    .rightColor(Color.WHITE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("In Sewer:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.inSewer()))
                    .rightColor(Color.WHITE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("In Fight:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.isInFightRoom()))
                    .rightColor(Color.WHITE)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time running:")
                    .leftColor(Color.WHITE)
                    .right(formatDuration(duration.toMillis(), timeFormat))
                    .rightColor(Color.WHITE)
                    .build());


        } else {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Plugin Disabled")
                    .color(Color.RED)
                    .build());

        }
        return super.render(graphics);
    }
}
