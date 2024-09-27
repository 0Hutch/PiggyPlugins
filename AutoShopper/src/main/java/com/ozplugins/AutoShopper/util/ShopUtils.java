package com.ozplugins.AutoShopper.util;

import com.example.EthanApiPlugin.Collections.Shop;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.ShopInteraction;
import com.google.inject.Inject;
import com.ozplugins.AutoShopper.AutoShopperConfiguration;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;

@Slf4j
public class ShopUtils {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    public Utils util;
    @Inject
    private AutoShopperConfiguration config;

    int buyAmount = 0;
    public Widget getShopItemWidget(String name) {
        return Shop.search().withName(name).first().orElse(null);
    }

    public int getShopItemAmount(String name) {
        return getShopItemWidget(name).getItemQuantity();
    }

    public void buyXAmountFromShop(String name, int amount) {
        while (amount >= 50) {
            ShopInteraction.buyFifty(name);
            amount = amount - 50;
        }

        while (amount >= 10) {
            ShopInteraction.buyTen(name);
            amount = amount - 10;
        }

        while (amount >= 5) {
            ShopInteraction.buyFive(name);
            amount = amount - 5;
        }

        while (amount >= 1) {
            ShopInteraction.buyOne(name);
            amount--;
        }
    }

    public boolean shopContainsItem(String name) {
        return Shop.search().withName(name).first().isPresent();
    }

    public boolean isShopOpen() {
        return Widgets.search().hiddenState(false).withId(19660800).first().isPresent();
    }

    public void closeShop() {
        log.info("Attempting to close shop");
        EthanApiPlugin.invoke(-1, -1, 26, -1, -1, -1, "", "", -1, -1);
    }
}
