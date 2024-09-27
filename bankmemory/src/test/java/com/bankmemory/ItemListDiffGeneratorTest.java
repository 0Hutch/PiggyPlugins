package com.bankmemory;

import com.bankmemory.data.BankItem;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class ItemListDiffGeneratorTest extends TestCase {
    @Parameter(0)
    public List<BankItem> beforeItems;
    @Parameter(1)
    public List<BankItem> afterItems;
    @Parameter(2)
    public List<BankItem> expectedResult;

    @Parameters
    public static Object[][] data() {
        List<BankItem> empty = new ArrayList<>();
        List<BankItem> data = Lists.newArrayList(
                i(1, 5), i(7, 2), i(3, 20), i(10, 10000), i(66, 4));
        List<BankItem> sameDataDifferentOrder = Lists.newArrayList(
                i(10, 10000), i(66, 4), i(7, 2), i(3, 20), i(1, 5));
        List<BankItem> different = Lists.newArrayList(
                i(1, 6), i(22, 67), i(7, 1), i(10, 5000), i(4, 66));
        return new Object[][] {
                { empty, empty, empty },
                { data, data, empty },
                { data, sameDataDifferentOrder, empty },
                { data, different, Lists.newArrayList(
                        i(1, 1), i(7, -1), i(3, -20), i(10, -5000), i(66, -4), i(22, 67), i(4, 66)) },
                { different, data, Lists.newArrayList(
                        i(1, -1), i(22, -67), i(7, 1), i(10, 5000), i(4, -66), i(3, 20), i(66, 4)) }
        };
    }

    private static BankItem i(int id, int quantity) {
        return new BankItem(id, quantity);
    }

    @Test
    public void testFindDifferencesBetween() {
        ItemListDiffGenerator diffGenerator = new ItemListDiffGenerator();

        List<BankItem> result = diffGenerator.findDifferencesBetween(beforeItems, afterItems);

        assertThat(result, is(expectedResult));
    }
}