package com.crescentflare.datainjector.transformer;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.injector.DataInjector;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Data injector transformation: URL decode a string
 * Convert data to a URL decoded string
 */
public class UrlDecodeTransformer extends BaseTransformer
{
    // --
    // Members
    // --

    private InjectorPath sourceDataPath;


    // --
    // Initialization
    // --

    public UrlDecodeTransformer()
    {
    }


    // --
    // Manual transformation
    // --

    @NotNull
    public static InjectorResult decode(@Nullable Object data)
    {
        String string = InjectorConv.asString(data);
        if (string != null)
        {
            try
            {
                String result = URLDecoder.decode(string, "UTF8");
                return InjectorResult.withModifiedObject(result);
            }
            catch (UnsupportedEncodingException ignored)
            {
            }
        }
        return InjectorResult.withError(InjectorResult.Error.SourceInvalid);
    }


    // --
    // General transformation
    // --

    @Override
    @NotNull
    protected InjectorResult onApply(@Nullable Object sourceData)
    {
        Object useSourceData = DataInjector.get(sourceData, sourceDataPath != null ? sourceDataPath : new InjectorPath());
        return decode(useSourceData);
    }


    // --
    // Set values
    // --

    public void setSourceDataPath(@Nullable InjectorPath sourceDataPath)
    {
        this.sourceDataPath = sourceDataPath;
    }
}
