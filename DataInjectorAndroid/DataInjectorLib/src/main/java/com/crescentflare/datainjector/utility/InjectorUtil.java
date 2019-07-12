package com.crescentflare.datainjector.utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Data injector utility: utilities for data injection
 * Utilities to easily initialize maps
 */
public class InjectorUtil
{
    // --
    // Initialization
    // --

    private InjectorUtil()
    {
    }


    // --
    // Map
    // --

    public static Map<String, Object> initMap(InjectorMapEntry<?, ?>... items)
    {
        Map<String, Object> map = new HashMap<>();
        for (InjectorMapEntry<?, ?> item : items)
        {
            if (item.getKey() instanceof String)
            {
                map.put((String)item.getKey(), item.getValue());
            }
        }
        return map;
    }

    public static Map<String, Object> initMap(Object... itemSets)
    {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < itemSets.length; i += 2)
        {
            if (itemSets[i] instanceof String)
            {
                map.put((String)itemSets[i], itemSets[i + 1]);
            }
        }
        return map;
    }
}
