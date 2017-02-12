package com.crescentflare.datainjectorexample;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.crescentflare.datainjector.dependency.InjectorDependencyManager;
import com.crescentflare.datainjector.utility.InjectorUtil;
import com.crescentflare.datainjectorexample.recyclerview.DetailAdapter;
import com.crescentflare.datainjectorexample.recyclerview.MainAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The detail activity shows a list of products for a given customer in the example
 */
public class DetailActivity extends AppCompatActivity implements InjectorDependencyManager.DependencyUpdateListener
{
    // ---
    // Members
    // ---

    private DetailAdapter recyclerAdapter = new DetailAdapter();
    private boolean dependenciesOpen = false;


    // ---
    // Initialization
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Add back button to action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set up recycler view
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.activity_detail_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        // Determine if dependencies are open
        dependenciesOpen = InjectorDependencyManager.instance.getUnresolvedDependencies(Arrays.asList("customers", "products")).size() > 0;
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
            List<String> dependenciesLeft = InjectorDependencyManager.instance.getUnresolvedDependencies(Arrays.asList("customers", "products"));
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
    // Menu handling
    // ---

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ---
    // View creation
    // ---

    public void updateViews()
    {
        List<Object> productItems = InjectorUtil.asObjectList(InjectorDependencyManager.instance.getDependency("products").obtainInjectableData());
        recyclerAdapter.setItems(productItems);
    }


    // ---
    // Dependency handling
    // ---

    @Override
    public void onDependencyResolved(String dependency)
    {
        if (dependenciesOpen)
        {
            List<String> dependenciesLeft = InjectorDependencyManager.instance.getUnresolvedDependencies(Arrays.asList("customers", "products"));
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
