package com.crescentflare.datainjector.utility;

/**
 * Data injector utility: a map entry
 * Used to easily initialize maps
 */
public class InjectorMapEntry<K, V>
{
    // --
    // Members
    // --

    private K key;
    private V value;


    // --
    // Initialization
    // --

    public InjectorMapEntry(K key, V value)
    {
        this.key = key;
        this.value = value;
    }


    // --
    // Obtain values
    // --

    public K getKey()
    {
        return key;
    }

    public V getValue()
    {
        return value;
    }
}
