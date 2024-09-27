package com.wigglydonplugins.AutoVardorvis;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.PrayerUtil;
import com.piggyplugins.PiggyUtils.API.SpellUtil;
import com.wigglydonplugins.AutoVardorvis.state.StateHandler;
import com.wigglydonplugins.AutoVardorvis.state.StateHandler.State;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
    name = "<html><font color=\"#FF0000\">[WD]</font>Auto Vardorvis</html>",
    description = "Automated vardorvis killer"
)
public class AutoVardorvisPlugin extends Plugin {

  private static final int RANGE_PROJECTILE = 2521;
  private Projectile rangeProjectile;
  private int rangeTicks = 0;
  private int rangeCooldown = 0;


  int totalKills = 0;
  long startTime = System.currentTimeMillis();
  long elapsedTime = 0;
  boolean running = false;
  int tickDelay = 0;

  State botState = null;
  private boolean drankSuperCombat = false;

  @Inject
  private Client client;
  @Inject
  private OverlayManager overlayManager;
  @Inject
  private AutoVardorvisOverlay overlay;
  @Inject
  private AutoVardorvisConfig config;

  @Provides
  private AutoVardorvisConfig getConfig(ConfigManager configManager) {
    return configManager.getConfig(AutoVardorvisConfig.class);
  }

  @Override
  protected void startUp() throws Exception {
    startTime = System.currentTimeMillis();
    overlayManager.add(overlay);
    running = client.getGameState() == GameState.LOGGED_IN;
    botState = State.GO_TO_BANK;
  }

  @Override
  protected void shutDown() throws Exception {
    totalKills = 0;
    overlayManager.remove(overlay);
    drankSuperCombat = false;
    running = false;
    botState = null;
  }

  @Getter
  @Setter
  public static class MainClassContext {

    private Client client;
    private AutoVardorvisConfig config;
    private State contextBotState;
    private int contextTickDelay;
    private boolean drankSuperCombat;

    public MainClassContext(Client client, AutoVardorvisConfig config, State passedBotState,
        int passedTickDelay,
        boolean drankSuperCombat) {
      this.client = client;
      this.config = config;
      this.contextBotState = passedBotState;
      this.contextTickDelay = passedTickDelay;
      this.drankSuperCombat = drankSuperCombat;
    }
  }

  private void handleBotState(State passedBotState, int passedTickDelay) {
    if (passedBotState == null) {
      System.out.println("Null state...");
      return;
    }
    MainClassContext context = new MainClassContext(client, config, passedBotState, passedTickDelay,
        drankSuperCombat);
    StateHandler stateHandler = new StateHandler();
    stateHandler.handleState(botState, context);
    tickDelay = context.getContextTickDelay();
    botState = context.getContextBotState();
  }

  @Subscribe
  private void onGameTick(GameTick event) {
    long currentTime = System.currentTimeMillis();
    elapsedTime = currentTime - startTime;
    overlay.updateKillsPerHour();

    if (running) {
      if (tickDelay > 0) {
        tickDelay--;
        return;
      }

      //if run is off
      if (EthanApiPlugin.getClient().getVarpValue(173) == 0) {
        MousePackets.queueClickPacket();
        //turn on run
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
      }
      handleBotState(botState, tickDelay);
      if (isInFight()) {
        autoPray();
      }
    }
  }

  @Subscribe
  private void onVarbitChanged(VarbitChanged event) {
    if (event.getVarbitId() == Varbits.DIVINE_SUPER_COMBAT) {
      drankSuperCombat = true;
      if (event.getValue() <= 10) {
        Inventory.search().nameContains("Divine super combat").first().ifPresent(potion -> {
          InventoryInteraction.useItem(potion, "Drink");
        });
      }
    }
    if (event.getVarbitId() == Varbits.RESURRECT_THRALL) {
      if (event.getValue() == 0) {
        Widget thrallSpellWidget = SpellUtil.getSpellWidget(client, "Resurrect Greater Ghost");
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(thrallSpellWidget, "Cast");
      }
    }
  }

  @Subscribe
  private void onProjectileMoved(ProjectileMoved event) {
    if (client.getGameState() != GameState.LOGGED_IN) {
      return;
    }
    Projectile projectile = event.getProjectile();
    if (projectile.getId() == RANGE_PROJECTILE) {
      if (rangeProjectile == null && rangeCooldown == 0) {
        rangeTicks = 4;
        rangeProjectile = projectile;
      }
    }
  }

  private void autoPray() {
    if (rangeTicks > 0) {
      rangeTicks--;
      if (rangeTicks == 0) {
        rangeCooldown = 3;
      }
    }

    if (rangeTicks == 0) {
      rangeProjectile = null;
      if (rangeCooldown > 0) {
        rangeCooldown--;
      }
    }
    handleRangeFirstGameTick();
  }

  private void handleRangeFirstGameTick() {
    if (rangeTicks > 0) {
      if (!PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MISSILES)) {
        PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MISSILES);
      }
    } else {
      if (!PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MELEE)) {
        PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MELEE);
      }
    }
  }

  @Subscribe
  private void onChatMessage(ChatMessage e) {
    if (e.getMessage().contains("Your Vardorvis kill count is:")) {
      totalKills++;
    }
  }

  private boolean isInFight() {
    return client.isInInstancedRegion() && NPCs.search().nameContains("Vardorvis").nearestToPlayer()
        .isPresent();
  }
}
