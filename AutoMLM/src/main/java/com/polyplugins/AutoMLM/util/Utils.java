package com.polyplugins.AutoMLM.util;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.query.ItemQuery;
import com.example.InteractionApi.InventoryInteraction;
import com.google.inject.Inject;
import com.polyplugins.AutoMLM.AutoMLMConfiguration;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.callback.ClientThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    protected static final Random random = new Random();
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoMLMConfiguration config;

    public Utils() {
    }

    public List<String> getGearNames(String gear) {
        return (List)Arrays.stream(gear.split(",")).map(String::trim).collect(Collectors.toList());
    }

    public void swapGear(List<String> gearNames) {
        Iterator var2 = gearNames.iterator();

        while(var2.hasNext()) {
            String gearName = (String)var2.next();
            nameContainsNoCase(gearName).first().ifPresent((item) -> {
                InventoryInteraction.useItem(item, new String[]{"Equip", "Wield", "Wear"});
            });
        }

    }

    public static ItemQuery nameContainsNoCase(String name) {
        return Inventory.search().filter((widget) -> {
            return widget.getName().toLowerCase().contains(name.toLowerCase());
        });
    }

    public void toggleGear(List<String> gearNames) {
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.swapGear(gearNames);
        }
    }

    public void sendIntValue(int amount) {
        this.client.setVarcStrValue(359, Integer.toString(amount));
        this.client.setVarcIntValue(5, 7);
        this.client.runScript(new Object[]{681});
    }

    public int tickDelay() {
        int tickLength = (int)this.randomDelay(this.config.tickDelayWeightedDistribution(), this.config.tickDelayMin(), this.config.tickDelayMax(), this.config.tickDelayDeviation(), this.config.tickDelayTarget());
        return tickLength;
    }

    public long randomDelay(boolean weightedDistribution, int min, int max, int deviation, int target) {
        return weightedDistribution ? (long)this.clamp(-Math.log(Math.abs(random.nextGaussian())) * (double)deviation + (double)target, min, max) : (long)this.clamp((double)Math.round(random.nextGaussian() * (double)deviation + (double)target), min, max);
    }

    private double clamp(double val, int min, int max) {
        return Math.max((double)min, Math.min((double)max, val));
    }

    public int getRandomIntBetweenRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
