package com.piggyplugins.collectionlogluck.util;

import java.awt.*;

public final class LuckUtils {

    // Format luck as a percentage with at least 2 significant digits, or more if needed for numbers close to 0 or 1.
    // Example: 0.0000145 -> 0.000015; 0.2535 -> 0.25; 0.500000 -> 0.50; 0.9999999 -> 0.9999999
    public static String formatLuckSigDigits(double luck) {
        if (luck >= 1) {
            return "100";
        }
        if (luck <= 0) {
            return "0";
        }

        int sigDigitsIfUnlucky = 2;
        // prevent numbers very close to 1, like 0.999, from being rounded up
        int sigDigitsIfLucky = (int) Math.ceil(-Math.log10(1.0-luck));

        int sigDigits = Math.max(sigDigitsIfUnlucky, sigDigitsIfLucky);
        return String.format("%."+sigDigits+"G", luck*100);
    }

    // Return green when overall luck = 1, red when overall luck = 0, and interpolate for values in between
    // Note that interpolation is done in HSB space, not for individual RGB components,
    // so the midpoint is standard yellow.
    public static Color getOverallLuckColor(double overallFraction) {
        // green is 1/3rd of the way around the HSB wheel
        return new Color(Color.HSBtoRGB((float) (overallFraction / 3.0), 1.0f, 1.0f)).darker().darker();
    }

    public static double getOverallLuck(double luckFraction, double drynessFraction) {
        double overallFraction = (luckFraction - drynessFraction + 1) / 2.0;
        // double check to make sure the fraction is in the correct range [0,1]
        overallFraction = Math.max(0, Math.min(1, overallFraction));
        return overallFraction;
    }

    // Return green when luck = 1, yellow when luck = 0, and interpolate for values in between.
    // luck = 0 could be a result of low KC, so it is just colored yellow.
    private static Color getLuckColor(double luckFraction) {
        int r = (int)(255 * (1 - luckFraction));
        return new Color(r, 255, 0).darker();
    }

    // Return red when dryness = 1, yellow when dryness = 0, and interpolate for values in between.
    // dryness = 0 could be a result of low KC, so it is just colored yellow.
    private static Color getDrynessColor(double drynessFraction) {
        int g = (int)(255 * (1 - drynessFraction));
        return new Color(255, g, 0).darker();
    }

    // Given a number (e.g. 3), return its ordinal suffix ("rd").
    public static String getOrdinalSuffix(int n) {
        int tensDigit = n % 10;
        int percentile = n % 100;
        if (tensDigit == 1 && percentile != 11) {
            return "st";
        } else if (tensDigit == 2 && percentile != 12) {
            return "nd";
        } else if (tensDigit == 3 && percentile != 13) {
            return "rd";
        }
        return "th";
    }
}
