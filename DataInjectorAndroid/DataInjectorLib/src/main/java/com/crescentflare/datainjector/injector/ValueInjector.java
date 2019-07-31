package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.transformer.BaseTransformer;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Data injector: set a value
 * A simple injector to set a value manually, or based on a data source
 */
public class ValueInjector extends BaseInjector
{
    // --
    // Members
    // --

    private List<? extends BaseTransformer> sourceTransformers = new ArrayList<>();
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
    public static InjectorResult setValue(@Nullable Object data, @NotNull InjectorPath path, @Nullable Object value)
    {
        return setValue(data, path, value, true);
    }

    @NotNull
    public static InjectorResult setValue(@Nullable Object data, @NotNull InjectorPath path, @Nullable final Object value, boolean allowNull)
    {
        if (!allowNull && value == null)
        {
            return InjectorResult.withError(InjectorResult.Error.NullNotAllowed);
        }
        return DataInjector.inject(data, path, new DataInjector.ModifyCallback()
        {
            @Override
            @NotNull
            public InjectorResult modify(@Nullable Object originalData)
            {
                return InjectorResult.withModifiedObject(value);
            }
        });
    }


    // --
    // General injection
    // --

    @Override
    @NotNull
    protected InjectorResult onApply(@Nullable Object targetData, @Nullable Object sourceData)
    {
        // Use manual value when specified
        if (value != null)
        {
            return ValueInjector.setValue(targetData, targetItemPath != null ? targetItemPath : new InjectorPath(), value, allowNull);
        }

        // Prepare source data with optional transformation
        Object checkSourceData = DataInjector.get(sourceData, sourceDataPath != null ? sourceDataPath : new InjectorPath());
        for (BaseTransformer transformer : sourceTransformers)
        {
            InjectorResult result = transformer.apply(checkSourceData);
            if (result.hasError())
            {
                return result;
            }
            checkSourceData = result.getModifiedObject();
        }

        // Set value based on source data
        return ValueInjector.setValue(targetData, targetItemPath != null ? targetItemPath : new InjectorPath(), checkSourceData, allowNull);
    }


    // --
    // Set values
    // --

    public void setSourceTransformers(@Nullable List<? extends BaseTransformer> sourceTransformers)
    {
        this.sourceTransformers = sourceTransformers != null ? sourceTransformers : new ArrayList<BaseTransformer>();
    }

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
