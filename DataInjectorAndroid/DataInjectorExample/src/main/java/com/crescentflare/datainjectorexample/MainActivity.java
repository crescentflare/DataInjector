package com.crescentflare.datainjectorexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.dependency.InjectorDependencyManager;
import com.crescentflare.datainjector.utility.InjectorUtil;
import com.crescentflare.datainjectorexample.recyclerview.MainAdapter;

import java.util.Collections;
import java.util.List;

/**
 * The main activity shows a list of customers in the example
 */
public class MainActivity extends AppCompatActivity implements InjectorDependencyManager.DependencyUpdateListener, MainAdapter.ItemClickListener
{
    // ---
    // Members
    // ---

    private MainAdapter recyclerAdapter = new MainAdapter();
    private boolean dependenciesOpen = false;


    // ---
    // Initialization
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up recycler view
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.activity_main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setItemClickListener(this);

        // Determine if dependencies are open
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
        List<Object> customerItems = InjectorUtil.asObjectList(InjectorDependencyManager.instance.getDependency("customers").obtainInjectableData());
        recyclerAdapter.setItems(customerItems);
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


    // ---
    // Interaction
    // ---

    @Override
    public void onClickedItem(int index)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}
