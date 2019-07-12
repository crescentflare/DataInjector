package com.crescentflare.datainjector.injector;


import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;
import com.crescentflare.datainjector.utility.InjectorUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Injector test: duplicate injection
 */
public class DuplicateInjectorTest
{
    // --
    // Test manual injection using a specified amount
    // --

    @Test
    public void duplicateItemWithCount() throws Exception
    {
        // Set up list
        List<Map<String, Object>> simpleList = Collections.singletonList(InjectorUtil.initMap(
                "key", "value",
                "counter", 0
        ));

        // Apply manual injection to duplicate the item and match the counter of the injected item with the index
        int duplicateCount = 3;
        InjectorResult result = DuplicateInjector.duplicateItem(simpleList, duplicateCount, new DuplicateInjector.CountCallback()
        {
            @Override
            public @NotNull InjectorResult onDuplicate(@Nullable Object duplicatedItem, final int duplicateIndex)
            {
                return DataInjector.inject(duplicatedItem, new InjectorPath("counter"), new DataInjector.ModifyCallback()
                {
                    @Override
                    public @NotNull InjectorResult modify(@Nullable Object originalData)
                    {
                        return InjectorResult.withModifiedObject(duplicateIndex);
                    }
                });
            }
        });

        // Verify amount of items
        List<Object> resultList = InjectorConv.asObjectList(result.getModifiedObject());
        Assert.assertEquals(resultList.size(), duplicateCount);

        // Verify the counter
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "0.counter"), 0);
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.counter"), 1);
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "2.counter"), 2);
    }


    // --
    // Test manual injection using a data source
    // --

    @Test
    public void duplicateItemWithData() throws Exception
    {
        // Set up list for modification
        List<Map<String, Object>> templateList = Collections.singletonList(InjectorUtil.initMap(
                "type", "vehicle",
                "name", "$template"
        ));

        // Set up list for the data source
        List<String> dataSource = Arrays.asList("Car", "Van", "Bike", "Truck");

        // Apply manual injection to duplicate the item and match the name value to the source item
        InjectorResult result = DuplicateInjector.duplicateItem(templateList, dataSource, new DuplicateInjector.DataCallback()
        {
            @Override
            public @NotNull InjectorResult onDuplicate(@Nullable Object duplicatedItem, @Nullable final Object sourceItem)
            {
                return DataInjector.inject(duplicatedItem, new InjectorPath("name"), new DataInjector.ModifyCallback()
                {
                    @Override
                    public @NotNull InjectorResult modify(@Nullable Object originalData)
                    {
                        return InjectorResult.withModifiedObject(sourceItem);
                    }
                });
            }
        });

        // Verify amount of items
        List<Object> resultList = InjectorConv.asObjectList(result.getModifiedObject());
        Assert.assertEquals(resultList.size(), dataSource.size());

        // Verify name insertion
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "0.name"), dataSource.get(0));
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.name"), dataSource.get(1));
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "2.name"), dataSource.get(2));
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "3.name"), dataSource.get(3));
    }


    // --
    // Test generic injection with a sub injector
    // --

    @Test
    public void apply() throws Exception
    {
        // Set up list for modification
        List<Map<String, Object>> templateList = Collections.singletonList(InjectorUtil.initMap(
                "title", "$template",
                "subItems", Arrays.asList(
                        InjectorUtil.initMap(
                                "type", "item",
                                "text", "template"
                        ),
                        InjectorUtil.initMap(
                                "type", "divider"
                        ),
                        InjectorUtil.initMap(
                                "type", "empty",
                                "text", "There are no items"
                        )
                )
        ));

        // Set up list for the data source
        List<Map<String, Object>> dataSource = Arrays.asList(
                InjectorUtil.initMap(
                        "section", "Colors",
                        "values", Arrays.asList("Red", "Green", "Blue", "Yellow", "Cyan", "Magenta")
                ),
                InjectorUtil.initMap(
                        "section", "Shapes",
                        "values", Arrays.asList("Square", "Circle")
                ),
                InjectorUtil.initMap(
                        "section", "Available",
                        "values", Collections.emptyList()
                )
        );

        // Set up main data injector for sections, including a custom injector to modify the section title
        DuplicateInjector injector = new DuplicateInjector();
        CustomInjector sectionValueInjector = new CustomInjector("title", "section");

        // Set up sub injector for sub items, including a custom injector to modify the text
        DuplicateInjector duplicateSubItemInjector = new DuplicateInjector();
        CustomInjector itemValueInjector = new CustomInjector("text");
        duplicateSubItemInjector.setTargetItemPath(new InjectorPath("subItems"));
        duplicateSubItemInjector.setSourceDataPath(new InjectorPath("values"));
        duplicateSubItemInjector.setBetweenItemIndex(1);
        duplicateSubItemInjector.setEmptyItemIndex(2);
        duplicateSubItemInjector.setSubInjectors(Collections.singletonList(itemValueInjector));

        // Link sub injectors and apply
        injector.setSubInjectors(Arrays.asList(sectionValueInjector, duplicateSubItemInjector));
        InjectorResult result = injector.apply(templateList, dataSource);

        // Verify amount of items and sub items
        List<Object> resultSectionArray = InjectorConv.asObjectList(result.getModifiedObject());
        Assert.assertEquals(resultSectionArray.size(), dataSource.size());
        Assert.assertEquals(InjectorConv.asObjectList(DataInjector.get(result.getModifiedObject(), "0.subItems")).size(), 6 * 2 - 1);
        Assert.assertEquals(InjectorConv.asObjectList(DataInjector.get(result.getModifiedObject(), "1.subItems")).size(), 2 * 2 - 1);
        Assert.assertEquals(InjectorConv.asObjectList(DataInjector.get(result.getModifiedObject(), "2.subItems")).size(), 1);

        // Verify some deep nested items
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.title"), "Shapes");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "0.subItems.2.text"), "Green");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "0.subItems.3.type"), "divider");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "1.subItems.0.text"), "Square");
        Assert.assertEquals(DataInjector.get(result.getModifiedObject(), "2.subItems.0.text"), "There are no items");
    }


    // --
    // Custom set value injector
    // --

    private class CustomInjector extends BaseInjector
    {
        private String targetKey;
        private String sourceKey = null;

        CustomInjector(String targetKey)
        {
            this.targetKey = targetKey;
        }

        CustomInjector(String targetKey, String sourceKey)
        {
            this.targetKey = targetKey;
            this.sourceKey = sourceKey;
        }

        @Override
        protected @NotNull InjectorResult onApply(@Nullable Object targetData, @Nullable final Object sourceData)
        {
            return DataInjector.inject(targetData, new InjectorPath(targetKey), new DataInjector.ModifyCallback()
            {
                @Override
                public @NotNull InjectorResult modify(@Nullable Object originalData)
                {
                    Object modifiedData = sourceData;
                    if (sourceKey != null)
                    {
                        modifiedData = DataInjector.get(sourceData, new InjectorPath(sourceKey));
                    }
                    return InjectorResult.withModifiedObject(modifiedData);
                }
            });
        }
    }
}
