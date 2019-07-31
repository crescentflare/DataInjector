package com.crescentflare.datainjector.transformer;


import com.crescentflare.datainjector.utility.InjectorResult;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Transformation test: join string transformer
 */
public class JoinStringTransformerTest
{
    // --
    // Test manual joining string with a list
    // --

    @Test
    public void joinStringFromList() throws Exception
    {
        // Set up list
        List<String> sampleList = Arrays.asList("Jack", "the", "Joker");

        // Apply manual injection to join the strings
        InjectorResult result = JoinStringTransformer.joinString(sampleList, " ");

        // Verify the result
        Assert.assertEquals(result.getModifiedObject(), "Jack the Joker");
    }


    // --
    // Test manual joining string with a map
    // --

    @Test
    public void joinStringFromMap() throws Exception
    {
        // Set up map
        Map<String, Object> sampleMap = InjectorUtil.initMap(
                "firstName", "John",
                "middleName", null,
                "lastName", "Doe"
        );

        // Apply manual injection to join the strings
        InjectorResult result = JoinStringTransformer.joinString(sampleMap, Arrays.asList("firstName", "middleName", "lastName"), " ");

        // Verify the result
        Assert.assertEquals(result.getModifiedObject(), "John Doe");
    }


    // --
    // Test generic transformation
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up map for modification
        Map<String, Object> sampleMap = InjectorUtil.initMap(
                "firstName", "Jack",
                "middleName", "the",
                "lastName", "Joker",
                "address", "Injectorstreet 200",
                "city", "Washington"
        );

        // Set up injector
        JoinStringTransformer transformer = new JoinStringTransformer();
        transformer.setFromItems(Arrays.asList("firstName", "middleName", "lastName"));
        transformer.setDelimiter(" ");

        // Apply
        InjectorResult result = transformer.apply(sampleMap);

        // Verify values
        Assert.assertEquals(result.getModifiedObject(), "Jack the Joker");
    }
}
