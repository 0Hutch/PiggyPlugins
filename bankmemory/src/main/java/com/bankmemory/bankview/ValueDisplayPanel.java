package com.bankmemory.bankview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

class ValueDisplayPanel extends JComponent {
    private static final String GE = "GE: ";
    private static final String HA = "HA: ";

    private final ValueFormatter formatter = new ValueFormatter();
    private long geValue;
    private long haValue;
    private boolean styliseForDiffs;

    void setValues(long geValue, long haValue) { ;
        this.geValue = geValue;
        this.haValue = haValue;
        updateToolTip();
        repaint();
    }

    private void updateToolTip() {
        setToolTipText("" +
                "<html>" +
                "<p> Grand Exchange value: "+ formatter.format(geValue) + "gp</p>" +
                "</br>" +
                "<p>High Alchemy value: "+ formatter.format(haValue) + "gp</p>" +
                "</html>"
        );
    }

    void setStylisedForDiffs(boolean stylise) {
        formatter.setShowPositiveSign(stylise);
        styliseForDiffs = stylise;
        updateToolTip();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Insets i = getInsets();
        Graphics gInset = g.create(i.left, i.top, getWidth() - i.left - i.right, getHeight() - i.top - i.bottom);
        Rectangle b = gInset.getClipBounds();
        paintValue(gInset.create(0, 0, b.width / 2, b.height), GE, geValue);
        paintValue(gInset.create(b.width / 2, 0, b.width / 2, b.height), HA, haValue);
        gInset.dispose();
    }

    private void paintValue(Graphics g, String name, long value) {
        FontMetrics fm = g.getFontMetrics();
        int fontH = fm.getHeight();
        int nameW = fm.stringWidth(name);
        String numText = formatter.formatAbbreviated(value);
        int start = g.getClipBounds().width / 2 - fm.stringWidth(name + numText) / 2;

        g.setColor(getForeground());
        g.drawString(name, start, fontH);

        if (styliseForDiffs) {
            g.setColor(Color.BLACK);
            g.drawString(numText, start + nameW + 1, fontH + 1);
            g.setColor(value == 0 ? getForeground() : value > 0 ? Color.GREEN : Color.RED);
        }
        g.drawString(numText, start + nameW, fontH);

        g.dispose();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        Graphics g = getGraphics();
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D strB = fm.getStringBounds(
                GE + formatter.formatAbbreviated(geValue) + " " + HA + formatter.formatAbbreviated(haValue), g);
        return new Dimension((int) Math.ceil(strB.getWidth()), (int) Math.ceil(strB.getHeight()));
    }
}
