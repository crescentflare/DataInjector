package com.crescentflare.datainjector.transformer;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.injector.DataInjector;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Data injector transformation: transform data to a string
 * Convert data to a string, including nested structures
 */
public class StringifyTransformer extends BaseTransformer
{
    // --
    // Members
    // --

    private InjectorPath sourceDataPath;
    private boolean includeSpaces;
    private boolean includeNewlines;


    // --
    // Initialization
    // --

    public StringifyTransformer()
    {
    }


    // --
    // Manual transformation
    // --

    @NotNull
    public static InjectorResult stringify(@Nullable Object data)
    {
        return stringify(data, false, false);
    }

    @NotNull
    public static InjectorResult stringify(@Nullable Object data, boolean includeSpaces)
    {
        return stringify(data, includeSpaces, false);
    }

    @NotNull
    public static InjectorResult stringify(@Nullable Object data, boolean includeSpaces, boolean includeNewlines)
    {
        return InjectorResult.withModifiedObject(dataToString(data, includeSpaces, includeNewlines, 0));
    }


    // --
    // General transformation
    // --

    @Override
    @NotNull
    protected InjectorResult onApply(@Nullable Object sourceData)
    {
        Object useSourceData = DataInjector.get(sourceData, sourceDataPath != null ? sourceDataPath : new InjectorPath());
        return stringify(useSourceData, includeSpaces, includeNewlines);
    }


    // --
    // Set values
    // --

    public void setSourceDataPath(@Nullable InjectorPath sourceDataPath)
    {
        this.sourceDataPath = sourceDataPath;
    }

    public void setIncludeSpaces(boolean includeSpaces)
    {
        this.includeSpaces = includeSpaces;
    }

    public void setIncludeNewlines(boolean includeNewlines)
    {
        this.includeNewlines = includeNewlines;
    }


    // --
    // Internal conversion
    // --

    private static String dataToString(Object data, boolean includeSpaces, boolean includeNewlines, int level)
    {
        if (data instanceof Map)
        {
            return mapToString((Map)data, includeSpaces, includeNewlines, level);
        }
        else if (data instanceof List)
        {
            return listToString((List)data, includeSpaces, includeNewlines, level);
        }
        else if (data != null)
        {
            String result = InjectorConv.asString(data);
            if (result == null)
            {
                result = data.toString();
            }
            if (result != null)
            {
                if (data instanceof String)
                {
                    return "\"" + result + "\"";
                }
                return result;
            }
        }
        return "null";
    }

    private static String mapToString(Map<?, ?> map, boolean includeSpaces, boolean includeNewlines, int level)
    {
        StringBuilder result = new StringBuilder("{");
        boolean firstElement = true;
        if (includeNewlines)
        {
            result.append('\n');
        }
        for (Object key : map.keySet())
        {
            String stringKey = dataToString(key, includeSpaces, includeNewlines, 0);
            String stringValue = dataToString(map.get(key), includeSpaces, includeNewlines, level + 1);
            if (!firstElement)
            {
                result.append(',');
                if (includeNewlines)
                {
                    result.append('\n');
                }
                else if (includeSpaces)
                {
                    result.append(' ');
                }
            }
            if (includeNewlines)
            {
                for (int i = 0; i < level * 2 + 2; i++)
                {
                    result.append(' ');
                }
            }
            result.append(stringKey);
            result.append(':');
            if (includeSpaces)
            {
                result.append(' ');
            }
            result.append(stringValue);
            firstElement = false;
        }
        if (includeNewlines)
        {
            if (!firstElement)
            {
                result.append('\n');
            }
            for (int i = 0; i < level * 2; i++)
            {
                result.append(' ');
            }
        }
        result.append('}');
        return result.toString();
    }

    private static String listToString(List<?> list, boolean includeSpaces, boolean includeNewlines, int level)
    {
        StringBuilder result = new StringBuilder("[");
        boolean firstElement = true;
        if (includeNewlines)
        {
            result.append('\n');
        }
        for (Object item : list)
        {
            String stringItem = dataToString(item, includeSpaces, includeNewlines, level + 1);
            if (!firstElement)
            {
                result.append(',');
                if (includeNewlines)
                {
                    result.append('\n');
                }
                else if (includeSpaces)
                {
                    result.append(' ');
                }
            }
            if (includeNewlines)
            {
                for (int i = 0; i < level * 2 + 2; i++)
                {
                    result.append(' ');
                }
            }
            result.append(stringItem);
            firstElement = false;
        }
        if (includeNewlines)
        {
            if (!firstElement)
            {
                result.append('\n');
            }
            for (int i = 0; i < level * 2; i++)
            {
                result.append(' ');
            }
        }
        result.append(']');
        return result.toString();
    }
}
