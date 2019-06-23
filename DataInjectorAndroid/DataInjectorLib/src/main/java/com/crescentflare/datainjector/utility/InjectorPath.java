package com.crescentflare.datainjector.utility;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

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

    public InjectorPath()
    {
        pathComponents = new String[0];
    }

    public InjectorPath(@NotNull String path)
    {
        this(path, ".");
    }

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
