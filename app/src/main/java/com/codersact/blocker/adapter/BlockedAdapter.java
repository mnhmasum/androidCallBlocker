package com.codersact.blocker.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codersact.blocker.R;
import com.codersact.blocker.db.DataBaseUtil;
import com.codersact.blocker.model.MobileData;

import java.util.ArrayList;

public class BlockedAdapter extends RecyclerView.Adapter<BlockedAdapter.ViewHolder>{
    private static String TAG = "BlockedAdapter";
    private ArrayList<MobileData> mDataset = new ArrayList<>();
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public TextView mTextDesc;
        public CardView cardView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.person_name);
            mTextDesc = (TextView) v.findViewById(R.id.person_age);
            cardView = (CardView) v.findViewById(R.id.cv);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BlockedAdapter(ArrayList<MobileData> myDataset, Context context) {
        mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BlockedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sms_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.mTextView.setText(mDataset.get(position).getMobileNumber());
        holder.mTextDesc.setText(mDataset.get(position).getCreatedTime());

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Do you really want to delete?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteLog(position);
                            }})
                        .setNegativeButton("NO", null).show();
            }

        });

    }

    private void deleteLog(int position) {
        DataBaseUtil dataBaseUtil = new DataBaseUtil(context);
        dataBaseUtil.open();
        Log.e(TAG, "DELETED: " + dataBaseUtil.deleteSingleLog(mDataset.get(position).getMobileNumber()));
        dataBaseUtil.close();

        mDataset.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
        doButtonOneClickActions();
    }

    public void delete_thread(String thread) {
        Cursor c = context.getContentResolver().query(
                Uri.parse("content://sms/"),new String[] {
                        "_id", "thread_id", "address", "person", "date","body" }, null, null, null);

        try {
            while (c.moveToNext())
            {
                int id = c.getInt(0);
                Log.i("ID", "***" + id);
                String address = c.getString(2);
                if (address.equals(thread))
                {
                    Log.i("OK", "***" + address);
                    Log.i("OK ID", "***" + id);
                    context.getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);
                }

            }
        } catch (Exception e) {
            Log.e("DELETE EXCEPTION", "" + e.getMessage() );
        }
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