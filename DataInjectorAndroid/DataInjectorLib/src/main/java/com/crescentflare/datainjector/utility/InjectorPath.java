package com.crescentflare.datainjector.utility;

import com.crescentflare.datainjector.conversion.InjectorConv;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Data injector utility: a target for data injection
 * Use a path to point to a nested data item to be read or modified
 */
public final class InjectorPath
{
    // --
    // Members
    // --

    private String[] pathComponents;


    // --
    // Initialization
    // --

    @SuppressWarnings("WeakerAccess")
    public InjectorPath()
    {
        pathComponents = new String[0];
    }

    public InjectorPath(@NotNull String path)
    {
        this(path, ".");
    }

    @SuppressWarnings("WeakerAccess")
    public InjectorPath(@NotNull String path, @NotNull String separator)
    {
        if (!path.isEmpty())
        {
            pathComponents = path.split(separator.equals(".") ? "\\." : separator);
        }
        else
        {
            pathComponents = new String[0];
        }
    }

    @SuppressWarnings("WeakerAccess")
    public InjectorPath(@NotNull String[] pathComponents)
    {
        this.pathComponents = pathComponents;
    }


    // --
    // Access elements
    // --

    @Nullable
    public String firstElement()
    {
        if (pathComponents.length > 0)
        {
            return pathComponents[0];
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Nullable
    public String nextElement()
    {
        if (pathComponents.length > 1)
        {
            return pathComponents[1];
        }
        return null;
    }

    public boolean hasElements()
    {
        return pathComponents.length > 0;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasNextElement()
    {
        return pathComponents.length > 1;
    }


    // --
    // Traversal
    // --

    @NotNull
    public InjectorPath deeperPath()
    {
        if (pathComponents.length > 0)
        {
            int count = pathComponents.length - 1;
            String[] subPathComponents = new String[count];
            System.arraycopy(pathComponents, 1, subPathComponents, 0, count);
            return new InjectorPath(subPathComponents);
        }
        return new InjectorPath();
    }

    @Nullable
    public static InjectorPath seekPathForMap(@Nullable Object data, @NotNull String markerKey, @NotNull String value)
    {
        if (data instanceof Map)
        {
            Map<String, Object> dataMap = InjectorConv.asStringObjectMap(data);
            if (dataMap != null)
            {
                Object testObject = dataMap.get(markerKey);
                if (testObject instanceof String && testObject.equals(value))
                {
                    return new InjectorPath();
                }
                for (String mapKey : dataMap.keySet())
                {
                    InjectorPath result = seekPathForMap(dataMap.get(mapKey), markerKey, value);
                    if (result != null)
                    {
                        return new InjectorPath(mapKey + "." + result.toString());
                    }
                }
            }
        }
        else if (data instanceof List)
        {
            List<Object> dataList = InjectorConv.asObjectList(data);
            if (dataList != null)
            {
                for (int i = 0; i < dataList.size(); i++)
                {
                    InjectorPath result = seekPathForMap(dataList.get(i), markerKey, value);
                    if (result != null)
                    {
                        return new InjectorPath("" + i + "." + result.toString());
                    }
                }
            }
        }
        return null;
    }


    // --
    // Conversion
    // --

    @Override
    @NotNull
    public String toString()
    {
        return toString(".");
    }

    @NotNull
    public String toString(final String separator)
    {
        StringBuilder result = new StringBuilder(pathComponents.length > 0 ? pathComponents[0] : "");
        for (int i = 1; i < pathComponents.length; i++) {
            result.append(separator).append(pathComponents[i]);
        }
        return result.toString();
    }
}
