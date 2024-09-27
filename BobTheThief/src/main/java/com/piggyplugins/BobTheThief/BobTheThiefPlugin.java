package com.piggyplugins.BobTheThief;

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.*;
import com.example.Packets.MousePackets;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.BankUtil;
import com.piggyplugins.PiggyUtils.API.EquipmentUtil;
import com.piggyplugins.PiggyUtils.API.InventoryUtil;
import com.piggyplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import com.google.inject.Inject;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;


@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Bob The Thief</html>",
        description = "Will interact with an object and drop or bank all items when inventory is full",
        tags = {"ethan", "piggy", "skilling"}
)
public class BobTheThiefPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    BobTheThiefConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BobTheThiefOverlay overlay;
    @Inject
    private ReflectBreakHandler breakHandler;
    State state;
    String debugMessage = "";
    boolean started;
    Random rand;
    int m_nextEatHealth = 0;
    boolean bankPin = false;
    private int timeout;
    NPC m_targetNPC = null;

    private final int RestockSize = 14;
    private final String Dodgy_Necklace = "Dodgy necklace";

    @Override
    protected void startUp() throws Exception {
        bankPin = false;
        keyManager.registerKeyListener(toggle);
        breakHandler.registerPlugin(this);
        this.overlayManager.add(overlay);
        rand = new Random();
    }

    @Override
    protected void shutDown() throws Exception {
        bankPin = false;
        keyManager.unregisterKeyListener(toggle);
        breakHandler.unregisterPlugin(this);
        this.overlayManager.remove(overlay);
    }

    @Provides
    private BobTheThiefConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BobTheThiefConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || !started || breakHandler.isBreakActive(this)) {
            // We do an early return if the user isn't logged in
            m_targetNPC = null;
            return;
        }
        debugMessage = "RUNNING";



        state = getNextState();
        handleState();
    }

    private void handleState() {
        switch (state) {
            case HANDLE_BREAK:
                breakHandler.startBreak(this);
                timeout = 10;
                break;
            //TODO: Redo banking
            case PICKPOCKET:
                pickpocket();
                setTimeout();
                break;
            case TIMEOUT:
                timeout--;
                break;
            case FIND_TARGET:
                if (config.searchNpc()) {
                    findNpc();
                } else {
                    findObject();
                }
                setTimeout();
                break;
            case EAT:
                if (!config.foodToUse().isEmpty())
                {
                    eatFood();
                    setTimeout();
                }
                break;
            case EQUIP_DODGY:
                EquipDodgy();
                break;
            case RESTOCK:
                restockItems();
                setTimeout();
                break;
            case DROP_ITEMS:
                dropItems();
                break;
        }
    }

    private State getNextState() {
        // self-explanatory, we just return a State if the conditions are met.
        if (EthanApiPlugin.isMoving() /*|| client.getLocalPlayer().getAnimation() != -1*/) {
            // this is to prevent clicks while animating/moving.
            return State.ANIMATING;
        }

        if (isBankPinOpen()) {
            return State.BANK_PIN_WAIT;
        }

        if (timeout > 0) {
            return State.TIMEOUT;
        }

        if (breakHandler.shouldBreak(this)) {
            return State.HANDLE_BREAK;
        }

        if (shouldBank() && Inventory.full() || (Bank.isOpen() && !isInventoryReset())) {
            if (shouldBank() && !isInventoryReset()) {
                return State.BANK;
            }
        }

        if ((isDroppingItems() && !isInventoryReset()) || !shouldBank() && Inventory.full()) {
            // if the user should be dropping items, we'll check if they're done
            // should sit at this state til it's finished.
            return State.DROP_ITEMS;
        }

        if (((client.getBoostedSkillLevel(Skill.HITPOINTS) < config.foodThresholdMax() && m_nextEatHealth == 0) ||
                client.getBoostedSkillLevel(Skill.HITPOINTS) < m_nextEatHealth))
        {
            Optional<Widget> food = InventoryUtil.nameContainsNoCase(config.foodToUse()).first();
            if (food.isPresent()) {
                return State.EAT;
            }
            else {
                if (InventoryUtil.emptySlots() < RestockSize)
                {
                    return State.DROP_ITEMS;
                }
                return State.RESTOCK;
            }
        }

        if (config.useDodgy() && !EquipmentUtil.hasItem(Dodgy_Necklace))
        {
            if (InventoryUtil.nameContainsNoCase(Dodgy_Necklace).first().isPresent())
            {
                return State.EQUIP_DODGY;
            }
            else
            {
                if (InventoryUtil.emptySlots() < RestockSize)
                {
                    return State.DROP_ITEMS;
                }
                return State.RESTOCK;
            }
        }

        if (m_targetNPC != null)
            return State.PICKPOCKET;

        // default it'll look for an object.
        return State.FIND_TARGET;
    }

    private void pickpocket()
    {
        MousePackets.queueClickPacket();
        NPCInteraction.interact(m_targetNPC, "Pickpocket");
    }

    private void EquipDodgy()
    {
        Optional<Widget> dodgy = InventoryUtil.nameContainsNoCase(Dodgy_Necklace).first();
        if (dodgy.isPresent())
        {
            MousePackets.queueClickPacket();
            InventoryInteraction.useItem(Dodgy_Necklace, "Wear");
        }
    }

    private void eatFood() {
        Optional<Widget> food = InventoryUtil.nameContainsNoCase(config.foodToUse()).first();
        if (food.isPresent()) {
            MousePackets.queueClickPacket();
            InventoryInteraction.useItem(food.get(), "Eat");
            m_nextEatHealth = (Math.abs(rand.nextInt()) % (config.foodThresholdMax() - config.foodThresholdMin())) + config.foodThresholdMin();
        }
    }

    private void restockItems()
    {
        debugMessage = "Restock called";
        if (Bank.isOpen()) {
            debugMessage = "Taking out food";
            Optional<Widget> bankFood = BankUtil.nameContainsNoCase(config.foodToUse()).first();
            bankFood.ifPresent(widget -> BankInteraction.withdrawX(widget, 10));

            Optional<Widget> bankDodgy = BankUtil.nameContainsNoCase(Dodgy_Necklace).first();
            bankDodgy.ifPresent(widget -> BankInteraction.withdrawX(widget, 4));
        }
        else {
            debugMessage = "Finding bank";

            Optional<TileObject> bankBooth = TileObjects.search().filter(tileObject -> {
                ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(tileObject);
                return getName().toLowerCase().contains("bank") ||
                        Arrays.stream(objectComposition.getActions()).anyMatch(action -> action != null && action.toLowerCase().contains("bank"));
            }).nearestToPlayer();

            if (bankBooth.isPresent()) {
                debugMessage = "Found bank";

                MousePackets.queueClickPacket();
                TileObjectInteraction.interact(bankBooth.get(), "Bank");
                setTimeout();
            }
        }
    }

    private void equipDodgy()
    {

    }

    private boolean isBankPinOpen() {
        Widget bankPinWidget = client.getWidget(213, 0);
        if (bankPinWidget == null) {
            return false;
        }
        return !bankPinWidget.isHidden();
    }

    private void dropItems() {
        List<Widget> itemsToDrop = Inventory.search()
                .filter(item -> !shouldKeep(item.getName()) && !isFood(item.getName())).result(); // filter the inventory to only get the items we want to drop

        if (config.itemsToKeep().isEmpty())
            itemsToDrop = Inventory.search().result();

        for (int i = 0; i < Math.min(itemsToDrop.size(), RandomUtils.nextInt(config.dropPerTickOne(), config.dropPerTickTwo())); i++) {
            InventoryInteraction.useItem(itemsToDrop.get(i), "Drop"); // we'll loop through this at a max of 10 times.  can make this a config options.  drops x items per tick (x = 10 in this example)
        }
    }

    private void findNpc() {
        String npcName = config.objectToInteract();
        NPCs.search().withName(npcName).nearestToPlayer().ifPresent(npc -> {
            m_targetNPC = npc;
        });
    }

    private void findObject() {
        String objectName = config.objectToInteract();
        TileObjects.search().withName(objectName).nearestToPlayer().ifPresent(tileObject -> {
            ObjectComposition comp = TileObjectQuery.getObjectComposition(tileObject);
            MousePackets.queueClickPacket();
            TileObjectInteraction.interact(tileObject, "Steal from");
        });

    }

    private boolean isInventoryReset() {
        List<Widget> inventory = Inventory.search().result();
        for (Widget item : inventory) {
            if (!shouldKeep(Text.removeTags(item.getName()))) { // using our shouldKeep method, we can filter the items here to only include the ones we want to drop.
                return false;
            }
        }
        return true; // we will know that the inventory is reset because the inventory only contains items we want to keep
    }

    private boolean isDroppingItems() {
        return state == State.DROP_ITEMS; // if the user is dropping items, we don't want it to proceed until they're all dropped.
    }


    private boolean shouldKeep(String name) {
        List<String> itemsToKeep = new ArrayList<>(List.of(config.itemsToKeep().split(",")));
        itemsToKeep.addAll(List.of(config.foodToUse().split(",")));

        return itemsToKeep.stream()
                .anyMatch(i -> Text.removeTags(name.toLowerCase()).contains(i.toLowerCase())) || Text.removeTags(name.toLowerCase()).contains(Dodgy_Necklace.toLowerCase());

    }

    private void setTimeout() {
        timeout = RandomUtils.nextInt(config.tickdelayMin(), config.tickDelayMax());
    }

    private boolean isFood(String name) {
        String[] tools = config.foodToUse().split(","); // split the tools listed by comma, no space.

        return Arrays.stream(tools) // stream the array using Arrays.stream() from java.util
                .anyMatch(i -> name.toLowerCase().contains(i.toLowerCase())); // more likely for user error than the shouldKeep option, but we'll follow the same idea as shouldKeep.
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    private boolean shouldBank() {
        return config.shouldBank() &&
                (NPCs.search().withAction("Bank").first().isPresent() || TileObjects.search().withAction("Bank").first().isPresent()
                || TileObjects.search().withAction("Collect").first().isPresent() && !bankPin);
    }

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;

        if (!started) {
            this.state = State.TIMEOUT;
            breakHandler.stopPlugin(this);
        } else {
            breakHandler.startPlugin(this);
        }

    }
}
