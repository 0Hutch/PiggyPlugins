package com.bankmemory.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PluginDataStoreTest {

    @Mock @Bind private ConfigReaderWriter configReaderWriter;

    @Mock private DataStoreUpdateListener listener;

    @Captor ArgumentCaptor<List<BankSave>> bankSaveListCaptor;

    @Before
    public void before() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        when(configReaderWriter.readCurrentBanks()).thenReturn(new ArrayList<>());
        when(configReaderWriter.readBankSnapshots()).thenReturn(new ArrayList<>());
        when(configReaderWriter.readNameMap()).thenReturn(new HashMap<>());
    }

    @Test
    public void testRegisterDisplayNameForAccountId_ifAccountIdIsNew_savesNewNameMapAndCallsListeners() {
        String accountId = AccountIdentifier.ACCOUNT_HASH_ID_PREFIX + "123";
        PluginDataStore pluginDataStore = createPluginDataStore();

        pluginDataStore.registerDisplayNameForAccountId(accountId, "CoolUsername");

        verify(configReaderWriter).writeNameMap(Map.of(accountId, "CoolUsername"));
        verify(listener).displayNameMapUpdated();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testRegisterDisplayNameForAccountId_ifUsernameRegisteredForAccountIdAlready_doesNothing() {
        String accountId = AccountIdentifier.ACCOUNT_HASH_ID_PREFIX + "123";
        when(configReaderWriter.readNameMap()).thenReturn(Map.of(accountId, "CoolUsername"));
        PluginDataStore pluginDataStore = createPluginDataStore();

        pluginDataStore.registerDisplayNameForAccountId(accountId, "CoolUsername");

        verify(configReaderWriter, never()).writeNameMap(any());
        verifyNoInteractions(listener);
    }

    @Test
    public void testRegisterDisplayNameForAccountId_ifGivenUsernameIsDifferentToRegisteredOne_savesNewNameMapAndCallsListeners() {
        String accountId = AccountIdentifier.ACCOUNT_HASH_ID_PREFIX + "123";
        when(configReaderWriter.readNameMap()).thenReturn(Map.of(accountId, "OldUsername"));
        PluginDataStore pluginDataStore = createPluginDataStore();

        pluginDataStore.registerDisplayNameForAccountId(accountId, "NewUsername");

        verify(configReaderWriter).writeNameMap(Map.of(accountId, "NewUsername"));
        verify(listener).displayNameMapUpdated();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testRegisterDisplayNameForLogin_ifRegisteringUsernameWithNewAccountIdentifierWhenOldStyleIdentifierIsRegistered_updateNameMapAndBankSavesAndCallsListeners() {
        String oldStyleAccountId = "mylogin@whatever.com";
        String newStyleAccountId = AccountIdentifier.fromAccountHash(123);
        String sameUsername = "SameUsername";
        String anotherOldStyleAccountId = "another.old.id@x.com";
        BankSave existingSave1 = new BankSave(BankWorldType.DEFAULT, oldStyleAccountId, null, "111", ImmutableList.of());
        BankSave existingSave2 = new BankSave(BankWorldType.DEFAULT, anotherOldStyleAccountId, null, "222", ImmutableList.of());
        when(configReaderWriter.readNameMap()).thenReturn(Map.of(oldStyleAccountId, "SameUsername", anotherOldStyleAccountId, "DifferentUsername"));
        when(configReaderWriter.readCurrentBanks()).thenReturn(list(existingSave1, existingSave2));
        PluginDataStore pluginDataStore = createPluginDataStore();

        pluginDataStore.registerDisplayNameForAccountId(newStyleAccountId, sameUsername);

        BankSave expectedModifiedSave = BankSave.withNewAccountId(newStyleAccountId, existingSave1);
        verify(configReaderWriter).writeNameMap(Map.of(oldStyleAccountId, sameUsername, anotherOldStyleAccountId, "DifferentUsername", newStyleAccountId, sameUsername));
        verify(configReaderWriter).writeCurrentBanks(bankSaveListCaptor.capture());
        List<BankSave> savedCurrentBankList = bankSaveListCaptor.getValue();
        assertThat(savedCurrentBankList.size(), is(2));
        assertThatBankSavesAreEqualExceptForSaveId(savedCurrentBankList.get(0), expectedModifiedSave);
        assertThat(savedCurrentBankList.get(1), is(existingSave2));

        verify(listener).displayNameMapUpdated();
        verify(listener).currentBanksListChanged();
        verify(listener).snapshotBanksListChanged();
    }

    @Test
    public void testRegisterDisplayNameForLogin_ifExistingSavesAreForExistingNewStyleAccountId_doesNotChangeSaves() {
        String newStyleAccountId = AccountIdentifier.fromAccountHash(123);
        String sameUsername = "SameUsername";
        BankSave existingSave1 = new BankSave(BankWorldType.DEFAULT, newStyleAccountId, null, "111", ImmutableList.of());
        BankSave existingSave2 = new BankSave(BankWorldType.DEFAULT, newStyleAccountId, null, "222", ImmutableList.of());
        when(configReaderWriter.readNameMap()).thenReturn(Map.of(newStyleAccountId, "SameUsername"));
        when(configReaderWriter.readCurrentBanks()).thenReturn(list(existingSave1, existingSave2));
        PluginDataStore pluginDataStore = createPluginDataStore();

        pluginDataStore.registerDisplayNameForAccountId(newStyleAccountId, sameUsername);

        verify(configReaderWriter, never()).writeNameMap(any());
        verify(configReaderWriter, never()).writeCurrentBanks(any());
        verify(configReaderWriter, never()).writeBankSnapshots(any());
        verifyNoInteractions(listener);
    }

    private PluginDataStore createPluginDataStore() {
        PluginDataStore pluginDataStore = new PluginDataStore(configReaderWriter);
        pluginDataStore.addListener(listener);
        return pluginDataStore;
    }

    private static List<BankSave> list(BankSave... bankSaves) {
        return Lists.newArrayList(bankSaves);
    }

    private static void assertThatBankSavesAreEqualExceptForSaveId(BankSave given, BankSave expected) {
        assertThat(given.getAccountIdentifier(), is(expected.getAccountIdentifier()));
        assertThat(given.getSaveName(), is(expected.getSaveName()));
        assertThat(given.getItemData(), is(expected.getItemData()));
        assertThat(given.getDateTimeString(), is(expected.getDateTimeString()));
        assertThat(given.getWorldType(), is(expected.getWorldType()));
    }
}