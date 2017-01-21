package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.utility.InjectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data injector: remove null values
 * Traverses a data set recursively removing null values
 */
public class FilterNullInjector extends DataInjector
{
    // ---
    // Initialization
    // ---

    public FilterNullInjector()
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
        List<Object> modifiedList = new ArrayList<>();
        for (Object arrayItem : array)
        {
            Map<String, Object> targetMap = InjectorUtil.asStringObjectMap(arrayItem);
            List<Object> targetList = InjectorUtil.asObjectList(arrayItem);
            if (targetMap != null)
            {
                processMap(targetMap);
                modifiedList.add(targetMap);
            }
            else if (targetList != null)
            {
                processList(targetList);
                modifiedList.add(targetList);
            }
            else if (arrayItem != null)
            {
                modifiedList.add(arrayItem);
            }
        }
        array.clear();
        array.addAll(modifiedList);
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
            Object value = map.get(key);
            Map<String, Object> mapValue = InjectorUtil.asStringObjectMap(value);
            List<Object> listValue = InjectorUtil.asObjectList(value);
            if (mapValue != null)
            {
                processMap(mapValue);
            }
            else if (listValue != null)
            {
                processList(listValue);
            }
            else if (value == null)
            {
                map.remove(key);
            }
        }
    }
}
