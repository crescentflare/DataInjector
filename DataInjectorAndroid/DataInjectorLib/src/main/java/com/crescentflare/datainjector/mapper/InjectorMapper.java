package com.crescentflare.datainjector.mapper;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorDataDetector;
import com.crescentflare.datainjector.utility.InjectorDataType;
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

    private Map<Object, Object> mappingTable = new HashMap<>();
    private Map<String, Object> fullRefData = null;
    private Map<String, Object> subRefData = null;
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

        // Loop through mapping and find items
        int curPos = 0;
        boolean findingData = true;
        while (findingData)
        {
            // Reset state
            findingData = false;
            curPos = findNonSpace(mapping, curPos);

            // Find the item type for the mapping key
            InjectorDataType keyType = InjectorDataDetector.detectFromString(mapping, curPos);
            int endPos = InjectorDataDetector.endOfTypeTypeString(keyType, mapping, curPos);
            if (endPos < 0)
            {
                endPos = findAssignmentSeparator(mapping, curPos);
            }
            if (endPos < 0)
            {
                break;
            }
            Object keyObject = obtainObject(keyType, mapping, curPos, endPos, fullRefData, subRefData);

            // Find the item type for the mapping value
            curPos = findAssignmentSeparator(mapping, endPos) + 2;
            curPos = findNonSpace(mapping, curPos);
            InjectorDataType valueType = InjectorDataDetector.detectFromString(mapping, curPos);
            endPos = InjectorDataDetector.endOfTypeTypeString(valueType, mapping, curPos);
            if (endPos < 0)
            {
                endPos = findDividerSeparator(mapping, curPos);
            }
            if (endPos < 0)
            {
                break;
            }
            Object valueObject = obtainObject(valueType, mapping, curPos, endPos, fullRefData, subRefData);

            // Add the value
            if (keyObject != null && valueObject != null)
            {
                mappingTable.put(keyObject, valueObject);
                endPos = findDividerSeparator(mapping, curPos);
                if (endPos < mapping.length())
                {
                    curPos = endPos + 1;
                    findingData = true;
                }
            }
            else
            {
                initFail = true;
                return;
            }
        }
        if (mappingTable.keySet().size() == 0)
        {
            initFail = true;
        }
        else
        {
            this.fullRefData = fullRefData;
            this.subRefData = subRefData;
        }
    }


    // ---
    // Fetch mapped value
    // ---

    public Object obtainMapping(Object item)
    {
        if (!initFail && item != null)
        {
            InjectorDataType itemType = InjectorDataDetector.detectFromObject(item);
            if (itemType == InjectorDataType.Reference || itemType == InjectorDataType.SubReference)
            {
                item = obtainConvertedObject(itemType, InjectorConv.toString(item), fullRefData, subRefData);
                if (item == null)
                {
                    return null;
                }
            }
            for (Object key : mappingTable.keySet())
            {
                if (key instanceof String)
                {
                    if (key.equals(InjectorConv.toString(item)))
                    {
                        return mappingTable.get(key);
                    }
                }
                else if (key instanceof Double)
                {
                    if (key.equals(InjectorConv.toDouble(item)))
                    {
                        return mappingTable.get(key);
                    }
                }
                else if (key instanceof Integer)
                {
                    if (key.equals(InjectorConv.toInteger(item)))
                    {
                        return mappingTable.get(key);
                    }
                }
                else if (key instanceof Boolean)
                {
                    if (key.equals(InjectorConv.toBoolean(item)))
                    {
                        return mappingTable.get(key);
                    }
                }
            }
            return mappingTable.get("else");
        }
        return null;
    }


    // ---
    // Helper
    // ---

    private Object obtainObject(InjectorDataType type, String mapping, int start, int end, Map<String, Object> fullRefData, Map<String, Object> subRefData)
    {
        String item = mapping.substring(start, end).trim();
        if (type == InjectorDataType.String)
        {
            char quoteChr = item.charAt(0);
            if (quoteChr == '\'' || quoteChr == '"')
            {
                if (item.endsWith("" + quoteChr))
                {
                    item = item.substring(1, item.length() - 1);
                }
            }
        }
        return obtainConvertedObject(type, item, fullRefData, subRefData);
    }

    private Object obtainConvertedObject(InjectorDataType type, String item, Map<String, Object> fullRefData, Map<String, Object> subRefData)
    {
        switch (type)
        {
            case String:
                return item;
            case Number:
                return InjectorConv.toInteger(item);
            case DecimalNumber:
                return InjectorConv.toDouble(item);
            case Boolean:
                return InjectorConv.toBoolean(item);
            case Reference:
                if (fullRefData != null)
                {
                    return InjectorUtil.itemFromMap(fullRefData, item.substring(1));
                }
                break;
            case SubReference:
                if (fullRefData != null)
                {
                    return InjectorUtil.itemFromMap(subRefData, item.substring(2));
                }
                break;
        }
        return null;
    }

    private int findNonSpace(String string, int start)
    {
        int len = string.length();
        for (int i = start; i < len; i++)
        {
            if (string.charAt(i) != ' ')
            {
                return i;
            }
        }
        return -1;
    }

    private int findAssignmentSeparator(String string, int start)
    {
        int len = string.length();
        for (int i = start; i < len; i++)
        {
            if (string.charAt(i) == '-' && i + 1 < len && string.charAt(i + 1) == '>')
            {
                return i;
            }
        }
        return -1;
    }

    private int findDividerSeparator(String string, int start)
    {
        int len = string.length();
        for (int i = start; i < len; i++)
        {
            if (string.charAt(i) == ',')
            {
                return i;
            }
        }
        return len;
    }
}
