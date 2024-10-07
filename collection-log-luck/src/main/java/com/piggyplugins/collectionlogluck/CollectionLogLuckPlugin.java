package com.piggyplugins.collectionlogluck;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.inject.Provides;
import com.piggyplugins.collectionlogluck.luck.CollectionLogItemAliases;
import com.piggyplugins.collectionlogluck.luck.LogItemInfo;
import com.piggyplugins.collectionlogluck.luck.LuckCalculationResult;
import com.piggyplugins.collectionlogluck.luck.drop.AbstractDrop;
import com.piggyplugins.collectionlogluck.luck.drop.DropLuck;
import com.piggyplugins.collectionlogluck.model.CollectionLog;
import com.piggyplugins.collectionlogluck.model.CollectionLogItem;
import com.piggyplugins.collectionlogluck.model.CollectionLogPage;
import com.piggyplugins.collectionlogluck.util.CollectionLogDeserializer;
import com.piggyplugins.collectionlogluck.util.CollectionLogLuckApiClient;
import com.piggyplugins.collectionlogluck.util.JsonUtils;
import com.piggyplugins.collectionlogluck.util.LuckUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
        name = "Collection Log Luck",
        description = "Calculates and displays luck for collection log items.",
        tags = {"collection", "log", "luck"}
)
public class CollectionLogLuckPlugin extends Plugin {

    private static final Pattern COLLECTION_LOG_LUCK_CHECK_REGEX = Pattern.compile("^You have received (.*) x (.*)\\.$");
    private static final String COLLECTION_LOG_LUCK_COMMAND_STRING = "!luck";
    private static final Pattern COLLECTION_LOG_LUCK_COMMAND_PATTERN = Pattern.compile("!luck\\s*(.+)\\s*", Pattern.CASE_INSENSITIVE);
    private static final String COLLECTION_LOG_LUCK_CONFIG_GROUP = "collectionlogluck";

    private final String pluginVersion = "v1.1.0";
    private final String pluginMessage = "<colHIGHLIGHT>Collection Log Luck " + pluginVersion + ":<br>" +
            "<colHIGHLIGHT>* Fixed shared clue items. Visual tweaks.<br>" +
            "<colHIGHLIGHT>* Support Tormented Demon drops and Nightmare buff settings<br>" +
            "<colHIGHLIGHT>* Support abyssal bludgeon, Moons of Peril, Wintertodt<br>" +
            "<colHIGHLIGHT>* This update message can be hidden in settings";

    private Map<Integer, Integer> loadedCollectionLogIcons;

    // caches collection log per username. Cleared on logout (including hopping worlds).
    // Returns a CompletableFuture to help track in-progress collection log requests
    private Map<String, CompletableFuture<CollectionLog>> loadedCollectionLogs;

    // caches luck calculations per username+luckCalculationID. Cleared on logout (including hopping worlds).
    private Map<String, LuckCalculationResult> luckCalculationResults;

    @Getter
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ChatCommandManager chatCommandManager;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private CollectionLogLuckConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private CollectionLogLuckApiClient apiClient;

    @Inject
    private JsonUtils jsonUtils;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    private CollectionLogWidgetItemOverlay collectionLogWidgetItemOverlay;

    @Provides
    CollectionLogLuckConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CollectionLogLuckConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(collectionLogWidgetItemOverlay);

        loadedCollectionLogIcons = new HashMap<>();
        loadedCollectionLogs = new HashMap<>();
        luckCalculationResults = new HashMap<>();

        chatCommandManager.registerCommandAsync(COLLECTION_LOG_LUCK_COMMAND_STRING, this::processLuckCommandMessage);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(collectionLogWidgetItemOverlay);

        loadedCollectionLogIcons.clear();
        loadedCollectionLogs.clear();
        luckCalculationResults.clear();

