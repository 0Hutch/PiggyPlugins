package com.bankmemory.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ItemDataParserTest {
    @Parameter
    public String invalidSaveString;

    @Parameters
    public static Object[][] invalidData() {
        String tooFewNumbers = "301,";
        String extraNumber = "10,10,5,5,666,";
        String nonNumericCharacters = "10,10,spade,5,";
        return new Object[][] {
                { tooFewNumbers },
                { extraNumber },
                { nonNumericCharacters },
        };
    }

    @Test
    public void testParseSaveString_givenInvalidData() {
        ItemDataParser parser = new ItemDataParser();

        try {
            parser.parseSaveString(invalidSaveString);
            fail();
        } catch (JsonParseException ex) {
            // Good!
        }
    }

    @Test
    public void testParseSaveStringAndToSaveStringMethods_givenNullString() {
        ItemDataParser parser = new ItemDataParser();

        assertThat(parser.parseSaveString(null), is(new ArrayList<>()));
    }

    @Test
    public void testParseSaveStringAndToSaveStringMethods_givenEmptyBank() {
        String emptyBankSaveString = "";
        assertEqualsAllWays(emptyBankSaveString, new ArrayList<>());
    }

    @Test
    public void testParseSaveStringAndToSaveStringMethods_givenBankWithItems() {
        String nonEmptyBankSaveString = "301,1,302,10,303,5,304,3,";
        List<BankItem> bankItems = ImmutableList.of(
                new BankItem(301, 1), new BankItem(302, 10), new BankItem(303, 5), new BankItem(304, 3));
        assertEqualsAllWays(nonEmptyBankSaveString, bankItems);
    }

    private void assertEqualsAllWays(String saveString, List<BankItem> bankItemList) {
        ItemDataParser parser = new ItemDataParser();

        assertThat(parser.parseSaveString(saveString), is(bankItemList));
        assertThat(parser.toSaveString(bankItemList), is(saveString));
        assertThat(parser.toSaveString(parser.parseSaveString(saveString)), is(saveString));
        assertThat(parser.parseSaveString(parser.toSaveString(bankItemList)), is(bankItemList));
    }
}