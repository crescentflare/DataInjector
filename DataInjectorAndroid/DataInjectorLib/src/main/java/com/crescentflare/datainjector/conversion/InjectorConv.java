package com.crescentflare.datainjector.conversion;

import android.content.res.Resources;
import android.graphics.Color;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Data injector utility: data type conversion
 * Easily recognize and convert between data types
 */
public class InjectorConv
{
    // ---
    // Initialization
    // ---

    private InjectorConv()
    {
    }


    // ---
    // Parsing
    // ---

    public static Date toDate(Object fromObject)
    {
        if (fromObject instanceof String)
        {
            String stringDate = (String)fromObject;
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


    // ---
    // Primitive types
    // ---

    public static String toString(Object fromObject)
    {
        if (fromObject instanceof String)
        {
            return (String)fromObject;
        }
        else if (fromObject instanceof Double || fromObject instanceof Float || fromObject instanceof Integer || fromObject instanceof Boolean)
        {
            return fromObject.toString();
        }
        return null;
    }

    public static Double toDouble(Object fromObject)
    {
        if (fromObject instanceof String)
        {
            try
            {
                return Double.parseDouble((String)fromObject);
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        else if (fromObject instanceof Double)
        {
            return (Double)fromObject;
        }
        else if (fromObject instanceof Float)
        {
            return ((Float)fromObject).doubleValue();
        }
        else if (fromObject instanceof Integer)
        {
            return ((Integer)fromObject).doubleValue();
        }
        else if (fromObject instanceof Boolean)
        {
            return ((Boolean)fromObject) ? 1.0 : 0.0;
        }
        return null;
    }

    public static Float toFloat(Object fromObject)
    {
        if (fromObject instanceof String)
        {
            try
            {
                return Float.parseFloat((String)fromObject);
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        else if (fromObject instanceof Double)
        {
            return ((Double)fromObject).floatValue();
        }
        else if (fromObject instanceof Float)
        {
            return (Float)fromObject;
        }
        else if (fromObject instanceof Integer)
        {
            return ((Integer)fromObject).floatValue();
        }
        else if (fromObject instanceof Boolean)
        {
            return ((Boolean)fromObject) ? 1.0f : 0.0f;
        }
        return null;
    }

    public static Integer toInteger(Object fromObject)
    {
        if (fromObject instanceof String)
        {
            try
            {
                return Integer.parseInt((String)fromObject);
            }
            catch (IllegalArgumentException ignored)
            {
            }
            try
            {
                Double result = Double.parseDouble((String)fromObject);
                return result.intValue();
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        else if (fromObject instanceof Double)
        {
            return ((Double)fromObject).intValue();
        }
        else if (fromObject instanceof Float)
        {
            return ((Float)fromObject).intValue();
        }
        else if (fromObject instanceof Integer)
        {
            return (Integer)fromObject;
        }
        else if (fromObject instanceof Boolean)
        {
            return ((Boolean)fromObject) ? 1 : 0;
        }
        return null;
    }

    public static Boolean toBoolean(Object fromObject)
    {
        if (fromObject instanceof String)
        {
            try
            {
                return Boolean.parseBoolean((String)fromObject);
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        else if (fromObject instanceof Double)
        {
            return (Double)fromObject > 0;
        }
        else if (fromObject instanceof Float)
        {
            return (Float)fromObject > 0;
        }
        else if (fromObject instanceof Integer)
        {
            return (Integer)fromObject > 0;
        }
        else if (fromObject instanceof Boolean)
        {
            return (Boolean)fromObject;
        }
        return null;
    }
}
