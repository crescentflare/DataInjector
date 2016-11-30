package com.crescentflare.datainjector.conversion;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * Utility test: map utility
 */
public class InjectorConvTest
{
    // ---
    // Test parse conversion
    // ---

    @Test
    public void optionalDate() throws Exception
    {
        String dates[] = new String[]
        {
                "2016-08-19",
                "2016-05-16T01:10:28",
                "2016-02-27T12:24:11Z",
                "2016-02-27T19:00:00+02:00"
        };
        Assert.assertEquals(dateFrom(2016, 8, 19).toString(), InjectorConv.toDate(dates[0]).toString());
        Assert.assertEquals(dateFrom(2016, 5, 16, 1, 10, 28).toString(), InjectorConv.toDate(dates[1]).toString());
        Assert.assertEquals(utcDateFrom(2016, 2, 27, 12, 24, 11).toString(), InjectorConv.toDate(dates[2]).toString());
        Assert.assertEquals(utcDateFrom(2016, 2, 27, 17, 0, 0).toString(), InjectorConv.toDate(dates[3]).toString());
    }



    // ---
    // Test primitive type conversion
    // ---

    @Test
    public void optionalString() throws Exception
    {
        Assert.assertEquals("test", InjectorConv.toString("test"));
        Assert.assertEquals("12", InjectorConv.toString(12));
        Assert.assertEquals("14.42", InjectorConv.toString(14.42));
        Assert.assertEquals("true", InjectorConv.toString(true));
    }

    @Test
    public void optionalDouble() throws Exception
    {
        Assert.assertEquals(89.213, InjectorConv.toDouble("89.213"));
        Assert.assertEquals(31.0, InjectorConv.toDouble(31));
    }

    @Test
    public void optionalFloat() throws Exception
    {
        Assert.assertEquals(21.3f, InjectorConv.toFloat(21.3));
        Assert.assertEquals(1.0f, InjectorConv.toFloat(true));
    }

    @Test
    public void optionalInteger() throws Exception
    {
        Assert.assertEquals((Integer)3, InjectorConv.toInteger("3"));
        Assert.assertEquals((Integer)45, InjectorConv.toInteger(45.75));
    }

    @Test
    public void optionalBoolean() throws Exception
    {
        Map<String, Object> map = new HashMap<>();
        map.put("int", 0);
        map.put("boolean", true);
        Assert.assertEquals(Boolean.FALSE, InjectorConv.toBoolean("false"));
        Assert.assertEquals(Boolean.TRUE, InjectorConv.toBoolean(2));
    }


    // ---
    // Helpers
    // ---

    public Date dateFrom(int year, int month, int day)
    {
        return dateFrom(year, month, day, 0, 0, 0);
    }

    public Date dateFrom(int year, int month, int day, int hour, int minute, int second)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }

    public Date utcDateFrom(int year, int month, int day)
    {
        return utcDateFrom(year, month, day, 0, 0, 0);
    }

    public Date utcDateFrom(int year, int month, int day, int hour, int minute, int second)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return calendar.getTime();
    }
}
