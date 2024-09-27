package com.bankmemory.data;

import java.util.Map;
import java.util.TreeMap;

public class DisplayNameMapper {
    private static final SaferUsernameFunction SAFER_USERNAME = new SaferUsernameFunction();

    private final TreeMap<String, String> displayNamesByLogin = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    DisplayNameMapper(Map<String, String> map) {
        displayNamesByLogin.putAll(map);
    }

    public String map(String accountIdentifier) {
        String displayName = displayNamesByLogin.get(accountIdentifier);
        if (displayName != null) {
            return displayName;
        }
        return SAFER_USERNAME.from(accountIdentifier);
    }
}
