package com.piggyplugins.collectionlogluck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.util.Text;

@Getter
@AllArgsConstructor
public class CollectionLogKillCount
{
    private final String name;
	@Setter
    private int amount;
    private final int sequence;

	public static CollectionLogKillCount fromString(String killCountString, int sequence)
	{
		String[] killCountSplit = killCountString.split(": ");
		String name = killCountSplit[0];
		String amount = Text.removeTags(killCountSplit[1])
			.replace(",", "");

		return new CollectionLogKillCount(name, Integer.parseInt(amount), sequence);
	}
}