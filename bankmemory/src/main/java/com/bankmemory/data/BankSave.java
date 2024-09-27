package com.bankmemory.data;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import lombok.Value;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;

@Value
public class BankSave {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss, d MMM uuuu");
    private static final SaferUsernameFunction SAFER_USERNAME = new SaferUsernameFunction();
    private static final long ID_BASE = System.currentTimeMillis();
    private static final AtomicInteger idIncrementer = new AtomicInteger();

    private static final int NULL_ITEM_ID = -1;

    long id;
    BankWorldType worldType;
    String dateTimeString;
    @SerializedName(value = "accountIdentifier", alternate = {"userName"}) String accountIdentifier;
    @Nullable String saveName;
    ImmutableList<BankItem> itemData;

    @VisibleForTesting
    public BankSave(
            BankWorldType worldType,
            String accountIdentifier,
            @Nullable String saveName,
            String dateTimeString,
            ImmutableList<BankItem> itemData) {
        id = ID_BASE + idIncrementer.incrementAndGet();
        this.worldType = worldType;
        this.accountIdentifier = accountIdentifier;
        this.saveName = saveName;
        this.dateTimeString = dateTimeString;
        this.itemData = itemData;
    }

    // TODO: when I create a proper data upgrader/transitioner thing, remove this and put relevant logic there
    public BankWorldType getWorldType() {
        return worldType == null ? BankWorldType.DEFAULT : worldType;
    }

    public static BankSave fromCurrentBank(
            BankWorldType worldType,
            String accountIdentifier,
            ItemContainer bank,
            ItemManager itemManager) {
        Objects.requireNonNull(accountIdentifier);
        Objects.requireNonNull(bank);
        net.runelite.api.Item[] contents = bank.getItems();
        ImmutableList.Builder<BankItem> itemData = ImmutableList.builder();

        for (net.runelite.api.Item item : contents) {
            int idInBank = item.getId();
            int canonId = itemManager.canonicalize(idInBank);
            if (idInBank != canonId) {
                // It's just a placeholder
                continue;
            } else if (isItemToClean(idInBank)) {
                continue;
            }

            itemData.add(new BankItem(canonId, item.getQuantity()));
        }
        String timeString = DATE_FORMATTER.format(ZonedDateTime.now());
        return new BankSave(worldType, accountIdentifier, null, timeString, itemData.build());
    }

    private static boolean isItemToClean(int itemId) {
        return itemId == NULL_ITEM_ID || itemId == ItemID.BANK_FILLER;
    }

    public static BankSave snapshotFromExistingBank(String newName, BankSave existingBank) {
        Objects.requireNonNull(newName);
        return new BankSave(
                existingBank.worldType,
                existingBank.accountIdentifier,
                newName,
                existingBank.dateTimeString,
                existingBank.itemData);
    }

    public static BankSave cleanItemData(BankSave existingBank) {
        Objects.requireNonNull(existingBank);

        ImmutableList<BankItem> cleanItemData = existingBank.itemData.stream()
                .filter(i -> !isItemToClean(i.getItemId()))
                .collect(ImmutableList.toImmutableList());

        return new BankSave(
                existingBank.worldType,
                existingBank.accountIdentifier,
                existingBank.saveName,
                existingBank.dateTimeString,
                cleanItemData);
    }

    public static BankSave withNewAccountId(String newAccountId, BankSave existingBank) {
        Objects.requireNonNull(newAccountId);
        return new BankSave(
                existingBank.worldType,
                newAccountId,
                existingBank.saveName,
                existingBank.dateTimeString,
                existingBank.itemData);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("accountIdentifier", SAFER_USERNAME.from(accountIdentifier))
                .add("dateTimeString", dateTimeString)
                .add("saveName", saveName)
                .add("itemData", itemData)
                .toString();
    }
}
