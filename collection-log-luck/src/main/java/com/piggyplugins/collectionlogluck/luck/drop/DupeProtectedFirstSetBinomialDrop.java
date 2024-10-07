package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.luck.LogItemInfo;
import com.piggyplugins.collectionlogluck.luck.RollInfo;
import com.piggyplugins.collectionlogluck.model.CollectionLog;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;

import java.util.ArrayList;
import java.util.List;

// Represents a Binomial drop whose FIRST set of gear (configured in constructor) will not drop duplicate pieces
// until all of the items in the set have been received at least once. Afterwards, normal binomial drop behavior resumes.
// This is not accurate, since the variance of a dupe-protected set is much lower than an unprotected set of the same
// individual item rarity. But math is hard, and I'm not sure how else to calculate this besides simulation, because
// dupe protection violates the basic binomial assumption of independent trials.
// NOTE: This class assumes that all items in the set have the same drop chance and drop mechanics.
public class DupeProtectedFirstSetBinomialDrop extends BinomialDrop {

    // The item IDs of all items in the set, including this item.
    // For technical reasons (LogItemInfos are static variables rather than an enum because of its size), these should
    // be integer item IDs.
    private final List<Integer> setItemIds;
    private final List<LogItemInfo> setLogItemInfos = new ArrayList<>();

    public DupeProtectedFirstSetBinomialDrop(RollInfo rollInfo, List<Integer> setItemIds) {
        super(rollInfo);
        this.setItemIds = setItemIds;
    }

    private void fetchLogItemInfos() {
        if (setLogItemInfos.isEmpty()) {
            for (Integer itemId : setItemIds) {
                setLogItemInfos.add(LogItemInfo.findByItemId(itemId));
            }
        }
    }

    // Before (and when) the set is complete, then individual item luck is related to the sum of the number of drops.
    // AFTER the set is completed (meaning the player has received at least 1 non-dupe-protected item),
    // then individual item luck is related to only that item's drop quantity and drop chance
    private boolean hasProgressedPastCompletedSet(CollectionLog collectionLog) {
        for (LogItemInfo setPiece : setLogItemInfos) {
            CollectionLogItem item = collectionLog.searchForItem(setPiece.getItemName());
            if (item != null && item.getQuantity() > 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected int getNumSuccesses(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        fetchLogItemInfos();

        if (hasProgressedPastCompletedSet(collectionLog)) {
            return super.getNumSuccesses(item, collectionLog, config);
        }

        // Sum the number of successes for all items in this set.
        int numSuccesses = 0;
        for (LogItemInfo setPiece : setLogItemInfos) {
            CollectionLogItem setItem = collectionLog.searchForItem(setPiece.getItemName());
            if (setItem != null) {
                numSuccesses += super.getNumSuccesses(setItem, collectionLog, config);
            }
        }
        return numSuccesses;
    }

    @Override
    protected double getDropChance(RollInfo rollInfo, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        fetchLogItemInfos();

        if (hasProgressedPastCompletedSet(collectionLog)) {
            return super.getDropChance(rollInfo, collectionLog, config);
        }

        // Assumes all items in the set drop with equal chance
        return super.getDropChance(rollInfo, collectionLog, config) * setLogItemInfos.size();
    }
}
