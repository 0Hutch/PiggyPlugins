package com.piggyplugins.collectionlogluck.util;

import com.google.gson.*;
import com.piggyplugins.collectionlogluck.model.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionLogDeserializer implements JsonDeserializer<CollectionLog> {
    private static final String COLLECTION_LOG_ITEMS_KEY = "items";
    private static final String COLLECTION_LOG_KILL_COUNTS_KEY = "killCounts";
    // The data returned by collectionlog.net has "killCount" instead of "killCounts", and also has "items.obtainedAt".
    private static final String COLLECTION_LOG_WEBSITE_KILL_COUNTS_KEY = "killCount";
    private static final String COLLECTION_LOG_TABS_KEY = "tabs";
    private static final String COLLECTION_LOG_USERNAME_KEY = "username";
    private static final String COLLECTION_LOG_TOTAL_OBTAINED_KEY = "totalObtained";
    private static final String COLLECTION_LOG_TOTAL_ITEMS_KEY = "totalItems";
    private static final String COLLECTION_LOG_UNIQUE_OBTAINED_KEY = "uniqueObtained";
    private static final String COLLECTION_LOG_UNIQUE_ITEMS_KEY = "uniqueItems";
    private static final String COLLECTION_LOG_IS_UPDATED_KEY = "isUpdated";

    @Override
    public CollectionLog deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObjectLog = jsonElement.getAsJsonObject();

        JsonObject jsonObjectTabs = jsonObjectLog.get(COLLECTION_LOG_TABS_KEY).getAsJsonObject();

        Map<String, CollectionLogTab> newTabs = new HashMap<>();
        for (String tabKey : jsonObjectTabs.keySet()) {
            JsonObject tab = jsonObjectTabs.get(tabKey).getAsJsonObject();
            Map<String, CollectionLogPage> newPages = new HashMap<>();

            for (String pageKey : tab.keySet()) {
                JsonObject page = tab.get(pageKey).getAsJsonObject();
                List<CollectionLogItem> newItems = new ArrayList<>();

                for (JsonElement item : page.get(COLLECTION_LOG_ITEMS_KEY).getAsJsonArray()) {
                    CollectionLogItem newItem = context.deserialize(item, CollectionLogItem.class);

                    // Uncomment to update LogItemInfo list
                    // Example: (Farmer's shirt,13643)
//                    LogItemInfo logItemInfo = LogItemInfo.findByName(newItem.getName());
//                    if (logItemInfo == null) {
//                        System.out.println("New collection log item detected!:(" + newItem.getName() + "," + newItem.getId() + ")");
//                    }

                    newItems.add(newItem);
                }

                List<CollectionLogKillCount> newKillCounts = new ArrayList<>();
                JsonElement pageKillCounts = page.get(COLLECTION_LOG_KILL_COUNTS_KEY);
                if (pageKillCounts == null) {
                    // killCounts might be null because collectionlog.net returned "killCount" instead.
                    pageKillCounts = page.get(COLLECTION_LOG_WEBSITE_KILL_COUNTS_KEY);
                }
                if (pageKillCounts != null) {
                    for (JsonElement killCount : pageKillCounts.getAsJsonArray()) {
                        CollectionLogKillCount newKillCount;
                        newKillCount = context.deserialize(killCount, CollectionLogKillCount.class);
                        newKillCounts.add(newKillCount);

                        // Uncomment to update LogItemSourceInfo list
//                        LogItemSourceInfo logItemSourceInfo = LogItemSourceInfo.findByName(newKillCount.getName());
//                        if (logItemSourceInfo == null) {
//                            System.out.println("New collection log page detected!: (" + newKillCount.getName() + ")");
//                        }
                    }
                }

                boolean isUpdated = page.get(COLLECTION_LOG_IS_UPDATED_KEY) != null
                        && page.get(COLLECTION_LOG_IS_UPDATED_KEY).getAsBoolean();

                CollectionLogPage newPage = new CollectionLogPage(pageKey, newItems, newKillCounts, isUpdated);
                newPages.put(pageKey, newPage);
            }
            CollectionLogTab newTab = new CollectionLogTab(tabKey, newPages);
            newTabs.put(tabKey, newTab);
        }
        return new CollectionLog(
                jsonObjectLog.get(COLLECTION_LOG_USERNAME_KEY).getAsString(),
                jsonObjectLog.get(COLLECTION_LOG_TOTAL_OBTAINED_KEY).getAsInt(),
                jsonObjectLog.get(COLLECTION_LOG_TOTAL_ITEMS_KEY).getAsInt(),
                jsonObjectLog.get(COLLECTION_LOG_UNIQUE_OBTAINED_KEY).getAsInt(),
                jsonObjectLog.get(COLLECTION_LOG_UNIQUE_ITEMS_KEY).getAsInt(),
                newTabs
        );
    }
}
