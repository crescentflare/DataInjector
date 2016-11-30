package com.crescentflare.datainjector.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Data injector mapper: a data type
 * An enum to store the detected data types in string values for parsing
 */
public enum InjectorDataType
{
    Unknown,
    String,
    Number,
    DecimalNumber,
    Boolean,
    Reference,
    SubReference;

    public static InjectorDataType detectFromObject(Object object)
    {
        if (object instanceof String)
        {
            return detectFromString((String)object);
        }
        else if (object instanceof Double || object instanceof Float)
        {
            return DecimalNumber;
        }
        else if (object instanceof Integer)
        {
            return Number;
        }
        else if (object instanceof Boolean)
        {
            return Boolean;
        }
        return Unknown;
    }

    public static InjectorDataType detectFromString(String value)
    {
        if (value.startsWith("'") || value.startsWith("\""))
        {
            return String;
        }
        else if (value.startsWith("@."))
        {
            return SubReference;
        }
        else if (value.startsWith("@"))
        {
            return Reference;
        }
        else if (value.startsWith("true") || value.startsWith("false"))
        {
            return Boolean;
        }
        else if (value.length() > 0 && value.charAt(0) >= '0' && value.charAt(0) <= '9')
        {
            return containsDot(value) ? DecimalNumber : Number;
        }
        else if (value.length() > 2 && value.charAt(0) == '-' && value.charAt(1) >= '0' && value.charAt(1) <= '9')
        {
            return containsDot(value) ? DecimalNumber : Number;
        }
        return String;
    }

    private static boolean containsDot(String value)
    {
        int len = value.length();
        for (int i = 0; i < len; i++)
        {
            if (value.charAt(i) == '.')
            {
                return true;
            }
            if (!(value.charAt(i) >= '0' && value.charAt(i) <= '9') && value.charAt(i) != '-')
            {
                return false;
            }
        }
        return false;
    }
}
