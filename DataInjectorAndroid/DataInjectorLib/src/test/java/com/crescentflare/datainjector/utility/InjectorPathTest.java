package com.crescentflare.datainjector.utility;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * Utility test: data injector path
 */
public class InjectorPathTest
{
    // --
    // Test initialization
    // --

    @Test
    public void init() throws Exception
    {
        // Create and test through string
        InjectorPath path = new InjectorPath("");
        Assert.assertEquals(path.toString(), "");
        path = new InjectorPath("item.subItem");
        Assert.assertEquals(path.toString("."), "item.subItem");
        path = new InjectorPath("item/subItem", "/");
        Assert.assertEquals(path.toString("/"), "item/subItem");

        // Create and test through components
        path = new InjectorPath(new String[]{ "item", "subItem" });
        Assert.assertEquals(path.toString("."), "item.subItem");
    }


    // --
    // Test data access
    // --

    @Test
    public void elements() throws Exception
    {
        // Case with a simple nested path
        InjectorPath path = new InjectorPath("item.subItem");
        Assert.assertTrue(path.hasElements());
        Assert.assertEquals(path.firstElement(), "item");
        Assert.assertTrue(path.hasNextElement());
        Assert.assertEquals(path.nextElement(), "subItem");

        // Case with only a single path
        path = new InjectorPath("item");
        Assert.assertTrue(path.hasElements());
        Assert.assertEquals(path.firstElement(), "item");
        Assert.assertFalse(path.hasNextElement());
        Assert.assertNull(path.nextElement());

        // Case with an empty path
        path = new InjectorPath();
        Assert.assertFalse(path.hasElements());
        Assert.assertNull(path.firstElement());
        Assert.assertFalse(path.hasNextElement());
        Assert.assertNull(path.nextElement());

        // Case with a deeper path
        path = new InjectorPath("item.subItem.deeperItem");
        Assert.assertTrue(path.hasElements());
        Assert.assertEquals(path.firstElement(), "item");
        Assert.assertTrue(path.hasNextElement());
        Assert.assertEquals(path.nextElement(), "subItem");
    }


    // --
    // Test traversal
    // --

    @Test
    public void deeperPath() throws Exception
    {
        InjectorPath path = new InjectorPath("item.subItem.deeperItem");
        path = path.deeperPath();
        Assert.assertEquals(path.toString(), "subItem.deeperItem");
    }

    @Test
    public void seekPathForMap() throws Exception
    {
        // Set up mixed map
        Map<String, Object> mixedMap = InjectorUtil.initMap(
                "furniture", Arrays.asList(
                        InjectorUtil.initMap(
                                "$marker", "chair",
                                "type", "chair",
                                "price", "51.95"
                        ),
                        InjectorUtil.initMap(
                                "type", "table",
                                "price", "132.95"
                        )
                ),
                "lighting", Arrays.asList(
                        InjectorUtil.initMap(
                                "type", "lightBulb",
                                "price", "9.95"
                        ),
                        InjectorUtil.initMap(
                                "$marker", "sensor",
                                "type", "sensor",
                                "price", "19.95"
                        )
                )
        );

        // Check if the path can be indexed to the marker items
        Assert.assertEquals(InjectorPath.seekPathForMap(mixedMap, "$marker", "chair").toString(), "furniture.0");
        Assert.assertEquals(InjectorPath.seekPathForMap(mixedMap, "$marker", "sensor").toString(), "lighting.1");
    }
}
