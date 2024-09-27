package com.piggyplugins.AutoRogues;

import com.google.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class AutoRoguesOverlay extends OverlayPanel {
    private final AutoRoguesPlugin plugin;

    @Inject
    private AutoRoguesOverlay(AutoRoguesPlugin plugin){
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPreferredSize(new Dimension(200, 320));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(200, 320));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Auto Rogues Den")
                .color(Color.MAGENTA)
                .build());
        panelComponent.getChildren().add(TitleComponent.builder().
                text(plugin.getElapsedTime())
                .color(Color.WHITE)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("PLUGIN STATUS")
                .leftColor(Color.WHITE)
                .right(plugin.isStarted() ? "RUNNING" : "PAUSED")
                .rightColor(plugin.isStarted() ? Color.GREEN : Color.RED)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("CURRENT STATE")
                .leftColor(Color.WHITE)
                .right(plugin.isStarted() ? plugin.getState().name() : "PAUSED")
                .rightColor(plugin.isStarted() ? Color.GREEN : Color.RED)
                .build());

        return super.render(graphics);
    }
}
