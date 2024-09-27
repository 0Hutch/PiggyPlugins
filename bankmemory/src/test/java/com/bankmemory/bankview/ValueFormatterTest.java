package com.bankmemory.bankview;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ValueFormatterTest extends TestCase {

    @Parameter(0) public long value;
    @Parameter(1) public String formatted;
    @Parameter(2) public String formattedAbbreviated;

    @Parameters
    public static Object[][] parameterData() {
        return new Object[][]{
                {1, "1", "1"},
                {9999, "9,999", "9,999"},
                {10000, "10,000", "10K"},
                {10001, "10,001", "10K"},
                {10101, "10,101", "10.1K"},
                {123000, "123,000", "123K"},
                {999999, "999,999", "999.99K"},
                {1000001, "1,000,001", "1M"},
                {1020000, "1,020,000", "1.02M"},
                {999999999, "999,999,999", "999.99M"},
                {808019000000L, "808,019,000,000", "808.01B"}
        };
    }

    @Test
    public void testFormat() {
        ValueFormatter formatter = new ValueFormatter();

        assertEquals(formatted, formatter.format(value));

        assertEquals("-" + formatted, formatter.format(-value));

        formatter.setShowPositiveSign(true);

        assertEquals("+" + formatted, formatter.format(value));
    }

    @Test
    public void testFormatAbbreviated() {
        ValueFormatter formatter = new ValueFormatter();

        assertEquals(formattedAbbreviated, formatter.formatAbbreviated(value));

        assertEquals("-" + formattedAbbreviated, formatter.formatAbbreviated(-value));

        formatter.setShowPositiveSign(true);

        assertEquals("+" + formattedAbbreviated, formatter.formatAbbreviated(value));
    }
}
