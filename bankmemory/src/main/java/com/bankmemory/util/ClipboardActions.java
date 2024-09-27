package com.bankmemory.util;

import com.bankmemory.data.BankItem;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Objects;
import javax.swing.SwingUtilities;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

public class ClipboardActions {
    private ClipboardActions() {}

    public static void copyItemDataAsTsvToClipboardOnClientThread(ClientThread clientThread, ItemManager itemManager, List<BankItem> itemData) {
        Objects.requireNonNull(clientThread);
        Objects.requireNonNull(itemManager);
        Objects.requireNonNull(itemData);
        assert SwingUtilities.isEventDispatchThread();

        clientThread.invokeLater(() -> {
            StringBuilder sb = new StringBuilder();

            sb.append("Item id\tItem name\tItem quantity").append(System.lineSeparator());

            itemData.forEach(i -> sb
                    .append(i.getItemId()).append('\t')
                    .append(itemManager.getItemComposition(i.getItemId()).getName()).append('\t')
                    .append(i.getQuantity()).append(System.lineSeparator()));

            StringSelection stringSelection = new StringSelection(sb.toString());

            // Bad to pass in the StringSelection as the ClipboardOwner?
            // Idk, maybe! But its implementation of that interface is NOOP and that's good with me
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
        });
    }
}
