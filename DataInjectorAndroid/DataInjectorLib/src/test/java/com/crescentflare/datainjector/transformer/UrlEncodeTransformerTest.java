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
public class UrlEncodeTransformerTest
{
    // --
    // Test manual transformation
    // --

    @Test
    public void encode() throws Exception
    {
        InjectorResult result = UrlEncodeTransformer.encode("First / Second");
        Assert.assertEquals(result.getModifiedObject(), "First+%2F+Second");
    }


    // --
    // Test generic transformation
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up data
        Map<String, Object> sampleData = InjectorUtil.initMap(
                "value", "First / Second"
        );

        // Set up transformer and apply
        UrlEncodeTransformer transformer = new UrlEncodeTransformer();
        transformer.setSourceDataPath(new InjectorPath("value"));
        InjectorResult result = transformer.apply(sampleData);

        // Verify result
        Assert.assertEquals(result.getModifiedObject(), "First+%2F+Second");
    }
}
