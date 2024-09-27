package com.bankmemory.bankview;

import com.bankmemory.util.Constants;
import com.bankmemory.util.OnAnyChangeDocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.AsyncBufferedImage;


import static com.bankmemory.util.Constants.PAD;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class BankViewPanel extends JPanel {

    private enum DisplayState {
        RESET, SHOWING_NO_DATA, SHOWING_ITEM_LIST
    }

    private DisplayState state;

    private final JLabel syncTimeLabel;
    private final JPanel northPanel;
    private final IconTextField filterField;
    private final ValueDisplayPanel valueDisplay;
    private final ItemList itemsList;
    private final JScrollPane itemsScrollPane;
    private final PluginErrorPanel errorPanel;

    public BankViewPanel() {
        super(false);
        setLayout(new BorderLayout(0, PAD));
        setBorder(BorderFactory.createEmptyBorder(PAD, 0, PAD, 0));

        northPanel = new JPanel(new BorderLayout(0, PAD / 2));
        valueDisplay = new ValueDisplayPanel();
        northPanel.add(valueDisplay, BorderLayout.NORTH);
        filterField = new IconTextField();
        northPanel.add(filterField, BorderLayout.SOUTH);

        itemsList = new ItemList();
        itemsList.setCellRenderer(new ItemListRenderer());
        itemsScrollPane = new JScrollPane(itemsList);

        filterField.setIcon(IconTextField.Icon.SEARCH);
        filterField.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        filterField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        filterField.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        filterField.getDocument().addDocumentListener(new OnAnyChangeDocumentListener() {
            @Override
            public void onChange(DocumentEvent e) {
                itemsScrollPane.getViewport().setViewPosition(new Point(0, 0));
                itemsList.getModel().applyFilter(filterField.getText());
            }
        });

        syncTimeLabel = new JLabel();
        syncTimeLabel.setFont(FontManager.getRunescapeSmallFont());

        errorPanel = new PluginErrorPanel();
        errorPanel.setContent(Constants.BANK_MEMORY,
                "Log in to a character and open a bank window to populate bank data for that character.");
        displayNoDataMessage();
    }

    public void displayNoDataMessage() {
        checkState(SwingUtilities.isEventDispatchThread());
        reset();
        add(errorPanel, BorderLayout.NORTH);
        state = DisplayState.SHOWING_NO_DATA;
        repaint();
    }

    /**
     * Resets filter, resets list data, resets scroll position and removes UI components.
     * Indirectly: releases some held objects (by resetting the list).
     */
    public void reset() {
        checkState(SwingUtilities.isEventDispatchThread());
        itemsList.getModel().clearList();
        itemsList.getModel().clearFilter();
        filterField.setText("");
        resetScrolling();
        removeAll();
        state = DisplayState.RESET;
    }

    private void resetScrolling() {
        itemsScrollPane.getViewport().setViewPosition(new Point(0, 0));
    }

    public void updateTimeDisplay(String timeString) {
        checkState(SwingUtilities.isEventDispatchThread());
        syncTimeLabel.setText("Data from: " + timeString);
    }

    public void displayItemListings(List<ItemListEntry> items, boolean preserveScrollPos) {
        checkState(SwingUtilities.isEventDispatchThread());

        ensureDisplayIsInItemListState();
        Point scrollPosition = itemsScrollPane.getViewport().getViewPosition();

        long geValue = 0;
        long haValue = 0;
        for (int i = 0; i < items.size(); i++) {
            AsyncBufferedImage img = items.get(i).getImage();
            int unfilteredRow = i;
            img.onLoaded(() -> repaintItemEntryIfRowVisible(unfilteredRow));
            geValue += items.get(i).getGeValue();
            haValue += items.get(i).getHaValue();
        }
        valueDisplay.setValues(geValue, haValue);
        FilterableItemListModel listModel = itemsList.getModel();
        listModel.setListContents(items);

        if (preserveScrollPos) {
            itemsScrollPane.getViewport().setViewPosition(scrollPosition);
        } else {
            itemsScrollPane.getViewport().setViewPosition(new Point(0, 0));
        }
        validate();
        repaint();
    }

    private void ensureDisplayIsInItemListState() {
        if (state == DisplayState.SHOWING_ITEM_LIST) {
            return;
        }
        removeAll();
        add(northPanel, BorderLayout.NORTH);
        add(itemsScrollPane, BorderLayout.CENTER);
        add(syncTimeLabel, BorderLayout.SOUTH);
        state = DisplayState.SHOWING_ITEM_LIST;
    }

    private void repaintItemEntryIfRowVisible(int unfilteredIndex) {
        int adjustedIndex = itemsList.getModel().getAdjustedIndex(unfilteredIndex);
        if (adjustedIndex < 0) {
            // Filtered out!
            return;
        }
        if (itemsList.getFirstVisibleIndex() <= unfilteredIndex && unfilteredIndex <= itemsList.getLastVisibleIndex()) {
            itemsList.repaint(itemsList.getCellBounds(adjustedIndex, adjustedIndex));
        }
    }

    public void setItemsListRenderer(ListCellRenderer<ItemListEntry> renderer) {
        itemsList.setCellRenderer(renderer);
    }

    public void setStyliseTotalValuesForDiffs(boolean show) {
        valueDisplay.setStylisedForDiffs(show);
    }

    public void setItemListPopupMenuAction(Action action) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(action);
        itemsList.setComponentPopupMenu(menu);
    }

    private static class ItemList extends JList<ItemListEntry> {
        ItemList() {
            super(new FilterableItemListModel());
        }

        @Override
        public void setModel(ListModel<ItemListEntry> model) {
            checkNotNull(model);
            checkArgument(model instanceof FilterableItemListModel, "Incorrect class: " + model.getClass());
            super.setModel(model);
        }

        @Override
        public FilterableItemListModel getModel() {
            return (FilterableItemListModel) super.getModel();
        }
    }
}
