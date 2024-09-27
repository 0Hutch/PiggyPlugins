package com.wigglydonplugins.AutoVardorvis.state.botStates;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.piggyplugins.PiggyUtils.API.PrayerUtil;
import com.piggyplugins.PiggyUtils.API.SpellUtil;
import com.wigglydonplugins.AutoVardorvis.AutoVardorvisConfig;
import com.wigglydonplugins.AutoVardorvis.AutoVardorvisPlugin.MainClassContext;
import com.wigglydonplugins.AutoVardorvis.state.StateHandler.State;
import java.util.List;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

public class FightingState {

  private final String VARDORVIS = "Vardorvis";
  private Client client;
  private static WorldPoint safeTile = null;
  private static WorldPoint axeMoveTile = null;
  private boolean drankSuperCombat;
  private static boolean hasCastDeathCharge = false;
  private static int axeTicks = 0;
  private static int specTicks = 0;
  private static int thrallTicks = 0;
  private static int vardorvisHpPercent = 100;
  private MainClassContext context;

  public void execute(MainClassContext context) {
    client = context.getClient();
    this.context = context;
    AutoVardorvisConfig config = context.getConfig();
    drankSuperCombat = context.isDrankSuperCombat();
    List<NPC> newAxes = NPCs.search().withId(12225).result();
    List<NPC> activeAxes = NPCs.search().withId(12227).result();
    Optional<NPC> vardorvis = NPCs.search().nameContains(VARDORVIS).first();
    WorldPoint playerTile = client.getLocalPlayer().getWorldLocation();
    Optional<TileObject> safeRock = TileObjects.search().withAction("Leave").first();

    vardorvis.ifPresent(FightingState::updateNpcHp);

    if (!TileItems.search().empty()) {
      TileItems.search().first().ifPresent((item) -> {
        if (Inventory.full() && !Inventory.search().withAction("Eat").empty()) {
          Inventory.search().withAction("Eat").result().stream()
              .findFirst()
              .ifPresent(food -> InventoryInteraction.useItem(food, "Eat")
              );
        }
        item.interact(false);
      });
      return;
    }

    if (TileObjects.search().nameContains("Portal Nexus").first().isPresent()) {
      System.out.println("inside house in fighting state");
      context.setContextBotState(State.GO_TO_BANK);
    }

    if (!PrayerUtil.isPrayerActive(Prayer.PIETY)) {
      PrayerUtil.togglePrayer(Prayer.PIETY);
    }

    //axe dodge
    if (safeTile != null) {
      if (!newAxes.isEmpty()) {
        newAxes.forEach((axe) -> {
          if (axe.getWorldLocation().getX() == safeTile.getX() - 1
              && axe.getWorldLocation().getY() == safeTile.getY() - 1) {
            handleAxeMove();
          }
        });
      } else if (!activeAxes.isEmpty()) {
        activeAxes.forEach((axe) -> {
          if (axe.getWorldLocation().getX() == safeTile.getX() + 1
              && axe.getWorldLocation().getY() == safeTile.getY() - 1) {
            axeTicks = 1;
            handleAxeMove();
          }
        });
      }

    }

    doBloodCaptcha();
    drinkPrayer(config.DRINKPRAYERAT());
    eat(config.EATAT());
    useSpecialAttack();

    if (!isInFight(client)) {
      turnOffPrayers();
      if (TileItems.search().first().isEmpty() && !enoughFood()
      ) {
        teleToHouse();
        return;
      } else if (safeTile != null) {
        if (playerTile.getX() != safeTile.getX() || playerTile.getY() != safeTile.getY()) {
          movePlayerToTile(safeTile);
        }
      }
    }

    //initial attack
    if (vardorvis.isPresent() && safeTile != null) {
      if (vardorvis.get().getWorldLocation().getX() == safeTile.getX() + 4
          && vardorvis.get().getWorldLocation().getY() == safeTile.getY() - 1
          && vardorvis.get().getAnimation() == -1
      ) {
        if (enoughFood()) {
          vardorvis.ifPresent(npc -> {
            NPCInteraction.interact(npc, "Attack");
            if (!drankSuperCombat) {
              Inventory.search().nameContains("Divine super combat").first().ifPresent(potion -> {
                InventoryInteraction.useItem(potion, "Drink");
                drankSuperCombat = true;
                context.setDrankSuperCombat(true);
              });
            }
            summonThrall();
          });
        } else {
          teleToHouse();
          context.setContextTickDelay(3);
        }
        return;
      } else if (vardorvis.get().getWorldLocation().getX() == safeTile.getX()) {
        EthanApiPlugin.sendClientMessage("Vardorvis stuck");
        movePlayerToTile(safeTile);
        eat(config.EATAT());
        return;
      }
    }

    if (safeTile != null) {
      if (playerTile.getX() != safeTile.getX() || playerTile.getY() != safeTile.getY()) {
        movePlayerToTile(safeTile);
        return;
      }
    }

    if (safeRock.isPresent() && safeTile == null) {
      WorldPoint safeRockLocation = safeRock.get().getWorldLocation();
      safeTile = new WorldPoint(safeRockLocation.getX() + 6, safeRockLocation.getY() - 10, 0);
      axeMoveTile = new WorldPoint(safeTile.getX() + 2, safeTile.getY() - 2, 0);
    }
    context.setDrankSuperCombat(drankSuperCombat);

    if (!client.getLocalPlayer().isInteracting()) {
      NPCs.search().nameContains(VARDORVIS).first().ifPresent(npc -> {
        NPCInteraction.interact(npc, "Attack");
      });
    }
    if (vardorvisHpPercent <= 30 && !hasCastDeathCharge) {
      castDeathCharge();
      hasCastDeathCharge = true;
    }

  }

