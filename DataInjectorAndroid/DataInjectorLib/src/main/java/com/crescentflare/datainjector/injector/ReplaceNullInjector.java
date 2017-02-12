package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.utility.InjectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data injector: remove or replace null values
 * Traverses a data set recursively removing null values or replacing them with the given defaults
 */
public class ReplaceNullInjector extends DataInjector
{
    // ---
    // Initialization
    // ---

    public ReplaceNullInjector()
    {
    }


    // ---
    // Manual injection
    // ---

    public static void filterNull(Object targetData)
    {
        replaceNull(targetData, null, false);
    }

    public static void replaceNull(Object targetData, Object replaceData)
    {
        replaceNull(targetData, replaceData, false);
    }

    public static void replaceNull(Object targetData, Object replaceData, boolean ignoreNotExisting)
    {
        Map<String, Object> targetMap = InjectorUtil.asStringObjectMap(targetData);
        List<Object> targetList = InjectorUtil.asObjectList(targetData);
        if (targetMap != null)
        {
            processMap(targetMap, InjectorUtil.asStringObjectMap(replaceData), ignoreNotExisting);
        }
        else if (targetList != null)
        {
            processList(targetList, InjectorUtil.asObjectList(replaceData), ignoreNotExisting);
        }
    }


    // ---
    // General injection
    // ---

    @Override
    public void onApply(Object targetData, Object subTargetData, Object referencedData, Object subReferencedData)
    {
        replaceNull(targetData, referencedData);
    }


    // ---
    // Dependencies
    // ---

    public List<String> findDependencies()
    {
        return new ArrayList<>();
    }


    // ---
    // Internal data processing
    // ---

    private static void processList(List<Object> array, List<Object> replaceArray, boolean ignoreNotExisting)
    {
        List<Object> modifiedList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++)
        {
            Object arrayItem = array.get(i);
            Map<String, Object> targetMap = InjectorUtil.asStringObjectMap(arrayItem);
            List<Object> targetList = InjectorUtil.asObjectList(arrayItem);
            if (targetMap != null)
            {
                Map<String, Object> supplyReplaceMap = null;
                if (replaceArray != null && i < replaceArray.size())
                {
                    supplyReplaceMap = InjectorUtil.asStringObjectMap(replaceArray.get(i));
                }
                processMap(targetMap, supplyReplaceMap, ignoreNotExisting);
                modifiedList.add(targetMap);
            }
            else if (targetList != null)
            {
                List<Object> supplyReplaceArray = null;
                if (replaceArray != null && i < replaceArray.size())
                {
                    supplyReplaceArray = InjectorUtil.asObjectList(replaceArray.get(i));
                }
                processList(targetList, supplyReplaceArray, ignoreNotExisting);
                modifiedList.add(targetList);
            }
            else if (arrayItem != null)
            {
                modifiedList.add(arrayItem);
            }
            else if (replaceArray != null && i < replaceArray.size() && replaceArray.get(i) != null)
            {
                modifiedList.add(replaceArray.get(i));
            }
        }
        if (!ignoreNotExisting && replaceArray != null && replaceArray.size() > array.size())
        {
            for (int i = array.size(); i < replaceArray.size(); i++)
            {
                if (replaceArray.get(i) != null)
                {
                    modifiedList.add(replaceArray.get(i));
                }
            }
        }
        array.clear();
        array.addAll(modifiedList);
    }

    private static void processMap(Map<String, Object> map, Map<String, Object> replaceMap, boolean ignoreNotExisting)
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
                Map<String, Object> supplyReplaceMap = null;
                if (replaceMap != null)
                {
                    supplyReplaceMap = InjectorUtil.asStringObjectMap(replaceMap.get(key));
                }
                processMap(mapValue, supplyReplaceMap, ignoreNotExisting);
            }
            else if (listValue != null)
            {
                List<Object> supplyReplaceArray = null;
                if (replaceMap != null)
                {
                    supplyReplaceArray = InjectorUtil.asObjectList(replaceMap.get(key));
                }
                processList(listValue, supplyReplaceArray, ignoreNotExisting);
            }
            else if (value == null)
            {
                if (replaceMap != null && replaceMap.containsKey(key) && replaceMap.get(key) != null)
                {
                    map.put(key, replaceMap.get(key));
                }
                else
                {
                    map.remove(key);
                }
            }
        }
        if (!ignoreNotExisting && replaceMap != null)
        {
            for (String key : replaceMap.keySet())
            {
                if (!map.containsKey(key) && replaceMap.get(key) != null)
                {
                    map.put(key, replaceMap.get(key));
                }
            }
        }
    }
}
