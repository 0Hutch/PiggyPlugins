package com.bankmemory.bankview;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


import static com.google.common.base.Preconditions.checkElementIndex;

class FilterableItemListModel implements ListModel<ItemListEntry> {

    private List<ItemListEntry> rawListContents = new ArrayList<>();
    private String lcFilterString = "";
    private List<Integer> postFilterIndexes = new ArrayList<>();
    private final List<ListDataListener> listeners = new ArrayList<>();

    public void setListContents(List<ItemListEntry> contents) {
        rawListContents = ImmutableList.copyOf(contents);
        applyFilterSilently(lcFilterString);
        fireListeners();
    }

    private static List<Integer> unfilteredIndexList(int numElements) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < numElements; i++) {
            indexes.add(i);
        }
        return indexes;
    }

    private void fireListeners() {
        ListDataEvent evt = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1);
        listeners.forEach(l -> l.contentsChanged(evt));
    }

    public void clearList() {
        setListContents(new ArrayList<>());
    }

    @Override
    public int getSize() {
        return postFilterIndexes.size();
    }

    @Override
    public ItemListEntry getElementAt(int index) {
        return rawListContents.get(postFilterIndexes.get(index));
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public void applyFilter(String filterString) {
        List<Integer> oldIndexes = postFilterIndexes;
        applyFilterSilently(filterString);
        if (!oldIndexes.equals(postFilterIndexes)) {
            fireListeners();
        }
    }

    private void applyFilterSilently(String filterString) {
        lcFilterString = filterString.toLowerCase();
        if (filterString.isEmpty()) {
            postFilterIndexes = unfilteredIndexList(rawListContents.size());
            return;
        }
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < rawListContents.size(); i++) {
            if (rawListContents.get(i).getItemName().toLowerCase().contains(lcFilterString)) {
                indexes.add(i);
            }
        }
        postFilterIndexes = indexes;
    }

    public void clearFilter() {
        applyFilter("");
    }

    /**
     * @return -1 if the rawIndex has been filtered out, else the index's adjusted position with any filter applied.
     */
    public int getAdjustedIndex(int rawIndex) {
        checkElementIndex(rawIndex, rawListContents.size());
        return postFilterIndexes.indexOf(rawIndex);
    }
}
