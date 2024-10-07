package com.piggyplugins.collectionlogluck.luck;

import com.piggyplugins.collectionlogluck.util.LuckUtils;
import lombok.Getter;

import java.awt.*;

public class LuckCalculationResult {

    @Getter
    private double luck;

    @Getter
    private double dryness;

    @Getter
    private double overallLuck;

    @Getter
    private Color luckColor;

    public LuckCalculationResult(double luck, double dryness) {
        this.luck = luck;
        this.dryness = dryness;
        this.overallLuck = LuckUtils.getOverallLuck(luck, dryness);
        this.luckColor = LuckUtils.getOverallLuckColor(overallLuck);
    }

}
