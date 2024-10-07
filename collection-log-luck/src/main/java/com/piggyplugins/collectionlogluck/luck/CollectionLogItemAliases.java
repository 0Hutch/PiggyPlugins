package com.piggyplugins.collectionlogluck.luck;

import com.piggyplugins.collectionlogluck.util.FuzzyStringMatch;

public final class CollectionLogItemAliases
{

    // Note: Only add manual alias names where fuzzy match is likely to fail
    public static String aliasItemName(String aliasNameRaw)
    {
        String aliasName = aliasNameRaw.trim().toLowerCase();
        switch (aliasName.toLowerCase())
        {
            case "enh weapon seed":
            case "enhanced weapon seed":
            case "enh crystal weapon seed":
                return LogItemInfo.ENHANCED_CRYSTAL_WEAPON_SEED_25859.getItemName();

            case "d pick":
                return LogItemInfo.DRAGON_PICKAXE_11920.getItemName();

            case "d axe":
                return LogItemInfo.DRAGON_AXE_6739.getItemName();

            case "d chain":
                return LogItemInfo.DRAGON_CHAINBODY_3140.getItemName();

            case "d full helm":
                return LogItemInfo.DRAGON_FULL_HELM_11335.getItemName();

            case "enh teleport seed":
            case "enh tele seed":
            case "enhanced teleport seed":
            case "enhanced tele seed":
            case "enh crystal teleport seed":
            case "enh crystal tele seed":
                return LogItemInfo.ENHANCED_CRYSTAL_TELEPORT_SEED_23959.getItemName();

            // pets
            case "chaos ele pet":
            case "pet chaos ele":
                return LogItemInfo.PET_CHAOS_ELEMENTAL_11995.getItemName();

            case "thermy pet":
            case "pet thermy":
                return LogItemInfo.PET_SMOKE_DEVIL_12648.getItemName();

            case "giant mole pet":
            case "mole pet":
            case "pet giant mole":
            case "pet mole":
                return LogItemInfo.BABY_MOLE_12646.getItemName();

            case "kbd pet":
            case "pet kbd":
                return LogItemInfo.PRINCE_BLACK_DRAGON_12653.getItemName();

            case "kq pet":
            case "kalphite queen pet":
            case "pet kq":
            case "pet kalphite queen":
                return LogItemInfo.KALPHITE_PRINCESS_12647.getItemName();

            case "corp pet":
            case "corp beast pet":
            case "corporeal beast pet":
            case "pet corp":
            case "pet corp beast":
            case "pet corporeal beast":
                return LogItemInfo.PET_DARK_CORE_12816.getItemName();

            case "kree pet":
            case "pet kree":
            case "armadyl pet":
            case "pet armadyl":
                return LogItemInfo.PET_KREEARRA_12649.getItemName();

            case "zammy pet":
            case "pet zammy":
                return LogItemInfo.PET_KRIL_TSUTSAROTH_12652.getItemName();

            case "bandos pet":
            case "pet bandos":
                return LogItemInfo.PET_GENERAL_GRAARDOR_12650.getItemName();

            case "sara pet":
            case "saradomin pet":
            case "pet sara":
            case "pet saradomin":
                return LogItemInfo.PET_ZILYANA_12651.getItemName();

            case "jad pet":
            case "pet jad":
                return LogItemInfo.TZREK_JAD_13225.getItemName();

            case "inferno pet":
            case "zuk pet":
            case "pet zuk":
                return LogItemInfo.JAL_NIB_REK_21291.getItemName();

            case "scorpia pet":
            case "pet scorpia":
                return LogItemInfo.SCORPIAS_OFFSPRING_13181.getItemName();

            case "venenatis pet":
            case "pet venenatis":
                return LogItemInfo.VENENATIS_SPIDERLING_13177.getItemName();

            case "callisto pet":
            case "pet callisto":
                return LogItemInfo.CALLISTO_CUB_13178.getItemName();

            case "cerb pet":
            case "cerberus pet":
            case "pet cerb":
            case "pet cerberus":
                return LogItemInfo.HELLPUPPY_13247.getItemName();

            case "abby sire pet":
            case "abyssal sire pet":
            case "pet abby sire":
            case "pet abyssal sire":
                return LogItemInfo.ABYSSAL_ORPHAN_13262.getItemName();

            case "zulrah pet":
            case "pet zulrah":
            case "snakeling":
                return LogItemInfo.PET_SNAKELING_12921.getItemName();

            case "fishing pet":
                return LogItemInfo.HERON_13320.getItemName();

            case "mining pet":
                return LogItemInfo.ROCK_GOLEM_13321.getItemName();

            case "wc pet":
            case "woodcutting pet":
                return LogItemInfo.BEAVER_13322.getItemName();

            case "baby chin pet":
            case "baby chin":
            case "chin pet":
            case "pet baby chin":
            case "pet chin":
                return LogItemInfo.BABY_CHINCHOMPA_13324.getItemName();

            case "clue pet":
            case "master clue pet":
                return LogItemInfo.BLOODHOUND_19730.getItemName();

            case "agility pet":
                return LogItemInfo.GIANT_SQUIRREL_20659.getItemName();

            case "farming pet":
            case "farm pet":
                return LogItemInfo.TANGLEROOT_20661.getItemName();

            case "rc pet":
            case "runecraft pet":
            case "runecrafting pet":
                return LogItemInfo.RIFT_GUARDIAN_20665.getItemName();

            case "thieving pet":
            case "racoon pet":
            case "raccoon pet":
                return LogItemInfo.ROCKY_20663.getItemName();

            case "wintertodt pet":
                return LogItemInfo.PHOENIX_20693.getItemName();

            case "olm pet":
            case "cox pet":
            case "chambers pet":
            case "chambers of xeric pet":
            case "pet olm":
                return LogItemInfo.OLMLET_20851.getItemName();

            case "skotizo pet":
            case "pet skotizo":
                return LogItemInfo.SKOTOS_21273.getItemName();

            case "herbiboar pet":
                return LogItemInfo.HERBI_21509.getItemName();

            case "vorkath pet":
            case "pet vorkath":
                return LogItemInfo.VORKI_21992.getItemName();

            case "tob pet":
            case "verzik pet":
            case "pet verzik":
                return LogItemInfo.LIL_ZIK_22473.getItemName();

            case "hydra pet":
            case "pet hydra":
                return LogItemInfo.IKKLE_HYDRA_22746.getItemName();

            case "sarachnis pet":
            case "pet sarachnis":
                return LogItemInfo.SRARACHA_23495.getItemName();

            case "toa pet":
            case "tumeken pet":
            case "tombs pet":
            case "tombs of amascut pet":
                return LogItemInfo.TUMEKENS_GUARDIAN_27352.getItemName();

            case "gauntlet pet":
            case "corrupted gauntlet pet":
            case "cg pet":
            case "pet hunllef":
            case "pet hunlleff":
            case "pet hunlef":
            case "pet hunleff":
                return LogItemInfo.YOUNGLLEF_23757.getItemName();

            case "zalcano pet":
            case "pet zalcano":
                return LogItemInfo.SMOLCANO_23760.getItemName();

            case "nightmare pet":
            case "pet nightmare":
                return LogItemInfo.LITTLE_NIGHTMARE_24491.getItemName();

            case "soul wars pet":
                return LogItemInfo.LIL_CREATOR_25348.getItemName();

            case "tempoross pet":
            case "pet tempoross":
                return LogItemInfo.TINY_TEMPOR_25602.getItemName();

            case "nex pet":
            case "pet nex":
                return LogItemInfo.NEXLING_26348.getItemName();

            case "guardians of the rift pet":
            case "gotr pet":
                return LogItemInfo.ABYSSAL_PROTECTOR_26901.getItemName();

            case "muspah pet":
            case "phantom muspah pet":
            case "pet muspah":
            case "pet phantom muspah":
                return LogItemInfo.MUPHIN_27590.getItemName();

            case "whisperer pet":
            case "the whisperer pet":
            case "pet whisperer":
                return LogItemInfo.WISP_28246.getItemName();

            case "vardorvis pet":
            case "pet vardovis":
                return LogItemInfo.BUTCH_28248.getItemName();

            case "leviathan pet":
            case "the leviathan pet":
            case "pet leviathan":
            case "pet the leviathan":
                return LogItemInfo.LILVIATHAN_28252.getItemName();

            case "duke pet":
            case "duke sucellus pet":
            case "sucellus pet":
            case "pet duke":
            case "pet duke sucellus":
            case "pet sucellus":
                return LogItemInfo.BARON_28250.getItemName();

            case "pet scurrius":
            case "scurrius pet":
            case "scurry pet":
            case "pet scurry":
                return LogItemInfo.SCURRY_28801.getItemName();

            default:
                return backupFuzzyMatch(aliasNameRaw);
        }
    }

    public static String backupFuzzyMatch(String aliasNameRaw)
    {
        String bestMatch = aliasNameRaw;
        double bestScore = -Double.MAX_VALUE;

        for (LogItemInfo logItemInfo : LogItemInfo.getAllLogItemInfos()) {
            String itemNameRaw = logItemInfo.getItemName().trim();

            if (aliasNameRaw.trim().equalsIgnoreCase(itemNameRaw)) {
                // for exact match, don't try to further match
                // for example, we don't want "Crystal weapon seed" to expand into "Enhanced crystal weapon seed".
                return itemNameRaw;
            }

            double score = FuzzyStringMatch.fuzzyMatchScore(aliasNameRaw, itemNameRaw);

            if (score > bestScore) {
                bestScore = score;
                bestMatch = itemNameRaw;
            }
        }

        // if the match is terrible, don't return anything
        if (bestScore < -aliasNameRaw.length()) {
            return aliasNameRaw;
        }
        return bestMatch;
    }

}
