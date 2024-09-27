package com.piggyplugins.autocannonballer;

/* Keep the change ya filthy animal
 * Chris
 */

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.BankInventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.PlayerPackets;
import com.example.Packets.WidgetPackets;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.inject.Provides;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import javax.inject.Singleton;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
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
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
//import com.piggyplugins.nex.maths.MathUtil;
//import com.piggyplugins.nex.movement.MovementUtil;
//import com.piggyplugins.nex.timer.TickTimer;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "<html><font color=#86C43F>[RB]</font> Auto CannonBaller</html>",
	enabledByDefault = false,
	description = "Starts Cannonballs",
	tags = {"bosses", "combat", "nex", "gwd", "pvm","ported", "RB"}
)

@Slf4j
@Singleton
public class CannonBallerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private CannonBallerPlugin plugin;

	@Inject
	private CannonBallerOverlay overlay;

	@Inject
	private CannonBallerConfig config;

	@Inject
	private OverlayManager overlayManager;

	private WorldPoint startingTile;

	private ArrayList<WorldPoint> danceTiles = new ArrayList<>();

	public botState currstate;

	public int timeout;

	Instant timer;

	public int createdCannonballs = 1;

	public int actionsPrHour;

	private ArrayList<Integer> molds = new ArrayList<>();



	@Provides
	CannonBallerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CannonBallerConfig.class);
	}

	@Override
	protected void startUp()
	{
		if(client.getGameState() != GameState.LOGGED_IN){
			return;
		}

		overlayManager.add(overlay);
		currstate = determineState();

		timeout = 0;

		molds.add(ItemID.AMMO_MOULD);
		molds.add(ItemID.DOUBLE_AMMO_MOULD);

		timer = Instant.now();
		createdCannonballs =0;

	}

	@Override
	protected void shutDown()
	{
		currstate = null;
		overlayManager.remove(overlay);

		timeout = 0;

		molds.add(ItemID.AMMO_MOULD);
		molds.add(ItemID.DOUBLE_AMMO_MOULD);

		timer = null;

	}

	private int calculateActionPerHour(Instant timer, int actionAmount){
		Instant endTime = Instant.now();
		Duration elapsedDuration = Duration.between(timer, endTime);
		long elapsedSeconds = elapsedDuration.getSeconds();
		double countRatePerSeond = (double) actionAmount / elapsedSeconds;
		int actionsPerHour = (int) (countRatePerSeond * 3600);
		return actionsPerHour;
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		actionsPrHour = calculateActionPerHour(timer, createdCannonballs);
		//System.out.println("1");
		if(client.getGameState() != GameState.LOGGED_IN){
			return;
		}
		//System.out.println("2");
		if (EthanApiPlugin.isMoving()){
			return;
		}

		//System.out.println("3");
		if (timeout > 0){
			timeout--;
			return;
		}

		System.out.println(currstate);

		switch (currstate){
			case BANKING:

				break;
			case MAKING_CANNONBALLS:
				if(client.getWidget(270,4) == null){
					System.out.println("Null");

						System.out.println("Hidden");
						this.nextTimeout();
						Optional<TileObject> furnace = TileObjects.search().nameContains("Furnace").first();
						if (furnace.isPresent()) {

							TileObjectInteraction.interact(furnace.get(), "Smelt");
						}
				}
				else{

					int steelbars = Inventory.search().withId(ItemID.STEEL_BAR).result().size();
					timeout = Inventory.search().withId(ItemID.DOUBLE_AMMO_MOULD).first().isPresent() ? steelbars *5: steelbars * 10;
					MousePackets.queueClickPacket();
					WidgetPackets.queueResumePause(17694734,steelbars);;
					currstate = botState.OPEN_BANK;
				}
				break;
			case OPEN_BANK:
				Optional<Widget> steel = Inventory.search().withId(ItemID.STEEL_BAR).first();
				if(steel.isPresent()){
					currstate = botState.MAKING_CANNONBALLS;
					return;
				}

				if (plugin.client.getWidget(WidgetInfo.BANK_CONTAINER) == null) {
					Optional<TileObject> bank = TileObjects.search().nameContains("Bank booth").nearestToPlayer();
					if(bank.isPresent()){
						TileObjectInteraction.interact(bank.get(), "Bank");
					}
					else{
						EthanApiPlugin.stopPlugin(this);
					}
					return;
				}
				else{
					Optional<Widget> cannonballsToBank = BankInventory.search().withId(ItemID.CANNONBALL).first();
					this.createdCannonballs += cannonballsToBank.get().getItemQuantity();
					MousePackets.queueClickPacket();
					BankInventoryInteraction.useItem(cannonballsToBank.get(),"Deposit-ALL");
					Optional<Widget> bankBars = Bank.search().withId(ItemID.STEEL_BAR).first();
					if(bankBars.isPresent()){
						BankInteraction.withdrawX(bankBars.get(), 27);
						currstate = botState.MAKING_CANNONBALLS;
						nextTimeout();
						client.runScript(ScriptID.MESSAGE_LAYER_CLOSE, 1, 1, 0);
						break;
					}
					else{
						EthanApiPlugin.stopPlugin(this);
					}
					return;

				}


			case ERROR_START_WITH_MOLD:
				EthanApiPlugin.stopPlugin(this);
		}

	}

	private void nextTimeout(){
		Random random = new Random();
		timeout = random.nextInt((config.ttloghigh()- config.ttloglow()) +1) + config.ttloglow();
	}

	private botState determineState(){
		Optional<Widget> actualMould = Inventory.search().idInList(molds).first();
		if(!actualMould.isPresent()){
			return botState.ERROR_START_WITH_MOLD;
		}
		Optional<Widget> SteelBar = Inventory.search().withId(ItemID.STEEL_BAR).first();
		if(SteelBar.isPresent()){
			return botState.MAKING_CANNONBALLS;
		}
		else{
			return botState.OPEN_BANK;
		}
	}



	enum botState{
		OPEN_BANK,
		BANKING,
		MAKING_CANNONBALLS,
		ERROR_START_WITH_MOLD
	}

}
