package com.bankmemory.data;

import javax.annotation.Nullable;

public class AccountIdentifier {

    public static final String ACCOUNT_HASH_ID_PREFIX = "accId#hash1#";

    private AccountIdentifier() {}

    @Nullable
    public static String fromAccountHash(long accountHash) {
        if (accountHash == -1) {
            return null;
        }
        return ACCOUNT_HASH_ID_PREFIX + accountHash;
    }
}
