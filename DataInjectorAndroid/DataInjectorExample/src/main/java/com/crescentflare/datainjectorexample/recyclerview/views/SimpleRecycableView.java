package com.crescentflare.datainjectorexample.recyclerview.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crescentflare.datainjectorexample.R;

/**
 * A simple view with only a label used in the recycler view
 */
public class SimpleRecycableView extends LinearLayout
{
    // ---
    // Members
    // ---

    private TextView textView;
    private View divider;


    // ---
    // Initialization
    // ---

    public SimpleRecycableView(Context context)
    {
        super(context);

        LayoutInflater.from(getContext()).inflate(R.layout.view_recycable_simple, this, true);
        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.recycable_view_background));
        setOrientation(VERTICAL);
        textView = (TextView)findViewById(R.id.view_recycable_simple_text);
        divider = findViewById(R.id.view_recycable_simple_divider);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params)
    {
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        super.setLayoutParams(params);
    }


    // ---
    // Set values
    // ---

    public void setText(String text)
    {
        if (text == null)
        {
            text = "";
        }
        textView.setText(text);
    }

    public void showDivider(boolean show)
    {
        divider.setVisibility(show ? VISIBLE : GONE);
    }
}
