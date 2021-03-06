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
 * Join multiple strings together with an optional delimiter, prefix or suffix
 */
public class JoinStringTransformer extends BaseTransformer
{
    // --
    // Members
    // --

    private InjectorPath sourceDataPath;
    private List<String> fromItems = new ArrayList<>();
    private String delimiter = "";
    private String prefix = "";
    private String suffix = "";


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
        return joinString(sourceList, "", "", "");
    }

    @NotNull
    public static InjectorResult joinString(@NotNull List<?> sourceList, @NotNull String delimiter)
    {
        return joinString(sourceList, delimiter, "", "");
    }

    @NotNull
    public static InjectorResult joinString(@NotNull List<?> sourceList, @NotNull String delimiter, @NotNull String prefix, @NotNull String suffix)
    {
        List<String> stringItems = new ArrayList<>();
        for (Object value : sourceList)
        {
            String stringValue = InjectorConv.asString(value);
            if (stringValue != null)
            {
                stringItems.add(stringValue);
            }
        }
        return InjectorResult.withModifiedObject(joinStringList(stringItems, delimiter, prefix, suffix));
    }

    @NotNull
    public static InjectorResult joinString(@NotNull Map<String, Object> sourceMap, @NotNull List<String> fromItems)
    {
        return joinString(sourceMap, fromItems, "", "", "");
    }

    @NotNull
    public static InjectorResult joinString(@NotNull Map<String, Object> sourceMap, @NotNull List<String> fromItems, @NotNull String delimiter)
    {
        return joinString(sourceMap, fromItems, delimiter, "", "");
    }

    @NotNull
    public static InjectorResult joinString(@NotNull Map<String, Object> sourceMap, @NotNull List<String> fromItems, @NotNull String delimiter, @NotNull String prefix, @NotNull String suffix)
    {
        List<String> stringItems = new ArrayList<>();
        for (String item : fromItems)
        {
            String stringValue = InjectorConv.asString(sourceMap.get(item));
            if (stringValue != null)
            {
                stringItems.add(stringValue);
            }
        }
        return InjectorResult.withModifiedObject(joinStringList(stringItems, delimiter, prefix, suffix));
    }


    // --
    // Data helpers
    // --

    @NotNull
    public static String joinStringList(@NotNull List<String> stringList)
    {
        return joinStringList(stringList, "", "", "");
    }

    @NotNull
    public static String joinStringList(@NotNull List<String> stringList, @NotNull String delimiter)
    {
        return joinStringList(stringList, delimiter, "", "");
    }

    @NotNull
    public static String joinStringList(@NotNull List<String> stringList, @NotNull String delimiter, @NotNull String prefix, @NotNull String suffix)
    {
        StringBuilder result = new StringBuilder(prefix);
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
        result.append(suffix);
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
            return joinString(sourceMap, fromItems, delimiter, prefix, suffix);
        }
        else if (sourceList != null)
        {
            return joinString(sourceList, delimiter, prefix, suffix);
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

    public void setPrefix(@Nullable String prefix)
    {
        this.prefix = prefix != null ? prefix : "";
    }

    public void setSuffix(@Nullable String suffix)
    {
        this.suffix = suffix != null ? suffix : "";
    }
}
