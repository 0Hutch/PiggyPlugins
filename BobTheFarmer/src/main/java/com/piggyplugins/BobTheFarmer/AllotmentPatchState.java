package com.piggyplugins.BobTheFarmer;

public enum AllotmentPatchState {
    NOT_STARTED(0),
    TRAVEL(1),
    PROCESS_ALLOTMENT_PATCH(2),
    HARVEST(3),
    CLEAR(4),
    RAKE(5),
    PLANTING(6),
    COMPOST(7),
    EMPTY_INVENTORY(8),
    MANAGE_INVENTORY(9),
    PROTECT(10),
    NOTE(11),
    DONE(12);

    public final int Index;
    private AllotmentPatchState(int index)
    {
        this.Index = index;
    }
}
