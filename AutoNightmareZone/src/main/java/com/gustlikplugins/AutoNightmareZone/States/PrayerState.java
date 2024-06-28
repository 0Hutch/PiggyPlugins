package com.gustlikplugins.AutoNightmareZone.States;

import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.gustlikplugins.AutoNightmareZone.AutoNightmareZonePlugin;
import com.gustlikplugins.AutoNightmareZone.NightmareZoneStrategy;
import com.gustlikplugins.AutoNightmareZone.PrayerFlickMode;
import com.gustlikplugins.AutoNightmareZone.State;
import com.piggyplugins.PiggyUtils.API.PrayerUtil;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.WidgetInfo;

import java.util.Arrays;


//Only concers toggling prayers, not drinking prayer pots
public class PrayerState extends State {

    @Override
    public String getName() {
        return "PRAYER_STATE";
    }

    public PrayerState(AutoNightmareZonePlugin plugin, State initialState) {
        super(plugin, initialState);
    }

    private final int quickPrayerWidgetID = WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId();

    private int prayerOffset = (int) (Math.random() * plugin.config.prayerOffset());

    private int tickDelay = (int) (Math.random() * (plugin.config.tickDelayMax() - plugin.config.tickdelayMin())) + plugin.config.tickdelayMin();

    @Override
    public State getState() {
        tickDelay--;
        if (plugin.config.strategy() == NightmareZoneStrategy.ABSORPTION)
            if (plugin.config.prayerFlickMode() == PrayerFlickMode.OFF)
                return getState(IdleState.class);

        if (plugin.config.strategy().equals(NightmareZoneStrategy.PRAYER) && plugin.config.usePrayerPots())
        {
            int currentPrayer = plugin.client.getBoostedSkillLevel(Skill.PRAYER);
            int prayerLevel = plugin.client.getRealSkillLevel(Skill.PRAYER);
            int prayerBoost = (prayerLevel / 4) + 7;
            if(currentPrayer + prayerOffset < prayerLevel - prayerBoost){
                tickDelay--;
                if(tickDelay < 1){
                    prayerOffset = (int) (Math.random() * 8);
                    tickDelay = (int) (Math.random() * (plugin.config.tickDelayMax() - plugin.config.tickdelayMin())) + plugin.config.tickdelayMin();
                    return getState(DrinkPrayerState.class);
                }
            }

        }

        return this;

    }

    @Override
    public void onGameTick() {
        if (plugin.client.getBoostedSkillLevel(Skill.PRAYER) < 1) return;
        switch (plugin.config.prayerFlickMode()){
            case OFF: {
                Prayer[] prayers = parsePrayers(plugin.config.prayers());
                for (Prayer prayer : prayers) {
                    if (!PrayerUtil.isPrayerActive(prayer)) {
                        PrayerUtil.togglePrayer(prayer);
                    }
                }
            }break;
            case QUICK_PRAYER: {
                if (plugin.client.getVarbitValue(Varbits.QUICK_PRAYER) == 1) {
                    togglePrayersMinimap();
                }
                togglePrayersMinimap();
            } break;
            case CUSTOM: {
                Prayer[] prayers = parsePrayers(plugin.config.prayers());
                for (Prayer prayer : prayers) {
                    if (PrayerUtil.isPrayerActive(prayer)) {
                        PrayerUtil.togglePrayer(prayer);
                    }
                    PrayerUtil.togglePrayer(prayer);
                }
            }break;
        }
    }

    public static Prayer[] parsePrayers(String prayerString) {
        String[] prayerNames = prayerString.split(",");
        return Arrays.stream(prayerNames)
                .map(String::trim)
                .map(name -> name.replace(' ', '_'))
                .map(name -> Arrays.stream(Prayer.values())
                        .filter(prayer -> prayer.name().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null))
                .filter(prayer -> prayer != null)
                .toArray(Prayer[]::new);
    }

    private void togglePrayersMinimap() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, quickPrayerWidgetID, -1, -1);
    }

    public void disablePrayers(){
        Prayer[] prayers = parsePrayers(plugin.config.prayers());
        for (Prayer prayer : prayers) {
            if (PrayerUtil.isPrayerActive(prayer)) {
                PrayerUtil.togglePrayer(prayer);
            }
        }
        if (plugin.client.getVarbitValue(Varbits.QUICK_PRAYER) == 1) {
            togglePrayersMinimap();
        }
    }
}
