package com.piggyplugins.collectionlogluck.luck;

import com.google.common.collect.ImmutableList;
import com.piggyplugins.collectionlogluck.CollectionLogLuckConfig;
import com.piggyplugins.collectionlogluck.luck.drop.*;
import net.runelite.api.ItemID;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

// All 1524 collection log items as of 7/25/2024 and a mapping to their item IDs and drop mechanics / probabilities.
public class LogItemInfo {

    // case-insensitive map, just in case
    private static final Map<String, LogItemInfo> logItemInfos = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static final Map<Integer, LogItemInfo> logItemInfoIds = new HashMap<>();

    /*
     Example of each supported probability distribution type:

        ...ABYSSAL_BLUE_DYE_26809 = new LogItemInfo("Abyssal blue dye", 26809,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 1200))),
        ...ANCIENT_PAGE_11341 = new LogItemInfo("Ancient page", 11341,
            new MissingKillCountDrop()),
        ...BELLATOR_VESTIGE_28279 = new LogItemInfo("Bellator vestige", 28279,
            new HiddenShardDrop(new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 1.0 / 64.0 * 3.0 / 8.0), 3)),
        ...CHOMPY_BIRD_HAT_2978 = new LogItemInfo("Chompy bird hat", 2978,
            new DeterministicDrop()),
        ...GILDED_2H_SWORD_20155 = new LogItemInfo("Gilded 2h sword", 20155,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            ))),
        ...KEY_MASTER_TELEPORT_13249 = new LogItemInfo("Key master teleport", 13249,
            new FixedStackDrop(new RollInfo(LogItemSourceInfo.CERBERUS_KILLS, 1.0 / 64), 3)),
        ...VETION_JR_13179 = new LogItemInfo("Vet'ion jr.", 13179,
            new UnimplementedDrop()),
        ...VORKATHS_HEAD_21907 = new LogItemInfo("Vorkath's head", 21907,
            new GuaranteedOnceBinomialDrop(new RollInfo(LogItemSourceInfo.VORKATH_KILLS, 1.0 / 50), 50)),

        TODO: Is Slayer Kill Log available in the code somewhere? Users could use this to calculate luck for related drops.
            Maybe users would have to open this every so often to be able to track luck.

        Remaining items to support:
            Misc:
                Dry-only drops (single drop tablets, etc.)
    */

