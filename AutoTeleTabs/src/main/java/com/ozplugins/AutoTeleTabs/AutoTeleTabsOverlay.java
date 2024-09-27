package com.ozplugins.AutoTeleTabs;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
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
class AutoTeleTabsOverlay extends OverlayPanel {
    private final AutoTeleTabsPlugin plugin;
    private final AutoTeleTabsConfiguration config;


    String timeFormat;
    public String infoStatus = "Starting...";

    @Inject
    private AutoTeleTabsOverlay(final Client client, final AutoTeleTabsPlugin plugin, final AutoTeleTabsConfiguration config) {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Auto Tele Tabs overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (plugin.botTimer == null || !config.enableUI()) {
            log.debug("Overlay conditions not met, not starting overlay");
            return null;
        }

        if (!plugin.enablePlugin) {
            infoStatus = "Plugin disabled";
        }
        Duration duration = Duration.between(plugin.botTimer, Instant.now());
        timeFormat = (duration.toHours() < 1) ? "mm:ss" : "HH:mm:ss";
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Auto Tele Tabs " + config.version)
                .color(ColorUtil.fromHex("#FFE247"))
                .build());

            if (plugin.enablePlugin) {
                //panelComponent.setBackgroundColor(ColorUtil.fromHex("#E6837A63")); //TODO add some background color that looks good.
                panelComponent.setPreferredSize(new Dimension(200, 200));
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
                        .left("Time running:")
                        .leftColor(Color.WHITE)
                        .right(formatDuration(duration.toMillis(), timeFormat))
                        .rightColor(Color.WHITE)
                        .build());

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("State:")
                        .leftColor(Color.WHITE)
                        .right(String.valueOf(plugin.state))
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
