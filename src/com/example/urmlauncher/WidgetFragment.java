package com.example.urmlauncher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WidgetFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_widget, root, false);
		return view;
	}
}
