package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.dependency.InjectorDependency;
import com.crescentflare.datainjector.dependency.InjectorDependencyManager;
import com.crescentflare.datainjector.utility.InjectorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Data injector: concatenate strings
 * Join multiple strings together with an optional delimiter
 */
public class JoinStringInjector extends DataInjector
{
    // ---
    // Members
    // ---

    private List<String> fromItems = new ArrayList<>();
    private String item;
    private String delimiter = "";
    private boolean removeOriginals = false;


    // ---
    // Initialization
    // ---

    public JoinStringInjector()
    {
    }

    public JoinStringInjector(String item, List<String> fromItems)
    {
        this.item = item;
        this.fromItems = fromItems;
    }

    public JoinStringInjector(String item, List<String> fromItems, String delimiter)
    {
        this.item = item;
        this.fromItems = fromItems;
        this.delimiter = delimiter;
    }

    public JoinStringInjector(String item, List<String> fromItems, String delimiter, boolean removeOriginals)
    {
        this.item = item;
        this.fromItems = fromItems;
        this.delimiter = delimiter;
        this.removeOriginals = removeOriginals;
    }


    // ---
    // Data helpers
    // ---

    public static String joinStrings(String[] stringArray, String delimiter)
    {
        String result = "";
        boolean firstString = true;
        for (String item : stringArray)
        {
            if (!firstString)
            {
                result += delimiter;
            }
            result += item;
            firstString = false;
        }
        return result;
    }

    public static String joinStrings(List<String> stringList, String delimiter)
    {
        String result = "";
        boolean firstString = true;
        for (String item : stringList)
        {
            if (!firstString)
            {
                result += delimiter;
            }
            result += item;
            firstString = false;
        }
        return result;
    }


    // ---
    // General injection
    // ---

    @Override
    public void onApply(Object targetData, Object subTargetData, Object referencedData, Object subReferencedData)
    {
        if (item == null)
        {
            return;
        }
        String finalString = "";
        boolean firstString = true;
        for (String fromItem : fromItems)
        {
            String concatString = InjectorConv.toString(obtainValue(fromItem, targetData, null, referencedData, subReferencedData));
            if (concatString != null)
            {
                if (!firstString)
                {
                    finalString += delimiter;
                }
                finalString += concatString;
                firstString = false;
            }
        }
        Map<String, Object> modifyMap = InjectorUtil.asStringObjectMap(targetData);
        List<Object> modifyList = InjectorUtil.asObjectList(targetData);
        if (modifyMap != null)
        {
            if (removeOriginals)
            {
                for (String fromItem : fromItems)
                {
                    if (!fromItem.startsWith("~.") && fromItem.startsWith("~"))
                    {
                        InjectorUtil.setItemOnMap(modifyMap, fromItem.substring(1), null);
                    }
                }
            }
            InjectorUtil.setItemOnMap(modifyMap, item, finalString);
        }
        else if (modifyList != null)
        {
            if (removeOriginals)
            {
                for (String fromItem : fromItems)
                {
                    if (!fromItem.startsWith("~.") && fromItem.startsWith("~"))
                    {
                        InjectorUtil.setItemOnList(modifyList, fromItem.substring(1), null);
                    }
                }
            }
            InjectorUtil.setItemOnList(modifyList, item, finalString);
        }
    }


    // ---
    // Dependencies
    // ---

    public List<InjectorDependency> findDependencies()
    {
        InjectorDependency dependency = InjectorDependencyManager.instance.dependencyFromInjectSource(item);
        if (dependency != null)
        {
            return Collections.singletonList(dependency);
        }
        return new ArrayList<>();
    }
}
