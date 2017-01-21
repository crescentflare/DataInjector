package com.crescentflare.datainjector.utility;

import com.crescentflare.datainjector.conversion.InjectorConv;

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
    // Dynamic map/array access
    // ---

    public static Object itemFromObject(Object object, String path)
    {
        Map<String, Object> map = asStringObjectMap(object);
        List<Object> list = asObjectList(object);
        if (map != null)
        {
            return itemFromMap(map, path);
        }
        if (list != null)
        {
            return itemFromList(list, path);
        }
        return null;
    }

    public static Object itemFromObject(Object object, String path, char separator)
    {
        Map<String, Object> map = asStringObjectMap(object);
        List<Object> list = asObjectList(object);
        if (map != null)
        {
            return itemFromMap(map, path, separator);
        }
        if (list != null)
        {
            return itemFromList(list, path, separator);
        }
        return null;
    }

    public static Object itemFromObject(Object object, String[] path)
    {
        Map<String, Object> map = asStringObjectMap(object);
        List<Object> list = asObjectList(object);
        if (map != null)
        {
            return itemFromMap(map, path);
        }
        if (list != null)
        {
            return itemFromList(list, path);
        }
        return null;
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

    public static void setItemOnMap(Map<String, Object> map, String path, Object value)
    {
        if (path == null)
        {
            return;
        }
        setItemOnMap(map, path.split("\\."), value);
    }

    public static void setItemOnMap(Map<String, Object> map, String path, char separator, Object value)
    {
        if (path == null)
        {
            return;
        }
        if (separator == '.')
        {
            setItemOnMap(map, path.split("\\" + separator), value);
        }
        else
        {
            setItemOnMap(map, path.split("" + separator), value);
        }
    }

    public static void setItemOnMap(Map<String, Object> map, String[] path, Object value)
    {
        if (map == null || path == null)
        {
            return;
        }
        if (path.length > 0)
        {
            String key = path[0];
            if (path.length > 1)
            {
                String[] nextPath = slicedPath(path, 1);
                Map<String, Object> modifyMap = asStringObjectMap(map.get(key));
                List<Object> modifyArray = asObjectList(map.get(key));
                if (modifyMap != null)
                {
                    setItemOnMap(modifyMap, nextPath, value);
                }
                else if (modifyArray != null)
                {
                    setItemOnList(modifyArray, nextPath, value);
                }
            }
            else
            {
                if (value == null)
                {
                    map.remove(key);
                }
                else
                {
                    map.put(key, value);
                }
            }
        }
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
            if (path[0].startsWith("@"))
            {
                index = findInjectRef(list, path[0].substring(1));
            }
            else
            {
                try
                {
                    index = Integer.parseInt(path[0]);
                }
                catch (NumberFormatException ignored)
                {
                }
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

    public static void setItemOnList(List<Object> list, String path, Object value)
    {
        if (path == null)
        {
            return;
        }
        setItemOnList(list, path.split("\\."), value);
    }

    public static void setItemOnList(List<Object> list, String path, char separator, Object value)
    {
        if (path == null)
        {
            return;
        }
        if (separator == '.')
        {
            setItemOnList(list, path.split("\\" + separator), value);
        }
        else
        {
            setItemOnList(list, path.split("" + separator), value);
        }
    }

    public static void setItemOnList(List<Object> list, String[] path, Object value)
    {
        if (list == null || path == null)
        {
            return;
        }
        if (path.length > 0)
        {
            int index = -1;
            if (path[0].startsWith("@"))
            {
                index = findInjectRef(list, path[0].substring(1));
            }
            else
            {
                try
                {
                    index = Integer.parseInt(path[0]);
                }
                catch (NumberFormatException ignored)
                {
                }
            }
            if (index >= 0 && index < list.size())
            {
                if (path.length > 1)
                {
                    String[] nextPath = slicedPath(path, 1);
                    Map<String, Object> modifyMap = asStringObjectMap(list.get(index));
                    List<Object> modifyArray = asObjectList(list.get(index));
                    if (modifyMap != null)
                    {
                        setItemOnMap(modifyMap, nextPath, value);
                    }
                    else if (modifyArray != null)
                    {
                        setItemOnList(modifyArray, nextPath, value);
                    }
                }
                else
                {
                    if (value == null)
                    {
                        list.remove(index);
                    }
                    else
                    {
                        list.set(index, value);
                    }
                }
            }
        }
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

    private static int findInjectRef(List<Object> array, String find)
    {
        for (int i = 0; i < array.size(); i++)
        {
            Map<String, Object> itemMap = asStringObjectMap(array.get(i));
            if (itemMap != null)
            {
                String injectRef = InjectorConv.toString(itemMap.get("injectRef"));
                if (injectRef != null && injectRef.equals(find))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    private static String[] slicedPath(String[] inArr, int startPos)
    {
        String[] slice = new String[inArr.length - startPos];
        slice = Arrays.asList(inArr).subList(startPos, inArr.length).toArray(slice);
        return slice;
    }
}
