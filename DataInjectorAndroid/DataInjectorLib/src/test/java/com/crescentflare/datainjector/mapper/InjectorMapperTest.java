package com.crescentflare.datainjector.mapper;

import com.crescentflare.datainjector.utility.InjectorMapEntry;
import com.crescentflare.datainjector.utility.InjectorUtil;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility test: data injection utilities
 */
public class InjectorMapperTest
{
    @Test
    public void obtainMapping() throws Exception
    {
        // Test simple mappings
        Assert.assertEquals(100, new InjectorMapper("[ first -> 80 , second -> 100 , third -> 23 ]").obtainMapping("second"));
        Assert.assertEquals(true, new InjectorMapper("['on'->true,'off'->false]").obtainMapping("on"));
        Assert.assertEquals("fork", new InjectorMapper("[1->'spoon',2->'fork',3->'knife']").obtainMapping(2));
        Assert.assertEquals("other", new InjectorMapper("[0->zero,1->one,2->two,else->other]").obtainMapping("fallback"));

        // Test mappings with references
        Map<String, Object> subMap = InjectorUtil.initMap(
                "twelve", 12,
                "thirteen", 13,
                "fourteen", 14
        );
        Map<String, Object> refMap = InjectorUtil.initMap(
                "four", 4,
                "five", 5,
                "six", 6,
                "subMap", subMap
        );
        Assert.assertEquals("dog", new InjectorMapper("[4->cat,5->dog,6->sheep,12->cow,13->goat,14->hamster,else->invalid]", refMap, subMap).obtainMapping("@five"));
        Assert.assertEquals("cow", new InjectorMapper("[4->cat,5->dog,6->sheep,12->cow,13->goat,14->hamster,else->invalid]", refMap, subMap).obtainMapping("@subMap.twelve"));
        Assert.assertEquals("hamster", new InjectorMapper("[4->cat,5->dog,6->sheep,12->cow,13->goat,14->hamster,else->invalid]", refMap, subMap).obtainMapping("@.fourteen"));
    }
}
