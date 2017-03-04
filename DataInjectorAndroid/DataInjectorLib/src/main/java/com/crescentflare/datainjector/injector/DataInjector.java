package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.dependency.InjectorDependency;
import com.crescentflare.datainjector.dependency.InjectorDependencyState;
import com.crescentflare.datainjector.utility.InjectorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Data injector: the base class
 * Classes derived from this base class can be used as a data injector
 */
public class DataInjector
{
    // ---
    // Initialization
    // ---

    public DataInjector()
    {
    }


    // ---
    // Apply overloads
    // ---

    public final void apply(Object targetData)
    {
        onApply(targetData, null, null, null);
    }

    public final void apply(Object targetData, Object referencedData)
    {
        onApply(targetData, null, referencedData, null);
    }

    public final void apply(Object targetData, Object referencedData, Object subReferencedData)
    {
        onApply(targetData, null, referencedData, subReferencedData);
    }

    public final void apply(Object targetData, Object subTargetData, Object referencedData, Object subReferencedData)
    {
        onApply(targetData, subTargetData, referencedData, subReferencedData);
    }


    // ---
    // Functions to implement
    // ---

    public void onApply(Object targetData, Object subTargetData, Object referencedData, Object subReferencedData)
    {
    }

    public List<InjectorDependency> findDependencies()
    {
        return new ArrayList<>();
    }


    // ---
    // Helper
    // ---

    public static Object obtainValue(String item, Object targetData, Object subTargetData, Object referencedData, Object subReferencedData)
    {
        if (item.startsWith("@."))
        {
            return InjectorUtil.itemFromObject(subReferencedData, item.substring(2));
        }
        else if (item.startsWith("@"))
        {
            return InjectorUtil.itemFromObject(referencedData, item.substring(1));
        }
        else if (item.startsWith("~."))
        {
            return InjectorUtil.itemFromObject(subTargetData, item.substring(2));
        }
        else if (item.startsWith("~"))
        {
            return InjectorUtil.itemFromObject(targetData, item.substring(1));
        }
        return item;
    }
}
