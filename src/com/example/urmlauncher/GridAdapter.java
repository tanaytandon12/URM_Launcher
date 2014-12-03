package com.example.urmlauncher;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	private static HashMap<String, ResolveInfo> map;
	private Context ctxt;
	private List<String> list;
	private PackageManager pm;
	private int dimen, w;

	public GridAdapter(Context context, List<String> appNameList, int height, int width) {
		this.ctxt = context;
		this.list = appNameList;
		this.dimen = height;
		this.w = width;
		pm = ctxt.getPackageManager();
	}

	public static void setMap(HashMap<String, ResolveInfo> appMap) {
		map = appMap;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) ctxt
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.app_view, parent, false);
		}
		String appName = list.get(position);
		ResolveInfo resolveInfo = map.get(appName);
		TextView appNameTextView = (TextView) convertView
				.findViewById(R.id.app_name);
		ImageView appIconImageView = (ImageView) convertView
				.findViewById(R.id.app_icon);
		Drawable drawable = resolveInfo.loadIcon(pm);
		appNameTextView.setText(appName);
		appIconImageView.setImageDrawable(drawable);
		appIconImageView.getLayoutParams().height = dimen;
		appIconImageView.getLayoutParams().width = w;
		appNameTextView.getLayoutParams().width = w;
		appNameTextView.getLayoutParams().height = dimen;
		return convertView;
	}

}
