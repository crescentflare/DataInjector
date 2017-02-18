package com.crescentflare.datainjectorexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.crescentflare.datainjector.dependency.InjectorDependencyManager;
import com.crescentflare.datainjector.utility.InjectorUtil;
import com.crescentflare.datainjectorexample.recyclerview.MainAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The main activity shows a list of customers in the example
 */
public class MainActivity extends AppCompatActivity implements InjectorDependencyManager.DependencyUpdateListener, MainAdapter.ItemClickListener
{
    // ---
    // Constants
    // ---

    private static final List<String> dependencies = Collections.singletonList("customers");


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
        dependenciesOpen = InjectorDependencyManager.instance.getUnresolvedDependencies(dependencies).size() > 0;
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
            List<String> dependenciesLeft = InjectorDependencyManager.instance.getUnresolvedDependencies(dependencies);
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
        List<Map<String, Object>> customerItems = InjectorUtil.asStringObjectMapList(InjectorDependencyManager.instance.getDependency("customers").obtainInjectableData());
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
            List<String> dependenciesLeft = InjectorDependencyManager.instance.getUnresolvedDependencies(dependencies);
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
        // Fetch customer ID from index
        String customerId = null;
        List<Object> customerItems = InjectorUtil.asObjectList(InjectorDependencyManager.instance.getDependency("customers").obtainInjectableData());
        if (customerItems != null && index < customerItems.size())
        {
            Map<String, Object> customer = InjectorUtil.asStringObjectMap(customerItems.get(index));
            if (customer != null)
            {
                if (customer.get("id") instanceof String)
                {
                    customerId = (String) customer.get("id");
                }
            }
        }

        // Start activity
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtras(DetailActivity.extraBundle(customerId));
        startActivity(intent);
    }
}
