package com.bankmemory;

import com.bankmemory.data.BankWorldType;
import com.bankmemory.util.Constants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.PluginErrorPanel;


public class BanksListPanel extends JPanel {

    private static final String DELETE_SAVE = "Delete save...";
    private static final String SAVE_SNAPSHOT = "Save snapshot...";

    private final PluginErrorPanel noDataMessage;
    private final JPanel listPanel;
    private final JPopupMenu bankEntryContextMenu;
    private final ListEntryMouseListener mouseListener;
    private BanksListInteractionListener interactionListener;

    public BanksListPanel() {
        super();
        mouseListener = new ListEntryMouseListener();
        bankEntryContextMenu = createContextMenu();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(Constants.PAD, 0, Constants.PAD, 0));

        noDataMessage = new PluginErrorPanel();
        noDataMessage.setContent("No bank saves", "You currently do not have any bank saves.");
        add(noDataMessage, BorderLayout.NORTH);

        listPanel = new JPanel(new GridBagLayout());
        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.add(listPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(listWrapper);
        add(scrollPane, BorderLayout.CENTER);

        JButton compareBanks = new JButton("Compare bank saves");
        compareBanks.addActionListener(a -> interactionListener.openBanksDiffPanel());
        add(compareBanks, BorderLayout.SOUTH);
    }

    private JPopupMenu createContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(createMenuSaveAsAction(menu));
        menu.add(createMenuCopyItemDataToClipboardAction(menu));
        menu.add(createMenuDeleteAction(menu));
        return menu;
    }

    private Action createMenuSaveAsAction(JPopupMenu menu) {
        return new AbstractAction(SAVE_SNAPSHOT) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputName;
                do {
                    inputName = JOptionPane.showInputDialog(
                            BanksListPanel.this, "Enter name for new bank snapshot:", "Save Snapshot As", JOptionPane.PLAIN_MESSAGE);
                    if (inputName == null) {
                        return;
                    }
                    inputName = inputName.trim();
                } while (inputName.isEmpty());

                BanksListEntry save = ((EntryPanel) menu.getInvoker()).entry;
                interactionListener.saveBankAs(save, inputName);
            }
        };
    }

    private Action createMenuCopyItemDataToClipboardAction(JPopupMenu menu) {
        return new AbstractAction(Constants.ACTION_COPY_ITEM_DATA_TO_CLIPBOARD) {
            @Override
            public void actionPerformed(ActionEvent e) {
                BanksListEntry save = ((EntryPanel) menu.getInvoker()).entry;
                interactionListener.copyBankSaveItemDataToClipboard(save);
            }
        };
    }

    private Action createMenuDeleteAction(JPopupMenu menu) {
        return new AbstractAction(DELETE_SAVE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = "Are you sure you want to delete this save?";
                int result = JOptionPane.showConfirmDialog(
                        BanksListPanel.this, message, Constants.BANK_MEMORY, JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    BanksListEntry save = ((EntryPanel) menu.getInvoker()).entry;
                    interactionListener.selectedToDelete(save);
                }
            }
        };
    }

    public void setInteractionListener(BanksListInteractionListener listener) {
        interactionListener = listener;
    }

    public void updateBanksList(List<BanksListEntry> entries) {
        listPanel.removeAll();

        noDataMessage.setVisible(entries.isEmpty());
        if (!entries.isEmpty()) {
            displayListOfBanks(entries);
        }

        revalidate();
        repaint();
    }

    private void displayListOfBanks(List<BanksListEntry> entries) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        for (BanksListEntry entry : entries) {
            JPanel entriesGapPad = new JPanel(new BorderLayout());
            entriesGapPad.setBorder(BorderFactory.createEmptyBorder(Constants.PAD / 2, 0, Constants.PAD / 2, 0));
            entriesGapPad.add(new EntryPanel(entry), BorderLayout.NORTH);
            listPanel.add(entriesGapPad, c);
            c.gridy++;
        }
    }

    private class EntryPanel extends JPanel {
        final BanksListEntry entry;

        public EntryPanel(BanksListEntry entry) {
            this.entry = entry;

            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(Constants.PAD, Constants.PAD, Constants.PAD, Constants.PAD));
            setBackground(ColorScheme.DARKER_GRAY_COLOR);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(entry.getDateTime());
            setComponentPopupMenu(bankEntryContextMenu);

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0;
            c.gridheight = 2;
            add(new JLabel(entry.getIcon()), c);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.weightx = 1;
            c.gridheight = 1;
            add(new JLabel(entry.getSaveName()), c);

            c.gridy = 1;
            String subText = entry.getAccountDisplayName();
            if (entry.getWorldType() != BankWorldType.DEFAULT) {
                subText += " (" + entry.getWorldType().getDisplayString() + ")";
            }
            JLabel subLabel = new JLabel(subText);
            subLabel.setFont(FontManager.getRunescapeSmallFont());
            add(subLabel, c);

            addMouseListener(mouseListener);
        }
    }

    private class ListEntryMouseListener extends MouseAdapter {
        private final Color normalBgColour = ColorScheme.DARKER_GRAY_COLOR;
        private final Color hoverBgColour = ColorScheme.DARKER_GRAY_HOVER_COLOR;

        @Override
        public void mouseEntered(MouseEvent e) {
            e.getComponent().setBackground(hoverBgColour);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            e.getComponent().setBackground(normalBgColour);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                BanksListEntry entryClicked = ((EntryPanel) e.getComponent()).entry;
                interactionListener.selectedToOpen(entryClicked);

                // mouseExited won't trigger if the interaction listener changes the view entirely,
                // so trigger manually if needed
                if (!e.getComponent().contains(MouseInfo.getPointerInfo().getLocation())) {
                    mouseExited(e);
                }
            }
        }
    }
}
