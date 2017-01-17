package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.dependency.InjectorDependencyState;

import java.util.ArrayList;
import java.util.List;

/**
 * Data injector: the base class
 * Classes derived from this base class can be used as a data injector
 */
public class DataInjector
{
    public DataInjector()
    {
    }

    public void apply(Object targetData, Object referencedData, Object subReferencedData)
    {
    }

    public List<String> findDependencies()
    {
        return new ArrayList<>();
    }
}
