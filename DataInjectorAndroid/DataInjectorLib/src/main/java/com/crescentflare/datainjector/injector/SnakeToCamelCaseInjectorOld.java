package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.conversion.InjectorConv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data injector: snake case conversion
 * Converts each entry key in the data set from snake case to camel case recursively
 */
public class SnakeToCamelCaseInjectorOld extends BaseInjectorOld
{
    // ---
    // Initialization
    // ---

    public SnakeToCamelCaseInjectorOld()
    {
    }


    // ---
    // Data helper
    // ---

    public static String stringToCamelCase(String snakeCaseString)
    {
        String[] splitString = snakeCaseString.split("_");
        if (splitString.length > 1)
        {
            String resultString = splitString[0];
            for (int i = 1; i < splitString.length; i++)
            {
                resultString += splitString[i].substring(0, 1).toUpperCase() + splitString[i].substring(1);
            }
            return resultString;
        }
        return snakeCaseString;
    }


    // ---
    // Manual injection
    // ---

    public static void changeToCamelCase(Object targetData)
    {
        Map<String, Object> targetMap = InjectorConv.asStringObjectMap(targetData);
        List<Object> targetList = InjectorConv.asObjectList(targetData);
        if (targetMap != null)
        {
            processMap(targetMap);
        }
        else if (targetList != null)
        {
            processList(targetList);
        }
    }


    // ---
    // General injection
    // ---

    @Override
    public void onApply(Object targetData, Object subTargetData, Object referencedData, Object subReferencedData)
    {
        changeToCamelCase(targetData);
    }


    // ---
    // Helper
    // ---

    private static void processList(List<Object> array)
    {
        for (Object arrayItem : array)
        {
            Map<String, Object> targetMap = InjectorConv.asStringObjectMap(arrayItem);
            List<Object> targetList = InjectorConv.asObjectList(arrayItem);
            if (targetMap != null)
            {
                processMap(targetMap);
            }
            else if (targetList != null)
            {
                processList(targetList);
            }
        }
    }

    private static void processMap(Map<String, Object> map)
    {
        List<String> keys = new ArrayList<>();
        for (String key : map.keySet())
        {
            keys.add(key);
        }
        for (String key : keys)
        {
            String newKey = stringToCamelCase(key);
            Object value = map.get(key);
            Map<String, Object> mapValue = InjectorConv.asStringObjectMap(value);
            List<Object> listValue = InjectorConv.asObjectList(value);
            boolean adjustKey = !newKey.equals(key);
            if (adjustKey)
            {
                map.remove(key);
            }
            if (mapValue != null)
            {
                processMap(mapValue);
                if (adjustKey)
                {
                    map.put(newKey, mapValue);
                }
            }
            else if (listValue != null)
            {
                processList(listValue);
                if (adjustKey)
                {
                    map.put(newKey, listValue);
                }
            }
            else if (adjustKey)
            {
                map.put(newKey, value);
            }
        }
    }
}
