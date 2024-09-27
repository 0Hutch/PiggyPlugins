package com.piggyplugins.AutoWarriorsGuild;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AnimatorRoom {
    private final List<WorldPoint> corners;
    @Inject
    Client client;

    @Inject
    public AnimatorRoom() {
        corners = Arrays.asList(
                new WorldPoint(2849, 3545, 0), // NORTH WESTERN TILE
                new WorldPoint(2849, 3534, 0), // SOUTH WESTERN TILE
                new WorldPoint(2859, 3534, 0), // SOUTH EASTERN TILE
                new WorldPoint(2859, 3537, 0), // INWARD STICKING CORNER
                new WorldPoint(2861, 3537, 0), // 2ND SOUTH EASTERN TILE
                new WorldPoint(2861, 3545, 0)  // NORTH EASTERN TILE
        );
    }

    public List<WorldPoint> getTilesWithinRoom() {
        List<WorldPoint> tilesWithinRoom = new ArrayList<>();

        int minX = corners.stream().min(Comparator.comparingInt(WorldPoint::getX)).get().getX();
        int minY = corners.stream().min(Comparator.comparingInt(WorldPoint::getY)).get().getY();
        int maxX = corners.stream().max(Comparator.comparingInt(WorldPoint::getX)).get().getX() + 1; // Add 1 to include edge
        int maxY = corners.stream().max(Comparator.comparingInt(WorldPoint::getY)).get().getY() + 1; // Add 1 to include edge

        maxX += 1;
        maxY += 1;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                WorldPoint point = new WorldPoint(x, y, 0); // Assuming the plane (z-coordinate) is 0
                if (isPointInsidePolygon(point, corners)) {
                    tilesWithinRoom.add(point);
                }
            }
        }

        return tilesWithinRoom;
    }

    private boolean isPointInsidePolygon(WorldPoint point, List<WorldPoint> polygon) {
        boolean inside = false;
        int n = polygon.size();
        for (int i = 0, j = n - 1; i < n; j = i++) {
            WorldPoint pi = polygon.get(i);
            WorldPoint pj = polygon.get(j);

            if ((point.getY() - pi.getY()) * (point.getY() - pj.getY()) <= 0 &&
                    (point.getX() - pi.getX()) * (point.getX() - pj.getX()) <= 0 &&
                    (pi.getY() - pj.getY()) * (point.getX() - pi.getX()) ==
                            (pi.getX() - pj.getX()) * (point.getY() - pi.getY())) {
                return true;
            }

            if ((pi.getY() > point.getY()) != (pj.getY() > point.getY())) {
                int intersectX = pi.getX() + (point.getY() - pi.getY()) * (pj.getX() - pi.getX()) / (pj.getY() - pi.getY());
                if (point.getX() < intersectX) {
                    inside = !inside;
                }
            }
        }
        return inside;
    }
}

