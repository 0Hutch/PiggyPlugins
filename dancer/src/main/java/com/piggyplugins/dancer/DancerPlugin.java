package com.piggyplugins.dancer;
/* Keep the change ya filthy animal
 * Chris
 */

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.PlayerPackets;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.inject.Provides;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javax.inject.Singleton;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
//import net.runelite.client.plugins.nex.maths.MathUtil;
//import net.runelite.client.plugins.nex.movement.MovementUtil;
//import net.runelite.client.plugins.nex.timer.TickTimer;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "<html><font color=#86C43F>[RB]</font> Dancer</html>",
	enabledByDefault = false,
	description = "Starts Configurable Dances",
	tags = {"bosses", "combat", "nex", "gwd", "pvm","ported", "RB"}
)

@Slf4j
@Singleton
public class DancerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private DancerPlugin plugin;

	@Inject
	private DancerConfig config;
	//private net.runelite.client.plugins.dancer.DancerConfig config;

	@Inject
	private OverlayManager overlayManager;

	private WorldPoint startingTile;

	private ArrayList<WorldPoint> danceTiles = new ArrayList<>();



	@Provides
	DancerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DancerConfig.class);
	}

	@Override
	protected void startUp()
	{
		if(client.getGameState() != GameState.LOGGED_IN){
			return;
		}
		startingTile = client.getLocalPlayer().getWorldLocation();

		for(String x : config.DanceString().split(",")){
			switch (x){
				case "N":
					startingTile = new WorldPoint( startingTile.getX(), startingTile.getY()+1,startingTile.getPlane());
					danceTiles.add(startingTile);
					break;
				case "S":
					startingTile = new WorldPoint( startingTile.getX(), startingTile.getY()-1,startingTile.getPlane());
					danceTiles.add(startingTile);
					break;
				case "E":
					startingTile = new WorldPoint( startingTile.getX()+1, startingTile.getY(),startingTile.getPlane());
					danceTiles.add(startingTile);
					break;
				case "W":
					startingTile = new WorldPoint( startingTile.getX()-1, startingTile.getY(),startingTile.getPlane());
					danceTiles.add(startingTile);
					break;
				case "NE":
					startingTile = new WorldPoint( startingTile.getX()+1, startingTile.getY()+1,startingTile.getPlane());
					danceTiles.add(startingTile);
					break;
				case "NW":
					startingTile = new WorldPoint( startingTile.getX()-1, startingTile.getY()+1,startingTile.getPlane());
					danceTiles.add(startingTile);
					break;
				case "SW":
					startingTile = new WorldPoint( startingTile.getX()-1, startingTile.getY()-1,startingTile.getPlane());
					danceTiles.add(startingTile);
					break;
				case "SE":
					startingTile = new WorldPoint( startingTile.getX()+1, startingTile.getY()-1,startingTile.getPlane());
					danceTiles.add(startingTile);
					break;

			}
		}

	}

	@Subscribe
	private void onGameTick(GameTick event) {
		while(!danceTiles.isEmpty()){
			MousePackets.queueClickPacket();
			MovementPackets.queueMovement(danceTiles.get(0));
			danceTiles.remove(danceTiles.get(0));
			if(danceTiles.isEmpty()){
				for(Player target : client.getPlayers()){
					if(target !=null){
						if(target.getName().equals(config.Name())){
							PlayerPackets.queuePlayerAction(target, "Follow");
							return;
						}
					}
				}

			}
			return;
		}
	}

}
