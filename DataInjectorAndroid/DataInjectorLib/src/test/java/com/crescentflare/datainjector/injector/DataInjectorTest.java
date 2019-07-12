package com.crescentflare.datainjector.injector;


import com.crescentflare.datainjector.utility.InjectorResult;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Injector test: general data injection
 */
public class DataInjectorTest
{
    @Test
    public void injectMaps() throws Exception
    {
        // Set up map objects
        Map<String, Object> toolsMap = InjectorUtil.initMap(
                "hammer", "9.95",
                "screwDriver", "5.45"
        );
        Map<String, Object> shirtsMap = InjectorUtil.initMap(
                "small", "9.95",
                "medium", "10.95",
                "large", "11.95"
        );
        Map<String, Object> clothingMap = InjectorUtil.initMap(
                "shirts", shirtsMap,
                "trousers", "49.95",
                "jacket", "79.95"
        );
        Map<String, Object> inventoryMap = InjectorUtil.initMap(
                "tools", toolsMap,
                "clothing", clothingMap
        );

        // Apply manual injection to change the price
        InjectorResult result = DataInjector.inject(inventoryMap, "clothing.shirts.small", new DataInjector.ModifyCallback()
        {
            @Override
            @NotNull
            public InjectorResult modify(Object originalData)
            {
                return InjectorResult.withModifiedObject("8.95");
            }
        });

        // Check that only the injection target has been modified
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "clothing.shirts.small"), "8.95");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "clothing.shirts.large"), "11.95");

        // The original map structure shouldn't be modified
        Assert.assertSame(inventoryMap.get("tools"), toolsMap);
        Assert.assertSame(inventoryMap.get("clothing"), clothingMap);
        Assert.assertSame(clothingMap.get("shirts"), shirtsMap);
        Assert.assertEquals(shirtsMap.get("small"), "9.95");

        // The instances within the derived map structure in the result should only change if there was a modified object in it
        Assert.assertNotSame(result.getModifiedObject(), inventoryMap);
        Assert.assertNotSame(shirtsMap, DataInjector.get(result.getModifiedObject(), "clothing.shirts"));
        Assert.assertNotSame(clothingMap, DataInjector.get(result.getModifiedObject(), "clothing"));
        Assert.assertSame(toolsMap, DataInjector.get(result.getModifiedObject(), "tools"));
    }

    @Test
    public void injectArrays() throws Exception
    {
        // Set up array objects
        List<Integer> evenNumbers = Arrays.asList(0, 2, 4, 6, 8);
        List<Integer> oddNumbers = Arrays.asList(1, 3, 6, 7, 9); // Intentionally put a wrong number here
        List<List<Integer>> nestedNumbers = Arrays.asList(evenNumbers, oddNumbers);

        // Apply manual injection to change the number
        InjectorResult result = DataInjector.inject(nestedNumbers, "1.2", new DataInjector.ModifyCallback()
        {
            @Override
            @NotNull
            public InjectorResult modify(Object originalData)
            {
                return InjectorResult.withModifiedObject(5);
            }
        });

        // Check that only the injection target has been modified
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.2"), 5);
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.3"), 7);

        // The original array structure shouldn't be modified
        Assert.assertSame(nestedNumbers.get(0), evenNumbers);
        Assert.assertSame(nestedNumbers.get(1), oddNumbers);
        Assert.assertEquals(oddNumbers.get(2).intValue(), 6);

        // The instances within the derived array structure in the result should only change if there was a modified object in it
        Assert.assertNotSame(result.getModifiedObject(), nestedNumbers);
        Assert.assertSame(DataInjector.get(result.getModifiedObject(), "0"), evenNumbers);
        Assert.assertNotSame(DataInjector.get(result.getModifiedObject(), "1"), oddNumbers);
    }

    @Test
    public void injectMixed() throws Exception
    {
        // Set up structure
        List<Integer> numberSequence = Arrays.asList(0, 1, 2, 3, 5, 8);
        Map<String, Object> dictionary = InjectorUtil.initMap(
                "first", "1st",
                "second", "4th",
                "third", "3rd"
        );
        List<Object> randomItems = Arrays.asList(numberSequence, dictionary);

        // Apply manual injection to change the dictionary
        InjectorResult result = DataInjector.inject(randomItems, "1.second", new DataInjector.ModifyCallback()
        {
            @Override
            @NotNull
            public InjectorResult modify(Object originalData)
            {
                return InjectorResult.withModifiedObject("2nd");
            }
        });

        // Check the result
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.second"), "2nd");
    }
}
