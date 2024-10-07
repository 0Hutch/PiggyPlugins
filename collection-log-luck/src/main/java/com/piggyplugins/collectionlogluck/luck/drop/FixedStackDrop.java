package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.model.CollectionLog;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;
import com.piggyplugins.collectionlogluck.luck.RollInfo;

// When a fixed-size stack has a chance to drop (e.g. 1/64 chance for 3 Key master teleport scrolls), this is actually
// a binomial distribution where the number of successes is the number of items received divided by the stack size
public class FixedStackDrop extends BinomialDrop {

    private final int stackSize;

    public FixedStackDrop(RollInfo rollInfo, int stackSize) {
        super(rollInfo);
        this.stackSize = stackSize;
    }

    @Override
    protected int getNumSuccesses(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        return (int) Math.ceil((double) item.getQuantity() / stackSize);
    }
}
