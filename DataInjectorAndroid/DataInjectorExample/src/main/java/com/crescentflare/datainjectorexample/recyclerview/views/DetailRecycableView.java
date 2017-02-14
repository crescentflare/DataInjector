package com.crescentflare.datainjectorexample.recyclerview.views;

import android.content.Context;
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
public class DetailRecycableView extends LinearLayout
{
    // ---
    // Members
    // ---

    private TextView titleView;
    private TextView infoView;
    private TextView valueView;
    private View divider;


    // ---
    // Initialization
    // ---

    public DetailRecycableView(Context context)
    {
        super(context);

        LayoutInflater.from(getContext()).inflate(R.layout.view_recycable_detail, this, true);
        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.recycable_view_background));
        setOrientation(VERTICAL);
        titleView = (TextView)findViewById(R.id.view_recycable_detail_title);
        infoView = (TextView)findViewById(R.id.view_recycable_detail_info);
        valueView = (TextView)findViewById(R.id.view_recycable_detail_value);
        divider = findViewById(R.id.view_recycable_detail_divider);
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

    public void setTitle(String text)
    {
        if (text == null)
        {
            text = "";
        }
        titleView.setText(text);
    }

    public void setInfo(String text)
    {
        if (text == null)
        {
            text = "";
        }
        infoView.setText(text);
        infoView.setVisibility(text.length() > 0 ? VISIBLE : GONE);
    }

    public void setValue(String text)
    {
        if (text == null)
        {
            text = "";
        }
        valueView.setText(text);
        valueView.setVisibility(text.length() > 0 ? VISIBLE : GONE);
    }

    public void setValueColor(int colorCode)
    {
        valueView.setTextColor(colorCode);
    }

    public void showDivider(boolean show)
    {
        divider.setVisibility(show ? VISIBLE : GONE);
    }
}
