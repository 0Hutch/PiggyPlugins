package com.masterkenth;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import com.masterkenth.discord.Author;
import com.masterkenth.discord.Embed;
import com.masterkenth.discord.Field;
import com.masterkenth.discord.Image;
import com.masterkenth.discord.Webhook;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.loottracker.LootReceived;
import net.runelite.client.ui.DrawManager;
import net.runelite.http.api.loottracker.LootRecordType;
import okhttp3.HttpUrl;
import org.json.JSONObject;

@Slf4j
@PluginDescriptor(
	name = "DisDropFree",
	description = "Sends a detailed notification via Discord webhooks whenever you get a rare/unique drop.",
	tags = {"discord", "loot", "unique", "boss", "notification"}
)
public class DiscordRareDropNotificaterPlugin extends Plugin
{
	private static final String PET_MESSAGE_DUPLICATE = "You have a funny feeling like you would have been followed";
	private static final ImmutableList<String> PET_MESSAGES = ImmutableList.of(
		"You have a funny feeling like you're being followed", "You feel something weird sneaking into your backpack",
		"You have a funny feeling like you would have been followed", PET_MESSAGE_DUPLICATE);

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private DiscordRareDropNotificaterConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private DrawManager drawManager;

	private final RarityChecker rarityChecker = new RarityChecker();

	private CompletableFuture<java.awt.Image> queuedScreenshot = null;

