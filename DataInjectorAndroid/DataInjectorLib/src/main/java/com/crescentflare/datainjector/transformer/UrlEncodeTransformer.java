package com.crescentflare.datainjector.transformer;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.injector.DataInjector;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Data injector transformation: URL encode a string
 * Convert data to a URL encoded string
 */
public class UrlEncodeTransformer extends BaseTransformer
{
    // --
    // Members
    // --

    private InjectorPath sourceDataPath;


    // --
    // Initialization
    // --

    public UrlEncodeTransformer()
    {
    }


    // --
    // Manual transformation
    // --

    @NotNull
    public static InjectorResult encode(@Nullable Object data)
    {
        String string = InjectorConv.asString(data);
        if (string != null)
        {
            try
            {
                String result = URLEncoder.encode(string, "UTF8");
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
        return encode(useSourceData);
    }


    // --
    // Set values
    // --

    public void setSourceDataPath(@Nullable InjectorPath sourceDataPath)
    {
        this.sourceDataPath = sourceDataPath;
    }
}
