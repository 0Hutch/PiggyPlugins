//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.polyplugins.BobTheBuilder;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class BobTheBuilderOverlay extends OverlayPanel {
    private final BobTheBuilderPlugin plugin;

    @Inject
    private BobTheBuilderOverlay(BobTheBuilderPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.setPreferredSize(new Dimension(160, 160));
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.setPreferredSize(new Dimension(200, 320));
        this.panelComponent.getChildren().add(TitleComponent.builder().text("El constructor").color(new Color(0, 216, 249)).build());
        this.panelComponent.getChildren().add(TitleComponent.builder().text(this.plugin.started ? "Encendido" : "Pausado").color(this.plugin.started ? Color.GREEN : Color.RED).build());
        this.panelComponent.getChildren().add(LineComponent.builder().left("Estado: ").leftColor(new Color(215, 255, 0)).right(this.plugin.state != null && this.plugin.started ? this.plugin.state.name() : "Apagado").rightColor(Color.WHITE).build());
        this.panelComponent.getChildren().add(LineComponent.builder().left("DEBUG: ").leftColor(new Color(216, 255, 255)).right(this.plugin.debug).rightColor(Color.WHITE).build());
        return super.render(graphics);
    }
}
