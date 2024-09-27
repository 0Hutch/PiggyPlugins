package com.bankmemory.data;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class ItemDataParser implements JsonSerializer<ImmutableList<BankItem>>, JsonDeserializer<ImmutableList<BankItem>> {

    private static final String PARSE_EXCEPTION_MESSAGE = "Item data section format invalid";

    @Override
    public JsonElement serialize(ImmutableList<BankItem> src, Type typeOfSrc, JsonSerializationContext context) {
        String saveString = toSaveString(src);
        return context.serialize(saveString);
    }

    String toSaveString(List<BankItem> items) {
        StringBuilder sb = new StringBuilder();
        for (BankItem item : items) {
            sb.append(item.getItemId()).append(",").append(item.getQuantity()).append(",");
        }
        return sb.toString();
    }

    @Override
    public ImmutableList<BankItem> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String saveString = json.getAsString();
        return parseSaveString(saveString);
    }

    ImmutableList<BankItem> parseSaveString(String saveString) throws JsonParseException {
        if (Strings.isNullOrEmpty(saveString)) {
            return ImmutableList.of();
        }

        List<Integer> numbers = new ArrayList<>();
        for (String num : saveString.split(",")) {
            try {
                numbers.add(Integer.parseInt(num));
            } catch (NumberFormatException ex) {
                throw new JsonParseException(PARSE_EXCEPTION_MESSAGE, ex);
            }
        }

        if (!(numbers.size() % 2 == 0)) {
            throw new JsonParseException(PARSE_EXCEPTION_MESSAGE);
        }
        ImmutableList.Builder<BankItem> items = ImmutableList.builder();
        for (int i = 0; i < numbers.size(); i += 2) {
            items.add(new BankItem(numbers.get(i), numbers.get(i + 1)));
        }
        return items.build();
    }
}
