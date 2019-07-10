package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data injector: set a value
 * A simple injector to set a value manually, or based on a data source
 */
public class ValueInjector extends BaseInjector
{
    // --
    // Members
    // --

    private InjectorPath targetItemPath;
    private InjectorPath sourceDataPath;
    private Object value;
    private boolean allowNull = true;


    // --
    // Initialization
    // --

    public ValueInjector()
    {
    }


    // --
    // Manual injection
    // --

    @NotNull
    public static InjectorResult setValue(@Nullable Object inData, @NotNull InjectorPath path, @Nullable Object value)
    {
        return setValue(inData, path, value, true);
    }

    @NotNull
    public static InjectorResult setValue(@Nullable Object inData, @NotNull InjectorPath path, @Nullable final Object value, boolean allowNull)
    {
        if (!allowNull && value == null)
        {
            return InjectorResult.withError(InjectorResult.Error.NullNotAllowed);
        }
        return DataInjector.inject(inData, path, new DataInjector.ModifyCallback()
        {
            @Override
            public InjectorResult modify(@Nullable Object originalData)
            {
                return InjectorResult.withModifiedObject(value);
            }
        });
    }


    // --
    // General injection
    // --

    @NotNull
    protected InjectorResult onApply(@Nullable Object targetData, @Nullable Object sourceData)
    {
        // Use manual value when specified
        if (value != null)
        {
            return ValueInjector.setValue(targetData, targetItemPath != null ? targetItemPath : new InjectorPath(), value, allowNull);
        }

        // Set value based on source data
        Object sourceValue = DataInjector.get(sourceData, sourceDataPath != null ? sourceDataPath : new InjectorPath());
        return ValueInjector.setValue(targetData, targetItemPath != null ? targetItemPath : new InjectorPath(), sourceValue, allowNull);
    }


    // --
    // Set values
    // --

    public void setTargetItemPath(@Nullable InjectorPath targetItemPath)
    {
        this.targetItemPath = targetItemPath;
    }

    public void setSourceDataPath(@Nullable InjectorPath sourceDataPath)
    {
        this.sourceDataPath = sourceDataPath;
    }

    public void setValue(@Nullable Object value)
    {
        this.value = value;
    }

    public void setAllowNull(boolean allowNull)
    {
        this.allowNull = allowNull;
    }
}
