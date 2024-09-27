package com.bankmemory;

import com.bankmemory.data.PluginDataStore;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankMemoryPluginTest {
    @Mock @Bind private ClientToolbar clientToolbar;
    @Mock @Bind private Client client;
    @Mock @Bind private ClientThread clientThread;
    @Mock @Bind private ItemManager itemManager;
    @Mock @Bind private PluginDataStore pluginDataStore;
    @Mock @Bind private BankMemoryConfig bankMemoryConfig;
    @Mock @Bind private OverlayManager overlayManager;
    @Mock private CurrentBankPanelController currentBankPanelController;
    @Mock private SavedBanksPanelController savedBanksPanelController;
    @Mock private BankDiffPanelController bankDiffPanelController;
    @Mock private BankMemoryPluginPanel pluginPanel;
    @Mock private Injector pluginInjector;

    @Inject private TestBankMemoryPlugin bankMemoryPlugin;

    @Before
    public void before() {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);
        bankMemoryPlugin.setInjector(pluginInjector);
        when(pluginInjector.getInstance(BankMemoryPluginPanel.class)).thenReturn(pluginPanel);
        when(pluginInjector.getInstance(CurrentBankPanelController.class)).thenReturn(currentBankPanelController);
        when(pluginInjector.getInstance(SavedBanksPanelController.class)).thenReturn(savedBanksPanelController);
        when(pluginInjector.getInstance(BankDiffPanelController.class)).thenReturn(bankDiffPanelController);
        when(pluginPanel.getSavedBanksTopPanel()).thenReturn(mock(BankSavesTopPanel.class));
    }

    @Test
    public void testStartup_startsCurrentBankControllerOnClientThread() throws Exception {
        ArgumentCaptor<Runnable> ac = ArgumentCaptor.forClass(Runnable.class);

        SwingUtilities.invokeAndWait(noCatch(bankMemoryPlugin::startUp));

        verify(clientThread).invokeLater(ac.capture());
        verify(currentBankPanelController, never()).startUp(any());
        ac.getValue().run();
        verify(currentBankPanelController).startUp(pluginPanel.getCurrentBankViewPanel());
    }

    private static Runnable noCatch(ThrowingRunnable throwingRunnable) {
        return () -> {
            try {
                throwingRunnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static class TestBankMemoryPlugin extends BankMemoryPlugin {
        void setInjector(Injector injector) {
            this.injector = injector;
        }
    }

    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}