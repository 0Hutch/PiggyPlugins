package com.polyplugins.AutoWintertodt.API;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import java.util.List;
import java.util.Optional;
import net.runelite.api.widgets.Widget;

public class InventoryUtil {
    public InventoryUtil() {
    }

    public static boolean useItemNoCase(String name, String... actions) {
        return (Boolean)nameContainsNoCase(name).first().flatMap((item) -> {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, actions);
            return Optional.of(true);
        }).orElse(false);
    }

    public static ItemQuery nameContainsNoCase(String name) {
        return Inventory.search().filter((widget) -> {
            return widget.getName().toLowerCase().contains(name.toLowerCase());
        });
    }

    public static Optional<Widget> getById(int id) {
        return Inventory.search().withId(id).first();
    }

    public static Optional<Widget> getItemNameContains(String name, boolean caseSensitive) {
        return caseSensitive ? Inventory.search().filter((widget) -> {
            return widget.getName().contains(name);
        }).first() : Inventory.search().filter((widget) -> {
            return widget.getName().toLowerCase().contains(name.toLowerCase());
        }).first();
    }

    public static Optional<Widget> getItemNameContains(String name) {
        return getItemNameContains(name, true);
    }

    public static Optional<Widget> getItem(String name, boolean caseSensitive) {
        return caseSensitive ? Inventory.search().filter((widget) -> {
            return widget.getName().equals(name);
        }).first() : Inventory.search().filter((widget) -> {
            return widget.getName().toLowerCase().equals(name.toLowerCase());
        }).first();
    }

    public static Optional<Widget> getItem(String name) {
        return getItem(name, true);
    }

    public static int getItemAmount(String name, boolean stacked) {
        if (stacked) {
            return nameContainsNoCase(name).first().isPresent() ? ((Widget)nameContainsNoCase(name).first().get()).getItemQuantity() : 0;
        } else {
            return nameContainsNoCase(name).result().size();
        }
    }

    public static int getItemAmount(int id) {
        return getItemAmount(id, false);
    }

    public static int getItemAmount(int id, boolean stacked) {
        if (stacked) {
            return getById(id).isPresent() ? ((Widget)getById(id).get()).getItemQuantity() : 0;
        } else {
            return Inventory.search().withId(id).result().size();
        }
    }

    public static boolean hasItem(String name) {
        return getItemAmount(name, false) > 0;
    }

    public static boolean hasItem(String name, boolean stacked) {
        return getItemAmount(name, stacked) > 0;
    }

    public static boolean hasItem(String name, int amount) {
        return getItemAmount(name, false) >= amount;
    }

    public static boolean hasItem(String name, int amount, boolean stacked) {
        return getItemAmount(name, stacked) >= amount;
    }

    public static boolean hasItems(String... names) {
        String[] var1 = names;
        int var2 = names.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String name = var1[var3];
            if (!hasItem(name)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasAnyItems(String... names) {
        String[] var1 = names;
        int var2 = names.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String name = var1[var3];
            if (hasItem(name)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasItem(int id) {
        return getItemAmount(id) > 0;
    }

    public static List<Widget> getItems() {
        return Inventory.search().result();
    }

    public static int emptySlots() {
        return 28 - Inventory.search().result().size();
    }
}
