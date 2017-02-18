package com.crescentflare.datainjectorexample.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.crescentflare.datainjector.conversion.InjectorConv;
import com.crescentflare.datainjectorexample.recyclerview.views.SimpleRecycableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides display of the customer list on the main overview screen
 */
public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    // ---
    // Members
    // ---

    private List<Map<String, Object>> items = new ArrayList<>();
    private ItemClickListener clickListener;


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
        return new ViewHolder(new SimpleRecycableView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {
        Map<String, Object> item = items.get(position);
        SimpleRecycableView itemView = (SimpleRecycableView)holder.itemView;
        itemView.setText(InjectorConv.toString(item.get("fullName")));
        itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (clickListener != null)
                {
                    clickListener.onClickedItem(holder.getAdapterPosition());
                }
            }
        });
    }

    private static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
        }
    }


    // ---
    // Item click listener
    // ---

    public void setItemClickListener(ItemClickListener listener)
    {
        clickListener = listener;
    }

    public interface ItemClickListener
    {
        void onClickedItem(int index);
    }
}
