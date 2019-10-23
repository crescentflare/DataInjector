package com.crescentflare.datainjector.transformer;


import com.crescentflare.datainjector.injector.DataInjector;
import com.crescentflare.datainjector.injector.ValueInjector;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Transformation test: stringify transformer
 */
public class InjectTransformerTest
{
    // --
    // Test manual transformation
    // --

    @Test
    public void inject() throws Exception
    {
        // Set up data
        Map<String, Object> sampleData = InjectorUtil.initMap(
                "firstName", "John",
                "middleName", null,
                "lastName", "Doe"
        );

        // Apply manual transformation to inject a value
        ValueInjector valueInjector = new ValueInjector();
        valueInjector.setTargetItemPath(new InjectorPath("fullName"));
        valueInjector.setValue("John Doe");
        InjectorResult result = InjectTransformer.inject(sampleData, Collections.singletonList(valueInjector));

        // Verify the result
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), new InjectorPath("fullName")), "John Doe");
    }


    // --
    // Test generic transformation
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up data
        Map<String, Object> sampleData = InjectorUtil.initMap(
                "firstName", "John",
                "middleName", null,
                "lastName", "Doe"
        );

        // Set up injector with transformer
        ValueInjector valueInjector = new ValueInjector();
        JoinStringTransformer joinStringTransformer = new JoinStringTransformer();
        valueInjector.setTargetItemPath(new InjectorPath("fullName"));
        joinStringTransformer.setFromItems(Arrays.asList("firstName", "middleName", "lastName"));
        joinStringTransformer.setDelimiter(" ");
        valueInjector.setSourceTransformers(Collections.singletonList(joinStringTransformer));

        // Set up transformer
        InjectTransformer transformer = new InjectTransformer();
        transformer.setInjectors(Collections.singletonList(valueInjector));

        // Apply
        InjectorResult result = transformer.apply(sampleData);

        // Verify the result
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), new InjectorPath("fullName")), "John Doe");
    }
}
