package com.piggyplugins.AutoWarriorsGuild;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;


public class WarriorsGuildRoomOverlays extends Overlay {
    private final WarriorsGuildPlugin plugin;
    private final AnimatorRoom overlay;
    private final Client client;
    private final WarriorsGuildConfig config;

    @Inject
    public WarriorsGuildRoomOverlays(WarriorsGuildPlugin plugin, Client client, AnimatorRoom overlay, WarriorsGuildConfig config) {
        this.plugin = plugin;
        this.client = client;
        this.overlay = overlay;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);

    }

    @Override
    public Dimension render(Graphics2D graphics) {
            if (!config.showRoomOverlays())
            {
                return null;
            }

        for (WorldPoint worldPoint : overlay.getTilesWithinRoom()) {
            drawTile(graphics, worldPoint, new Color(0, 255, 0, 53), new Color(135, 255, 0, 255));
        }
        return null;
    }



    private void drawTile(Graphics2D graphics, WorldPoint tile, Color fillColor, Color borderColor) {
        if (tile.getPlane() != client.getLocalPlayer().getWorldLocation().getPlane())
            return;

        LocalPoint lp = LocalPoint.fromWorld(client, tile);
        if (lp == null)
            return;

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly == null)
            return;

        final Stroke originalStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(1));
        graphics.setColor(fillColor);
        graphics.fillPolygon(poly);
        graphics.setColor(borderColor);
        graphics.drawPolygon(poly);
        graphics.setStroke(originalStroke);
    }
}
