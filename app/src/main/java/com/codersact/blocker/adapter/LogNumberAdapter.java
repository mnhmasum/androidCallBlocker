package com.codersact.blocker.adapter;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codersact.blocker.R;
import com.codersact.blocker.model.NumberData;

import java.util.ArrayList;


public class LogNumberAdapter extends BaseAdapter {
    private ArrayList<NumberData> optionDataArrayList;
	private Activity context;
	private LayoutInflater mInflater;
	private TextToSpeech ttobj;

	public LogNumberAdapter(Activity context, ArrayList<NumberData> optionDatas) {
        this.optionDataArrayList = optionDatas;
		this.context = context;
	}

	@Override
	public int getCount() {
		return this.optionDataArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.optionDataArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
            holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.row_number_list, null);
			holder.txtViewName = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtViewName.setText(optionDataArrayList.get(position).getSenderNumber());

		return convertView;
	}


	//Static dssg
	static class ViewHolder {
		TextView txtViewName;

	}

}
