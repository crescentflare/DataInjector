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
 * Injector test: replacing null injection
 */
public class ReplaceNullInjectorTest
{
    // --
    // Test manual null filtering
    // --

    @Test
    public void filterNull() throws Exception
    {
        // Set up map
        Map<String, Object> nestedMap = InjectorUtil.initMap(
                "Netherlands", InjectorUtil.initMap(
                        "Amsterdam", InjectorUtil.initMap(
                                "centralLocation", "52.3791283, 4.8980833",
                                "harborLocation", null
                        ),
                        "Rotterdam", InjectorUtil.initMap(
                                "centralLocation", "51.9231934, 4.4676489",
                                "harborLocation", "51.9496008, 4.1430743"
                        )
                ),
                "Germany", InjectorUtil.initMap(
                        "Düsseldorf", InjectorUtil.initMap(
                                "centralLocation", "51.2226277, 6.7866488",
                                "harborLocation", null
                        ),
                        "Berlin", InjectorUtil.initMap(
                                "centralLocation", "52.511654, 13.2737445",
                                "harborLocation", null
                        )
                )
        );

        // Apply manual injection to filter out the null values
        InjectorResult result = ReplaceNullInjector.filterNull(nestedMap, true);

        // Verify the change
        Map<String, Object> amsterdamMap = InjectorConv.asStringObjectMap(DataInjector.get(result.getModifiedObject(), "Netherlands.Amsterdam"));
        Map<String, Object> rotterdamMap = InjectorConv.asStringObjectMap(DataInjector.get(result.getModifiedObject(), "Netherlands.Rotterdam"));
        Assert.assertTrue(amsterdamMap.keySet().contains("centralLocation"));
        Assert.assertFalse(amsterdamMap.keySet().contains("harborLocation"));
        Assert.assertTrue(rotterdamMap.keySet().contains("centralLocation"));
        Assert.assertTrue(rotterdamMap.keySet().contains("harborLocation"));
    }


    // --
    // Test manual null replacement
    // --

    @Test
    public void replaceNull() throws Exception
    {
        // Set up map
        Map<String, Object> nestedMap = InjectorUtil.initMap(
                "Vegetables", Arrays.asList(
                        InjectorUtil.initMap(
                                "name", "Broccoli",
                                "description", "A healthy vegetable containing many nutrients"
                        ),
                        InjectorUtil.initMap(
                                "name", "Carrots",
                                "description", null
                        )
                ),
                "Meat", Collections.singletonList(
                        InjectorUtil.initMap(
                                "name", "Burger"
                        )
                ),
                "Deserts", Collections.singletonList(null)
        );

        // Set up defaults
        Map<String, Object> defaults = InjectorUtil.initMap(
                "Vegetables", Collections.singletonList(
                        InjectorUtil.initMap(
                                "name", "Untitled",
                                "description", "No description given"
                        )
                ),
                "Meat", Collections.singletonList(
                        InjectorUtil.initMap(
                                "name", "Untitled",
                                "description", "No description given"
                        )
                ),
                "Deserts", Collections.singletonList(
                        InjectorUtil.initMap(
                                "name", "Untitled",
                                "description", "No description given"
                        )
                )
        );

        // Apply manual injection to replace the nil values
        InjectorResult result = ReplaceNullInjector.replaceNull(nestedMap, defaults, true, false);

        // Verify the change
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "Vegetables.0.description"), "A healthy vegetable containing many nutrients");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "Vegetables.1.description"), "No description given");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "Meat.0.description"), "No description given");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "Deserts.0.name"), "Untitled");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "Deserts.0.description"), "No description given");
    }


    // --
    // Test generic injection
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up map for modification
        Map<String, Object> sampleMap = InjectorUtil.initMap(
                "title", "Data Injector",
                "text", "A library to easily manipulate data",
                "description", null
        );

        // Set up map for the data source
        Map<String, Object> dataSource = InjectorUtil.initMap(
                "title", "Untitled",
                "text", "...",
                "description", "No description given",
                "status", "Unknown"
        );

        // Set up injector
        ReplaceNullInjector injector = new ReplaceNullInjector();
        injector.setRecursive(true);
        injector.setIgnoreNotExisting(true);

        // Apply
        InjectorResult result = injector.apply(sampleMap, dataSource);

        // Verify values
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "title"), sampleMap.get("title"));
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "text"), sampleMap.get("text"));
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "description"), dataSource.get("description"));
        Assert.assertNull(DataInjector.get(result.getModifiedObject(), "status"));
    }
}
