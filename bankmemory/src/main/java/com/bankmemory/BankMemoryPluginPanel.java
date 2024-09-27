package com.bankmemory;

import com.bankmemory.bankview.BankViewPanel;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;


import static com.bankmemory.util.Constants.PAD;

class BankMemoryPluginPanel extends PluginPanel {

    private final BankViewPanel currentBankViewPanel = new BankViewPanel();
    private final BankSavesTopPanel savedBanksTopPanel = new BankSavesTopPanel();

    protected BankMemoryPluginPanel() {
        super(false);
        setBorder(BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD));

        JPanel displayPanel = new JPanel();
        MaterialTabGroup tabGroup = new MaterialTabGroup(displayPanel);
        MaterialTab currentBankTab = new MaterialTab("Current bank", tabGroup, currentBankViewPanel);
        MaterialTab savesListTab = new MaterialTab("Saved banks", tabGroup, savedBanksTopPanel);
        tabGroup.addTab(currentBankTab);
        tabGroup.addTab(savesListTab);
        tabGroup.select(currentBankTab);

        setLayout(new BorderLayout());
        add(tabGroup, BorderLayout.NORTH);
        add(displayPanel, BorderLayout.CENTER);
    }

    BankViewPanel getCurrentBankViewPanel() {
        return currentBankViewPanel;
    }

    BankSavesTopPanel getSavedBanksTopPanel() {
        return savedBanksTopPanel;
    }
}
