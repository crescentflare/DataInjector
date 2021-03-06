package com.crescentflare.datainjectorexample.recyclerview;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjectorexample.recyclerview.views.DetailRecycableView;

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

    public void setItems(List<Map<String, Object>> items)
    {
        this.items.clear();
        if (items == null)
        {
            return;
        }
        this.items.addAll(items);
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
        Boolean paid = InjectorConv.asBoolean(item.get("paid"));
        itemView.setTitle(InjectorConv.asString(item.get("name")));
        itemView.setInfo(InjectorConv.asString(item.get("description")));
        itemView.setValue(InjectorConv.asString(item.get("price")));
        itemView.setValueColor(paid != null && paid ? Color.BLACK : Color.RED);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
    }
}
