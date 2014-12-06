package com.example.urmlauncher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppFragment extends Fragment {

	private GridView smallGridView;
	private GridAdapter smallAdapter;
	private DatabaseHelper mDatabaseHelper;
	private Context context;
	private SharedPreferences sharedPref;
	private boolean onPauseCalled;
	private HashMap<String, ResolveInfo> map;
	private RelativeLayout[] appLayouts = new RelativeLayout[9];
	private ImageView[] appIconImageViews = new ImageView[9];
	private TextView[] appNameTextViews = new TextView[9];
	private static int[] LAYOUT_ID = { R.id.medium_app_view_1,
			R.id.medium_app_view_2, R.id.medium_app_view_3,
			R.id.medium_app_view_4, R.id.medium_app_view_5,
			R.id.medium_app_view_6, R.id.large_app_view_1,
			R.id.large_app_view_2, R.id.large_app_view_3 };
	private static int[] NAME_ID = { R.id.medium_app_name_1,
			R.id.medium_app_name_2, R.id.medium_app_name_3,
			R.id.medium_app_name_4, R.id.medium_app_name_5,
			R.id.medium_app_name_6, R.id.large_app_name_1,
			R.id.large_app_name_2, R.id.large_app_name_3 };
	private static int[] ICON_ID = { R.id.medium_app_icon_1,
			R.id.medium_app_icon_2, R.id.medium_app_icon_3,
			R.id.medium_app_icon_4, R.id.medium_app_icon_5,
			R.id.medium_app_icon_6, R.id.large_app_icon_1,
			R.id.large_app_icon_2, R.id.large_app_icon_3 };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		Log.d("", "fuck this shit");
		View view = inflater.inflate(R.layout.fragment_app, parent, false);
		context = getActivity().getApplicationContext();

		// get the shared Preference
		sharedPref = getActivity().getSharedPreferences(Constants.URM,
				Context.MODE_PRIVATE);

		int width = LauncherActivity.WIDTH / 3;
		int mediumHeight = LauncherActivity.HEIGHT * 3 / 32;
		int largeHeight = LauncherActivity.HEIGHT / 8;

		for (int i = 0; i < 9; i = i + 1) {
			appLayouts[i] = (RelativeLayout) view.findViewById(LAYOUT_ID[i]);
			appIconImageViews[i] = (ImageView) view.findViewById(ICON_ID[i]);
			appNameTextViews[i] = (TextView) view.findViewById(NAME_ID[i]);
			appLayouts[i].getLayoutParams().width = width;
			appIconImageViews[i].getLayoutParams().width = width;
			appNameTextViews[i].getLayoutParams().width = width;
			if (i < 6) {
				appIconImageViews[i].getLayoutParams().height = mediumHeight;
			} else {
				appIconImageViews[i].getLayoutParams().height = largeHeight;
			}
		}

		smallGridView = (GridView) view.findViewById(R.id.small_grid);
		mDatabaseHelper = new DatabaseHelper(getActivity()
				.getApplicationContext());

		initialize();

		OnItemClickListener gridClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView appNameTextView = (TextView) view
						.findViewById(R.id.app_name);
				String appName = appNameTextView.getText().toString();
				launchApplication(appName);
			}
		};

		// the click listener for all the apps
		OnClickListener appViewClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (sharedPref.getBoolean(Constants.APP_LAUNCH, false)) {
					view.setBackgroundColor(Color.parseColor("#00CCCC"));
					String appName = view.getTag().toString();
					launchApplication(appName);
				}
			}
		};

		smallGridView.setOnItemClickListener(gridClickListener);

		for (int i = 0; i < 9; i = i + 1) {
			appLayouts[i].setOnClickListener(appViewClickListener);
		}

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		onPauseCalled = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (onPauseCalled) {
			initialize();
		}
	}

	private void initialize() {
		PackageManager mPackageManager = getActivity().getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		// get the list of all the apps that are installed in the system
		List<ResolveInfo> installedAppList = mPackageManager
				.queryIntentActivities(intent, 0);

		// create a list of app names that already exist in the database
		ArrayList<String> appList = mDatabaseHelper.getAppNameList();

		// get the number of apps stored in the database
		int numOfStoredApps = appList.size();
		String lastAppName = "";

		try {
			lastAppName = appList.get(numOfStoredApps - 1);
		} catch (ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		}
		// create a HashMap to store all the app names and the corresponding
		// ResolveInfo object
		map = new HashMap<>();

		int length = installedAppList.size();
		for (int i = 0; i < length; i++) {
			String appName = installedAppList.get(i).loadLabel(mPackageManager)
					.toString();
			map.put(appName, installedAppList.get(i));
			if (!appList.contains(appName)) {
				appList.add(appName);
			}
		}

		for (int i = 0; i < numOfStoredApps; i = i + 1) {
			String name = appList.get(i);
			if (!map.containsKey(name)) {
				appList.remove(i);
				i = i - 1;
			}
			if (name.equals(lastAppName))
				break;
		}

		for (int i = 0; i < 3; i = i + 1) {
			String name = appList.get(i);
			appLayouts[i + 6].setTag(name);
			appLayouts[i + 6].setBackgroundColor(Color.TRANSPARENT);
			appNameTextViews[i + 6].setText(name);
			ResolveInfo resolveInfo = map.get(name);
			Drawable drawable = resolveInfo.loadIcon(mPackageManager);
			appIconImageViews[i + 6].setImageDrawable(drawable);
		}

		for (int i = 0; i < 6; i = i + 1) {
			String name = appList.get(i + 3);
			appLayouts[i].setTag(name);
			appLayouts[i].setBackgroundColor(Color.TRANSPARENT);
			appNameTextViews[i].setText(name);
			ResolveInfo resolveInfo = map.get(name);
			Drawable drawable = resolveInfo.loadIcon(mPackageManager);
			appIconImageViews[i].setImageDrawable(drawable);
		}

		List<String> smallList = appList.subList(9, appList.size());
		Collections.sort(smallList);

		GridAdapter.setMap(map);
		smallAdapter = new GridAdapter(context, smallList,
				LauncherActivity.HEIGHT / 12, LauncherActivity.WIDTH / 3);

		smallGridView.setAdapter(smallAdapter);

	}

	private void launchApplication(String appName) {
			ResolveInfo clickedResolveInfo = map.get(appName);

			ActivityInfo clickedActivityInfo = clickedResolveInfo.activityInfo;

			// update the database based on the click
			mDatabaseHelper.updateCount(appName);

			// start the application that was selected
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClassName(
					clickedActivityInfo.applicationInfo.packageName,
					clickedActivityInfo.name);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(intent);
		}

}
