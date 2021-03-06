package com.crescentflare.datainjector.conversion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    public static List<String> asStringList(@Nullable Object value)
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

    @NotNull
    public static List<Double> asDoubleList(@Nullable Object value)
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

    @NotNull
    public static List<Float> asFloatList(@Nullable Object value)
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

    @NotNull
    public static List<Integer> asIntegerList(@Nullable Object value)
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

    @NotNull
    public static List<Boolean> asBooleanList(@Nullable Object value)
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
    @Nullable
    public static List<Map<String, Object>> asStringObjectMapList(@Nullable Object object)
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
    @Nullable
    public static Map<String, Object> asStringObjectMap(@Nullable Object value)
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
    @Nullable
    public static List<Object> asObjectList(@Nullable Object value)
    {
        boolean isList = value instanceof List<?>;
        return isList ? (List<Object>)value : null;
    }


    // --
    // Date parsing
    // --

    @NotNull
    public static List<Date> asDateList(@Nullable Object value)
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

    @Nullable
    public static Date asDate(@Nullable Object value)
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

    @Nullable
    public static String asString(@Nullable Object value)
    {
        if (value instanceof String)
        {
            return (String)value;
        }
        else if (value instanceof Double)
        {
            Double checkDoubleValue = (Double)value;
            if (checkDoubleValue % 1 == 0)
            {
                return Integer.toString(checkDoubleValue.intValue());
            }
            return value.toString();
        }
        else if (value instanceof Float)
        {
            Float checkFloatValue = (Float)value;
            if (checkFloatValue % 1 == 0)
            {
                return Integer.toString(checkFloatValue.intValue());
            }
            return value.toString();
        }
        else if (value instanceof Integer || value instanceof Boolean)
        {
            return value.toString();
        }
        return null;
    }

    @Nullable
    public static Double asDouble(@Nullable Object value)
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

    @Nullable
    public static Float asFloat(@Nullable Object value)
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

    @Nullable
    public static Integer asInteger(@Nullable Object value)
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

    @Nullable
    public static Boolean asBoolean(@Nullable Object value)
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
