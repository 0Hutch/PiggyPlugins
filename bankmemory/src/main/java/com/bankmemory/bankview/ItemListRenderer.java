package com.bankmemory.bankview;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class ItemListRenderer extends JLabel implements ListCellRenderer<ItemListEntry> {
    @Override
    public Component getListCellRendererComponent(JList<? extends ItemListEntry> list,
                                                  ItemListEntry value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        setText(value.getItemName());
        setIcon(new ImageIcon(value.getImage()));
        return this;
    }
}
