package com.crescentflare.datainjectorexample.helper;

import com.crescentflare.datainjectorexample.R;

/**
 * The list of bitlets used in the app
 */
public class Bitlets
{
    public static MockBitlet customers = new MockBitlet(R.raw.customer_list, "customers");
    public static MockBitlet products = new MockBitlet(R.raw.product_list, "products");
}
