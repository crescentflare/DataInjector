package com.crescentflare.datainjector.injector;


import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Injector test: data linking
 */
public class LinkDataInjectorTest
{
    // --
    // Test manual injection on a single item
    // --

    @Test
    public void linkDataSingle() throws Exception
    {
        // Set up map and lookup list
        Map<String, Object> targetMap = InjectorUtil.initMap(
                "name", "John Doe",
                "statusId", "1"
        );
        List<Map<String, Object>> sourceList = Arrays.asList(
                InjectorUtil.initMap(
                        "statusId", "0",
                        "status", "ready"
                ),
                InjectorUtil.initMap(
                        "statusId", "1",
                        "status", "progress"
                ),
                InjectorUtil.initMap(
                        "statusId", "2",
                        "status", "done"
                )
        );

        // Apply manual injection to link one of the statuses
        InjectorResult result = LinkDataInjector.linkData(targetMap, sourceList, "statusId");

        // Verify the linked status text
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "status"), "progress");
    }


    // --
    // Test manual injection on a list of items
    // --

    @Test
    public void linkDataList() throws Exception
    {
        // Set up data and lookup list
        List<Map<String, Object>> targetList = Arrays.asList(
                InjectorUtil.initMap(
                        "name", "John Doe",
                        "statusId", "1"
                ),
                InjectorUtil.initMap(
                        "name", "Jack the Joker",
                        "statusId", "0"
                ),
                InjectorUtil.initMap(
                        "name", "Mary-Anne Adams",
                        "statusId", "2"
                )
        );
        List<Map<String, Object>> sourceList = Arrays.asList(
                InjectorUtil.initMap(
                        "statusId", "0",
                        "status", "ready"
                ),
                InjectorUtil.initMap(
                        "statusId", "1",
                        "status", "progress"
                ),
                InjectorUtil.initMap(
                        "statusId", "2",
                        "status", "done"
                )
        );

        // Apply manual injection to link the statuses
        InjectorResult result = LinkDataInjector.linkData(targetList, sourceList, "statusId");

        // Verify status texts
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "0.status"), "progress");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.status"), "ready");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "2.status"), "done");
    }


    // --
    // Test generic injection
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up list for modification
        List<Map<String, Object>> originalList = Arrays.asList(
                InjectorUtil.initMap(
                        "name", "John Doe",
                        "statusId", "1"
                ),
                InjectorUtil.initMap(
                        "name", "Jack the Joker",
                        "statusId", "0"
                ),
                InjectorUtil.initMap(
                        "name", "Mary-Anne Adams",
                        "statusId", "2"
                )
        );

        // Set up list for the data source
        List<Map<String, Object>> dataSource = Arrays.asList(
                InjectorUtil.initMap(
                        "statusId", "0",
                        "status", "ready"
                ),
                InjectorUtil.initMap(
                        "statusId", "1",
                        "status", "progress"
                ),
                InjectorUtil.initMap(
                        "statusId", "2",
                        "status", "done"
                )
        );

        // Set up injector
        LinkDataInjector injector = new LinkDataInjector();
        injector.setLinkKey("statusId");

        // Apply
        InjectorResult result = injector.apply(originalList, dataSource);

        // Verify values
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "0.status"), "progress");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.status"), "ready");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "2.status"), "done");
    }
}
