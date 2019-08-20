package com.ustc.carcalculate.utils;

import java.util.List;

import com.ustc.carcalculate.R;
import com.ustc.carcalculate.dto.PhoneInfo;
import com.ustc.carcalculate.dto.SortModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SortAdapter extends BaseAdapter {

	private List<PhoneInfo> data;
	private Context context;

	public SortAdapter(Context context, List<PhoneInfo> data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public PhoneInfo getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.number,
					null);
			holder = new ViewHolder();
			holder.sortLetter = (TextView) convertView.findViewById(R.id.word);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PhoneInfo sortModel = data.get(position);
		int selection = getSelectionByPosition(position);
		int index = getPositionBySelection(selection);
		if (position == index) {
			holder.sortLetter.setVisibility(View.VISIBLE);
			holder.sortLetter.setText(sortModel.getSortLetter());
		} else {
			holder.sortLetter.setVisibility(View.GONE);
		}
		holder.number.setText(sortModel.getNumber());
		holder.name.setText(sortModel.getName());
		return convertView;
	}

	public int getSelectionByPosition(int position) {
		return data.get(position).getSortLetter().charAt(0);
	}

	public int getPositionBySelection(int selection) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = data.get(i).getSortLetter();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == selection) {
				return i;
			}
		}
		return -1;
	}

	class ViewHolder {
		TextView sortLetter;
		TextView name;
		TextView number;
		ImageView imageView;
	}
}
