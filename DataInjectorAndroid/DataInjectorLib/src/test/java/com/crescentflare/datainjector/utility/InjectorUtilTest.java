package com.crescentflare.datainjector.utility;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Utility test: data injection utilities
 */
public class InjectorUtilTest
{
    // --
    // Test map utilities
    // --

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
}
