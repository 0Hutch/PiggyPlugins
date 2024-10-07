package com.piggyplugins.collectionlogluck.luck;


public enum LogItemSourceInfo {

    // Currently, only item sources with KCs in the collection log are listed
    ABYSSAL_SIRE_KILLS("Abyssal Sire kills"),
    ALCHEMICAL_HYDRA_KILLS("Alchemical Hydra kills"),
    ARTIO_KILLS("Artio kills"),
    BARROWS_CHESTS_OPENED("Barrows Chests opened"),
    BEGINNER_CLUES_COMPLETED("Beginner clues completed"),
    BRYOPHYTA_KILLS("Bryophyta kills"),
    CALLISTO_KILLS("Callisto kills"),
    CALVARION_KILLS("Calvar'ion kills"),
    CERBERUS_KILLS("Cerberus kills"),
    CHAMBERS_OF_XERIC_CM_COMPLETIONS("Chambers of Xeric (CM) completions"),
    CHAMBERS_OF_XERIC_COMPLETIONS("Chambers of Xeric completions"),
    CHAOS_ELEMENTAL_KILLS("Chaos Elemental kills"),
    CHAOS_FANATIC_KILLS("Chaos Fanatic kills"),
    COMMANDER_ZILYANA_KILLS("Commander Zilyana kills"),
    CORPOREAL_BEAST_KILLS("Corporeal Beast kills"),
    CORRUPTED_GAUNTLET_COMPLETION_COUNT("Corrupted Gauntlet completion count"),
    CRAZY_ARCHAEOLOGIST_KILLS("Crazy Archaeologist kills"),
    DAGANNOTH_PRIME_KILLS("Dagannoth Prime kills"),
    DAGANNOTH_REX_KILLS("Dagannoth Rex kills"),
    DAGANNOTH_SUPREME_KILLS("Dagannoth Supreme kills"),
    DEMONIC_GORILLA_KILLS("Demonic Gorilla kills"),
    DUKE_SUCELLUS_KILLS("Duke Sucellus kills"),
    EASY_CLUES_COMPLETED("Easy clues completed"),
    ELITE_CLUES_COMPLETED("Elite clues completed"),
    GAUNTLET_COMPLETION_COUNT("Gauntlet completion count"),
    GENERAL_GRAARDOR_KILLS("General Graardor kills"),
    GIANT_MOLE_KILLS("Giant Mole kills"),
    GNOME_RESTAURANT_EASY_DELIVERIES("Gnome restaurant easy deliveries"),
    GNOME_RESTAURANT_HARD_DELIVERIES("Gnome restaurant hard deliveries"),
    GRAND_HALLOWED_COFFINS_OPENED("Grand Hallowed Coffins opened"),
    GROTESQUE_GUARDIAN_KILLS("Grotesque Guardian kills"),
    HARD_CLUES_COMPLETED("Hard clues completed"),
    HESPORI_KILLS("Hespori kills"),
    HIGH_LEVEL_GAMBLES("High-level Gambles"),
    KALPHITE_QUEEN_KILLS("Kalphite Queen kills"),
    KING_BLACK_DRAGON_KILLS("King Black Dragon kills"),
    KRAKEN_KILLS("Kraken kills"),
    KREEARRA_KILLS("Kree'arra kills"),
    KRIL_TSUTSAROTH_KILLS("K'ril Tsutsaroth kills"),
    LAST_MAN_STANDING_GAMES_PLAYED("Last Man Standing games played"),
    LAST_MAN_STANDING_KILLS("Last Man Standing Kills"),
    LAST_MAN_STANDING_WINS("Last Man Standing Wins"),
    LEVIATHAN_KILLS("Leviathan kills"),
    LUNAR_CHESTS_OPENED("Lunar Chests opened"),
    MASTER_CLUES_COMPLETED("Master clues completed"),
    MEDIUM_CLUES_COMPLETED("Medium clues completed"),
    NEX_KILLS("Nex kills"),
    NIGHTMARE_KILLS("Nightmare kills"),
    OBOR_KILLS("Obor kills"),
    PHANTOM_MUSPAH_KILLS("Phantom Muspah kills"),
    PHOSANIS_NIGHTMARE_KILLS("Phosani's Nightmare kills"),
    REVENANT_KILLS("Revenant kills"),
    REWARD_PERMITS_CLAIMED("Reward permits claimed"),
    RIFTS_CLOSED("Rifts closed"),
    RIFTS_SEARCHES("Rifts searches"),
    RUMOURS_COMPLETED("Rumours Completed"),
    SARACHNIS_KILLS("Sarachnis kills"),
    SCORPIA_KILLS("Scorpia kills"),
    SCURRIUS_KILLS("Scurrius kills"),
    SKOTIZO_KILLS("Skotizo kills"),
    SOL_HEREDIT_KILLS("Sol Heredit kills"),
    SPINDEL_KILLS("Spindel kills"),
    SPOILS_OF_WAR_OPENED("Spoils of war opened"),
    SWORDS_CREATED("Swords created"),
    TEMPOROSS_KILLS("Tempoross kills"),
    THEATRE_OF_BLOOD_COMPLETIONS("Theatre of Blood completions"),
    THEATRE_OF_BLOOD_ENTRY_COMPLETIONS("Theatre of Blood (Entry) completions"),
    THEATRE_OF_BLOOD_HARD_COMPLETIONS("Theatre of Blood (Hard) completions"),
    THERMONUCLEAR_SMOKE_DEVIL_KILLS("Thermonuclear Smoke Devil kills"),
    TOMBS_OF_AMASCUT_COMPLETIONS("Tombs of Amascut completions"),
    TOMBS_OF_AMASCUT_ENTRY_COMPLETIONS("Tombs of Amascut (Entry) completions"),
    TOMBS_OF_AMASCUT_EXPERT_COMPLETIONS("Tombs of Amascut (Expert) completions"),
    TORMENTED_DEMON_KILLS("Tormented Demon kills"),
    TORTURED_GORILLA_KILLS("Tortured Gorilla kills"),
    TOTAL_CLUES_COMPLETED("Total clues completed"),
    TZKAL_ZUK_KILLS("TzKal-Zuk kills"),
    TZTOK_JAD_KILLS("TzTok-Jad kills"),
    VARDORVIS_KILLS("Vardorvis kills"),
    VENENATIS_KILLS("Venenatis kills"),
    VETION_KILLS("Vet'ion kills"),
    VORKATH_KILLS("Vorkath kills"),
    WHISPERER_KILLS("Whisperer kills"),
    WINTERTODT_KILLS("Wintertodt kills"),
    ZALCANO_KILLS("Zalcano kills"),
    ZULRAH_KILLS("Zulrah kills");


    private final String name;

    LogItemSourceInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static LogItemSourceInfo findByName(String logItemSourceName) {
        for(LogItemSourceInfo e: LogItemSourceInfo.values()) {
            if(e.name.equalsIgnoreCase(logItemSourceName)) {
                return e;
            }
        }
        // not found
        return null;
    }

}
