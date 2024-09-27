package com.bankmemory.bankview;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.event.ListDataListener;
import com.google.inject.testing.fieldbinder.*;
import net.runelite.client.callback.*;
import net.runelite.client.util.AsyncBufferedImage;
import org.junit.Test;
import org.mockito.*;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class FilterableItemListModelTest {

    private static final ItemListEntry dragonScimitar = item("Dragon Scimitar");
    private static final ItemListEntry runeAxe = item("Rune axe");
    private static final ItemListEntry runeDagger = item("Rune dagger");
    private static final ItemListEntry magicStaff = item("Magic staff");
    private static final ItemListEntry airRune = item("Air rune");

    private static final ItemListEntry dragonstone = item("Dragonstone");
    private static final ItemListEntry amuletOfGlory = item("Amulet of Glory");
    private static final ItemListEntry antiDragonShield = item("Anti-dragon shield");
    private static final ItemListEntry petKitten = item("Pet kitten");

    @Mock
    @Bind
    private static ClientThread clientThread;

    @Test
    public void testGetElementAt_givenNoFilter() {
        FilterableItemListModel model = new FilterableItemListModel();
        model.setListContents(list(dragonScimitar, runeAxe, runeDagger, magicStaff, airRune));

        assertThat(model.getSize(), is(5));
        assertThat(model.getElementAt(0), is(dragonScimitar));
        assertThat(model.getElementAt(1), is(runeAxe));
        assertThat(model.getElementAt(2), is(runeDagger));
        assertThat(model.getElementAt(3), is(magicStaff));
        assertThat(model.getElementAt(4), is(airRune));

        model.setListContents(list(dragonstone, amuletOfGlory, antiDragonShield, petKitten));

        assertThat(model.getSize(), is(4));
        assertThat(model.getElementAt(0), is(dragonstone));
        assertThat(model.getElementAt(1), is(amuletOfGlory));
        assertThat(model.getElementAt(2), is(antiDragonShield));
        assertThat(model.getElementAt(3), is(petKitten));
    }

    @Test
    public void testGetElementAt_givenFilterApplied() {
        FilterableItemListModel model = new FilterableItemListModel();
        model.setListContents(list(dragonScimitar, runeAxe, runeDagger, magicStaff, airRune));
        model.applyFilter("RuNe");

        assertThat(model.getSize(), is(3));
        assertThat(model.getElementAt(0), is(runeAxe));
        assertThat(model.getElementAt(1), is(runeDagger));
        assertThat(model.getElementAt(2), is(airRune));
    }

    @Test
    public void testGetElementAt_givenFilterRemoved() {
        FilterableItemListModel model = new FilterableItemListModel();
        model.setListContents(list(dragonScimitar, runeAxe, runeDagger, magicStaff, airRune));
        model.applyFilter("rUnE");

        assertThat(model.getSize(), is(3));
        assertThat(model.getElementAt(0), is(runeAxe));
        assertThat(model.getElementAt(1), is(runeDagger));
        assertThat(model.getElementAt(2), is(airRune));

        model.applyFilter("");

        assertThat(model.getSize(), is(5));
        assertThat(model.getElementAt(0), is(dragonScimitar));
        assertThat(model.getElementAt(1), is(runeAxe));
        assertThat(model.getElementAt(2), is(runeDagger));
        assertThat(model.getElementAt(3), is(magicStaff));
        assertThat(model.getElementAt(4), is(airRune));
    }

    @Test
    public void testGetElementAt_givenFilterAppliedAndNewContentsSet() {
        FilterableItemListModel model = new FilterableItemListModel();
        model.setListContents(list(dragonScimitar, runeAxe, runeDagger, magicStaff, airRune));
        model.applyFilter("dRaGOn");

        assertThat(model.getSize(), is(1));
        assertThat(model.getElementAt(0), is(dragonScimitar));

        model.setListContents(list(dragonstone, amuletOfGlory, antiDragonShield, petKitten));

        assertThat(model.getSize(), is(2));
        assertThat(model.getElementAt(0), is(dragonstone));
        assertThat(model.getElementAt(1), is(antiDragonShield));
    }

    @Test
    public void testGetAdjustedIndex() {
        FilterableItemListModel model = new FilterableItemListModel();
        model.setListContents(list(dragonScimitar, runeAxe, runeDagger, magicStaff, airRune));
        assertThat(model.getSize(), is(5));
        assertThat(model.getAdjustedIndex(0), is(0));
        assertThat(model.getAdjustedIndex(1), is(1));
        assertThat(model.getAdjustedIndex(2), is(2));
        assertThat(model.getAdjustedIndex(3), is(3));
        assertThat(model.getAdjustedIndex(4), is(4));

        model.applyFilter("RuNe");

        assertThat(model.getSize(), is(3));
        assertThat(model.getAdjustedIndex(0), is(-1));
        assertThat(model.getAdjustedIndex(1), is(0));
        assertThat(model.getAdjustedIndex(2), is(1));
        assertThat(model.getAdjustedIndex(3), is(-1));
        assertThat(model.getAdjustedIndex(4), is(2));

        model.applyFilter("Karil's crossbow"); // Which, of course, my BTW doesn't have

        assertThat(model.getSize(), is(0));
        assertThat(model.getAdjustedIndex(0), is(-1));
        assertThat(model.getAdjustedIndex(1), is(-1));
        assertThat(model.getAdjustedIndex(2), is(-1));
        assertThat(model.getAdjustedIndex(3), is(-1));
        assertThat(model.getAdjustedIndex(4), is(-1));
    }

    @Test
    public void testGetAdjustedIndex_givenIndexBelowZero() {
        FilterableItemListModel model = new FilterableItemListModel();
        model.setListContents(list(dragonScimitar, runeAxe, runeDagger, magicStaff, airRune));

        try {
            model.getAdjustedIndex(-1);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            assertThat(ex.getMessage(), is("index (-1) must not be negative"));
        }
    }

    @Test
    public void testGetAdjustedIndex_givenIndexAboveRawContentsMaxIndex() {
        FilterableItemListModel model = new FilterableItemListModel();
        model.setListContents(list(dragonScimitar, runeAxe, runeDagger, magicStaff, airRune));

        try {
            model.getAdjustedIndex(100000);
            fail();
        } catch (IndexOutOfBoundsException ex) {
            assertThat(ex.getMessage(), is("index (100000) must be less than size (5)"));
        }
    }

    @Test
    public void testSetListContents_notifiesOffListeners() {
        FilterableItemListModel model = new FilterableItemListModel();
        ListDataListener listener1 = mock(ListDataListener.class);
        ListDataListener listener2 = mock(ListDataListener.class);
        model.addListDataListener(listener1);
        model.addListDataListener(listener2);

        verifyNoInteractions(listener1, listener2);

        model.setListContents(list(dragonScimitar, runeAxe, runeDagger, magicStaff, airRune));

        verify(listener1).contentsChanged(any());
        verify(listener2).contentsChanged(any());
        verifyNoMoreInteractions(listener1, listener2);
    }

    @Test
    public void testApplyFilter_notifiesListenersOnlyIfEffectIsDifferent() {
        FilterableItemListModel model = new FilterableItemListModel();
        model.setListContents(list(dragonScimitar, runeAxe, runeDagger, magicStaff, airRune));
        ListDataListener listener1 = mock(ListDataListener.class);
        ListDataListener listener2 = mock(ListDataListener.class);
        model.addListDataListener(listener1);
        model.addListDataListener(listener2);

        verifyNoInteractions(listener1, listener2);

        model.applyFilter("");

        verifyNoInteractions(listener1, listener2);

        model.applyFilter("RUN");

        verify(listener1).contentsChanged(any());
        verify(listener2).contentsChanged(any());

        model.applyFilter("ruNE");

        verifyNoMoreInteractions(listener1, listener2);
    }

    private static ItemListEntry item(String name) {
        return new ItemListEntry(name, 1, new AsyncBufferedImage(clientThread, 1, 1, BufferedImage.TYPE_INT_ARGB), 1, 1);
    }

    private static List<ItemListEntry> list(ItemListEntry... items) {
        return Lists.newArrayList(items);
    }
}