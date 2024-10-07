package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;

// Items whose KC is only tracked by the collection log up until some relatively low count
// For example, Atlatl darts are only tracked until 250 are dropped, but thousands are dropped easily, so luck cannot
// be calculated.
// In the future, it may be possible to display a dry-only luck, or to display luck as long as the number obtained
// is below the maximum possible to be tracked by the collection log.
public class CappedCollectionLogTrackingDrop extends AbstractUnsupportedDrop {

    private final int itemCap;

    public CappedCollectionLogTrackingDrop(int itemCap) {
        this.itemCap = itemCap;
    }

    @Override
    public String getIncalculableReason(CollectionLogItem item, CollectionLogLuckConfig config) {
        return "Collection Log Luck plugin can't calculate " + itemName + ": log only tracks " + itemCap + " of this item";

    }
}
