package com.crescentflare.datainjector.conversion;

import com.crescentflare.datainjector.utility.InjectorUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Utility test: data conversion
 */
public class InjectorConvTest
{
    // --
    // Test array list conversion
    // --

    @Test
    public void asStringList() throws Exception
    {
        List<?> mixedList = Arrays.asList(
                "test",
                12,
                14.42,
                true
        );
        List<String> stringList = InjectorConv.asStringList(mixedList);
        Assert.assertEquals("test", stringList.get(0));
        Assert.assertEquals("12", stringList.get(1));
        Assert.assertEquals("14.42", stringList.get(2));
        Assert.assertEquals("true", stringList.get(3));
    }

    @Test
    public void asDoubleList() throws Exception
    {
        List<?> mixedList = Arrays.asList(
                "89.213",
                31
        );
        List<Double> doubleList = InjectorConv.asDoubleList(mixedList);
        Assert.assertEquals(89.213, doubleList.get(0), 0.00001);
        Assert.assertEquals(31.0, doubleList.get(1), 0.00001);
    }

    @Test
    public void asFloatList() throws Exception
    {
        List<?> mixedList = Arrays.asList(
                21.3,
                true
        );
        List<Float> floatList = InjectorConv.asFloatList(mixedList);
        Assert.assertEquals(21.3f, floatList.get(0), 0.00001);
        Assert.assertEquals(1.0f, floatList.get(1), 0.00001);
    }

    @Test
    public void asIntegerList() throws Exception
    {
        List<?> mixedList = Arrays.asList(
                "3",
                45.75
        );
        List<Integer> integerList = InjectorConv.asIntegerList(mixedList);
        Assert.assertEquals((Integer)3, integerList.get(0));
        Assert.assertEquals((Integer)45, integerList.get(1));
    }

    @Test
    public void asBooleanList() throws Exception
    {
        List<?> mixedList = Arrays.asList(
                "false",
                2
        );
        List<Boolean> booleanList = InjectorConv.asBooleanList(mixedList);
        Assert.assertEquals(Boolean.FALSE, booleanList.get(0));
        Assert.assertEquals(Boolean.TRUE, booleanList.get(1));
    }


    // --
    // Test special map and list conversion
    // --

    @Test
    public void asStringObjectMap() throws Exception
    {
        Map<String, Object> correctMap = InjectorUtil.initMap(
                "first", 11,
                "second", "string"
        );
        Map<Date, String> incorrectMap = new HashMap<>();
        incorrectMap.put(new Date(), "string");
        Object noMap = "string";
        Assert.assertEquals(correctMap, InjectorConv.asStringObjectMap(correctMap));
        Assert.assertNull(InjectorConv.asStringObjectMap(incorrectMap));
        Assert.assertNull(InjectorConv.asStringObjectMap(noMap));
    }

    @Test
    public void asObjectList() throws Exception
    {
        List<?> correctList = Arrays.asList("string", 11.23);
        Object noList = "string";
        Assert.assertEquals(correctList, InjectorConv.asObjectList(correctList));
        Assert.assertNull(InjectorConv.asObjectList(noList));
    }


    // --
    // Test date parse conversion
    // --

    @Test
    public void asDateList() throws Exception
    {
        List<String> stringList = Arrays.asList(
                "2016-08-19",
                "2016-05-16T01:10:28",
                "2016-02-27T12:24:11Z",
                "2016-02-27T19:00:00+02:00"
        );
        List<Date> dateList = InjectorConv.asDateList(stringList);
        Assert.assertEquals(dateFrom(2016, 8, 19).toString(), dateList.get(0).toString());
        Assert.assertEquals(dateFrom(2016, 5, 16, 1, 10, 28).toString(), dateList.get(1).toString());
        Assert.assertEquals(utcDateFrom(2016, 2, 27, 12, 24, 11).toString(), dateList.get(2).toString());
        Assert.assertEquals(utcDateFrom(2016, 2, 27, 17, 0, 0).toString(), dateList.get(3).toString());
    }

    @Test
    public void asDate() throws Exception
    {
        String[] dates = new String[]
        {
                "2016-08-19",
                "2016-05-16T01:10:28",
                "2016-02-27T12:24:11Z",
                "2016-02-27T19:00:00+02:00"
        };
        Assert.assertEquals(dateFrom(2016, 8, 19).toString(), InjectorConv.asDate(dates[0]).toString());
        Assert.assertEquals(dateFrom(2016, 5, 16, 1, 10, 28).toString(), InjectorConv.asDate(dates[1]).toString());
        Assert.assertEquals(utcDateFrom(2016, 2, 27, 12, 24, 11).toString(), InjectorConv.asDate(dates[2]).toString());
        Assert.assertEquals(utcDateFrom(2016, 2, 27, 17, 0, 0).toString(), InjectorConv.asDate(dates[3]).toString());
    }


    // --
    // Test primitive type conversion
    // --

    @Test
    public void asString() throws Exception
    {
        Assert.assertEquals("test", InjectorConv.asString("test"));
        Assert.assertEquals("12", InjectorConv.asString(12));
        Assert.assertEquals("14.42", InjectorConv.asString(14.42));
        Assert.assertEquals("true", InjectorConv.asString(true));
    }

    @Test
    public void asDouble() throws Exception
    {
        Assert.assertEquals(89.213, InjectorConv.asDouble("89.213"), 0.00001);
        Assert.assertEquals(31.0, InjectorConv.asDouble(31), 0.00001);
    }

    @Test
    public void asFloat() throws Exception
    {
        Assert.assertEquals(21.3f, InjectorConv.asFloat(21.3), 0.00001);
        Assert.assertEquals(1.0f, InjectorConv.asFloat(true), 0.00001);
    }

    @Test
    public void asInteger() throws Exception
    {
        Assert.assertEquals((Integer)3, InjectorConv.asInteger("3"));
        Assert.assertEquals((Integer)45, InjectorConv.asInteger(45.75));
    }

    @Test
    public void asBoolean() throws Exception
    {
        Assert.assertEquals(Boolean.FALSE, InjectorConv.asBoolean("false"));
        Assert.assertEquals(Boolean.TRUE, InjectorConv.asBoolean(2));
    }


    // --
    // Helpers
    // --

    private Date dateFrom(int year, int month, int day)
    {
        return dateFrom(year, month, day, 0, 0, 0);
    }

    private Date dateFrom(int year, int month, int day, int hour, int minute, int second)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }

    private Date utcDateFrom(int year, int month, int day)
    {
        return utcDateFrom(year, month, day, 0, 0, 0);
    }

    private Date utcDateFrom(int year, int month, int day, int hour, int minute, int second)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }
}
