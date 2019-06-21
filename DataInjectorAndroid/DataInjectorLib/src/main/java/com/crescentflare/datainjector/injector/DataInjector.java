package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data injector: manually modify data
 * Search for an item in a nested data structure and return the modified result
 */
public class DataInjector
{
    // --
    // Singleton instance
    // --

    public static DataInjector instance = new DataInjector();


    // --
    // Initialization
    // --

    public DataInjector()
    {
    }


    // --
    // Modify data
    // --

    public Result inject(Object data, String path, ModifyCallback modifyCallback)
    {
        return inject(data, new Path(path), modifyCallback);
    }

    public Result inject(Object data, Path path, ModifyCallback modifyCallback)
    {
        if (path.hasElements())
        {
            if (data instanceof Map)
            {
                Map<String, Object> dataMap = InjectorUtil.asStringObjectMap(data);
                if (dataMap != null)
                {
                    Object originalData = dataMap.get(path.firstElement());
                    Result result = inject(originalData, path.deeperPath(), modifyCallback);
                    if (result.isError())
                    {
                        return result;
                    }
                    if (result.getModifiedObject() != originalData)
                    {
                        HashMap<String, Object> modifiedMap = new HashMap<>();
                        modifiedMap.put(path.firstElement(), result.getModifiedObject());
                        for (String key : dataMap.keySet())
                        {
                            if (!key.equals(path.firstElement()))
                            {
                                modifiedMap.put(key, dataMap.get(key));
                            }
                        }
                        return new Result(modifiedMap);
                    }
                    return new Result(data);
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
                        if (result.isError())
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
                            return new Result(modifiedList);
                        }
                        return new Result(data);
                    }
                }
            }
        }
        else
        {
            return modifyCallback.modify(data);
        }
        return new Result(null);
    }


    // --
    // Inject result class
    // --

    public static class Result
    {
        private Object modifiedObject;
        private boolean error;

        public Result(Object modifiedObject)
        {
            this.modifiedObject = modifiedObject;
            this.error = modifiedObject == null;
        }

        Object getModifiedObject()
        {
            return modifiedObject;
        }

        boolean isError()
        {
            return error;
        }
    }


    // --
    // Path helper class
    // --

    public static class Path
    {
        private String[] pathComponents;

        public Path(String path)
        {
            if (path != null && !path.isEmpty())
            {
                pathComponents = path.split("\\.");
            }
            else
            {
                pathComponents = new String[0];
            }
        }

        public Path(String[] pathComponents)
        {
            this.pathComponents = pathComponents;
        }

        String firstElement()
        {
            if (pathComponents.length > 0)
            {
                return pathComponents[0];
            }
            return "";
        }

        boolean hasElements()
        {
            return pathComponents.length > 0;
        }

        Path deeperPath()
        {
            if (pathComponents.length > 0)
            {
                int count = pathComponents.length - 1;
                String[] subPathComponents = new String[count];
                System.arraycopy(pathComponents, 1, subPathComponents, 0, count);
                return new Path(subPathComponents);
            }
            return new Path("");
        }
    }


    // --
    // Modify callback interface
    // --

    public interface ModifyCallback
    {
        Result modify(Object originalData);
    }
}
