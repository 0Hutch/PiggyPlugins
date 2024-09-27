package com.bankmemory.data;

public abstract class AbstractDataStoreUpdateListener implements DataStoreUpdateListener {
    @Override
    public void currentBanksListChanged() {
        // NO OP
    }

    @Override
    public void currentBanksListOrderChanged() {
        // NO OP
    }

    @Override
    public void snapshotBanksListChanged() {
        // NO OP
    }

    @Override
    public void displayNameMapUpdated() {
        // NO OP
    }
}
