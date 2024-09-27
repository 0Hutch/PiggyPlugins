package com.wigglydonplugins.AutoVardorvis.state.botStates;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.wigglydonplugins.AutoVardorvis.AutoVardorvisConfig;
import com.wigglydonplugins.AutoVardorvis.AutoVardorvisPlugin.MainClassContext;
import com.wigglydonplugins.AutoVardorvis.state.StateHandler.State;
import java.awt.event.KeyEvent;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class BankingState {

  private AutoVardorvisConfig config;
  private Client client;


  private final WorldArea bankArea = new WorldArea(2096, 3911, 20, 11, 0);
  private final WorldPoint bankLocation = new WorldPoint(2099, 3919, 0);

  public void execute(MainClassContext context) {
    int tickDelay = context.getContextTickDelay();
    this.config = context.getConfig();
    this.client = context.getClient();
    Optional<TileObject> strangleWoodPyramid = TileObjects.search().withId(48723).first();

    strangleWoodPyramid.ifPresent((e) -> {
      context.setContextBotState(State.GO_TO_VARDORVIS);
    });

    if (preparedForTrip() && !Bank.isOpen() && NPCs.search().nameContains("Jack").nearestToPlayer()
        .isPresent()) {

      Widgets.search().withTextContains("Enter amount:").first().ifPresent(w -> {
        client.runScript(299, 1, 0, 0);
      });
      Widgets.search().withTextContains("Where would you like to teleport to?").first()
          .ifPresentOrElse(((e) -> {
            WidgetPackets.queueResumePause(e.getId(), 5);
            context.setContextTickDelay(4);
          }), () -> {
            Inventory.search().nameContains("Ring of shadows").result().stream().findFirst()
                .ifPresent(ring ->
                    InventoryInteraction.useItem(ring, "Teleport")
                );
          });
    } else if (!Bank.isOpen() && !preparedForTrip()) {
      if (client.getLocalPlayer().getWorldLocation().getX() != bankLocation.getX()
          && client.getLocalPlayer().getWorldLocation().getY() != bankLocation.getY()) {
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(bankLocation);
      } else {
        NPCs.search().nameContains("Jack").nearestToPlayer().ifPresent((bank) -> {
          NPCInteraction.interact(bank, "Bank");
          context.setContextTickDelay(3);
        });
      }
    } else if (Bank.isOpen() && !preparedForTrip()) {
      bank(context);
    } else if (Bank.isOpen() && preparedForTrip()) {
      sendKey(KeyEvent.VK_ESCAPE);
      Widgets.search().withTextContains("Enter amount:").first().ifPresent(w -> {
        client.runScript(299, 1, 0, 0);
      });
    }
  }

  private void bank(MainClassContext context) {
    if (Bank.isOpen() && !preparedForTrip() && Inventory.getEmptySlots() != 28) {
      Widgets.search()
          .filter(widget -> widget.getParentId() != 786474).withAction("Deposit inventory").first()
          .ifPresent(button -> {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(button, "Deposit inventory");
          });
    } else if (!preparedForTrip()) {
      withdraw("Ring of shadows", 1);
      withdraw("Teleport to house", 50);
      withdraw("Divine super combat", 1);
      withdraw("Rune pouch", 1);
      withdraw("Soul rune", 1000);
      withdraw("Death rune", 1000);
      withdraw("Book of the dead", 1);
      withdraw("Voidwaker", 1);
      withdraw("Prayer potion", config.PPOTS_TO_BRING());
      withdraw("Manta ray", 100);
      sendKey(KeyEvent.VK_ESCAPE);
      context.setContextTickDelay(2);
    } else {
      sendKey(KeyEvent.VK_ESCAPE);
    }
  }

  private void withdraw(String name, int amount) {
    Bank.search().nameContains(name).first().ifPresent(item ->
        BankInteraction.withdrawX(item, amount)
    );
  }

  private boolean hasItemQuantity(String name, int quantity) {
    return (Inventory.search().nameContains(name).result().size() == quantity);
  }

  private boolean preparedForTrip() {
    return hasItemQuantity("Ring of shadows", 1) &&
        (!Inventory.search().nameContains("Teleport to house").result().isEmpty()) &&
        hasItemQuantity("Divine super combat", 1) &&
        hasItemQuantity("Prayer potion", config.PPOTS_TO_BRING()) &&
        (Inventory.search().nameContains("Manta ray").result().size() >= config.MIN_FOOD()) &&
        Inventory.full();
  }

  private int getEmptySlots() {
    return 28 - Inventory.search().result().size();
  }

  private void sendKey(int key) {
    keyEvent(KeyEvent.KEY_PRESSED, key);
    keyEvent(KeyEvent.KEY_RELEASED, key);
  }

  private void keyEvent(int id, int key) {
    KeyEvent e = new KeyEvent(
        client.getCanvas(),
        id,
        System.currentTimeMillis(),
        0,
        key,
        KeyEvent.CHAR_UNDEFINED
    );
    client.getCanvas().dispatchEvent(e);
  }
}

