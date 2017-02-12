package com.crescentflare.datainjectorexample.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjector.utility.InjectorUtil;
import com.crescentflare.datainjectorexample.recyclerview.views.DetailRecycableView;
import com.crescentflare.datainjectorexample.recyclerview.views.SimpleRecycableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides display of the product list on the detail screen
 */
public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    // ---
    // Members
    // ---

    private List<Map<String, Object>> items = new ArrayList<>();


    // ---
    // Set items
    // ---

    public void setItems(List<Object> items)
    {
        this.items.clear();
        if (items == null)
        {
            return;
        }
        for (Object item : items)
        {
            Map<String, Object> mapItem = InjectorUtil.asStringObjectMap(item);
            if (mapItem != null)
            {
                this.items.add(mapItem);
            }
        }
        notifyDataSetChanged();
    }


    // ---
    // Internal item access
    // ---

    @Override
    public int getItemCount()
    {
        return items.size();
    }


    // ---
    // View holder
    // ---

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(new DetailRecycableView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        Map<String, Object> item = items.get(position);
        DetailRecycableView itemView = (DetailRecycableView)holder.itemView;
        itemView.setTitle(InjectorConv.toString(item.get("name")));
        itemView.setInfo(InjectorConv.toString(item.get("description")));
        itemView.setValue(InjectorConv.toString(item.get("price")));
    }

    private static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
    }
}
