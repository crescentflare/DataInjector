package com.crescentflare.datainjector.utility;

import com.crescentflare.datainjector.utility.InjectorMapEntry;
import com.crescentflare.datainjector.utility.InjectorUtil;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility test: data injection utilities
 */
public class InjectorUtilTest
{
    // ---
    // Test map utilities
    // ---

    @Test
    public void itemFromMap() throws Exception
    {
        Map<String, Object> map = InjectorUtil.initMap(
                "first", "string",
                "second", 10,
                "third", InjectorUtil.initMap(
                        "nested", "value",
                        "deeplynested", InjectorUtil.initMap(
                                "value", 11.1
                        )
                ),
                "fourth", true
        );
        Assert.assertEquals("string", InjectorUtil.itemFromMap(map, "first"));
        Assert.assertEquals(10, InjectorUtil.itemFromMap(map, "second"));
        Assert.assertEquals("value", InjectorUtil.itemFromMap(map, "third.nested"));
        Assert.assertEquals(11.1, InjectorUtil.itemFromMap(map, "third.deeplynested.value"));
        Assert.assertEquals(true, InjectorUtil.itemFromMap(map, "fourth"));
        Assert.assertNull(InjectorUtil.itemFromMap(map, "invalidpath"));
    }

    @Test
    public void setItemOnMap() throws Exception
    {
        Map<String, Object> map = InjectorUtil.initMap(
                "string", "text",
                "array", Arrays.asList(0, 1, 2, 3),
                "dictionary", InjectorUtil.initMap(
                        "first", "stringValue",
                        "second", 123,
                        "third", true
                )
        );
        InjectorUtil.setItemOnMap(map, "string", "modified text");
        InjectorUtil.setItemOnMap(map, "array.2", 77);
        InjectorUtil.setItemOnMap(map, "dictionary.first", "modifiedStringValue");
        InjectorUtil.setItemOnMap(map, "dictionary.second", 987);
        InjectorUtil.setItemOnMap(map, "dictionary.third", false);
        Assert.assertEquals("modified text", InjectorUtil.itemFromMap(map, "string"));
        Assert.assertEquals(77, InjectorUtil.itemFromMap(map, "array.2"));
        Assert.assertEquals("modifiedStringValue", InjectorUtil.itemFromMap(map, "dictionary.first"));
        Assert.assertEquals(987, InjectorUtil.itemFromMap(map, "dictionary.second"));
        Assert.assertEquals(false, InjectorUtil.itemFromMap(map, "dictionary.third"));
    }

    @Test
    public void initMap() throws Exception
    {
        Map<String, Object> firstMap = InjectorUtil.initMap(
                "first", "stringValue",
                "second", 20
        );
        Map<String, Object> secondMap = InjectorUtil.initMap(
                new InjectorMapEntry<>("first", "otherString"),
                new InjectorMapEntry<>("second", true)
        );
        Assert.assertEquals("stringValue", firstMap.get("first"));
        Assert.assertEquals(20, firstMap.get("second"));
        Assert.assertEquals("otherString", secondMap.get("first"));
        Assert.assertEquals(true, secondMap.get("second"));
    }


    // ---
    // Test array utilities
    // ---

    @Test
    public void itemFromList() throws Exception
    {
        List<Object> list = Arrays.asList(
                "zero",
                "one",
                Arrays.asList("a", "b", "c", "d"),
                "three",
                InjectorUtil.initMap(
                        "test", "dictionary",
                        "dictarray", Arrays.asList("first", "second", "third")
                )
        );
        Assert.assertEquals("zero", InjectorUtil.itemFromList(list, "0"));
        Assert.assertEquals("one", InjectorUtil.itemFromList(list, "1"));
        Assert.assertEquals("b", InjectorUtil.itemFromList(list, "2.1"));
        Assert.assertEquals("three", InjectorUtil.itemFromList(list, "3"));
        Assert.assertEquals("dictionary", InjectorUtil.itemFromList(list, "4.test"));
        Assert.assertEquals("third", InjectorUtil.itemFromList(list, "4.dictarray.2"));
    }

    @Test
    public void setItemOnList() throws Exception
    {
        List<Object> list = Arrays.asList(
                "string",
                InjectorUtil.initMap(
                        "first", "stringValue",
                        "second", 123
                )
        );
        InjectorUtil.setItemOnList(list, "0", "modified string");
        InjectorUtil.setItemOnList(list, "1.first", "newString");
        InjectorUtil.setItemOnList(list, "1.second", 2022);
        Assert.assertEquals("modified string", InjectorUtil.itemFromList(list, "0"));
        Assert.assertEquals("newString", InjectorUtil.itemFromList(list, "1.first"));
        Assert.assertEquals(2022, InjectorUtil.itemFromList(list, "1.second"));
    }
}
