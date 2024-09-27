package com.bankmemory.data;

import com.google.common.base.Strings;

/**
 * Obscures the login name so other people don't know what it is (e.g. if the user has to share their logs, or if
 * they're a streamer), whilst leaving the first 3 characters (or less) visible so hopefully the user can still
 * understand things.
 */
class SaferUsernameFunction {
    String from(String username) {
        int start = Math.min(3, username.length() / 3);
        return username.substring(0, start) + Strings.repeat("-", username.length() - start);
    }
}
