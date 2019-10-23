package com.crescentflare.datainjector.transformer;


import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Transformation test: URL encode transformer
 */
public class UrlDecodeTransformerTest
{
    // --
    // Test manual transformation
    // --

    @Test
    public void encode() throws Exception
    {
        InjectorResult result = UrlDecodeTransformer.decode("First+%2F+Second");
        Assert.assertEquals(result.getModifiedObject(), "First / Second");
    }


    // --
    // Test generic transformation
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up data
        Map<String, Object> sampleData = InjectorUtil.initMap(
                "value", "First+%2F+Second"
        );

        // Set up transformer and apply
        UrlDecodeTransformer transformer = new UrlDecodeTransformer();
        transformer.setSourceDataPath(new InjectorPath("value"));
        InjectorResult result = transformer.apply(sampleData);

        // Verify result
        Assert.assertEquals(result.getModifiedObject(), "First / Second");
    }
}
