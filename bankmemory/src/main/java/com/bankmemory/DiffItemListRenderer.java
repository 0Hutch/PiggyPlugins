package com.bankmemory;

import com.bankmemory.bankview.ItemListEntry;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.font.LineMetrics;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import net.runelite.client.ui.FontManager;

/**
 * Special renderer needed for reasons:
 * - The icons returned by the item manager won't paint on "-1"s to the icons for some reason. It will do positive 1s
 *   and other negative numbers, but not -1.
 * - Actually paint '+' symbol with positive numbers.
 * - Green/red colouring of numbers!
 * - Display numbers left of item icon so a mix of icon colour/text colour isn't unreadable, and reduce overlapping.
 *   The UI doesn't give us much width though, so we're fine with some overlapping on long numbers.
 */
public class DiffItemListRenderer extends JLabel implements ListCellRenderer<ItemListEntry> {

    @Override
    public Component getListCellRendererComponent(JList<? extends ItemListEntry> list,
                                                  ItemListEntry value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        setText(value.getItemName());
        setIcon(new IconWithQuantity(value.getImage(), value.getQuantity()));
        return this;
    }

    private static class IconWithQuantity extends ImageIcon {
        // A roughly worked out gap that seems fine
        private static final int LEFT_PAD = (int) (net.runelite.api.Constants.ITEM_SPRITE_WIDTH * 1.2);

        private final int quantity;

        public IconWithQuantity(Image image, int quantity) {
            super(image);
            this.quantity = quantity;
        }

        @Override
        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            super.paintIcon(c, g, x + LEFT_PAD, y);

            String displayNum;
            Color numColour;
            if (quantity < 0) {
                numColour = Color.RED;
                displayNum = Integer.toString(quantity);
            } else {
                numColour = Color.GREEN;
                displayNum = "+" + quantity;
            }

            Graphics g2 = g.create();
            Font font = FontManager.getRunescapeFont();
            g2.setFont(font);
            LineMetrics lm = font.getLineMetrics(displayNum, g2.getFontMetrics().getFontRenderContext());
            int height = (int) Math.ceil(lm.getHeight());
            g2.setColor(Color.BLACK);
            g2.drawString(displayNum, 1, height + 1);
            g2.setColor(numColour);
            g2.drawString(displayNum, 0, height);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return super.getIconWidth() + LEFT_PAD;
        }
    }
}