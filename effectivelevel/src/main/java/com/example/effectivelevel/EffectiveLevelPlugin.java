package com.example.effectivelevel;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Effective Levels",
	description = "Shows the invisible effective boosted skill levels in the skills tab.<br>" +
		"These are the levels that go into the max hit, accuracy roll and <br>" +
		"defence roll formulas before accounting for equipment bonuses.",
	tags = {"skill", "effective", "boosted", "invisible", "levels"}
)
public class EffectiveLevelPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private EffectiveLevelConfig config;

	private final Skill[] combatSkills = new Skill[] { Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.RANGED, Skill.MAGIC };
	private final Skill[] nonCombatSkills = new Skill[] { Skill.MINING, Skill.CONSTRUCTION, Skill.FISHING, Skill.WOODCUTTING };
	private final Skill[] skills = concat(combatSkills, nonCombatSkills);

	private final int[] miningRings = new int[]
	{
		ItemID.CELESTIAL_RING, ItemID.CELESTIAL_RING_UNCHARGED, ItemID.CELESTIAL_SIGNET, ItemID.CELESTIAL_SIGNET_UNCHARGED
	};

	private static Skill[] concat(Skill[] a, Skill[] b)
	{
		Skill[] combined = Arrays.copyOf(a, a.length + b.length);
		System.arraycopy(b, 0, combined, a.length, b.length);
		return combined;
	}

	@Provides
	EffectiveLevelConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EffectiveLevelConfig.class);
	}

	@Override
	protected void shutDown() throws Exception
	{
		resetLevels();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("effectivelevel"))
		{
			resetLevels();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (!GameState.LOGGED_IN.equals(client.getGameState()))
		{
			return;
		}

		for (Skill skill : combatSkills)
		{
			double prayerBoost = getPrayerBoost(skill);
			int stanceBonus = getStanceBonus(skill);
			double voidBonus = getVoidBonus(skill);

			int effectiveLevel = client.getBoostedSkillLevel(skill);

			if (config.showPrayerBoost())
			{
				effectiveLevel *= prayerBoost;
			}

			if (config.showStanceBonus())
			{
				effectiveLevel += stanceBonus;
			}

			if (config.showAdjustmentConstant())
			{
				effectiveLevel += 8;
			}

			if (config.showVoidBonus())
			{
				effectiveLevel *= voidBonus;
			}

			updateSkillLevel(skill, effectiveLevel);
		}

		if (config.showInvisibleBoost())
		{
			int miningLevel = client.getBoostedSkillLevel(Skill.MINING);
			int constructionLevel = client.getBoostedSkillLevel(Skill.CONSTRUCTION);
			int fishingLevel = client.getBoostedSkillLevel(Skill.FISHING);
			int woodcuttingLevel = client.getBoostedSkillLevel(Skill.WOODCUTTING);

			Set<Integer> equipment = getItemIDs(InventoryID.EQUIPMENT);
			for (int id : miningRings)
			{
				if (equipment.contains(id))
				{
					miningLevel += 4;
					break;
				}
			}

			Set<Integer> inventory = getItemIDs(InventoryID.INVENTORY);
			if (inventory.contains(ItemID.CRYSTAL_SAW))
			{
				constructionLevel += 3;
			}

			if (client.getLocalPlayer() != null)
			{
				int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();

				if (regionId == 11927 || regionId == 12183)
				{
					miningLevel += 7;
				}
				else if (regionId == 10293)
				{
					fishingLevel += 7;
				}
				else if (regionId == 6198 || regionId == 6454)
				{
					woodcuttingLevel += 7;
				}
			}

			updateSkillLevel(Skill.MINING, miningLevel);
			updateSkillLevel(Skill.CONSTRUCTION, constructionLevel);
			updateSkillLevel(Skill.FISHING, fishingLevel);
			updateSkillLevel(Skill.WOODCUTTING, woodcuttingLevel);
		}
	}

	private void resetLevels()
	{
		for (Skill skill : skills)
		{
			updateSkillLevel(skill, client.getBoostedSkillLevel(skill));
		}
	}

	private void updateSkillLevel(Skill skill, int effectiveLevel)
	{
		int childId;
		switch (skill)
		{
			case ATTACK:
				childId = 1;
				break;
			case STRENGTH:
				childId = 2;
				break;
			case DEFENCE:
				childId = 3;
				break;
			case RANGED:
				childId = 4;
				break;
			case MAGIC:
				childId = 6;
				break;
			case MINING:
				childId = 17;
				break;
			case CONSTRUCTION:
				childId = 8;
				break;
			case FISHING:
				childId = 19;
				break;
			case WOODCUTTING:
				childId = 22;
				break;
			default:
				return;
		}
		Widget skillWidget = client.getWidget(InterfaceID.SKILLS, childId);
		if (skillWidget == null)
		{
			return;
		}

		Widget[] skillWidgetComponents = skillWidget.getDynamicChildren();
		if (skillWidgetComponents.length >= 4)
		{
			skillWidgetComponents[3].setText("" + effectiveLevel);
		}
	}

	private double getPrayerBoost(Skill skill)
	{
		double multiplier = 1;

		switch (skill)
		{
			case ATTACK:
				multiplier = client.isPrayerActive(Prayer.CLARITY_OF_THOUGHT) ? 1.05 : multiplier;
				multiplier = client.isPrayerActive(Prayer.IMPROVED_REFLEXES) ? 1.10 : multiplier;
				multiplier = client.isPrayerActive(Prayer.INCREDIBLE_REFLEXES) ? 1.15 : multiplier;
				multiplier = client.isPrayerActive(Prayer.CHIVALRY) ? 1.15 : multiplier;
				multiplier = client.isPrayerActive(Prayer.PIETY) ? 1.20 : multiplier;
				break;
			case STRENGTH:
				multiplier = client.isPrayerActive(Prayer.BURST_OF_STRENGTH) ? 1.05 : multiplier;
				multiplier = client.isPrayerActive(Prayer.SUPERHUMAN_STRENGTH) ? 1.10 : multiplier;
				multiplier = client.isPrayerActive(Prayer.ULTIMATE_STRENGTH) ? 1.15 : multiplier;
				multiplier = client.isPrayerActive(Prayer.CHIVALRY) ? 1.18 : multiplier;
				multiplier = client.isPrayerActive(Prayer.PIETY) ? 1.23 : multiplier;
				break;
			case DEFENCE:
				multiplier = client.isPrayerActive(Prayer.THICK_SKIN) ? 1.05 : multiplier;
				multiplier = client.isPrayerActive(Prayer.ROCK_SKIN) ? 1.10 : multiplier;
				multiplier = client.isPrayerActive(Prayer.STEEL_SKIN) ? 1.15 : multiplier;
				multiplier = client.isPrayerActive(Prayer.CHIVALRY) ? 1.20 : multiplier;
				multiplier = client.isPrayerActive(Prayer.PIETY) ? 1.25 : multiplier;
				multiplier = client.isPrayerActive(Prayer.RIGOUR) ? 1.25 : multiplier;
				multiplier = client.isPrayerActive(Prayer.AUGURY) ? 1.25 : multiplier;
				break;
			case RANGED:
				multiplier = client.isPrayerActive(Prayer.SHARP_EYE) ? 1.05 : multiplier;
				multiplier = client.isPrayerActive(Prayer.HAWK_EYE) ? 1.10 : multiplier;
				multiplier = client.isPrayerActive(Prayer.EAGLE_EYE) ? 1.15 : multiplier;
				multiplier = client.isPrayerActive(Prayer.RIGOUR) ? 1.20 : multiplier;
				break;
			case MAGIC:
				multiplier = client.isPrayerActive(Prayer.MYSTIC_WILL) ? 1.05 : multiplier;
				multiplier = client.isPrayerActive(Prayer.MYSTIC_LORE) ? 1.10 : multiplier;
				multiplier = client.isPrayerActive(Prayer.MYSTIC_MIGHT) ? 1.15 : multiplier;
				multiplier = client.isPrayerActive(Prayer.AUGURY) ? 1.25 : multiplier;
				break;
		}
		return multiplier;
	}

	private int getStanceBonus(Skill skill)
	{
		int attackStyleVarbit = client.getVarpValue(VarPlayer.ATTACK_STYLE);
		int combatStyleVarbit = client.getVarbitValue(Varbits.EQUIPPED_WEAPON_TYPE);

		String attackStyle = CombatStyle.getAttackStyleText(combatStyleVarbit, attackStyleVarbit);

		int bonus = 0;

		switch (skill)
		{
			case ATTACK:
				bonus = "Accurate".equals(attackStyle) ? 3 : bonus;
				bonus = "Controlled".equals(attackStyle) ? 1 : bonus;
				break;
			case STRENGTH:
				bonus = "Aggressive".equals(attackStyle) ? 3 : bonus;
				bonus = "Controlled".equals(attackStyle) ? 1 : bonus;
				break;
			case DEFENCE:
				bonus = "Controlled".equals(attackStyle) ? 1 : bonus;
				bonus = "Defensive".equals(attackStyle) ? 3 : bonus;
				bonus = "Longrange".equals(attackStyle) ? 3 : bonus;
				break;
			case RANGED:
				bonus = "Accurate ranging".equals(attackStyle) ? 3 : bonus;
				break;
			case MAGIC:
				bonus = "Accurate casting".equals(attackStyle) ? 3 : bonus;
				bonus = "Longrange casting".equals(attackStyle) ? 1 : bonus;
				break;
		}
		return bonus;
	}

	private Set<Integer> getItemIDs(final InventoryID inventoryID)
	{
		final ItemContainer container = client.getItemContainer(inventoryID);
		Set<Integer> itemIDs = new HashSet<>();
		if (container != null)
		{
			for (Item item : container.getItems())
			{
				itemIDs.add(item.getId());
			}
		}
		return itemIDs;
	}

	private double getVoidBonus(Skill skill)
	{
		double multiplier = 1;

		if (Skill.DEFENCE.equals(skill))
		{
			return multiplier;
		}

		Set<Integer> itemIDs = getItemIDs(InventoryID.EQUIPMENT);

		boolean voidGloves = (itemIDs.contains(ItemID.VOID_KNIGHT_GLOVES) ||
			itemIDs.contains(ItemID.VOID_KNIGHT_GLOVES_L) ||
			itemIDs.contains(ItemID.VOID_KNIGHT_GLOVES_OR) ||
			itemIDs.contains(ItemID.VOID_KNIGHT_GLOVES_LOR));
		boolean voidTop = (itemIDs.contains(ItemID.VOID_KNIGHT_TOP) ||
			itemIDs.contains(ItemID.VOID_KNIGHT_TOP_L) ||
			itemIDs.contains(ItemID.VOID_KNIGHT_TOP_OR) ||
			itemIDs.contains(ItemID.VOID_KNIGHT_TOP_LOR));
		boolean voidBottom = (itemIDs.contains(ItemID.VOID_KNIGHT_ROBE) ||
			itemIDs.contains(ItemID.VOID_KNIGHT_ROBE_L) ||
			itemIDs.contains(ItemID.VOID_KNIGHT_ROBE_OR) ||
			itemIDs.contains(ItemID.VOID_KNIGHT_ROBE_LOR));

		boolean eliteVoidTop = (itemIDs.contains(ItemID.ELITE_VOID_TOP) ||
			itemIDs.contains(ItemID.ELITE_VOID_TOP_L) ||
			itemIDs.contains(ItemID.ELITE_VOID_TOP_OR) ||
			itemIDs.contains(ItemID.ELITE_VOID_TOP_LOR));
		boolean eliteVoidBottom = (itemIDs.contains(ItemID.ELITE_VOID_ROBE) ||
			itemIDs.contains(ItemID.ELITE_VOID_ROBE_L) ||
			itemIDs.contains(ItemID.ELITE_VOID_ROBE_OR) ||
			itemIDs.contains(ItemID.ELITE_VOID_ROBE_LOR));

		boolean voidHelmMelee = (itemIDs.contains(ItemID.VOID_MELEE_HELM) ||
			itemIDs.contains(ItemID.VOID_MELEE_HELM_L) ||
			itemIDs.contains(ItemID.VOID_MELEE_HELM_OR) ||
			itemIDs.contains(ItemID.VOID_MELEE_HELM_LOR));
		boolean voidHelmRanged = (itemIDs.contains(ItemID.VOID_RANGER_HELM) ||
			itemIDs.contains(ItemID.VOID_RANGER_HELM_L) ||
			itemIDs.contains(ItemID.VOID_RANGER_HELM_OR) ||
			itemIDs.contains(ItemID.VOID_RANGER_HELM_LOR));
		boolean voidHelmMagic = (itemIDs.contains(ItemID.VOID_MAGE_HELM) ||
			itemIDs.contains(ItemID.VOID_MAGE_HELM_L) ||
			itemIDs.contains(ItemID.VOID_MAGE_HELM_OR) ||
			itemIDs.contains(ItemID.VOID_MAGE_HELM_LOR));

		if (!(voidGloves && (voidTop || eliteVoidTop) && (voidBottom || eliteVoidBottom)))
		{
			return multiplier;
		}

		if ((Skill.ATTACK.equals(skill) || Skill.STRENGTH.equals(skill)) && voidHelmMelee)
		{
			multiplier = 1.10;
		}
		else if (Skill.RANGED.equals(skill) && voidHelmRanged)
		{
			if (eliteVoidTop && eliteVoidBottom)
			{
				multiplier = 1.125;
			}
			else
			{
				multiplier = 1.10;
			}
		}
		else if (Skill.MAGIC.equals(skill) && voidHelmMagic)
		{
			if (eliteVoidTop && eliteVoidBottom)
			{
				multiplier = 1.475;
			}
			else
			{
				multiplier = 1.45;
			}
		}
		return multiplier;
	}

	private enum CombatStyle
	{
		TYPE_0("Accurate", "Aggressive", null, "Defensive"),
		TYPE_1("Accurate", "Aggressive", "Aggressive", "Defensive"),
		TYPE_2("Accurate", "Aggressive", null, "Defensive"),
		TYPE_3("Accurate ranging", "Rapid", null, "Longrange"),
		TYPE_4("Accurate", "Aggressive", "Controlled", "Defensive"),
		TYPE_5("Accurate ranging", "Rapid", null, "Longrange"),
		TYPE_6("Aggressive", "Rapid", "Casting", null),
		TYPE_7("Accurate ranging", "Rapid", null, "Longrange"),
		TYPE_8("Other", "Aggressive", null, null),
		TYPE_9("Accurate", "Aggressive", "Controlled", "Defensive"),
		TYPE_10("Accurate", "Aggressive", "Aggressive", "Defensive"),
		TYPE_11("Accurate", "Aggressive", "Aggressive", "Defensive"),
		TYPE_12("Controlled", "Aggressive", null, "Defensive"),
		TYPE_13("Accurate", "Aggressive", null, "Defensive"),
		TYPE_14("Accurate", "Aggressive", "Aggressive", "Defensive"),
		TYPE_15("Controlled", "Controlled", "Controlled", "Defensive"),
		TYPE_16("Accurate", "Aggressive", "Controlled", "Defensive"),
		TYPE_17("Accurate", "Aggressive", "Aggressive", "Defensive"),
		TYPE_18("Accurate", "Aggressive", null, "Defensive", "Casting", "Defensive casting"),
		TYPE_19("Accurate ranging", "Rapid", null, "Longrange"),
		TYPE_20("Accurate", "Controlled", null, "Defensive"),
		TYPE_21("Accurate", "Aggressive", null, "Defensive", "Casting", "Defensive casting"),
		TYPE_22("Accurate", "Aggressive", "Aggressive", "Defensive"),
		TYPE_23("Accurate casting", "Accurate casting", null, "Longrange casting"),
		TYPE_24("Accurate", "Aggressive", "Controlled", "Defensive"),
		TYPE_25("Controlled", "Aggressive", null, "Defensive"),
		TYPE_26("Aggressive", "Aggressive", null, "Aggressive"),
		TYPE_27("Accurate", null, null, "Other"),
		TYPE_28("Accurate casting", "Accurate casting", null, "Longrange casting"),
		TYPE_29("Accurate", "Aggressive", "Aggressive", "Defensive");

		private final String[] attackStyles;

		private static final Map<Integer, CombatStyle> combatStyles;

		static
		{
			ImmutableMap.Builder<Integer, CombatStyle> builder = new ImmutableMap.Builder<>();

			for (CombatStyle combatStyle : values())
			{
				builder.put(combatStyle.ordinal(), combatStyle);
			}

			combatStyles = builder.build();
		}

		CombatStyle(final String... attackStyles)
		{
			this.attackStyles = attackStyles;
		}

		public static String getAttackStyleText(int combatStyleId, int attackStyleId)
		{
			return combatStyles.get(combatStyleId).attackStyles[attackStyleId];
		}
	}
}
