package com.crescentflare.datainjectorexample;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.crescentflare.datainjector.dependency.InjectorDependency;
import com.crescentflare.datainjector.dependency.InjectorDependencyManager;
import com.crescentflare.datainjector.dependency.InjectorDependencyState;
import com.crescentflare.datainjector.injector.LinkDataInjector;
import com.crescentflare.datainjector.utility.InjectorUtil;
import com.crescentflare.datainjectorexample.recyclerview.DetailAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The detail activity shows a list of products for a given customer in the example
 */
public class DetailActivity extends AppCompatActivity implements InjectorDependencyManager.DependencyUpdateListener
{
    // ---
    // Constants
    // ---

    private static final String ARG_CUSTOMER_ID = "arg_customer_id";
    private static final List<InjectorDependency> dependencies = InjectorDependencyManager.instance.getDependencies(Arrays.asList("customers", "products"));


    // ---
    // Members
    // ---

    private DetailAdapter recyclerAdapter = new DetailAdapter();
    private boolean dependenciesOpen = false;


    // ---
    // Initialization
    // ---

    public static Bundle extraBundle(String customerId)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CUSTOMER_ID, customerId);
        return bundle;
    }

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
        dependenciesOpen = InjectorDependencyManager.instance.filterDependenciesExcludingState(dependencies, InjectorDependencyState.Resolved).size() > 0;
        if (!dependenciesOpen)
        {
            updateViews();
        }
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
            List<InjectorDependency> dependenciesLeft = InjectorDependencyManager.instance.filterDependenciesExcludingState(dependencies, InjectorDependencyState.Resolved);
            if (dependenciesLeft.size() > 0)
            {
                for (InjectorDependency dependency : dependenciesLeft)
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
        // Look up dependency data
        List<Map<String, Object>> customers = InjectorUtil.asStringObjectMapList(InjectorDependencyManager.instance.getDependency("customers").obtainInjectableData());
        List<Map<String, Object>> products = InjectorUtil.asStringObjectMapList(InjectorDependencyManager.instance.getDependency("products").obtainInjectableData());

        // Find the products of the given customer id
        Map<String, Object> customer = LinkDataInjector.findDataItem(customers, getIntent().getStringExtra(ARG_CUSTOMER_ID), "id");
        List<Map<String, Object>> customerProducts = null;
        if (customer != null)
        {
            customerProducts = InjectorUtil.asStringObjectMapList(customer.get("products"));
        }

        // If everything is there, link the product details to the customer product list
        LinkDataInjector.linkDataArray(customerProducts, products, "id");
        recyclerAdapter.setItems(customerProducts);
    }


    // ---
    // Dependency handling
    // ---

    @Override
    public void onDependencyResolved(InjectorDependency dependency)
    {
        if (dependenciesOpen)
        {
            List<InjectorDependency> dependenciesLeft = InjectorDependencyManager.instance.filterDependenciesExcludingState(dependencies, InjectorDependencyState.Resolved);
            if (dependenciesLeft.size() == 0)
            {
                dependenciesOpen = false;
                updateViews();
            }
        }
    }

    @Override
    public void onDependencyFailed(InjectorDependency dependency, String reason)
    {
    }
}
