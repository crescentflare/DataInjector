package com.crescentflare.datainjector.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data injector utility: detect data types
 * Determine a data type from an object or string
 */
public class InjectorDataDetector
{
    // ---
    // Initialization
    // ---

    private InjectorDataDetector()
    {
    }


    // ---
    // Detection
    // ---

    public static InjectorDataType detectFromObject(Object object)
    {
        if (object instanceof String)
        {
            return detectFromString((String)object);
        }
        else if (object instanceof Double || object instanceof Float)
        {
            return InjectorDataType.DecimalNumber;
        }
        else if (object instanceof Integer)
        {
            return InjectorDataType.Number;
        }
        else if (object instanceof Boolean)
        {
            return InjectorDataType.Boolean;
        }
        else if (object == null)
        {
            return InjectorDataType.Empty;
        }
        return InjectorDataType.Unknown;
    }

    public static InjectorDataType detectFromString(String value)
    {
        return detectFromString(value, 0);
    }

    public static InjectorDataType detectFromString(String value, int start)
    {
        if (start < 0)
        {
            return InjectorDataType.Unknown;
        }
        if (value.startsWith("'", start) || value.startsWith("\"", start))
        {
            return InjectorDataType.String;
        }
        else if (value.startsWith("@.", start))
        {
            return InjectorDataType.SubReference;
        }
        else if (value.startsWith("@", start))
        {
            return InjectorDataType.Reference;
        }
        else if (value.startsWith("true", start) || value.startsWith("false", start))
        {
            return InjectorDataType.Boolean;
        }
        else if (value.startsWith("empty", start))
        {
            return InjectorDataType.Empty;
        }
        else if (value.length() > start && value.charAt(start) >= '0' && value.charAt(start) <= '9')
        {
            return containsDot(value) ? InjectorDataType.DecimalNumber : InjectorDataType.Number;
        }
        else if (value.length() > start + 1 && value.charAt(start) == '-' && value.charAt(start + 1) >= '0' && value.charAt(start + 1) <= '9')
        {
            return containsDot(value) ? InjectorDataType.DecimalNumber : InjectorDataType.Number;
        }
        return InjectorDataType.String;
    }

    public static int endOfTypeTypeString(InjectorDataType type, String value)
    {
        return endOfTypeTypeString(type, value, 0);
    }

    public static int endOfTypeTypeString(InjectorDataType type, String value, int start)
    {
        if (start < 0)
        {
            return -1;
        }
        if (type == InjectorDataType.String && (value.startsWith("'", start) || value.startsWith("\"", start)))
        {
            char findEndChar = value.charAt(start);
            int len = value.length();
            for (int i = start + 1; i < len; i++)
            {
                if (value.charAt(i) == findEndChar)
                {
                    return i + 1;
                }
            }
        }
        else if (type == InjectorDataType.Number || type == InjectorDataType.DecimalNumber)
        {
            int len = value.length();
            for (int i = start + 1; i < len; i++)
            {
                char chr = value.charAt(i);
                if (!(chr >= '0' && chr <= '9') && chr != '.')
                {
                    return i;
                }
            }
        }
        else if (type == InjectorDataType.Boolean)
        {
            if (value.startsWith("true", start))
            {
                return start + 4;
            }
            else if (value.startsWith("false", start))
            {
                return start + 5;
            }
        }
        else if (type == InjectorDataType.Empty)
        {
            return start + 5;
        }
        return -1;
    }


    // ---
    // Helper
    // ---

    private static boolean containsDot(String numberString)
    {
        int len = numberString.length();
        for (int i = 0; i < len; i++)
        {
            char chr = numberString.charAt(i);
            if (chr == '.')
            {
                return true;
            }
            if (!(chr >= '0' && chr <= '9') && chr != '-')
            {
                return false;
            }
        }
        return false;
    }
}
