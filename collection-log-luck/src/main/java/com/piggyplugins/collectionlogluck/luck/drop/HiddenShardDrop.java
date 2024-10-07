package com.piggyplugins.collectionlogluck.luck.drop;

import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.model.CollectionLog;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;
import com.piggyplugins.collectionlogluck.luck.RollInfo;

// When a drop requires a number of hidden "shards" to accumulate (e.g. the player must hit the Desert Treasure 2 boss
// vestige drop 3 times) before actually receiving the item, this is actually a binomial distribution
// where the number of successes is the number of items received multiplied by the number of
// hidden shards.
// Extra shards are not taken into account, but this is hidden information anyway, so this implementation is still
// correct.
public class HiddenShardDrop extends BinomialDrop {

    private final int shardsRequired;

    public HiddenShardDrop(RollInfo rollInfo, int shardsRequired) {
        super(rollInfo);
        this.shardsRequired = shardsRequired;
    }

    @Override
    protected int getNumSuccesses(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        return item.getQuantity() * shardsRequired;
    }

    // Anyone who has received the same number of actual drops (and any unknown number of hidden shards) is
    // "in the same boat" in terms of luck
    @Override
    protected int getMaxEquivalentNumSuccesses(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        return (item.getQuantity() + 1) * shardsRequired - 1;
    }

}
