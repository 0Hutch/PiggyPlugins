package com.ozplugins.AutoMTA.util;

import com.ozplugins.AutoMTA.AutoMTAConfiguration;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class Utils {
    protected static final Random random = new Random();

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    @Inject
    private AutoMTAConfiguration config;

    private int nextRunEnergy;

    public List<String> getGearNames(String gear) {
        return Arrays.stream(gear.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public void swapGear(List<String> gearNames) {
        for (String gearName : gearNames) {
            nameContainsNoCase(gearName).first().ifPresent(item -> {
                InventoryInteraction.useItem(item, "Equip", "Wield", "Wear");
            });
        }
    }

    public static ItemQuery nameContainsNoCase(String name) {
        return Inventory.search().filter(widget -> widget.getName().toLowerCase().contains(name.toLowerCase()));
    }

    public void toggleGear(List<String> gearNames) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        swapGear(gearNames);
    }

    public void sendIntValue(int amount) {
        client.setVarcStrValue(359, Integer.toString(amount));
        client.setVarcIntValue(5, 7);
        client.runScript(681);
    }

    //Delay stuff
    public int tickDelay() {
        int tickLength = (int) randomDelay(config.tickDelayWeightedDistribution(), config.tickDelayMin(), config.tickDelayMax(), config.tickDelayDeviation(), config.tickDelayTarget());
        //log.debug("tick delay for {} ticks", tickLength);
        return tickLength;
    }

    public long randomDelay(boolean weightedDistribution, int min, int max, int deviation, int target) {
        if (weightedDistribution) {
            return (long) clamp((-Math.log(Math.abs(random.nextGaussian()))) * deviation + target, min, max);
        } else {
            /* generate a normal even distribution random */
            return (long) clamp(Math.round(random.nextGaussian() * deviation + target), min, max);
        }
    }

    private double clamp(double val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public int getRandomIntBetweenRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


    public boolean isRunEnabled() {
        return client.getVarpValue(173) == 1;
    }

    public void handleRun(int minEnergy, int randMax) {
        if (nextRunEnergy < minEnergy || nextRunEnergy > minEnergy + randMax) {
            nextRunEnergy = getRandomIntBetweenRange(minEnergy, minEnergy + getRandomIntBetweenRange(0, randMax));
        }
        if ((client.getEnergy() / 100) > nextRunEnergy ||
                client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) != 0) {
            if (!isRunEnabled()) {
                nextRunEnergy = 0;
                Widget runOrb = client.getWidget(WidgetInfo.MINIMAP_RUN_ORB);

                if (runOrb != null) {
                    enableRun();
                }
            }
        }
    }

    public void enableRun() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
    }
}
