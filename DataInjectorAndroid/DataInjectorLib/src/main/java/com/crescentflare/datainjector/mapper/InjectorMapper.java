package com.crescentflare.datainjector.mapper;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Data injector mapper: a mapping utility
 * Contains a list of mapping values and matches one of them based on a given value
 */
public class InjectorMapper
{
    // ---
    // Members
    // ---

    private Map<String, MappingObject> mappingTable = new HashMap<>();
    private boolean initFail = false;


    // ---
    // Initialization
    // ---

    public InjectorMapper(String mapping, Map<String, Object> fullRefData, Map<String, Object> subRefData)
    {
        // Parse away brackets if needed
        if (mapping.startsWith("["))
        {
            int endBracket = mapping.lastIndexOf("]");
            if (endBracket > 0)
            {
                mapping = mapping.substring(1, endBracket);
            }
            else
            {
                initFail = true;
                return;
            }
        }

        // Split mappings
        String mappingItems[] = mapping.split(",");
        if (mappingItems.length == 0)
        {
            initFail = true;
            return;
        }
        for (String mappingItem : mappingItems)
        {
            String[] mappingSet = mappingItem.split("->");
            if (mappingSet.length == 2)
            {
                String mapFrom = mappingSet[0];
                MappingObject mapTo = new MappingObject(mappingSet[1], fullRefData, subRefData);
                if (mapTo.getType() != InjectorDataType.Unknown)
                {
                    mappingTable.put(mapFrom, mapTo);
                }
                else
                {
                    initFail = true;
                    return;
                }
            }
            else
            {
                initFail = true;
                return;
            }
        }
    }


    // ---
    // Fetch mapped value
    // ---

    public Object obtainMapping(Object item)
    {
        if (!initFail && item != null)
        {
            MappingObject result = mappingTable.get(item.toString());
            if (result != null)
            {
                return result.getValue();
            }
            return mappingTable.get("else").getValue();
        }
        return null;
    }


    // ---
    // A mapping object
    // ---

    private static class MappingObject
    {
        private InjectorDataType type = InjectorDataType.Unknown;
        private Object value;

        public MappingObject(Object value, Map<String, Object> fullRefData, Map<String, Object> subRefData)
        {
            boolean detecting = true;
            while (detecting && value != null)
            {
                detecting = false;
                type = InjectorDataType.detectFromObject(value);
                switch (type)
                {
                    case String:
                        this.value = InjectorConv.toString(value);
                        break;
                    case Number:
                        this.value = InjectorConv.toInteger(value);
                        break;
                    case DecimalNumber:
                        this.value = InjectorConv.toDouble(value);
                        break;
                    case Boolean:
                        this.value = InjectorConv.toBoolean(value);
                        break;
                    case Reference:
                        type = InjectorDataType.Unknown;
                        if (fullRefData != null)
                        {
                            value = InjectorUtil.itemFromMap(fullRefData, ((String)value).substring(2));
                            detecting = true;
                        }
                        break;
                    case SubReference:
                        type = InjectorDataType.Unknown;
                        if (subRefData != null)
                        {
                            value = InjectorUtil.itemFromMap(subRefData, ((String)value).substring(1));
                            detecting = true;
                        }
                        break;
                }
            }
        }

        public InjectorDataType getType()
        {
            return type;
        }

        public Object getValue()
        {
            return value;
        }
    }
}