    public static LogItemInfo ABYSSAL_BLUE_DYE_26809 = new LogItemInfo("Abyssal blue dye", 26809,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 1200)));
    public static LogItemInfo ABYSSAL_DAGGER_13265 = new LogItemInfo("Abyssal dagger", 13265,
            new MissingKillCountDrop());
    public static LogItemInfo ABYSSAL_GREEN_DYE_26807 = new LogItemInfo("Abyssal green dye", 26807,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 1200)));
    public static LogItemInfo ABYSSAL_HEAD_7979 = new LogItemInfo("Abyssal head", 7979,
            new MissingKillCountDrop());
    public static LogItemInfo ABYSSAL_LANTERN_26822 = new LogItemInfo("Abyssal lantern", 26822,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 700))
                    .withConfigOption(CollectionLogLuckConfig.NUM_ABYSSAL_LANTERNS_PURCHASED_KEY));
    public static LogItemInfo ABYSSAL_NEEDLE_26813 = new LogItemInfo("Abyssal needle", 26813,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 300)));
    // Note: This represents the effective chance of dropping from the boss, NOT the chance given your # of Unsired.
    public static LogItemInfo ABYSSAL_ORPHAN_13262 = new LogItemInfo("Abyssal orphan", 13262,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ABYSSAL_SIRE_KILLS, 1.0 / 2560)));
    public static LogItemInfo ABYSSAL_PEARLS_26792 = new LogItemInfo("Abyssal pearls", 26792,
            new BinomialUniformSumDrop(
                    new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 6.94),
                    10, 20
            ));
    public static LogItemInfo ABYSSAL_PROTECTOR_26901 = new LogItemInfo("Abyssal protector", 26901,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 4000)));
    public static LogItemInfo ABYSSAL_RED_DYE_26811 = new LogItemInfo("Abyssal red dye", 26811,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 1200)));
    public static LogItemInfo ABYSSAL_WHIP_4151 = new LogItemInfo("Abyssal whip", 4151,
            new MissingKillCountDrop());
    public static LogItemInfo ADAMANT_BOOTS_4129 = new LogItemInfo("Adamant boots", 4129,
            new MissingKillCountDrop());
    public static LogItemInfo ADAMANT_CANE_12377 = new LogItemInfo("Adamant cane", 12377,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_DEFENDER_8849 = new LogItemInfo("Adamant defender", 8849,
            new MissingKillCountDrop());
    public static LogItemInfo ADAMANT_DRAGON_MASK_23270 = new LogItemInfo("Adamant dragon mask", 23270,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo ADAMANT_FULL_HELM_G_2613 = new LogItemInfo("Adamant full helm (g)", 2613,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_FULL_HELM_T_2605 = new LogItemInfo("Adamant full helm (t)", 2605,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_HELM_H1_10296 = new LogItemInfo("Adamant helm (h1)", 10296,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_HELM_H2_10298 = new LogItemInfo("Adamant helm (h2)", 10298,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_HELM_H3_10300 = new LogItemInfo("Adamant helm (h3)", 10300,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_HELM_H4_10302 = new LogItemInfo("Adamant helm (h4)", 10302,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_HELM_H5_10304 = new LogItemInfo("Adamant helm (h5)", 10304,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_KITESHIELD_G_2611 = new LogItemInfo("Adamant kiteshield (g)", 2611,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_KITESHIELD_T_2603 = new LogItemInfo("Adamant kiteshield (t)", 2603,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATEBODY_G_2607 = new LogItemInfo("Adamant platebody (g)", 2607,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATEBODY_H1_23392 = new LogItemInfo("Adamant platebody (h1)", 23392,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATEBODY_H2_23395 = new LogItemInfo("Adamant platebody (h2)", 23395,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATEBODY_H3_23398 = new LogItemInfo("Adamant platebody (h3)", 23398,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATEBODY_H4_23401 = new LogItemInfo("Adamant platebody (h4)", 23401,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATEBODY_H5_23404 = new LogItemInfo("Adamant platebody (h5)", 23404,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATEBODY_T_2599 = new LogItemInfo("Adamant platebody (t)", 2599,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATELEGS_G_2609 = new LogItemInfo("Adamant platelegs (g)", 2609,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATELEGS_T_2601 = new LogItemInfo("Adamant platelegs (t)", 2601,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATESKIRT_G_3475 = new LogItemInfo("Adamant plateskirt (g)", 3475,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_PLATESKIRT_T_3474 = new LogItemInfo("Adamant plateskirt (t)", 3474,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_SHIELD_H1_7334 = new LogItemInfo("Adamant shield (h1)", 7334,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_SHIELD_H2_7340 = new LogItemInfo("Adamant shield (h2)", 7340,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_SHIELD_H3_7346 = new LogItemInfo("Adamant shield (h3)", 7346,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_SHIELD_H4_7352 = new LogItemInfo("Adamant shield (h4)", 7352,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ADAMANT_SHIELD_H5_7358 = new LogItemInfo("Adamant shield (h5)", 7358,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo AFRO_12430 = new LogItemInfo("Afro", 12430,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo AGILITY_ARENA_TICKET_2996 = new LogItemInfo("Agility arena ticket", 2996,
            new DeterministicDrop());
    public static LogItemInfo AHRIMS_HOOD_4708 = new LogItemInfo("Ahrim's hood", 4708,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo AHRIMS_ROBESKIRT_4714 = new LogItemInfo("Ahrim's robeskirt", 4714,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo AHRIMS_ROBETOP_4712 = new LogItemInfo("Ahrim's robetop", 4712,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo AHRIMS_STAFF_4710 = new LogItemInfo("Ahrim's staff", 4710,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo ALCHEMICAL_HYDRA_HEADS_23077 = new LogItemInfo("Alchemical hydra heads", 23077,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ALCHEMICAL_HYDRA_KILLS, 1.0 / 256)));
    public static LogItemInfo ALE_OF_THE_GODS_20056 = new LogItemInfo("Ale of the gods", 20056,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo AMULET_OF_AVARICE_22557 = new LogItemInfo("Amulet of avarice", 22557,
            new MissingKillCountDrop());
    public static LogItemInfo AMULET_OF_DEFENCE_T_23309 = new LogItemInfo("Amulet of defence (t)", 23309,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo AMULET_OF_ETERNAL_GLORY_19707 = new LogItemInfo("Amulet of eternal glory", 19707,
            new MissingKillCountDrop());
    public static LogItemInfo AMULET_OF_GLORY_T4_10354 = new LogItemInfo("Amulet of glory (t4)", 10354,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo AMULET_OF_MAGIC_T_10366 = new LogItemInfo("Amulet of magic (t)", 10366,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 360, 3)));
    public static LogItemInfo AMULET_OF_POWER_T_23354 = new LogItemInfo("Amulet of power (t)", 23354,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo AMULET_OF_THE_DAMNED_FULL_12851 = new LogItemInfo("Amulet of the damned (full)", 12851,
            new MissingKillCountDrop());
    public static LogItemInfo AMYS_SAW_24880 = new LogItemInfo("Amy's saw", 24880,
            new DeterministicDrop());
    public static LogItemInfo ANCESTRAL_HAT_21018 = new LogItemInfo("Ancestral hat", 21018,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 23),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 23)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    public static LogItemInfo ANCESTRAL_ROBE_BOTTOM_21024 = new LogItemInfo("Ancestral robe bottom", 21024,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 23),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 23)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    public static LogItemInfo ANCESTRAL_ROBE_TOP_21021 = new LogItemInfo("Ancestral robe top", 21021,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 23),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 23)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    public static LogItemInfo ANCIENT_ASTROSCOPE_25690 = new LogItemInfo("Ancient astroscope", 25690,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_BLESSING_20235 = new LogItemInfo("Ancient blessing", 20235,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 606.4, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 645.8, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 541.7, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 682, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2160, 3)
            )));
    public static LogItemInfo ANCIENT_BRACERS_12490 = new LogItemInfo("Ancient bracers", 12490,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_CARCANET_25694 = new LogItemInfo("Ancient carcanet", 25694,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_CEREMONIAL_BOOTS_26229 = new LogItemInfo("Ancient ceremonial boots", 26229,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_CEREMONIAL_GLOVES_26227 = new LogItemInfo("Ancient ceremonial gloves", 26227,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_CEREMONIAL_LEGS_26223 = new LogItemInfo("Ancient ceremonial legs", 26223,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_CEREMONIAL_MASK_26225 = new LogItemInfo("Ancient ceremonial mask", 26225,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_CEREMONIAL_TOP_26221 = new LogItemInfo("Ancient ceremonial top", 26221,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_CHAPS_12494 = new LogItemInfo("Ancient chaps", 12494,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_CLOAK_12197 = new LogItemInfo("Ancient cloak", 12197,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ANCIENT_COIF_12496 = new LogItemInfo("Ancient coif", 12496,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_CROZIER_12199 = new LogItemInfo("Ancient crozier", 12199,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ANCIENT_CRYSTAL_21804 = new LogItemInfo("Ancient crystal", 21804,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_DHIDE_BODY_12492 = new LogItemInfo("Ancient d'hide body", 12492,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_DHIDE_BOOTS_19921 = new LogItemInfo("Ancient d'hide boots", 19921,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_DHIDE_SHIELD_23197 = new LogItemInfo("Ancient d'hide shield", 23197,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 9750, 5)));
    public static LogItemInfo ANCIENT_EFFIGY_22302 = new LogItemInfo("Ancient effigy", 22302,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_EMBLEM_21807 = new LogItemInfo("Ancient emblem", 21807,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_ESSENCE_27616 = new LogItemInfo("Ancient essence", 27616,
            new CappedCollectionLogTrackingDrop(65535));
    public static LogItemInfo ANCIENT_FULL_HELM_12466 = new LogItemInfo("Ancient full helm", 12466,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_GLOBE_25686 = new LogItemInfo("Ancient globe", 25686,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_HALO_24201 = new LogItemInfo("Ancient halo", 24201,
            new DeterministicDrop());
    public static LogItemInfo ANCIENT_HILT_26370 = new LogItemInfo("Ancient hilt", 26370,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.NEX_KILLS, 1.0 / 516))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NEX_REWARDS_FRACTION_KEY)
    );
    public static LogItemInfo ANCIENT_ICON_27627 = new LogItemInfo("Ancient icon", 27627,
            // includes chance from frozen cache(s)
            new BinomialDrop(new RollInfo(LogItemSourceInfo.PHANTOM_MUSPAH_KILLS, 1.0 / 50 + 1.0 / 24.8 / 250)));
    public static LogItemInfo ANCIENT_KITESHIELD_12468 = new LogItemInfo("Ancient kiteshield", 12468,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_LEDGER_25688 = new LogItemInfo("Ancient ledger", 25688,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_MEDALLION_22299 = new LogItemInfo("Ancient medallion", 22299,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_MITRE_12203 = new LogItemInfo("Ancient mitre", 12203,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ANCIENT_NOTE_21668 = new LogItemInfo("Ancient note", 21668,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11341 = new LogItemInfo("Ancient page", 11341,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11342 = new LogItemInfo("Ancient page", 11342,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11343 = new LogItemInfo("Ancient page", 11343,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11344 = new LogItemInfo("Ancient page", 11344,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11345 = new LogItemInfo("Ancient page", 11345,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11346 = new LogItemInfo("Ancient page", 11346,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11347 = new LogItemInfo("Ancient page", 11347,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11348 = new LogItemInfo("Ancient page", 11348,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11349 = new LogItemInfo("Ancient page", 11349,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11350 = new LogItemInfo("Ancient page", 11350,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11351 = new LogItemInfo("Ancient page", 11351,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11352 = new LogItemInfo("Ancient page", 11352,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11353 = new LogItemInfo("Ancient page", 11353,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11354 = new LogItemInfo("Ancient page", 11354,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11355 = new LogItemInfo("Ancient page", 11355,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11356 = new LogItemInfo("Ancient page", 11356,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11357 = new LogItemInfo("Ancient page", 11357,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11358 = new LogItemInfo("Ancient page", 11358,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11359 = new LogItemInfo("Ancient page", 11359,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11360 = new LogItemInfo("Ancient page", 11360,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11361 = new LogItemInfo("Ancient page", 11361,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11362 = new LogItemInfo("Ancient page", 11362,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11363 = new LogItemInfo("Ancient page", 11363,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11364 = new LogItemInfo("Ancient page", 11364,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11365 = new LogItemInfo("Ancient page", 11365,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_11366 = new LogItemInfo("Ancient page", 11366,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_PAGE_1_12621 = new LogItemInfo("Ancient page 1", 12621,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ANCIENT_PAGE_2_12622 = new LogItemInfo("Ancient page 2", 12622,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ANCIENT_PAGE_3_12623 = new LogItemInfo("Ancient page 3", 12623,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ANCIENT_PAGE_4_12624 = new LogItemInfo("Ancient page 4", 12624,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ANCIENT_PLATEBODY_12460 = new LogItemInfo("Ancient platebody", 12460,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_PLATELEGS_12462 = new LogItemInfo("Ancient platelegs", 12462,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_PLATESKIRT_12464 = new LogItemInfo("Ancient plateskirt", 12464,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ANCIENT_RELIC_22305 = new LogItemInfo("Ancient relic", 22305,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_REMNANT_27381 = new LogItemInfo("Ancient remnant", 27381,
            new DeterministicDrop());
    public static LogItemInfo ANCIENT_ROBE_LEGS_12195 = new LogItemInfo("Ancient robe legs", 12195,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo ANCIENT_ROBE_TOP_12193 = new LogItemInfo("Ancient robe top", 12193,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo ANCIENT_SHARD_19677 = new LogItemInfo("Ancient shard", 19677,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_STATUETTE_21813 = new LogItemInfo("Ancient statuette", 21813,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_STOLE_12201 = new LogItemInfo("Ancient stole", 12201,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ANCIENT_TOTEM_21810 = new LogItemInfo("Ancient totem", 21810,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_TREATISE_25692 = new LogItemInfo("Ancient treatise", 25692,
            new MissingKillCountDrop());
    public static LogItemInfo ANCIENT_WRITINGS_21670 = new LogItemInfo("Ancient writings", 21670,
            new MissingKillCountDrop());
    public static LogItemInfo ANGLER_BOOTS_13261 = new LogItemInfo("Angler boots", 13261,
            new DeterministicDrop());
    public static LogItemInfo ANGLER_HAT_13258 = new LogItemInfo("Angler hat", 13258,
            new DeterministicDrop());
    public static LogItemInfo ANGLER_TOP_13259 = new LogItemInfo("Angler top", 13259,
            new DeterministicDrop());
    public static LogItemInfo ANGLER_WADERS_13260 = new LogItemInfo("Angler waders", 13260,
            new DeterministicDrop());
    public static LogItemInfo ANGUISH_ORNAMENT_KIT_22246 = new LogItemInfo("Anguish ornament kit", 22246,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo ANKOUS_LEGGINGS_20104 = new LogItemInfo("Ankou's leggings", 20104,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo ANKOU_GLOVES_20101 = new LogItemInfo("Ankou gloves", 20101,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo ANKOU_MASK_20095 = new LogItemInfo("Ankou mask", 20095,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo ANKOU_SOCKS_20107 = new LogItemInfo("Ankou socks", 20107,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo ANKOU_TOP_20098 = new LogItemInfo("Ankou top", 20098,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo APPRENTICE_WAND_6910 = new LogItemInfo("Apprentice wand", 6910,
            new DeterministicDrop());
    public static LogItemInfo ARCANE_PRAYER_SCROLL_21079 = new LogItemInfo("Arcane prayer scroll", 21079,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 3.45),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 3.45)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    public static LogItemInfo ARCANE_SIGIL_12827 = new LogItemInfo("Arcane sigil", 12827,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CORPOREAL_BEAST_KILLS, 1.0 / 1365)));
    public static LogItemInfo ARCEUUS_BANNER_20251 = new LogItemInfo("Arceuus banner", 20251,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ARCEUUS_HOOD_20113 = new LogItemInfo("Arceuus hood", 20113,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo ARCEUUS_SCARF_19943 = new LogItemInfo("Arceuus scarf", 19943,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo ARCHERS_RING_6733 = new LogItemInfo("Archers ring", 6733,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DAGANNOTH_SUPREME_KILLS, 1.0 / 128)));
    public static LogItemInfo ARMADYL_BRACERS_12506 = new LogItemInfo("Armadyl bracers", 12506,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    // Note: Add 3 minion kills per kc.
    public static LogItemInfo ARMADYL_CHAINSKIRT_11830 = new LogItemInfo("Armadyl chainskirt", 11830,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KREEARRA_KILLS, 1.0 / 381 + 3.0 / 16129)));
    public static LogItemInfo ARMADYL_CHAPS_12510 = new LogItemInfo("Armadyl chaps", 12510,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    // Note: Add 3 minion kills per kc.
    public static LogItemInfo ARMADYL_CHESTPLATE_11828 = new LogItemInfo("Armadyl chestplate", 11828,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KREEARRA_KILLS, 1.0 / 381 + 3.0 / 16129)));
    public static LogItemInfo ARMADYL_CLOAK_12261 = new LogItemInfo("Armadyl cloak", 12261,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ARMADYL_COIF_12512 = new LogItemInfo("Armadyl coif", 12512,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ARMADYL_CROSSBOW_11785 = new LogItemInfo("Armadyl crossbow", 11785,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.COMMANDER_ZILYANA_KILLS, 1.0 / 508)));
    public static LogItemInfo ARMADYL_CROZIER_12263 = new LogItemInfo("Armadyl crozier", 12263,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ARMADYL_DHIDE_BODY_12508 = new LogItemInfo("Armadyl d'hide body", 12508,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ARMADYL_DHIDE_BOOTS_19930 = new LogItemInfo("Armadyl d'hide boots", 19930,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ARMADYL_DHIDE_SHIELD_23200 = new LogItemInfo("Armadyl d'hide shield", 23200,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 9750, 5)));
    public static LogItemInfo ARMADYL_FULL_HELM_12476 = new LogItemInfo("Armadyl full helm", 12476,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ARMADYL_GODSWORD_ORNAMENT_KIT_20068 = new LogItemInfo("Armadyl godsword ornament kit", 20068,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 3404, 6)));
    public static LogItemInfo ARMADYL_HALO_24192 = new LogItemInfo("Armadyl halo", 24192,
            new DeterministicDrop());
    // Note: Add 3 minion kills per kc, and ignore the 1 in 1M clue guard drop chance.
    public static LogItemInfo ARMADYL_HELMET_11826 = new LogItemInfo("Armadyl helmet", 11826,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KREEARRA_KILLS, 1.0 / 381 + 3.0 / 16129)));
    public static LogItemInfo ARMADYL_HILT_11810 = new LogItemInfo("Armadyl hilt", 11810,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KREEARRA_KILLS, 1.0 / 508)));
    public static LogItemInfo ARMADYL_KITESHIELD_12478 = new LogItemInfo("Armadyl kiteshield", 12478,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ARMADYL_MITRE_12259 = new LogItemInfo("Armadyl mitre", 12259,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ARMADYL_PAGE_1_12617 = new LogItemInfo("Armadyl page 1", 12617,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ARMADYL_PAGE_2_12618 = new LogItemInfo("Armadyl page 2", 12618,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ARMADYL_PAGE_3_12619 = new LogItemInfo("Armadyl page 3", 12619,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ARMADYL_PAGE_4_12620 = new LogItemInfo("Armadyl page 4", 12620,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ARMADYL_PLATEBODY_12470 = new LogItemInfo("Armadyl platebody", 12470,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ARMADYL_PLATELEGS_12472 = new LogItemInfo("Armadyl platelegs", 12472,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ARMADYL_PLATESKIRT_12474 = new LogItemInfo("Armadyl plateskirt", 12474,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ARMADYL_ROBE_LEGS_12255 = new LogItemInfo("Armadyl robe legs", 12255,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo ARMADYL_ROBE_TOP_12253 = new LogItemInfo("Armadyl robe top", 12253,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo ARMADYL_STOLE_12257 = new LogItemInfo("Armadyl stole", 12257,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ASH_COVERED_TOME_21697 = new LogItemInfo("Ash covered tome", 21697,
            new DeterministicDrop());
    public static LogItemInfo ATTAS_SEED_22881 = new LogItemInfo("Attas seed", 22881,
            new BinomialUniformSumDrop(
                    new RollInfo(LogItemSourceInfo.HESPORI_KILLS, 1.0 / 3),
                    1, 2
            ));
    public static LogItemInfo AVERNIC_DEFENDER_HILT_22477 = new LogItemInfo("Avernic defender hilt", 22477,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_COMPLETIONS, 1.0 / 21.61),
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 19.8)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_HM_POINTS_KEY)
    );
    public static LogItemInfo AWAKENERS_ORB_28334 = new LogItemInfo("Awakener's orb", 28334,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 1.0 / 34.5),
                    new RollInfo(LogItemSourceInfo.DUKE_SUCELLUS_KILLS, 1.0 / 48.5),
                    new RollInfo(LogItemSourceInfo.LEVIATHAN_KILLS, 1.0 / 53.6),
                    new RollInfo(LogItemSourceInfo.VARDORVIS_KILLS, 1.0 / 80)
            )));
    public static LogItemInfo A_POWDERED_WIG_10392 = new LogItemInfo("A powdered wig", 10392,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BABY_CHINCHOMPA_13324 = new LogItemInfo("Baby chinchompa", 13324,
            new MissingKillCountDrop());
    public static LogItemInfo BABY_MOLE_12646 = new LogItemInfo("Baby mole", 12646,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GIANT_MOLE_KILLS, 1.0 / 3000)));
    public static LogItemInfo BALLISTA_LIMBS_19592 = new LogItemInfo("Ballista limbs", 19592,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.DEMONIC_GORILLA_KILLS, 1.0 / 500),
                    new RollInfo(LogItemSourceInfo.TORTURED_GORILLA_KILLS, 1.0 / 5000)
            )));
    public static LogItemInfo BALLISTA_SPRING_19601 = new LogItemInfo("Ballista spring", 19601,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.DEMONIC_GORILLA_KILLS, 1.0 / 500),
                    new RollInfo(LogItemSourceInfo.TORTURED_GORILLA_KILLS, 1.0 / 5000)
            )));
    // Note: Add 3 minion kills per kc, and ignore the 1 in 1M clue guard drop chance.
    public static LogItemInfo BANDOS_BOOTS_11836 = new LogItemInfo("Bandos boots", 11836,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GENERAL_GRAARDOR_KILLS, 1.0 / 381 + 3.0 / 16256)));
    public static LogItemInfo BANDOS_BRACERS_12498 = new LogItemInfo("Bandos bracers", 12498,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BANDOS_CHAPS_12502 = new LogItemInfo("Bandos chaps", 12502,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    // Note: Add 3 minion kills per kc.
    public static LogItemInfo BANDOS_CHESTPLATE_11832 = new LogItemInfo("Bandos chestplate", 11832,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GENERAL_GRAARDOR_KILLS, 1.0 / 381 + 3.0 / 16256)));
    public static LogItemInfo BANDOS_CLOAK_12273 = new LogItemInfo("Bandos cloak", 12273,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo BANDOS_COIF_12504 = new LogItemInfo("Bandos coif", 12504,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BANDOS_CROZIER_12275 = new LogItemInfo("Bandos crozier", 12275,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo BANDOS_DHIDE_BODY_12500 = new LogItemInfo("Bandos d'hide body", 12500,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BANDOS_DHIDE_BOOTS_19924 = new LogItemInfo("Bandos d'hide boots", 19924,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BANDOS_DHIDE_SHIELD_23203 = new LogItemInfo("Bandos d'hide shield", 23203,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 9750, 5)));
    public static LogItemInfo BANDOS_FULL_HELM_12486 = new LogItemInfo("Bandos full helm", 12486,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BANDOS_GODSWORD_ORNAMENT_KIT_20071 = new LogItemInfo("Bandos godsword ornament kit", 20071,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 3404, 6)));
    public static LogItemInfo BANDOS_HALO_24195 = new LogItemInfo("Bandos halo", 24195,
            new DeterministicDrop());
    public static LogItemInfo BANDOS_HILT_11812 = new LogItemInfo("Bandos hilt", 11812,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GENERAL_GRAARDOR_KILLS, 1.0 / 508)));
    public static LogItemInfo BANDOS_KITESHIELD_12488 = new LogItemInfo("Bandos kiteshield", 12488,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BANDOS_MITRE_12271 = new LogItemInfo("Bandos mitre", 12271,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo BANDOS_PAGE_1_12613 = new LogItemInfo("Bandos page 1", 12613,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo BANDOS_PAGE_2_12614 = new LogItemInfo("Bandos page 2", 12614,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo BANDOS_PAGE_3_12615 = new LogItemInfo("Bandos page 3", 12615,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo BANDOS_PAGE_4_12616 = new LogItemInfo("Bandos page 4", 12616,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo BANDOS_PLATEBODY_12480 = new LogItemInfo("Bandos platebody", 12480,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BANDOS_PLATELEGS_12482 = new LogItemInfo("Bandos platelegs", 12482,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BANDOS_PLATESKIRT_12484 = new LogItemInfo("Bandos plateskirt", 12484,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BANDOS_ROBE_LEGS_12267 = new LogItemInfo("Bandos robe legs", 12267,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BANDOS_ROBE_TOP_12265 = new LogItemInfo("Bandos robe top", 12265,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BANDOS_STOLE_12269 = new LogItemInfo("Bandos stole", 12269,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    // Note: Add 3 minion kills per kc.
    public static LogItemInfo BANDOS_TASSETS_11834 = new LogItemInfo("Bandos tassets", 11834,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GENERAL_GRAARDOR_KILLS, 1.0 / 381 + 3.0 / 16256)));
    public static LogItemInfo BARON_28250 = new LogItemInfo("Baron", 28250,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DUKE_SUCELLUS_KILLS, 1.0 / 2500)));
    public static LogItemInfo BARRONITE_GUARD_25639 = new LogItemInfo("Barronite guard", 25639,
            new MissingKillCountDrop());
    public static LogItemInfo BARRONITE_HANDLE_25637 = new LogItemInfo("Barronite handle", 25637,
            new MissingKillCountDrop());
    public static LogItemInfo BARRONITE_HEAD_25635 = new LogItemInfo("Barronite head", 25635,
            new MissingKillCountDrop());
    public static LogItemInfo BARRONITE_MACE_25641 = new LogItemInfo("Barronite mace", 25641,
            new MissingKillCountDrop());
    public static LogItemInfo BASILISK_HEAD_7977 = new LogItemInfo("Basilisk head", 7977,
            new MissingKillCountDrop());
    public static LogItemInfo BASILISK_JAW_24268 = new LogItemInfo("Basilisk jaw", 24268,
            new MissingKillCountDrop());
    public static LogItemInfo BEANIE_12245 = new LogItemInfo("Beanie", 12245,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BEAR_FEET_23291 = new LogItemInfo("Bear feet", 23291,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo BEAVER_13322 = new LogItemInfo("Beaver", 13322,
            new MissingKillCountDrop());
    public static LogItemInfo BEEKEEPERS_BOOTS_25137 = new LogItemInfo("Beekeeper's boots", 25137,
            new MissingKillCountDrop());
    public static LogItemInfo BEEKEEPERS_GLOVES_25135 = new LogItemInfo("Beekeeper's gloves", 25135,
            new MissingKillCountDrop());
    public static LogItemInfo BEEKEEPERS_HAT_25129 = new LogItemInfo("Beekeeper's hat", 25129,
            new MissingKillCountDrop());
    public static LogItemInfo BEEKEEPERS_LEGS_25133 = new LogItemInfo("Beekeeper's legs", 25133,
            new MissingKillCountDrop());
    public static LogItemInfo BEEKEEPERS_TOP_25131 = new LogItemInfo("Beekeeper's top", 25131,
            new MissingKillCountDrop());
    public static LogItemInfo BEGINNER_WAND_6908 = new LogItemInfo("Beginner wand", 6908,
            new DeterministicDrop());
    public static LogItemInfo BELLATOR_VESTIGE_28279 = new LogItemInfo("Bellator vestige", 28279,
            new HiddenShardDrop(new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 1.0 / 64.0 * 3.0 / 8.0), 3));
    public static LogItemInfo BERSERKER_NECKLACE_ORNAMENT_KIT_23237 = new LogItemInfo("Berserker necklace ornament kit", 23237,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BERSERKER_RING_6737 = new LogItemInfo("Berserker ring", 6737,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DAGANNOTH_REX_KILLS, 1.0 / 128)));
    public static LogItemInfo BIG_BASS_7989 = new LogItemInfo("Big bass", 7989,
            new MissingKillCountDrop());
    public static LogItemInfo BIG_HARPOONFISH_25559 = new LogItemInfo("Big harpoonfish", 25559,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.REWARD_PERMITS_CLAIMED, 1.0 / 1600)));
    public static LogItemInfo BIG_PIRATE_HAT_12355 = new LogItemInfo("Big pirate hat", 12355,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo BIG_SHARK_7993 = new LogItemInfo("Big shark", 7993,
            new MissingKillCountDrop());
    public static LogItemInfo BIG_SWORDFISH_7991 = new LogItemInfo("Big swordfish", 7991,
            new MissingKillCountDrop());
    public static LogItemInfo BLACKSMITHS_HELM_19988 = new LogItemInfo("Blacksmith's helm", 19988,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo BLACK_BERET_2635 = new LogItemInfo("Black beret", 2635,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_BOATER_7327 = new LogItemInfo("Black boater", 7327,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo BLACK_BOOTS_4125 = new LogItemInfo("Black boots", 4125,
            new MissingKillCountDrop());
    public static LogItemInfo BLACK_CANE_12375 = new LogItemInfo("Black cane", 12375,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_CAVALIER_2643 = new LogItemInfo("Black cavalier", 2643,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BLACK_DEFENDER_8847 = new LogItemInfo("Black defender", 8847,
            new MissingKillCountDrop());
    public static LogItemInfo BLACK_DEMON_MASK_20026 = new LogItemInfo("Black demon mask", 20026,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo BLACK_DHIDE_BODY_G_12381 = new LogItemInfo("Black d'hide body (g)", 12381,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo BLACK_DHIDE_BODY_T_12385 = new LogItemInfo("Black d'hide body (t)", 12385,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo BLACK_DHIDE_CHAPS_G_12383 = new LogItemInfo("Black d'hide chaps (g)", 12383,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo BLACK_DHIDE_CHAPS_T_12387 = new LogItemInfo("Black d'hide chaps (t)", 12387,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo BLACK_DRAGON_MASK_12524 = new LogItemInfo("Black dragon mask", 12524,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BLACK_ELEGANT_LEGS_10402 = new LogItemInfo("Black elegant legs", 10402,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo BLACK_ELEGANT_SHIRT_10400 = new LogItemInfo("Black elegant shirt", 10400,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo BLACK_FULL_HELM_G_2595 = new LogItemInfo("Black full helm (g)", 2595,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_FULL_HELM_T_2587 = new LogItemInfo("Black full helm (t)", 2587,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_HEADBAND_2647 = new LogItemInfo("Black headband", 2647,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo BLACK_HELM_H1_10306 = new LogItemInfo("Black helm (h1)", 10306,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_HELM_H2_10308 = new LogItemInfo("Black helm (h2)", 10308,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_HELM_H3_10310 = new LogItemInfo("Black helm (h3)", 10310,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_HELM_H4_10312 = new LogItemInfo("Black helm (h4)", 10312,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_HELM_H5_10314 = new LogItemInfo("Black helm (h5)", 10314,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_KITESHIELD_G_2597 = new LogItemInfo("Black kiteshield (g)", 2597,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_KITESHIELD_T_2589 = new LogItemInfo("Black kiteshield (t)", 2589,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_LEPRECHAUN_HAT_20246 = new LogItemInfo("Black leprechaun hat", 20246,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo BLACK_LOCKS_25448 = new LogItemInfo("Black locks", 25448,
            new MissingKillCountDrop());
    public static LogItemInfo BLACK_MASK_10_8901 = new LogItemInfo("Black mask (10)", 8901,
            new MissingKillCountDrop());
    public static LogItemInfo BLACK_NAVAL_SHIRT_8956 = new LogItemInfo("Black naval shirt", 8956,
            new DeterministicDrop());
    public static LogItemInfo BLACK_NAVY_SLACKS_8995 = new LogItemInfo("Black navy slacks", 8995,
            new DeterministicDrop());
    public static LogItemInfo BLACK_PICKAXE_12297 = new LogItemInfo("Black pickaxe", 12297,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 36, 3),
                    new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 805.1, 2)
            )));
    public static LogItemInfo BLACK_PLATEBODY_G_2591 = new LogItemInfo("Black platebody (g)", 2591,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATEBODY_H1_23366 = new LogItemInfo("Black platebody (h1)", 23366,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATEBODY_H2_23369 = new LogItemInfo("Black platebody (h2)", 23369,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATEBODY_H3_23372 = new LogItemInfo("Black platebody (h3)", 23372,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATEBODY_H4_23375 = new LogItemInfo("Black platebody (h4)", 23375,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATEBODY_H5_23378 = new LogItemInfo("Black platebody (h5)", 23378,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATEBODY_T_2583 = new LogItemInfo("Black platebody (t)", 2583,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATELEGS_G_2593 = new LogItemInfo("Black platelegs (g)", 2593,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATELEGS_T_2585 = new LogItemInfo("Black platelegs (t)", 2585,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATESKIRT_G_3473 = new LogItemInfo("Black plateskirt (g)", 3473,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_PLATESKIRT_T_3472 = new LogItemInfo("Black plateskirt (t)", 3472,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_SATCHEL_25621 = new LogItemInfo("Black satchel", 25621,
            new MissingKillCountDrop());
    public static LogItemInfo BLACK_SHIELD_H1_7332 = new LogItemInfo("Black shield (h1)", 7332,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_SHIELD_H2_7338 = new LogItemInfo("Black shield (h2)", 7338,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_SHIELD_H3_7344 = new LogItemInfo("Black shield (h3)", 7344,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_SHIELD_H4_7350 = new LogItemInfo("Black shield (h4)", 7350,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_SHIELD_H5_7356 = new LogItemInfo("Black shield (h5)", 7356,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_SKIRT_G_12445 = new LogItemInfo("Black skirt (g)", 12445,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_SKIRT_T_12447 = new LogItemInfo("Black skirt (t)", 12447,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_TOURMALINE_CORE_21730 = new LogItemInfo("Black tourmaline core", 21730,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GROTESQUE_GUARDIAN_KILLS, 1.0 / 1000, 2)));
    public static LogItemInfo BLACK_TRICORN_HAT_8963 = new LogItemInfo("Black tricorn hat", 8963,
            new DeterministicDrop());
    public static LogItemInfo BLACK_UNICORN_MASK_20266 = new LogItemInfo("Black unicorn mask", 20266,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo BLACK_WIZARD_HAT_G_12453 = new LogItemInfo("Black wizard hat (g)", 12453,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_WIZARD_HAT_T_12455 = new LogItemInfo("Black wizard hat (t)", 12455,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_WIZARD_ROBE_G_12449 = new LogItemInfo("Black wizard robe (g)", 12449,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLACK_WIZARD_ROBE_T_12451 = new LogItemInfo("Black wizard robe (t)", 12451,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLOODHOUND_19730 = new LogItemInfo("Bloodhound", 19730,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 1000)));
    public static LogItemInfo BLOODY_NOTES_25476 = new LogItemInfo("Bloody notes", 25476,
            new MissingKillCountDrop());
    public static LogItemInfo BLOOD_QUARTZ_28268 = new LogItemInfo("Blood quartz", 28268,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.VARDORVIS_KILLS, 135.0 / 136.0 * 79.0 / 80.0 * 24.0 / 25.0 / 200)));
    public static LogItemInfo BLOOD_SHARD_24777 = new LogItemInfo("Blood shard", 24777,
            new MissingKillCountDrop());
    public static LogItemInfo BLUDGEON_AXON_13276 = new LogItemInfo("Bludgeon axon", 13276,
            new DupeProtectedSetBinomialDrop(
                    // unsired chance, with 1/3rd of the bludgeon drop table rate.
                    new RollInfo(LogItemSourceInfo.ABYSSAL_SIRE_KILLS, 1.0 / 100.0 * 62.0 / 128.0 / 3.0),
                    ImmutableList.of(13276, 13275, 13274)
            ));
    public static LogItemInfo BLUDGEON_CLAW_13275 = new LogItemInfo("Bludgeon claw", 13275,
            new DupeProtectedSetBinomialDrop(
                    // unsired chance, with 1/3rd of the bludgeon drop table rate.
                    new RollInfo(LogItemSourceInfo.ABYSSAL_SIRE_KILLS, 1.0 / 100.0 * 62.0 / 128.0 / 3.0),
                    ImmutableList.of(13276, 13275, 13274)
            ));
    public static LogItemInfo BLUDGEON_SPINE_13274 = new LogItemInfo("Bludgeon spine", 13274,
            new DupeProtectedSetBinomialDrop(
                    // unsired chance, with 1/3rd of the bludgeon drop table rate.
                    new RollInfo(LogItemSourceInfo.ABYSSAL_SIRE_KILLS, 1.0 / 100.0 * 62.0 / 128.0 / 3.0),
                    ImmutableList.of(13276, 13275, 13274)
            ));
    public static LogItemInfo BLUE_BERET_2633 = new LogItemInfo("Blue beret", 2633,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLUE_BOATER_7325 = new LogItemInfo("Blue boater", 7325,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo BLUE_DARK_BOW_PAINT_12757 = new LogItemInfo("Blue dark bow paint", 12757,
            new DeterministicDrop());
    public static LogItemInfo BLUE_DHIDE_BODY_G_7374 = new LogItemInfo("Blue d'hide body (g)", 7374,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BLUE_DHIDE_BODY_T_7376 = new LogItemInfo("Blue d'hide body (t)", 7376,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BLUE_DHIDE_CHAPS_G_7382 = new LogItemInfo("Blue d'hide chaps (g)", 7382,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BLUE_DHIDE_CHAPS_T_7384 = new LogItemInfo("Blue d'hide chaps (t)", 7384,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BLUE_DRAGON_MASK_12520 = new LogItemInfo("Blue dragon mask", 12520,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo BLUE_EGG_SAC_25846 = new LogItemInfo("Blue egg sac", 25846,
            new MissingKillCountDrop());
    public static LogItemInfo BLUE_ELEGANT_BLOUSE_10428 = new LogItemInfo("Blue elegant blouse", 10428,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo BLUE_ELEGANT_LEGS_10410 = new LogItemInfo("Blue elegant legs", 10410,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo BLUE_ELEGANT_SHIRT_10408 = new LogItemInfo("Blue elegant shirt", 10408,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo BLUE_ELEGANT_SKIRT_10430 = new LogItemInfo("Blue elegant skirt", 10430,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo BLUE_FIRELIGHTER_7331 = new LogItemInfo("Blue firelighter", 7331,
            new PoissonBinomialStackDrop());
    public static LogItemInfo BLUE_HEADBAND_12301 = new LogItemInfo("Blue headband", 12301,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo BLUE_NAVAL_SHIRT_8952 = new LogItemInfo("Blue naval shirt", 8952,
            new DeterministicDrop());
    public static LogItemInfo BLUE_NAVY_SLACKS_8991 = new LogItemInfo("Blue navy slacks", 8991,
            new DeterministicDrop());
    public static LogItemInfo BLUE_SKIRT_G_7386 = new LogItemInfo("Blue skirt (g)", 7386,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLUE_SKIRT_T_7388 = new LogItemInfo("Blue skirt (t)", 7388,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLUE_TRICORN_HAT_8959 = new LogItemInfo("Blue tricorn hat", 8959,
            new DeterministicDrop());
    public static LogItemInfo BLUE_WIZARD_HAT_G_7394 = new LogItemInfo("Blue wizard hat (g)", 7394,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLUE_WIZARD_HAT_T_7396 = new LogItemInfo("Blue wizard hat (t)", 7396,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLUE_WIZARD_ROBE_G_7390 = new LogItemInfo("Blue wizard robe (g)", 7390,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BLUE_WIZARD_ROBE_T_7392 = new LogItemInfo("Blue wizard robe (t)", 7392,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BOBS_BLACK_SHIRT_10322 = new LogItemInfo("Bob's black shirt", 10322,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BOBS_BLUE_SHIRT_10318 = new LogItemInfo("Bob's blue shirt", 10318,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BOBS_GREEN_SHIRT_10320 = new LogItemInfo("Bob's green shirt", 10320,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BOBS_PURPLE_SHIRT_10324 = new LogItemInfo("Bob's purple shirt", 10324,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BOBS_RED_SHIRT_10316 = new LogItemInfo("Bob's red shirt", 10316,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BOLT_RACK_4740 = new LogItemInfo("Bolt rack", 4740,
            new BinomialUniformSumDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 8.096, 7),
                    35, 40)
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY)
                    .withConfigOption(CollectionLogLuckConfig.BARROWS_BOLT_RACKS_ENABLED_KEY));
    public static LogItemInfo BONES_TO_PEACHES_6926 = new LogItemInfo("Bones to peaches", 6926,
            new DeterministicDrop());
    public static LogItemInfo BOOTS_OF_DARKNESS_20140 = new LogItemInfo("Boots of darkness", 20140,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo BOOTS_OF_THE_EYE_26856 = new LogItemInfo("Boots of the eye", 26856,
            new DeterministicDrop());
    public static LogItemInfo BOTTOMLESS_COMPOST_BUCKET_22994 = new LogItemInfo("Bottomless compost bucket", 22994,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HESPORI_KILLS, 1.0 / 35)));
    public static LogItemInfo BOTTOM_OF_SCEPTRE_9011 = new LogItemInfo("Bottom of sceptre", 9011,
            new MissingKillCountDrop());
    public static LogItemInfo BOWL_WIG_20110 = new LogItemInfo("Bowl wig", 20110,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo BRACELET_OF_ETHEREUM_UNCHARGED_21817 = new LogItemInfo("Bracelet of ethereum (uncharged)", 21817,
            new MissingKillCountDrop());
    public static LogItemInfo BRASSICA_HALO_24204 = new LogItemInfo("Brassica halo", 24204,
            new DeterministicDrop());
    public static LogItemInfo BREACH_OF_THE_SCARAB_27283 = new LogItemInfo("Breach of the scarab", 27283,
            new UnimplementedDrop());
    public static LogItemInfo BRIEFCASE_12335 = new LogItemInfo("Briefcase", 12335,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo BRINE_SABRE_11037 = new LogItemInfo("Brine sabre", 11037,
            new MissingKillCountDrop());
    public static LogItemInfo BROKEN_DRAGON_HASTA_22963 = new LogItemInfo("Broken dragon hasta", 22963,
            new MissingKillCountDrop());
    public static LogItemInfo BRONZE_BOOTS_4119 = new LogItemInfo("Bronze boots", 4119,
            new MissingKillCountDrop());
    public static LogItemInfo BRONZE_DEFENDER_8844 = new LogItemInfo("Bronze defender", 8844,
            new MissingKillCountDrop());
    public static LogItemInfo BRONZE_DRAGON_MASK_12363 = new LogItemInfo("Bronze dragon mask", 12363,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo BRONZE_FIST_FLAG_8968 = new LogItemInfo("Bronze fist flag", 8968,
            new DeterministicDrop());
    public static LogItemInfo BRONZE_FULL_HELM_G_12211 = new LogItemInfo("Bronze full helm (g)", 12211,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BRONZE_FULL_HELM_T_12221 = new LogItemInfo("Bronze full helm (t)", 12221,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BRONZE_KITESHIELD_G_12213 = new LogItemInfo("Bronze kiteshield (g)", 12213,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BRONZE_KITESHIELD_T_12223 = new LogItemInfo("Bronze kiteshield (t)", 12223,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BRONZE_LOCKS_25442 = new LogItemInfo("Bronze locks", 25442,
            new MissingKillCountDrop());
    public static LogItemInfo BRONZE_PLATEBODY_G_12205 = new LogItemInfo("Bronze platebody (g)", 12205,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BRONZE_PLATEBODY_T_12215 = new LogItemInfo("Bronze platebody (t)", 12215,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BRONZE_PLATELEGS_G_12207 = new LogItemInfo("Bronze platelegs (g)", 12207,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BRONZE_PLATELEGS_T_12217 = new LogItemInfo("Bronze platelegs (t)", 12217,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BRONZE_PLATESKIRT_G_12209 = new LogItemInfo("Bronze plateskirt (g)", 12209,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BRONZE_PLATESKIRT_T_12219 = new LogItemInfo("Bronze plateskirt (t)", 12219,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo BROWN_HEADBAND_2649 = new LogItemInfo("Brown headband", 2649,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo BROWN_NAVAL_SHIRT_8955 = new LogItemInfo("Brown naval shirt", 8955,
            new DeterministicDrop());
    public static LogItemInfo BROWN_NAVY_SLACKS_8994 = new LogItemInfo("Brown navy slacks", 8994,
            new DeterministicDrop());
    public static LogItemInfo BROWN_TRICORN_HAT_8962 = new LogItemInfo("Brown tricorn hat", 8962,
            new DeterministicDrop());
    public static LogItemInfo BRUMA_TORCH_20720 = new LogItemInfo("Bruma torch", 20720,
            // Note: This ignores the mechanic where torstol seeds are dropped instead if the player has > 3 torches.
            // Users will probably understand why their luck is lower than expected if they've purposely kept 3+...
            new BinomialDrop(new RollInfo(LogItemSourceInfo.WINTERTODT_KILLS, 1.0 / 151.2))
                    .withConfigOption(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY)
    );
    public static LogItemInfo BRYOPHYTAS_ESSENCE_22372 = new LogItemInfo("Bryophyta's essence", 22372,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BRYOPHYTA_KILLS, 1.0 / 118)));
    public static LogItemInfo BUCKET_HELM_19991 = new LogItemInfo("Bucket helm", 19991,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo BUCKET_HELM_G_20059 = new LogItemInfo("Bucket helm (g)", 20059,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13616, 6)));
    public static LogItemInfo BURNT_PAGE_20718 = new LogItemInfo("Burnt page", 20718,
            new CappedCollectionLogTrackingDrop(250));
    public static LogItemInfo BUTCH_28248 = new LogItemInfo("Butch", 28248,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.VARDORVIS_KILLS, 1.0 / 3000)));
    public static LogItemInfo CABBAGE_ROUND_SHIELD_20272 = new LogItemInfo("Cabbage round shield", 20272,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    // TODO: Technically, the rate of uniques should lower the effective drop rate. Low priority to fix.
    public static LogItemInfo CACHE_OF_RUNES_27293 = new LogItemInfo("Cache of runes", 27293,
            new BinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS, 1.0 / 27, 3),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS, 1.0 / 27, 3),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS, 1.0 / 27, 3)
            )));
    public static LogItemInfo CALLISTO_CUB_13178 = new LogItemInfo("Callisto cub", 13178,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CALLISTO_KILLS, 1.0 / 2000),
                    new RollInfo(LogItemSourceInfo.ARTIO_KILLS, 1.0 / 2800)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_CALLISTO_REWARDS_FRACTION_KEY));
    public static LogItemInfo CAMO_BOTTOMS_6655 = new LogItemInfo("Camo bottoms", 6655,
            new MissingKillCountDrop());
    public static LogItemInfo CAMO_HELMET_6656 = new LogItemInfo("Camo helmet", 6656,
            new MissingKillCountDrop());
    public static LogItemInfo CAMO_TOP_6654 = new LogItemInfo("Camo top", 6654,
            new MissingKillCountDrop());
    public static LogItemInfo CAPE_OF_SKULLS_23351 = new LogItemInfo("Cape of skulls", 23351,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 5616, 3)));
    public static LogItemInfo CARPENTERS_BOOTS_24878 = new LogItemInfo("Carpenter's boots", 24878,
            new DeterministicDrop());
    public static LogItemInfo CARPENTERS_HELMET_24872 = new LogItemInfo("Carpenter's helmet", 24872,
            new DeterministicDrop());
    public static LogItemInfo CARPENTERS_SHIRT_24874 = new LogItemInfo("Carpenter's shirt", 24874,
            new DeterministicDrop());
    public static LogItemInfo CARPENTERS_TROUSERS_24876 = new LogItemInfo("Carpenter's trousers", 24876,
            new DeterministicDrop());
    public static LogItemInfo CASTLEWARS_CLOAK_4514 = new LogItemInfo("Castlewars cloak", 4514,
            new DeterministicDrop());
    public static LogItemInfo CASTLEWARS_CLOAK_4516 = new LogItemInfo("Castlewars cloak", 4516,
            new DeterministicDrop());
    public static LogItemInfo CASTLEWARS_HOOD_4513 = new LogItemInfo("Castlewars hood", 4513,
            new DeterministicDrop());
    public static LogItemInfo CASTLEWARS_HOOD_4515 = new LogItemInfo("Castlewars hood", 4515,
            new DeterministicDrop());
    public static LogItemInfo CATALYTIC_TALISMAN_26798 = new LogItemInfo("Catalytic talisman", 26798,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 200)));
    public static LogItemInfo CAT_MASK_12361 = new LogItemInfo("Cat mask", 12361,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo CELESTIAL_RING_UNCHARGED_25539 = new LogItemInfo("Celestial ring (uncharged)", 25539,
            new DeterministicDrop());
    public static LogItemInfo CHAMPIONS_CAPE_21439 = new LogItemInfo("Champion's cape", 21439,
            new MissingKillCountDrop());
    public static LogItemInfo CHARGED_ICE_27643 = new LogItemInfo("Charged ice", 27643,
            new DeterministicDrop());
    public static LogItemInfo CHARGE_DRAGONSTONE_JEWELLERY_SCROLL_20238 = new LogItemInfo("Charge dragonstone jewellery scroll", 20238,
            new PoissonBinomialStackDrop());
    public static LogItemInfo CHEWED_BONES_11338 = new LogItemInfo("Chewed bones", 11338,
            new MissingKillCountDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2978 = new LogItemInfo("Chompy bird hat", 2978,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2979 = new LogItemInfo("Chompy bird hat", 2979,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2980 = new LogItemInfo("Chompy bird hat", 2980,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2981 = new LogItemInfo("Chompy bird hat", 2981,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2982 = new LogItemInfo("Chompy bird hat", 2982,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2983 = new LogItemInfo("Chompy bird hat", 2983,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2984 = new LogItemInfo("Chompy bird hat", 2984,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2985 = new LogItemInfo("Chompy bird hat", 2985,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2986 = new LogItemInfo("Chompy bird hat", 2986,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2987 = new LogItemInfo("Chompy bird hat", 2987,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2988 = new LogItemInfo("Chompy bird hat", 2988,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2989 = new LogItemInfo("Chompy bird hat", 2989,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2990 = new LogItemInfo("Chompy bird hat", 2990,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2991 = new LogItemInfo("Chompy bird hat", 2991,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2992 = new LogItemInfo("Chompy bird hat", 2992,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2993 = new LogItemInfo("Chompy bird hat", 2993,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2994 = new LogItemInfo("Chompy bird hat", 2994,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_BIRD_HAT_2995 = new LogItemInfo("Chompy bird hat", 2995,
            new DeterministicDrop());
    public static LogItemInfo CHOMPY_CHICK_13071 = new LogItemInfo("Chompy chick", 13071,
            new MissingKillCountDrop());
    public static LogItemInfo CHROMIUM_INGOT_28276 = new LogItemInfo("Chromium ingot", 28276,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 1.0 / 170.7),
                    new RollInfo(LogItemSourceInfo.DUKE_SUCELLUS_KILLS, 1.0 / 240),
                    new RollInfo(LogItemSourceInfo.LEVIATHAN_KILLS, 1.0 / 256),
                    new RollInfo(LogItemSourceInfo.VARDORVIS_KILLS, 1.0 / 362.7)
            )));
    public static LogItemInfo CLAWS_OF_CALLISTO_27667 = new LogItemInfo("Claws of callisto", 27667,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CALLISTO_KILLS, 1.0 / 196),
                    new RollInfo(LogItemSourceInfo.ARTIO_KILLS, 1.0 / 618)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_CALLISTO_REWARDS_FRACTION_KEY));
    public static LogItemInfo CLIMBING_BOOTS_G_23413 = new LogItemInfo("Climbing boots (g)", 23413,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo CLOTHES_POUCH_BLUEPRINT_28166 = new LogItemInfo("Clothes pouch blueprint", 28166,
            new DeterministicDrop());
    public static LogItemInfo COAL_BAG_25627 = new LogItemInfo("Coal bag", 25627,
            new DeterministicDrop());
    public static LogItemInfo COCKATRICE_HEAD_7976 = new LogItemInfo("Cockatrice head", 7976,
            new MissingKillCountDrop());
    public static LogItemInfo COLOSSAL_BLADE_27021 = new LogItemInfo("Colossal blade", 27021,
            new DeterministicDrop());
    public static LogItemInfo CRAWLING_HAND_7975 = new LogItemInfo("Crawling hand", 7975,
            new MissingKillCountDrop());
    public static LogItemInfo CRAWS_BOW_U_22547 = new LogItemInfo("Craw's bow (u)", 22547,
            new MissingKillCountDrop());
    public static LogItemInfo CRIER_BELL_20243 = new LogItemInfo("Crier bell", 20243,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo CRIER_COAT_20240 = new LogItemInfo("Crier coat", 20240,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo CRIER_HAT_12319 = new LogItemInfo("Crier hat", 12319,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo CRYSTAL_ARMOUR_SEED_23956 = new LogItemInfo("Crystal armour seed", 23956,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.GAUNTLET_COMPLETION_COUNT, 1.0 / 120),
                    new RollInfo(LogItemSourceInfo.CORRUPTED_GAUNTLET_COMPLETION_COUNT, 1.0 / 50)
            )));
    public static LogItemInfo CRYSTAL_GRAIL_24000 = new LogItemInfo("Crystal grail", 24000,
            new DeterministicDrop());
    public static LogItemInfo CRYSTAL_TOOL_SEED_23953 = new LogItemInfo("Crystal tool seed", 23953,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZALCANO_KILLS, 1.0 / 200))
                    .withConfigOption(CollectionLogLuckConfig.AVG_ZALCANO_REWARDS_FRACTION_KEY)
    );
    // Note: this can be bought from the LMS shop, and it appears to count towards the collection log.
    public static LogItemInfo CRYSTAL_WEAPON_SEED_4207 = new LogItemInfo("Crystal weapon seed", 4207,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.GAUNTLET_COMPLETION_COUNT, 1.0 / 120),
                    new RollInfo(LogItemSourceInfo.CORRUPTED_GAUNTLET_COMPLETION_COUNT, 1.0 / 50)
            )).withConfigOption(CollectionLogLuckConfig.NUM_CRYSTAL_WEAPON_SEEDS_PURCHASED_KEY));
    public static LogItemInfo CURSED_PHALANX_27248 = new LogItemInfo("Cursed phalanx", 27248,
            new DeterministicDrop());
    public static LogItemInfo CURVED_BONE_10977 = new LogItemInfo("Curved bone", 10977,
            new MissingKillCountDrop());
    public static LogItemInfo CUTTHROAT_FLAG_8966 = new LogItemInfo("Cutthroat flag", 8966,
            new DeterministicDrop());
    public static LogItemInfo CYCLOPS_HEAD_19915 = new LogItemInfo("Cyclops head", 19915,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo DAGONHAI_HAT_24288 = new LogItemInfo("Dagon'hai hat", 24288,
            new MissingKillCountDrop());
    public static LogItemInfo DAGONHAI_ROBE_BOTTOM_24294 = new LogItemInfo("Dagon'hai robe bottom", 24294,
            new MissingKillCountDrop());
    public static LogItemInfo DAGONHAI_ROBE_TOP_24291 = new LogItemInfo("Dagon'hai robe top", 24291,
            new MissingKillCountDrop());
    public static LogItemInfo DARK_ACORN_24733 = new LogItemInfo("Dark acorn", 24733,
            new DeterministicDrop());
    public static LogItemInfo DARK_BOW_11235 = new LogItemInfo("Dark bow", 11235,
            new MissingKillCountDrop());
    public static LogItemInfo DARK_BOW_TIE_19970 = new LogItemInfo("Dark bow tie", 19970,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo DARK_CAVALIER_2641 = new LogItemInfo("Dark cavalier", 2641,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo DARK_CLAW_21275 = new LogItemInfo("Dark claw", 21275,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SKOTIZO_KILLS, 1.0 / 25)));
    public static LogItemInfo DARK_DYE_24729 = new LogItemInfo("Dark dye", 24729,
            new DeterministicDrop());
    public static LogItemInfo DARK_INFINITY_COLOUR_KIT_12528 = new LogItemInfo("Dark infinity colour kit", 12528,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo DARK_RELIC_21027 = new LogItemInfo("Dark relic", 21027,
            new BinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 33, 2),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 33, 2)
            )));
    public static LogItemInfo DARK_TOTEM_19685 = new LogItemInfo("Dark totem", 19685,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SKOTIZO_KILLS, 1.0 / 128)));
    public static LogItemInfo DARK_TOTEM_BASE_19679 = new LogItemInfo("Dark totem base", 19679,
            new MissingKillCountDrop());
    public static LogItemInfo DARK_TOTEM_MIDDLE_19681 = new LogItemInfo("Dark totem middle", 19681,
            new MissingKillCountDrop());
    public static LogItemInfo DARK_TOTEM_TOP_19683 = new LogItemInfo("Dark totem top", 19683,
            new MissingKillCountDrop());
    public static LogItemInfo DARK_TROUSERS_19964 = new LogItemInfo("Dark trousers", 19964,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo DARK_TUXEDO_CUFFS_19961 = new LogItemInfo("Dark tuxedo cuffs", 19961,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo DARK_TUXEDO_JACKET_19958 = new LogItemInfo("Dark tuxedo jacket", 19958,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo DARK_TUXEDO_SHOES_19967 = new LogItemInfo("Dark tuxedo shoes", 19967,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo DEADMANS_CAPE_24191 = new LogItemInfo("Deadman's cape", 24191,
            new DeterministicDrop());
    public static LogItemInfo DEADMANS_CHEST_24189 = new LogItemInfo("Deadman's chest", 24189,
            new DeterministicDrop());
    public static LogItemInfo DEADMANS_LEGS_24190 = new LogItemInfo("Deadman's legs", 24190,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_11893 = new LogItemInfo("Decorative armour", 11893,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_11894 = new LogItemInfo("Decorative armour", 11894,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_11895 = new LogItemInfo("Decorative armour", 11895,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_11896 = new LogItemInfo("Decorative armour", 11896,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_11897 = new LogItemInfo("Decorative armour", 11897,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_11898 = new LogItemInfo("Decorative armour", 11898,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_11899 = new LogItemInfo("Decorative armour", 11899,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_11900 = new LogItemInfo("Decorative armour", 11900,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_11901 = new LogItemInfo("Decorative armour", 11901,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_4069 = new LogItemInfo("Decorative armour", 4069,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_4070 = new LogItemInfo("Decorative armour", 4070,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_4504 = new LogItemInfo("Decorative armour", 4504,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_4505 = new LogItemInfo("Decorative armour", 4505,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_4509 = new LogItemInfo("Decorative armour", 4509,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_ARMOUR_4510 = new LogItemInfo("Decorative armour", 4510,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_BOOTS_25163 = new LogItemInfo("Decorative boots", 25163,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_BOOTS_25167 = new LogItemInfo("Decorative boots", 25167,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_BOOTS_25171 = new LogItemInfo("Decorative boots", 25171,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_FULL_HELM_25165 = new LogItemInfo("Decorative full helm", 25165,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_FULL_HELM_25169 = new LogItemInfo("Decorative full helm", 25169,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_FULL_HELM_25174 = new LogItemInfo("Decorative full helm", 25174,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_HELM_4071 = new LogItemInfo("Decorative helm", 4071,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_HELM_4506 = new LogItemInfo("Decorative helm", 4506,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_HELM_4511 = new LogItemInfo("Decorative helm", 4511,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_SHIELD_4072 = new LogItemInfo("Decorative shield", 4072,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_SHIELD_4507 = new LogItemInfo("Decorative shield", 4507,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_SHIELD_4512 = new LogItemInfo("Decorative shield", 4512,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_SWORD_4068 = new LogItemInfo("Decorative sword", 4068,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_SWORD_4503 = new LogItemInfo("Decorative sword", 4503,
            new DeterministicDrop());
    public static LogItemInfo DECORATIVE_SWORD_4508 = new LogItemInfo("Decorative sword", 4508,
            new DeterministicDrop());
    public static LogItemInfo DEERSTALKER_12540 = new LogItemInfo("Deerstalker", 12540,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo DEMON_FEET_23294 = new LogItemInfo("Demon feet", 23294,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo DEXTEROUS_PRAYER_SCROLL_21034 = new LogItemInfo("Dexterous prayer scroll", 21034,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 3.45),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 3.45)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    public static LogItemInfo DHAROKS_GREATAXE_4718 = new LogItemInfo("Dharok's greataxe", 4718,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo DHAROKS_HELM_4716 = new LogItemInfo("Dharok's helm", 4716,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo DHAROKS_PLATEBODY_4720 = new LogItemInfo("Dharok's platebody", 4720,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo DHAROKS_PLATELEGS_4722 = new LogItemInfo("Dharok's platelegs", 4722,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo DIGSITE_TELEPORT_12403 = new LogItemInfo("Digsite teleport", 12403,
            new PoissonBinomialStackDrop());
    public static LogItemInfo DINHS_BULWARK_21015 = new LogItemInfo("Dinh's bulwark", 21015,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 23),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 23)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    public static LogItemInfo DOUBLE_AMMO_MOULD_27012 = new LogItemInfo("Double ammo mould", 27012,
            new DeterministicDrop());
    public static LogItemInfo DRACONIC_VISAGE_11286 = new LogItemInfo("Draconic visage", 11286,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGONBONE_NECKLACE_22111 = new LogItemInfo("Dragonbone necklace", 22111,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.VORKATH_KILLS, 1.0 / 1000)));
    public static LogItemInfo DRAGONSTONE_BOOTS_24043 = new LogItemInfo("Dragonstone boots", 24043,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGONSTONE_FULL_HELM_24034 = new LogItemInfo("Dragonstone full helm", 24034,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGONSTONE_GAUNTLETS_24046 = new LogItemInfo("Dragonstone gauntlets", 24046,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGONSTONE_PLATEBODY_24037 = new LogItemInfo("Dragonstone platebody", 24037,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGONSTONE_PLATELEGS_24040 = new LogItemInfo("Dragonstone platelegs", 24040,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_2H_SWORD_7158 = new LogItemInfo("Dragon 2h sword", 7158,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAOS_ELEMENTAL_KILLS, 1.0 / 128),
                    new RollInfo(LogItemSourceInfo.KALPHITE_QUEEN_KILLS, 1.0 / 256),
                    new RollInfo(LogItemSourceInfo.CALLISTO_KILLS, 1.0 / 256),
                    new RollInfo(LogItemSourceInfo.VENENATIS_KILLS, 1.0 / 256),
                    new RollInfo(LogItemSourceInfo.VETION_KILLS, 1.0 / 256),
                    new RollInfo(LogItemSourceInfo.ARTIO_KILLS, 1.0 / 358),
                    new RollInfo(LogItemSourceInfo.CALVARION_KILLS, 1.0 / 358),
                    new RollInfo(LogItemSourceInfo.SPINDEL_KILLS, 1.0 / 358)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_CALLISTO_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_VENENATIS_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_VETION_REWARDS_FRACTION_KEY)
    );
    public static LogItemInfo DRAGON_AXE_6739 = new LogItemInfo("Dragon axe", 6739,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.DAGANNOTH_PRIME_KILLS, 1.0 / 128),
                    new RollInfo(LogItemSourceInfo.DAGANNOTH_REX_KILLS, 1.0 / 128),
                    new RollInfo(LogItemSourceInfo.DAGANNOTH_SUPREME_KILLS, 1.0 / 128),
                    new RollInfo(LogItemSourceInfo.WINTERTODT_KILLS, 1.0 / 10000)
            ))
                    .withConfigOption(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY));
    public static LogItemInfo DRAGON_BOOTS_11840 = new LogItemInfo("Dragon boots", 11840,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_BOOTS_ORNAMENT_KIT_22231 = new LogItemInfo("Dragon boots ornament kit", 22231,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo DRAGON_CANE_12373 = new LogItemInfo("Dragon cane", 12373,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo DRAGON_CHAINBODY_3140 = new LogItemInfo("Dragon chainbody", 3140,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_CHAINBODY_ORNAMENT_KIT_12534 = new LogItemInfo("Dragon chainbody ornament kit", 12534,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo DRAGON_CLAWS_13652 = new LogItemInfo("Dragon claws", 13652,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 23),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 23)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    public static LogItemInfo DRAGON_DEFENDER_12954 = new LogItemInfo("Dragon defender", 12954,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_DEFENDER_ORNAMENT_KIT_20143 = new LogItemInfo("Dragon defender ornament kit", 20143,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo DRAGON_FULL_HELM_11335 = new LogItemInfo("Dragon full helm", 11335,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_FULL_HELM_ORNAMENT_KIT_12538 = new LogItemInfo("Dragon full helm ornament kit", 12538,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo DRAGON_HARPOON_21028 = new LogItemInfo("Dragon harpoon", 21028,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_HUNTER_CROSSBOW_21012 = new LogItemInfo("Dragon hunter crossbow", 21012,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 17.25),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 17.25)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    public static LogItemInfo DRAGON_KITESHIELD_ORNAMENT_KIT_22239 = new LogItemInfo("Dragon kiteshield ornament kit", 22239,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 25530, 6)));
    public static LogItemInfo DRAGON_KNIFE_22804 = new LogItemInfo("Dragon knife", 22804,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_LEGS_SKIRT_ORNAMENT_KIT_12536 = new LogItemInfo("Dragon legs/skirt ornament kit", 12536,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo DRAGON_LIMBS_21918 = new LogItemInfo("Dragon limbs", 21918,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_METAL_LUMP_22103 = new LogItemInfo("Dragon metal lump", 22103,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_METAL_SLICE_22100 = new LogItemInfo("Dragon metal slice", 22100,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_PICKAXE_11920 = new LogItemInfo("Dragon pickaxe", 11920,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff
                    new RollInfo(LogItemSourceInfo.KING_BLACK_DRAGON_KILLS, 1.0 / 1500),
                    // post-buff
                    new RollInfo(LogItemSourceInfo.KING_BLACK_DRAGON_KILLS, 1.0 / 1000),
                    new RollInfo(LogItemSourceInfo.CHAOS_ELEMENTAL_KILLS, 1.0 / 256),
                    new RollInfo(LogItemSourceInfo.CALLISTO_KILLS, 1.0 / 256),
                    new RollInfo(LogItemSourceInfo.VENENATIS_KILLS, 1.0 / 256),
                    new RollInfo(LogItemSourceInfo.VETION_KILLS, 1.0 / 256),
                    new RollInfo(LogItemSourceInfo.ARTIO_KILLS, 1.0 / 358),
                    new RollInfo(LogItemSourceInfo.CALVARION_KILLS, 1.0 / 358),
                    new RollInfo(LogItemSourceInfo.SPINDEL_KILLS, 1.0 / 358),
                    new RollInfo(LogItemSourceInfo.KALPHITE_QUEEN_KILLS, 1.0 / 400)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_CALLISTO_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_VENENATIS_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_VETION_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.KQ_KC_PRE_D_PICK_BUFF_KEY)
                    .withConfigOption(CollectionLogLuckConfig.KBD_KC_PRE_D_PICK_BUFF_KEY)
    );
    // TODO: Does repairing this unlock the regular pickaxe?
    public static LogItemInfo DRAGON_PICKAXE_BROKEN_27695 = new LogItemInfo("Dragon pickaxe (broken)", 27695,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_PICKAXE_UPGRADE_KIT_12800 = new LogItemInfo("Dragon pickaxe upgrade kit", 12800,
            new DeterministicDrop());
    public static LogItemInfo DRAGON_PLATEBODY_ORNAMENT_KIT_22236 = new LogItemInfo("Dragon platebody ornament kit", 22236,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo DRAGON_SCIMITAR_ORNAMENT_KIT_20002 = new LogItemInfo("Dragon scimitar ornament kit", 20002,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo DRAGON_SPEAR_1249 = new LogItemInfo("Dragon spear", 1249,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_SQ_SHIELD_ORNAMENT_KIT_12532 = new LogItemInfo("Dragon sq shield ornament kit", 12532,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo DRAGON_SWORD_21009 = new LogItemInfo("Dragon sword", 21009,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_THROWNAXE_20849 = new LogItemInfo("Dragon thrownaxe", 20849,
            new MissingKillCountDrop());
    public static LogItemInfo DRAGON_WARHAMMER_13576 = new LogItemInfo("Dragon warhammer", 13576,
            new MissingKillCountDrop());
    public static LogItemInfo DRAKES_CLAW_22957 = new LogItemInfo("Drake's claw", 22957,
            new MissingKillCountDrop());
    public static LogItemInfo DRAKES_TOOTH_22960 = new LogItemInfo("Drake's tooth", 22960,
            new MissingKillCountDrop());
    public static LogItemInfo DUAL_SAI_23206 = new LogItemInfo("Dual sai", 23206,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo DUST_BATTLESTAFF_20736 = new LogItemInfo("Dust battlestaff", 20736,
            new MissingKillCountDrop());
    public static LogItemInfo EARTH_WARRIOR_CHAMPION_SCROLL_6798 = new LogItemInfo("Earth warrior champion scroll", 6798,
            new MissingKillCountDrop());
    public static LogItemInfo ECTOPLASMATOR_25340 = new LogItemInfo("Ectoplasmator", 25340,
            new DeterministicDrop());
    public static LogItemInfo ECUMENICAL_KEY_11942 = new LogItemInfo("Ecumenical key", 11942,
            new MissingKillCountDrop());
    public static LogItemInfo ELDER_CHAOS_HOOD_20595 = new LogItemInfo("Elder chaos hood", 20595,
            new MissingKillCountDrop());
    public static LogItemInfo ELDER_CHAOS_ROBE_20520 = new LogItemInfo("Elder chaos robe", 20520,
            new MissingKillCountDrop());
    public static LogItemInfo ELDER_CHAOS_TOP_20517 = new LogItemInfo("Elder chaos top", 20517,
            new MissingKillCountDrop());
    public static LogItemInfo ELDER_MAUL_21003 = new LogItemInfo("Elder maul", 21003,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 34.5),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 34.5)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY)
    );
    public static LogItemInfo ELDRITCH_ORB_24517 = new LogItemInfo("Eldritch orb", 24517,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 1800),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 960),
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 3000),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 1600)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_TEAM_SIZE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.NIGHTMARE_KC_PRE_BUFF_KEY)
                    .withConfigOption(CollectionLogLuckConfig.PHOSANIS_NIGHTMARE_KC_PRE_BUFF_KEY)
    );
    public static LogItemInfo ELIDINIS_WARD_25985 = new LogItemInfo("Elidinis' ward", 25985,
            new PoissonBinomialDrop(ImmutableList.of(
                    // drop chance is reduced by 98% in entry mode
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS, 1.0 / 8 / 50),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS, 1.0 / 8),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS, 1.0 / 8)
            ))
                    .withConfigOption(CollectionLogLuckConfig.ENTRY_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.REGULAR_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.EXPERT_TOA_UNIQUE_CHANCE_KEY)
    );
    public static LogItemInfo ELITE_VOID_ROBE_13073 = new LogItemInfo("Elite void robe", 13073,
            new DeterministicDrop());
    public static LogItemInfo ELITE_VOID_TOP_13072 = new LogItemInfo("Elite void top", 13072,
            new DeterministicDrop());
    public static LogItemInfo ELVEN_SIGNET_23943 = new LogItemInfo("Elven signet", 23943,
            new MissingKillCountDrop());
    public static LogItemInfo ELYSIAN_SIGIL_12819 = new LogItemInfo("Elysian sigil", 12819,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CORPOREAL_BEAST_KILLS, 1.0 / 4095)));
    public static LogItemInfo ENCHANTED_HAT_7400 = new LogItemInfo("Enchanted hat", 7400,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ENCHANTED_ROBE_7398 = new LogItemInfo("Enchanted robe", 7398,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ENCHANTED_TOP_7399 = new LogItemInfo("Enchanted top", 7399,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ENHANCED_CRYSTAL_TELEPORT_SEED_23959 = new LogItemInfo("Enhanced crystal teleport seed", 23959,
            new MissingKillCountDrop());
    public static LogItemInfo ENHANCED_CRYSTAL_WEAPON_SEED_25859 = new LogItemInfo("Enhanced crystal weapon seed", 25859,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.GAUNTLET_COMPLETION_COUNT, 1.0 / 2000),
                    new RollInfo(LogItemSourceInfo.CORRUPTED_GAUNTLET_COMPLETION_COUNT, 1.0 / 400)
            )));
    public static LogItemInfo ETERNAL_CRYSTAL_13227 = new LogItemInfo("Eternal crystal", 13227,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CERBERUS_KILLS, 1.0 / 512)));
    public static LogItemInfo ETERNAL_GEM_21270 = new LogItemInfo("Eternal gem", 21270,
            new MissingKillCountDrop());
    public static LogItemInfo EVIL_CHICKEN_FEET_20433 = new LogItemInfo("Evil chicken feet", 20433,
            new MissingKillCountDrop());
    public static LogItemInfo EVIL_CHICKEN_HEAD_20439 = new LogItemInfo("Evil chicken head", 20439,
            new MissingKillCountDrop());
    public static LogItemInfo EVIL_CHICKEN_LEGS_20442 = new LogItemInfo("Evil chicken legs", 20442,
            new MissingKillCountDrop());
    public static LogItemInfo EVIL_CHICKEN_WINGS_20436 = new LogItemInfo("Evil chicken wings", 20436,
            new MissingKillCountDrop());
    public static LogItemInfo EXECUTIONERS_AXE_HEAD_28319 = new LogItemInfo("Executioner's axe head", 28319,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.VARDORVIS_KILLS, 1.0 / 1088)));
    public static LogItemInfo EXPERIMENTAL_NOTE_21672 = new LogItemInfo("Experimental note", 21672,
            new MissingKillCountDrop());
    public static LogItemInfo EXPERT_MINING_GLOVES_21392 = new LogItemInfo("Expert mining gloves", 21392,
            new DeterministicDrop());
    public static LogItemInfo EXPLORER_BACKPACK_12514 = new LogItemInfo("Explorer backpack", 12514,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo EYE_OF_THE_CORRUPTOR_27285 = new LogItemInfo("Eye of the corruptor", 27285,
            new UnimplementedDrop());
    public static LogItemInfo EYE_OF_THE_DUKE_28321 = new LogItemInfo("Eye of the duke", 28321,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DUKE_SUCELLUS_KILLS, 1.0 / 720)));
    public static LogItemInfo FANCY_TIARA_20008 = new LogItemInfo("Fancy tiara", 20008,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo FANGS_OF_VENENATIS_27670 = new LogItemInfo("Fangs of venenatis", 27670,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.VENENATIS_KILLS, 1.0 / 196),
                    new RollInfo(LogItemSourceInfo.SPINDEL_KILLS, 1.0 / 618)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_VENENATIS_REWARDS_FRACTION_KEY));
    public static LogItemInfo FARMERS_BOOTS_13644 = new LogItemInfo("Farmer's boots", 13644,
            new DeterministicDrop());
    public static LogItemInfo FARMERS_BORO_TROUSERS_13640 = new LogItemInfo("Farmer's boro trousers", 13640,
            new DeterministicDrop());
    public static LogItemInfo FARMERS_JACKET_13642 = new LogItemInfo("Farmer's jacket", 13642,
            new DeterministicDrop());
    public static LogItemInfo FARMERS_STRAWHAT_13646 = new LogItemInfo("Farmer's strawhat", 13646,
            new DeterministicDrop());
    public static LogItemInfo FEDORA_11990 = new LogItemInfo("Fedora", 11990,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CRAZY_ARCHAEOLOGIST_KILLS, 1.0 / 128)));
    public static LogItemInfo FELDIP_HILLS_TELEPORT_12404 = new LogItemInfo("Feldip hills teleport", 12404,
            new PoissonBinomialStackDrop());
    public static LogItemInfo FIGHTER_HAT_10548 = new LogItemInfo("Fighter hat", 10548,
            new DeterministicDrop());
    public static LogItemInfo FIGHTER_TORSO_10551 = new LogItemInfo("Fighter torso", 10551,
            new DeterministicDrop());
    public static LogItemInfo FINE_CLOTH_3470 = new LogItemInfo("Fine cloth", 3470,
            new MissingKillCountDrop());
    public static LogItemInfo FIRE_CAPE_6570 = new LogItemInfo("Fire cape", 6570,
            new DeterministicDrop());
    public static LogItemInfo FISH_BARREL_25582 = new LogItemInfo("Fish barrel", 25582,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.REWARD_PERMITS_CLAIMED, 1.0 / 400)));
    public static LogItemInfo FISH_SACK_22838 = new LogItemInfo("Fish sack", 22838,
            new DeterministicDrop());
    public static LogItemInfo FLAMTAER_BAG_25630 = new LogItemInfo("Flamtaer bag", 25630,
            new MissingKillCountDrop());
    public static LogItemInfo FLARED_TROUSERS_10394 = new LogItemInfo("Flared trousers", 10394,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo FLIPPERS_6666 = new LogItemInfo("Flippers", 6666,
            new MissingKillCountDrop());
    public static LogItemInfo FORESTRY_BOOTS_28175 = new LogItemInfo("Forestry boots", 28175,
            new MissingKillCountDrop());
    public static LogItemInfo FORESTRY_HAT_28173 = new LogItemInfo("Forestry hat", 28173,
            new MissingKillCountDrop());
    public static LogItemInfo FORESTRY_LEGS_28171 = new LogItemInfo("Forestry legs", 28171,
            new MissingKillCountDrop());
    public static LogItemInfo FORESTRY_TOP_28169 = new LogItemInfo("Forestry top", 28169,
            new MissingKillCountDrop());
    public static LogItemInfo FREMENNIK_KILT_23246 = new LogItemInfo("Fremennik kilt", 23246,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo FRESH_CRAB_CLAW_7536 = new LogItemInfo("Fresh crab claw", 7536,
            new MissingKillCountDrop());
    public static LogItemInfo FRESH_CRAB_SHELL_7538 = new LogItemInfo("Fresh crab shell", 7538,
            new MissingKillCountDrop());
    public static LogItemInfo FROG_SLIPPERS_23288 = new LogItemInfo("Frog slippers", 23288,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo FROG_TOKEN_6183 = new LogItemInfo("Frog token", 6183,
            new MissingKillCountDrop());
    public static LogItemInfo FROZEN_CACHE_27622 = new LogItemInfo("Frozen cache", 27622,
            // Chance of receiving a frozen cache, or a frozen cache inside a frozen cache, and so on.
            // This infinite series converges to 1/24.8. This assumes frozen caches received inside frozen caches
            // are counted in the collection log.
            new BinomialDrop(new RollInfo(LogItemSourceInfo.PHANTOM_MUSPAH_KILLS, 1.0 / 24.8)));
    public static LogItemInfo FROZEN_TABLET_28333 = new LogItemInfo("Frozen tablet", 28333,
            new DeterministicDrop());
    public static LogItemInfo FROZEN_WHIP_MIX_12769 = new LogItemInfo("Frozen whip mix", 12769,
            new DeterministicDrop());
    public static LogItemInfo FUNKY_SHAPED_LOG_28138 = new LogItemInfo("Funky shaped log", 28138,
            new DeterministicDrop());
    public static LogItemInfo FURY_ORNAMENT_KIT_12526 = new LogItemInfo("Fury ornament kit", 12526,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo GAUNTLET_CAPE_23859 = new LogItemInfo("Gauntlet cape", 23859,
            new DeterministicDrop());
    public static LogItemInfo GEM_BAG_25628 = new LogItemInfo("Gem bag", 25628,
            new DeterministicDrop());
    public static LogItemInfo GHOUL_CHAMPION_SCROLL_6799 = new LogItemInfo("Ghoul champion scroll", 6799,
            new MissingKillCountDrop());
    public static LogItemInfo GHRAZI_RAPIER_22324 = new LogItemInfo("Ghrazi rapier", 22324,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_COMPLETIONS, 1.0 / 86.45),
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 69.3)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_HM_POINTS_KEY)
    );
    public static LogItemInfo GIANT_BOOT_23252 = new LogItemInfo("Giant boot", 23252,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo GIANT_CHAMPION_SCROLL_6800 = new LogItemInfo("Giant champion scroll", 6800,
            new MissingKillCountDrop());
    public static LogItemInfo GIANT_EGG_SACFULL_23517 = new LogItemInfo("Giant egg sac(full)", 23517,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SARACHNIS_KILLS, 1.0 / 20)));
    public static LogItemInfo GIANT_KEY_20754 = new LogItemInfo("Giant key", 20754,
            new MissingKillCountDrop());
    public static LogItemInfo GIANT_SQUIRREL_20659 = new LogItemInfo("Giant squirrel", 20659,
            new MissingKillCountDrop());
    public static LogItemInfo GILDED_2H_SWORD_20155 = new LogItemInfo("Gilded 2h sword", 20155,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_AXE_23279 = new LogItemInfo("Gilded axe", 23279,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14_663, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13_616, 6)
            )));
    public static LogItemInfo GILDED_BOOTS_12391 = new LogItemInfo("Gilded boots", 12391,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14_663, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13_616, 6)
            )));
    public static LogItemInfo GILDED_CHAINBODY_20149 = new LogItemInfo("Gilded chainbody", 20149,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_COIF_23258 = new LogItemInfo("Gilded coif", 23258,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14_663, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13_616, 6)
            )));
    public static LogItemInfo GILDED_DHIDE_BODY_23264 = new LogItemInfo("Gilded d'hide body", 23264,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14_663, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13_616, 6)
            )));
    public static LogItemInfo GILDED_DHIDE_CHAPS_23267 = new LogItemInfo("Gilded d'hide chaps", 23267,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14_663, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13_616, 6)
            )));
    public static LogItemInfo GILDED_DHIDE_VAMBRACES_23261 = new LogItemInfo("Gilded d'hide vambraces", 23261,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14_663, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13_616, 6)
            )));
    public static LogItemInfo GILDED_FULL_HELM_3486 = new LogItemInfo("Gilded full helm", 3486,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_HASTA_20161 = new LogItemInfo("Gilded hasta", 20161,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_KITESHIELD_3488 = new LogItemInfo("Gilded kiteshield", 3488,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_MED_HELM_20146 = new LogItemInfo("Gilded med helm", 20146,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_PICKAXE_23276 = new LogItemInfo("Gilded pickaxe", 23276,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14_663, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13_616, 6)
            )));
    public static LogItemInfo GILDED_PLATEBODY_3481 = new LogItemInfo("Gilded platebody", 3481,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_PLATELEGS_3483 = new LogItemInfo("Gilded platelegs", 3483,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_PLATESKIRT_3485 = new LogItemInfo("Gilded plateskirt", 3485,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_SCIMITAR_12389 = new LogItemInfo("Gilded scimitar", 12389,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14_663, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13_616, 6)
            )));
    public static LogItemInfo GILDED_SMILE_FLAG_8967 = new LogItemInfo("Gilded smile flag", 8967,
            new DeterministicDrop());
    public static LogItemInfo GILDED_SPADE_23282 = new LogItemInfo("Gilded spade", 23282,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14_663, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13_616, 6)
            )));
    public static LogItemInfo GILDED_SPEAR_20158 = new LogItemInfo("Gilded spear", 20158,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GILDED_SQ_SHIELD_20152 = new LogItemInfo("Gilded sq shield", 20152,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 35_750, 5),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 32_258, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 149_776, 6)
            )));
    public static LogItemInfo GLOVES_OF_DARKNESS_20134 = new LogItemInfo("Gloves of darkness", 20134,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo GNOME_GOGGLES_9472 = new LogItemInfo("Gnome goggles", 9472,
            new UnimplementedDrop());
    public static LogItemInfo GNOME_SCARF_9470 = new LogItemInfo("Gnome scarf", 9470,
            new UnimplementedDrop());
    public static LogItemInfo GNOMISH_FIRELIGHTER_20275 = new LogItemInfo("Gnomish firelighter", 20275,
            new UnimplementedDrop());
    public static LogItemInfo GOBLIN_CHAMPION_SCROLL_6801 = new LogItemInfo("Goblin champion scroll", 6801,
            new MissingKillCountDrop());
    public static LogItemInfo GOBLIN_MASK_12251 = new LogItemInfo("Goblin mask", 12251,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo GODSWORD_SHARD_1_11818 = new LogItemInfo("Godsword shard 1", 11818,
            new BinomialDrop(ImmutableList.of(
                    // Assume that the player also kills all 3 minions each kill. Even in teams, as long as the player
                    // kills the minions in the same proportion as they receive kill credit for the main GWD boss,
                    // this should be a good approximation. The boss is worth 2 minion drop chances.
                    new RollInfo(LogItemSourceInfo.COMMANDER_ZILYANA_KILLS, 1.0 / 1524, 5),
                    new RollInfo(LogItemSourceInfo.GENERAL_GRAARDOR_KILLS, 1.0 / 1524, 5),
                    new RollInfo(LogItemSourceInfo.KRIL_TSUTSAROTH_KILLS, 1.0 / 1524, 5),
                    new RollInfo(LogItemSourceInfo.KREEARRA_KILLS, 1.0 / 1524, 5)
            )));
    public static LogItemInfo GODSWORD_SHARD_2_11820 = new LogItemInfo("Godsword shard 2", 11820,
            new BinomialDrop(ImmutableList.of(
                    // Assume that the player also kills all 3 minions each kill. Even in teams, as long as the player
                    // kills the minions in the same proportion as they receive kill credit for the main GWD boss,
                    // this should be a good approximation. The boss is worth 2 minion drop chances.
                    new RollInfo(LogItemSourceInfo.COMMANDER_ZILYANA_KILLS, 1.0 / 1524, 5),
                    new RollInfo(LogItemSourceInfo.GENERAL_GRAARDOR_KILLS, 1.0 / 1524, 5),
                    new RollInfo(LogItemSourceInfo.KRIL_TSUTSAROTH_KILLS, 1.0 / 1524, 5),
                    new RollInfo(LogItemSourceInfo.KREEARRA_KILLS, 1.0 / 1524, 5)
            )));
    public static LogItemInfo GODSWORD_SHARD_3_11822 = new LogItemInfo("Godsword shard 3", 11822,
            new BinomialDrop(ImmutableList.of(
                    // Assume that the player also kills all 3 minions each kill. Even in teams, as long as the player
                    // kills the minions in the same proportion as they receive kill credit for the main GWD boss,
                    // this should be a good approximation. The boss is worth 2 minion drop chances.
                    new RollInfo(LogItemSourceInfo.COMMANDER_ZILYANA_KILLS, 1.0 / 1524, 5),
                    new RollInfo(LogItemSourceInfo.GENERAL_GRAARDOR_KILLS, 1.0 / 1524, 5),
                    new RollInfo(LogItemSourceInfo.KRIL_TSUTSAROTH_KILLS, 1.0 / 1524, 5),
                    new RollInfo(LogItemSourceInfo.KREEARRA_KILLS, 1.0 / 1524, 5)
            )));
    public static LogItemInfo GOLDEN_APRON_20208 = new LogItemInfo("Golden apron", 20208,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo GOLDEN_ARMADYL_SPECIAL_ATTACK_24868 = new LogItemInfo("Golden armadyl special attack", 24868,
            new DeterministicDrop());
    public static LogItemInfo GOLDEN_BANDOS_SPECIAL_ATTACK_24869 = new LogItemInfo("Golden bandos special attack", 24869,
            new DeterministicDrop());
    public static LogItemInfo GOLDEN_CHEFS_HAT_20205 = new LogItemInfo("Golden chef's hat", 20205,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo GOLDEN_SARADOMIN_SPECIAL_ATTACK_24870 = new LogItemInfo("Golden saradomin special attack", 24870,
            new DeterministicDrop());
    public static LogItemInfo GOLDEN_TENCH_22840 = new LogItemInfo("Golden tench", 22840,
            new MissingKillCountDrop());
    public static LogItemInfo GOLDEN_ZAMORAK_SPECIAL_ATTACK_24871 = new LogItemInfo("Golden zamorak special attack", 24871,
            new DeterministicDrop());
    public static LogItemInfo GOLD_ELEGANT_BLOUSE_12343 = new LogItemInfo("Gold elegant blouse", 12343,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo GOLD_ELEGANT_LEGS_12349 = new LogItemInfo("Gold elegant legs", 12349,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo GOLD_ELEGANT_SHIRT_12347 = new LogItemInfo("Gold elegant shirt", 12347,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo GOLD_ELEGANT_SKIRT_12345 = new LogItemInfo("Gold elegant skirt", 12345,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo GOLD_HEADBAND_12303 = new LogItemInfo("Gold headband", 12303,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo GOLD_LOCKS_25454 = new LogItemInfo("Gold locks", 25454,
            new MissingKillCountDrop());
    public static LogItemInfo GOLD_SATCHEL_25622 = new LogItemInfo("Gold satchel", 25622,
            new MissingKillCountDrop());
    public static LogItemInfo GRACEFUL_BOOTS_11860 = new LogItemInfo("Graceful boots", 11860,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_BOOTS_21076 = new LogItemInfo("Graceful boots", 21076,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_CAPE_11852 = new LogItemInfo("Graceful cape", 11852,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_CAPE_21064 = new LogItemInfo("Graceful cape", 21064,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_GLOVES_11858 = new LogItemInfo("Graceful gloves", 11858,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_GLOVES_21073 = new LogItemInfo("Graceful gloves", 21073,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_HOOD_11850 = new LogItemInfo("Graceful hood", 11850,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_HOOD_21061 = new LogItemInfo("Graceful hood", 21061,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_LEGS_11856 = new LogItemInfo("Graceful legs", 11856,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_LEGS_21070 = new LogItemInfo("Graceful legs", 21070,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_TOP_11854 = new LogItemInfo("Graceful top", 11854,
            new DeterministicDrop());
    public static LogItemInfo GRACEFUL_TOP_21067 = new LogItemInfo("Graceful top", 21067,
            new DeterministicDrop());
    public static LogItemInfo GRAND_SEED_POD_9469 = new LogItemInfo("Grand seed pod", 9469,
            new UnimplementedDrop());
    public static LogItemInfo GRANITE_BODY_10564 = new LogItemInfo("Granite body", 10564,
            new DeterministicDrop());
    public static LogItemInfo GRANITE_BOOTS_21643 = new LogItemInfo("Granite boots", 21643,
            new MissingKillCountDrop());
    public static LogItemInfo GRANITE_CLAMP_12849 = new LogItemInfo("Granite clamp", 12849,
            new DeterministicDrop());
    // Capped at 65535
    public static LogItemInfo GRANITE_DUST_21726 = new LogItemInfo("Granite dust", 21726,
            new BinomialUniformSumDrop(
                    new RollInfo(LogItemSourceInfo.GROTESQUE_GUARDIAN_KILLS, 1),
                    50, 100
            ));
    public static LogItemInfo GRANITE_GLOVES_21736 = new LogItemInfo("Granite gloves", 21736,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GROTESQUE_GUARDIAN_KILLS, 1.0 / 500, 2)));
    public static LogItemInfo GRANITE_HAMMER_21742 = new LogItemInfo("Granite hammer", 21742,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GROTESQUE_GUARDIAN_KILLS, 1.0 / 750, 2)));
    public static LogItemInfo GRANITE_HELM_10589 = new LogItemInfo("Granite helm", 10589,
            new MissingKillCountDrop());
    public static LogItemInfo GRANITE_LEGS_6809 = new LogItemInfo("Granite legs", 6809,
            new MissingKillCountDrop());
    public static LogItemInfo GRANITE_LONGSWORD_21646 = new LogItemInfo("Granite longsword", 21646,
            new MissingKillCountDrop());
    public static LogItemInfo GRANITE_MAUL_4153 = new LogItemInfo("Granite maul", 4153,
            new MissingKillCountDrop());
    public static LogItemInfo GRANITE_RING_21739 = new LogItemInfo("Granite ring", 21739,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GROTESQUE_GUARDIAN_KILLS, 1.0 / 500, 2)));
    public static LogItemInfo GREATER_DEMON_MASK_20023 = new LogItemInfo("Greater demon mask", 20023,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo GREEN_BOATER_7323 = new LogItemInfo("Green boater", 7323,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo GREEN_DARK_BOW_PAINT_12759 = new LogItemInfo("Green dark bow paint", 12759,
            new DeterministicDrop());
    public static LogItemInfo GREEN_DHIDE_BODY_G_7370 = new LogItemInfo("Green d'hide body (g)", 7370,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo GREEN_DHIDE_BODY_T_7372 = new LogItemInfo("Green d'hide body (t)", 7372,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo GREEN_DHIDE_CHAPS_G_7378 = new LogItemInfo("Green d'hide chaps (g)", 7378,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo GREEN_DHIDE_CHAPS_T_7380 = new LogItemInfo("Green d'hide chaps (t)", 7380,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo GREEN_DRAGON_MASK_12518 = new LogItemInfo("Green dragon mask", 12518,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GREEN_ELEGANT_BLOUSE_10432 = new LogItemInfo("Green elegant blouse", 10432,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo GREEN_ELEGANT_LEGS_10414 = new LogItemInfo("Green elegant legs", 10414,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo GREEN_ELEGANT_SHIRT_10412 = new LogItemInfo("Green elegant shirt", 10412,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo GREEN_ELEGANT_SKIRT_10434 = new LogItemInfo("Green elegant skirt", 10434,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo GREEN_FIRELIGHTER_7330 = new LogItemInfo("Green firelighter", 7330,
            new PoissonBinomialStackDrop());
    public static LogItemInfo GREEN_HEADBAND_12307 = new LogItemInfo("Green headband", 12307,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo GREEN_NAVAL_SHIRT_8953 = new LogItemInfo("Green naval shirt", 8953,
            new DeterministicDrop());
    public static LogItemInfo GREEN_NAVY_SLACKS_8992 = new LogItemInfo("Green navy slacks", 8992,
            new DeterministicDrop());
    public static LogItemInfo GREEN_SATCHEL_25619 = new LogItemInfo("Green satchel", 25619,
            new MissingKillCountDrop());
    public static LogItemInfo GREEN_TRICORN_HAT_8960 = new LogItemInfo("Green tricorn hat", 8960,
            new DeterministicDrop());
    public static LogItemInfo GREY_NAVAL_SHIRT_8958 = new LogItemInfo("Grey naval shirt", 8958,
            new DeterministicDrop());
    public static LogItemInfo GREY_NAVY_SLACKS_8997 = new LogItemInfo("Grey navy slacks", 8997,
            new DeterministicDrop());
    public static LogItemInfo GREY_TRICORN_HAT_8965 = new LogItemInfo("Grey tricorn hat", 8965,
            new DeterministicDrop());
    public static LogItemInfo GRICOLLERS_CAN_13353 = new LogItemInfo("Gricoller's can", 13353,
            new DeterministicDrop());
    public static LogItemInfo GUARDIANS_EYE_26820 = new LogItemInfo("Guardian's eye", 26820,
            new DeterministicDrop());
    public static LogItemInfo GUTHANS_CHAINSKIRT_4730 = new LogItemInfo("Guthan's chainskirt", 4730,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo GUTHANS_HELM_4724 = new LogItemInfo("Guthan's helm", 4724,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo GUTHANS_PLATEBODY_4728 = new LogItemInfo("Guthan's platebody", 4728,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo GUTHANS_WARSPEAR_4726 = new LogItemInfo("Guthan's warspear", 4726,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo GUTHIXIAN_ICON_24217 = new LogItemInfo("Guthixian icon", 24217,
            new DeterministicDrop());
    public static LogItemInfo GUTHIX_BRACERS_10376 = new LogItemInfo("Guthix bracers", 10376,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_CHAPS_10380 = new LogItemInfo("Guthix chaps", 10380,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_CLOAK_10448 = new LogItemInfo("Guthix cloak", 10448,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo GUTHIX_COIF_10382 = new LogItemInfo("Guthix coif", 10382,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_CROZIER_10442 = new LogItemInfo("Guthix crozier", 10442,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_DHIDE_BODY_10378 = new LogItemInfo("Guthix d'hide body", 10378,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_DHIDE_BOOTS_19927 = new LogItemInfo("Guthix d'hide boots", 19927,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_DHIDE_SHIELD_23188 = new LogItemInfo("Guthix d'hide shield", 23188,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 9750, 5)));
    public static LogItemInfo GUTHIX_FULL_HELM_2673 = new LogItemInfo("Guthix full helm", 2673,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_HALO_12639 = new LogItemInfo("Guthix halo", 12639,
            new DeterministicDrop());
    public static LogItemInfo GUTHIX_KITESHIELD_2675 = new LogItemInfo("Guthix kiteshield", 2675,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_MITRE_10454 = new LogItemInfo("Guthix mitre", 10454,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo GUTHIX_PAGE_1_3835 = new LogItemInfo("Guthix page 1", 3835,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo GUTHIX_PAGE_2_3836 = new LogItemInfo("Guthix page 2", 3836,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo GUTHIX_PAGE_3_3837 = new LogItemInfo("Guthix page 3", 3837,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo GUTHIX_PAGE_4_3838 = new LogItemInfo("Guthix page 4", 3838,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo GUTHIX_PLATEBODY_2669 = new LogItemInfo("Guthix platebody", 2669,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_PLATELEGS_2671 = new LogItemInfo("Guthix platelegs", 2671,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_PLATESKIRT_3480 = new LogItemInfo("Guthix plateskirt", 3480,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo GUTHIX_ROBE_LEGS_10466 = new LogItemInfo("Guthix robe legs", 10466,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo GUTHIX_ROBE_TOP_10462 = new LogItemInfo("Guthix robe top", 10462,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo GUTHIX_STOLE_10472 = new LogItemInfo("Guthix stole", 10472,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo HALF_MOON_SPECTACLES_20053 = new LogItemInfo("Half moon spectacles", 20053,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo HALLOWED_FOCUS_24723 = new LogItemInfo("Hallowed focus", 24723,
            new DeterministicDrop());
    public static LogItemInfo HALLOWED_GRAPPLE_24721 = new LogItemInfo("Hallowed grapple", 24721,
            new DeterministicDrop());
    public static LogItemInfo HALLOWED_HAMMER_24727 = new LogItemInfo("Hallowed hammer", 24727,
            new DeterministicDrop());
    public static LogItemInfo HALLOWED_MARK_24711 = new LogItemInfo("Hallowed mark", 24711,
            new MissingKillCountDrop());
    public static LogItemInfo HALLOWED_RING_24731 = new LogItemInfo("Hallowed ring", 24731,
            new DeterministicDrop());
    public static LogItemInfo HALLOWED_SYMBOL_24725 = new LogItemInfo("Hallowed symbol", 24725,
            new DeterministicDrop());
    public static LogItemInfo HALLOWED_TOKEN_24719 = new LogItemInfo("Hallowed token", 24719,
            new DeterministicDrop());
    public static LogItemInfo HAM_JOINT_23360 = new LogItemInfo("Ham joint", 23360,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo HARMONISED_ORB_24511 = new LogItemInfo("Harmonised orb", 24511,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 1800),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 960),
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 3000),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 1600)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_TEAM_SIZE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.NIGHTMARE_KC_PRE_BUFF_KEY)
                    .withConfigOption(CollectionLogLuckConfig.PHOSANIS_NIGHTMARE_KC_PRE_BUFF_KEY)
    );
    public static LogItemInfo HASTILY_SCRAWLED_NOTE_21678 = new LogItemInfo("Hastily scrawled note", 21678,
            new MissingKillCountDrop());
    public static LogItemInfo HAT_OF_THE_EYE_26850 = new LogItemInfo("Hat of the eye", 26850,
            new DeterministicDrop());
    public static LogItemInfo HEALER_HAT_10547 = new LogItemInfo("Healer hat", 10547,
            new DeterministicDrop());
    public static LogItemInfo HEAVY_FRAME_19589 = new LogItemInfo("Heavy frame", 19589,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.DEMONIC_GORILLA_KILLS, 1.0 / 1500),
                    new RollInfo(LogItemSourceInfo.TORTURED_GORILLA_KILLS, 1.0 / 15000)
            )));
    public static LogItemInfo HELLPUPPY_13247 = new LogItemInfo("Hellpuppy", 13247,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CERBERUS_KILLS, 1.0 / 3000)));
    public static LogItemInfo HERBI_21509 = new LogItemInfo("Herbi", 21509,
            new MissingKillCountDrop());
    public static LogItemInfo HERB_SACK_13226 = new LogItemInfo("Herb sack", 13226,
            new DeterministicDrop());
    public static LogItemInfo HERON_13320 = new LogItemInfo("Heron", 13320,
            new MissingKillCountDrop());
    public static LogItemInfo HESPORI_SEED_22875 = new LogItemInfo("Hespori seed", 22875,
            new MissingKillCountDrop());
    public static LogItemInfo HIGHWAYMAN_MASK_2631 = new LogItemInfo("Highwayman mask", 2631,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo HILL_GIANT_CLUB_20756 = new LogItemInfo("Hill giant club", 20756,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.OBOR_KILLS, 1.0 / 118)));
    public static LogItemInfo HOBGOBLIN_CHAMPION_SCROLL_6802 = new LogItemInfo("Hobgoblin champion scroll", 6802,
            new MissingKillCountDrop());
    public static LogItemInfo HOLY_BLESSING_20220 = new LogItemInfo("Holy blessing", 20220,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 606.4, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 645.8, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 541.7, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 682, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2160, 3)
            )));
    public static LogItemInfo HOLY_ELIXIR_12833 = new LogItemInfo("Holy elixir", 12833,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CORPOREAL_BEAST_KILLS, 3.0 / 512)));
    // Always assume the player completes the HM raid within the challenge time.
    public static LogItemInfo HOLY_ORNAMENT_KIT_25742 = new LogItemInfo("Holy ornament kit", 25742,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 100)));
    public static LogItemInfo HOLY_SANDALS_12598 = new LogItemInfo("Holy sandals", 12598,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo HOLY_WRAPS_19997 = new LogItemInfo("Holy wraps", 19997,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo HONOURABLE_BLESSING_20229 = new LogItemInfo("Honourable blessing", 20229,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 606.4, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 645.8, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 541.7, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 682, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2160, 3)
            )));
    public static LogItemInfo HOOD_OF_DARKNESS_20128 = new LogItemInfo("Hood of darkness", 20128,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo HOSIDIUS_BANNER_20254 = new LogItemInfo("Hosidius banner", 20254,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo HOSIDIUS_BLUEPRINTS_24885 = new LogItemInfo("Hosidius blueprints", 24885,
            new DeterministicDrop());
    public static LogItemInfo HOSIDIUS_HOOD_20116 = new LogItemInfo("Hosidius hood", 20116,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo HOSIDIUS_SCARF_19946 = new LogItemInfo("Hosidius scarf", 19946,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo HYDRAS_CLAW_22966 = new LogItemInfo("Hydra's claw", 22966,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ALCHEMICAL_HYDRA_KILLS, 1.0 / 1001)));
    public static LogItemInfo HYDRAS_EYE_22973 = new LogItemInfo("Hydra's eye", 22973,
            new MissingKillCountDrop());
    public static LogItemInfo HYDRAS_FANG_22971 = new LogItemInfo("Hydra's fang", 22971,
            new MissingKillCountDrop());
    public static LogItemInfo HYDRAS_HEART_22969 = new LogItemInfo("Hydra's heart", 22969,
            new MissingKillCountDrop());
    public static LogItemInfo HYDRA_LEATHER_22983 = new LogItemInfo("Hydra leather", 22983,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ALCHEMICAL_HYDRA_KILLS, 1.0 / 514)));
    public static LogItemInfo HYDRA_TAIL_22988 = new LogItemInfo("Hydra tail", 22988,
            new MissingKillCountDrop());
    public static LogItemInfo IASOR_SEED_22883 = new LogItemInfo("Iasor seed", 22883,
            new BinomialUniformSumDrop(
                    new RollInfo(LogItemSourceInfo.HESPORI_KILLS, 1.0 / 3),
                    1, 2
            ));
    public static LogItemInfo ICE_QUARTZ_28270 = new LogItemInfo("Ice quartz", 28270,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DUKE_SUCELLUS_KILLS, 89.0 / 90.0 * 47.0 / 48.0 * 24.0 / 25.0 / 200)));
    public static LogItemInfo ICTHLARINS_SHROUD_TIER_1_27257 = new LogItemInfo("Icthlarin's shroud (tier 1)", 27257,
            new DeterministicDrop());
    public static LogItemInfo ICTHLARINS_SHROUD_TIER_2_27259 = new LogItemInfo("Icthlarin's shroud (tier 2)", 27259,
            new DeterministicDrop());
    public static LogItemInfo ICTHLARINS_SHROUD_TIER_3_27261 = new LogItemInfo("Icthlarin's shroud (tier 3)", 27261,
            new DeterministicDrop());
    public static LogItemInfo ICTHLARINS_SHROUD_TIER_4_27263 = new LogItemInfo("Icthlarin's shroud (tier 4)", 27263,
            new DeterministicDrop());
    public static LogItemInfo ICTHLARINS_SHROUD_TIER_5_27265 = new LogItemInfo("Icthlarin's shroud (tier 5)", 27265,
            new DeterministicDrop());
    public static LogItemInfo IKKLE_HYDRA_22746 = new LogItemInfo("Ikkle hydra", 22746,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ALCHEMICAL_HYDRA_KILLS, 1.0 / 3000)));
    public static LogItemInfo IMBUED_HEART_20724 = new LogItemInfo("Imbued heart", 20724,
            new MissingKillCountDrop());
    public static LogItemInfo IMCANDO_HAMMER_25644 = new LogItemInfo("Imcando hammer", 25644,
            new MissingKillCountDrop());
    public static LogItemInfo IMP_CHAMPION_SCROLL_6803 = new LogItemInfo("Imp champion scroll", 6803,
            new MissingKillCountDrop());
    public static LogItemInfo IMP_MASK_12249 = new LogItemInfo("Imp mask", 12249,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo INFERNAL_CAPE_21295 = new LogItemInfo("Infernal cape", 21295,
            new DeterministicDrop());
    public static LogItemInfo INFINITY_BOOTS_6920 = new LogItemInfo("Infinity boots", 6920,
            new DeterministicDrop());
    public static LogItemInfo INFINITY_BOTTOMS_6924 = new LogItemInfo("Infinity bottoms", 6924,
            new DeterministicDrop());
    public static LogItemInfo INFINITY_GLOVES_6922 = new LogItemInfo("Infinity gloves", 6922,
            new DeterministicDrop());
    public static LogItemInfo INFINITY_HAT_6918 = new LogItemInfo("Infinity hat", 6918,
            new DeterministicDrop());
    public static LogItemInfo INFINITY_TOP_6916 = new LogItemInfo("Infinity top", 6916,
            new DeterministicDrop());
    public static LogItemInfo INQUISITORS_GREAT_HELM_24419 = new LogItemInfo("Inquisitor's great helm", 24419,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 600),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 420),
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 1000),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 700)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_TEAM_SIZE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.NIGHTMARE_KC_PRE_BUFF_KEY)
                    .withConfigOption(CollectionLogLuckConfig.PHOSANIS_NIGHTMARE_KC_PRE_BUFF_KEY)
    );
    public static LogItemInfo INQUISITORS_HAUBERK_24420 = new LogItemInfo("Inquisitor's hauberk", 24420,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 600),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 420),
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 1000),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 700)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_TEAM_SIZE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.NIGHTMARE_KC_PRE_BUFF_KEY)
                    .withConfigOption(CollectionLogLuckConfig.PHOSANIS_NIGHTMARE_KC_PRE_BUFF_KEY)
    );
    public static LogItemInfo INQUISITORS_MACE_24417 = new LogItemInfo("Inquisitor's mace", 24417,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 1200),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 750),
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 2000),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 1250)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_TEAM_SIZE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.NIGHTMARE_KC_PRE_BUFF_KEY)
                    .withConfigOption(CollectionLogLuckConfig.PHOSANIS_NIGHTMARE_KC_PRE_BUFF_KEY)
    );
    public static LogItemInfo INQUISITORS_PLATESKIRT_24421 = new LogItemInfo("Inquisitor's plateskirt", 24421,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 600),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 420),
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 1000),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 700)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_TEAM_SIZE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.NIGHTMARE_KC_PRE_BUFF_KEY)
                    .withConfigOption(CollectionLogLuckConfig.PHOSANIS_NIGHTMARE_KC_PRE_BUFF_KEY)
    );
    // Note: This drop rate assumes you own all essence pouches or a colossal pouch.
    public static LogItemInfo INTRICATE_POUCH_26908 = new LogItemInfo("Intricate pouch", 26908,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 25)));
    public static LogItemInfo IORWERTH_CAMP_TELEPORT_12410 = new LogItemInfo("Iorwerth camp teleport", 12410,
            new PoissonBinomialStackDrop());
    public static LogItemInfo IRON_BOOTS_4121 = new LogItemInfo("Iron boots", 4121,
            new MissingKillCountDrop());
    public static LogItemInfo IRON_DEFENDER_8845 = new LogItemInfo("Iron defender", 8845,
            new MissingKillCountDrop());
    public static LogItemInfo IRON_DRAGON_MASK_12365 = new LogItemInfo("Iron dragon mask", 12365,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo IRON_FULL_HELM_G_12241 = new LogItemInfo("Iron full helm (g)", 12241,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo IRON_FULL_HELM_T_12231 = new LogItemInfo("Iron full helm (t)", 12231,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo IRON_KITESHIELD_G_12243 = new LogItemInfo("Iron kiteshield (g)", 12243,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo IRON_KITESHIELD_T_12233 = new LogItemInfo("Iron kiteshield (t)", 12233,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo IRON_PLATEBODY_G_12235 = new LogItemInfo("Iron platebody (g)", 12235,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo IRON_PLATEBODY_T_12225 = new LogItemInfo("Iron platebody (t)", 12225,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo IRON_PLATELEGS_G_12237 = new LogItemInfo("Iron platelegs (g)", 12237,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo IRON_PLATELEGS_T_12227 = new LogItemInfo("Iron platelegs (t)", 12227,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo IRON_PLATESKIRT_G_12239 = new LogItemInfo("Iron plateskirt (g)", 12239,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo IRON_PLATESKIRT_T_12229 = new LogItemInfo("Iron plateskirt (t)", 12229,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo JAL_NIB_REK_21291 = new LogItemInfo("Jal-nib-rek", 21291,
            new PoissonBinomialDrop(ImmutableList.of(
                    // First kill drop rate (no slayer task)
                    new RollInfo(LogItemSourceInfo.TZKAL_ZUK_KILLS, 1.0 / 100),
                    // Slayer task drop rate (all subsequent KC)
                    new RollInfo(LogItemSourceInfo.TZKAL_ZUK_KILLS, 1.0 / 75),
                    // Cape sacrifice drop rate
                    new RollInfo(LogItemSourceInfo.TZKAL_ZUK_KILLS, 1.0 / 100)
            ))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INFERNAL_CAPES_SACRIFICED_KEY));
    public static LogItemInfo JAR_OF_CHEMICALS_23064 = new LogItemInfo("Jar of chemicals", 23064,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ALCHEMICAL_HYDRA_KILLS, 1.0 / 2000)));
    public static LogItemInfo JAR_OF_DARKNESS_19701 = new LogItemInfo("Jar of darkness", 19701,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.SKOTIZO_KILLS, 1.0 / 2500),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.SKOTIZO_KILLS, 1.0 / 200)
            ))
                    .withConfigOption(CollectionLogLuckConfig.SKOTIZO_KC_PRE_BUFF_KEY));
    public static LogItemInfo JAR_OF_DECAY_22106 = new LogItemInfo("Jar of decay", 22106,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.VORKATH_KILLS, 1.0 / 3000)));
    public static LogItemInfo JAR_OF_DIRT_12007 = new LogItemInfo("Jar of dirt", 12007,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KRAKEN_KILLS, 1.0 / 1000)));
    public static LogItemInfo JAR_OF_DREAMS_24495 = new LogItemInfo("Jar of dreams", 24495,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 2000),
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 4000)
            ))
                    .withConfigOption("Jar of dreams")
    );
    public static LogItemInfo JAR_OF_EYES_23525 = new LogItemInfo("Jar of eyes", 23525,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SARACHNIS_KILLS, 1.0 / 2000)));
    // Note: This represents the effective chance of dropping from the boss, NOT the chance given your # of Unsired.
    public static LogItemInfo JAR_OF_MIASMA_13277 = new LogItemInfo("Jar of miasma", 13277,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ABYSSAL_SIRE_KILLS, 1.0 / 984)));
    public static LogItemInfo JAR_OF_SAND_12885 = new LogItemInfo("Jar of sand", 12885,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KALPHITE_QUEEN_KILLS, 1.0 / 2000)));
    public static LogItemInfo JAR_OF_SMOKE_25524 = new LogItemInfo("Jar of smoke", 25524,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.THERMONUCLEAR_SMOKE_DEVIL_KILLS, 1.0 / 2000)));
    public static LogItemInfo JAR_OF_SOULS_13245 = new LogItemInfo("Jar of souls", 13245,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CERBERUS_KILLS, 1.0 / 2000)));
    public static LogItemInfo JAR_OF_SPIRITS_25521 = new LogItemInfo("Jar of spirits", 25521,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CORPOREAL_BEAST_KILLS, 1.0 / 1000)));
    public static LogItemInfo JAR_OF_STONE_21745 = new LogItemInfo("Jar of stone", 21745,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GROTESQUE_GUARDIAN_KILLS, 1.0 / 5000)));
    public static LogItemInfo JAR_OF_SWAMP_12936 = new LogItemInfo("Jar of swamp", 12936,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZULRAH_KILLS, 1.0 / 3000)));
    public static LogItemInfo JESTER_CAPE_23297 = new LogItemInfo("Jester cape", 23297,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo JEWEL_OF_THE_SUN_27289 = new LogItemInfo("Jewel of the sun", 27289,
            new UnimplementedDrop());
    public static LogItemInfo JOGRE_CHAMPION_SCROLL_6804 = new LogItemInfo("Jogre champion scroll", 6804,
            new MissingKillCountDrop());
    public static LogItemInfo JUNGLE_DEMON_MASK_20032 = new LogItemInfo("Jungle demon mask", 20032,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo JUSTICIAR_CHESTGUARD_22327 = new LogItemInfo("Justiciar chestguard", 22327,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_COMPLETIONS, 1.0 / 86.45),
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 69.3)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_HM_POINTS_KEY)
    );
    public static LogItemInfo JUSTICIAR_FACEGUARD_22326 = new LogItemInfo("Justiciar faceguard", 22326,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_COMPLETIONS, 1.0 / 86.45),
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 69.3)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_HM_POINTS_KEY)
    );
    public static LogItemInfo JUSTICIAR_LEGGUARDS_22328 = new LogItemInfo("Justiciar legguards", 22328,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_COMPLETIONS, 1.0 / 86.45),
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 69.3)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_HM_POINTS_KEY)
    );
    public static LogItemInfo KALPHITE_PRINCESS_12647 = new LogItemInfo("Kalphite princess", 12647,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KALPHITE_QUEEN_KILLS, 1.0 / 3000)));
    public static LogItemInfo KARAMJAN_MONKEY_24862 = new LogItemInfo("Karamjan monkey", 24862,
            new DeterministicDrop());
    public static LogItemInfo KARILS_COIF_4732 = new LogItemInfo("Karil's coif", 4732,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo KARILS_CROSSBOW_4734 = new LogItemInfo("Karil's crossbow", 4734,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo KARILS_LEATHERSKIRT_4738 = new LogItemInfo("Karil's leatherskirt", 4738,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo KARILS_LEATHERTOP_4736 = new LogItemInfo("Karil's leathertop", 4736,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo KATANA_12357 = new LogItemInfo("Katana", 12357,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo KBD_HEADS_7980 = new LogItemInfo("Kbd heads", 7980,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KING_BLACK_DRAGON_KILLS, 1.0 / 128)));
    public static LogItemInfo KEY_MASTER_TELEPORT_13249 = new LogItemInfo("Key master teleport", 13249,
            new FixedStackDrop(new RollInfo(LogItemSourceInfo.CERBERUS_KILLS, 1.0 / 64), 3));
    public static LogItemInfo KODAI_INSIGNIA_21043 = new LogItemInfo("Kodai insignia", 21043,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 34.5),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 34.5)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY)
    );
    public static LogItemInfo KOVACS_GROG_27014 = new LogItemInfo("Kovac's grog", 27014,
            new DeterministicDrop());
    public static LogItemInfo KQ_HEAD_7981 = new LogItemInfo("Kq head", 7981,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KALPHITE_QUEEN_KILLS, 1.0 / 128)));
    public static LogItemInfo KRAKEN_TENTACLE_12004 = new LogItemInfo("Kraken tentacle", 12004,
            new MissingKillCountDrop());
    public static LogItemInfo KRONOS_SEED_22885 = new LogItemInfo("Kronos seed", 22885,
            new BinomialUniformSumDrop(
                    new RollInfo(LogItemSourceInfo.HESPORI_KILLS, 1.0 / 3),
                    1, 2
            ));
    public static LogItemInfo KRUK_JR_24866 = new LogItemInfo("Kruk jr", 24866,
            new DeterministicDrop());
    public static LogItemInfo KURASK_HEAD_7978 = new LogItemInfo("Kurask head", 7978,
            new MissingKillCountDrop());
    public static LogItemInfo LARGE_WATER_CONTAINER_25615 = new LogItemInfo("Large water container", 25615,
            new DeterministicDrop());
    public static LogItemInfo LAVA_DRAGON_MASK_12371 = new LogItemInfo("Lava dragon mask", 12371,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14663, 5)));
    public static LogItemInfo LAVA_STAFF_UPGRADE_KIT_21202 = new LogItemInfo("Lava staff upgrade kit", 21202,
            new DeterministicDrop());
    public static LogItemInfo LEAF_BLADED_BATTLEAXE_20727 = new LogItemInfo("Leaf-bladed battleaxe", 20727,
            new MissingKillCountDrop());
    public static LogItemInfo LEAF_BLADED_SWORD_11902 = new LogItemInfo("Leaf-bladed sword", 11902,
            new MissingKillCountDrop());
    public static LogItemInfo LEATHER_BODY_G_23381 = new LogItemInfo("Leather body (g)", 23381,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo LEATHER_CHAPS_G_23384 = new LogItemInfo("Leather chaps (g)", 23384,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo LEDERHOSEN_HAT_6182 = new LogItemInfo("Lederhosen hat", 6182,
            new MissingKillCountDrop());
    public static LogItemInfo LEDERHOSEN_SHORTS_6181 = new LogItemInfo("Lederhosen shorts", 6181,
            new MissingKillCountDrop());
    public static LogItemInfo LEDERHOSEN_TOP_6180 = new LogItemInfo("Lederhosen top", 6180,
            new MissingKillCountDrop());
    public static LogItemInfo LEFT_EYE_PATCH_19724 = new LogItemInfo("Left eye patch", 19724,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo LEFT_SKULL_HALF_9008 = new LogItemInfo("Left skull half", 9008,
            new MissingKillCountDrop());
    public static LogItemInfo LEPRECHAUN_HAT_12359 = new LogItemInfo("Leprechaun hat", 12359,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo LESSER_DEMON_CHAMPION_SCROLL_6805 = new LogItemInfo("Lesser demon champion scroll", 6805,
            new MissingKillCountDrop());
    public static LogItemInfo LESSER_DEMON_MASK_20020 = new LogItemInfo("Lesser demon mask", 20020,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo LEVIATHANS_LURE_28325 = new LogItemInfo("Leviathan's lure", 28325,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.LEVIATHAN_KILLS, 1.0 / 768)));
    public static LogItemInfo LIGHTBEARER_25975 = new LogItemInfo("Lightbearer", 25975,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS, 1.0 / 3.429),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS, 1.0 / 3.429),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS, 1.0 / 3.429)
            ))
                    .withConfigOption(CollectionLogLuckConfig.ENTRY_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.REGULAR_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.EXPERT_TOA_UNIQUE_CHANCE_KEY)
    );
    public static LogItemInfo LIGHT_BOW_TIE_19985 = new LogItemInfo("Light bow tie", 19985,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo LIGHT_FRAME_19586 = new LogItemInfo("Light frame", 19586,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.DEMONIC_GORILLA_KILLS, 1.0 / 750),
                    new RollInfo(LogItemSourceInfo.TORTURED_GORILLA_KILLS, 1.0 / 7500)
            )));
    public static LogItemInfo LIGHT_INFINITY_COLOUR_KIT_12530 = new LogItemInfo("Light infinity colour kit", 12530,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo LIGHT_TROUSERS_19979 = new LogItemInfo("Light trousers", 19979,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo LIGHT_TUXEDO_CUFFS_19976 = new LogItemInfo("Light tuxedo cuffs", 19976,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo LIGHT_TUXEDO_JACKET_19973 = new LogItemInfo("Light tuxedo jacket", 19973,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo LIGHT_TUXEDO_SHOES_19982 = new LogItemInfo("Light tuxedo shoes", 19982,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 12750, 5)));
    public static LogItemInfo LILVIATHAN_28252 = new LogItemInfo("Lil'viathan", 28252,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.LEVIATHAN_KILLS, 1.0 / 2500)));
    public static LogItemInfo LIL_CREATOR_25348 = new LogItemInfo("Lil' creator", 25348,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SPOILS_OF_WAR_OPENED, 1.0 / 400)));
    // This assumes that the player always reaches max pet rate. This is fairly easy to do, as the player can die 2+
    // times per raid and still receive the max pet rate.
    public static LogItemInfo LIL_ZIK_22473 = new LogItemInfo("Lil' zik", 22473,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 500),
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_COMPLETIONS, 1.0 / 650)
            )));
    public static LogItemInfo LITTLE_NIGHTMARE_24491 = new LogItemInfo("Little nightmare", 24491,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 800),
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 1400)
            ))
                    .withConfigOption("Little nightmare")
    );
    public static LogItemInfo LOG_BASKET_28140 = new LogItemInfo("Log basket", 28140,
            new DeterministicDrop());
    public static LogItemInfo LOG_BRACE_28146 = new LogItemInfo("Log brace", 28146,
            new DeterministicDrop());
    public static LogItemInfo LONG_BONE_10976 = new LogItemInfo("Long bone", 10976,
            new MissingKillCountDrop());
    public static LogItemInfo LOST_BAG_26912 = new LogItemInfo("Lost bag", 26912,
            // Assumes the player opens their intricate pouches
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 25.0 / 60)));
    public static LogItemInfo LOVAKENGJ_BANNER_20257 = new LogItemInfo("Lovakengj banner", 20257,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo LOVAKENGJ_HOOD_20119 = new LogItemInfo("Lovakengj hood", 20119,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo LOVAKENGJ_SCARF_19949 = new LogItemInfo("Lovakengj scarf", 19949,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo LUCKY_SHOT_FLAG_8969 = new LogItemInfo("Lucky shot flag", 8969,
            new DeterministicDrop());
    public static LogItemInfo LUMBERJACK_BOOTS_10933 = new LogItemInfo("Lumberjack boots", 10933,
            new DeterministicDrop());
    public static LogItemInfo LUMBERJACK_HAT_10941 = new LogItemInfo("Lumberjack hat", 10941,
            new DeterministicDrop());
    public static LogItemInfo LUMBERJACK_LEGS_10940 = new LogItemInfo("Lumberjack legs", 10940,
            new DeterministicDrop());
    public static LogItemInfo LUMBERJACK_TOP_10939 = new LogItemInfo("Lumberjack top", 10939,
            new DeterministicDrop());
    public static LogItemInfo LUMBERYARD_TELEPORT_12642 = new LogItemInfo("Lumberyard teleport", 12642,
            new PoissonBinomialStackDrop());
    public static LogItemInfo LUNAR_ISLE_TELEPORT_12405 = new LogItemInfo("Lunar isle teleport", 12405,
            new PoissonBinomialStackDrop());
    public static LogItemInfo MAGES_BOOK_6889 = new LogItemInfo("Mage's book", 6889,
            new DeterministicDrop());
    public static LogItemInfo MAGIC_COMP_BOW_10284 = new LogItemInfo("Magic comp bow", 10284,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 270.8, 5)));
    public static LogItemInfo MAGIC_FANG_12932 = new LogItemInfo("Magic fang", 12932,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZULRAH_KILLS, 1.0 / 1024, 2)));
    public static LogItemInfo MAGMA_MUTAGEN_13201 = new LogItemInfo("Magma mutagen", 13201,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZULRAH_KILLS, 1.0 / 13106, 2)));
    public static LogItemInfo MAGUS_VESTIGE_28281 = new LogItemInfo("Magus vestige", 28281,
            new HiddenShardDrop(new RollInfo(LogItemSourceInfo.DUKE_SUCELLUS_KILLS, 1.0 / 90.0 * 3.0 / 8.0), 3));
    public static LogItemInfo MALEDICTION_SHARD_1_11931 = new LogItemInfo("Malediction shard 1", 11931,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CHAOS_FANATIC_KILLS, 1.0 / 256)));
    public static LogItemInfo MALEDICTION_SHARD_2_11932 = new LogItemInfo("Malediction shard 2", 11932,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CRAZY_ARCHAEOLOGIST_KILLS, 1.0 / 256)));
    public static LogItemInfo MALEDICTION_SHARD_3_11933 = new LogItemInfo("Malediction shard 3", 11933,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SCORPIA_KILLS, 1.0 / 256)));
    public static LogItemInfo MANIACAL_MONKEY_24864 = new LogItemInfo("Maniacal monkey", 24864,
            new DeterministicDrop());
    public static LogItemInfo MARK_OF_GRACE_11849 = new LogItemInfo("Mark of grace", 11849,
            new MissingKillCountDrop());
    public static LogItemInfo MASK_OF_RANUL_23522 = new LogItemInfo("Mask of ranul", 23522,
            new MissingKillCountDrop());
    public static LogItemInfo MASORI_BODY_27229 = new LogItemInfo("Masori body", 27229,
            new PoissonBinomialDrop(ImmutableList.of(
                    // drop chance is reduced by 98% in entry mode
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS, 1.0 / 12 / 50),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS, 1.0 / 12),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS, 1.0 / 12)
            ))
                    .withConfigOption(CollectionLogLuckConfig.ENTRY_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.REGULAR_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.EXPERT_TOA_UNIQUE_CHANCE_KEY)
    );
    public static LogItemInfo MASORI_CHAPS_27232 = new LogItemInfo("Masori chaps", 27232,
            new PoissonBinomialDrop(ImmutableList.of(
                    // drop chance is reduced by 98% in entry mode
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS, 1.0 / 12 / 50),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS, 1.0 / 12),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS, 1.0 / 12)
            ))
                    .withConfigOption(CollectionLogLuckConfig.ENTRY_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.REGULAR_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.EXPERT_TOA_UNIQUE_CHANCE_KEY)
    );
    public static LogItemInfo MASORI_CRAFTING_KIT_27372 = new LogItemInfo("Masori crafting kit", 27372,
            new DeterministicDrop());
    public static LogItemInfo MASORI_MASK_27226 = new LogItemInfo("Masori mask", 27226,
            new PoissonBinomialDrop(ImmutableList.of(
                    // drop chance is reduced by 98% in entry mode
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS, 1.0 / 12 / 50),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS, 1.0 / 12),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS, 1.0 / 12)
            ))
                    .withConfigOption(CollectionLogLuckConfig.ENTRY_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.REGULAR_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.EXPERT_TOA_UNIQUE_CHANCE_KEY)
    );
    public static LogItemInfo MASTER_SCROLL_BOOK_EMPTY_21387 = new LogItemInfo("Master scroll book (empty)", 21387,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 333.5, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 355.2, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 595.8, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 750.2, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 792, 3)
            )));
    public static LogItemInfo MASTER_WAND_6914 = new LogItemInfo("Master wand", 6914,
            new DeterministicDrop());
    public static LogItemInfo MENAPHITE_ORNAMENT_KIT_27255 = new LogItemInfo("Menaphite ornament kit", 27255,
            new DeterministicDrop());
    public static LogItemInfo MERFOLK_TRIDENT_21649 = new LogItemInfo("Merfolk trident", 21649,
            new DeterministicDrop());
    // Always assume the player completes the CM raid within the challenge time.
    public static LogItemInfo METAMORPHIC_DUST_22386 = new LogItemInfo("Metamorphic dust", 22386,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 400)));
    public static LogItemInfo MIME_BOOTS_3061 = new LogItemInfo("Mime boots", 3061,
            new MissingKillCountDrop());
    public static LogItemInfo MIME_GLOVES_3060 = new LogItemInfo("Mime gloves", 3060,
            new MissingKillCountDrop());
    public static LogItemInfo MIME_LEGS_3059 = new LogItemInfo("Mime legs", 3059,
            new MissingKillCountDrop());
    public static LogItemInfo MIME_MASK_3057 = new LogItemInfo("Mime mask", 3057,
            new MissingKillCountDrop());
    public static LogItemInfo MIME_TOP_3058 = new LogItemInfo("Mime top", 3058,
            new MissingKillCountDrop());
    public static LogItemInfo MINING_GLOVES_21343 = new LogItemInfo("Mining gloves", 21343,
            new DeterministicDrop());
    public static LogItemInfo MINT_CAKE_9475 = new LogItemInfo("Mint cake", 9475,
            new UnimplementedDrop());
    public static LogItemInfo MIST_BATTLESTAFF_20730 = new LogItemInfo("Mist battlestaff", 20730,
            new MissingKillCountDrop());
    public static LogItemInfo MITHRIL_BOOTS_4127 = new LogItemInfo("Mithril boots", 4127,
            new MissingKillCountDrop());
    public static LogItemInfo MITHRIL_DEFENDER_8848 = new LogItemInfo("Mithril defender", 8848,
            new MissingKillCountDrop());
    public static LogItemInfo MITHRIL_DRAGON_MASK_12369 = new LogItemInfo("Mithril dragon mask", 12369,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo MITHRIL_FULL_HELM_G_12283 = new LogItemInfo("Mithril full helm (g)", 12283,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo MITHRIL_FULL_HELM_T_12293 = new LogItemInfo("Mithril full helm (t)", 12293,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo MITHRIL_KITESHIELD_G_12281 = new LogItemInfo("Mithril kiteshield (g)", 12281,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo MITHRIL_KITESHIELD_T_12291 = new LogItemInfo("Mithril kiteshield (t)", 12291,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo MITHRIL_PLATEBODY_G_12277 = new LogItemInfo("Mithril platebody (g)", 12277,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo MITHRIL_PLATEBODY_T_12287 = new LogItemInfo("Mithril platebody (t)", 12287,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo MITHRIL_PLATELEGS_G_12279 = new LogItemInfo("Mithril platelegs (g)", 12279,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo MITHRIL_PLATELEGS_T_12289 = new LogItemInfo("Mithril platelegs (t)", 12289,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo MITHRIL_PLATESKIRT_G_12285 = new LogItemInfo("Mithril plateskirt (g)", 12285,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo MITHRIL_PLATESKIRT_T_12295 = new LogItemInfo("Mithril plateskirt (t)", 12295,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    // Capped at 250
    public static LogItemInfo MOLE_CLAW_7416 = new LogItemInfo("Mole claw", 7416,
            new DeterministicDrop());
    // Capped at 250
    public static LogItemInfo MOLE_SKIN_7418 = new LogItemInfo("Mole skin", 7418,
            new BinomialUniformSumDrop(
                    new RollInfo(LogItemSourceInfo.GIANT_MOLE_KILLS, 1),
                    1, 3
            ));
    public static LogItemInfo MOLE_SLIPPERS_23285 = new LogItemInfo("Mole slippers", 23285,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo MONKEY_TAIL_19610 = new LogItemInfo("Monkey tail", 19610,
            new MissingKillCountDrop());
    public static LogItemInfo MONKS_ROBE_G_20202 = new LogItemInfo("Monk's robe (g)", 20202,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 14040, 3)));
    public static LogItemInfo MONKS_ROBE_TOP_G_20199 = new LogItemInfo("Monk's robe top (g)", 20199,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 14040, 3)));
    public static LogItemInfo MONKS_ROBE_TOP_T_23303 = new LogItemInfo("Monk's robe top (t)", 23303,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo MONKS_ROBE_T_23306 = new LogItemInfo("Monk's robe (t)", 23306,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo MONOCLE_12353 = new LogItemInfo("Monocle", 12353,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo MORTTON_TELEPORT_12406 = new LogItemInfo("Mort'ton teleport", 12406,
            new PoissonBinomialStackDrop());
    public static LogItemInfo MOSSY_KEY_22374 = new LogItemInfo("Mossy key", 22374,
            new MissingKillCountDrop());
    public static LogItemInfo MOS_LEHARMLESS_TELEPORT_12411 = new LogItemInfo("Mos le'harmless teleport", 12411,
            new PoissonBinomialStackDrop());
    public static LogItemInfo MUDSKIPPER_HAT_6665 = new LogItemInfo("Mudskipper hat", 6665,
            new MissingKillCountDrop());
    public static LogItemInfo MUD_BATTLESTAFF_6562 = new LogItemInfo("Mud battlestaff", 6562,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DAGANNOTH_PRIME_KILLS, 1.0 / 128)));
    public static LogItemInfo MUMMYS_BODY_20083 = new LogItemInfo("Mummy's body", 20083,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo MUMMYS_FEET_20092 = new LogItemInfo("Mummy's feet", 20092,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo MUMMYS_HANDS_20086 = new LogItemInfo("Mummy's hands", 20086,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo MUMMYS_HEAD_20080 = new LogItemInfo("Mummy's head", 20080,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo MUMMYS_LEGS_20089 = new LogItemInfo("Mummy's legs", 20089,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 12765, 6)));
    public static LogItemInfo MUPHIN_27590 = new LogItemInfo("Muphin", 27590,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.PHANTOM_MUSPAH_KILLS, 1.0 / 2500)));
    public static LogItemInfo MUSKETEER_HAT_12351 = new LogItemInfo("Musketeer hat", 12351,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo MUSKETEER_PANTS_12443 = new LogItemInfo("Musketeer pants", 12443,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo MUSKETEER_TABARD_12441 = new LogItemInfo("Musketeer tabard", 12441,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo MUSTY_SMELLING_NOTE_21676 = new LogItemInfo("Musty smelling note", 21676,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTERIOUS_PAGE_24763 = new LogItemInfo("Mysterious page", 24763,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTERIOUS_PAGE_24765 = new LogItemInfo("Mysterious page", 24765,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTERIOUS_PAGE_24767 = new LogItemInfo("Mysterious page", 24767,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTERIOUS_PAGE_24769 = new LogItemInfo("Mysterious page", 24769,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTERIOUS_PAGE_24771 = new LogItemInfo("Mysterious page", 24771,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_BOOTS_DARK_4107 = new LogItemInfo("Mystic boots (dark)", 4107,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_BOOTS_DUSK_23059 = new LogItemInfo("Mystic boots (dusk)", 23059,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_BOOTS_LIGHT_4117 = new LogItemInfo("Mystic boots (light)", 4117,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_GLOVES_DARK_4105 = new LogItemInfo("Mystic gloves (dark)", 4105,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_GLOVES_DUSK_23056 = new LogItemInfo("Mystic gloves (dusk)", 23056,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_GLOVES_LIGHT_4115 = new LogItemInfo("Mystic gloves (light)", 4115,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_HAT_DARK_4099 = new LogItemInfo("Mystic hat (dark)", 4099,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_HAT_DUSK_23047 = new LogItemInfo("Mystic hat (dusk)", 23047,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_HAT_LIGHT_4109 = new LogItemInfo("Mystic hat (light)", 4109,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_ROBE_BOTTOM_DARK_4103 = new LogItemInfo("Mystic robe bottom (dark)", 4103,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_ROBE_BOTTOM_DUSK_23053 = new LogItemInfo("Mystic robe bottom (dusk)", 23053,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_ROBE_BOTTOM_LIGHT_4113 = new LogItemInfo("Mystic robe bottom (light)", 4113,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_ROBE_TOP_DARK_4101 = new LogItemInfo("Mystic robe top (dark)", 4101,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_ROBE_TOP_DUSK_23050 = new LogItemInfo("Mystic robe top (dusk)", 23050,
            new MissingKillCountDrop());
    public static LogItemInfo MYSTIC_ROBE_TOP_LIGHT_4111 = new LogItemInfo("Mystic robe top (light)", 4111,
            new MissingKillCountDrop());
    public static LogItemInfo NARDAH_TELEPORT_12402 = new LogItemInfo("Nardah teleport", 12402,
            new PoissonBinomialStackDrop());
    public static LogItemInfo NAVY_CAVALIER_12325 = new LogItemInfo("Navy cavalier", 12325,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo NEXLING_26348 = new LogItemInfo("Nexling", 26348,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.NEX_KILLS, 1.0 / 500))
                    // We don't actually know exactly how much of a bonus MVPing gives to the Nex pet, but assuming it's
                    // 10% multiplicatively just like uniques, we can just use the rewards fraction as if it's
                    // a normal drop since the MVP rate is included in the rewards fraction
                    .withConfigOption(CollectionLogLuckConfig.AVG_NEX_REWARDS_FRACTION_KEY)
    );
    public static LogItemInfo NIGHTMARE_STAFF_24422 = new LogItemInfo("Nightmare staff", 24422,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 400),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 300),
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 667),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 533)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_TEAM_SIZE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.NIGHTMARE_KC_PRE_BUFF_KEY)
                    .withConfigOption(CollectionLogLuckConfig.PHOSANIS_NIGHTMARE_KC_PRE_BUFF_KEY)
    );
    public static LogItemInfo NIHIL_HORN_26372 = new LogItemInfo("Nihil horn", 26372,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.NEX_KILLS, 1.0 / 258))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NEX_REWARDS_FRACTION_KEY)
    );
    public static LogItemInfo NIHIL_SHARD_26231 = new LogItemInfo("Nihil shard", 26231,
            new PoissonBinomialStackDrop());
    public static LogItemInfo NOON_21748 = new LogItemInfo("Noon", 21748,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GROTESQUE_GUARDIAN_KILLS, 1.0 / 3000)));
    public static LogItemInfo NUNCHAKU_19918 = new LogItemInfo("Nunchaku", 19918,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo OBSIDIAN_CAPE_6568 = new LogItemInfo("Obsidian cape", 6568,
            new MissingKillCountDrop());
    public static LogItemInfo OBSIDIAN_CAPE_R_20050 = new LogItemInfo("Obsidian cape (r)", 20050,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo OBSIDIAN_HELMET_21298 = new LogItemInfo("Obsidian helmet", 21298,
            new MissingKillCountDrop());
    public static LogItemInfo OBSIDIAN_PLATEBODY_21301 = new LogItemInfo("Obsidian platebody", 21301,
            new MissingKillCountDrop());
    public static LogItemInfo OBSIDIAN_PLATELEGS_21304 = new LogItemInfo("Obsidian platelegs", 21304,
            new MissingKillCountDrop());
    public static LogItemInfo OCCULT_NECKLACE_12002 = new LogItemInfo("Occult necklace", 12002,
            new MissingKillCountDrop());
    public static LogItemInfo OCCULT_ORNAMENT_KIT_20065 = new LogItemInfo("Occult ornament kit", 20065,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo ODIUM_SHARD_1_11928 = new LogItemInfo("Odium shard 1", 11928,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CHAOS_FANATIC_KILLS, 1.0 / 256)));
    public static LogItemInfo ODIUM_SHARD_2_11929 = new LogItemInfo("Odium shard 2", 11929,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CRAZY_ARCHAEOLOGIST_KILLS, 1.0 / 256)));
    public static LogItemInfo ODIUM_SHARD_3_11930 = new LogItemInfo("Odium shard 3", 11930,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SCORPIA_KILLS, 1.0 / 256)));
    public static LogItemInfo OLD_DEMON_MASK_20029 = new LogItemInfo("Old demon mask", 20029,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo OLD_WRITING_21680 = new LogItemInfo("Old writing", 21680,
            new MissingKillCountDrop());
    public static LogItemInfo OLMLET_20851 = new LogItemInfo("Olmlet", 20851,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 53),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 53)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    // Note: We can't know how many raids had Tekton in them.
    public static LogItemInfo ONYX_6573 = new LogItemInfo("Onyx", 6573,
            new MissingKillCountDrop());
    public static LogItemInfo ORANGE_BOATER_7321 = new LogItemInfo("Orange boater", 7321,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ORANGE_EGG_SAC_25844 = new LogItemInfo("Orange egg sac", 25844,
            new MissingKillCountDrop());
    public static LogItemInfo ORE_PACK_27019 = new LogItemInfo("Ore pack", 27019,
            new DeterministicDrop());
    public static LogItemInfo ORNATE_MAUL_HANDLE_24229 = new LogItemInfo("Ornate maul handle", 24229,
            new DeterministicDrop());
    public static LogItemInfo OSMUMTENS_FANG_26219 = new LogItemInfo("Osmumten's fang", 26219,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS, 1.0 / 3.429),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS, 1.0 / 3.429),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS, 1.0 / 3.429)
            ))
                    .withConfigOption(CollectionLogLuckConfig.ENTRY_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.REGULAR_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.EXPERT_TOA_UNIQUE_CHANCE_KEY)
    );
    public static LogItemInfo PANTALOONS_10396 = new LogItemInfo("Pantaloons", 10396,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo PARAGRAPH_OF_TEXT_21674 = new LogItemInfo("Paragraph of text", 21674,
            new MissingKillCountDrop());
    public static LogItemInfo PARASITIC_EGG_25838 = new LogItemInfo("Parasitic egg", 25838,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 200)));
    public static LogItemInfo PARTIAL_NOTE_21666 = new LogItemInfo("Partial note", 21666,
            new MissingKillCountDrop());
    public static LogItemInfo PEACEFUL_BLESSING_20226 = new LogItemInfo("Peaceful blessing", 20226,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 606.4, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 645.8, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 541.7, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 682, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2160, 3)
            )));
    public static LogItemInfo PEARL_BARBARIAN_ROD_22842 = new LogItemInfo("Pearl barbarian rod", 22842,
            new DeterministicDrop());
    public static LogItemInfo PEARL_FISHING_ROD_22846 = new LogItemInfo("Pearl fishing rod", 22846,
            new DeterministicDrop());
    public static LogItemInfo PEARL_FLY_FISHING_ROD_22844 = new LogItemInfo("Pearl fly fishing rod", 22844,
            new DeterministicDrop());
    public static LogItemInfo PEGASIAN_CRYSTAL_13229 = new LogItemInfo("Pegasian crystal", 13229,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CERBERUS_KILLS, 1.0 / 512)));
    public static LogItemInfo PENANCE_GLOVES_10553 = new LogItemInfo("Penance gloves", 10553,
            new DeterministicDrop());
    public static LogItemInfo PENANCE_SKIRT_10555 = new LogItemInfo("Penance skirt", 10555,
            new DeterministicDrop());
    public static LogItemInfo PENGUIN_MASK_12428 = new LogItemInfo("Penguin mask", 12428,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo PEST_CONTROL_TELEPORT_12407 = new LogItemInfo("Pest control teleport", 12407,
            new PoissonBinomialStackDrop());
    public static LogItemInfo PET_CHAOS_ELEMENTAL_11995 = new LogItemInfo("Pet chaos elemental", 11995,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAOS_ELEMENTAL_KILLS, 1.0 / 300),
                    new RollInfo(LogItemSourceInfo.CHAOS_FANATIC_KILLS, 1.0 / 1000)
            )));
    public static LogItemInfo PET_DAGANNOTH_PRIME_12644 = new LogItemInfo("Pet dagannoth prime", 12644,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DAGANNOTH_PRIME_KILLS, 1.0 / 5000)));
    public static LogItemInfo PET_DAGANNOTH_REX_12645 = new LogItemInfo("Pet dagannoth rex", 12645,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DAGANNOTH_REX_KILLS, 1.0 / 5000)));
    public static LogItemInfo PET_DAGANNOTH_SUPREME_12643 = new LogItemInfo("Pet dagannoth supreme", 12643,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DAGANNOTH_SUPREME_KILLS, 1.0 / 5000)));
    public static LogItemInfo PET_DARK_CORE_12816 = new LogItemInfo("Pet dark core", 12816,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CORPOREAL_BEAST_KILLS, 1.0 / 5000)));
    public static LogItemInfo PET_GENERAL_GRAARDOR_12650 = new LogItemInfo("Pet general graardor", 12650,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GENERAL_GRAARDOR_KILLS, 1.0 / 5000)));
    public static LogItemInfo PET_KRAKEN_12655 = new LogItemInfo("Pet kraken", 12655,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KRAKEN_KILLS, 1.0 / 3000)));
    public static LogItemInfo PET_KREEARRA_12649 = new LogItemInfo("Pet kree'arra", 12649,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KREEARRA_KILLS, 1.0 / 5000)));
    public static LogItemInfo PET_KRIL_TSUTSAROTH_12652 = new LogItemInfo("Pet k'ril tsutsaroth", 12652,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KRIL_TSUTSAROTH_KILLS, 1.0 / 5000)));
    public static LogItemInfo PET_PENANCE_QUEEN_12703 = new LogItemInfo("Pet penance queen", 12703,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HIGH_LEVEL_GAMBLES, 1.0 / 1000)));
    public static LogItemInfo PET_SMOKE_DEVIL_12648 = new LogItemInfo("Pet smoke devil", 12648,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.THERMONUCLEAR_SMOKE_DEVIL_KILLS, 1.0 / 3000)));
    public static LogItemInfo PET_SNAKELING_12921 = new LogItemInfo("Pet snakeling", 12921,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZULRAH_KILLS, 1.0 / 4000)));
    public static LogItemInfo PET_ZILYANA_12651 = new LogItemInfo("Pet zilyana", 12651,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.COMMANDER_ZILYANA_KILLS, 1.0 / 5000)));
    public static LogItemInfo PHARAOHS_SCEPTRE_UNCHARGED_26945 = new LogItemInfo("Pharaoh's sceptre (uncharged)", 26945,
            new MissingKillCountDrop());
    public static LogItemInfo PHASMATYS_FLAG_8971 = new LogItemInfo("Phasmatys flag", 8971,
            new DeterministicDrop());
    public static LogItemInfo PHOENIX_20693 = new LogItemInfo("Phoenix", 20693,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.WINTERTODT_KILLS, 1.0 / 5000))
                    .withConfigOption(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY)
    );
    public static LogItemInfo PINK_BOATER_12309 = new LogItemInfo("Pink boater", 12309,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo PINK_ELEGANT_BLOUSE_12339 = new LogItemInfo("Pink elegant blouse", 12339,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo PINK_ELEGANT_LEGS_12317 = new LogItemInfo("Pink elegant legs", 12317,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo PINK_ELEGANT_SHIRT_12315 = new LogItemInfo("Pink elegant shirt", 12315,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo PINK_ELEGANT_SKIRT_12341 = new LogItemInfo("Pink elegant skirt", 12341,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo PINK_HEADBAND_12305 = new LogItemInfo("Pink headband", 12305,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo PIRATES_HAT_2651 = new LogItemInfo("Pirate's hat", 2651,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo PIRATES_HOOK_2997 = new LogItemInfo("Pirate's hook", 2997,
            new DeterministicDrop());
    public static LogItemInfo PISCARILIUS_BANNER_20260 = new LogItemInfo("Piscarilius banner", 20260,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo PISCARILIUS_HOOD_20122 = new LogItemInfo("Piscarilius hood", 20122,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo PISCARILIUS_SCARF_19952 = new LogItemInfo("Piscarilius scarf", 19952,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo PISCATORIS_TELEPORT_12408 = new LogItemInfo("Piscatoris teleport", 12408,
            new PoissonBinomialStackDrop());
    public static LogItemInfo PITH_HELMET_12516 = new LogItemInfo("Pith helmet", 12516,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo PLAIN_SATCHEL_25618 = new LogItemInfo("Plain satchel", 25618,
            new MissingKillCountDrop());
    public static LogItemInfo PLANK_SACK_25629 = new LogItemInfo("Plank sack", 25629,
            new DeterministicDrop());
    public static LogItemInfo PRIMORDIAL_CRYSTAL_13231 = new LogItemInfo("Primordial crystal", 13231,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CERBERUS_KILLS, 1.0 / 512)));
    public static LogItemInfo PRINCELY_MONKEY_24867 = new LogItemInfo("Princely monkey", 24867,
            new DeterministicDrop());
    public static LogItemInfo PRINCE_BLACK_DRAGON_12653 = new LogItemInfo("Prince black dragon", 12653,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KING_BLACK_DRAGON_KILLS, 1.0 / 3000)));
    public static LogItemInfo PROSPECTOR_BOOTS_12016 = new LogItemInfo("Prospector boots", 12016,
            new DeterministicDrop());
    public static LogItemInfo PROSPECTOR_HELMET_12013 = new LogItemInfo("Prospector helmet", 12013,
            new DeterministicDrop());
    public static LogItemInfo PROSPECTOR_JACKET_12014 = new LogItemInfo("Prospector jacket", 12014,
            new DeterministicDrop());
    public static LogItemInfo PROSPECTOR_LEGS_12015 = new LogItemInfo("Prospector legs", 12015,
            new DeterministicDrop());
    public static LogItemInfo PURPLE_BOATER_12311 = new LogItemInfo("Purple boater", 12311,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo PURPLE_ELEGANT_BLOUSE_10436 = new LogItemInfo("Purple elegant blouse", 10436,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo PURPLE_ELEGANT_LEGS_10418 = new LogItemInfo("Purple elegant legs", 10418,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo PURPLE_ELEGANT_SHIRT_10416 = new LogItemInfo("Purple elegant shirt", 10416,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo PURPLE_ELEGANT_SKIRT_10438 = new LogItemInfo("Purple elegant skirt", 10438,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo PURPLE_FIRELIGHTER_10326 = new LogItemInfo("Purple firelighter", 10326,
            new PoissonBinomialStackDrop());
    public static LogItemInfo PURPLE_NAVAL_SHIRT_8957 = new LogItemInfo("Purple naval shirt", 8957,
            new DeterministicDrop());
    public static LogItemInfo PURPLE_NAVY_SLACKS_8996 = new LogItemInfo("Purple navy slacks", 8996,
            new DeterministicDrop());
    public static LogItemInfo PURPLE_SWEETS_10476 = new LogItemInfo("Purple sweets", 10476,
            new PoissonBinomialStackDrop());
    public static LogItemInfo PURPLE_TRICORN_HAT_8964 = new LogItemInfo("Purple tricorn hat", 8964,
            new DeterministicDrop());
    public static LogItemInfo PYROMANCER_BOOTS_20710 = new LogItemInfo("Pyromancer boots", 20710,
            new DupeProtectedSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.WINTERTODT_KILLS, 1.0 / 152.2 / 4.0),
                    ImmutableList.of(20710, 20704, 20708, 20706)
            ).withConfigOption(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY)
    );
    public static LogItemInfo PYROMANCER_GARB_20704 = new LogItemInfo("Pyromancer garb", 20704,
            new DupeProtectedSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.WINTERTODT_KILLS, 1.0 / 152.2 / 4.0),
                    ImmutableList.of(20710, 20704, 20708, 20706)
            ).withConfigOption(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY)
    );
    public static LogItemInfo PYROMANCER_HOOD_20708 = new LogItemInfo("Pyromancer hood", 20708,
            new DupeProtectedSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.WINTERTODT_KILLS, 1.0 / 152.2 / 4.0),
                    ImmutableList.of(20710, 20704, 20708, 20706)
            ).withConfigOption(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY)
    );
    public static LogItemInfo PYROMANCER_ROBE_20706 = new LogItemInfo("Pyromancer robe", 20706,
            new DupeProtectedSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.WINTERTODT_KILLS, 1.0 / 152.2 / 4.0),
                    ImmutableList.of(20710, 20704, 20708, 20706)
            ).withConfigOption(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY)
    );
    public static LogItemInfo RAIN_BOW_23357 = new LogItemInfo("Rain bow", 23357,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo RANGERS_TIGHTS_23249 = new LogItemInfo("Rangers' tights", 23249,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo RANGERS_TUNIC_12596 = new LogItemInfo("Rangers' tunic", 12596,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo RANGER_BOOTS_2577 = new LogItemInfo("Ranger boots", 2577,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo RANGER_GLOVES_19994 = new LogItemInfo("Ranger gloves", 19994,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo RANGER_HAT_10550 = new LogItemInfo("Ranger hat", 10550,
            new DeterministicDrop());
    public static LogItemInfo RED_BERET_12247 = new LogItemInfo("Red beret", 12247,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo RED_BOATER_7319 = new LogItemInfo("Red boater", 7319,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo RED_CAVALIER_12323 = new LogItemInfo("Red cavalier", 12323,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RED_DHIDE_BODY_G_12327 = new LogItemInfo("Red d'hide body (g)", 12327,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RED_DHIDE_BODY_T_12331 = new LogItemInfo("Red d'hide body (t)", 12331,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RED_DHIDE_CHAPS_G_12329 = new LogItemInfo("Red d'hide chaps (g)", 12329,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RED_DHIDE_CHAPS_T_12333 = new LogItemInfo("Red d'hide chaps (t)", 12333,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RED_DRAGON_MASK_12522 = new LogItemInfo("Red dragon mask", 12522,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RED_ELEGANT_BLOUSE_10424 = new LogItemInfo("Red elegant blouse", 10424,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo RED_ELEGANT_LEGS_10406 = new LogItemInfo("Red elegant legs", 10406,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo RED_ELEGANT_SHIRT_10404 = new LogItemInfo("Red elegant shirt", 10404,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo RED_ELEGANT_SKIRT_10426 = new LogItemInfo("Red elegant skirt", 10426,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2808, 3)));
    public static LogItemInfo RED_FIRELIGHTER_7329 = new LogItemInfo("Red firelighter", 7329,
            new PoissonBinomialStackDrop());
    public static LogItemInfo RED_HEADBAND_2645 = new LogItemInfo("Red headband", 2645,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo RED_NAVAL_SHIRT_8954 = new LogItemInfo("Red naval shirt", 8954,
            new DeterministicDrop());
    public static LogItemInfo RED_NAVY_SLACKS_8993 = new LogItemInfo("Red navy slacks", 8993,
            new DeterministicDrop());
    public static LogItemInfo RED_SATCHEL_25620 = new LogItemInfo("Red satchel", 25620,
            new MissingKillCountDrop());
    public static LogItemInfo RED_TRICORN_HAT_8961 = new LogItemInfo("Red tricorn hat", 8961,
            new DeterministicDrop());
    public static LogItemInfo REMNANT_OF_AKKHA_27377 = new LogItemInfo("Remnant of akkha", 27377,
            new DeterministicDrop());
    public static LogItemInfo REMNANT_OF_BA_BA_27378 = new LogItemInfo("Remnant of ba-ba", 27378,
            new DeterministicDrop());
    public static LogItemInfo REMNANT_OF_KEPHRI_27379 = new LogItemInfo("Remnant of kephri", 27379,
            new DeterministicDrop());
    public static LogItemInfo REMNANT_OF_ZEBAK_27380 = new LogItemInfo("Remnant of zebak", 27380,
            new DeterministicDrop());
    public static LogItemInfo REVENANT_CAVE_TELEPORT_21802 = new LogItemInfo("Revenant cave teleport", 21802,
            new MissingKillCountDrop());
    public static LogItemInfo REVENANT_ETHER_21820 = new LogItemInfo("Revenant ether", 21820,
            new MissingKillCountDrop());
    public static LogItemInfo RIFT_GUARDIAN_20665 = new LogItemInfo("Rift guardian", 20665,
            new MissingKillCountDrop());
    public static LogItemInfo RIGHT_SKULL_HALF_9007 = new LogItemInfo("Right skull half", 9007,
            new MissingKillCountDrop());
    public static LogItemInfo RING_OF_THIRD_AGE_23185 = new LogItemInfo("Ring of 3rd age", 23185,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 44),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 40)
            )));
    public static LogItemInfo RING_OF_COINS_20017 = new LogItemInfo("Ring of coins", 20017,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 13616, 6)));
    public static LogItemInfo RING_OF_ENDURANCE_UNCHARGED_24844 = new LogItemInfo("Ring of endurance (uncharged)", 24844,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.GRAND_HALLOWED_COFFINS_OPENED, 1.0 / 200)));
    public static LogItemInfo RING_OF_NATURE_20005 = new LogItemInfo("Ring of nature", 20005,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 14663, 5)));
    public static LogItemInfo RING_OF_THE_ELEMENTS_26815 = new LogItemInfo("Ring of the elements", 26815,
            new DeterministicDrop());
    public static LogItemInfo RING_OF_THE_GODS_12601 = new LogItemInfo("Ring of the gods", 12601,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.VETION_KILLS, 1.0 / 512),
                    new RollInfo(LogItemSourceInfo.CALVARION_KILLS, 1.0 / 716)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_VETION_REWARDS_FRACTION_KEY));
    public static LogItemInfo ROBE_BOTTOMS_OF_THE_EYE_26854 = new LogItemInfo("Robe bottoms of the eye", 26854,
            new DeterministicDrop());
    public static LogItemInfo ROBE_BOTTOM_OF_DARKNESS_20137 = new LogItemInfo("Robe bottom of darkness", 20137,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo ROBE_TOP_OF_DARKNESS_20131 = new LogItemInfo("Robe top of darkness", 20131,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo ROBE_TOP_OF_THE_EYE_26852 = new LogItemInfo("Robe top of the eye", 26852,
            new DeterministicDrop());
    public static LogItemInfo ROBIN_HOOD_HAT_2581 = new LogItemInfo("Robin hood hat", 2581,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ROCKY_20663 = new LogItemInfo("Rocky", 20663,
            new MissingKillCountDrop());
    public static LogItemInfo ROCK_GOLEM_13321 = new LogItemInfo("Rock golem", 13321,
            new MissingKillCountDrop());
    public static LogItemInfo ROGUE_BOOTS_5557 = new LogItemInfo("Rogue boots", 5557,
            new MissingKillCountDrop());
    public static LogItemInfo ROGUE_GLOVES_5556 = new LogItemInfo("Rogue gloves", 5556,
            new MissingKillCountDrop());
    public static LogItemInfo ROGUE_MASK_5554 = new LogItemInfo("Rogue mask", 5554,
            new MissingKillCountDrop());
    public static LogItemInfo ROGUE_TOP_5553 = new LogItemInfo("Rogue top", 5553,
            new MissingKillCountDrop());
    public static LogItemInfo ROGUE_TROUSERS_5555 = new LogItemInfo("Rogue trousers", 5555,
            new MissingKillCountDrop());
    public static LogItemInfo ROYAL_CROWN_12397 = new LogItemInfo("Royal crown", 12397,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo ROYAL_GOWN_BOTTOM_12395 = new LogItemInfo("Royal gown bottom", 12395,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo ROYAL_GOWN_TOP_12393 = new LogItemInfo("Royal gown top", 12393,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo ROYAL_SCEPTRE_12439 = new LogItemInfo("Royal sceptre", 12439,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo RUM_8940 = new LogItemInfo("Rum", 8940,
            new DeterministicDrop());
    public static LogItemInfo RUM_8941 = new LogItemInfo("Rum", 8941,
            new DeterministicDrop());
    public static LogItemInfo RUNE_BOOTS_4131 = new LogItemInfo("Rune boots", 4131,
            new MissingKillCountDrop());
    public static LogItemInfo RUNE_CANE_12379 = new LogItemInfo("Rune cane", 12379,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_DEFENDER_8850 = new LogItemInfo("Rune defender", 8850,
            new MissingKillCountDrop());
    public static LogItemInfo RUNE_DEFENDER_ORNAMENT_KIT_23227 = new LogItemInfo("Rune defender ornament kit", 23227,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_DRAGON_MASK_23273 = new LogItemInfo("Rune dragon mask", 23273,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo RUNE_FULL_HELM_G_2619 = new LogItemInfo("Rune full helm (g)", 2619,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_FULL_HELM_T_2627 = new LogItemInfo("Rune full helm (t)", 2627,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_HELM_H1_10286 = new LogItemInfo("Rune helm (h1)", 10286,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_HELM_H2_10288 = new LogItemInfo("Rune helm (h2)", 10288,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_HELM_H3_10290 = new LogItemInfo("Rune helm (h3)", 10290,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_HELM_H4_10292 = new LogItemInfo("Rune helm (h4)", 10292,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_HELM_H5_10294 = new LogItemInfo("Rune helm (h5)", 10294,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_KITESHIELD_G_2621 = new LogItemInfo("Rune kiteshield (g)", 2621,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_KITESHIELD_T_2629 = new LogItemInfo("Rune kiteshield (t)", 2629,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_PLATEBODY_G_2615 = new LogItemInfo("Rune platebody (g)", 2615,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_PLATEBODY_H1_23209 = new LogItemInfo("Rune platebody (h1)", 23209,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 8125, 5)));
    public static LogItemInfo RUNE_PLATEBODY_H2_23212 = new LogItemInfo("Rune platebody (h2)", 23212,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 8125, 5)));
    public static LogItemInfo RUNE_PLATEBODY_H3_23215 = new LogItemInfo("Rune platebody (h3)", 23215,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 8125, 5)));
    public static LogItemInfo RUNE_PLATEBODY_H4_23218 = new LogItemInfo("Rune platebody (h4)", 23218,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 8125, 5)));
    public static LogItemInfo RUNE_PLATEBODY_H5_23221 = new LogItemInfo("Rune platebody (h5)", 23221,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 8125, 5)));
    public static LogItemInfo RUNE_PLATEBODY_T_2623 = new LogItemInfo("Rune platebody (t)", 2623,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_PLATELEGS_G_2617 = new LogItemInfo("Rune platelegs (g)", 2617,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_PLATELEGS_T_2625 = new LogItemInfo("Rune platelegs (t)", 2625,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_PLATESKIRT_G_3476 = new LogItemInfo("Rune plateskirt (g)", 3476,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_PLATESKIRT_T_3477 = new LogItemInfo("Rune plateskirt (t)", 3477,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_SATCHEL_25623 = new LogItemInfo("Rune satchel", 25623,
            new MissingKillCountDrop());
    public static LogItemInfo RUNE_SCIMITAR_ORNAMENT_KIT_GUTHIX_23321 = new LogItemInfo("Rune scimitar ornament kit (guthix)", 23321,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo RUNE_SCIMITAR_ORNAMENT_KIT_SARADOMIN_23324 = new LogItemInfo("Rune scimitar ornament kit (saradomin)", 23324,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo RUNE_SCIMITAR_ORNAMENT_KIT_ZAMORAK_23327 = new LogItemInfo("Rune scimitar ornament kit (zamorak)", 23327,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo RUNE_SHIELD_H1_7336 = new LogItemInfo("Rune shield (h1)", 7336,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_SHIELD_H2_7342 = new LogItemInfo("Rune shield (h2)", 7342,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_SHIELD_H3_7348 = new LogItemInfo("Rune shield (h3)", 7348,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_SHIELD_H4_7354 = new LogItemInfo("Rune shield (h4)", 7354,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNE_SHIELD_H5_7360 = new LogItemInfo("Rune shield (h5)", 7360,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo RUNNER_BOOTS_10552 = new LogItemInfo("Runner boots", 10552,
            new DeterministicDrop());
    public static LogItemInfo RUNNER_HAT_10549 = new LogItemInfo("Runner hat", 10549,
            new DeterministicDrop());
    public static LogItemInfo SAGACIOUS_SPECTACLES_12337 = new LogItemInfo("Sagacious spectacles", 12337,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo SAMURAI_BOOTS_20047 = new LogItemInfo("Samurai boots", 20047,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo SAMURAI_GLOVES_20041 = new LogItemInfo("Samurai gloves", 20041,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo SAMURAI_GREAVES_20044 = new LogItemInfo("Samurai greaves", 20044,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo SAMURAI_KASA_20035 = new LogItemInfo("Samurai kasa", 20035,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo SAMURAI_SHIRT_20038 = new LogItemInfo("Samurai shirt", 20038,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo SANDWICH_LADY_BOTTOM_23318 = new LogItemInfo("Sandwich lady bottom", 23318,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo SANDWICH_LADY_HAT_23312 = new LogItemInfo("Sandwich lady hat", 23312,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo SANDWICH_LADY_TOP_23315 = new LogItemInfo("Sandwich lady top", 23315,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo SANGUINESTI_STAFF_UNCHARGED_22481 = new LogItemInfo("Sanguinesti staff (uncharged)", 22481,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_COMPLETIONS, 1.0 / 86.45),
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 69.3)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_HM_POINTS_KEY)
    );
    // Always assume the player completes the HM raid within the challenge time.
    public static LogItemInfo SANGUINE_DUST_25746 = new LogItemInfo("Sanguine dust", 25746,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 275)));
    // Always assume the player completes the HM raid within the challenge time.
    public static LogItemInfo SANGUINE_ORNAMENT_KIT_25744 = new LogItemInfo("Sanguine ornament kit", 25744,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 150)));
    public static LogItemInfo SARACHNIS_CUDGEL_23528 = new LogItemInfo("Sarachnis cudgel", 23528,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SARACHNIS_KILLS, 1.0 / 384)));
    public static LogItemInfo SARADOMINS_LIGHT_13256 = new LogItemInfo("Saradomin's light", 13256,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.COMMANDER_ZILYANA_KILLS, 1.0 / 254)));
    public static LogItemInfo SARADOMIN_BANNER_11891 = new LogItemInfo("Saradomin banner", 11891,
            new DeterministicDrop());
    public static LogItemInfo SARADOMIN_BRACERS_10384 = new LogItemInfo("Saradomin bracers", 10384,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_CHAPS_10388 = new LogItemInfo("Saradomin chaps", 10388,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_CLOAK_10446 = new LogItemInfo("Saradomin cloak", 10446,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo SARADOMIN_COIF_10390 = new LogItemInfo("Saradomin coif", 10390,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_CROZIER_10440 = new LogItemInfo("Saradomin crozier", 10440,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_DHIDE_BODY_10386 = new LogItemInfo("Saradomin d'hide body", 10386,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_DHIDE_BOOTS_19933 = new LogItemInfo("Saradomin d'hide boots", 19933,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_DHIDE_SHIELD_23191 = new LogItemInfo("Saradomin d'hide shield", 23191,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 9750, 5)));
    public static LogItemInfo SARADOMIN_FULL_HELM_2665 = new LogItemInfo("Saradomin full helm", 2665,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_GODSWORD_ORNAMENT_KIT_20074 = new LogItemInfo("Saradomin godsword ornament kit", 20074,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 3404, 6)));
    public static LogItemInfo SARADOMIN_HALO_12637 = new LogItemInfo("Saradomin halo", 12637,
            new DeterministicDrop());
    public static LogItemInfo SARADOMIN_HILT_11814 = new LogItemInfo("Saradomin hilt", 11814,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.COMMANDER_ZILYANA_KILLS, 1.0 / 508)));
    public static LogItemInfo SARADOMIN_KITESHIELD_2667 = new LogItemInfo("Saradomin kiteshield", 2667,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_MITRE_10452 = new LogItemInfo("Saradomin mitre", 10452,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo SARADOMIN_PAGE_1_3827 = new LogItemInfo("Saradomin page 1", 3827,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo SARADOMIN_PAGE_2_3828 = new LogItemInfo("Saradomin page 2", 3828,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo SARADOMIN_PAGE_3_3829 = new LogItemInfo("Saradomin page 3", 3829,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo SARADOMIN_PAGE_4_3830 = new LogItemInfo("Saradomin page 4", 3830,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo SARADOMIN_PLATEBODY_2661 = new LogItemInfo("Saradomin platebody", 2661,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_PLATELEGS_2663 = new LogItemInfo("Saradomin platelegs", 2663,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_PLATESKIRT_3479 = new LogItemInfo("Saradomin plateskirt", 3479,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo SARADOMIN_ROBE_LEGS_10464 = new LogItemInfo("Saradomin robe legs", 10464,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo SARADOMIN_ROBE_TOP_10458 = new LogItemInfo("Saradomin robe top", 10458,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo SARADOMIN_STOLE_10470 = new LogItemInfo("Saradomin stole", 10470,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    // Note: Add 3 minion kills per kc.
    public static LogItemInfo SARADOMIN_SWORD_11838 = new LogItemInfo("Saradomin sword", 11838,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.COMMANDER_ZILYANA_KILLS, 1.0 / 127 + 3.0 / 5376)));
    public static LogItemInfo SCARRED_TABLET_28332 = new LogItemInfo("Scarred tablet", 28332,
            new DeterministicDrop());
    public static LogItemInfo SCORPIAS_OFFSPRING_13181 = new LogItemInfo("Scorpia's offspring", 13181,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SCORPIA_KILLS, 1.0 / 2016)));
    public static LogItemInfo SCRIBBLED_NOTE_21664 = new LogItemInfo("Scribbled note", 21664,
            new MissingKillCountDrop());
    public static LogItemInfo SCURRY_28801 = new LogItemInfo("Scurry", ItemID.SCURRY,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SCURRIUS_KILLS, 1.0 / 3000))
                    .withConfigOption(CollectionLogLuckConfig.AVG_SCURRIUS_MVP_RATE_KEY)
    );
    public static LogItemInfo SCURRIUS_SPINE_28798 = new LogItemInfo("Scurrius' spine", ItemID.SCURRIUS_SPINE,
            // Scurrius' spine not affected by MVP rate
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SCURRIUS_KILLS, 1.0 / 33))
    );
    public static LogItemInfo SCYTHE_OF_VITUR_UNCHARGED_22486 = new LogItemInfo("Scythe of vitur (uncharged)", 22486,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_COMPLETIONS, 1.0 / 172.9),
                    new RollInfo(LogItemSourceInfo.THEATRE_OF_BLOOD_HARD_COMPLETIONS, 1.0 / 138.6)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_TOB_HM_POINTS_KEY)
    );
    public static LogItemInfo SEED_BOX_13639 = new LogItemInfo("Seed box", 13639,
            new DeterministicDrop());
    public static LogItemInfo SEERCULL_6724 = new LogItemInfo("Seercull", 6724,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DAGANNOTH_SUPREME_KILLS, 1.0 / 128)));
    public static LogItemInfo SEERS_RING_6731 = new LogItemInfo("Seers ring", 6731,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DAGANNOTH_PRIME_KILLS, 1.0 / 128)));
    public static LogItemInfo SEREN_HALO_24198 = new LogItemInfo("Seren halo", 24198,
            new DeterministicDrop());
    public static LogItemInfo SERPENTINE_VISAGE_12927 = new LogItemInfo("Serpentine visage", 12927,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZULRAH_KILLS, 1.0 / 1024, 2)));
    public static LogItemInfo SHADOW_QUARTZ_28272 = new LogItemInfo("Shadow quartz", 28272,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 63.0 / 64.0 * 33.0 / 34.0 * 24.0 / 25.0 / 200)));
    public static LogItemInfo SHAMAN_MASK_21838 = new LogItemInfo("Shaman mask", 21838,
            new MissingKillCountDrop());
    public static LogItemInfo SHAYZIEN_BANNER_20263 = new LogItemInfo("Shayzien banner", 20263,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo SHAYZIEN_BODY_5_13381 = new LogItemInfo("Shayzien body (5)", 13381,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_BOOTS_1_13358 = new LogItemInfo("Shayzien boots (1)", 13358,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_BOOTS_2_13363 = new LogItemInfo("Shayzien boots (2)", 13363,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_BOOTS_3_13368 = new LogItemInfo("Shayzien boots (3)", 13368,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_BOOTS_4_13373 = new LogItemInfo("Shayzien boots (4)", 13373,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_BOOTS_5_13378 = new LogItemInfo("Shayzien boots (5)", 13378,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GLOVES_1_13357 = new LogItemInfo("Shayzien gloves (1)", 13357,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GLOVES_2_13362 = new LogItemInfo("Shayzien gloves (2)", 13362,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GLOVES_3_13367 = new LogItemInfo("Shayzien gloves (3)", 13367,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GLOVES_4_13372 = new LogItemInfo("Shayzien gloves (4)", 13372,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GLOVES_5_13377 = new LogItemInfo("Shayzien gloves (5)", 13377,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GREAVES_1_13360 = new LogItemInfo("Shayzien greaves (1)", 13360,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GREAVES_2_13365 = new LogItemInfo("Shayzien greaves (2)", 13365,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GREAVES_3_13370 = new LogItemInfo("Shayzien greaves (3)", 13370,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GREAVES_4_13375 = new LogItemInfo("Shayzien greaves (4)", 13375,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_GREAVES_5_13380 = new LogItemInfo("Shayzien greaves (5)", 13380,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_HELM_1_13359 = new LogItemInfo("Shayzien helm (1)", 13359,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_HELM_2_13364 = new LogItemInfo("Shayzien helm (2)", 13364,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_HELM_3_13369 = new LogItemInfo("Shayzien helm (3)", 13369,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_HELM_4_13374 = new LogItemInfo("Shayzien helm (4)", 13374,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_HELM_5_13379 = new LogItemInfo("Shayzien helm (5)", 13379,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_HOOD_20125 = new LogItemInfo("Shayzien hood", 20125,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo SHAYZIEN_PLATEBODY_1_13361 = new LogItemInfo("Shayzien platebody (1)", 13361,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_PLATEBODY_2_13366 = new LogItemInfo("Shayzien platebody (2)", 13366,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_PLATEBODY_3_13371 = new LogItemInfo("Shayzien platebody (3)", 13371,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_PLATEBODY_4_13376 = new LogItemInfo("Shayzien platebody (4)", 13376,
            new DeterministicDrop());
    public static LogItemInfo SHAYZIEN_SCARF_19955 = new LogItemInfo("Shayzien scarf", 19955,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo SHIELD_LEFT_HALF_2366 = new LogItemInfo("Shield left half", 2366,
            new MissingKillCountDrop());
    public static LogItemInfo SHORT_NOTE_21682 = new LogItemInfo("Short note", 21682,
            new MissingKillCountDrop());
    public static LogItemInfo SHOULDER_PARROT_23300 = new LogItemInfo("Shoulder parrot", 23300,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BEGINNER_CLUES_COMPLETED, 1.0 / 360, 2)));
    public static LogItemInfo SILVER_LOCKS_25451 = new LogItemInfo("Silver locks", 25451,
            new MissingKillCountDrop());
    public static LogItemInfo SINHAZA_SHROUD_TIER_1_22494 = new LogItemInfo("Sinhaza shroud tier 1", 22494,
            new DeterministicDrop());
    public static LogItemInfo SINHAZA_SHROUD_TIER_2_22496 = new LogItemInfo("Sinhaza shroud tier 2", 22496,
            new DeterministicDrop());
    public static LogItemInfo SINHAZA_SHROUD_TIER_3_22498 = new LogItemInfo("Sinhaza shroud tier 3", 22498,
            new DeterministicDrop());
    public static LogItemInfo SINHAZA_SHROUD_TIER_4_22500 = new LogItemInfo("Sinhaza shroud tier 4", 22500,
            new DeterministicDrop());
    public static LogItemInfo SINHAZA_SHROUD_TIER_5_22502 = new LogItemInfo("Sinhaza shroud tier 5", 22502,
            new DeterministicDrop());
    public static LogItemInfo SIRENIC_TABLET_28331 = new LogItemInfo("Sirenic tablet", 28331,
            new DeterministicDrop());
    public static LogItemInfo SIRENS_STAFF_28323 = new LogItemInfo("Siren's staff", 28323,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 1.0 / 512)));
    public static LogItemInfo SKELETAL_VISAGE_22006 = new LogItemInfo("Skeletal visage", 22006,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.VORKATH_KILLS, 1.0 / 5000)));
    public static LogItemInfo SKELETON_CHAMPION_SCROLL_6806 = new LogItemInfo("Skeleton champion scroll", 6806,
            new MissingKillCountDrop());
    public static LogItemInfo SKELETON_MONKEY_24865 = new LogItemInfo("Skeleton monkey", 24865,
            new DeterministicDrop());
    public static LogItemInfo SKOTOS_21273 = new LogItemInfo("Skotos", 21273,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SKOTIZO_KILLS, 1.0 / 65)));
    public static LogItemInfo SKULL_OF_VETION_27673 = new LogItemInfo("Skull of vet'ion", 27673,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.VETION_KILLS, 1.0 / 196),
                    new RollInfo(LogItemSourceInfo.CALVARION_KILLS, 1.0 / 618)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_VETION_REWARDS_FRACTION_KEY));
    public static LogItemInfo SLEEPING_CAP_10398 = new LogItemInfo("Sleeping cap", 10398,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo SLEPEY_TABLET_25837 = new LogItemInfo("Slepey tablet", 25837,
            new UnimplementedDrop());
    public static LogItemInfo SMITHING_CATALYST_27017 = new LogItemInfo("Smithing catalyst", 27017,
            new DeterministicDrop());
    public static LogItemInfo SMITHS_BOOTS_27027 = new LogItemInfo("Smiths boots", 27027,
            new DeterministicDrop());
    public static LogItemInfo SMITHS_GLOVES_27029 = new LogItemInfo("Smiths gloves", 27029,
            new DeterministicDrop());
    public static LogItemInfo SMITHS_TROUSERS_27025 = new LogItemInfo("Smiths trousers", 27025,
            new DeterministicDrop());
    public static LogItemInfo SMITHS_TUNIC_27023 = new LogItemInfo("Smiths tunic", 27023,
            new DeterministicDrop());
    public static LogItemInfo SMOKE_BATTLESTAFF_11998 = new LogItemInfo("Smoke battlestaff", 11998,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.THERMONUCLEAR_SMOKE_DEVIL_KILLS, 1.0 / 512)));
    public static LogItemInfo SMOKE_QUARTZ_28274 = new LogItemInfo("Smoke quartz", 28274,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.LEVIATHAN_KILLS, 95.0 / 96.0 * 52.0 / 53.0 * 24.0 / 25.0 / 200)));
    // This drop is not affected by performance / contribution.
    public static LogItemInfo SMOLCANO_23760 = new LogItemInfo("Smolcano", 23760,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZALCANO_KILLS, 1.0 / 2250)));
    public static LogItemInfo SMOULDERING_STONE_13233 = new LogItemInfo("Smouldering stone", 13233,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CERBERUS_KILLS, 1.0 / 512)));
    public static LogItemInfo SOAKED_PAGE_25578 = new LogItemInfo("Soaked page", 25578,
            new PoissonBinomialStackDrop());
    public static LogItemInfo SOUL_CAPE_25346 = new LogItemInfo("Soul cape", 25346,
            new DeterministicDrop());
    public static LogItemInfo SPECTRAL_SIGIL_12823 = new LogItemInfo("Spectral sigil", 12823,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CORPOREAL_BEAST_KILLS, 1.0 / 1365)));
    public static LogItemInfo SPIKED_MANACLES_23389 = new LogItemInfo("Spiked manacles", 23389,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo SPIRIT_ANGLER_BOOTS_25598 = new LogItemInfo("Spirit angler boots", 25598,
            new DeterministicDrop());
    public static LogItemInfo SPIRIT_ANGLER_HEADBAND_25592 = new LogItemInfo("Spirit angler headband", 25592,
            new DeterministicDrop());
    public static LogItemInfo SPIRIT_ANGLER_TOP_25594 = new LogItemInfo("Spirit angler top", 25594,
            new DeterministicDrop());
    public static LogItemInfo SPIRIT_ANGLER_WADERS_25596 = new LogItemInfo("Spirit angler waders", 25596,
            new DeterministicDrop());
    public static LogItemInfo SPIRIT_FLAKES_25588 = new LogItemInfo("Spirit flakes", 25588,
            new BinomialUniformSumDrop(
                    new RollInfo(LogItemSourceInfo.REWARD_PERMITS_CLAIMED, 1.0 / 4),
                    32, 64
            ));
    public static LogItemInfo SPIRIT_SHIELD_12829 = new LogItemInfo("Spirit shield", 12829,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CORPOREAL_BEAST_KILLS, 1.0 / 64)));
    public static LogItemInfo SRARACHA_23495 = new LogItemInfo("Sraracha", 23495,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.SARACHNIS_KILLS, 1.0 / 3000)));
    public static LogItemInfo STAFF_OF_BOB_THE_CAT_23363 = new LogItemInfo("Staff of bob the cat", 23363,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STAFF_OF_THE_DEAD_11791 = new LogItemInfo("Staff of the dead", 11791,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KRIL_TSUTSAROTH_KILLS, 1.0 / 508)));
    public static LogItemInfo STALE_BAGUETTE_20590 = new LogItemInfo("Stale baguette", 20590,
            new MissingKillCountDrop());
    public static LogItemInfo STAR_FRAGMENT_25547 = new LogItemInfo("Star fragment", 25547,
            new DeterministicDrop());
    public static LogItemInfo STEAM_BATTLESTAFF_11787 = new LogItemInfo("Steam battlestaff", 11787,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KRIL_TSUTSAROTH_KILLS, 1.0 / 127)));
    public static LogItemInfo STEAM_STAFF_UPGRADE_KIT_12798 = new LogItemInfo("Steam staff upgrade kit", 12798,
            new DeterministicDrop());
    public static LogItemInfo STEEL_BOOTS_4123 = new LogItemInfo("Steel boots", 4123,
            new MissingKillCountDrop());
    public static LogItemInfo STEEL_DEFENDER_8846 = new LogItemInfo("Steel defender", 8846,
            new MissingKillCountDrop());
    public static LogItemInfo STEEL_DRAGON_MASK_12367 = new LogItemInfo("Steel dragon mask", 12367,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo STEEL_FULL_HELM_G_20178 = new LogItemInfo("Steel full helm (g)", 20178,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STEEL_FULL_HELM_T_20193 = new LogItemInfo("Steel full helm (t)", 20193,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STEEL_KITESHIELD_G_20181 = new LogItemInfo("Steel kiteshield (g)", 20181,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STEEL_KITESHIELD_T_20196 = new LogItemInfo("Steel kiteshield (t)", 20196,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STEEL_LOCKS_25445 = new LogItemInfo("Steel locks", 25445,
            new MissingKillCountDrop());
    public static LogItemInfo STEEL_PLATEBODY_G_20169 = new LogItemInfo("Steel platebody (g)", 20169,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STEEL_PLATEBODY_T_20184 = new LogItemInfo("Steel platebody (t)", 20184,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STEEL_PLATELEGS_G_20172 = new LogItemInfo("Steel platelegs (g)", 20172,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STEEL_PLATELEGS_T_20187 = new LogItemInfo("Steel platelegs (t)", 20187,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STEEL_PLATESKIRT_G_20175 = new LogItemInfo("Steel plateskirt (g)", 20175,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STEEL_PLATESKIRT_T_20190 = new LogItemInfo("Steel plateskirt (t)", 20190,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STRANGE_OLD_LOCKPICK_FULL_24740 = new LogItemInfo("Strange old lockpick (full)", 24740,
            new MissingKillCountDrop());
    public static LogItemInfo STRANGLED_TABLET_28330 = new LogItemInfo("Strangled tablet", 28330,
            new DeterministicDrop());
    public static LogItemInfo STRENGTH_AMULET_T_10364 = new LogItemInfo("Strength amulet (t)", 10364,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 341, 4)));
    public static LogItemInfo STUDDED_BODY_G_7362 = new LogItemInfo("Studded body (g)", 7362,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STUDDED_BODY_T_7364 = new LogItemInfo("Studded body (t)", 7364,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STUDDED_CHAPS_G_7366 = new LogItemInfo("Studded chaps (g)", 7366,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo STUDDED_CHAPS_T_7368 = new LogItemInfo("Studded chaps (t)", 7368,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo SUPERIOR_MINING_GLOVES_21345 = new LogItemInfo("Superior mining gloves", 21345,
            new DeterministicDrop());
    public static LogItemInfo SUPPLY_CRATE_24884 = new LogItemInfo("Supply crate", 24884,
            new DeterministicDrop());
    public static LogItemInfo SWIFT_BLADE_24219 = new LogItemInfo("Swift blade", 24219,
            new DeterministicDrop());
    public static LogItemInfo TACKLE_BOX_25580 = new LogItemInfo("Tackle box", 25580,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.REWARD_PERMITS_CLAIMED, 1.0 / 400)));
    public static LogItemInfo TAI_BWO_WANNAI_TELEPORT_12409 = new LogItemInfo("Tai bwo wannai teleport", 12409,
            new PoissonBinomialStackDrop());
    public static LogItemInfo TANGLEROOT_20661 = new LogItemInfo("Tangleroot", 20661,
            new MissingKillCountDrop());
    public static LogItemInfo TANZANITE_FANG_12922 = new LogItemInfo("Tanzanite fang", 12922,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZULRAH_KILLS, 1.0 / 1024, 2)));
    public static LogItemInfo TANZANITE_MUTAGEN_13200 = new LogItemInfo("Tanzanite mutagen", 13200,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZULRAH_KILLS, 1.0 / 13106, 2)));
    public static LogItemInfo TAN_CAVALIER_2639 = new LogItemInfo("Tan cavalier", 2639,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo TARNISHED_LOCKET_26910 = new LogItemInfo("Tarnished locket", 26910,
            // Assumes the player opens their intricate pouches
            new BinomialDrop(new RollInfo(LogItemSourceInfo.RIFTS_SEARCHES, 1.0 / 25.0 / 40)));
    public static LogItemInfo TEACHER_WAND_6912 = new LogItemInfo("Teacher wand", 6912,
            new DeterministicDrop());
    public static LogItemInfo TEAM_CAPE_I_20217 = new LogItemInfo("Team cape i", 20217,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 5616, 3)));
    public static LogItemInfo TEAM_CAPE_X_20214 = new LogItemInfo("Team cape x", 20214,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 5616, 3)));
    public static LogItemInfo TEAM_CAPE_ZERO_20211 = new LogItemInfo("Team cape zero", 20211,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 5616, 3)));
    public static LogItemInfo TEA_FLASK_25617 = new LogItemInfo("Tea flask", 25617,
            new MissingKillCountDrop());
    public static LogItemInfo THAMMARONS_SCEPTRE_U_22552 = new LogItemInfo("Thammaron's sceptre (u)", 22552,
            new MissingKillCountDrop());
    public static LogItemInfo THE_STUFF_8988 = new LogItemInfo("The stuff", 8988,
            new DeterministicDrop());
    public static LogItemInfo THIEVING_BAG_23224 = new LogItemInfo("Thieving bag", 23224,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo THREAD_OF_ELIDINIS_27279 = new LogItemInfo("Thread of elidinis", 27279,
            new UnimplementedDrop());
    public static LogItemInfo TINY_TEMPOR_25602 = new LogItemInfo("Tiny tempor", 25602,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.REWARD_PERMITS_CLAIMED, 1.0 / 8000)));
    public static LogItemInfo TOKTZ_KET_XIL_6524 = new LogItemInfo("Toktz-ket-xil", 6524,
            new MissingKillCountDrop());
    public static LogItemInfo TOKTZ_MEJ_TAL_6526 = new LogItemInfo("Toktz-mej-tal", 6526,
            new MissingKillCountDrop());
    public static LogItemInfo TOKTZ_XIL_AK_6523 = new LogItemInfo("Toktz-xil-ak", 6523,
            new MissingKillCountDrop());
    public static LogItemInfo TOKTZ_XIL_EK_6525 = new LogItemInfo("Toktz-xil-ek", 6525,
            new MissingKillCountDrop());
    public static LogItemInfo TOKTZ_XIL_UL_6522 = new LogItemInfo("Toktz-xil-ul", 6522,
            new MissingKillCountDrop());
    public static LogItemInfo TOME_OF_FIRE_EMPTY_20716 = new LogItemInfo("Tome of fire (empty)", 20716,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.WINTERTODT_KILLS, 1.0 / 1000.3))
                    .withConfigOption(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY)
    );
    public static LogItemInfo TOME_OF_WATER_EMPTY_25576 = new LogItemInfo("Tome of water (empty)", 25576,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.REWARD_PERMITS_CLAIMED, 1.0 / 1600)));
    public static LogItemInfo TOP_HAT_12432 = new LogItemInfo("Top hat", 12432,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo TOP_OF_SCEPTRE_9010 = new LogItemInfo("Top of sceptre", 9010,
            new MissingKillCountDrop());
    public static LogItemInfo TORAGS_HAMMERS_4747 = new LogItemInfo("Torag's hammers", 4747,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo TORAGS_HELM_4745 = new LogItemInfo("Torag's helm", 4745,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo TORAGS_PLATEBODY_4749 = new LogItemInfo("Torag's platebody", 4749,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo TORAGS_PLATELEGS_4751 = new LogItemInfo("Torag's platelegs", 4751,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo TORMENTED_ORNAMENT_KIT_23348 = new LogItemInfo("Tormented ornament kit", 23348,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo TORN_PRAYER_SCROLL_21047 = new LogItemInfo("Torn prayer scroll", 21047,
            new BinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 33, 2),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 33, 2)
            )));
    public static LogItemInfo TORTURE_ORNAMENT_KIT_20062 = new LogItemInfo("Torture ornament kit", 20062,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 851, 6)));
    public static LogItemInfo TORVA_FULL_HELM_DAMAGED_26376 = new LogItemInfo("Torva full helm (damaged)", 26376,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.NEX_KILLS, 1.0 / 258))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NEX_REWARDS_FRACTION_KEY)
    );
    public static LogItemInfo TORVA_PLATEBODY_DAMAGED_26378 = new LogItemInfo("Torva platebody (damaged)", 26378,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.NEX_KILLS, 1.0 / 258))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NEX_REWARDS_FRACTION_KEY)
    );
    public static LogItemInfo TORVA_PLATELEGS_DAMAGED_26380 = new LogItemInfo("Torva platelegs (damaged)", 26380,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.NEX_KILLS, 1.0 / 258))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NEX_REWARDS_FRACTION_KEY)
    );
    public static LogItemInfo TREASONOUS_RING_12605 = new LogItemInfo("Treasonous ring", 12605,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.VENENATIS_KILLS, 1.0 / 512),
                    new RollInfo(LogItemSourceInfo.SPINDEL_KILLS, 1.0 / 716)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_VENENATIS_REWARDS_FRACTION_KEY));
    public static LogItemInfo TREASURE_FLAG_8970 = new LogItemInfo("Treasure flag", 8970,
            new DeterministicDrop());
    public static LogItemInfo TREE_WIZARDS_JOURNAL_25474 = new LogItemInfo("Tree wizards' journal", 25474,
            new MissingKillCountDrop());
    public static LogItemInfo TRIDENT_OF_THE_SEAS_FULL_11905 = new LogItemInfo("Trident of the seas (full)", 11905,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KRAKEN_KILLS, 1.0 / 512)));
    public static LogItemInfo TUMEKENS_GUARDIAN_27352 = new LogItemInfo("Tumeken's guardian", 27352,
            new PoissonBinomialDrop(ImmutableList.of(
                    // drop chance is handled elsewhere
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS, 1.0),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS, 1.0),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS, 1.0)
            ))
                    .withConfigOption("Tumeken's guardian")
    );
    public static LogItemInfo TUMEKENS_SHADOW_UNCHARGED_27277 = new LogItemInfo("Tumeken's shadow (uncharged)", 27277,
            new PoissonBinomialDrop(ImmutableList.of(
                    // drop chance is reduced by 98% in entry mode
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS, 1.0 / 24 / 50),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_COMPLETIONS, 1.0 / 24),
                    new RollInfo(LogItemSourceInfo.TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS, 1.0 / 24)
            ))
                    .withConfigOption(CollectionLogLuckConfig.ENTRY_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.REGULAR_TOA_UNIQUE_CHANCE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.EXPERT_TOA_UNIQUE_CHANCE_KEY)
    );
    // Always assume the player completes the CM raid within the challenge time.
    public static LogItemInfo TWISTED_ANCESTRAL_COLOUR_KIT_24670 = new LogItemInfo("Twisted ancestral colour kit", 24670,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 75)));
    public static LogItemInfo TWISTED_BOW_20997 = new LogItemInfo("Twisted bow", 20997,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 34.5),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 34.5)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY)
    );
    public static LogItemInfo TWISTED_BUCKLER_21000 = new LogItemInfo("Twisted buckler", 21000,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_COMPLETIONS, 1.0 / 17.25),
                    new RollInfo(LogItemSourceInfo.CHAMBERS_OF_XERIC_CM_COMPLETIONS, 1.0 / 17.25)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_POINTS_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_PERSONAL_COX_CM_POINTS_KEY));
    public static LogItemInfo TYRANNICAL_RING_12603 = new LogItemInfo("Tyrannical ring", 12603,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CALLISTO_KILLS, 1.0 / 512),
                    new RollInfo(LogItemSourceInfo.ARTIO_KILLS, 1.0 / 716)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_CALLISTO_REWARDS_FRACTION_KEY));
    public static LogItemInfo TZHAAR_KET_OM_6528 = new LogItemInfo("Tzhaar-ket-om", 6528,
            new MissingKillCountDrop());
    public static LogItemInfo TZHAAR_KET_OM_ORNAMENT_KIT_23232 = new LogItemInfo("Tzhaar-ket-om ornament kit", 23232,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo TZREK_JAD_13225 = new LogItemInfo("Tzrek-jad", 13225,
            new PoissonBinomialDrop(ImmutableList.of(
                    // First kill drop rate (no slayer task)
                    new RollInfo(LogItemSourceInfo.TZTOK_JAD_KILLS, 1.0 / 200),
                    // Slayer task drop rate (all subsequent KC)
                    new RollInfo(LogItemSourceInfo.TZTOK_JAD_KILLS, 1.0 / 100),
                    // Cape sacrifice drop rate
                    new RollInfo(LogItemSourceInfo.TZTOK_JAD_KILLS, 1.0 / 200)
            ))
                    .withConfigOption(CollectionLogLuckConfig.NUM_FIRE_CAPES_SACRIFICED_KEY));
    public static LogItemInfo ULTOR_VESTIGE_28285 = new LogItemInfo("Ultor vestige", 28285,
            new HiddenShardDrop(new RollInfo(LogItemSourceInfo.VARDORVIS_KILLS, 1.0 / 136.0 * 3.0 / 8.0), 3));
    public static LogItemInfo UNCHARGED_TRIDENT_11908 = new LogItemInfo("Uncharged trident", 11908,
            new MissingKillCountDrop());
    public static LogItemInfo UNCUT_ONYX_6571 = new LogItemInfo("Uncut onyx", 6571,
            new MissingKillCountDrop());
    public static LogItemInfo UNHOLY_BLESSING_20223 = new LogItemInfo("Unholy blessing", 20223,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 606.4, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 645.8, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 541.7, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 682, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2160, 3)
            )));
    public static LogItemInfo UNSIRED_25624 = new LogItemInfo("Unsired", 25624,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ABYSSAL_SIRE_KILLS, 1.0 / 100)));
    public static LogItemInfo URIS_HAT_23255 = new LogItemInfo("Uri's hat", 23255,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 1275, 5)));
    public static LogItemInfo VENATOR_SHARD_27614 = new LogItemInfo("Venator shard", 27614,
            // includes chance from frozen cache(s)
            new BinomialDrop(new RollInfo(LogItemSourceInfo.PHANTOM_MUSPAH_KILLS, 1.0 / 100 + 1.0 / 24.8 / 500)));
    public static LogItemInfo VENATOR_VESTIGE_28283 = new LogItemInfo("Venator vestige", 28283,
            new HiddenShardDrop(new RollInfo(LogItemSourceInfo.LEVIATHAN_KILLS, 1.0 / 96.0 * 3.0 / 8.0), 3));
    public static LogItemInfo VENENATIS_SPIDERLING_13177 = new LogItemInfo("Venenatis spiderling", 13177,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.VENENATIS_KILLS, 1.0 / 2000),
                    new RollInfo(LogItemSourceInfo.SPINDEL_KILLS, 1.0 / 2800)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_VENENATIS_REWARDS_FRACTION_KEY));
    public static LogItemInfo VERACS_BRASSARD_4757 = new LogItemInfo("Verac's brassard", 4757,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo VERACS_FLAIL_4755 = new LogItemInfo("Verac's flail", 4755,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo VERACS_HELM_4753 = new LogItemInfo("Verac's helm", 4753,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo VERACS_PLATESKIRT_4759 = new LogItemInfo("Verac's plateskirt", 4759,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.BARROWS_CHESTS_OPENED, 1.0 / 2448, 7))
                    .withConfigOption(CollectionLogLuckConfig.NUM_INVALID_BARROWS_KC_KEY));
    public static LogItemInfo VIAL_OF_BLOOD_22446 = new LogItemInfo("Vial of blood", 22446,
            new PoissonBinomialStackDrop());
    public static LogItemInfo VICTORS_CAPE_1000_24520 = new LogItemInfo("Victor's cape (1000)", 24520,
            new DeterministicDrop());
    public static LogItemInfo VICTORS_CAPE_100_24213 = new LogItemInfo("Victor's cape (100)", 24213,
            new DeterministicDrop());
    public static LogItemInfo VICTORS_CAPE_10_24209 = new LogItemInfo("Victor's cape (10)", 24209,
            new DeterministicDrop());
    public static LogItemInfo VICTORS_CAPE_1_24207 = new LogItemInfo("Victor's cape (1)", 24207,
            new DeterministicDrop());
    public static LogItemInfo VICTORS_CAPE_500_24215 = new LogItemInfo("Victor's cape (500)", 24215,
            new DeterministicDrop());
    public static LogItemInfo VICTORS_CAPE_50_24211 = new LogItemInfo("Victor's cape (50)", 24211,
            new DeterministicDrop());
    public static LogItemInfo VIGGORAS_CHAINMACE_U_22542 = new LogItemInfo("Viggora's chainmace (u)", 22542,
            new MissingKillCountDrop());
    public static LogItemInfo VIRTUS_MASK_26241 = new LogItemInfo("Virtus mask", 26241,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 1.0 / 1536),
                    new RollInfo(LogItemSourceInfo.DUKE_SUCELLUS_KILLS, 1.0 / 2160),
                    new RollInfo(LogItemSourceInfo.LEVIATHAN_KILLS, 1.0 / 2304),
                    new RollInfo(LogItemSourceInfo.VARDORVIS_KILLS, 1.0 / 3264)
            )));
    public static LogItemInfo VIRTUS_ROBE_BOTTOM_26245 = new LogItemInfo("Virtus robe bottom", 26245,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 1.0 / 1536),
                    new RollInfo(LogItemSourceInfo.DUKE_SUCELLUS_KILLS, 1.0 / 2160),
                    new RollInfo(LogItemSourceInfo.LEVIATHAN_KILLS, 1.0 / 2304),
                    new RollInfo(LogItemSourceInfo.VARDORVIS_KILLS, 1.0 / 3264)
            )));
    public static LogItemInfo VIRTUS_ROBE_TOP_26243 = new LogItemInfo("Virtus robe top", 26243,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 1.0 / 1536),
                    new RollInfo(LogItemSourceInfo.DUKE_SUCELLUS_KILLS, 1.0 / 2160),
                    new RollInfo(LogItemSourceInfo.LEVIATHAN_KILLS, 1.0 / 2304),
                    new RollInfo(LogItemSourceInfo.VARDORVIS_KILLS, 1.0 / 3264)
            )));
    public static LogItemInfo VOIDWAKER_BLADE_27684 = new LogItemInfo("Voidwaker blade", 27684,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.VETION_KILLS, 1.0 / 360),
                    new RollInfo(LogItemSourceInfo.CALVARION_KILLS, 1.0 / 912)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_VETION_REWARDS_FRACTION_KEY));
    public static LogItemInfo VOIDWAKER_GEM_27687 = new LogItemInfo("Voidwaker gem", 27687,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.VENENATIS_KILLS, 1.0 / 360),
                    new RollInfo(LogItemSourceInfo.SPINDEL_KILLS, 1.0 / 912)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_VENENATIS_REWARDS_FRACTION_KEY));
    public static LogItemInfo VOIDWAKER_HILT_27681 = new LogItemInfo("Voidwaker hilt", 27681,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.CALLISTO_KILLS, 1.0 / 360),
                    new RollInfo(LogItemSourceInfo.ARTIO_KILLS, 1.0 / 912)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_CALLISTO_REWARDS_FRACTION_KEY));
    public static LogItemInfo VOID_KNIGHT_GLOVES_8842 = new LogItemInfo("Void knight gloves", 8842,
            new DeterministicDrop());
    public static LogItemInfo VOID_KNIGHT_MACE_8841 = new LogItemInfo("Void knight mace", 8841,
            new DeterministicDrop());
    public static LogItemInfo VOID_KNIGHT_ROBE_8840 = new LogItemInfo("Void knight robe", 8840,
            new DeterministicDrop());
    public static LogItemInfo VOID_KNIGHT_TOP_8839 = new LogItemInfo("Void knight top", 8839,
            new DeterministicDrop());
    public static LogItemInfo VOID_MAGE_HELM_11663 = new LogItemInfo("Void mage helm", 11663,
            new DeterministicDrop());
    public static LogItemInfo VOID_MELEE_HELM_11665 = new LogItemInfo("Void melee helm", 11665,
            new DeterministicDrop());
    public static LogItemInfo VOID_RANGER_HELM_11664 = new LogItemInfo("Void ranger helm", 11664,
            new DeterministicDrop());
    public static LogItemInfo VOID_SEAL8_11666 = new LogItemInfo("Void seal(8)", 11666,
            new DeterministicDrop());
    public static LogItemInfo VOLATILE_ORB_24514 = new LogItemInfo("Volatile orb", 24514,
            new PoissonBinomialDrop(ImmutableList.of(
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 1800),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.NIGHTMARE_KILLS, 1.0 / 960),
                    // pre-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 3000),
                    // post-buff drop rate
                    new RollInfo(LogItemSourceInfo.PHOSANIS_NIGHTMARE_KILLS, 1.0 / 1600)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_TEAM_SIZE_KEY)
                    .withConfigOption(CollectionLogLuckConfig.AVG_NIGHTMARE_REWARDS_FRACTION_KEY)
                    .withConfigOption(CollectionLogLuckConfig.NIGHTMARE_KC_PRE_BUFF_KEY)
                    .withConfigOption(CollectionLogLuckConfig.PHOSANIS_NIGHTMARE_KC_PRE_BUFF_KEY)
    );
    public static LogItemInfo VOLCANIC_MINE_TELEPORT_21541 = new LogItemInfo("Volcanic mine teleport", 21541,
            new DeterministicDrop());
    public static LogItemInfo VOLCANIC_WHIP_MIX_12771 = new LogItemInfo("Volcanic whip mix", 12771,
            new DeterministicDrop());
    public static LogItemInfo VORKATHS_HEAD_21907 = new LogItemInfo("Vorkath's head", 21907,
            new GuaranteedOnceBinomialDrop(new RollInfo(LogItemSourceInfo.VORKATH_KILLS, 1.0 / 50), 50));
    public static LogItemInfo VORKI_21992 = new LogItemInfo("Vorki", 21992,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.VORKATH_KILLS, 1.0 / 3000)));
    public static LogItemInfo WARD_UPGRADE_KIT_12802 = new LogItemInfo("Ward upgrade kit", 12802,
            new DeterministicDrop());
    public static LogItemInfo WARM_GLOVES_20712 = new LogItemInfo("Warm gloves", 20712,
            // Note: This ignores the mechanic where magic seeds are dropped instead if the player has > 3 warm gloves.
            // Users will probably understand why their luck is lower than expected if they've purposely kept 3+...
            new BinomialDrop(new RollInfo(LogItemSourceInfo.WINTERTODT_KILLS, 1.0 / 150.2))
                    .withConfigOption(CollectionLogLuckConfig.NUM_ROLLS_PER_WINTERTODT_CRATE_KEY)
    );
    public static LogItemInfo WARRIOR_RING_6735 = new LogItemInfo("Warrior ring", 6735,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.DAGANNOTH_REX_KILLS, 1.0 / 128)));
    public static LogItemInfo WAR_BLESSING_20232 = new LogItemInfo("War blessing", 20232,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 606.4, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 645.8, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 541.7, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 682, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 2160, 3)
            )));
    public static LogItemInfo WHITE_BERET_2637 = new LogItemInfo("White beret", 2637,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo WHITE_BOATER_12313 = new LogItemInfo("White boater", 12313,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo WHITE_CAVALIER_12321 = new LogItemInfo("White cavalier", 12321,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo WHITE_DARK_BOW_PAINT_12763 = new LogItemInfo("White dark bow paint", 12763,
            new DeterministicDrop());
    public static LogItemInfo WHITE_ELEGANT_BLOUSE_10420 = new LogItemInfo("White elegant blouse", 10420,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo WHITE_ELEGANT_SKIRT_10422 = new LogItemInfo("White elegant skirt", 10422,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo WHITE_FIRELIGHTER_10327 = new LogItemInfo("White firelighter", 10327,
            new PoissonBinomialStackDrop());
    public static LogItemInfo WHITE_HEADBAND_12299 = new LogItemInfo("White headband", 12299,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo WHITE_UNICORN_MASK_20269 = new LogItemInfo("White unicorn mask", 20269,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 2266, 4)));
    public static LogItemInfo WILLOW_COMP_BOW_10280 = new LogItemInfo("Willow comp bow", 10280,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 360, 3)));
    public static LogItemInfo WISP_28246 = new LogItemInfo("Wisp", 28246,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.WHISPERER_KILLS, 1.0 / 2000)));
    public static LogItemInfo WIZARD_BOOTS_2579 = new LogItemInfo("Wizard boots", 2579,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo WOLF_CLOAK_23410 = new LogItemInfo("Wolf cloak", 23410,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo WOLF_MASK_23407 = new LogItemInfo("Wolf mask", 23407,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo WOODEN_SHIELD_G_20166 = new LogItemInfo("Wooden shield (g)", 20166,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo WYVERN_VISAGE_21637 = new LogItemInfo("Wyvern visage", 21637,
            new MissingKillCountDrop());
    public static LogItemInfo XERICS_CHAMPION_22396 = new LogItemInfo("Xeric's champion", 22396,
            new DeterministicDrop());
    public static LogItemInfo XERICS_GENERAL_22394 = new LogItemInfo("Xeric's general", 22394,
            new DeterministicDrop());
    public static LogItemInfo XERICS_GUARD_22388 = new LogItemInfo("Xeric's guard", 22388,
            new DeterministicDrop());
    public static LogItemInfo XERICS_SENTINEL_22392 = new LogItemInfo("Xeric's sentinel", 22392,
            new DeterministicDrop());
    public static LogItemInfo XERICS_TALISMAN_INERT_13392 = new LogItemInfo("Xeric's talisman (inert)", 13392,
            new MissingKillCountDrop());
    public static LogItemInfo XERICS_WARRIOR_22390 = new LogItemInfo("Xeric's warrior", 22390,
            new DeterministicDrop());
    public static LogItemInfo YELLOW_DARK_BOW_PAINT_12761 = new LogItemInfo("Yellow dark bow paint", 12761,
            new DeterministicDrop());
    public static LogItemInfo YEW_COMP_BOW_10282 = new LogItemInfo("Yew comp bow", 10282,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 341, 4)));
    public static LogItemInfo YOUNGLLEF_23757 = new LogItemInfo("Youngllef", 23757,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.GAUNTLET_COMPLETION_COUNT, 1.0 / 2000),
                    new RollInfo(LogItemSourceInfo.CORRUPTED_GAUNTLET_COMPLETION_COUNT, 1.0 / 800)
            )));
    public static LogItemInfo ZALCANO_SHARD_23908 = new LogItemInfo("Zalcano shard", 23908,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.ZALCANO_KILLS, 1.0 / 1500))
                    .withConfigOption(CollectionLogLuckConfig.AVG_ZALCANO_POINTS_KEY)
    );
    // Note: Add 3 minion kills per kc.
    public static LogItemInfo ZAMORAKIAN_SPEAR_11824 = new LogItemInfo("Zamorakian spear", 11824,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KRIL_TSUTSAROTH_KILLS, 1.0 / 127 + 3.0 / 5376)));
    public static LogItemInfo ZAMORAK_BANNER_11892 = new LogItemInfo("Zamorak banner", 11892,
            new DeterministicDrop());
    public static LogItemInfo ZAMORAK_BRACERS_10368 = new LogItemInfo("Zamorak bracers", 10368,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_CHAPS_10372 = new LogItemInfo("Zamorak chaps", 10372,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_CLOAK_10450 = new LogItemInfo("Zamorak cloak", 10450,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ZAMORAK_COIF_10374 = new LogItemInfo("Zamorak coif", 10374,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_CROZIER_10444 = new LogItemInfo("Zamorak crozier", 10444,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_DHIDE_BODY_10370 = new LogItemInfo("Zamorak d'hide body", 10370,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_DHIDE_BOOTS_19936 = new LogItemInfo("Zamorak d'hide boots", 19936,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_DHIDE_SHIELD_23194 = new LogItemInfo("Zamorak d'hide shield", 23194,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 9750, 5)));
    public static LogItemInfo ZAMORAK_FULL_HELM_2657 = new LogItemInfo("Zamorak full helm", 2657,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_GODSWORD_ORNAMENT_KIT_20077 = new LogItemInfo("Zamorak godsword ornament kit", 20077,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 3404, 6)));
    public static LogItemInfo ZAMORAK_HALO_12638 = new LogItemInfo("Zamorak halo", 12638,
            new DeterministicDrop());
    public static LogItemInfo ZAMORAK_HILT_11816 = new LogItemInfo("Zamorak hilt", 11816,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.KRIL_TSUTSAROTH_KILLS, 1.0 / 508)));
    public static LogItemInfo ZAMORAK_KITESHIELD_2659 = new LogItemInfo("Zamorak kiteshield", 2659,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_MITRE_10456 = new LogItemInfo("Zamorak mitre", 10456,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 1133, 4)));
    public static LogItemInfo ZAMORAK_PAGE_1_3831 = new LogItemInfo("Zamorak page 1", 3831,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ZAMORAK_PAGE_2_3832 = new LogItemInfo("Zamorak page 2", 3832,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ZAMORAK_PAGE_3_3833 = new LogItemInfo("Zamorak page 3", 3833,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ZAMORAK_PAGE_4_3834 = new LogItemInfo("Zamorak page 4", 3834,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 702.6, 6),
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 775, 5),
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 650, 5),
                    new RollInfo(LogItemSourceInfo.MEDIUM_CLUES_COMPLETED, 1.0 / 818.4, 4),
                    new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 864, 3)
            )));
    public static LogItemInfo ZAMORAK_PLATEBODY_2653 = new LogItemInfo("Zamorak platebody", 2653,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_PLATELEGS_2655 = new LogItemInfo("Zamorak platelegs", 2655,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_PLATESKIRT_3478 = new LogItemInfo("Zamorak plateskirt", 3478,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZAMORAK_ROBE_LEGS_10468 = new LogItemInfo("Zamorak robe legs", 10468,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo ZAMORAK_ROBE_TOP_10460 = new LogItemInfo("Zamorak robe top", 10460,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.EASY_CLUES_COMPLETED, 1.0 / 1404, 3)));
    public static LogItemInfo ZAMORAK_STOLE_10474 = new LogItemInfo("Zamorak stole", 10474,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZARYTE_VAMBRACES_26235 = new LogItemInfo("Zaryte vambraces", 26235,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.NEX_KILLS, 1.0 / 172))
                    .withConfigOption(CollectionLogLuckConfig.AVG_NEX_REWARDS_FRACTION_KEY)
    );
    public static LogItemInfo ZEALOTS_BOOTS_25440 = new LogItemInfo("Zealot's boots", 25440,
            new MissingKillCountDrop());
    public static LogItemInfo ZEALOTS_HELM_25438 = new LogItemInfo("Zealot's helm", 25438,
            new MissingKillCountDrop());
    public static LogItemInfo ZEALOTS_ROBE_BOTTOM_25436 = new LogItemInfo("Zealot's robe bottom", 25436,
            new MissingKillCountDrop());
    public static LogItemInfo ZEALOTS_ROBE_TOP_25434 = new LogItemInfo("Zealot's robe top", 25434,
            new MissingKillCountDrop());
    public static LogItemInfo ZENYTE_SHARD_19529 = new LogItemInfo("Zenyte shard", 19529,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.DEMONIC_GORILLA_KILLS, 1.0 / 300),
                    new RollInfo(LogItemSourceInfo.TORTURED_GORILLA_KILLS, 1.0 / 3000)
            )));
    public static LogItemInfo ZOMBIE_BOOTS_7596 = new LogItemInfo("Zombie boots", 7596,
            new MissingKillCountDrop());
    public static LogItemInfo ZOMBIE_CHAMPION_SCROLL_6807 = new LogItemInfo("Zombie champion scroll", 6807,
            new MissingKillCountDrop());
    public static LogItemInfo ZOMBIE_GLOVES_7595 = new LogItemInfo("Zombie gloves", 7595,
            new MissingKillCountDrop());
    public static LogItemInfo ZOMBIE_HEAD_19912 = new LogItemInfo("Zombie head", 19912,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 1625, 5)));
    public static LogItemInfo ZOMBIE_MASK_7594 = new LogItemInfo("Zombie mask", 7594,
            new MissingKillCountDrop());
    public static LogItemInfo ZOMBIE_MONKEY_24863 = new LogItemInfo("Zombie monkey", 24863,
            new DeterministicDrop());
    public static LogItemInfo ZOMBIE_SHIRT_7592 = new LogItemInfo("Zombie shirt", 7592,
            new MissingKillCountDrop());
    public static LogItemInfo ZOMBIE_TROUSERS_7593 = new LogItemInfo("Zombie trousers", 7593,
            new MissingKillCountDrop());
    public static LogItemInfo ZULRAHS_SCALES_12934 = new LogItemInfo("Zulrah's scales", 12934,
            new CappedCollectionLogTrackingDrop(65535));
    public static LogItemInfo ZUL_ANDRA_TELEPORT_12938 = new LogItemInfo("Zul-andra teleport", 12938,
            new FixedStackDrop(new RollInfo(LogItemSourceInfo.ZULRAH_KILLS, 1.0 / 16.53, 2), 4));
    public static LogItemInfo THIRD_AGE_AMULET_10344 = new LogItemInfo("3rd age amulet", 10344,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_AXE_20011 = new LogItemInfo("3rd age axe", 20011,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_BOW_12424 = new LogItemInfo("3rd age bow", 12424,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_CLOAK_12437 = new LogItemInfo("3rd age cloak", 12437,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_DRUIDIC_CLOAK_23345 = new LogItemInfo("3rd age druidic cloak", 23345,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_DRUIDIC_ROBE_BOTTOMS_23339 = new LogItemInfo("3rd age druidic robe bottoms", 23339,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_DRUIDIC_ROBE_TOP_23336 = new LogItemInfo("3rd age druidic robe top", 23336,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_DRUIDIC_STAFF_23342 = new LogItemInfo("3rd age druidic staff", 23342,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_FULL_HELMET_10350 = new LogItemInfo("3rd age full helmet", 10350,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_KITESHIELD_10352 = new LogItemInfo("3rd age kiteshield", 10352,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_LONGSWORD_12426 = new LogItemInfo("3rd age longsword", 12426,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_MAGE_HAT_10342 = new LogItemInfo("3rd age mage hat", 10342,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_PICKAXE_20014 = new LogItemInfo("3rd age pickaxe", 20014,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_PLATEBODY_10348 = new LogItemInfo("3rd age platebody", 10348,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_PLATELEGS_10346 = new LogItemInfo("3rd age platelegs", 10346,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_PLATESKIRT_23242 = new LogItemInfo("3rd age plateskirt", 23242,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_RANGE_COIF_10334 = new LogItemInfo("3rd age range coif", 10334,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_RANGE_LEGS_10332 = new LogItemInfo("3rd age range legs", 10332,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_RANGE_TOP_10330 = new LogItemInfo("3rd age range top", 10330,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_ROBE_10340 = new LogItemInfo("3rd age robe", 10340,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_ROBE_TOP_10338 = new LogItemInfo("3rd age robe top", 10338,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_VAMBRACES_10336 = new LogItemInfo("3rd age vambraces", 10336,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.HARD_CLUES_COMPLETED, 1.0 / 211_250, 5),
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo THIRD_AGE_WAND_12422 = new LogItemInfo("3rd age wand", 12422,
            new PoissonBinomialDrop(ImmutableList.of(
                    // assumes players immediately kill the Mimic, and do so on the first try
                    new RollInfo(LogItemSourceInfo.ELITE_CLUES_COMPLETED, 1.0 / 35.0 / 5750 + 1.0 / 249_262, 5),
                    new RollInfo(LogItemSourceInfo.MASTER_CLUES_COMPLETED, 1.0 / 15.0 / 5244 + 1.0 / 313_168, 6)
            )));
    public static LogItemInfo VETION_JR_13179 = new LogItemInfo("Vet'ion jr.", 13179,
            new PoissonBinomialDrop(ImmutableList.of(
                    new RollInfo(LogItemSourceInfo.VETION_KILLS, 1.0 / 2000),
                    new RollInfo(LogItemSourceInfo.CALVARION_KILLS, 1.0 / 2800)
            ))
                    .withConfigOption(CollectionLogLuckConfig.AVG_VETION_REWARDS_FRACTION_KEY));
    public static LogItemInfo WARPED_SCEPTRE_UNCHARGED_28583 = new LogItemInfo("Warped sceptre (uncharged)", 28583,
            new MissingKillCountDrop());
    public static LogItemInfo FARMERS_SHIRT_13643 = new LogItemInfo("Farmer's shirt", 13643,
            new DeterministicDrop());
    public static LogItemInfo FOX_WHISTLE_28626 = new LogItemInfo("Fox whistle", 28626,
            new MissingKillCountDrop());
    public static LogItemInfo GOLDEN_PHEASANT_EGG_28663 = new LogItemInfo("Golden pheasant egg", 28663,
            new MissingKillCountDrop());
    public static LogItemInfo TWITCHERS_GLOVES_28630 = new LogItemInfo("Twitcher's gloves", 28630,
            new DeterministicDrop());
    public static LogItemInfo CAPE_POUCH_28613 = new LogItemInfo("Cape pouch", 28613,
            new DeterministicDrop());
    public static LogItemInfo FELLING_AXE_HANDLE_28177 = new LogItemInfo("Felling axe handle", 28177,
            new DeterministicDrop());
    public static LogItemInfo PHEASANT_HAT_28620 = new LogItemInfo("Pheasant hat", 28620,
            new DeterministicDrop());
    public static LogItemInfo PHEASANT_LEGS_28622 = new LogItemInfo("Pheasant legs", 28622,
            new DeterministicDrop());
    public static LogItemInfo PHEASANT_BOOTS_28618 = new LogItemInfo("Pheasant boots", 28618,
            new DeterministicDrop());
    public static LogItemInfo PHEASANT_CAPE_28616 = new LogItemInfo("Pheasant cape", 28616,
            new DeterministicDrop());
    public static LogItemInfo PETAL_GARLAND_28655 = new LogItemInfo("Petal garland", 28655,
            new MissingKillCountDrop());
    public static LogItemInfo STURDY_BEEHIVE_PARTS_28674 = new LogItemInfo("Sturdy beehive parts", 28674,
            new MissingKillCountDrop());
    public static LogItemInfo ZOMBIE_AXE_28813 = new LogItemInfo("Broken zombie axe", 28813,
            new MissingKillCountDrop());
    public static LogItemInfo SMOL_HEREDIT_28960 = new LogItemInfo("Smol heredit", 28960,
            new UnimplementedDrop());
    public static LogItemInfo DIZANAS_QUIVER_UNCHARGED_28947 = new LogItemInfo("Dizana's quiver (uncharged)", 28947,
            new DeterministicDrop());
    public static LogItemInfo SUNFIRE_FANATIC_CUIRASS_28936 = new LogItemInfo("Sunfire fanatic cuirass", 28936,
            new MissingKillCountDrop());
    public static LogItemInfo SUNFIRE_FANATIC_CHAUSSES_28939 = new LogItemInfo("Sunfire fanatic chausses", 28939,
            new MissingKillCountDrop());
    public static LogItemInfo SUNFIRE_FANATIC_HELM_28933 = new LogItemInfo("Sunfire fanatic helm", 28933,
            new MissingKillCountDrop());
    public static LogItemInfo ECHO_CRYSTAL_28942 = new LogItemInfo("Echo crystal", 28942,
            new MissingKillCountDrop());
    public static LogItemInfo TONALZTICS_OF_RALOS_UNCHARGED_28919 = new LogItemInfo("Tonalztics of ralos (uncharged)", 28919,
            new MissingKillCountDrop());
    public static LogItemInfo SUNFIRE_SPLINTERS_28924 = new LogItemInfo("Sunfire splinters", 28924,
            new CappedCollectionLogTrackingDrop(250));
    public static LogItemInfo ECLIPSE_MOON_CHESTPLATE_29004 = new LogItemInfo("Eclipse moon chestplate", 29004,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29004, 29007, 29010, 29000)
            ));
    public static LogItemInfo ECLIPSE_MOON_TASSETS_29007 = new LogItemInfo("Eclipse moon tassets", 29007,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29004, 29007, 29010, 29000)
            ));
    public static LogItemInfo ECLIPSE_MOON_HELM_29010 = new LogItemInfo("Eclipse moon helm", 29010,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29004, 29007, 29010, 29000)
            ));
    public static LogItemInfo ECLIPSE_ATLATL_29000 = new LogItemInfo("Eclipse atlatl", 29000,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29004, 29007, 29010, 29000)
            ));
    public static LogItemInfo BLUE_MOON_CHESTPLATE_29013 = new LogItemInfo("Blue moon chestplate", 29013,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29013, 29016, 29019, 28988)
            ));
    public static LogItemInfo BLUE_MOON_TASSETS_29016 = new LogItemInfo("Blue moon tassets", 29016,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29013, 29016, 29019, 28988)
            ));
    public static LogItemInfo BLUE_MOON_HELM_29019 = new LogItemInfo("Blue moon helm", 29019,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29013, 29016, 29019, 28988)
            ));
    public static LogItemInfo BLUE_MOON_SPEAR_28988 = new LogItemInfo("Blue moon spear", 28988,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29013, 29016, 29019, 28988)
            ));
    public static LogItemInfo BLOOD_MOON_CHESTPLATE_29022 = new LogItemInfo("Blood moon chestplate", 29022,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29022, 29025, 29028, 28997)
            ));
    public static LogItemInfo BLOOD_MOON_TASSETS_29025 = new LogItemInfo("Blood moon tassets", 29025,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29022, 29025, 29028, 28997)
            ));
    public static LogItemInfo BLOOD_MOON_HELM_29028 = new LogItemInfo("Blood moon helm", 29028,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29022, 29025, 29028, 28997)
            ));
    public static LogItemInfo DUAL_MACUAHUITL_28997 = new LogItemInfo("Dual macuahuitl", 28997,
            new DupeProtectedFirstSetBinomialDrop(
                    new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                    ImmutableList.of(29022, 29025, 29028, 28997)
            ));
    public static LogItemInfo ATLATL_DART_28991 = new LogItemInfo("Atlatl dart", 28991,
            new CappedCollectionLogTrackingDrop(250));
    public static LogItemInfo QUETZIN_28962 = new LogItemInfo("Quetzin", 28962,
            new MissingKillCountDrop());
    public static LogItemInfo HUNTSMANS_KIT_29309 = new LogItemInfo("Huntsman's kit", 29309,
            new MissingKillCountDrop());
    public static LogItemInfo GUILD_HUNTER_HEADWEAR_29263 = new LogItemInfo("Guild hunter headwear", 29263,
            new MissingKillCountDrop());
    public static LogItemInfo GUILD_HUNTER_TOP_29265 = new LogItemInfo("Guild hunter top", 29265,
            new MissingKillCountDrop());
    public static LogItemInfo GUILD_HUNTER_LEGS_29267 = new LogItemInfo("Guild hunter legs", 29267,
            new MissingKillCountDrop());
    public static LogItemInfo GUILD_HUNTER_BOOTS_29269 = new LogItemInfo("Guild hunter boots", 29269,
            new MissingKillCountDrop());
    public static LogItemInfo SULPHUR_BLADES_29084 = new LogItemInfo("Sulphur blades", 29084,
            new MissingKillCountDrop());
    public static LogItemInfo TELEPORT_ANCHORING_SCROLL_29455 = new LogItemInfo("Teleport anchoring scroll", 29455,
            new MissingKillCountDrop());
    public static LogItemInfo BRIMHAVEN_VOUCHER_29482 = new LogItemInfo("Brimhaven voucher", 29482,
            new DeterministicDrop());
    public static LogItemInfo TORMENTED_SYNAPSE_29580 = new LogItemInfo("Tormented synapse", 29580,
            new BinomialDrop(new RollInfo(LogItemSourceInfo.TORMENTED_DEMON_KILLS, 1.0 / 500)));
    public static LogItemInfo BURNING_CLAW_29574 = new LogItemInfo("Burning claw", 29574,
            // Rolls after tormented synapse
            new BinomialDrop(new RollInfo(LogItemSourceInfo.TORMENTED_DEMON_KILLS, 1.0 / 500.0 * 499.0 / 500.0)));
    public static LogItemInfo GUTHIXIAN_TEMPLE_TELEPORT_29684 = new LogItemInfo("Guthixian temple teleport", 29684,
            new FixedStackDrop(new RollInfo(LogItemSourceInfo.TORMENTED_DEMON_KILLS, 1.0 / 12), 2));


    private final String itemName;
    private final int itemId;
    private final DropLuck dropLuck;

    // Initializing static map here - don't allow other classes to initialize.
    private LogItemInfo(String itemName, int itemId, DropLuck dropLuck) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.dropLuck = dropLuck;
        dropLuck.setItemName(itemName);

        logItemInfos.put(itemName, this);
        logItemInfoIds.put(itemId, this);
    }

    // find the LogItemInfo corresponding to the given target
    public static LogItemInfo findByName(String targetItemName) {
        return logItemInfos.get(targetItemName);
    }

    // find the LogItemInfo corresponding to the given target
    public static LogItemInfo findByItemId(int itemId) {
        return logItemInfoIds.get(itemId);
    }

    public static Collection<LogItemInfo> getAllLogItemInfos() {
        return logItemInfos.values();
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public DropLuck getDropProbabilityDistribution() {
        return dropLuck;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        // this also covers o == null
        else if (!(o instanceof LogItemInfo)) {
            return false;
        }

        LogItemInfo other = (LogItemInfo) o;

        return this.itemId == other.getItemId() && this.itemName.equals(other.getItemName());
    }

}
