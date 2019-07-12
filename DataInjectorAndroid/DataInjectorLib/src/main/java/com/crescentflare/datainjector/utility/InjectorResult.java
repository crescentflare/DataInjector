package com.crescentflare.datainjector.utility;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data injector utility: a result after data injection
 * Stores the result of a data injection which can be a modified object or an error
 */
public final class InjectorResult
{
    // --
    // Members
    // --

    private Object modifiedObject;
    private Error error;
    private Object customInfo;


    // --
    // Initialization
    // --

    private InjectorResult()
    {
        // Private constructor, should be created with factory methods
    }

    @NotNull
    public static InjectorResult withModifiedObject(@Nullable Object modifiedObject)
    {
        InjectorResult result = new InjectorResult();
        result.modifiedObject = modifiedObject;
        return result;
    }

    @NotNull
    public static InjectorResult withError(@NotNull Error error)
    {
        InjectorResult result = new InjectorResult();
        result.error = error;
        return result;
    }

    @SuppressWarnings("unused")
    @NotNull
    public static InjectorResult withCustomError(@NotNull Object customInfo)
    {
        InjectorResult result = new InjectorResult();
        result.error = Error.Custom;
        result.customInfo = customInfo;
        return result;
    }


    // --
    // Obtain values
    // --

    @Nullable
    public Object getModifiedObject()
    {
        return modifiedObject;
    }

    @Nullable
    public Error getError()
    {
        return error;
    }

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


    // --
    // Error enum
    // --

    public enum Error
    {
        Unknown,
        NoIndexedCollection,
        IndexInvalid,
        TargetInvalid,
        SourceInvalid,
        NotFound,
        NullNotAllowed,
        Custom
    }
}
