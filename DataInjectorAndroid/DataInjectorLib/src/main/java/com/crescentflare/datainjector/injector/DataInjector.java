package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data injector: manually modify data
 * Search for an item in a nested data structure and return the modified result
 */
public final class DataInjector
{
    // --
    // Private constructor, this is a static class
    // --

    private DataInjector()
    {
    }


    // --
    // Obtain data
    // --

    @Nullable
    public static Object get(@Nullable Object data, @NotNull String path)
    {
        return get(data, new InjectorPath(path));
    }

    @Nullable
    public static Object get(@Nullable Object data, @NotNull InjectorPath path)
    {
        if (path.hasElements())
        {
            if (data instanceof Map)
            {
                Map<String, Object> dataMap = InjectorConv.asStringObjectMap(data);
                if (dataMap != null)
                {
                    return get(dataMap.get(path.firstElement()), path.deeperPath());
                }
            }
            else if (data instanceof List)
            {
                List<Object> dataList = InjectorConv.asObjectList(data);
                if (dataList != null)
                {
                    Integer boxedIndex = InjectorConv.asInteger(path.firstElement());
                    int index = boxedIndex != null ? boxedIndex : -1;
                    if (index >= 0 && index < dataList.size())
                    {
                        return get(dataList.get(index), path.deeperPath());
                    }
                }
            }
        }
        else
        {
            return data;
        }
        return null;
    }


    // --
    // Modify data
    // --

    @NotNull
    public static Result inject(@Nullable Object data, @NotNull String path, @NotNull ModifyCallback modifyCallback)
    {
        return inject(data, new InjectorPath(path), modifyCallback);
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static Result inject(@Nullable Object data, @NotNull InjectorPath path, @NotNull ModifyCallback modifyCallback)
    {
        if (path.hasElements())
        {
            if (data instanceof Map)
            {
                Map<String, Object> dataMap = InjectorConv.asStringObjectMap(data);
                if (dataMap != null)
                {
                    String mapIndex = path.firstElement();
                    Object originalData = dataMap.get(mapIndex);
                    Result result = inject(originalData, path.deeperPath(), modifyCallback);
                    if (result.hasError())
                    {
                        return result;
                    }
                    if (result.getModifiedObject() != originalData)
                    {
                        HashMap<String, Object> modifiedMap = new HashMap<>();
                        modifiedMap.put(mapIndex, result.getModifiedObject());
                        for (String key : dataMap.keySet())
                        {
                            if (!key.equals(mapIndex))
                            {
                                modifiedMap.put(key, dataMap.get(key));
                            }
                        }
                        return Result.withModifiedObject(modifiedMap);
                    }
                    return Result.withModifiedObject(data);
                }
            }
            else if (data instanceof List)
            {
                List<Object> dataList = InjectorConv.asObjectList(data);
                if (dataList != null)
                {
                    Integer boxedIndex = InjectorConv.asInteger(path.firstElement());
                    int index = boxedIndex != null ? boxedIndex : -1;
                    if (index >= 0 && index < dataList.size())
                    {
                        Object originalData = dataList.get(index);
                        Result result = inject(originalData, path.deeperPath(), modifyCallback);
                        if (result.hasError())
                        {
                            return result;
                        }
                        if (result.getModifiedObject() != originalData)
                        {
                            List<Object> modifiedList = new ArrayList<>();
                            for (int i = 0; i < dataList.size(); i++)
                            {
                                if (i == index)
                                {
                                    modifiedList.add(result.getModifiedObject());
                                }
                                else
                                {
                                    modifiedList.add(dataList.get(i));
                                }
                            }
                            return Result.withModifiedObject(modifiedList);
                        }
                        return Result.withModifiedObject(data);
                    }
                    else
                    {
                        return Result.withError(Error.IndexInvalid);
                    }
                }
            }
            else
            {
                return Result.withError(Error.NoIndexedCollection);
            }
        }
        else
        {
            Result result = modifyCallback.modify(data);
            if (result != null)
            {
                return result;
            }
        }
        return Result.withError(Error.Unknown);
    }


    // --
    // Inject result class
    // --

    public static final class Result
    {
        private Object modifiedObject;
        private Error error;
        private Object customInfo;

        private Result()
        {
            // Private constructor, should be created with factory methods
        }

        public static Result withModifiedObject(@Nullable Object modifiedObject)
        {
            Result result = new Result();
            result.modifiedObject = modifiedObject;
            return result;
        }

        @SuppressWarnings("WeakerAccess")
        public static Result withError(@NotNull Error error)
        {
            Result result = new Result();
            result.error = error;
            return result;
        }

        @SuppressWarnings("unused")
        public static Result withCustomError(@NotNull Object customInfo)
        {
            Result result = new Result();
            result.error = Error.Custom;
            result.customInfo = customInfo;
            return result;
        }

        @SuppressWarnings("WeakerAccess")
        @Nullable
        public Object getModifiedObject()
        {
            return modifiedObject;
        }

        @SuppressWarnings("unused")
        @Nullable
        public Error getError()
        {
            return error;
        }

        @SuppressWarnings("WeakerAccess")
        public boolean hasError()
        {
            return error != null;
        }

        @SuppressWarnings("unused")
        @Nullable
        public Object getCustomInfo()
        {
            return customInfo;
        }
    }


    // --
    // Modify callback interface
    // --

    public interface ModifyCallback
    {
        Result modify(@Nullable Object originalData);
    }


    // --
    // Error enum
    // --

    public enum Error
    {
        Unknown,
        NoIndexedCollection,
        IndexInvalid,
        Custom
    }
}
