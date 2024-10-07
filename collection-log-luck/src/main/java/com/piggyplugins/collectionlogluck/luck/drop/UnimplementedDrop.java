package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;

public class UnimplementedDrop extends AbstractUnsupportedDrop {

   @Override
    public String getIncalculableReason(CollectionLogItem item, CollectionLogLuckConfig config) {
        return "Collection Log Luck plugin does not currently support luck calculation for " + itemName;
    }
}
