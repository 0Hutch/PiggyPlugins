package com.bankmemory.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Slf4j
class ConfigReaderWriter {
    private static final String PLUGIN_BASE_GROUP = "bankMemory";
    private static final String CURRENT_LIST_KEY = "currentList";
    private static final String SNAPSHOT_LIST_KEY = "snapshotList";
    private static final String NAME_MAP_KEY = "nameMap";

    private final Gson gson;
    private final ConfigManager configManager;

    private final BlockingQueue<ConfigWrite> configWritesQueue = new LinkedBlockingQueue<>();

    @Inject
    ConfigReaderWriter(Gson gson, ConfigManager configManager) {
        Type itemDataListType = new TypeToken<ImmutableList<BankItem>>() {}.getType();
        this.gson = gson.newBuilder().registerTypeAdapter(itemDataListType, new ItemDataParser()).create();
        this.configManager = configManager;

        Thread configWriter = new Thread(new ConfigWriter(), "Bank Memory config writer");
        configWriter.setDaemon(true);
        configWriter.start();
    }

    List<BankSave> readCurrentBanks() {
        Type deserialiseType = new TypeToken<List<BankSave>>() {}.getType();
        List<BankSave> fromDataStore = loadDataFromConfig(CURRENT_LIST_KEY, deserialiseType, new ArrayList<>(), "Current bank list");
        return upgradeBankSaves(fromDataStore);
    }

    private List<BankSave> upgradeBankSaves(List<BankSave> bankSaves) {
        return bankSaves.stream().map(BankSave::cleanItemData).collect(Collectors.toList());
    }

    void writeCurrentBanks(List<BankSave> banks) {
        ConfigWrite configWrite = new ConfigWrite(PLUGIN_BASE_GROUP, CURRENT_LIST_KEY, new ArrayList<>(banks));
        scheduleConfigWrite(configWrite);
    }

    List<BankSave> readBankSnapshots() {
        Type deserialiseType = new TypeToken<List<BankSave>>() {}.getType();
        List<BankSave> fromDataStore = loadDataFromConfig(SNAPSHOT_LIST_KEY, deserialiseType, new ArrayList<>(), "Snapshot bank list");
        return upgradeBankSaves(fromDataStore);
    }

    void writeBankSnapshots(List<BankSave> banks) {
        ConfigWrite configWrite = new ConfigWrite(PLUGIN_BASE_GROUP, SNAPSHOT_LIST_KEY, new ArrayList<>(banks));
        scheduleConfigWrite(configWrite);
    }

    Map<String, String> readNameMap() {
        Type deserialiseType = new TypeToken<HashMap<String, String>>() {}.getType();
        return loadDataFromConfig(NAME_MAP_KEY, deserialiseType, new HashMap<>(), "Display name map");
    }

    void writeNameMap(Map<String, String> map) {
        ConfigWrite write = new ConfigWrite(PLUGIN_BASE_GROUP, NAME_MAP_KEY, new HashMap<>(map));
        scheduleConfigWrite(write);
    }

    private <T> T loadDataFromConfig(String configKey, Type deserialiseType, T defaultInstance, String dataName) {
        String jsonString = configManager.getConfiguration(PLUGIN_BASE_GROUP, configKey);
        if (jsonString == null) {
            // Never set before
            return defaultInstance;
        }

        try {
            T loadedData = gson.fromJson(jsonString, deserialiseType);
            return loadedData == null ? defaultInstance : loadedData;
        } catch (JsonParseException ex) {
            log.error("{} json invalid. All is lost", dataName, ex);
            configManager.unsetConfiguration(PLUGIN_BASE_GROUP, configKey);
            return defaultInstance;
        }
    }

    private void scheduleConfigWrite(ConfigWrite configWrite) {
        try {
            log.debug("Scheduling write for {}.{}", configWrite.configGroup, configWrite.configKey);
            configWritesQueue.put(configWrite);
        } catch (InterruptedException ex) {
            log.error("Unexpected interrupt whilst schedule config write. Data not being written", ex);
        }
    }

    @AllArgsConstructor
    private static class ConfigWrite {
        final String configGroup;
        final String configKey;
        final Object data;
    }

    // NB: technically there's a race condition because it's possible that the client could shut down before the call
    // to the ConfigManager is made. However, us trying to react to a client shutdown by writing all the remaining
    // writes won't work since the ConfigManager is the first thing to react to a client shutdown and won't perform any
    // new writes once it has.
    // Not very likely to happen anyway since it basically requires user to do something in game (to trigger plugin) and
    // then super quickly (like <10ms) close the client.
    private class ConfigWriter implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                ConfigWrite write;
                try {
                    write = configWritesQueue.take();
                    log.debug("Got write for {}.{}", write.configGroup, write.configKey);
                } catch (InterruptedException ex) {
                    log.warn("ConfigWriter thread interrupted", ex);
                    break;
                }
                configManager.setConfiguration(write.configGroup, write.configKey, gson.toJson(write.data));
            }
        }
    }
}
