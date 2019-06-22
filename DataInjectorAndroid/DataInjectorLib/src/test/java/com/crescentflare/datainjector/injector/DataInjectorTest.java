package com.crescentflare.datainjector.injector;


import com.crescentflare.datainjector.utility.InjectorUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Utility test: data injection utilities
 */
public class DataInjectorTest
{
    @Test
    @SuppressWarnings("all")
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
        DataInjector.Result result = DataInjector.inject(inventoryMap, "clothing.shirts.small", new DataInjector.ModifyCallback()
        {
            @Override
            public DataInjector.Result modify(Object originalData)
            {
                return DataInjector.Result.withModifiedObject("8.95");
            }
        });

        // Check that only the injection target has been modified
        Map<String, Map<String, Object>> resultMap = (Map<String, Map<String, Object>>)result.getModifiedObject();
        Map<String, Object> resultShirtMap = (Map<String, Object>)resultMap.get("clothing").get("shirts");
        Assert.assertEquals(resultShirtMap.get("small"), "8.95");
        Assert.assertEquals(resultShirtMap.get("large"), "11.95");

        // The original map structure shouldn't be modified
        Assert.assertSame(inventoryMap.get("tools"), toolsMap);
        Assert.assertSame(inventoryMap.get("clothing"), clothingMap);
        Assert.assertSame(clothingMap.get("shirts"), shirtsMap);
        Assert.assertEquals(shirtsMap.get("small"), "9.95");

        // The instances within the derived map structure in the result should only change if there was a modified object in it
        Assert.assertNotSame(resultMap, inventoryMap);
        Assert.assertNotSame(shirtsMap, resultMap.get("clothing").get("shirts"));
        Assert.assertNotSame(clothingMap, resultMap.get("clothing"));
        Assert.assertSame(toolsMap, resultMap.get("tools"));
    }

    @Test
    @SuppressWarnings("all")
    public void injectArrays() throws Exception
    {
        // Set up array objects
        List<Integer> evenNumbers = Arrays.asList(0, 2, 4, 6, 8);
        List<Integer> oddNumbers = Arrays.asList(1, 3, 6, 7, 9); // Intentionally put a wrong number here
        List<List<Integer>> nestedNumbers = Arrays.asList(evenNumbers, oddNumbers);

        // Apply manual injection to change the number
        DataInjector.Result result = DataInjector.inject(nestedNumbers, "1.2", new DataInjector.ModifyCallback()
        {
            @Override
            public DataInjector.Result modify(Object originalData)
            {
                return DataInjector.Result.withModifiedObject(5);
            }
        });

        // Check that only the injection target has been modified
        List<List<Integer>> resultList = (List<List<Integer>>)result.getModifiedObject();
        Assert.assertEquals(resultList.get(1).get(2).intValue(), 5);
        Assert.assertEquals(resultList.get(1).get(3).intValue(), 7);

        // The original array structure shouldn't be modified
        Assert.assertSame(nestedNumbers.get(0), evenNumbers);
        Assert.assertSame(nestedNumbers.get(1), oddNumbers);
        Assert.assertEquals(oddNumbers.get(2).intValue(), 6);

        // The instances within the derived array structure in the result should only change if there was a modified object in it
        Assert.assertNotSame(resultList, nestedNumbers);
        Assert.assertSame(resultList.get(0), evenNumbers);
        Assert.assertNotSame(resultList.get(1), oddNumbers);
    }
}
