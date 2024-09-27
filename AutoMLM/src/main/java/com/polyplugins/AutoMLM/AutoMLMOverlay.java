package com.polyplugins.AutoMLM;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class AutoMLMOverlay extends OverlayPanel {
    private static final Logger log = LoggerFactory.getLogger(AutoMLMOverlay.class);
    private final AutoMLMPlugin plugin;
    private final AutoMLMConfiguration config;
    String timeFormat;
    public String infoStatus = "Comenzando...";

    @Inject
    private AutoMLMOverlay(Client client, AutoMLMPlugin plugin, AutoMLMConfiguration config) {
        super(plugin);
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Auto MLM overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        if (this.plugin.botTimer != null && this.config.enableUI()) {
            if (!this.plugin.enablePlugin) {
                this.infoStatus = "Plugin apagado";
            }

            Duration duration = Duration.between(this.plugin.botTimer, Instant.now());
            this.timeFormat = duration.toHours() < 1L ? "mm:ss" : "HH:mm:ss";
            List var10000 = this.panelComponent.getChildren();
            TitleComponent.TitleComponentBuilder var10001 = TitleComponent.builder();
            AutoMLMConfiguration var10002 = this.config;
            var10000.add(var10001.text("Auto MLM " + "v1.0").color(ColorUtil.fromHex("#FFE247")).build());
            if (this.plugin.uiSetting != UISettings.NONE) {
                if (this.plugin.enablePlugin) {
                    this.panelComponent.setPreferredSize(new Dimension(200, 200));
                    this.panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
                    this.panelComponent.getChildren().add(TitleComponent.builder().text("Plugin encendido").color(Color.GREEN).build());
                    switch (this.plugin.uiSetting) {
                        case FULL:
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Estado:").leftColor(Color.WHITE).right(this.infoStatus).rightColor(Color.WHITE).build());
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Tiempo:").leftColor(Color.WHITE).right(DurationFormatUtils.formatDuration(duration.toMillis(), this.timeFormat)).rightColor(Color.WHITE).build());
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Minerales Depositado:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.depositOres)).rightColor(Color.WHITE).build());
                            break;
                        case DEFAULT:
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Estado:").leftColor(Color.WHITE).right(this.infoStatus).rightColor(Color.WHITE).build());
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Tiempo:").leftColor(Color.WHITE).right(DurationFormatUtils.formatDuration(duration.toMillis(), this.timeFormat)).rightColor(Color.WHITE).build());
                        case SIMPLE:
                    }
                } else {
                    this.panelComponent.getChildren().add(TitleComponent.builder().text("Plugin apagado").color(Color.RED).build());
                }
            }

            return super.render(graphics);
        } else {
            log.debug("Condiciones de superposición no cumplidas, no se inicia la superposición");
            return null;
        }
    }
}
