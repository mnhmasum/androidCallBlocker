package com.codersact.blocker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.codersact.blocker.R;
import com.codersact.blocker.model.NavigationMenu;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {
    private ArrayList<NavigationMenu> mDataset = new ArrayList<>();
    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public TextView txtViewName;
        int HolderId;

        public ViewHolder(View v, int viewType) {
            super(v);

            if (viewType == 1) {
                txtViewName = (TextView) v.findViewById(R.id.name);
                HolderId = 1;

            } else {
                HolderId = 0;
            }

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NavigationAdapter(ArrayList<NavigationMenu> myDataset, Context context) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NavigationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view

        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_navigation_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v, viewType);
            return vh;

        } else if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_navigation_list, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v, viewType);
            return vh;

        }


        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if(holder.HolderId == 1) {                              // as the list view is going to be called after the header view so we decrement the
            holder.txtViewName.setText(mDataset.get(position).getMenuName());

        } else {

        }

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}