package com.crescentflare.datainjector.transformer;

import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data injector transformation: the transformer base class
 * Classes derived from this base class can be used as a data transformer
 */
public class BaseTransformer
{
    // --
    // Initialization
    // --

    public BaseTransformer()
    {
    }


    // --
    // Apply overloads
    // --

    @NotNull
    public final InjectorResult apply(@Nullable Object sourceData)
    {
        return onApply(sourceData);
    }


    // --
    // Functions to implement
    // --

    @NotNull
    protected InjectorResult onApply(@Nullable Object sourceData)
    {
        return InjectorResult.withModifiedObject(sourceData);
    }
}
