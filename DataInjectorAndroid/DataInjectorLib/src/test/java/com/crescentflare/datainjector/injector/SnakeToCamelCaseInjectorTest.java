package com.crescentflare.datainjector.injector;


import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Injector test: convert to camel case injection
 */
public class SnakeToCamelCaseInjectorTest
{
    // --
    // Test manual injection
    // --

    @Test
    public void changeCase() throws Exception
    {
        // Set up map
        Map<String, Object> nestedMap = InjectorUtil.initMap(
                "first_set", InjectorUtil.initMap(
                        "snake_case_key", "converted",
                        "nocase", 10
                ),
                "second_set", InjectorUtil.initMap(
                        "another_case", null,
                        "alreadyCamelCase", true
                )
        );

        // Apply manual injection to convert the case of the dictionary keys
        InjectorResult result = SnakeToCamelCaseInjector.changeCase(nestedMap, true);

        // Verify the change
        Map<String, Object> secondMap = InjectorConv.asStringObjectMap(DataInjector.get(result.getModifiedObject(), "secondSet"));
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "firstSet.snakeCaseKey"), "converted");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "firstSet.nocase"), 10);
        Assert.assertTrue(secondMap.keySet().contains("anotherCase"));
        Assert.assertTrue((Boolean)DataInjector.get(result.getModifiedObject(), "secondSet.alreadyCamelCase"));
    }


    // --
    // Test generic injection
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up map for modification
        Map<String, Object> sampleMap = InjectorUtil.initMap(
                "dont_touch_this_key", InjectorUtil.initMap(
                        "snake_case_key", "First",
                        "another_key", "Second"
                )
        );

        // Set up injector
        SnakeToCamelCaseInjector injector = new SnakeToCamelCaseInjector();
        injector.setRecursive(true);
        injector.setTargetItemPath(new InjectorPath("dont_touch_this_key"));

        // Apply
        InjectorResult result = injector.apply(sampleMap);

        // Verify values
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "dont_touch_this_key.snakeCaseKey"), "First");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "dont_touch_this_key.anotherKey"), "Second");
    }
}
