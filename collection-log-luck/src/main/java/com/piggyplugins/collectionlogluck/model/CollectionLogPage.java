package com.piggyplugins.collectionlogluck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CollectionLogPage
{

	private final String name;
	private List<CollectionLogItem> items;
	private final List<CollectionLogKillCount> killCounts;

	@Setter
	private boolean isUpdated;

	public static String aliasPageName(String pageName)
	{
		switch (pageName.toLowerCase())
		{
			// Bosses Tab
			case "sire":
				return "Abyssal Sire";
			case "hydra":
				return "Alchemical Hydra";
			case "byro":
				return "Bryophyta";
			case "barrows":
				return "Barrows Chests";
			case "artio":
			case "art":
			case "cal":
			case "callisto":
				return "Callisto and Artio";
			case "cerb":
				return "Cerberus";
			case "chaos ele":
				return "Chaos Elemental";
			case "sara":
			case "saradomin":
			case "zilyana":
			case "zily":
				return "Commander Zilyana";
			case "corp":
				return "Corporeal Beast";
			case "crazy arch":
				return "Crazy Archaeologist";
			case "dks":
				return "Dagannoth Kings";
			case "duke":
			case "succ":
			case "succelus":
			case "ugly":
			case "succellus":
			case "duke succellus":
				return "Duke Succelus";
			case "jad":
			case "fight caves":
			case "fc":
				return "The Fight Caves";
			case "gaunt":
			case "gauntlet":
			case "cgaunt":
			case "cgauntlet":
			case "the corrupted gauntlet":
			case "cg":
				return "The Gauntlet";
			case "mole":
				return "Giant Mole";
			case "bando":
			case "bandos":
			case "graardor":
				return "General Graardor";
			case "dusk":
			case "dawn":
			case "gargs":
			case "ggs":
			case "gg":
				return "Grotesque Guardians";
			case "farming guild":
			case "uim":
				return "Hespori";
			case "zuk":
			case "inferno":
				return "The Inferno";
			case "kq":
				return "Kalphite Queen";
			case "kbd":
				return "King Black Dragon";
			case "cave kraken":
				return "Kraken";
			case "arma":
			case "kree":
			case "kreearra":
			case "armadyl":
				return "Kree'arra";
			case "zammy":
			case "zamorak":
			case "kril":
			case "kril trutsaroth":
				return "K'ril Tsutsaroth";
			case "leviathan":
			case "levi":
			case "lev":
				return "The Leviathan";
			case "nightmare":
			case "nm":
			case "tnm":
			case "nmare":
			case "pnm":
			case "phosani":
			case "phosanis":
			case "phosani nm":
			case "phosani nightmare":
			case "phosanis nightmare":
				return "The Nightmare";
			case "hill giant":
				return "Obor";
			case "mus":
			case "muspah":
			case "pm":
			case "the grumbler":
				return "Phantom Muspah";
			case "saracha":
			case "sarac":
			case "saracnis":
				return "Sarachnis";
			case "scorpa":
				return "Scorpia";
			case "scur":
			case "scurius":
			case "rat":
				return "Scurrius";
			case "skotizos":
			case "totems":
			case "totem":
				return "Skotizo";
			case "tempoross":
			case "temp":
				return "Tempoross";
			case "smoke devil":
			case "smoke devils":
			case "thermy":
			case "thermey":
				return "Thermonuclear Smoke Devil";
			case "vard":
			case "vardo":
				return "Vardorvis";
			case "spin":
			case "spindel":
			case "vene":
			case "venenatis":
				return "Venenatis and Spindel";
			case "calv":
			case "calvarion":
			case "calvar ion":
			case "calvar'ion":
			case "vetion":
			case "vet ion":
			case "vet'ion":
			case "veti":
				return "Vet'ion and Calvar'ion";
			case "vork":
				return "Vorkath";
			case "wt":
			case "todt":
				return "Wintertodt";
			case "whisperer":
			case "whisp":
				return "The Whisperer";
			case "zalc":
				return "Zalcano";
			case "zul":
			case "profit snake":
			case "bjs":
				return "Zulrah";

			// Raids Tab
			case "cox":
			case "xeric":
			case "chambers":
			case "olm":
			case "raids":
			case "cox cm":
			case "xeric cm":
			case "chambers cm":
			case "olm cm":
			case "raids cm":
			case "chambers of xeric - challenge mode":
				return "Chambers of Xeric";
			case "tob":
			case "theatre":
			case "verzik":
			case "verzik vitur":
			case "raids 2":
			case "theatre of blood: story mode":
			case "tob sm":
			case "tob story mode":
			case "tob story":
			case "theatre of blood: entry mode":
			case "tob em":
			case "tob entry mode":
			case "tob entry":
			case "theatre of blood: hard mode":
			case "tob cm":
			case "tob hm":
			case "tob hard mode":
			case "tob hard":
			case "hmt":
				return "Theatre of Blood";
			case "toa":
			case "tomb":
			case "tombs":
			case "raids 3":
			case "tomb of amascut":
				return "Tombs of Amascut";

			// Clues Tab
			case "begs":
			case "beg clues":
			case "beginners":
			case "beginner clues":
			case "beginner clue":
			case "clues beg":
			case "clues beginner":
			case "clue beg":
			case "clue beginner":
				return "Beginner Treasure Trails";
			case "easy clues":
			case "easy clue":
			case "easies":
			case "clues easy":
			case "clue easy":
				return "Easy Treasure Trails";
			case "meds":
			case "med":
			case "mediums":
			case "medium clues":
			case "medium clue":
			case "clues med":
			case "clues medium":
			case "clue med":
			case "clue medium":
				return "Medium Treasure Trails";
			case "hards":
			case "hard clues":
			case "hard clue":
			case "clue hard":
			case "clues hard":
				return "Hard Treasure Trails";
			case "masters":
			case "master clues":
			case "master clue":
			case "clue master":
			case "clues master":
				return "Master Treasure Trails";
			case "elites":
			case "elite clues":
			case "elite clue":
			case "clue elite":
			case "clues elite":
				return "Elite Treasure Trails";
			case "hards rare":
			case "hard clues rare":
			case "hard clue rare":
			case "hards rares":
			case "hard clues rares":
			case "hard clue rares":
			case "rare hards":
			case "rare hard":
				return "Hard Treasure Trails (Rare)";
			case "elites rare":
			case "elite clues rare":
			case "elite clue rare":
			case "elites rares":
			case "elite clues rares":
			case "elite clue rares":
			case "rare elite":
			case "rare elites":
				return "Elite Treasure Trails (Rare)";
			case "master treasure trails rare":
			case "masters rare":
			case "master clues rare":
			case "master clue rare":
			case "master treasure trails rares":
			case "masters rares":
			case "master clues rares":
			case "master clue rares":
			case "rare master":
			case "rare masters":
				return "Master Treasure Trails (Rare)";
			case "shared rewards":
			case "shared clue":
			case "shared clues":
			case "shared":
				return "Shared Treasure Trail Rewards";

			// Minigames Tab
			case "ba":
			case "barb":
				return "Barbarian Assault";
			case "brimhaven agility":
			case "brimhaven":
			case "brim agility":
			case "brim":
				return "Brimhaven Agility Arena";
			case "cw":
			case "cwars":
				return "Castle Wars";
			case "trawler":
				return "Fishing Trawler";
			case "giants foundry":
			case "foundry":
			case "gf":
				return "Giants' Foundry";
			case "gnome":
			case "restaurant":
				return "Gnome Restaurant";
			case "guardian of the rift":
			case "gotr":
			case "grift":
			case "grifting":
			case "guardians":
			case "runetodt":
				return "Guardians of the Rift";
			case "hs":
			case "sepc":
			case "sepulchre":
				return "Hallowed Sepulchre";
			case "lms":
				return "Last Man Standing";
			case "mta":
			case "magic arena":
				return "Magic Training Arena";
			case "mah homes":
			case "mahogany home":
			case "mh":
				return "Mahogany Homes";
			case "pc":
				return "Pest Control";
			case "rogues den":
			case "rogue den":
			case "rogues":
			case "rogue":
				return "Rogues' Den";
			case "shades of mortton":
			case "shade of mortton":
			case "shades":
			case "shade":
			case "mort ton":
			case "mortton":
				return "Shades of Mort'ton";
			case "soul war":
			case "sw":
				return "Soul Wars";
			case "trekking":
			case "temple trek":
				return "Temple Trekking";
			case "tithe":
				return "Tithe Farm";
			case "brewing":
				return "Trouble Brewing";
			case "vm":
			case "volc mine":
				return "Volcanic Mine";

			// Other Tab
			case "aerial fish":
			case "aerial":
				return "Aerial Fishing";
			case "all pets":
			case "pets":
			case "pet":
				return "All Pets";
			case "champions challenge":
			case "champion challenge":
			case "champion scrolls":
			case "champ scrolls":
				return "Champion's Challenge";
			case "chaos druid":
			case "elder druids":
			case "elder druid":
			case "elder":
				return "Chaos Druids";
			case "chompy":
			case "chompies":
			case "bird hunting":
				return "Chompy Bird Hunting";
			case "tower of life":
				return "Creature Creation";
			case "defenders":
			case "defender":
				return "Cyclopes";
			case "fossil island note":
			case "fossil island":
				return "Fossil Island Notes";
			case "gloughs experiments":
			case "gloughs experiment":
			case "glough experiments":
			case "glough experiment":
			case "demonics":
			case "demonic gorilla":
			case "demonic gorillas":
				return "Glough's Experiments";
			case "monkey backpacks":
			case "monkey backpack":
			case "ape atoll":
			case "backpack":
			case "backpacks":
				return "Monkey Backpacks";
			case "motherlode mine":
			case "motherlode":
			case "mlm":
			case "mm":
				return "Motherlode Mine";
			case "my notes":
			case "notes":
			case "note":
				return "My Notes";
			case "random event":
			case "random":
			case "randoms":
				return "Random Events";
			case "revs":
				return "Revenants";
			case "rooftop":
			case "rooftops":
			case "agility":
			case "graceful":
				return "Rooftop Agility";
			case "shayzien":
				return "Shayzien Armour";
			case "shooting star":
			case "star mining":
			case "ss":
			case "stars":
				return "Shooting Stars";
			case "skilling pet":
			case "skill pets":
			case "skill pet":
			case "pets skill":
			case "pet skill":
			case "pets skilling":
			case "pet skilling":
				return "Skilling Pets";
			case "slay":
				return "Slayer";
			case "tz haar":
			case "tz":
			case "tzh":
				return "TzHaar";
			case "misc":
				return "Miscellaneous";

			default:
				return pageName;
		}
	}

	public int getObtainedItemCount()
	{
		return (int) items.stream()
			.filter(CollectionLogItem::isObtained)
			.count();
	}

	public CollectionLogItem getItemById(int itemId)
	{
		for (CollectionLogItem item : getItems())
		{
			if (item.getId() == itemId)
			{
				return item;
			}
		}
		return null;
	}

	public CollectionLogKillCount getKillCountByName(String name)
	{
		for (CollectionLogKillCount killCount : getKillCounts())
		{
			if (killCount.getName().equals(name))
			{
				return killCount;
			}
		}
		return null;
	}
}
