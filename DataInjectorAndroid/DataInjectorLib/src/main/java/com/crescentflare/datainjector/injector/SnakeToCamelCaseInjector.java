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
 * Data injector: snake case conversion
 * Converts each entry key in the data set from snake case to camel case (with optional recursion)
 */
public class SnakeToCamelCaseInjector extends BaseInjector
{
    // --
    // Members
    // --

    private InjectorPath targetItemPath;
    private boolean recursive = false;


    // --
    // Initialization
    // --

    public SnakeToCamelCaseInjector()
    {
    }


    // --
    // Manual injection
    // --

    @NotNull
    public static InjectorResult changeCase(@Nullable Object targetData)
    {
        return changeCase(targetData, false);
    }

    @NotNull
    public static InjectorResult changeCase(@Nullable Object targetData, boolean recursive)
    {
        Map<String, Object> mapItem = InjectorConv.asStringObjectMap(targetData);
        List<Object> listItem = InjectorConv.asObjectList(targetData);
        if (mapItem != null)
        {
            return processMap(mapItem, recursive);
        }
        else if (listItem != null)
        {
            if (!recursive)
            {
                return InjectorResult.withError(InjectorResult.Error.TargetInvalid);
            }
            return processList(listItem);
        }
        return InjectorResult.withError(InjectorResult.Error.TargetInvalid);
    }


    // --
    // Data helpers
    // --

    @Nullable
    public static String stringToCamelCase(@Nullable String snakeCaseString)
    {
        if (snakeCaseString != null)
        {
            String[] splitString = snakeCaseString.split("_");
            if (splitString.length > 1)
            {
                StringBuilder builder = new StringBuilder(splitString[0]);
                for (int i = 1; i < splitString.length; i++)
                {
                    builder.append(splitString[i].substring(0, 1).toUpperCase());
                    builder.append(splitString[i].substring(1));
                }
                return builder.toString();
            }
        }
        return snakeCaseString;
    }


    // --
    // General injection
    // --

    @Override
    @NotNull
    protected InjectorResult onApply(@Nullable Object targetData, @Nullable Object sourceData)
    {
        return DataInjector.inject(targetData, targetItemPath != null ? targetItemPath : new InjectorPath(), new DataInjector.ModifyCallback()
        {
            @Override
            public @NotNull InjectorResult modify(@Nullable Object originalData)
            {
                return SnakeToCamelCaseInjector.changeCase(originalData, recursive);
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

    public void setRecursive(boolean recursive)
    {
        this.recursive = recursive;
    }


    // --
    // Internal data processing
    // --

    private static InjectorResult processList(List<Object> list)
    {
        List<Object> modifiedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
        {
            Map<String, Object> mapItem = InjectorConv.asStringObjectMap(list.get(i));
            List<Object> listItem = InjectorConv.asObjectList(list.get(i));
            if (mapItem != null)
            {
                InjectorResult result = processMap(mapItem, true);
                if (result.hasError())
                {
                    return result;
                }
                modifiedList.add(result.getModifiedObject());
            }
            else if (listItem != null)
            {
                InjectorResult result = processList(listItem);
                if (result.hasError())
                {
                    return result;
                }
                modifiedList.add(result.getModifiedObject());
            }
            else
            {
                modifiedList.add(list.get(i));
            }
        }
        return InjectorResult.withModifiedObject(modifiedList);
    }

    private static InjectorResult processMap(Map<String, Object> map, boolean recursive)
    {
        HashMap<String, Object> modifiedMap = new HashMap<>();
        for (String key : map.keySet())
        {
            Object value = map.get(key);
            String newKey = stringToCamelCase(key);
            if (newKey != null)
            {
                if (recursive)
                {
                    Map<String, Object> mapItem = InjectorConv.asStringObjectMap(value);
                    List<Object> listItem = InjectorConv.asObjectList(value);
                    if (mapItem != null)
                    {
                        InjectorResult result = processMap(mapItem, recursive);
                        if (result.hasError())
                        {
                            return result;
                        }
                        modifiedMap.put(newKey, result.getModifiedObject());
                    }
                    else if (listItem != null)
                    {
                        InjectorResult result = processList(listItem);
                        if (result.hasError())
                        {
                            return result;
                        }
                        modifiedMap.put(newKey, result.getModifiedObject());
                    }
                    else
                    {
                        modifiedMap.put(newKey, value);
                    }
                }
                else
                {
                    modifiedMap.put(newKey, value);
                }
            }
        }
        return InjectorResult.withModifiedObject(modifiedMap);
    }
}
