package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;

// Base class for all unsupported or unimplemented drops
public abstract class AbstractUnsupportedDrop implements DropLuck {

    protected String itemName;

    @Override
    public abstract String getIncalculableReason(CollectionLogItem item, CollectionLogLuckConfig config);

    @Override
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

}
