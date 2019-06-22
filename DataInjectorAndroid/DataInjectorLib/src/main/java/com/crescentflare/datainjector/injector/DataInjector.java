package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.conversion.InjectorConv;
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
    // Modify data
    // --

    @NotNull
    public static Result inject(@Nullable Object data, @NotNull String path, @NotNull ModifyCallback modifyCallback)
    {
        return inject(data, new Path(path), modifyCallback);
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static Result inject(@Nullable Object data, @NotNull Path path, @NotNull ModifyCallback modifyCallback)
    {
        if (path.hasElements())
        {
            if (data instanceof Map)
            {
                Map<String, Object> dataMap = InjectorUtil.asStringObjectMap(data);
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
                List<Object> dataList = InjectorUtil.asObjectList(data);
                if (dataList != null)
                {
                    Integer boxedIndex = InjectorConv.toInteger(path.firstElement());
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
    // Path helper class
    // --

    public static final class Path
    {
        private String[] pathComponents;

        @SuppressWarnings("unused")
        public Path()
        {
            pathComponents = new String[0];
        }

        @SuppressWarnings("WeakerAccess")
        public Path(@NotNull String path)
        {
            if (!path.isEmpty())
            {
                pathComponents = path.split("\\.");
            }
            else
            {
                pathComponents = new String[0];
            }
        }

        @SuppressWarnings("WeakerAccess")
        public Path(@NotNull String[] pathComponents)
        {
            this.pathComponents = pathComponents;
        }

        @SuppressWarnings("WeakerAccess")
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

        @SuppressWarnings("WeakerAccess")
        public boolean hasElements()
        {
            return pathComponents.length > 0;
        }

        public boolean hasNextElement()
        {
            return pathComponents.length > 1;
        }

        @SuppressWarnings("WeakerAccess")
        @NotNull
        public Path deeperPath()
        {
            if (pathComponents.length > 0)
            {
                int count = pathComponents.length - 1;
                String[] subPathComponents = new String[count];
                System.arraycopy(pathComponents, 1, subPathComponents, 0, count);
                return new Path(subPathComponents);
            }
            return new Path();
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
