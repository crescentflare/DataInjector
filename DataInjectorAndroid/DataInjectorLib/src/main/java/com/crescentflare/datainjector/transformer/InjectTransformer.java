package com.crescentflare.datainjector.transformer;

import com.crescentflare.datainjector.injector.BaseInjector;
import com.crescentflare.datainjector.injector.DataInjector;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Data injector transformation: inject data
 * Inject values into the source data
 */
public class InjectTransformer extends BaseTransformer
{
    // --
    // Members
    // --

    private InjectorPath sourceDataPath;
    private List<? extends BaseInjector> injectors = new ArrayList<>();


    // --
    // Initialization
    // --

    public InjectTransformer()
    {
    }


    // --
    // Manual transformation
    // --

    @NotNull
    public static InjectorResult inject(@Nullable Object data, List<? extends BaseInjector> injectors)
    {
        Object result = data;
        for (BaseInjector injector : injectors)
        {
            InjectorResult injectorResult = injector.apply(result, result);
            if (injectorResult.hasError())
            {
                return injectorResult;
            }
            result = injectorResult.getModifiedObject();
        }
        return InjectorResult.withModifiedObject(result);
    }


    // --
    // General transformation
    // --

    @Override
    @NotNull
    protected InjectorResult onApply(@Nullable Object sourceData)
    {
        Object useSourceData = DataInjector.get(sourceData, sourceDataPath != null ? sourceDataPath : new InjectorPath());
        return inject(useSourceData, injectors);
    }


    // --
    // Set values
    // --

    public void setSourceDataPath(@Nullable InjectorPath sourceDataPath)
    {
        this.sourceDataPath = sourceDataPath;
    }

    public void setInjectors(@Nullable List<? extends BaseInjector> injectors)
    {
        this.injectors = injectors != null ? injectors : new ArrayList<BaseInjector>();
    }
}
