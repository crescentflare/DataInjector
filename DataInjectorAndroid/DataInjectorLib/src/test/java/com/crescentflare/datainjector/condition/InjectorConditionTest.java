package com.crescentflare.datainjector.condition;

import com.crescentflare.datainjector.mapper.InjectorMapper;
import com.crescentflare.datainjector.utility.InjectorUtil;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Map;

/**
 * Utility test: data condition object
 */
public class InjectorConditionTest
{
    @Test
    public void isMet() throws Exception
    {
        // Test value interpretation
        Assert.assertTrue(new InjectorCondition("true").isMet());
        Assert.assertFalse(new InjectorCondition("false").isMet());
        Assert.assertTrue(new InjectorCondition("20").isMet());
        Assert.assertFalse(new InjectorCondition("0").isMet());

        // Test simple comparisons
        Assert.assertTrue(new InjectorCondition("'string' == 'string'").isMet());
        Assert.assertFalse(new InjectorCondition("apple == pear").isMet());
        Assert.assertTrue(new InjectorCondition("apple != pear").isMet());
        Assert.assertTrue(new InjectorCondition("'20.5' > 10").isMet());
        Assert.assertTrue(new InjectorCondition("16 < 215").isMet());
        Assert.assertTrue(new InjectorCondition("14 >= '14'").isMet());

        // Test conditions with operators
        Assert.assertTrue(new InjectorCondition("true != 'false' && 10 > 9").isMet());
        Assert.assertFalse(new InjectorCondition("true == false || 4 <= 3").isMet());
        Assert.assertTrue(new InjectorCondition("4 >= 4 && 'string' == string && 9 < 13 && true").isMet());

        // Test conditions with references
        Map<String, Object> subMap = InjectorUtil.initMap(
                "one", 1,
                "two", 2,
                "three", 3,
                "four", 4
        );
        Map<String, Object> refMap = InjectorUtil.initMap(
                "first", 1,
                "second", 2,
                "third", 3,
                "fourth", 4,
                "subMap", subMap
        );
        Assert.assertTrue(new InjectorCondition("@first < @.three && @subMap.four == @fourth", refMap, subMap).isMet());
    }
}
