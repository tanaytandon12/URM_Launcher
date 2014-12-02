package com.example.urmlauncher;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class MenuFragment extends DialogFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu, container, false);
		Dialog dialog = getDialog();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
		dialog.setCancelable(true);
		return view;
	}

}
