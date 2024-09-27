package com.bankmemory.bankview;

import lombok.Value;
import net.runelite.client.util.AsyncBufferedImage;

@Value
public class ItemListEntry {
    String itemName;
    int quantity;
    AsyncBufferedImage image;
    int geValue;
    int haValue;
}
