package com.piggyplugins.autosquirk;

import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

public class SquirkOverlay extends OverlayPanel {

    private final SquirkPlugin plugin;
    private final SquirkConfig config;
    String timeFormat;

    @Inject
    private SquirkOverlay(final SquirkPlugin plugin, final SquirkConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Hunter overlay"));
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.timer == null) {
            return null;
        }

        Duration duration = Duration.between(plugin.timer, Instant.now());
        timeFormat = (duration.toHours() < 1) ? "mm:ss" : "HH:mm:ss";
        String formatted = formatDuration(duration.toMillis(), timeFormat);
        panelComponent.setPreferredSize(new Dimension(200, 100));

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("RB Squirker")
                .color(ColorUtil.fromHex("#86C43F"))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Time:")
                .right(formatted)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("State:")
                .right(plugin.currstate.toString())
                .build());


            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Made:")
                    .right(plugin.createdCannonballs + " (" + plugin.actionsPrHour + "p/H)")
                    .build());


        if (true) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Tick Delay:")
                    .right(plugin.timeout + "")
                    .build());
        }

        return super.render(graphics);
    }

}