  private void useSpecialAttack() {
    if (specTicks > 0) {
      specTicks--;
    }
    if (client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) >= 500 && (vardorvisHpPercent != 100
        && vardorvisHpPercent >= 50)) {
      if (Inventory.search().nameContains("Voidwaker").first().isPresent()) {
        InventoryInteraction.useItem("Voidwaker", "Wield");
      } else if (specTicks == 0) {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485795, -1, -1);
        specTicks = 6;
      }
    } else {
      if (Inventory.search().nameContains("Abyssal tentacle").first().isPresent()) {
        InventoryInteraction.useItem("Abyssal tentacle", "Wield");
      }
    }
  }

  private void castDeathCharge() {
    Widget deathChargeWidget = SpellUtil.getSpellWidget(client,
        "Death Charge");
    MousePackets.queueClickPacket();
    WidgetPackets.queueWidgetAction(deathChargeWidget, "Cast");
  }

  private void summonThrall() {
    if (thrallTicks > 0) {
      thrallTicks--;
    } else if (thrallTicks == 0) {
      Widget thrallSpellWidget = SpellUtil.getSpellWidget(client,
          "Resurrect Greater Ghost");
      MousePackets.queueClickPacket();
      WidgetPackets.queueWidgetAction(thrallSpellWidget, "Cast");
      thrallTicks = 10;
    }
  }

  private void handleAxeMove() {
    switch (axeTicks) {
      case 0:
        break;
      case 1:
        movePlayerToTile(axeMoveTile);
        break;
    }
    if (axeTicks == 1) {
      axeTicks = 0;
    } else {
      axeTicks++;
    }
  }

  private void movePlayerToTile(WorldPoint tile) {
    MousePackets.queueClickPacket();
    MovementPackets.queueMovement(tile);
  }

  private void doBloodCaptcha() {
    List<Widget> captchaBlood = Widgets.search().filter(widget -> widget.getParentId() != 9764864)
        .hiddenState(false).withAction("Destroy").result();
    if (!captchaBlood.isEmpty()) {
      captchaBlood.forEach(x -> {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(x, "Destroy");
      });
    }
  }

  private boolean isInFight(Client client) {
    return client.isInInstancedRegion() && NPCs.search().nameContains(VARDORVIS).nearestToPlayer()
        .isPresent();
  }

  private boolean enoughFood() {
    return context.getConfig().MIN_FOOD() <= Inventory.search().withAction("Eat")
        .result()
        .size() && context.getConfig().MIN_PRAYER_POTIONS() <= Inventory.search()
        .nameContains("Prayer potion").result().size();
  }

  private void turnOffPrayers() {
    if (PrayerUtil.isPrayerActive(Prayer.PIETY)) {
      PrayerUtil.togglePrayer(Prayer.PIETY);
    }
    if (PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MELEE)) {
      PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MELEE);
    }
  }

  private void eat(int at) {
    if (needsToEat(at)) {
      Inventory.search().withAction("Eat").result().stream()
          .findFirst()
          .ifPresentOrElse(food -> InventoryInteraction.useItem(food, "Eat"),
              this::teleToHouse
          );
    }
  }

  private void drinkPrayer(int at) {
    if (needsToDrinkPrayer(at)) {
      Inventory.search().nameContains("Prayer potion").result().stream()
          .findFirst()
          .ifPresentOrElse(prayerPotion -> InventoryInteraction.useItem(prayerPotion, "Drink"),
              this::teleToHouse
          );
    }
  }

  private boolean needsToEat(int at) {
    return client.getBoostedSkillLevel(Skill.HITPOINTS) <= at;
  }


  private void teleToHouse() {
    EthanApiPlugin.sendClientMessage("teleporting to house");
    InventoryInteraction.useItem("Teleport to house", "Break");
    drankSuperCombat = false;
    safeTile = null;
    axeMoveTile = null;
    vardorvisHpPercent = 100;
    context.setContextBotState(State.GO_TO_BANK);
  }

  private boolean needsToDrinkPrayer(int at) {
    return client.getBoostedSkillLevel(Skill.PRAYER) <= at;
  }

  public static int getHpPercentValue(float ratio, float scale) {
    return Math.round((ratio / scale) * 100f);
  }

  public static void updateNpcHp(NPC npc) {
    float healthRatio = npc.getHealthRatio();
    float healthScale = npc.getHealthScale();
    int currentHp = getHpPercentValue(healthRatio, healthScale);

    if (currentHp < vardorvisHpPercent && currentHp > -1) {
      vardorvisHpPercent = currentHp;
    }
    if (currentHp == 0 && vardorvisHpPercent == 0) {
      vardorvisHpPercent = 100;
      hasCastDeathCharge = false;
      axeTicks = 0;
    }
  }
}

