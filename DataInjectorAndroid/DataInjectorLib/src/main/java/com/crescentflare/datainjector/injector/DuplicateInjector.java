package com.crescentflare.datainjector.injector;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorPath;
import com.crescentflare.datainjector.utility.InjectorResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Data injector: duplicate an item
 * Duplicates an item in an array based on the amount of elements in a data source or an amount specified manually
 */
public class DuplicateInjector extends BaseInjector
{
    // --
    // Members
    // --

    private List<? extends BaseInjector> subInjectors = new ArrayList<>();
    private InjectorPath targetItemPath;
    private int duplicateItemIndex = 0;
    private int betweenItemIndex = -1;
    private int emptyItemIndex = -1;
    private InjectorPath sourceDataPath;
    private Object overrideSourceData;
    private int count = -1;


    // --
    // Initialization
    // --

    public DuplicateInjector()
    {
    }


    // --
    // Manual injection
    // --

    @NotNull
    public static InjectorResult duplicateItem(@Nullable Object targetList)
    {
        return duplicateItem(targetList, 2, 0, -1, -1, null);
    }

    @NotNull
    public static InjectorResult duplicateItem(@Nullable Object targetList, int count)
    {
        return duplicateItem(targetList, count, 0, -1, -1, null);
    }

    @NotNull
    public static InjectorResult duplicateItem(@Nullable Object targetList, int count, @Nullable CountCallback duplicateCallback)
    {
        return duplicateItem(targetList, count, 0, -1, -1, duplicateCallback);
    }

    @NotNull
    public static InjectorResult duplicateItem(@Nullable Object targetList, int count, int duplicateItemIndex, int betweenItemIndex, int emptyItemIndex, @Nullable CountCallback duplicateCallback)
    {
        List<Object> targetObjectList = InjectorConv.asObjectList(targetList);
        if (targetObjectList != null)
        {
            // Fetch items for duplication
            if (duplicateItemIndex < 0 || duplicateItemIndex >= targetObjectList.size())
            {
                return InjectorResult.withError(InjectorResult.Error.NotFound);
            }
            else if (betweenItemIndex >= 0 && betweenItemIndex >= targetObjectList.size())
            {
                return InjectorResult.withError(InjectorResult.Error.NotFound);
            }
            else if (emptyItemIndex >= 0 && emptyItemIndex >= targetObjectList.size())
            {
                return InjectorResult.withError(InjectorResult.Error.NotFound);
            }
            Object duplicateItem = targetObjectList.get(duplicateItemIndex);
            Object betweenItem = betweenItemIndex >= 0 && betweenItemIndex < targetObjectList.size() ? targetObjectList.get(betweenItemIndex) : null;
            Object emptyItem = emptyItemIndex >= 0 && emptyItemIndex < targetObjectList.size() ? targetObjectList.get(emptyItemIndex) : null;

            // Prepare new array without duplicate items
            List<Object> modifiedList = new ArrayList<>();
            for (int index = 0; index < targetObjectList.size(); index++)
            {
                if (index != duplicateItemIndex && index != betweenItemIndex && index != emptyItemIndex)
                {
                    modifiedList.add(targetObjectList.get(index));
                }
            }

            // Duplicate items
            int insertIndex = duplicateItemIndex;
            for (int i = 0; i < count; i++)
            {
                if (i > 0 && betweenItem != null)
                {
                    modifiedList.add(insertIndex, betweenItem);
                    insertIndex++;
                }
                if (duplicateCallback != null)
                {
                    InjectorResult result = duplicateCallback.onDuplicate(duplicateItem, i);
                    if (result.hasError())
                    {
                        return result;
                    }
                    modifiedList.add(insertIndex, result.getModifiedObject());
                }
                else
                {
                    modifiedList.add(insertIndex, duplicateItem);
                }
                insertIndex++;
            }
            if (count == 0 && emptyItem != null)
            {
                modifiedList.add(insertIndex, emptyItem);
            }
            return InjectorResult.withModifiedObject(modifiedList);
        }
        return InjectorResult.withError(InjectorResult.Error.TargetInvalid);
    }

    @NotNull
    public static InjectorResult duplicateItem(@Nullable Object targetList, @Nullable Object sourceList)
    {
        return duplicateItem(targetList, sourceList, 0, -1, -1, null);
    }

    @NotNull
    public static InjectorResult duplicateItem(@Nullable Object targetList, @Nullable Object sourceList, @Nullable DataCallback duplicateCallback)
    {
        return duplicateItem(targetList, sourceList, 0, -1, -1, duplicateCallback);
    }

