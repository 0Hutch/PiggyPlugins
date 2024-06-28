package com.gustlikplugins.AutoNightmareZone;

import com.google.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class AutoNightmareZoneOverlay extends OverlayPanel {

    private final AutoNightmareZonePlugin plugin;

    @Inject
    private AutoNightmareZoneOverlay(AutoNightmareZonePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPreferredSize(new Dimension(160, 160));
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(200, 320));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Gustlik NightmareZone")
                .color(new Color(255, 157, 249))
                .build());

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.started ? "Running" : "Paused")
                .color(plugin.started ? Color.GREEN : Color.RED)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("State: ")
                .leftColor(new Color(255, 157, 249))
                .right(plugin.state==null || !plugin.started ? "STOPPED" : plugin.state.getName())
                .rightColor(Color.WHITE)
                .build());


        return super.render(graphics);
    }
}