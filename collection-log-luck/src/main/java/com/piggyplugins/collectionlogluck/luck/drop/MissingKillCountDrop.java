package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;

// Items whose KC is not tracked by the collection log cannot currently have their luck calculated. In the future,
// it may be possible to use the loot tracker plugin to implement some of these items.
public class MissingKillCountDrop extends AbstractUnsupportedDrop {

    @Override
    public String getIncalculableReason(CollectionLogItem item, CollectionLogLuckConfig config) {
        return "Collection Log Luck plugin can't calculate " + itemName + ": KC not tracked for some drop sources";

    }
}
