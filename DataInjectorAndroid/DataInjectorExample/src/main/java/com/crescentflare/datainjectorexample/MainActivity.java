package com.crescentflare.datainjectorexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.crescentflare.bitletsynchronizer.bitlet.BitletResultObserver;
import com.crescentflare.bitletsynchronizer.cache.BitletCacheEntry;
import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;
import com.crescentflare.datainjector.utility.InjectorUtil;
import com.crescentflare.datainjectorexample.helper.Bitlets;
import com.crescentflare.datainjectorexample.helper.MockBitlet;
import com.crescentflare.datainjectorexample.recyclerview.MainAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The main activity shows a list of customers in the example
 */
public class MainActivity extends AppCompatActivity implements MainAdapter.ItemClickListener
{
    // ---
    // Constants
    // ---

    private static final List<MockBitlet> dependencies = Collections.singletonList(Bitlets.customers);


    // ---
    // Members
    // ---

    private MainAdapter recyclerAdapter = new MainAdapter();


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
    // View creation
    // ---

    private void updateViews()
    {
        Object customers = BitletSynchronizer.instance.getCachedBitlet(Bitlets.customers.getCacheKey());
        if (customers instanceof MockBitlet.ObjectArray)
        {
            List<Object> itemList = ((MockBitlet.ObjectArray)customers).getItemList();
            List<Map<String, Object>> customerItems = InjectorUtil.asStringObjectMapList(itemList);
            recyclerAdapter.setItems(customerItems);
        }
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


    // ---
    // Interaction
    // ---

    @Override
    public void onClickedItem(int index)
    {
        // Get customer item list
        Object customers = BitletSynchronizer.instance.getCachedBitlet(Bitlets.customers.getCacheKey());
        List<Map<String, Object>> customerItems = null;
        if (customers instanceof MockBitlet.ObjectArray)
        {
            List<Object> itemList = ((MockBitlet.ObjectArray) customers).getItemList();
            customerItems = InjectorUtil.asStringObjectMapList(itemList);
        }

        // Fetch customer ID from index
        String customerId = null;
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
