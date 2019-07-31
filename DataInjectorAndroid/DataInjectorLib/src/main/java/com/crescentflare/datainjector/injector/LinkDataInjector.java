package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.transformer.BaseTransformer;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data injector: link datasets
 * Inject one data set into another by a common field that links them together (such as ID)
 */
public class LinkDataInjector extends BaseInjector
{
    // --
    // Members
    // --

    private List<? extends BaseTransformer> sourceTransformers = new ArrayList<>();
    private InjectorPath targetItemPath;
    private InjectorPath sourceDataPath;
    private Object overrideSourceData;
    private String linkKey = "unknown";


    // --
    // Initialization
    // --

    public LinkDataInjector()
    {
    }


    // --
    // Manual injection
    // --

    @NotNull
    public static InjectorResult linkData(@NotNull Map<String, Object> targetMap, @NotNull List<?> sourceData, @NotNull String key)
    {
        Map<String, Object> foundItem = findDataItem(sourceData, targetMap.get(key), key);
        if (foundItem != null)
        {
            HashMap<String, Object> modifiedMap = new HashMap<>();
            for (String setKey : targetMap.keySet())
            {
                modifiedMap.put(setKey, targetMap.get(setKey));
            }
            for (String setKey : foundItem.keySet())
            {
                if (!setKey.equals(key))
                {
                    modifiedMap.put(setKey, foundItem.get(setKey));
                }
            }
            return InjectorResult.withModifiedObject(modifiedMap);
        }
        return InjectorResult.withModifiedObject(targetMap);
    }

    @NotNull
    public static InjectorResult linkData(@NotNull List<?> targetList, @NotNull List<?> sourceData, @NotNull String key)
    {
        List<Map<String, Object>> modifiedData = new ArrayList<>();
        for (Object listItem : targetList)
        {
            Map<String, Object> mapItem = InjectorConv.asStringObjectMap(listItem);
            if (mapItem != null)
            {
                InjectorResult result = LinkDataInjector.linkData(mapItem, sourceData, key);
                if (result.hasError())
                {
                    return result;
                }
                modifiedData.add(InjectorConv.asStringObjectMap(result.getModifiedObject()));
            }
            else
            {
                return InjectorResult.withError(InjectorResult.Error.TargetInvalid);
            }
        }
        return InjectorResult.withModifiedObject(modifiedData);
    }


    // --
    // Data helpers
    // --

    @Nullable
    public static Map<String, Object> findDataItem(@NotNull List<?> list, @Nullable Object value, @NotNull String key)
    {
        String searchValueString = InjectorConv.asString(value);
        if (searchValueString != null)
        {
            for (Object listItem : list)
            {
                Map<String, Object> mapItem = InjectorConv.asStringObjectMap(listItem);
                if (mapItem != null)
                {
                    String compareValueString = InjectorConv.asString(mapItem.get(key));
                    if (compareValueString != null)
                    {
                        if (compareValueString.equals(searchValueString))
                        {
                            return mapItem;
                        }
                    }
                }
            }
        }
        return null;
    }


    // --
    // General injection
    // --

    @Override
    @NotNull
    protected InjectorResult onApply(@Nullable Object targetData, @Nullable Object sourceData)
    {
        // Prepare source data with optional transformation
        Object checkSourceData = overrideSourceData != null ? overrideSourceData : sourceData;
        checkSourceData = DataInjector.get(checkSourceData, sourceDataPath != null ? sourceDataPath : new InjectorPath());
        for (BaseTransformer transformer : sourceTransformers)
        {
            InjectorResult result = transformer.apply(checkSourceData);
            if (result.hasError())
            {
                return result;
            }
            checkSourceData = result.getModifiedObject();
        }

        // Apply injection
        final Object useSourceData = checkSourceData;
        return DataInjector.inject(targetData, targetItemPath != null ? targetItemPath : new InjectorPath(), new DataInjector.ModifyCallback()
        {
            @Override
            public @NotNull InjectorResult modify(@Nullable Object originalData)
            {
                List<Object> sourceList = InjectorConv.asObjectList(useSourceData);
                if (sourceList != null)
                {
                    Map<String, Object> targetMap = InjectorConv.asStringObjectMap(originalData);
                    List<Object> targetList = InjectorConv.asObjectList(originalData);
                    if (targetMap != null)
                    {
                        return LinkDataInjector.linkData(targetMap, sourceList, linkKey);
                    }
                    else if (targetList != null)
                    {
                        return LinkDataInjector.linkData(targetList, sourceList, linkKey);
                    }
                    else
                    {
                        return InjectorResult.withError(InjectorResult.Error.TargetInvalid);
                    }
                }
                return InjectorResult.withError(InjectorResult.Error.SourceInvalid);
            }
        });
    }


    // --
    // Set values
    // --

    public void setSourceTransformers(@Nullable List<? extends BaseTransformer> sourceTransformers)
    {
        this.sourceTransformers = sourceTransformers != null ? sourceTransformers : new ArrayList<BaseTransformer>();
    }

    public void setTargetItemPath(@Nullable InjectorPath targetItemPath)
    {
        this.targetItemPath = targetItemPath;
    }

    public void setSourceDataPath(@Nullable InjectorPath sourceDataPath)
    {
        this.sourceDataPath = sourceDataPath;
    }

    public void setOverrideSourceData(@Nullable Object overrideSourceData)
    {
        this.overrideSourceData = overrideSourceData;
    }

    public void setLinkKey(@Nullable String linkKey)
    {
        this.linkKey = linkKey != null ? linkKey : "unknown";
    }
}
