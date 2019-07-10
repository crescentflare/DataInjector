package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data injector: the base class
 * Classes derived from this base class can be used as a data injector
 */
public class BaseInjector
{
    // --
    // Initialization
    // --

    public BaseInjector()
    {
    }


    // --
    // Apply overloads
    // --

    @NotNull
    public final InjectorResult apply(@Nullable Object targetData)
    {
        return onApply(targetData, null);
    }

    @NotNull
    public final InjectorResult apply(@Nullable Object targetData, @Nullable Object sourceData)
    {
        return onApply(targetData, sourceData);
    }


    // --
    // Functions to implement
    // --

    @NotNull
    protected InjectorResult onApply(@Nullable Object targetData, @Nullable Object sourceData)
    {
        return InjectorResult.withModifiedObject(targetData);
    }
}
