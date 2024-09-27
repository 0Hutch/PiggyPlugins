package com.polyplugins.AutoWintertodt.Tasks;


import com.polyplugins.AutoWintertodt.AutoWintertodtPlugin;
import com.polyplugins.AutoWintertodt.Data.Data;
import com.polyplugins.AutoWintertodt.Model.Task;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import net.runelite.api.Skill;

public class EatFood implements Task {
    public EatFood() {
    }

    public boolean activateTask() {
        if (Inventory.search().withAction("Eat").first().isEmpty() && Inventory.search().withAction("Drink").first().isEmpty()) {
            return false;
        } else {
            return EthanApiPlugin.getClient().getBoostedSkillLevel(Skill.HITPOINTS) < AutoWintertodtPlugin.getWintertodtConfig().eatAt();
        }
    }

    public void runTask() {
        Inventory.search().withAction("Eat").first().ifPresent((x) -> {
            InventoryInteraction.useItem(x, new String[]{"Eat"});
        });
        Inventory.search().withAction("Drink").first().ifPresent((x) -> {
            InventoryInteraction.useItem(x, new String[]{"Drink"});
        });
        TileObjects.search().withAction("Feed").withinDistance(3).nearestToPoint(Data.BRAZIER_TILE).ifPresent((x) -> {
            TileObjectInteraction.interact(x, new String[]{"Feed"});
        });
    }
}
