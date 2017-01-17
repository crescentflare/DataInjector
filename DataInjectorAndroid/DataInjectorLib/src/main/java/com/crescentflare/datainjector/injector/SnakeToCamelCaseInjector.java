package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.utility.InjectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data injector: snake case conversion
 * Converts each entry key in the data set from snake case to camel case recursively
 */
public class SnakeToCamelCaseInjector extends DataInjector
{
    // ---
    // Initialization
    // ---

    public SnakeToCamelCaseInjector()
    {
    }


    // ---
    // Injection
    // ---

    public void apply(Object targetData, Object referencedData, Object subReferencedData)
    {
        Map<String, Object> targetMap = InjectorUtil.asStringObjectMap(targetData);
        List<Object> targetList = InjectorUtil.asObjectList(targetData);
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
    // Dependencies
    // ---

    public List<String> findDependencies()
    {
        return new ArrayList<>();
    }


    // ---
    // Helper
    // ---

    private void processList(List<Object> array)
    {
        for (Object arrayItem : array)
        {
            Map<String, Object> targetMap = InjectorUtil.asStringObjectMap(arrayItem);
            List<Object> targetList = InjectorUtil.asObjectList(arrayItem);
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

    private void processMap(Map<String, Object> map)
    {
        List<String> keys = new ArrayList<>();
        for (String key : map.keySet())
        {
            keys.add(key);
        }
        for (String key : keys)
        {
            String[] splitKey = key.split("_");
            if (splitKey.length > 1)
            {
                Object value = map.get(key);
                String newKey = splitKey[0];
                for (int i = 1; i < splitKey.length; i++)
                {
                    newKey += splitKey[i].substring(0, 1).toUpperCase() + splitKey[i].substring(1);
                }
                map.remove(key);
                map.put(newKey, value);
            }
        }
    }
}
