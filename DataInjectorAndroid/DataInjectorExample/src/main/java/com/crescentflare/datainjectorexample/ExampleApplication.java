package com.crescentflare.datainjectorexample;

import android.app.Application;
import android.content.Context;

import com.crescentflare.datainjector.dependency.InjectorDependencyManager;
import com.crescentflare.datainjectorexample.helper.MockDependency;

/**
 * The singleton application context (containing the other singletons in the app)
 */
public class ExampleApplication extends Application
{
    // ---
    // Global context member
    // ---

    public static Context context = null;


    // ---
    // Initialization
    // ---

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this;
        InjectorDependencyManager.instance.addDependency("customers", new MockDependency(R.raw.customer_list));
        InjectorDependencyManager.instance.addDependency("products", new MockDependency(R.raw.product_list));
    }
}
