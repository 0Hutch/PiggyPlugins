package com.wigglydonplugins.AutoVardorvis.state.botStates;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.wigglydonplugins.AutoVardorvis.AutoVardorvisPlugin.MainClassContext;
import com.wigglydonplugins.AutoVardorvis.state.StateHandler.State;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;

public class GoToVardorvisState {

  MainClassContext context;

  public void execute(MainClassContext context) {
    this.context = context;
    boolean inStrangleWood = TileObjects.search().withId(48723).first().isPresent();
    Optional<TileObject> vardorvisRock = TileObjects.search().withId(49495).first();
    Optional<TileObject> tunnel1 = TileObjects.search().withId(48745).first();
    Optional<TileObject> tunnel2 = TileObjects.search().withId(48746).first();
    WorldPoint playerLocation = context.getClient().getLocalPlayer().getWorldLocation();

    if (isInFight(context.getClient())) {
      context.setContextBotState(State.FIGHTING);
    }
    if (tunnel1.isPresent()) {
      
      if (tunnel1.get().getWorldLocation().distanceTo(playerLocation) <= 5) {
        tunnel1.ifPresent((t1) -> {
          TileObjectInteraction.interact(t1, "Enter");
        });
        return;
      }
    }

    tunnel2.ifPresent((t2) -> {
      WorldPoint tunnel2Location = new WorldPoint(
          tunnel2.get().getWorldLocation().getX() - 30,
          tunnel2.get().getWorldLocation().getY() - 5,
          0);
      if (inStrangleWood && !isMoving()
          || playerLocation.getX() == tunnel2Location.getX()
          && playerLocation.getY() == tunnel2Location.getY()
      ) {
        if (tunnel1.isPresent()) {
          if (tunnel1.get().getWorldLocation().distanceTo(playerLocation) <= 4) {
            tunnel1.ifPresent((t1) -> {
              TileObjectInteraction.interact(t1, "Enter");
            });
            return;
          }
        }
        if (tunnel2.get().getWorldLocation()
            .distanceTo(playerLocation) <= 20) {
          MousePackets.queueClickPacket();
          MovementPackets.queueMovement(tunnel2Location);
        } else {
          if (inStrangleWood && !isMoving()) {
            vardorvisRock.ifPresentOrElse((rock) -> {
              TileObjectInteraction.interact(rock, "Climb-over");
            }, () -> {
              tunnel1.ifPresent((t1) -> {
                TileObjectInteraction.interact(t1, "Enter");
              });
            });
          }
        }
      }
    });

  }

  private boolean isMoving() {
    return EthanApiPlugin.isMoving() || context.getClient().getLocalPlayer().getAnimation() != -1;
  }

  private boolean isInFight(Client client) {
    return client.isInInstancedRegion() && NPCs.search().nameContains("Vardorvis").nearestToPlayer()
        .isPresent();
  }

}
