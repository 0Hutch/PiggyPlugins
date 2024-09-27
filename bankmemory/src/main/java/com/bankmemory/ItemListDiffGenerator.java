package com.bankmemory;

import com.bankmemory.data.BankItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemListDiffGenerator {
    /**
     * Generates a list of differences between the two given item lists. The result list's items are in the order they
     * appear in the 'before' list, or if they don't appear in that then the order they're in in the 'after' list.
     */
    List<BankItem> findDifferencesBetween(List<BankItem> before, List<BankItem> after) {
        Map<Integer, Integer> beforeItems = new HashMap<>();
        Map<Integer, Integer> afterItems = new HashMap<>();
        after.forEach(i -> afterItems.put(i.getItemId(), i.getQuantity()));
        List<BankItem> results = new ArrayList<>();
        for (BankItem i : before) {
            beforeItems.put(i.getItemId(), i.getQuantity());
            int diff = afterItems.getOrDefault(i.getItemId(), 0) - i.getQuantity();
            if (diff != 0) {
                results.add(new BankItem(i.getItemId(), diff));
            }
        }
        for (BankItem i : after) {
            if (!beforeItems.containsKey(i.getItemId())) {
                results.add(i);
            }
        }
        return results;
    }
}
