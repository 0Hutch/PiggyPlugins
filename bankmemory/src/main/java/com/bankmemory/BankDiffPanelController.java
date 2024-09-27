package com.bankmemory;

import com.bankmemory.BankDiffListOption.Type;
import com.bankmemory.bankview.ItemListEntry;
import com.bankmemory.data.AbstractDataStoreUpdateListener;
import com.bankmemory.data.BankItem;
import com.bankmemory.data.BankSave;
import com.bankmemory.data.DataStoreUpdateListener;
import com.bankmemory.data.DisplayNameMapper;
import com.bankmemory.data.PluginDataStore;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;

@Slf4j
public class BankDiffPanelController {

    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private ItemManager itemManager;
    @Inject private PluginDataStore dataStore;
    @Inject private ItemListDiffGenerator diffGenerator;

    private BankDiffPanel diffPanel;
    private DataUpdateListener dataListener;
    private BankDiffListOption lastBeforeSelection;
    private BankDiffListOption lastAfterSelection;

    public void startUp(BankDiffPanel diffPanel) {
        this.diffPanel = diffPanel;
        dataListener = new DataUpdateListener();
        diffPanel.setInteractionListener(this::userSelectedBankSaves);
        diffPanel.addHierarchyListener(e -> diffPanel.resetSelectionsAndItemList());
        dataStore.addListener(dataListener);
        updateForLatestBankData(true);
    }

    private void updateForLatestBankData(boolean currentBanksChanged) {
        assert SwingUtilities.isEventDispatchThread();

        List<BankDiffListOption> currentBanks = new ArrayList<>();
        List<BankDiffListOption> snapshotBanks = new ArrayList<>();
        DisplayNameMapper nameMapper = dataStore.getDisplayNameMapper();

        for (BankSave save : dataStore.getCurrentBanksList()) {
            String displayName = nameMapper.map(save.getAccountIdentifier());
            currentBanks.add(new BankDiffListOption(displayName, Type.CURRENT, save));
        }
        for (BankSave save : dataStore.getSnapshotBanksList()) {
            snapshotBanks.add(new BankDiffListOption(save.getSaveName(), Type.SNAPSHOT, save));
        }

        // Always need to update the options list as even if the banks are the same then that means it's a display name change
        diffPanel.displayBankOptions(currentBanks, snapshotBanks);

        BankDiffListOption equivalentBefore = findEquivalent(lastBeforeSelection, currentBanks, snapshotBanks);
        BankDiffListOption equivalentAfter = findEquivalent(lastAfterSelection, currentBanks, snapshotBanks);
        if (equivalentBefore != null && equivalentAfter != null) {
            diffPanel.setSelections(equivalentBefore, equivalentAfter);

            // NB: should only need to redo diff if current banks list change, not for snapshot banks list changing
            // (since if a snapshot save can be found again by its ID then it hasn't changed)
            if (currentBanksChanged && (
                    equivalentBefore.getBankType() == Type.CURRENT || equivalentAfter.getBankType() == Type.CURRENT)) {
                displayDiffOfSaves(equivalentBefore, equivalentAfter, true);
            }
        }
    }

    private BankDiffListOption findEquivalent(
            BankDiffListOption old,
            List<BankDiffListOption> currentBanks,
            List<BankDiffListOption> snapshots) {
        if (old == null) {
            return null;
        }
        switch (old.getBankType()) {
            case CURRENT:
                return currentBanks.stream()
                        .filter(b -> old.getSave().getAccountIdentifier().equalsIgnoreCase(b.getSave().getAccountIdentifier()))
                        .findAny().orElse(null);
            case SNAPSHOT:
                return snapshots.stream()
                        .filter(b -> old.getSave().getId() == b.getSave().getId())
                        .findAny().orElse(null);
        }
        throw new AssertionError();
    }

    private void userSelectedBankSaves(BankDiffListOption before, BankDiffListOption after) {
        assert SwingUtilities.isEventDispatchThread();
        lastBeforeSelection = before;
        lastAfterSelection = after;
        displayDiffOfSaves(before, after, false);
    }

    private void displayDiffOfSaves(BankDiffListOption before, BankDiffListOption after, boolean keepListPosition) {
        assert SwingUtilities.isEventDispatchThread();

        List<BankItem> differences = diffGenerator.findDifferencesBetween(
                before.getSave().getItemData(), after.getSave().getItemData());
        clientThread.invokeLater(() -> gatherItemDataToDisplay(differences, keepListPosition));
    }

    private void gatherItemDataToDisplay(List<BankItem> differences, boolean keepListPosition) {
        assert client.isClientThread();

        List<ItemListEntry> items = new ArrayList<>();

        for (BankItem i : differences) {
            ItemComposition ic = itemManager.getItemComposition(i.getItemId());
            // Quantity num is painted by renderer, but still give quantity so item stacks show nicely
            AsyncBufferedImage icon = itemManager.getImage(i.getItemId(), i.getQuantity(), false);
            int geValue = itemManager.getItemPrice(i.getItemId()) * i.getQuantity();
            int haValue = ic.getHaPrice() * i.getQuantity();
            items.add(new ItemListEntry(ic.getName(), i.getQuantity(), icon, geValue, haValue));
        }

        SwingUtilities.invokeLater(() -> diffPanel.displayItems(items, keepListPosition));
    }

    public void shutDown() {
        dataStore.removeListener(dataListener);
        lastBeforeSelection = null;
        lastAfterSelection = null;
    }

    private class DataUpdateListener extends AbstractDataStoreUpdateListener {
        @Override
        public void currentBanksListChanged() {
            SwingUtilities.invokeLater(() -> updateForLatestBankData(true));
        }

        @Override
        public void snapshotBanksListChanged() {
            SwingUtilities.invokeLater(() -> updateForLatestBankData(false));
        }

        @Override
        public void displayNameMapUpdated() {
            SwingUtilities.invokeLater(() -> updateForLatestBankData(false));
        }
    }
}
