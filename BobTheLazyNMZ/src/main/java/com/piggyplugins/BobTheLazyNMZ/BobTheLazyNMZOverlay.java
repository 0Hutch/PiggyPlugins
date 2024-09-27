package com.piggyplugins.BobTheLazyNMZ;

import com.google.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;

public class BobTheLazyNMZOverlay extends OverlayPanel {

    private final BobTheLazyNMZPlugin plugin;

    @Inject
    private BobTheLazyNMZOverlay(BobTheLazyNMZPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPreferredSize(new Dimension(160, 160));
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(200, 320));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Bob The Lazy NMZ")
                .color(new Color(255, 157, 249))
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.started ? "Running" : "Paused")
                .color(plugin.started ? Color.GREEN : Color.RED)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Idle counter : ")
                .leftColor(new Color(255, 157, 249))
                .right("" + plugin.idleCounter)
                .rightColor(Color.WHITE)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Absorption counter: ")
                .leftColor(new Color(255, 157, 249))
                .right("" + plugin.abosortionCounter)
                .rightColor(Color.WHITE)
                .build());





        return super.render(graphics);
    }
}
