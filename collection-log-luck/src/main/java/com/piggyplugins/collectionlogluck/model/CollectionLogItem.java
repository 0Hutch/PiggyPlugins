package com.piggyplugins.collectionlogluck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemComposition;

@Getter
@AllArgsConstructor
public class CollectionLogItem
{

	@Setter
    private int id;

    private final String name;

    @Setter
    private int quantity;

    @Setter
    private boolean obtained;

    private final int sequence;

    public static CollectionLogItem fromItemComposition(ItemComposition itemComposition, Integer sequence)
    {
        return new CollectionLogItem(
            itemComposition.getId(),
            itemComposition.getMembersName(),
            0,
            false,
            sequence
        );
    }
}
