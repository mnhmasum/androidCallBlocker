package com.codersact.blocker.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codersact.blocker.R;
import com.codersact.blocker.blacklist.BlacklistView;
import com.codersact.blocker.db.DataBaseUtil;
import com.codersact.blocker.model.MobileData;

import java.util.ArrayList;


public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder>{
    private ArrayList<MobileData> mDataset = new ArrayList<>();
    private Context context;
    private BlacklistView blacklistView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView mTextDesc;
        public ImageButton btnDelete;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.person_name);
            btnDelete = (ImageButton) v.findViewById(R.id.btnDelete);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BlackListAdapter(ArrayList<MobileData> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BlackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_black_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.mTextView.setText("" + mDataset.get(position).getMobileNumber());
        holder.btnDelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Do you really want to delete?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteItem(position);
                                doButtonOneClickActions();
                            }})
                        .setNegativeButton("NO", null).show();

            }
        });

    }

    public void deleteItem(int position) {
        DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
        dataBaseUtil.open();
        dataBaseUtil.deleteNumber(mDataset.get(position).getRowId());
        dataBaseUtil.close();
        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void doButtonOneClickActions() {
        if(mOnDataChangeListener != null){
            mOnDataChangeListener.onDataChanged(mDataset.size());
        }
    }

    OnDataChangeListener mOnDataChangeListener;
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }

    public interface OnDataChangeListener{
        void onDataChanged(int size);
    }

}