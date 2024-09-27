package com.polyplugins.BobTheWizard;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class BobTheWizardOverlay extends OverlayPanel {
    private final BobTheWizardPlugin plugin;

    @Inject
    private BobTheWizardOverlay(BobTheWizardPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.setPreferredSize(new Dimension((-1148194101 ^ -1190137141) >>> (1938 ^ 14208), (-4227 >>> 10272) - (-4387 >>> 9792)));
    }

    public Dimension render(Graphics2D graphics) {
        this.panelComponent.setPreferredSize(new Dimension(-18965 - -8154 + -11847 + 22858, (-31496 ^ 1245) + (4345 << 2496)));
        //this.panelComponent.setPreferredSize(new Dimension(-18965 - -8154 + -11847 + 22858, (-31496 ^ 1245) + ('脛' << 2496)));
        this.panelComponent.getChildren().add(TitleComponent.builder().text("Alch+Tele o Plank").color(new Color(0, 13824 << 15662 >>> (3637 << 418), -16867 - -494 ^ -21433 - -5293)).build());
        this.panelComponent.getChildren().add(TitleComponent.builder().text(this.plugin.started ? "Corriendo" : "Pausado").color(this.plugin.started ? Color.GREEN : Color.RED).build());
        this.panelComponent.getChildren().add(LineComponent.builder().left("Estado: ").leftColor(new Color(0, 31544 + -24722 + (2147480345 << 16097), 534773760 << 12321 >>> -19411 + (char)29897)).right(this.plugin.state != null && this.plugin.started ? this.plugin.state.name() : "Apagado").rightColor(Color.WHITE).build());
        return super.render(graphics);
    }
}
