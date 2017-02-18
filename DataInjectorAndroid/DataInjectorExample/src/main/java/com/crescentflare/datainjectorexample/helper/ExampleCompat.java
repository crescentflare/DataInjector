package com.crescentflare.datainjectorexample.helper;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * A utility class to provide compat functions for things missing in the compat library
 */
public class ExampleCompat
{
    public static void setBackgroundDrawable(View view, Drawable drawable)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            view.setBackground(drawable);
        }
        else
        {
            view.setBackgroundDrawable(drawable);
        }
    }
}