        chatCommandManager.unregisterCommand(COLLECTION_LOG_LUCK_COMMAND_STRING);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (!isValidWorldType()) {
            loadedCollectionLogs.clear();
            luckCalculationResults.clear();
            return;
        }

        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN ||
                gameStateChanged.getGameState() == GameState.HOPPING) {
            loadedCollectionLogs.clear();
            luckCalculationResults.clear();
        }

        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) return;

        // Send message about plugin updates one time
        if (!config.getVersion().equals(pluginVersion)) {
            configManager.setConfiguration(
                    CollectionLogLuckConfig.COLLECTION_LOG_LUCK_CONFIG_GROUP,
                    CollectionLogLuckConfig.COLLECTION_LOG_LUCK_CONFIG_VERSION_KEY,
                    pluginVersion);
            if (config.showPluginUpdates()) {
                chatMessageManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(pluginMessage)
                        .build()
                );
            }
        }

    }

    private boolean isValidWorldType() {
        List<WorldType> invalidTypes = ImmutableList.of(
                WorldType.DEADMAN,
                WorldType.NOSAVE_MODE,
                WorldType.SEASONAL,
                WorldType.TOURNAMENT_WORLD
        );

        for (WorldType worldType : invalidTypes) {
            if (client.getWorldType().contains(worldType)) {
                return false;
            }
        }

        return true;
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        Matcher checkLuckMatcher = COLLECTION_LOG_LUCK_CHECK_REGEX.matcher(chatMessage.getMessage());
        if (checkLuckMatcher.matches()) {
            processCheckItemMessage(checkLuckMatcher);
        }
    }

    /**
     * Display luck-related information when the player "check"s an item in the collection log.
     *
     * @param checkLuckMatcher the matcher containing command info
     */
    private void processCheckItemMessage(Matcher checkLuckMatcher) {
        // Note: this assumes this function is called for the local player
        if (config.hidePersonalLuckCalculation()) {
            return;
        }
        if (checkLuckMatcher.groupCount() < 2) {
            // Matcher didn't find 2 groups for some reason
            return;
        }

        // For now, assume that the "check item" message is for the local player. Some day, this could support the
        // "check item" functionality through another player's house Adventure Log
        String username = client.getLocalPlayer().getName();

        fetchCollectionLog(username, true, collectionLog -> {
            // fetching may be async, but we need to be back on client thread to add chat message.
            clientThread.invoke(() -> {
                String message = buildLuckCommandMessage(collectionLog, checkLuckMatcher.group(2), false);

                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
            });
        });
    }

    private String getChatMessageSenderUsername(ChatMessage chatMessage) {
        if (chatMessage.getType().equals(ChatMessageType.PRIVATECHATOUT)) {
            String username = client.getLocalPlayer().getName();
            if (username != null) {
                return Text.sanitize(username);
            }
            return "";
        }
        return Text.sanitize(chatMessage.getName());
    }

    /**
     * After a "!luck" chat message, fetches collection log for the chatting user and then replaces the message
     *
     * @param chatMessage The ChatMessage event
     * @param message     Text of the message
     */
    private void processLuckCommandMessage(ChatMessage chatMessage, String message) {
        String username = getChatMessageSenderUsername(chatMessage);

        fetchCollectionLog(username, true, collectionLog -> {
            // fetching may be async, but we need to be back on client thread to modify chat message.
            clientThread.invoke(() -> {
                replaceCommandMessage(chatMessage, message, collectionLog);
            });
        });
    }

    // Fetch the collection log for this username, then call the callback. If allowAsync is set to false,
    // the function will call the callback immediately with a null collection log, but it will still request a
    // new collection log if an equivalent request is not already in progress.
    protected void fetchCollectionLog(String rawUsername, boolean allowAsync, Consumer<CollectionLog> callback) {
        final String sanitizedUsername = Text.sanitize(rawUsername);

        try {
            // Only fetch collection log if necessary
            if (!loadedCollectionLogs.containsKey(sanitizedUsername)) {
                CompletableFuture<CollectionLog> collectionLogFuture = new CompletableFuture<>();
                loadedCollectionLogs.put(sanitizedUsername, collectionLogFuture);

                apiClient.getCollectionLog(sanitizedUsername, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        log.error("Unable to retrieve collection log: " + e.getMessage());
                        collectionLogFuture.complete(null);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        JsonObject collectionLogJson = apiClient.processResponse(response);
                        response.close();

                        if (collectionLogJson == null) {
                            collectionLogFuture.complete(null);
                            return;
                        }

                        CollectionLog collectionLog = jsonUtils.fromJsonObject(
                                collectionLogJson.getAsJsonObject("collectionLog"),
                                CollectionLog.class,
                                new CollectionLogDeserializer()
                        );
                        collectionLogFuture.complete(collectionLog);
                    }
                });
            }

            CompletableFuture<CollectionLog> collectionLogFuture = loadedCollectionLogs.get(sanitizedUsername);
            if (allowAsync) {
                callback.accept(collectionLogFuture.get());
            } else {
                // Return the value if present, otherwise return null
                callback.accept(collectionLogFuture.getNow(null));
            }
        } catch (IOException | ExecutionException | CancellationException | InterruptedException e) {
            log.error("Unable to retrieve collection log: " + e.getMessage());
            callback.accept(null);
        }
    }

    // Calculate luck for this item, caching results
    protected LuckCalculationResult fetchLuckCalculationResult(DropLuck dropLuck,
                                                               CollectionLogItem item,
                                                               CollectionLog collectionLog,
                                                               CollectionLogLuckConfig calculationConfig) {
        String username = Text.sanitize(collectionLog.getUsername());
        String calculationId = username + "|" + item.getId();

        // Only calculate if necessary
        if (!luckCalculationResults.containsKey(calculationId)) {
            double luck = dropLuck.calculateLuck(item, collectionLog, calculationConfig);
            double dryness = dropLuck.calculateDryness(item, collectionLog, calculationConfig);

            luckCalculationResults.put(calculationId, new LuckCalculationResult(luck, dryness));
        }

        return luckCalculationResults.get(calculationId);
    }

    private void replaceCommandMessage(ChatMessage chatMessage, String message, CollectionLog collectionLog) {
        Matcher commandMatcher = COLLECTION_LOG_LUCK_COMMAND_PATTERN.matcher(message);
        if (!commandMatcher.matches()) {
            return;
        }

        String replacementMessage;
        if (collectionLog == null) {
            String username = getChatMessageSenderUsername(chatMessage);
            replacementMessage = "Collection Log not found for " + username
                    + ". Make sure to upload to collectionlog.net using the Collection Log plugin.";
        } else {
            String commandTarget = commandMatcher.group(1);
            replacementMessage = buildLuckCommandMessage(collectionLog, commandTarget, true);
        }

        chatMessage.getMessageNode().setValue(replacementMessage);
        client.runScript(ScriptID.BUILD_CHATBOX);
    }

    /**
     * Loads a list of Collection Log items into the client's mod icons.
     *
     * @param collectionLogItems List of items to load
     */
    private void loadItemIcons(List<CollectionLogItem> collectionLogItems) {
        List<CollectionLogItem> itemsToLoad = collectionLogItems
                .stream()
                .filter(item -> !loadedCollectionLogIcons.containsKey(item.getId()))
                .collect(Collectors.toList());

        final IndexedSprite[] modIcons = client.getModIcons();

        final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + itemsToLoad.size());
        int modIconIdx = modIcons.length;

        for (int i = 0; i < itemsToLoad.size(); i++) {
            final CollectionLogItem item = itemsToLoad.get(i);
            final ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
            final BufferedImage image = ImageUtil.resizeImage(itemManager.getImage(itemComposition.getId()), 18, 16);
            final IndexedSprite sprite = ImageUtil.getImageIndexedSprite(image, client);
            final int spriteIndex = modIconIdx + i;

            newModIcons[spriteIndex] = sprite;
            loadedCollectionLogIcons.put(item.getId(), spriteIndex);
        }

        client.setModIcons(newModIcons);
    }

    /**
     * Convert to actual item name rather than "display" name (e.g. remove " (Members)" suffixes)
     * It may be possible to simply remove the suffix directly, but I haven't checked that it works for every item.
     * For example, there may be items whose display name differs from its "real" name in a way that isn't simply
     * adding " (Members)"
     *
     * @param itemDisplayName An item's display name which
     * @return The item's true name regardless of membership status
     */
    private String itemDisplayNameToItemName(String itemDisplayName) {
        for (int i = 0; i < client.getItemCount(); i++) {
            ItemComposition itemComposition = client.getItemDefinition(i);
            if (itemComposition.getName().equalsIgnoreCase(itemDisplayName)) {
                return itemComposition.getMembersName();
            }
        }
        return itemDisplayName;
    }

    /**
     * Builds the replacement messages for the !luck... command
     *
     * @param collectionLog The collection log to use for the luck calculation (which may be another player's)
     * @param commandTarget The item or page for which to calculate luck. If omitted, calculates account-level luck
     * @param useFuzzyMatch For sources that could be misspelled, e.g. user input, use a fuzzy match algorithm to
     *                      guess the intended item target
     * @return Replacement message
     */
    private String buildLuckCommandMessage(CollectionLog collectionLog, String commandTarget, boolean useFuzzyMatch) {
        boolean collectionLogIsLocalPlayer =
                client.getLocalPlayer().getName().equalsIgnoreCase(collectionLog.getUsername());

        if (collectionLogIsLocalPlayer && config.hidePersonalLuckCalculation()) {
            // This should make it obvious that 1) The player can go to the config to change this setting, and 2) other
            // players can still see their luck if they type in a !log luck command.
            return "Collection Log Luck plugin: Your luck is set to be hidden from you in the plugin config.";
        }
        // !luck [account|total|overall]
        if (commandTarget == null
                || commandTarget.equalsIgnoreCase("account")
                || commandTarget.equalsIgnoreCase("total")
                || commandTarget.equalsIgnoreCase("overall")) {
            return "Collection Log Luck plugin: Account-level luck calculation is not yet supported.";
        }

        // !luck <page-name>
        String pageName = CollectionLogPage.aliasPageName(commandTarget);
        if (collectionLog.searchForPage(pageName) != null) {
            return "Collection Log Luck plugin: Per-activity or per-page luck calculation is not yet supported.";
        }

        // !luck <item-name>
        String itemName = itemDisplayNameToItemName(commandTarget);
        if (useFuzzyMatch) {
            itemName = CollectionLogItemAliases.aliasItemName(itemName);
        }

        CollectionLogItem item = collectionLog.searchForItem(itemName);
        if (item == null) {
            return "Collection Log Luck plugin: Item " + itemName + " is not recognized.";
        }
        int numObtained = item.getQuantity();

        LogItemInfo logItemInfo = LogItemInfo.findByName(itemName);
        if (logItemInfo == null) {
            // This likely only happens if there is an update and the plugin does not yet support new items.
            return "Collection Log Luck plugin: Item " + itemName + " is not yet supported for luck calculation.";
        }

        String warningText = "";

        CollectionLogLuckConfig relevantConfig = config;
        if (!collectionLogIsLocalPlayer) {
            relevantConfig = null;
        }

        // all other unimplemented or unsupported drops take this path
        String failReason = logItemInfo.getDropProbabilityDistribution().getIncalculableReason(item, relevantConfig);
        if (failReason != null) {
            if (failReason.equals(AbstractDrop.INCALCULABLE_MISSING_CONFIG)) {
                // drops from other players will use YOUR config, which can lead to very inaccurate luck calculation!!
                // proceed with calculation but warn about likely inaccuracy
                warningText = " - Warning: Calculation uses YOUR config settings. May be inaccurate.";
            } else {
                return failReason;
            }
        }

        // make sure this item's icon is loaded
        loadItemIcons(ImmutableList.of(item));

        // calculate using player's config, even if the calculation is for another player
        LuckCalculationResult luckCalculationResult = fetchLuckCalculationResult(
                logItemInfo.getDropProbabilityDistribution(),
                item,
                collectionLog,
                config);

        double luck = luckCalculationResult.getLuck();
        double dryness = luckCalculationResult.getDryness();
        if (luck < 0 || luck > 1 || dryness < 0 || dryness > 1) {
            return "Collection Log Luck plugin: Unknown error calculating luck for item.";
        }

        int luckPercentile = (int) Math.round(luckCalculationResult.getOverallLuck() * 100);

        StringBuilder shownLuckText = new StringBuilder()
                .append("(");

        if (config.replacePercentileWithDrycalcNumber()) {
            shownLuckText.append(LuckUtils.formatLuckSigDigits(1 - dryness))
                    .append("% <lt>= your luck");
        } else {
            shownLuckText.append(luckPercentile)
                    .append(LuckUtils.getOrdinalSuffix(luckPercentile))
                    .append(" percentile");
        }

        shownLuckText.append(" | ")
                .append(LuckUtils.formatLuckSigDigits(dryness))
                .append("% luckier than you");

        // Only show luck if you've received an item - otherwise, luck is always just 0.
        if (numObtained > 0 || luck > 0) {
            shownLuckText
                    .append(" | ")
                    .append(LuckUtils.formatLuckSigDigits(luck))
                    .append("% drier than you");
        }
        shownLuckText.append(")");

        String kcDescription = logItemInfo.getDropProbabilityDistribution().getKillCountDescription(collectionLog);

        // rarer than 1 in 100M is likely an error. Note: 0 luck or 0 dryness is normal as a result of low KC and does
        // not need a warning.
        if (luck > 0.99999999 || dryness > 0.99999999) {
            // previous warnings supersede this warning
            if (warningText.isEmpty()) {
                warningText = " - Warning: Check plugin configuration. Did you have many KC" +
                        " before the log existed, or have you reached the max # tracked for this item?";
            }
        }

        return new ChatMessageBuilder()
                .append(item.getName() + " ")
                .img(loadedCollectionLogIcons.get(item.getId()))
                .append("x" + numObtained + ": ")
                .append(luckCalculationResult.getLuckColor(), shownLuckText.toString())
                .append(" in ")
                .append(kcDescription)
                .append(warningText)
                .build();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals(COLLECTION_LOG_LUCK_CONFIG_GROUP)) {
            List<String> nonCacheClearingConfigSettings = ImmutableList.of(
                    config.HIDE_PERSONAL_LUCK_CALCULATION_KEY,
                    config.SHOW_LUCK_TEXT_ON_COLLECTION_LOG_KEY,
                    config.SHOW_LUCK_BACKGROUND_ON_COLLECTION_LOG_KEY,
                    config.REPLACE_PERCENTILE_WITH_DRYCALC_NUMBER_KEY
            );

            // Skip clearing calculation cache if the modified setting could not possibly affect calculation results
            if (nonCacheClearingConfigSettings.contains(event.getKey())) {
                return;
            }

            luckCalculationResults.clear();
        }
    }

}