	@Provides
	DiscordRareDropNotificaterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DiscordRareDropNotificaterConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		JsonUtils.getInstance();
		super.startUp();
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived npcLootReceived)
	{
		if(isPlayerIgnored()) return;

		NPC npc = npcLootReceived.getNpc();
		Collection<ItemStack> items = npcLootReceived.getItems();

		List<CompletableFuture<Boolean>> futures = new ArrayList<CompletableFuture<Boolean>>();
		for (ItemStack item : items)
		{
			// Use a wrapper to 'capture' the lambda value
			// this way we only need to call the api once.
			CompletableFuture<ItemData>[] wrapper = new CompletableFuture[1];

			Supplier<CompletableFuture<ItemData>> itemDataSupplier = () -> {
				wrapper[0] = getNPCLootReceivedItemData(npc.getName(), item.getId(), item.getQuantity());
				return wrapper[0];
			};

			canBeSent(item.getId(), item.getQuantity(), itemDataSupplier).thenAccept(canSent -> {
				if (canSent)
				{
					if(wrapper[0] == null){
						// sets the value
						log.debug("We're setting the wrapper value");
						itemDataSupplier.get();
					}

					wrapper[0].thenAccept(itemData -> {
						futures.add(processNpcNotification(npc, item.getId(), item.getQuantity(), itemData.Rarity));
					});
				}
			});
		}

		if (futures.size() > 0)
		{
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
				.thenAccept(_v -> sendScreenshotIfSupposedTo());
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
			.thenAccept(_v -> sendScreenshotIfSupposedTo()).exceptionally(e ->
		{
			log.error(String.format("onNpcLootReceived error: %s", e.getMessage()), e);
			log.error(String.format("npc %d items %s", npcLootReceived.getNpc().getId(),
				npcLootReceived.getItems().stream().map(i -> "" + i.getId()).reduce("", (p, c) -> p + ", " + c)));
			return null;
		});
	}

	@Subscribe
	public void onLootReceived(LootReceived lootReceived)
	{
		if(isPlayerIgnored()) return;

		// Only process EVENTS such as Barrows, CoX etc. and PICKPOCKET
		// For NPCs onNpcLootReceived receives more information and is used instead.
		if (lootReceived.getType() == LootRecordType.NPC)
		{
			return;
		}

		Collection<ItemStack> items = lootReceived.getItems();
		List<CompletableFuture<Boolean>> futures = new ArrayList<>();

		for (ItemStack item : items)
		{
			canBeSent(item.getId(), item.getQuantity(), () -> getLootReceivedItemData(lootReceived.getName(), lootReceived.getType(), item.getId())).thenAccept(canSent -> {
				if(canSent){
					futures.add(processEventNotification(lootReceived.getType(), lootReceived.getName(), item.getId(), item.getQuantity()));
				}
			});
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
			.thenAccept(_v -> sendScreenshotIfSupposedTo()).exceptionally(e ->
		{
			log.error(String.format("onLootReceived error: %s", e.getMessage()), e);
			log.error(String.format("event %s items %s", lootReceived.getName(),
				lootReceived.getItems().stream().map(i -> "" + i.getId()).reduce("", (p, c) -> p + ", " + c)));
			return null;
		});

		if (futures.size() > 0)
		{
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
				.thenAccept(_v -> sendScreenshotIfSupposedTo());
		}
	}

	private CompletableFuture<ItemData> getLootReceivedItemData(String eventName, LootRecordType lootRecordType, int itemId){
		CompletableFuture<ItemData> result = new CompletableFuture<>();

		ItemData itemData = lootRecordType == LootRecordType.PICKPOCKET ?
			rarityChecker.CheckRarityPickpocket(eventName, EnrichItem(itemId), itemManager) :
			rarityChecker.CheckRarityEvent(eventName, EnrichItem(itemId), itemManager);

		result.complete(itemData);
		return result;
	}

	private CompletableFuture<ItemData> getNPCLootReceivedItemData(String npcName, int itemId, int quantity)
	{
		ItemData incomplete = EnrichItem(itemId);
		return rarityChecker.CheckRarityNPC(npcName, incomplete, itemManager, quantity);
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if(isPlayerIgnored()) return;

		String chatMessage = event.getMessage();

		if (event.getType() != ChatMessageType.GAMEMESSAGE
			&& event.getType() != ChatMessageType.SPAM
			&& event.getType() != ChatMessageType.TRADE
			&& event.getType() != ChatMessageType.FRIENDSCHATNOTIFICATION)
		{
			return;
		}

		if (PET_MESSAGES.stream().anyMatch(chatMessage::contains))
		{
			boolean isDuplicate = chatMessage.contains(PET_MESSAGE_DUPLICATE);
			log.info(String.format("Possible pet: duplicate=%b (%s, %s) %s", isDuplicate, event.getSender(), event.getName(),
				event.getMessage()));

			CompletableFuture<java.awt.Image> screenshotFuture = config.sendScreenshot() ? getScreenshot()
				: CompletableFuture.completedFuture(null);

			screenshotFuture
				// Waiting for screenshot before checking pet allows us to wait one frame, in
				// case pet data is not available yet
				// TODO: Figure out how to get pet info
				.thenApply(screenshot -> queuePetNotification(getPlayerName(), getPlayerIconUrl(), null, -1, isDuplicate)
					.thenCompose(_v -> screenshot != null ? sendScreenshot(getWebhookUrls(), screenshot)
						: CompletableFuture.completedFuture(null)))
				.exceptionally(e ->
				{
					log.error(String.format("onChatMessage (pet) error: %s", e.getMessage()), e);
					log.error(event.toString());
					return null;
				});
		}
	}

	private boolean isPlayerIgnored()
	{
		if(config.whiteListedRSNs().trim().length() > 0) {
			String playerName = getPlayerName().toLowerCase();
			List<String> whiteListedRSNs = Arrays.asList(config.whiteListedRSNs().split(","));

			return whiteListedRSNs.stream().noneMatch(rsn -> rsn.length() > 0 && playerName.equals(rsn.toLowerCase()));
		}

		return false;
	}

	private CompletableFuture<Boolean> canBeSent(int itemId, int quantity, Supplier<CompletableFuture<ItemData>> itemDataSupplier)
	{
		CompletableFuture<Boolean> result = new CompletableFuture<>();
		ItemComposition comp = itemManager.getItemComposition(itemId);
		String lowerName = comp.getName().toLowerCase();

		List<String> whitelist = Arrays.stream(config.whiteListedItems()
			.split(",")).filter(itemName -> itemName.length() > 0)
			.map(String::toLowerCase).collect(Collectors.toList());

		List<String> blacklist = Arrays.stream(config.ignoredKeywords()
			.split(",")).filter(itemName -> itemName.length() > 0)
			.map(String::toLowerCase).collect(Collectors.toList());

		if(log.isDebugEnabled())
		{
			log.debug(String.format("Checking if %s can be sent", lowerName));
		}

		if(whitelist.stream().anyMatch(lowerName::equals)){
			// It's an exact match with whitelist
			// This must be sent

			if(log.isDebugEnabled())
			{
				log.debug("We're whitelisted. We can be sent");
			}

			result.complete(true);
			return result;
		}

		if(blacklist.stream().anyMatch(lowerName::equals)){
			// Exact match with blacklist
			// must be ignored

			if(log.isDebugEnabled())
			{
				log.debug("We're blacklisted. We cannot be sent");
			}

			result.complete(false);
			return result;
		}

		if(whitelist.stream().anyMatch(lowerName::contains)){
			// Fuzzy whitelist
			// is accepted

			if(log.isDebugEnabled())
			{
				log.debug("We're fuzzy whitelisted. We can be sent");
			}

			result.complete(true);
			return result;
		}

		if(blacklist.stream().anyMatch(lowerName::contains)){
			// Fuzzy blacklist
			// is ignored
			if(log.isDebugEnabled())
			{
				log.debug("We're fuzzy blacklisted. We cannot be sent");
			}

			result.complete(false);
			return result;
		}

		if(log.isDebugEnabled())
		{
			log.debug("We're not in any item list. We need to continue our check.");
		}


		return itemDataSupplier.get().thenCompose(itemData -> {
			result.complete(meetsRequirements(itemData, quantity));
			return result;
		});
	}

	private CompletableFuture<Boolean> processEventNotification(LootRecordType lootRecordType, String eventName, int itemId, int quantity)
	{
		ItemData itemData = lootRecordType == LootRecordType.PICKPOCKET ? rarityChecker.CheckRarityPickpocket(eventName, EnrichItem(itemId), itemManager) : rarityChecker.CheckRarityEvent(eventName, EnrichItem(itemId), itemManager);

		queueScreenshot();
		clientThread.invokeLater(() -> {
			queueLootNotification(getPlayerName(), getPlayerIconUrl(), itemId, quantity, itemData.Rarity, -1, -1, null,
				eventName, config.webhookUrl()).thenApply(_v -> true);
		});

		return CompletableFuture.completedFuture(false);
	}

	private boolean meetsRequirements(ItemData item, int quantity)
	{
		if (item == null)
		{
			return false;
		}

		if (config.sendUniques() && item.Unique)
		{
			return true;
		}

		int totalGeValue = item.GePrice * quantity;
		int totalHaValue = item.HaPrice * quantity;

		boolean valueMet = totalGeValue >= config.minValue() || totalHaValue >= config.minValue();
		boolean rarityMet = item.Rarity <= (1f / config.minRarity());

		return config.andInsteadOfOr() ? (valueMet && rarityMet) : (valueMet || rarityMet);
	}

	private ItemData EnrichItem(int itemId)
	{
		ItemData r = new ItemData();
		r.ItemId = itemId;
		r.GePrice = itemManager.getItemPrice(itemId);
		r.HaPrice = itemManager.getItemComposition(itemId).getHaPrice();

		if(log.isDebugEnabled()){
			log.debug(MessageFormat.format("Item {0} prices HA{1}, GE{2}", itemId, r.HaPrice, r.GePrice));
		}

		return r;
	}

	private CompletableFuture<Boolean> processNpcNotification(NPC npc, int itemId, int quantity, float rarity)
	{
		int npcId = npc.getId();
		int npcCombatLevel = npc.getCombatLevel();
		String npcName = npc.getName();

		CompletableFuture<Boolean> f = new CompletableFuture<>();
		queueScreenshot();
		clientThread.invokeLater(() -> {
			queueLootNotification(getPlayerName(), getPlayerIconUrl(), itemId, quantity, rarity, npcId, npcCombatLevel,
				npcName, null, config.webhookUrl()).handle((_v, e) ->
			{
				if (e != null)
				{
					f.completeExceptionally(e);
				}
				else
				{
					f.complete(true);
				}
				return null;
			});
		});

		return f;
	}

	private void queueScreenshot()
	{
		if (queuedScreenshot == null && config.sendScreenshot())
		{
			queuedScreenshot = getScreenshot();
		}
	}

	private void sendScreenshotIfSupposedTo()
	{
		if (queuedScreenshot != null && config.sendScreenshot())
		{
			CompletableFuture<java.awt.Image> copy = queuedScreenshot;
			queuedScreenshot = null;
			copy.thenAccept(screenshot -> sendScreenshot(getWebhookUrls(), screenshot)).handle((v, e) ->
			{
				if (e != null)
				{
					log.error(String.format("sendScreenshotIfSupposedTo error: %s", e.getMessage()), e);
				}
				queuedScreenshot = null;
				return null;
			});
		}
	}

	private CompletableFuture<Void> queueLootNotification(String playerName, String playerIconUrl, int itemId,
														  int quantity, float rarity, int npcId, int npcCombatLevel, String npcName, String eventName, String webhookUrl)
	{
		Webhook webhookData = new Webhook();

		Author author = new Author();
		author.setName(playerName);

		if (playerIconUrl != null)
		{
			author.setIcon_url(playerIconUrl);
		}

		Embed embed = new Embed();
		embed.setAuthor(author);

		if(config.sendRarityAndValue()) {
			Field rarityField = new Field();
			rarityField.setName("Rare");
			rarityField.setValue(getRarityString(rarity));
			rarityField.setInline(true);

			Field haValueField = new Field();
			haValueField.setName("HA Valor");
			haValueField.setValue(getGPValueString(itemManager.getItemComposition(itemId).getHaPrice() * quantity));
			haValueField.setInline(true);

			Field geValueField = new Field();
			geValueField.setName("GE Valor");
			geValueField.setValue(getGPValueString(itemManager.getItemPrice(itemId) * quantity));
			geValueField.setInline(true);

			embed.setFields(new Field[]{rarityField, haValueField, geValueField});
		}

		Image thumbnail = new Image();
		String iconUrl = ApiTool.getInstance().getIconUrl(itemId);
		thumbnail.setUrl(iconUrl);
		embed.setThumbnail(thumbnail);

		CompletableFuture<Void> descFuture = getLootNotificationDescription(itemId, quantity, npcId, npcCombatLevel,
			npcName, eventName, !config.sendEmbeddedMessage()).handle((notifDesc, e) ->
		{
			if (e != null)
			{
				log.error(String.format("queueLootNotification (desc %d) error: %s", itemId, e.getMessage()), e);
			}
			embed.setDescription(notifDesc);
			if(!config.sendEmbeddedMessage()) webhookData.setContent("**" + playerName + "** - " + notifDesc);

			return null;
		});

		return CompletableFuture.allOf(descFuture).thenCompose(_v ->
		{
			if(config.sendEmbeddedMessage()) {
				webhookData.setEmbeds(new Embed[]{embed});
			}
			return sendWebhookData(getWebhookUrls(), webhookData);
		});
	}

	private CompletableFuture<Void> queuePetNotification(String playerName, String playerIconUrl, String petName,
														 int rarity, boolean isDuplicate)
	{
		Author author = new Author();
		author.setName(playerName);

		if (playerIconUrl != null)
		{
			author.setIcon_url(playerIconUrl);
		}

		/*
		 * Field rarityField = new Field(); rarityField.setName("Rarity");
		 * rarityField.setValue(getRarityString(rarity)); rarityField.setInline(true);
		 */

		Embed embed = new Embed();
		embed.setAuthor(author);
		embed.setFields(new Field[]{ /* rarityField */});
		embed.setDescription(getPetNotificationDescription(isDuplicate));

		/*
		 * Image thumbnail = new Image(); CompletableFuture<Void> iconFuture =
		 * ApiTool.getInstance().getIconUrl("pet", -1, petName).thenAccept(iconUrl -> {
		 * thumbnail.setUrl(iconUrl); embed.setThumbnail(thumbnail); });
		 */

		return CompletableFuture.allOf().thenCompose(_v ->
		{
			Webhook webhookData = new Webhook();
			webhookData.setEmbeds(new Embed[]{embed});
			return sendWebhookData(getWebhookUrls(), webhookData);
		});
	}

	private CompletableFuture<java.awt.Image> getScreenshot()
	{
		CompletableFuture<java.awt.Image> f = new CompletableFuture<>();
		drawManager.requestNextFrameListener(screenshotImage ->
		{
			f.complete(screenshotImage);
		});
		return f;
	}

	private CompletableFuture<Void> sendWebhookData(List<String> webhookUrls, Webhook webhookData)
	{
		JSONObject json = new JSONObject(webhookData);
		String jsonStr = json.toString();

		List<Throwable> exceptions = new ArrayList<>();
		List<CompletableFuture<Void>> sends = webhookUrls.stream()
			.map(url -> ApiTool.getInstance().postRaw(url, jsonStr, "application/json").handle((_v, e) ->
			{
				if (e != null)
				{
					exceptions.add(e);
				}
				return null;
			}).thenAccept(_v ->
			{
			})).collect(Collectors.toList());

		return CompletableFuture.allOf(sends.toArray(new CompletableFuture[sends.size()])).thenCompose(_v ->
		{
			if (exceptions.size() > 0)
			{
				log.error(String.format("sendWebhookData got %d error(s)", exceptions.size()));
				exceptions.forEach(t -> log.error(t.getMessage()));
				CompletableFuture<Void> f = new CompletableFuture<>();
				f.completeExceptionally(exceptions.get(0));
				return f;
			}
			return CompletableFuture.completedFuture(null);
		});
	}

	private CompletableFuture<Void> sendScreenshot(List<String> webhookUrls, java.awt.Image screenshot)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write((BufferedImage) screenshot, "png", baos);
			byte[] imageBytes = baos.toByteArray();

			List<Throwable> exceptions = new ArrayList<>();
			List<CompletableFuture<Void>> sends = webhookUrls.stream()
				.map(url -> ApiTool.getInstance().postFormImage(url, imageBytes, "image/png").handle((_v, e) ->
				{
					if (e != null)
					{
						exceptions.add(e);
					}
					return null;
				}).thenAccept(_v ->
				{
				})).collect(Collectors.toList());

			return CompletableFuture.allOf(sends.toArray(new CompletableFuture[sends.size()])).thenCompose(_v ->
			{
				if (exceptions.size() > 0)
				{
					log.error(String.format("sendScreenshot got %d error(s)", exceptions.size()));
					exceptions.forEach(t -> log.error(t.getMessage()));
					CompletableFuture<Void> f = new CompletableFuture<>();
					f.completeExceptionally(exceptions.get(0));
					return f;
				}
				return CompletableFuture.completedFuture(null);
			});
		}
		catch (Exception e)
		{
			log.error("Unable to send screenshot", e);
			return CompletableFuture.completedFuture(null);
		}
	}

	// TODO: Add Pet notification
	private CompletableFuture<String> getLootNotificationDescription(int itemId, int quantity, int npcId,
																	 int npcCombatLevel, String npcName, String eventName, boolean plainText)
	{
		ItemComposition itemComp = itemManager.getItemComposition(itemId);
		String itemUrl = getWikiUrl(itemComp.getName());
		String baseMsg = (plainText) ?
			"Acabo de **" + (quantity > 1 ? quantity + "x " : "") + itemComp.getName() + "**" :
			"Acabo de " + (quantity > 1 ? quantity + "x " : "") + "[" + itemComp.getName() + "](" + itemUrl + ")";

		if (npcId >= 0)
		{
			String npcUrl = getWikiUrl(npcName);
			String fullMsg = (plainText) ?
				baseMsg + " de lvl " + npcCombatLevel + " **" + npcName + "**" :
				baseMsg + " de lvl " + npcCombatLevel + " [" + npcName + "](" + npcUrl + ")";

			return CompletableFuture.completedFuture(fullMsg);
		}
		else if (eventName != null)
		{
			String eventUrl = getWikiUrl(eventName);
			String fullMsg = (plainText) ?
				baseMsg + " de **" + eventName + "**" :
				baseMsg + " de [" + eventName + "](" + eventUrl + ")";
			return CompletableFuture.completedFuture(fullMsg);
		}
		else
		{
			return CompletableFuture.completedFuture(baseMsg + " de algo");
		}
	}

	private String getWikiUrl(String search){
		return HttpUrl.parse("https://oldschool.runescape.wiki/").newBuilder()
			.addPathSegments("w/Special:Search").addQueryParameter("search", search).build().toString();
	}

	private String getPetNotificationDescription(boolean isDuplicate)
	{
		if (isDuplicate)
		{
			return "Would've gotten a pet, but already has it.";
		}
		else
		{
			return "Acabo de tener una Pet";
		}
	}

	private String getGPValueString(int value)
	{
		return "```fix\n" + NumberFormat.getNumberInstance(Locale.US).format(value) + " GP\n```";
	}

	private String getRarityString(float rarity)
	{
		return "```glsl\n# 1/" + (1 / rarity) + " (" + (rarity * 100f) + "%)\n```";
	}

	private String getPlayerIconUrl()
	{
		switch (client.getAccountType())
		{
			case IRONMAN:
				return "https://oldschool.runescape.wiki/images/0/09/Ironman_chat_badge.png";
			case HARDCORE_IRONMAN:
				return "https://oldschool.runescape.wiki/images/b/b8/Hardcore_ironman_chat_badge.png";
			case ULTIMATE_IRONMAN:
				return "https://oldschool.runescape.wiki/images/0/02/Ultimate_ironman_chat_badge.png";
			case GROUP_IRONMAN:
				return "https://oldschool.runescape.wiki/images/Group_ironman_chat_badge.png";
			case HARDCORE_GROUP_IRONMAN:
				return "https://oldschool.runescape.wiki/images/Hardcore_group_ironman_chat_badge.png";
			default:
				return null;
		}
	}
	
	private String getPlayerName()
	{
		return client.getLocalPlayer().getName();
	}

	private List<String> getWebhookUrls()
	{
		return Arrays.asList(config.webhookUrl().split("[\\n,]")).stream().filter(u -> u.length() > 0).map(u -> u.trim())
			.collect(Collectors.toList());
	}
}
