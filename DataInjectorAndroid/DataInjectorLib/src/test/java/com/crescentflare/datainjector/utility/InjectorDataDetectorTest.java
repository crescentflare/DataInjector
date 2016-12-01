package com.crescentflare.datainjector.utility;

import com.crescentflare.datainjector.mapper.InjectorMapper;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Map;

/**
 * Utility test: data detection utilities
 */
public class InjectorDataDetectorTest
{
    @Test
    public void detectFromObject() throws Exception
    {
        Assert.assertEquals(InjectorDataType.String, InjectorDataDetector.detectFromObject("test"));
        Assert.assertEquals(InjectorDataType.Number, InjectorDataDetector.detectFromObject(11));
        Assert.assertEquals(InjectorDataType.DecimalNumber, InjectorDataDetector.detectFromObject(16.1f));
        Assert.assertEquals(InjectorDataType.DecimalNumber, InjectorDataDetector.detectFromObject(231.12));
        Assert.assertEquals(InjectorDataType.Boolean, InjectorDataDetector.detectFromObject(true));
    }

    @Test
    public void detectFromString() throws Exception
    {
        Assert.assertEquals(InjectorDataType.String, InjectorDataDetector.detectFromString("'23 items'"));
        Assert.assertEquals(InjectorDataType.String, InjectorDataDetector.detectFromString("Freeform string"));
        Assert.assertEquals(InjectorDataType.Number, InjectorDataDetector.detectFromObject("16"));
        Assert.assertEquals(InjectorDataType.DecimalNumber, InjectorDataDetector.detectFromObject("231.12"));
        Assert.assertEquals(InjectorDataType.Boolean, InjectorDataDetector.detectFromObject("true"));
        Assert.assertEquals(InjectorDataType.Reference, InjectorDataDetector.detectFromObject("@reference"));
        Assert.assertEquals(InjectorDataType.SubReference, InjectorDataDetector.detectFromObject("@.subReference"));
    }

    @Test
    public void endOfTypeTypeString() throws Exception
    {
        Assert.assertEquals(15, InjectorDataDetector.endOfTypeTypeString(InjectorDataType.String, "'Quoted string'"));
        Assert.assertEquals(-1, InjectorDataDetector.endOfTypeTypeString(InjectorDataType.String, "Freeform string 16"));
        Assert.assertEquals(2, InjectorDataDetector.endOfTypeTypeString(InjectorDataType.Number, "24 string"));
        Assert.assertEquals(4, InjectorDataDetector.endOfTypeTypeString(InjectorDataType.DecimalNumber, "19.9,16.2"));
        Assert.assertEquals(5, InjectorDataDetector.endOfTypeTypeString(InjectorDataType.Boolean, "false or true"));
        Assert.assertEquals(-1, InjectorDataDetector.endOfTypeTypeString(InjectorDataType.Reference, "@reference"));
        Assert.assertEquals(-1, InjectorDataDetector.endOfTypeTypeString(InjectorDataType.SubReference, "@subReference"));
    }
}
