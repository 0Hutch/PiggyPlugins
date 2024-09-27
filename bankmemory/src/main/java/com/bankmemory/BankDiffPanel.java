package com.bankmemory;

import com.bankmemory.bankview.BankViewPanel;
import com.bankmemory.bankview.ItemListEntry;
import com.bankmemory.data.BankWorldType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.TitleCaseListCellRenderer;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

public class BankDiffPanel extends JPanel {
    static {
        BufferedImage backIcon = ImageUtil.getResourceStreamFromClass(BankDiffPanel.class, "reverse_icon.png");
        REVERSE_ICON = new ImageIcon(backIcon);
        REVERSE_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(backIcon, -100));
    }

    private static final Icon REVERSE_ICON;
    private static final Icon REVERSE_ICON_HOVER;
    private static final String CURRENT_BANKS = "- Current banks -";
    private static final String SNAPSHOT_BANKS = "- Snapshots -";

    private final BankViewPanel itemsList = new BankViewPanel();
    private final OptionsListModel beforeOptionsModel = new OptionsListModel();
    private final OptionsListModel afterOptionsModel = new OptionsListModel();
    private BankDiffPanelInteractionListener interactionListener;
    private boolean disableSelectionListener = false;

    BankDiffPanel() {
        super();
        setLayout(new BorderLayout());
        add(itemsList, BorderLayout.CENTER);
        itemsList.setItemsListRenderer(new DiffItemListRenderer());
        itemsList.setStyliseTotalValuesForDiffs(true);
        itemsList.reset();

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        topPanel.add(new JLabel("Before"), c);
        c.gridy = 1;
        topPanel.add(new JLabel("After"), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 1;
        c.gridy = 0;
        topPanel.add(createComboBox(beforeOptionsModel), c);
        c.gridy = 1;
        topPanel.add(createComboBox(afterOptionsModel), c);
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        JButton reverseButton = new JButton(REVERSE_ICON);
        reverseButton.setRolloverIcon(REVERSE_ICON_HOVER);
        SwingUtil.removeButtonDecorations(reverseButton);
        reverseButton.addActionListener(a -> reverseBankChoices());
        topPanel.add(reverseButton, c);

        add(topPanel, BorderLayout.NORTH);
    }

    private JComboBox<Object> createComboBox(OptionsListModel listModel) {
        JComboBox<Object> comboBox = new JComboBox<>(listModel);
        comboBox.setRenderer(new OptionsListRenderer());
        comboBox.addActionListener(a -> comboBoxSelectionChanged());
        return comboBox;
    }

    void setInteractionListener(BankDiffPanelInteractionListener listener) {
        interactionListener = listener;
    }

    private void comboBoxSelectionChanged() {
        if (disableSelectionListener) {
            return;
        }
        BankDiffListOption before = beforeOptionsModel.getSelectedItem();
        BankDiffListOption after = afterOptionsModel.getSelectedItem();
        if (before != null && after != null) {
            interactionListener.userSelectedSavesToDiff(before, after);
        }
    }

    private void reverseBankChoices() {
        disableSelectionListener = true;
        BankDiffListOption swap = beforeOptionsModel.getSelectedItem();
        beforeOptionsModel.setSelectedItem(afterOptionsModel.getSelectedItem());
        afterOptionsModel.setSelectedItem(swap);
        disableSelectionListener = false;
        comboBoxSelectionChanged();
    }

    void displayBankOptions(List<BankDiffListOption> currentBanks, List<BankDiffListOption> snapshotBanks) {
        List<Object> options = new ArrayList<>();
        if (!currentBanks.isEmpty()) {
            options.add(CURRENT_BANKS);
            options.addAll(listWithWorldSeparators(currentBanks));
        }
        if (!snapshotBanks.isEmpty()) {
            options.add(SNAPSHOT_BANKS);
            options.addAll(listWithWorldSeparators(snapshotBanks));
        }
        disableSelectionListener = true;
        beforeOptionsModel.removeAllElements();
        afterOptionsModel.removeAllElements();
        options.forEach(o -> {
            beforeOptionsModel.addElement(o);
            afterOptionsModel.addElement(o);
        });
        disableSelectionListener = false;
    }

    private List<Object> listWithWorldSeparators(List<BankDiffListOption> options) {
        ListMultimap<BankWorldType, BankDiffListOption> optionsByWorldType = ArrayListMultimap.create();
        options.forEach(o -> optionsByWorldType.put(o.getSave().getWorldType(), o));
        List<Object> resultList = new ArrayList<>();
        for (BankWorldType type : BankWorldType.values()) {
            if (optionsByWorldType.containsKey(type)) {
                if (type != BankWorldType.DEFAULT) {
                    resultList.add(type.getDisplayString());
                }
                resultList.addAll(optionsByWorldType.get(type));
            }
        }
        return resultList;
    }

    void setSelections(BankDiffListOption before, BankDiffListOption after) {
        disableSelectionListener = true;
        beforeOptionsModel.setSelectedItem(before);
        afterOptionsModel.setSelectedItem(after);
        disableSelectionListener = false;
    }

    void displayItems(List<ItemListEntry> items, boolean keepListPosition) {
        itemsList.displayItemListings(items, keepListPosition);
    }

    public void resetSelectionsAndItemList() {
        beforeOptionsModel.setSelectedItem(null);
        afterOptionsModel.setSelectedItem(null);
        itemsList.reset();
        revalidate();
        repaint();
    }

    private static class OptionsListModel extends DefaultComboBoxModel<Object> {
        @Override
        public void setSelectedItem(Object anObject) {
            if (anObject instanceof BankDiffListOption || anObject == null) {
                super.setSelectedItem(anObject);
            }
        }

        @Override
        public BankDiffListOption getSelectedItem() {
            Object selected = super.getSelectedItem();
            return selected instanceof BankDiffListOption ? (BankDiffListOption) selected : null;
        }
    }

    private static class OptionsListRenderer implements ListCellRenderer<Object> {
        private final TitleCaseListCellRenderer wrapped = new TitleCaseListCellRenderer();

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            Object valToRender = value == null ? "" : value; // else ComboBoxListRenderer breaks
            if (value instanceof BankDiffListOption) {
                valToRender = ((BankDiffListOption) value).getListText();
            }

            Component comp = wrapped.getListCellRendererComponent(list, valToRender, index, isSelected, cellHasFocus);

            Font font = FontManager.getRunescapeFont();
            Color fgColour = comp.getForeground();
            if (!(value instanceof BankDiffListOption)) {
                if (value == CURRENT_BANKS || value == SNAPSHOT_BANKS) {
                    font = FontManager.getRunescapeBoldFont();
                } else {
                    font = FontManager.getRunescapeSmallFont().deriveFont(Font.ITALIC);
                }
                fgColour = ColorScheme.BRAND_ORANGE;
            }
            comp.setFont(font);
            comp.setForeground(fgColour);
            return comp;
        }
    }
}
