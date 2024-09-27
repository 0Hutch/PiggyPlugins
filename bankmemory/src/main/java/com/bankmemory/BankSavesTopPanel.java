package com.bankmemory;

import com.bankmemory.bankview.BankViewPanel;
import com.bankmemory.bankview.ItemListEntry;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

public class BankSavesTopPanel extends JPanel {
    static {
        BufferedImage backIcon = ImageUtil.getResourceStreamFromClass(BankSavesTopPanel.class, "back_icon.png");
        BACK_ICON = new ImageIcon(backIcon);
        BACK_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(backIcon, -100));
    }

    private static final Icon BACK_ICON;
    private static final Icon BACK_ICON_HOVER;

    private final BanksListPanel banksListPanel = new BanksListPanel();
    private final BankViewPanel bankViewPanel = new BankViewPanel();
    private final BankDiffPanel bankDiffPanel = new BankDiffPanel();
    private final JPanel backButtonAndTitle = new JPanel();
    private final JLabel subUiTitle = new JLabel();

    public BankSavesTopPanel() {
        super();
        setLayout(new BorderLayout());

        backButtonAndTitle.setLayout(new BoxLayout(backButtonAndTitle, BoxLayout.LINE_AXIS));
        JButton backButton = new JButton(BACK_ICON);
        SwingUtil.removeButtonDecorations(backButton);
        backButton.setRolloverIcon(BACK_ICON_HOVER);
        backButton.addActionListener(e -> displayBanksListPanel());
        backButtonAndTitle.add(backButton);
        backButtonAndTitle.add(subUiTitle);
    }

    void setBanksListInteractionListener(BanksListInteractionListener listener) {
        banksListPanel.setInteractionListener(listener);
    }

    void displayBanksListPanel() {
        bankViewPanel.reset();
        removeAll();
        add(banksListPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    void updateBanksList(List<BanksListEntry> entries) {
        banksListPanel.updateBanksList(entries);
    }

    void displaySavedBankData(String saveName, List<ItemListEntry> items, String timeString) {
        removeAll();
        subUiTitle.setText(saveName);
        add(backButtonAndTitle, BorderLayout.NORTH);
        add(bankViewPanel, BorderLayout.CENTER);
        bankViewPanel.updateTimeDisplay(timeString);
        bankViewPanel.displayItemListings(items, false);
        revalidate();
        repaint();
    }

    public BankViewPanel getBankViewPanel() {
        return bankViewPanel;
    }

    public BankDiffPanel getDiffPanel() {
        return bankDiffPanel;
    }

    void showBankDiffPanel() {
        removeAll();
        subUiTitle.setText("Bank comparison");
        add(backButtonAndTitle, BorderLayout.NORTH);
        add(bankDiffPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
