package com.piggyplugins.BobTheFarmer;

//Possible tree patch states
public enum TreePatchState {
    NOT_STARTED(0),
    TRAVEL(1),
    PROCESS_TREE_PATCH(2),
    CHECK_HEALTH(3),
    PAY(4),

    RAKE(5),
    PLANT(6),
    PROTECT(7),
    EMPTY_INVENTORY(8),
    DONE(9);

    public final int Index;
    TreePatchState(int index)
    {
        this.Index = index;
    }
}
