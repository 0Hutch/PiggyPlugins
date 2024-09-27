package com.ozplugins.AutoCombat;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Singleton;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

@Slf4j
@Singleton
public class DebugTileOverlay extends OverlayPanel {
    AutoCombatPlugin plugin;
    private final AutoCombatConfiguration config;

    Client client;

    DebugTileOverlay(Client client, AutoCombatPlugin plugin, AutoCombatConfiguration config) {
        this.plugin = plugin;
        this.client = client;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "CombatTile overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.enableDebugTiles()) {
            return null;
        }
        if (plugin.enemyAttackArea != null && !plugin.enemyAttackArea.isEmpty() && config.UseSafespotNPCRadius()) {
            plugin.enemyAttackArea.forEach((enemyRadiusTile) -> {
                if (enemyRadiusTile == null) {
                    return;
                }
                renderArea(graphics, LocalPoint.fromWorld(client, enemyRadiusTile), plugin.config.enemyRadius(), 1, plugin.config.enemyRadiusAreaFill(), 1, "");
            });
        }

        if (plugin.enemyArea != null) {
            plugin.enemyArea.forEach((enemyAreaTile) -> {
                if (enemyAreaTile == null) {
                    return;
                }
                renderArea(graphics, LocalPoint.fromWorld(client, enemyAreaTile), plugin.config.enemyArea(), 2, plugin.config.enemyAreaFill(), 1, "");
            });
        }

        if (plugin.cannonSpot != null && config.UseCannon()) {
            renderArea(graphics, LocalPoint.fromWorld(client, plugin.cannonSpot), plugin.config.cannonSpotTile(), 2, plugin.config.cannonSpotTileFill(), 1, "Cannon");
        }

        if (plugin.safespotTile != null && config.UseSafespot()) {
            renderArea(graphics, LocalPoint.fromWorld(client, plugin.safespotTile), plugin.config.safespotTile(), 2, plugin.config.safespotTileFill(), 1, "Safespot");
        }

        return super.render(graphics);
    }

    private void renderArea(final Graphics2D graphics, final LocalPoint dest, final Color color,
                            final double borderWidth, final Color fillColor, int size, String label) {
        if (dest == null) {
            return;
        }

        final Polygon poly = Perspective.getCanvasTileAreaPoly(client, dest, size);

        if (poly == null) {
            return;
        }
        OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
        if (!Strings.isNullOrEmpty(label)) {
            Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, dest, label, 0);
            if (canvasTextLocation != null) {
                graphics.setFont(new Font("Arial", Font.PLAIN, 9));
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
            }
        }
    }
}
