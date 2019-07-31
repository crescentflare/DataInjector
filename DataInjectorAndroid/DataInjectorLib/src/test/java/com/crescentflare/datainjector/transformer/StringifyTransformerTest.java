package com.crescentflare.datainjector.transformer;


import com.crescentflare.datainjector.utility.InjectorResult;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Transformation test: stringify transformer
 */
public class StringifyTransformerTest
{
    // --
    // Test manual transformation
    // --

    @Test
    public void stringify() throws Exception
    {
        // Set up data
        Map<String, Object> sampleData = InjectorUtil.initMap(
                "name", "MacBook Pro",
                "type", "laptop",
                "brand", "Apple",
                "features", Arrays.asList("Fingerprint", "Touchbar", "Camera"),
                "price", InjectorUtil.initMap(
                        "currency", "USD",
                        "value", 1299
                )
        );

        // Test default string conversion (full string checking is not possible due to the random order of the map elements)
        InjectorResult result = StringifyTransformer.stringify(sampleData);
        Assert.assertTrue(((String)result.getModifiedObject()).contains("\"name\":\"MacBook Pro\""));
        Assert.assertTrue(((String)result.getModifiedObject()).contains("\"features\":[\"Fingerprint\",\"Touchbar\",\"Camera\"]"));
        Assert.assertTrue(((String)result.getModifiedObject()).contains("\"value\":1299"));

        // Test conversion with spaces
        result = StringifyTransformer.stringify(sampleData, true);
        Assert.assertTrue(((String)result.getModifiedObject()).contains("\"type\": \"laptop\""));
        Assert.assertTrue(((String)result.getModifiedObject()).contains("\"features\": [\"Fingerprint\", \"Touchbar\", \"Camera\"]"));
        Assert.assertTrue(((String)result.getModifiedObject()).contains("\"currency\": \"USD\""));

        // Test conversion with newlines
        result = StringifyTransformer.stringify(sampleData, true, true);
        Assert.assertTrue(((String)result.getModifiedObject()).contains("\"brand\": \"Apple\""));
        Assert.assertTrue(((String)result.getModifiedObject()).contains("{\n  "));
        Assert.assertTrue(((String)result.getModifiedObject()).contains("  \"price\": {\n    "));
        Assert.assertTrue(((String)result.getModifiedObject()).contains("    \"value\": 1299"));
        Assert.assertTrue(((String)result.getModifiedObject()).contains("  \"features\": [\n    \"Fingerprint\",\n    \"Touchbar\",\n    \"Camera\"\n  ]"));
    }


    // --
    // Test generic transformation
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up data
        List<Map<String, Object>> sampleData = Arrays.asList(
                InjectorUtil.initMap(
                        "type", "desktop"
                ),
                InjectorUtil.initMap(
                        "type", "laptop"
                ),
                InjectorUtil.initMap(
                        "type", "tablet"
                )
        );

        // Set up transformer
        StringifyTransformer transformer = new StringifyTransformer();
        transformer.setIncludeSpaces(true);
        transformer.setIncludeNewlines(true);

        // Apply
        InjectorResult result = transformer.apply(sampleData);

        // Verify result
        Assert.assertEquals(result.getModifiedObject(), "[\n  {\n    \"type\": \"desktop\"\n  },\n  {\n    \"type\": \"laptop\"\n  },\n  {\n    \"type\": \"tablet\"\n  }\n]");
    }
}
