package com.piggyplugins.collectionlogluck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Map;

@AllArgsConstructor
public class CollectionLogTab
{
    @Getter
    private final String name;

    @Getter
    private final Map<String, CollectionLogPage> pages;

    public boolean containsPage(String pageName)
    {
        return pages.containsKey(pageName);
    }
}
