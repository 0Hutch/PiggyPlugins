package com.ozplugins.AutoScurrius;


import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

public class AutoScurriusTileOverlay extends Overlay {
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;
    private final AutoScurriusPlugin plugin;

    @Inject
    private AutoScurriusTileOverlay(Client client, AutoScurriusPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.UNDER_WIDGETS);

    }

    @Override
    public Dimension render(Graphics2D graphics) {

            if (plugin.botTimer == null || !plugin.enablePlugin) {
                return null;
            }
            plugin.enemyArea.forEach((enemyTile) -> {
                if (enemyTile == null) {
                    return;
                }
                renderArea(graphics, LocalPoint.fromWorld(client, enemyTile), Color.MAGENTA, 1, new Color(255, 255, 255, 20), 1);
            });

            /*plugin.safeTiles.forEach((safeTile) -> {
                if (safeTile == null) {
                    return;
                }
                renderArea(graphics, LocalPoint.fromWorld(client, safeTile), Color.CYAN, 1, new Color(255, 255, 255, 20), 1);
            });*/

            if (plugin.goalTile != null) {
                renderTile(graphics, LocalPoint.fromWorld(client, plugin.goalTile), Color.GREEN, 1, new Color(255, 255, 255, 20));
            }



        /*if (plugin.furthestSafeTile != null) {
            renderTile(graphics, LocalPoint.fromWorld(client, plugin.furthestSafeTile), Color.GREEN, 1, new Color(255, 255, 255, 20));
            renderTextLocation(graphics, "Furthest Safe Tile", plugin.furthestSafeTile, Color.GREEN);
        }
        if(plugin.closestSafeTile != null){
            renderTile(graphics, LocalPoint.fromWorld(client, plugin.closestSafeTile), Color.GREEN, 1, new Color(255, 255, 255, 20));
            renderTextLocation(graphics, "Closest Safe Tile", plugin.closestSafeTile, Color.GREEN);
        }*/

        return null;
    }

    /**
     * Builds a line component with the given left and right text
     *
     * @param left
     * @param right
     * @return Returns a built line component with White left text and Yellow right text
     */
    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder()
                .left(left)
                .right(right)
                .leftColor(Color.WHITE)
                .rightColor(Color.YELLOW)
                .build();
    }

    private void renderTile(Graphics2D graphics, LocalPoint dest, Color color, double borderWidth, Color fillColor) {
        if (dest != null) {
            Polygon poly = Perspective.getCanvasTilePoly(this.client, dest);
            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
            }
        }
    }

    private void renderTextLocation(Graphics2D graphics, String text, WorldPoint worldPoint, Color color) {
        LocalPoint point = LocalPoint.fromWorld(client, worldPoint);
        if (point == null) {
            return;
        }
        Point textLocation = Perspective.getCanvasTextLocation(client, graphics, point, text, 0);
        if (textLocation != null) {
            OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
        }
    }

    private void renderArea(final Graphics2D graphics, final LocalPoint dest, final Color color,
                            final double borderWidth, final Color fillColor, int size) {
        if (dest == null) {
            return;
        }

        final Polygon poly = Perspective.getCanvasTileAreaPoly(client, dest, size);

        if (poly == null) {
            return;
        }
        OverlayUtil.renderPolygon(graphics, poly, color, fillColor, new BasicStroke((float) borderWidth));
    }

}
