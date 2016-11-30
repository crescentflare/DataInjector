package com.crescentflare.datainjector.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Data injector utility: utilities for data injection
 * Utilities to access or change values within data sets (like maps)
 */
public class InjectorUtil
{
    // ---
    // Initialization
    // ---

    private InjectorUtil()
    {
    }


    // ---
    // Map
    // ---

    public static Object itemFromMap(Map<String, Object> map, String path)
    {
        if (path == null)
        {
            return null;
        }
        return itemFromMap(map, path.split("\\."));
    }

    public static Object itemFromMap(Map<String, Object> map, String path, char separator)
    {
        if (path == null)
        {
            return null;
        }
        if (separator == '.')
        {
            return itemFromMap(map, path.split("\\" + separator));
        }
        return itemFromMap(map, path.split("" + separator));
    }

    public static Object itemFromMap(Map<String, Object> map, String[] path)
    {
        if (map == null || path == null)
        {
            return null;
        }
        if (path.length > 0)
        {
            Object checkObject = map.get(path[0]);
            if (path.length < 2)
            {
                return checkObject;
            }
            Map<String, Object> checkMap = asStringObjectMap(checkObject);
            List<Object> checkList = asObjectList(checkObject);
            if (checkMap != null)
            {
                return itemFromMap(checkMap, slicedPath(path, 1));
            }
            else if (checkList != null)
            {
                return itemFromList(checkList, slicedPath(path, 1));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asStringObjectMap(Object object)
    {
        if (isMap(object))
        {
            Map<?, ?> map = (Map<?, ?>)object;
            if (map.keySet().size() > 0)
            {
                Object firstKey = map.keySet().iterator().next();
                if (!(firstKey instanceof String))
                {
                    return null;
                }
            }
            return (Map<String, Object>)object;
        }
        return null;
    }

    public static boolean isMap(Object object)
    {
        return object != null && object instanceof Map<?, ?>;
    }

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


    // ---
    // Array
    // ---

    public static Object itemFromList(List<Object> list, String path)
    {
        if (path == null)
        {
            return null;
        }
        return itemFromList(list, path.split("\\."));
    }

    public static Object itemFromList(List<Object> list, String path, char separator)
    {
        if (path == null)
        {
            return null;
        }
        if (separator == '.')
        {
            return itemFromList(list, path.split("\\" + separator));
        }
        return itemFromList(list, path.split("" + separator));
    }

    public static Object itemFromList(List<Object> list, String[] path)
    {
        if (path.length > 0)
        {
            int index = -1;
            try
            {
                index = Integer.parseInt(path[0]);
            }
            catch (NumberFormatException ignored)
            {
            }
            if (index >= 0 && index < list.size())
            {
                Object checkObject = list.get(index);
                if (path.length < 2)
                {
                    return checkObject;
                }
                Map<String, Object> checkMap = asStringObjectMap(checkObject);
                List<Object> checkList = asObjectList(checkObject);
                if (checkMap != null)
                {
                    return itemFromMap(checkMap, slicedPath(path, 1));
                }
                else if (checkList != null)
                {
                    return itemFromList(checkList, slicedPath(path, 1));
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> asObjectList(Object object)
    {
        return isList(object) ? (List<Object>)object : null;
    }

    public static boolean isList(Object object)
    {
        return object != null && object instanceof List<?>;
    }


    // ---
    // Helper
    // ---

    private static String[] slicedPath(String[] inArr, int startPos)
    {
        String[] slice = new String[inArr.length - startPos];
        slice = Arrays.asList(inArr).subList(startPos, inArr.length).toArray(slice);
        return slice;
    }
}
