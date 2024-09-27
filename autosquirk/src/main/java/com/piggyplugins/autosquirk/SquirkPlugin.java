package com.piggyplugins.autosquirk;

/* Keep the change ya filthy animal
 * Chris
 */

import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.*;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.PathingTesting.PathingTesting;
import com.google.inject.Provides;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import javax.inject.Inject;
import javax.inject.Singleton;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
//import com.piggyplugins.autocannonballer.CannonBallerPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "<html><font color=#86C43F>[RB]</font> Auto Squirker</html>",
	enabledByDefault = false,
	description = "Starts Cannonballs",
	tags = {"bosses", "combat", "nex", "gwd", "pvm","ported", "RB"}
)

@Slf4j
@Singleton
public class SquirkPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SquirkPlugin plugin;

	@Inject
	private SquirkOverlay overlay;

	@Inject
	private SquirkConfig config;

	@Inject
	private OverlayManager overlayManager;

	private WorldPoint startingTile;

	private ArrayList<WorldPoint> danceTiles = new ArrayList<>();

	public botState currstate;

	public int timeout;

	Instant timer;

	public int createdCannonballs = 1;

	public int actionsPrHour;

	private WorldPoint inHouse = new WorldPoint(3321,3139,0);

	private WorldPoint bank = new WorldPoint(3270,3166,0);


	@Provides
	SquirkConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SquirkConfig.class);
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

		timer = Instant.now();
		createdCannonballs =0;

	}

	@Override
	protected void shutDown()
	{
		currstate = null;
		overlayManager.remove(overlay);

		timeout = 0;

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

	boolean runIsOff() {
		return EthanApiPlugin.getClient().getVarpValue(173) == 0;
	}

	boolean hasMoreThanZeroEnergy() {
		return EthanApiPlugin.getClient().getEnergy() > 100;
	}

	void enableRun() {
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		if (runIsOff() && hasMoreThanZeroEnergy()) {
			if(Inventory.search().nameContains("Stamina").first().isPresent()) {
				InventoryInteraction.useItem(Inventory.search().nameContains("Stamina").first().get(), "Drink");
			}
			enableRun();
		}
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
			case SQUIRKIN:
				if(Inventory.getItemAmount(ItemID.SUMMER_SQIRK) >=2){
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetOnWidget(Inventory.search().withId(ItemID.SUMMER_SQIRK).first().get(),Inventory.search().withId(ItemID.PESTLE_AND_MORTAR).first().get());

				}
				else if(Inventory.getItemAmount(ItemID.BEER_GLASS) <1){
					TileObjectInteraction.interact(TileObjects.search().withAction("Drink-from").first().get(),"Drink-from");
					currstate = botState.HEADED_BACK_TO_BANK;
				}
				else{
					//EthanApiPlugin.sendClientMessage(String.valueOf(client.getLocalPlayer().getWorldLocation().distanceTo(TileObjects.search().withId(12943).first().get().getWorldLocation())));
					if(client.getLocalPlayer().getWorldLocation().distanceTo(TileObjects.search().withId(12943).first().get().getWorldLocation()) <=10){
						TileObjectInteraction.interact(TileObjects.search().withId(12943).first().get(),"Pick-Fruit");
					}
					else{
						TileObjectInteraction.interact(TileObjects.search().nearestToPoint(new WorldPoint(2910,5480,0)).get(),"Open");
					}
				}
				break;
			case OPEN_BANK:
				if(Bank.isOpen()){
					if(config.stams() > 0) {
						if (EthanApiPlugin.getClient().getEnergy() < 4500 || BankInventory.search().nameContains("Stamina").first().isPresent()) {
							if (Bank.search().nameContains("Stamina").first().isPresent() && BankInventory.search().nameContains("Stamina").first().isEmpty()) {
								BankInteraction.useItem(Bank.search().nameContains("Stamina").first().get(), "Withdraw-1");

							} else if (BankInventory.search().nameContains("Stamina").first().isPresent()) {
								BankInventoryInteraction.useItem(BankInventory.search().nameContains("Stamin").first().get(), "Drink");
								BankInventoryInteraction.useItem(BankInventory.search().nameContains("Stamin").first().get(), "Deposit-1");
							} else {
								EthanApiPlugin.sendClientMessage("Out of items");
								EthanApiPlugin.stopPlugin(plugin);
							}
							return;
						}
					}

					if(BankInventory.search().withId(ItemID.SUMMER_SQIRKJUICE).first().isPresent()){
						MousePackets.queueClickPacket();
						WidgetPackets.queueWidgetActionPacket(1, 786474, -1, -1);
						createdCannonballs = createdCannonballs + (25- config.stams());
					}

					if(BankInventory.search().withId(ItemID.BEER_GLASS).first().isEmpty()){
						if(Bank.search().withId(ItemID.BEER_GLASS).first().isPresent()) {
							BankInteraction.useItem(Bank.search().withId(ItemID.PESTLE_AND_MORTAR).first().get(),"Withdraw-1");
							for (int i = 0; i < config.stams(); i++) {
								BankInteraction.useItem(Bank.search().withId(ItemID.STAMINA_POTION4).first().get(),"Withdraw-1");
							}
							BankInteraction.withdrawX(Bank.search().withId(ItemID.BEER_GLASS).first().get(),25 - config.stams());
							currstate = botState.HEADED_BACK_TO_SQUIRK;
							return;
						}
						else{
							EthanApiPlugin.sendClientMessage("Out of items");
							EthanApiPlugin.stopPlugin(plugin);
						}
					}
				}
				if(TileObjects.search().withAction("Bank").first().isPresent()) {
				TileObjectInteraction.interact(TileObjects.search().withAction("Bank").first().get(), "Bank");
			}
				else{
					currstate = botState.ERROR_START;
					break;
				}
				break;
			case ERROR_START:
				EthanApiPlugin.sendClientMessage("Please have empty beer glasses and a pestle and mortar");
				EthanApiPlugin.stopPlugin(this);
				break;
			case HEADED_BACK_TO_SQUIRK:
				if(PathingTesting.pathing()&&EthanApiPlugin.isMoving()){
					return;
				}
				else {
					client.runScript(138);
					client.runScript(299, 1, 0, 0);

					if(NPCs.search().withAction("Teleport").first().isPresent()){
						NPCInteraction.interact(NPCs.search().withAction("Teleport").first().get(),"Teleport");
						currstate = botState.SQUIRKIN;
					}
					else{
						PathingTesting.walkTo(inHouse);
					}
				}
				break;
			case HEADED_BACK_TO_BANK:

				if(PathingTesting.pathing()&&EthanApiPlugin.isMoving()){
					return;
				}
				else {
					if(NPCs.search().withAction("Bank").first().isPresent()){
						//NPCInteraction.interact(NPCs.search().withAction("Teleport").first().get(),"Teleport");
						currstate = botState.OPEN_BANK;
					}
					else{
						PathingTesting.walkTo(bank);
					}
				}
				break;
			case BANKING:
				currstate = botState.OPEN_BANK;
				break;
		}

	}

	private void nextTimeout(){
		Random random = new Random();
		timeout = random.nextInt((config.ttloghigh()- config.ttloglow()) +1) + config.ttloglow();
	}

	private botState determineState(){
		if(Inventory.search().withId(ItemID.BEER_GLASS).result().size() < (25-config.stams()) || Inventory.search().withId(ItemID.PESTLE_AND_MORTAR).result().size() < 1 || Inventory.getEmptySlots() <2){
				return botState.OPEN_BANK;
		}
		else{
			return botState.SQUIRKIN;
		}
	}



	enum botState{
		OPEN_BANK,
		BANKING,
		HEADED_BACK_TO_SQUIRK,
		HEADED_BACK_TO_BANK,
		SQUIRKIN,
		ERROR_START
	}

}
