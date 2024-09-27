package com.bankmemory;

import com.bankmemory.bankview.ItemListEntry;
import com.bankmemory.data.BankItem;
import com.bankmemory.data.BankSave;
import com.bankmemory.data.DataStoreUpdateListener;
import com.bankmemory.data.DisplayNameMapper;
import com.bankmemory.data.PluginDataStore;
import com.bankmemory.util.ClipboardActions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;

@Slf4j
public class SavedBanksPanelController {

    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private ItemManager itemManager;
    @Inject private PluginDataStore dataStore;

    private BankSavesTopPanel topPanel;
    private ImageIcon casketIcon;
    private ImageIcon notedCasketIcon;
    private final AtomicBoolean workingToOpenBank = new AtomicBoolean();
    private DataStoreListener dataStoreListener;
    @Nullable BankSave bankForClipboardAction;

    public void startUp(BankSavesTopPanel topPanel) {
        assert SwingUtilities.isEventDispatchThread();

        this.topPanel = topPanel;
        topPanel.setBanksListInteractionListener(new BanksListInteractionListenerImpl());
        casketIcon = new ImageIcon(itemManager.getImage(405));
        notedCasketIcon = new ImageIcon(itemManager.getImage(406));

        topPanel.displayBanksListPanel();
        updateCurrentBanksList();

        setPopupMenuActionOnBankView();

        dataStoreListener = new DataStoreListener();
        dataStore.addListener(dataStoreListener);
    }

    // Gets called on EDT and on game client thread
    private void updateCurrentBanksList() {
        List<BanksListEntry> saves = new ArrayList<>();
        DisplayNameMapper nameMapper = dataStore.getDisplayNameMapper();

        for (BankSave save : dataStore.getCurrentBanksList()) {
            String displayName = nameMapper.map(save.getAccountIdentifier());
            saves.add(new BanksListEntry(
                    save.getId(), casketIcon, save.getWorldType(), "Current bank", displayName, save.getDateTimeString()));
        }
        for (BankSave save : dataStore.getSnapshotBanksList()) {
            String displayName = nameMapper.map(save.getAccountIdentifier());
            saves.add(new BanksListEntry(
                    save.getId(), notedCasketIcon, save.getWorldType(), save.getSaveName(), displayName, save.getDateTimeString()));
        }

        Runnable updateList = () -> topPanel.updateBanksList(saves);
        if (SwingUtilities.isEventDispatchThread()) {
            updateList.run();
        } else {
            SwingUtilities.invokeLater(updateList);
        }
    }

    private void setPopupMenuActionOnBankView() {
        topPanel.getBankViewPanel().setItemListPopupMenuAction(new CopyItemsToClipboardAction(clientThread, itemManager) {
            @Nullable
            @Override
            public BankSave getBankItemData() {
                if (bankForClipboardAction == null) {
                    log.error("Tried to copy CSV data to clipboard before any bank save has been opened");
                }
                return bankForClipboardAction;
            }
        });
    }

    private void openSavedBank(BanksListEntry selected) {
        assert client.isClientThread();

        Optional<BankSave> save = dataStore.getBankSaveWithId(selected.getSaveId());
        if (!save.isPresent()) {
            log.error("Selected missing bank save: {}", selected);
            workingToOpenBank.set(false);
            return;
        }
        BankSave foundSave = save.get();

        List<ItemListEntry> items = new ArrayList<>();

        for (BankItem i : foundSave.getItemData()) {
            ItemComposition ic = itemManager.getItemComposition(i.getItemId());
            AsyncBufferedImage icon = itemManager.getImage(i.getItemId(), i.getQuantity(), i.getQuantity() > 1);
            int geValue = itemManager.getItemPrice(i.getItemId()) * i.getQuantity();
            int haValue = ic.getHaPrice() * i.getQuantity();
            items.add(new ItemListEntry(ic.getName(), i.getQuantity(), icon, geValue, haValue));
        }
        SwingUtilities.invokeLater(() -> {
            workingToOpenBank.set(false);
            bankForClipboardAction = foundSave;
            topPanel.displaySavedBankData(selected.getSaveName(), items, foundSave.getDateTimeString());
        });
    }

    public void shutDown() {
        dataStore.removeListener(dataStoreListener);
    }

    private class BanksListInteractionListenerImpl implements BanksListInteractionListener {
        @Override
        public void selectedToOpen(BanksListEntry save) {
            if (workingToOpenBank.get()) {
                return;
            }
            workingToOpenBank.set(true);
            clientThread.invokeLater(() -> openSavedBank(save));
        }

        @Override
        public void selectedToDelete(BanksListEntry save) {
            dataStore.deleteBankSaveWithId(save.getSaveId());
        }

        @Override
        public void saveBankAs(BanksListEntry save, String saveName) {
            Optional<BankSave> existingSave = dataStore.getBankSaveWithId(save.getSaveId());
            if (existingSave.isPresent()) {
                dataStore.saveAsSnapshotBank(saveName, existingSave.get());
            } else {
                log.error("Tried to 'Save As' missing bank save: {}", save);
            }
        }

        @Override
        public void copyBankSaveItemDataToClipboard(BanksListEntry save) {
            Optional<BankSave> existingSave = dataStore.getBankSaveWithId(save.getSaveId());
            if (existingSave.isPresent()) {
                ClipboardActions.copyItemDataAsTsvToClipboardOnClientThread(clientThread, itemManager, existingSave.get().getItemData());
            } else {
                log.error("Tried to copy CSV data to clipboard for missing bank save: {}", save);
            }
        }

        @Override
        public void openBanksDiffPanel() {
            topPanel.showBankDiffPanel();
        }
    }

    private class DataStoreListener implements DataStoreUpdateListener {
        @Override
        public void currentBanksListChanged() {
            updateCurrentBanksList();
        }

        @Override
        public void currentBanksListOrderChanged() {
            updateCurrentBanksList();
        }

        @Override
        public void snapshotBanksListChanged() {
            updateCurrentBanksList();
        }

        @Override
        public void displayNameMapUpdated() {
            updateCurrentBanksList();
        }
    }
}
