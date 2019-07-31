package com.crescentflare.datainjector.transformer;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.injector.DataInjector;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data injector transformation: concatenate strings
 * Join multiple strings together with an optional delimiter
 */
public class JoinStringTransformer extends BaseTransformer
{
    // --
    // Members
    // --

    private InjectorPath sourceDataPath;
    private List<String> fromItems = new ArrayList<>();
    private String delimiter = "";


    // --
    // Initialization
    // --

    public JoinStringTransformer()
    {
    }


    // --
    // Manual transformation
    // --

    @NotNull
    public static InjectorResult joinString(@NotNull List<?> sourceList)
    {
        return joinString(sourceList, "");
    }

    @NotNull
    public static InjectorResult joinString(@NotNull List<?> sourceList, @NotNull String delimiter)
    {
        List<String> stringItems = new ArrayList<>();
        for (Object value : sourceList)
        {
            if (value instanceof String)
            {
                stringItems.add((String)value);
            }
        }
        return InjectorResult.withModifiedObject(joinStringList(stringItems, delimiter));
    }

    @NotNull
    public static InjectorResult joinString(@NotNull Map<String, Object> sourceMap, @NotNull List<String> fromItems)
    {
        return joinString(sourceMap, fromItems, "");
    }

    @NotNull
    public static InjectorResult joinString(@NotNull Map<String, Object> sourceMap, @NotNull List<String> fromItems, @NotNull String delimiter)
    {
        List<String> stringItems = new ArrayList<>();
        for (String item : fromItems)
        {
            Object value = sourceMap.get(item);
            if (value instanceof String)
            {
                stringItems.add((String)value);
            }
        }
        return InjectorResult.withModifiedObject(joinStringList(stringItems, delimiter));
    }


    // --
    // Data helpers
    // --

    @NotNull
    public static String joinStringList(@NotNull List<String> stringList)
    {
        return joinStringList(stringList, "");
    }

    @NotNull
    public static String joinStringList(@NotNull List<String> stringList, @NotNull String delimiter)
    {
        StringBuilder result = new StringBuilder("");
        boolean firstString = true;
        for (String item : stringList)
        {
            if (!firstString)
            {
                result.append(delimiter);
            }
            result.append(item);
            firstString = false;
        }
        return result.toString();
    }


    // --
    // General transformation
    // --

    @Override
    @NotNull
    protected InjectorResult onApply(@Nullable Object sourceData)
    {
        Object useSourceData = DataInjector.get(sourceData, sourceDataPath != null ? sourceDataPath : new InjectorPath());
        Map<String, Object> sourceMap = InjectorConv.asStringObjectMap(useSourceData);
        List<Object> sourceList = InjectorConv.asObjectList(useSourceData);
        if (sourceMap != null)
        {
            return joinString(sourceMap, fromItems, delimiter);
        }
        else if (sourceList != null)
        {
            return joinString(sourceList, delimiter);
        }
        return InjectorResult.withError(InjectorResult.Error.SourceInvalid);
    }


    // --
    // Set values
    // --

    public void setSourceDataPath(@Nullable InjectorPath sourceDataPath)
    {
        this.sourceDataPath = sourceDataPath;
    }

    public void setFromItems(@Nullable List<String> fromItems)
    {
        this.fromItems = fromItems != null ? fromItems : new ArrayList<String>();
    }

    public void setDelimiter(@Nullable String delimiter)
    {
        this.delimiter = delimiter != null ? delimiter : "";
    }
}
