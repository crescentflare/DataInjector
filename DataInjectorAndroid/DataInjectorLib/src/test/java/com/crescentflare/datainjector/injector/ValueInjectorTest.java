package com.crescentflare.datainjector.injector;


import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Injector test: value injection
 */
public class ValueInjectorTest
{
    // --
    // Test manual injection
    // --

    @Test
    public void setValue() throws Exception
    {
        // Set up map
        Map<String, Object> nestedMap = InjectorUtil.initMap(
                "San Francisco", InjectorUtil.initMap(
                        "country", "USA",
                        "language", "English"
                ),
                "Nice", InjectorUtil.initMap(
                        "country", "France",
                        "language", "French"
                ),
                "Madrid", InjectorUtil.initMap(
                        "country", "Spain",
                        "language", "Spanish"
                )
        );

        // Apply manual injection to change the language of one of the cities
        InjectorResult result = ValueInjector.setValue(nestedMap, new InjectorPath("Madrid.language"), "German");

        // Verify the change
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "Madrid.language"), "German");

        // Null value tests
        Assert.assertNull(ValueInjector.setValue(nestedMap, new InjectorPath("Nice.language"), null, true).getError());
        Assert.assertEquals(ValueInjector.setValue(nestedMap, new InjectorPath("Nice.language"), null, false).getError(), InjectorResult.Error.NullNotAllowed);
    }


    // --
    // Test generic injection
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up array for modification
        List<Map<String, Object>> templateArray = Arrays.asList(
                InjectorUtil.initMap(
                        "title", "$template",
                        "text", "$template"
                ),
                InjectorUtil.initMap(
                        "title", "$template",
                        "text", "$template"
                )
        );

        // Set up array for the data source
        List<Map<String, Object>> dataSource = Arrays.asList(
                InjectorUtil.initMap(
                        "product", "Apple",
                        "description", "A juicy apple, freshly picked"
                ),
                InjectorUtil.initMap(
                        "product", "Strawberry",
                        "description", "A set of tasty strawberries, available this week"
                )
        );

        // Set up the injectors for the 2 array items
        List<BaseInjector> injectors = new ArrayList<>();
        for (int productIndex = 0; productIndex < dataSource.size(); productIndex++)
        {
            ValueInjector titleInjector = new ValueInjector();
            ValueInjector textInjector = new ValueInjector();
            titleInjector.setTargetItemPath(new InjectorPath("" + productIndex + ".title"));
            titleInjector.setSourceDataPath(new InjectorPath("" + productIndex + ".product"));
            textInjector.setTargetItemPath(new InjectorPath("" + productIndex + ".text"));
            textInjector.setSourceDataPath(new InjectorPath("" + productIndex + ".description"));
            injectors.add(titleInjector);
            injectors.add(textInjector);
        }

        // Apply
        InjectorResult result = InjectorResult.withModifiedObject(templateArray);
        for (BaseInjector injector : injectors)
        {
            result = injector.apply(result.getModifiedObject(), dataSource);
        }

        // Verify titles and descriptions
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "0.title"), "Apple");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "0.text"), "A juicy apple, freshly picked");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.title"), "Strawberry");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.text"), "A set of tasty strawberries, available this week");
    }
}
