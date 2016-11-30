package com.crescentflare.datainjector.conversion;

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
    public void asStringObjectMap() throws Exception
    {
        Map<String, Object> correctMap = InjectorUtil.initMap(
                "first", 11,
                "second", "string"
        );
        Map<Date, String> incorrectMap = new HashMap<>();
        incorrectMap.put(new Date(), "string");
        Object noMap = "string";
        Assert.assertEquals(correctMap, InjectorUtil.asStringObjectMap(correctMap));
        Assert.assertNull(InjectorUtil.asStringObjectMap(incorrectMap));
        Assert.assertNull(InjectorUtil.asStringObjectMap(noMap));
    }

    @Test
    public void isMap() throws Exception
    {
        Map<String, Object> correctMap = InjectorUtil.initMap(
                "first", 11,
                "second", "string"
        );
        Object noMap = "string";
        Assert.assertTrue(InjectorUtil.isMap(correctMap));
        Assert.assertFalse(InjectorUtil.isMap(noMap));
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
    public void asObjectList() throws Exception
    {
        List<Object> correctList = Arrays.asList((Object)"string", (Object)11.23);
        Object noList = "string";
        Assert.assertEquals(correctList, InjectorUtil.asObjectList(correctList));
        Assert.assertNull(InjectorUtil.asObjectList(noList));
    }

    @Test
    public void isList() throws Exception
    {
        List<Object> correctList = Arrays.asList((Object)"string", (Object)11.23);
        Object noList = "string";
        Assert.assertTrue(InjectorUtil.isList(correctList));
        Assert.assertFalse(InjectorUtil.isList(noList));
    }
}
