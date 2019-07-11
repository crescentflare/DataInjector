package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data injector: remove or replace null values
 * Traverses a data set and remove nil values or replace them with the given defaults (with optional recursion)
 */
public class ReplaceNullInjector extends BaseInjector
{
    // --
    // Members
    // --

    private InjectorPath targetItemPath;
    private InjectorPath sourceDataPath;
    private Object overrideSourceData;
    private boolean recursive = false;
    private boolean ignoreNotExisting = false;


    // --
    // Initialization
    // --

    public ReplaceNullInjector()
    {
    }


    // --
    // Manual injection
    // --

    @NotNull
    public static InjectorResult filterNull(@Nullable Object targetData)
    {
        return replaceNull(targetData, null, false, false);
    }

    @NotNull
    public static InjectorResult filterNull(@Nullable Object targetData, boolean recursive)
    {
        return replaceNull(targetData, null, recursive, false);
    }

    @NotNull
    public static InjectorResult replaceNull(@Nullable Object targetData, @Nullable Object replaceData)
    {
        return replaceNull(targetData, replaceData, false, false);
    }

    @NotNull
    public static InjectorResult replaceNull(@Nullable Object targetData, @Nullable Object replaceData, boolean recursive)
    {
        return replaceNull(targetData, replaceData, recursive, false);
    }

    @NotNull
    public static InjectorResult replaceNull(@Nullable Object targetData, @Nullable Object replaceData, boolean recursive, boolean ignoreNotExisting)
    {
        return processData(targetData, replaceData, recursive, ignoreNotExisting);
    }


    // --
    // General injection
    // --


    @Override
    protected @NotNull InjectorResult onApply(@Nullable Object targetData, @Nullable Object sourceData)
    {
        Object checkSourceData = overrideSourceData != null ? overrideSourceData : sourceData;
        final Object useSourceData = DataInjector.get(checkSourceData, sourceDataPath != null ? sourceDataPath : new InjectorPath());
        return DataInjector.inject(targetData, targetItemPath != null ? targetItemPath : new InjectorPath(), new DataInjector.ModifyCallback()
        {
            @Override
            public @NotNull InjectorResult modify(@Nullable Object originalData)
            {
                return ReplaceNullInjector.replaceNull(originalData, useSourceData, recursive, ignoreNotExisting);
            }
        });
    }


    // --
    // Set values
    // --

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

    public void setRecursive(boolean recursive)
    {
        this.recursive = recursive;
    }

    public void setIgnoreNotExisting(boolean ignoreNotExisting)
    {
        this.ignoreNotExisting = ignoreNotExisting;
    }


    // --
    // Internal data processing
    // --

    private static InjectorResult processData(Object data, Object replaceData, boolean recursive, boolean ignoreNotExisting)
    {
        if (data == null)
        {
            return replaceData != null ? InjectorResult.withModifiedObject(replaceData) : InjectorResult.withError(InjectorResult.Error.NullNotAllowed);
        }
        else if (recursive)
        {
            Map<String, Object> mapItem = InjectorConv.asStringObjectMap(data);
            List<Object> listItem = InjectorConv.asObjectList(data);
            if (mapItem != null)
            {
                return processMap(mapItem, InjectorConv.asStringObjectMap(replaceData), recursive, ignoreNotExisting);
            }
            else if (listItem != null)
            {
                return processList(listItem, InjectorConv.asObjectList(replaceData), recursive, ignoreNotExisting);
            }
        }
        return InjectorResult.withModifiedObject(data);
    }

    private static InjectorResult processList(List<Object> list, List<Object> replaceList, boolean recursive, boolean ignoreNotExisting)
    {
        Object firstReplaceItem = replaceList != null && replaceList.size() > 0 ? replaceList.get(0) : null;
        List<Object> modifiedList = new ArrayList<>();
        for (Object listItem : list)
        {
            Object addListItem = listItem == null ? firstReplaceItem : listItem;
            if (addListItem != null)
            {
                if (recursive)
                {
                    InjectorResult result = processData(addListItem, firstReplaceItem, recursive, ignoreNotExisting);
                    if (result.hasError())
                    {
                        return result;
                    }
                    if (result.getModifiedObject() != null)
                    {
                        modifiedList.add(result.getModifiedObject());
                    }
                    else
                    {
                        return InjectorResult.withError(InjectorResult.Error.NullNotAllowed);
                    }
                }
                else
                {
                    modifiedList.add(addListItem);
                }
            }
        }
        if (modifiedList.isEmpty() && !ignoreNotExisting)
        {
            if (firstReplaceItem != null)
            {
                modifiedList.add(firstReplaceItem);
            }
            else
            {
                return InjectorResult.withError(InjectorResult.Error.SourceInvalid);
            }
        }
        return InjectorResult.withModifiedObject(modifiedList);
    }

    private static InjectorResult processMap(Map<String, Object> map, Map<String, Object> replaceMap, boolean recursive, boolean ignoreNotExisting)
    {
        Map<String, Object> modifiedMap = new HashMap<>();
        for (String key : map.keySet())
        {
            Object replaceValue = replaceMap != null ? replaceMap.get(key) : null;
            Object value = map.get(key);
            Object setValue = value == null ? replaceValue : value;
            if (setValue != null)
            {
                if (recursive)
                {
                    InjectorResult result = processData(setValue, replaceValue, recursive, ignoreNotExisting);
                    if (result.hasError())
                    {
                        return result;
                    }
                    if (result.getModifiedObject() != null)
                    {
                        modifiedMap.put(key, result.getModifiedObject());
                    }
                    else
                    {
                        return InjectorResult.withError(InjectorResult.Error.NullNotAllowed);
                    }
                }
                else
                {
                    modifiedMap.put(key, setValue);
                }
            }
        }
        if (!ignoreNotExisting && replaceMap != null)
        {
            for (String key : replaceMap.keySet())
            {
                if (!modifiedMap.containsKey(key))
                {
                    Object setValue = replaceMap.get(key);
                    if (setValue != null)
                    {
                        modifiedMap.put(key, setValue);
                    }
                    else
                    {
                        return InjectorResult.withError(InjectorResult.Error.NullNotAllowed);
                    }
                }
            }
        }
        return InjectorResult.withModifiedObject(modifiedMap);
    }
}
