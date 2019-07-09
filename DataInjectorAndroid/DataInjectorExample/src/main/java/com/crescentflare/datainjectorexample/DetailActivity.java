package com.crescentflare.datainjectorexample;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.crescentflare.bitletsynchronizer.bitlet.BitletResultObserver;
import com.crescentflare.bitletsynchronizer.cache.BitletCacheEntry;
import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;
import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.injector.LinkDataInjector;
import com.crescentflare.datainjector.utility.InjectorUtil;
import com.crescentflare.datainjectorexample.helper.Bitlets;
import com.crescentflare.datainjectorexample.helper.MockBitlet;
import com.crescentflare.datainjectorexample.recyclerview.DetailAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The detail activity shows a list of products for a given customer in the example
 */
public class DetailActivity extends AppCompatActivity
{
    // ---
    // Constants
    // ---

    private static final String ARG_CUSTOMER_ID = "arg_customer_id";
    private static final List<MockBitlet> dependencies = Arrays.asList(Bitlets.customers, Bitlets.products);


    // ---
    // Members
    // ---

    private DetailAdapter recyclerAdapter = new DetailAdapter();


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
    }


    // ---
    // Lifecycle
    // ---

    @Override
    protected void onResume()
    {
        super.onResume();
        String[] checkCaches = new String[dependencies.size()];
        for (int i = 0; i < dependencies.size(); i++)
        {
            checkCaches[i] = dependencies.get(i).getCacheKey();
        }
        if (!BitletSynchronizer.instance.anyCacheInState(BitletCacheEntry.State.LoadingOrRefreshing, checkCaches) && !BitletSynchronizer.instance.anyCacheInState(BitletCacheEntry.State.Unavailable, checkCaches))
        {
            updateViews();
        }
        loadData(false);
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
        // Look up bitlet data
        Object customers = BitletSynchronizer.instance.getCachedBitlet(Bitlets.customers.getCacheKey());
        Object products = BitletSynchronizer.instance.getCachedBitlet(Bitlets.products.getCacheKey());
        List<Object> customerList = null;
        List<Object> productList = null;
        if (customers instanceof MockBitlet.ObjectArray)
        {
            customerList = ((MockBitlet.ObjectArray)customers).getItemList();
        }
        if (products instanceof MockBitlet.ObjectArray)
        {
            productList = ((MockBitlet.ObjectArray)products).getItemList();
        }

        // Find the products of the given customer id
        Map<String, Object> customer = LinkDataInjector.findDataItem(InjectorConv.asStringObjectMapList(customerList), getIntent().getStringExtra(ARG_CUSTOMER_ID), "id");
        List<Map<String, Object>> customerProducts = null;
        if (customer != null)
        {
            customerProducts = InjectorConv.asStringObjectMapList(customer.get("products"));
        }

        // If everything is there, link the product details to the customer product list
        LinkDataInjector.linkDataArray(customerProducts, InjectorConv.asStringObjectMapList(productList), "id");
        recyclerAdapter.setItems(customerProducts);
    }


    // ---
    // Data loading
    // ---

    private void loadData(boolean forced)
    {
        final String[] checkCaches = new String[dependencies.size()];
        for (int i = 0; i < dependencies.size(); i++)
        {
            checkCaches[i] = dependencies.get(i).getCacheKey();
        }
        for (MockBitlet dependency : dependencies)
        {
            BitletSynchronizer.instance.load(dependency, dependency.getCacheKey(), forced, new BitletResultObserver.SimpleCompletionListener<MockBitlet.ObjectArray>()
            {
                @Override
                public void onFinish(MockBitlet.ObjectArray bitlet, Throwable exception)
                {
                    if (!BitletSynchronizer.instance.anyCacheInState(BitletCacheEntry.State.LoadingOrRefreshing, checkCaches))
                    {
                        updateViews();
                    }
                }
            });
        }
    }
}
