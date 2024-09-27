package com.bankmemory.bankview;

import java.math.RoundingMode;
import java.text.DecimalFormat;

class ValueFormatter {
    private final DecimalFormat formatter;

    ValueFormatter() {
        formatter = new DecimalFormat();
        formatter.setMaximumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.DOWN);
    }

    String format(long value) {
        return formatter.format(value);
    }

    String formatAbbreviated(long value) {
        long absolute = Math.abs(value);
        double divisor;
        String suffix;
        if (absolute < 10000) {
            divisor = 1;
            suffix = "";
        } else if (absolute < 1000000) {
            divisor = 1000;
            suffix = "K";
        } else if (absolute < 1000000000) {
            divisor = 1000000;
            suffix = "M";
        } else {
            divisor = 1000000000;
            suffix = "B";
        }

        StringBuilder str = new StringBuilder();
        String numStr = formatter.format(value / divisor);
        return  str.append(numStr).append(suffix).toString();
    }

    void setShowPositiveSign(boolean show) {
        formatter.setPositivePrefix(show ? "+" : "");
    }
}
