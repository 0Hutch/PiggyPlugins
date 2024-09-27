package com.bankmemory;

import com.bankmemory.bankview.BankViewPanel;
import com.bankmemory.bankview.ItemListEntry;
import com.bankmemory.data.AccountIdentifier;
import com.bankmemory.data.BankItem;
import com.bankmemory.data.BankSave;
import com.bankmemory.data.BankWorldType;
import com.bankmemory.data.DataStoreUpdateListener;
import com.bankmemory.data.PluginDataStore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.WorldType;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CurrentBankPanelControllerTest {
    @Mock @Bind private Client client;
    @Mock @Bind private ItemManager itemManager;
    @Mock @Bind private PluginDataStore dataStore;
    @Mock private BankViewPanel panel;

    @Inject private CurrentBankPanelController currentBankPanelController;

    @Captor private ArgumentCaptor<DataStoreUpdateListener> dataStoreUpdateListenerArgumentCaptor;

    @Mock private AsyncBufferedImage coinsIcon;
    @Mock private AsyncBufferedImage burntLobsterIcon;

    @Before
    public void before() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
        when(client.isClientThread()).thenReturn(true);
        when(client.getWorldType()).thenReturn(EnumSet.of(WorldType.MEMBERS));
        ItemComposition coins = mockItemComposition("Coins", 1);
        ItemComposition burntLobster = mockItemComposition("Burnt lobster", 10);
        when(itemManager.getItemComposition(0)).thenReturn(coins);
        when(itemManager.getItemComposition(2)).thenReturn(burntLobster);
        when(itemManager.getImage(eq(0), anyInt(), anyBoolean())).thenReturn(coinsIcon);
        when(itemManager.getImage(eq(2), anyInt(), anyBoolean())).thenReturn(burntLobsterIcon);
        when(itemManager.getItemPrice(0)).thenReturn(1);
        when(itemManager.getItemPrice(2)).thenReturn(100);
    }

    @Test
    public void testStartup_ifNotLoggedIn_displayNoData() throws Exception {
        when(client.getGameState()).thenReturn(GameState.LOGIN_SCREEN);

        currentBankPanelController.startUp(panel);

        waitForEdtQueueToEmpty();
        verify(panel).displayNoDataMessage();
    }

    @Test
    public void testStartup_ifLoggedInButNoDataForAccount_displayNoData() throws Exception {
        String newStyleAccountId = AccountIdentifier.fromAccountHash(1337);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getAccountHash()).thenReturn(1337L);
        when(dataStore.getDataForCurrentBank(BankWorldType.DEFAULT, newStyleAccountId)).thenReturn(Optional.empty());

        currentBankPanelController.startUp(panel);

        waitForEdtQueueToEmpty();
        verify(panel).displayNoDataMessage();
    }

    @Test
    public void testStartup_ifLoggedInAndDataAvailable_displayAccountData() throws Exception {
        String newStyleAccountId = AccountIdentifier.fromAccountHash(1337);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getAccountHash()).thenReturn(1337L);
        when(dataStore.getDataForCurrentBank(BankWorldType.DEFAULT, newStyleAccountId)).thenReturn(Optional.of(
                new BankSave(BankWorldType.DEFAULT, newStyleAccountId, "My Bank", "Tuesday",
                        ImmutableList.of(new BankItem(0, 100), new BankItem(2, 666)))));

        currentBankPanelController.startUp(panel);

        waitForEdtQueueToEmpty();
        verify(panel).updateTimeDisplay("Tuesday");
        verify(panel).displayItemListings(eq(list(
                new ItemListEntry("Coins", 100, coinsIcon, 100, 100),
                new ItemListEntry("Burnt lobster", 666, burntLobsterIcon, 66600, 6660))),
                eq(true));
    }

    @Test
    public void testDataStoreUpdateListener_whenCurrentBankListChanges_ifItemDataHasNotChangedThenOnlyUpdateTime() throws Exception {
        String newStyleAccountId = AccountIdentifier.fromAccountHash(1337);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getAccountHash()).thenReturn(1337L);
        BankSave mondaySave = new BankSave(BankWorldType.DEFAULT, newStyleAccountId, "My Bank", "Monday",
                ImmutableList.of(new BankItem(0, 100), new BankItem(2, 666)));
        BankSave tuesdaySave = new BankSave(BankWorldType.DEFAULT, newStyleAccountId, "My Bank", "Tuesday", mondaySave.getItemData());
        currentBankPanelController.startUp(panel);

        verify(panel, never()).updateTimeDisplay(any());
        verify(panel, never()).displayItemListings(any(), anyBoolean());
        verify(dataStore).addListener(dataStoreUpdateListenerArgumentCaptor.capture());

        DataStoreUpdateListener listener = dataStoreUpdateListenerArgumentCaptor.getValue();
        when(dataStore.getDataForCurrentBank(BankWorldType.DEFAULT, newStyleAccountId)).thenReturn(Optional.of(mondaySave));
        listener.currentBanksListChanged();

        waitForEdtQueueToEmpty();
        verify(panel).updateTimeDisplay("Monday");
        verify(panel, times(1)).displayItemListings(eq(list(
                new ItemListEntry("Coins", 100, coinsIcon, 100, 100),
                new ItemListEntry("Burnt lobster", 666, burntLobsterIcon, 66600, 6660))),
                eq(true));

        when(dataStore.getDataForCurrentBank(BankWorldType.DEFAULT, newStyleAccountId)).thenReturn(Optional.of(tuesdaySave));
        listener.currentBanksListChanged();

        waitForEdtQueueToEmpty();
        verify(panel).updateTimeDisplay("Tuesday");
        verify(panel, times(1)).displayItemListings(any(), anyBoolean());
    }

    private static ItemComposition mockItemComposition(String name, int haValue) {
        ItemComposition item = mock(ItemComposition.class);
        when(item.getName()).thenReturn(name);
        when(item.getHaPrice()).thenReturn(haValue);
        return item;
    }

    private static void waitForEdtQueueToEmpty() throws Exception {
        SwingUtilities.invokeAndWait(() -> { /* Do nothing */ });
    }

    private static List<ItemListEntry> list(ItemListEntry... items) {
        return Lists.newArrayList(items);
    }
}