package com.santaplugins.AutoLoot;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.SneakyThrows;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;

import java.util.*;
import java.util.stream.Collectors;

@PluginDescriptor(name = "AutoLoot", description = "loots stuff from the ground", enabledByDefault = false, tags = {"supersanta, loot"})

public class AutoLootPlugin extends Plugin {
    @Inject
    Client client;
    @Inject
    private AutoLootConfig config;
    @Inject
    public ItemManager itemManager;
    private List<String> itemsToLoot = null;
    public Queue<ItemStack> lootQueue = new LinkedList<>();
    public int timeout = 0;
    public boolean isLooting = false;
    @Provides
    public AutoLootConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoLootConfig.class);
    }

    @Override
    @SneakyThrows
    public void startUp() {
        timeout = 0;
    }
    @Override
    public void shutDown() {
        lootQueue.clear();
    }
    @Subscribe
    public void onGameTick(GameTick e) {
        if(timeout > 0) {
            timeout--;
            return;
        }

        if (client.getGameState() != GameState.LOGGED_IN || EthanApiPlugin.isMoving() || client.getLocalPlayer().isInteracting()) {
            return;
        }

        if(lootQueue.isEmpty()) isLooting = false;
        if (TileItems.search().empty()) {
            return;
        }

        if (!lootQueue.isEmpty()) {
            isLooting = true;
            ItemStack itemStack = lootQueue.peek();
            TileItems.search().withId(itemStack.getId()).first().ifPresent(item -> {
                ItemComposition itemComposition = itemManager.getItemComposition(item.getTileItem().getId());
                if (itemComposition.isStackable() || itemComposition.getNote() != -1) {
                    item.interact(false);
                }
                if (!Inventory.full()) { //checks if inventory is full, uses ethanvann api
                    item.interact(false);
                }
                timeout = 3;
                lootQueue.remove();
                return;
            });
        }
        if(isLooting) timeout = 6;
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event) { // event triggers, when npc drops loot for you
        Collection<ItemStack> items = event.getItems(); //gets items dropped by npc
        items.forEach( item -> {
            ItemComposition itemComp = itemManager.getItemComposition(item.getId());
            if (getItemsToLoot().contains(itemComp.getName())) {
                lootQueue.add(item);
            }
        });
    }
    public List<String> getItemsToLoot() {
        if (itemsToLoot == null)
            itemsToLoot = Arrays.stream(config.itemsToLoot().split(";")).map(String::trim).collect(Collectors.toList());
        return itemsToLoot;
    }
}
