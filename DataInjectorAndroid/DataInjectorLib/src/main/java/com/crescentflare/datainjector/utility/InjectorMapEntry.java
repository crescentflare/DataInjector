package com.crescentflare.datainjector.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data injector utility: a map entry
 * Used to easily initialize maps
 */
public class InjectorMapEntry<K, V>
{
    // ---
    // Members
    // ---

    private K key;
    private V value;

    // ---
    // Initialization
    // ---

    public InjectorMapEntry(K key, V value)
    {
        this.key = key;
        this.value = value;
    }


    // ---
    // Obtain values
    // ---

    public K getKey()
    {
        return key;
    }

    public V getValue()
    {
        return value;
    }
}
