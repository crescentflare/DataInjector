package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.conversion.InjectorConv;

import java.util.List;
import java.util.Map;

/**
 * Data injector: link datasets
 * Inject one data set into another by a common field that links them together (such as ID)
 */
public class LinkDataInjectorOld extends BaseInjectorOld
{
    // ---
    // Members
    // ---

    private String linkKey;


    // ---
    // Initialization
    // ---

    public LinkDataInjectorOld()
    {
    }

    public LinkDataInjectorOld(String linkKey)
    {
        this.linkKey = linkKey;
    }


    // ---
    // Data helpers
    // ---

    public static Map<String, Object> findDataItem(List<Map<String, Object>> targetData, Object value, String key)
    {
        if (targetData == null)
        {
            return null;
        }
        String searchValueString = InjectorConv.asString(value);
        if (searchValueString != null)
        {
            for (Map<String, Object> dataItem : targetData)
            {
                String compareValueString = InjectorConv.asString(dataItem.get(key));
                if (compareValueString != null)
                {
                    if (compareValueString.equals(searchValueString))
                    {
                        return dataItem;
                    }
                }
            }
        }
        return null;
    }


    // ---
    // Manual injection
    // ---

    public static void linkData(Map<String, Object> targetData, List<Map<String, Object>> linkData, String key)
    {
        if (targetData == null || key == null)
        {
            return;
        }
        Map<String, Object> foundItem = findDataItem(linkData, targetData.get(key), key);
        if (foundItem != null)
        {
            for (String itemKey : foundItem.keySet())
            {
                if (!itemKey.equals(key))
                {
                    targetData.put(itemKey, foundItem.get(itemKey));
                }
            }
        }
    }

    public static void linkDataArray(List<Map<String, Object>> targetData, List<Map<String, Object>> linkData, String key)
    {
        if (targetData == null)
        {
            return;
        }
        for (Map<String, Object> targetDataItem : targetData)
        {
            LinkDataInjectorOld.linkData(targetDataItem, linkData, key);
        }
    }


    // ---
    // General injection
    // ---

    @Override
    public void onApply(Object targetData, Object subTargetData, Object referencedData, Object subReferencedData)
    {
        if (linkKey != null)
        {
            List<Map<String, Object>> linkedData = InjectorConv.asStringObjectMapList(referencedData);
            if (linkedData != null)
            {
                List<Map<String, Object>> targetDataArray = InjectorConv.asStringObjectMapList(targetData);
                Map<String, Object> targetDataItem = InjectorConv.asStringObjectMap(targetData);
                if (targetDataArray != null)
                {
                    LinkDataInjectorOld.linkDataArray(targetDataArray, linkedData, linkKey);
                }
                else if (targetDataItem != null)
                {
                    LinkDataInjectorOld.linkData(targetDataItem, linkedData, linkKey);
                }
            }
        }
    }
}
