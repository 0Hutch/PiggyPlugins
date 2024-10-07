package com.piggyplugins.collectionlogluck.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CollectionLog
{
    private final String username;

    @Setter
    private int totalObtained;

    @Setter
    private int totalItems;

    @Setter
    private int uniqueObtained;

    @Setter
    private int uniqueItems;

    private final Map<String, CollectionLogTab> tabs;

    public CollectionLogPage searchForPage(String pageName)
    {
        if (StringUtils.isEmpty(pageName))
        {
            return null;
        }

        for (CollectionLogTab tab : tabs.values())
        {
            for (CollectionLogPage page : tab.getPages().values())
            {
                if (pageName.equalsIgnoreCase(page.getName()))
                {
                    return page;
                }
            }
        }
        return null;
    }

    // TODO: initialize map in constructor if this is taking too long.
    public CollectionLogItem searchForItem(String itemName) {
        if (StringUtils.isEmpty(itemName)) {
            return null;
        }

        for (CollectionLogTab tab : tabs.values()) {
            for (CollectionLogPage page : tab.getPages().values()) {
                for (CollectionLogItem item : page.getItems()) {
                    if (itemName.equalsIgnoreCase(item.getName())) {
                        // Because the total number of items dropped from any page is reported on every such page,
                        // just return the item the first time it is seen on any page
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public CollectionLogKillCount searchForKillCount(String killCountName) {
        if (StringUtils.isEmpty(killCountName)) {
            return null;
        }

        for (CollectionLogTab tab : tabs.values()) {
            for (CollectionLogPage page : tab.getPages().values()) {
                for (CollectionLogKillCount killCount : page.getKillCounts()) {
                    if (killCountName.equalsIgnoreCase(killCount.getName())) {
                        return killCount;
                    }
                }
            }
        }
        return null;
    }

}