    @NotNull
    public static InjectorResult duplicateItem(@Nullable Object targetList, @Nullable Object sourceList, int duplicateItemIndex, int betweenItemIndex, int emptyItemIndex, @Nullable final DataCallback duplicateCallback)
    {
        final List<Object> sourceObjectList = InjectorConv.asObjectList(sourceList);
        if (sourceObjectList != null)
        {
            return duplicateItem(targetList, sourceObjectList.size(), duplicateItemIndex, betweenItemIndex, emptyItemIndex, new CountCallback()
            {
                @Override
                @NotNull
                public InjectorResult onDuplicate(@Nullable Object duplicatedItem, int duplicateIndex)
                {
                    if (duplicateCallback != null)
                    {
                        return duplicateCallback.onDuplicate(duplicatedItem, sourceObjectList.get(duplicateIndex));
                    }
                    return InjectorResult.withModifiedObject(duplicatedItem);
                }
            });
        }
        return InjectorResult.withError(InjectorResult.Error.SourceInvalid);
    }


    // --
    // General injection
    // --

    @Override
    @NotNull
    protected InjectorResult onApply(@Nullable Object targetData, @Nullable Object sourceData)
    {
        // Use manual count when specified
        if (count >= 0)
        {
            return DataInjector.inject(targetData, targetItemPath != null ? targetItemPath : new InjectorPath(), new DataInjector.ModifyCallback()
            {
                @Override
                @NotNull
                public InjectorResult modify(@Nullable Object originalData)
                {
                    return DuplicateInjector.duplicateItem(originalData, count, duplicateItemIndex, betweenItemIndex, emptyItemIndex, null);
                }
            });
        }

        // Duplicate based on source data
        Object checkSourceData = overrideSourceData != null ? overrideSourceData : sourceData;
        final Object useSourceData = DataInjector.get(checkSourceData, sourceDataPath != null ? sourceDataPath : new InjectorPath());
        return DataInjector.inject(targetData, targetItemPath != null ? targetItemPath : new InjectorPath(), new DataInjector.ModifyCallback()
        {
            @Override
            @NotNull
            public InjectorResult modify(@Nullable Object originalData)
            {
                return DuplicateInjector.duplicateItem(originalData, useSourceData, duplicateItemIndex, betweenItemIndex, emptyItemIndex, new DataCallback()
                {
                    @Override
                    @NotNull
                    public InjectorResult onDuplicate(@Nullable Object duplicatedItem, @Nullable Object sourceItem)
                    {
                        Object modifiedDuplicatedItem = duplicatedItem;
                        for (BaseInjector subInjector : subInjectors)
                        {
                            InjectorResult result = subInjector.apply(modifiedDuplicatedItem, sourceItem);
                            if (result.hasError())
                            {
                                return result;
                            }
                            modifiedDuplicatedItem = result.getModifiedObject();
                        }
                        return InjectorResult.withModifiedObject(modifiedDuplicatedItem);
                    }
                });
            }
        });
    }


    // --
    // Set values
    // --

    public void setSubInjectors(@Nullable List<? extends BaseInjector> subInjectors)
    {
        this.subInjectors = subInjectors != null ? subInjectors : new ArrayList<BaseInjector>();
    }

    public void setTargetItemPath(@Nullable InjectorPath targetItemPath)
    {
        this.targetItemPath = targetItemPath;
    }

    public void setDuplicateItemIndex(int duplicateItemIndex)
    {
        this.duplicateItemIndex = duplicateItemIndex;
    }

    public void setBetweenItemIndex(int betweenItemIndex)
    {
        this.betweenItemIndex = betweenItemIndex;
    }

    public void setEmptyItemIndex(int emptyItemIndex)
    {
        this.emptyItemIndex = emptyItemIndex;
    }

    public void setSourceDataPath(@Nullable InjectorPath sourceDataPath)
    {
        this.sourceDataPath = sourceDataPath;
    }

    public void setOverrideSourceData(@Nullable Object overrideSourceData)
    {
        this.overrideSourceData = overrideSourceData;
    }

    public void setCount(int count)
    {
        this.count = count;
    }


    // --
    // Callback interfaces
    // --

    public interface CountCallback
    {
        @NotNull
        InjectorResult onDuplicate(@Nullable Object duplicatedItem, int duplicateIndex);
    }

    public interface DataCallback
    {
        @NotNull
        InjectorResult onDuplicate(@Nullable Object duplicatedItem, @Nullable Object sourceItem);
    }
}
