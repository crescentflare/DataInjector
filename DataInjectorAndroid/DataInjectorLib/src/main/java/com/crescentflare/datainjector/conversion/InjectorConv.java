package com.crescentflare.datainjector.conversion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Data injector utility: data type conversion
 * Easily recognize and convert between data types
 */
public class InjectorConv
{
    // --
    // Initialization
    // --

    private InjectorConv()
    {
    }


    // --
    // Array list conversion
    // --

    public static List<String> asStringList(Object value)
    {
        List<String> list = new ArrayList<>();
        if (value instanceof List)
        {
            List<?> valueList = (List<?>)value;
            for (int i = 0; i < valueList.size(); i++)
            {
                String result = asString(valueList.get(i));
                if (result != null)
                {
                    list.add(result);
                }
            }
        }
        return list;
    }

    public static List<Double> asDoubleList(Object value)
    {
        List<Double> list = new ArrayList<>();
        if (value instanceof List)
        {
            List<?> valueList = (List<?>)value;
            for (int i = 0; i < valueList.size(); i++)
            {
                Double result = asDouble(valueList.get(i));
                if (result != null)
                {
                    list.add(result);
                }
            }
        }
        return list;
    }

    public static List<Float> asFloatList(Object value)
    {
        List<Float> list = new ArrayList<>();
        if (value instanceof List)
        {
            List<?> valueList = (List<?>)value;
            for (int i = 0; i < valueList.size(); i++)
            {
                Float result = asFloat(valueList.get(i));
                if (result != null)
                {
                    list.add(result);
                }
            }
        }
        return list;
    }

    public static List<Integer> asIntegerList(Object value)
    {
        List<Integer> list = new ArrayList<>();
        if (value instanceof List)
        {
            List<?> valueList = (List<?>)value;
            for (int i = 0; i < valueList.size(); i++)
            {
                Integer result = asInteger(valueList.get(i));
                if (result != null)
                {
                    list.add(result);
                }
            }
        }
        return list;
    }

    public static List<Boolean> asBooleanList(Object value)
    {
        List<Boolean> list = new ArrayList<>();
        if (value instanceof List)
        {
            List<?> valueList = (List<?>)value;
            for (int i = 0; i < valueList.size(); i++)
            {
                Boolean result = asBoolean(valueList.get(i));
                if (result != null)
                {
                    list.add(result);
                }
            }
        }
        return list;
    }


    // --
    // Special list and map conversion
    // --

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> asStringObjectMapList(Object object)
    {
        List<?> objectList = asObjectList(object);
        if (objectList != null)
        {
            if (objectList.size() > 0 && asStringObjectMap(objectList.get(0)) == null)
            {
                return null;
            }
            return (List<Map<String, Object>>)objectList;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asStringObjectMap(Object value)
    {
        boolean isMap = value instanceof Map<?, ?>;
        if (isMap)
        {
            Map<?, ?> map = (Map<?, ?>)value;
            if (map.keySet().size() > 0)
            {
                Object firstKey = map.keySet().iterator().next();
                if (!(firstKey instanceof String))
                {
                    return null;
                }
            }
            return (Map<String, Object>)value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> asObjectList(Object value)
    {
        boolean isList = value instanceof List<?>;
        return isList ? (List<Object>)value : null;
    }


    // --
    // Date parsing
    // --

    public static List<Date> asDateList(Object value)
    {
        List<Date> list = new ArrayList<>();
        if (value instanceof List)
        {
            List<?> valueList = (List<?>)value;
            for (int i = 0; i < valueList.size(); i++)
            {
                Date result = asDate(valueList.get(i));
                if (result != null)
                {
                    list.add(result);
                }
            }
        }
        return list;
    }

    public static Date asDate(Object value)
    {
        if (value instanceof String)
        {
            String stringDate = (String)value;
            List<String> formatterList = new ArrayList<>(Arrays.asList(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    "yyyy-MM-dd'T'HH:mm:ssX",
                    "yyyy-MM-dd'T'HH:mm:ssZ",
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd"
            ));
            for (String formatter : formatterList)
            {
                try
                {
                    DateFormat dateFormatter = new SimpleDateFormat(formatter, Locale.US);
                    if (formatter.endsWith("'Z'") || formatter.endsWith("Z") || formatter.endsWith("X"))
                    {
                        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    }
                    return dateFormatter.parse(stringDate);
                }
                catch (Exception ignored)
                {
                }
            }
        }
        return null;
    }


    // --
    // Primitive types
    // --

    public static String asString(Object value)
    {
        if (value instanceof String)
        {
            return (String)value;
        }
        else if (value instanceof Double || value instanceof Float || value instanceof Integer || value instanceof Boolean)
        {
            return value.toString();
        }
        return null;
    }

    public static Double asDouble(Object value)
    {
        if (value instanceof String)
        {
            try
            {
                return Double.parseDouble((String)value);
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        else if (value instanceof Double)
        {
            return (Double)value;
        }
        else if (value instanceof Float)
        {
            return ((Float)value).doubleValue();
        }
        else if (value instanceof Integer)
        {
            return ((Integer)value).doubleValue();
        }
        else if (value instanceof Boolean)
        {
            return ((Boolean)value) ? 1.0 : 0.0;
        }
        return null;
    }

    public static Float asFloat(Object value)
    {
        if (value instanceof String)
        {
            try
            {
                return Float.parseFloat((String)value);
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        else if (value instanceof Double)
        {
            return ((Double)value).floatValue();
        }
        else if (value instanceof Float)
        {
            return (Float)value;
        }
        else if (value instanceof Integer)
        {
            return ((Integer)value).floatValue();
        }
        else if (value instanceof Boolean)
        {
            return ((Boolean)value) ? 1.0f : 0.0f;
        }
        return null;
    }

    public static Integer asInteger(Object value)
    {
        if (value instanceof String)
        {
            try
            {
                return Integer.parseInt((String)value);
            }
            catch (IllegalArgumentException ignored)
            {
            }
            try
            {
                Double result = Double.parseDouble((String)value);
                return result.intValue();
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        else if (value instanceof Double)
        {
            return ((Double)value).intValue();
        }
        else if (value instanceof Float)
        {
            return ((Float)value).intValue();
        }
        else if (value instanceof Integer)
        {
            return (Integer)value;
        }
        else if (value instanceof Boolean)
        {
            return ((Boolean)value) ? 1 : 0;
        }
        return null;
    }

    public static Boolean asBoolean(Object value)
    {
        if (value instanceof String)
        {
            try
            {
                return Boolean.parseBoolean((String)value);
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        else if (value instanceof Double)
        {
            return (Double)value > 0;
        }
        else if (value instanceof Float)
        {
            return (Float)value > 0;
        }
        else if (value instanceof Integer)
        {
            return (Integer)value > 0;
        }
        else if (value instanceof Boolean)
        {
            return (Boolean)value;
        }
        return null;
    }
}
