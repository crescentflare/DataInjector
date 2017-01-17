package com.crescentflare.datainjectorexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.crescentflare.datainjector.dependency.InjectorDependencyManager;

import java.util.Collections;
import java.util.List;

/**
 * The main activity shows a small layout example, explanation and buttons to show other layout examples
 */
public class MainActivity extends AppCompatActivity implements InjectorDependencyManager.DependencyUpdateListener
{
    // ---
    // Members
    // ---

    private boolean dependenciesOpen = false;


    // ---
    // Initialization
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dependenciesOpen = InjectorDependencyManager.instance.getUnresolvedDependencies(Collections.singletonList("customers")).size() > 0;
    }


    // ---
    // Lifecycle
    // ---

    @Override
    protected void onResume()
    {
        super.onResume();
        InjectorDependencyManager.instance.addUpdateListener(this);
        if (dependenciesOpen)
        {
            List<String> dependenciesLeft = InjectorDependencyManager.instance.getUnresolvedDependencies(Collections.singletonList("customers"));
            if (dependenciesLeft.size() > 0)
            {
                for (String dependency : dependenciesLeft)
                {
                    InjectorDependencyManager.instance.resolveDependency(dependency);
                }
            }
            else
            {
                dependenciesOpen = false;
                updateViews();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        InjectorDependencyManager.instance.removeUpdateListener(this);
    }


    // ---
    // View creation
    // ---

    public void updateViews()
    {
        // Nothing yet...
    }


    // ---
    // Dependency handling
    // ---

    @Override
    public void onDependencyResolved(String dependency)
    {
        if (dependenciesOpen)
        {
            List<String> dependenciesLeft = InjectorDependencyManager.instance.getUnresolvedDependencies(Collections.singletonList("customers"));
            if (dependenciesLeft.size() == 0)
            {
                dependenciesOpen = false;
                updateViews();
            }
        }
    }

    @Override
    public void onDependencyFailed(String dependency, String reason)
    {
    }
}
