package com.bankmemory;

import com.bankmemory.bankview.BankViewPanel;
import com.bankmemory.bankview.ItemListEntry;
import com.bankmemory.data.AbstractDataStoreUpdateListener;
import com.bankmemory.data.AccountIdentifier;
import com.bankmemory.data.BankItem;
import com.bankmemory.data.BankSave;
import com.bankmemory.data.BankWorldType;
import com.bankmemory.data.PluginDataStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;

@Slf4j
public class CurrentBankPanelController {
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private ItemManager itemManager;
    @Inject private PluginDataStore dataStore;

    private BankViewPanel panel;

    @Nullable private BankSave latestDisplayedData = null;

    public void startUp(BankViewPanel panel) {
        assert client.isClientThread();

        this.panel = panel;
        SwingUtilities.invokeLater(this::setPopupMenuActionOnBankView);

        DataStoreListener dataStoreListener = new DataStoreListener();
        dataStore.addListener(dataStoreListener);

        if (client.getGameState() == GameState.LOGGED_IN) {
            updateDisplayForCurrentAccount();
        } else {
            SwingUtilities.invokeLater(panel::displayNoDataMessage);
        }
    }

    private void setPopupMenuActionOnBankView() {
        this.panel.setItemListPopupMenuAction(new CopyItemsToClipboardAction(clientThread, itemManager) {
            @Nullable
            @Override
            public BankSave getBankItemData() {
                if (latestDisplayedData == null) {
                    log.error("Tried to copy CSV data to clipboard before any current bank shown");
                }
                return latestDisplayedData;
            }
        });
    }

    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        assert client.isClientThread();

        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        updateDisplayForCurrentAccount();
    }

    private void updateDisplayForCurrentAccount() {
        BankWorldType worldType = BankWorldType.forWorld(client.getWorldType());
        String accountIdentifier = AccountIdentifier.fromAccountHash(client.getAccountHash());
        Optional<BankSave> existingSave = dataStore.getDataForCurrentBank(worldType, accountIdentifier);
        if (existingSave.isPresent()) {
            viewBankSave(existingSave.get());
        } else {
            latestDisplayedData = null;
            SwingUtilities.invokeLater(panel::displayNoDataMessage);
        }
    }

    private void viewBankSave(BankSave bankSave) {
        assert client.isClientThread();

        dataStore.currentBankViewed(bankSave.getId());

        boolean shouldReset = isBankIdentityDifferentToLastDisplayed(bankSave);
        boolean shouldUpdateItemsDisplay = shouldReset || isItemDataNew(bankSave);
        List<ItemListEntry> items = new ArrayList<>();
        if (shouldUpdateItemsDisplay) {
            // Get all the data we need for the UI on this thread (the game thread)
            // Doing it on the EDT seems to cause random crashes & NPEs
            for (BankItem i : bankSave.getItemData()) {
                ItemComposition ic = itemManager.getItemComposition(i.getItemId());
                AsyncBufferedImage icon = itemManager.getImage(i.getItemId(), i.getQuantity(), i.getQuantity() > 1);
                int geValue = itemManager.getItemPrice(i.getItemId()) * i.getQuantity();
                int haValue = ic.getHaPrice() * i.getQuantity();
                items.add(new ItemListEntry(ic.getName(), i.getQuantity(), icon, geValue, haValue));
            }
        }
        SwingUtilities.invokeLater(() -> {
            if (shouldReset) {
                panel.reset();
            }
            panel.updateTimeDisplay(bankSave.getDateTimeString());
            if (shouldUpdateItemsDisplay) {
                panel.displayItemListings(items, true);
            }
        });
        latestDisplayedData = bankSave;
    }

    private boolean isBankIdentityDifferentToLastDisplayed(BankSave newSave) {
        if (latestDisplayedData == null) {
            return true;
        }
        boolean accountIdentifiersSame = latestDisplayedData.getAccountIdentifier().equalsIgnoreCase(newSave.getAccountIdentifier());
        boolean worldTypesSame = latestDisplayedData.getWorldType() == newSave.getWorldType();
        return !accountIdentifiersSame || !worldTypesSame;
    }

    private boolean isItemDataNew(BankSave newSave) {
        return latestDisplayedData == null || !latestDisplayedData.getItemData().equals(newSave.getItemData());
    }

    private class DataStoreListener extends AbstractDataStoreUpdateListener {
        @Override
        public void currentBanksListChanged() {
            updateDisplayForCurrentAccount();
        }
    }
}
