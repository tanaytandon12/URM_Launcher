package com.example.urmlauncher;

import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingsFragment extends Fragment {

	private Button lockButton, adminAccessButton;
	private static String ENABLE_ADMIN_ACCESS = "Touch to enable admin access";
	private static String DISABLE_ADMIN_ACCESS = "Touch to disable admin access";
	private static int ADMIN_INTENT = 12;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, parent, false);
		Button changeBackgroundButton = (Button) view
				.findViewById(R.id.set_background_button);
		lockButton = (Button) view.findViewById(R.id.lock_button);
		adminAccessButton = (Button) view
				.findViewById(R.id.admin_access_button);

		if (LauncherActivity.mDevicePolicyManager
				.isAdminActive(LauncherActivity.mComponentName)) {
			adminAccessButton.setText(DISABLE_ADMIN_ACCESS);
		} else {
			adminAccessButton.setText(ENABLE_ADMIN_ACCESS);
			lockButton.setEnabled(false);
		}

		adminAccessButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Button button = (Button) view
						.findViewById(R.id.admin_access_button);
				String text = button.getText().toString();
				switch (text) {
				
				case "Touch to enable admin access":
					// make the app an admin
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							LauncherActivity.mComponentName);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"Admin Device");
					getActivity().startActivityForResult(intent, ADMIN_INTENT);
					// change the text that is displayed
					button.setText(DISABLE_ADMIN_ACCESS);
					lockButton.setEnabled(true);
					break;
					
				case "Touch to disable admin access":
					LauncherActivity.mDevicePolicyManager
							.removeActiveAdmin(LauncherActivity.mComponentName);
					button.setText(ENABLE_ADMIN_ACCESS);
					lockButton.setEnabled(false);
					break;
				}
			}
		});

		lockButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LauncherActivity.mDevicePolicyManager.lockNow();
			}
		});
		changeBackgroundButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				getActivity()
						.startActivityForResult(
								new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
								Constants.OPEN_GALLERY);
			}
		});
		return view;
	}
}
