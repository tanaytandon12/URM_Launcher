package com.example.urmlauncher;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LauncherActivity extends FragmentActivity {

	public static DevicePolicyManager mDevicePolicyManager;
	public static ComponentName mComponentName;
	public static int WIDTH, HEIGHT;
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor sharedPrefEditor;
	private LinearLayout mainLayout;
	private ImageView toggleImageView, viewModeImageView,
			appChangeIntensityImageView, volumeImageView,
			changeBackgroundImageView, batterySaverModeImageView,
			swipeHintImageView;
	private TextView swipeHintTextView;
	private FragmentManager fm;
	private FragmentTransaction ft;
	private AudioManager audioManager;
	private int[] volumeLevels = new int[4];
	private int[] audioStreams = { AudioManager.STREAM_ALARM,
			AudioManager.STREAM_DTMF, AudioManager.STREAM_NOTIFICATION,
			AudioManager.STREAM_RING };

	private int ICON_WIDTH = 256, ICON_HEIGHT = 256;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(this, MyAdminReceiver.class);

		// get the height and the width of the screen
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		HEIGHT = size.y;
		WIDTH = size.x;

		// get the shared preferences
		sharedPref = getSharedPreferences(Constants.URM, Context.MODE_PRIVATE);
		sharedPrefEditor = sharedPref.edit();

		mainLayout = (LinearLayout) findViewById(R.id.main);
		mainLayout.setAlpha(0.05f);

		// icon imageviews
		toggleImageView = (ImageView) findViewById(R.id.icon_toggle);
		viewModeImageView = (ImageView) findViewById(R.id.icon_view);
		appChangeIntensityImageView = (ImageView) findViewById(R.id.icon_intensity);
		volumeImageView = (ImageView) findViewById(R.id.icon_volume);
		changeBackgroundImageView = (ImageView) findViewById(R.id.icon_background);
		batterySaverModeImageView = (ImageView) findViewById(R.id.icon_battery_saver);

		// set the position of the menu
		setUpMenu();

		// the hint views
		swipeHintImageView = (ImageView) findViewById(R.id.hint_swipe_image);
		swipeHintTextView = (TextView) findViewById(R.id.hint_swipe_text);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

		Fragment appFragment = new AppFragment();

		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		ft.add(R.id.main_frame, appFragment, Constants.TAG_APP).commit();

		sharedPrefEditor.putBoolean(Constants.APP_LAUNCH, false).commit();

		if (sharedPref.getBoolean(Constants.HINT_VISIBILITY, false)) {
			swipeHintImageView.setVisibility(View.GONE);
			swipeHintTextView.setVisibility(View.GONE);
			mainLayout.setAlpha(1.0f);
			sharedPrefEditor.putBoolean(Constants.APP_LAUNCH, true).commit();
		}

		mainLayout.setOnTouchListener(new OnSwipeTouchListener(
				getApplicationContext()) {
			@Override
			public void onSwipeUp() {
				Log.d("", "Swipe up");
				
				// hide the hints
				swipeHintImageView.setVisibility(View.GONE);
				swipeHintTextView.setVisibility(View.GONE);

				// set the opacity of the main layout to 5%
				mainLayout.setAlpha(0.05f);

				// the apps cannot be laucnched
				sharedPrefEditor.putBoolean(Constants.APP_LAUNCH, false).commit();
				
				// show the menu icons
				showMenu();

				// set the value in the sharedPref that the hint is no longer
				// needed
				sharedPrefEditor.putBoolean(Constants.HINT_VISIBILITY, true)
						.commit();
			}

			@Override
			public void onSwipeDown() {
				Log.d("", "Swipe Down");

				// hide the menu icons
				hideMenu();

				// the click will trigger application launches
				sharedPrefEditor.putBoolean(Constants.APP_LAUNCH, true).commit();

				mainLayout.setAlpha(1.0f);
			}

			@Override
			public void onSwipeLeft() {
				Log.d("m", "swipe left");
			}

			@Override
			public void onSwipeRight() {
				Log.d("m", "swipe right");
			}
		});

		int iconWidth = WIDTH / 6;
		// int iconHeight = HEIGHT / 16;

		// set the width of the image views
		toggleImageView.getLayoutParams().width = iconWidth;
		viewModeImageView.getLayoutParams().width = iconWidth;
		appChangeIntensityImageView.getLayoutParams().width = iconWidth;
		volumeImageView.getLayoutParams().width = iconWidth;
		changeBackgroundImageView.getLayoutParams().width = iconWidth;
		batterySaverModeImageView.getLayoutParams().width = iconWidth;

		// toggle imageview to hide the menu
		toggleImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
			}
		});

		String backgroundUriStrig = sharedPref.getString(
				Constants.BACKGROUND_URI, null);
		if (backgroundUriStrig != null) {
			setBackground(Uri.parse(backgroundUriStrig));
		}

		changeBackgroundImageView
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						startActivityForResult(
								new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
								Constants.OPEN_GALLERY);
					}
				});

		viewModeImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				ImageView imgView = (ImageView) view;
				ft = fm.beginTransaction();
				Fragment currentFragment = fm
						.findFragmentByTag(Constants.TAG_APP);
				if (currentFragment != null) {
					ft.replace(R.id.main_frame, new WidgetFragment(),
							Constants.TAG_WIDGET).commit();
					imgView.setImageResource(R.drawable.widget);
				} else {
					ft.replace(R.id.main_frame, new AppFragment(),
							Constants.TAG_APP).commit();
					imgView.setImageResource(R.drawable.app);
				}
			}
		});

		volumeImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				ImageView imgView = (ImageView) view;
				audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
					imgView.setImageResource(R.drawable.silent);
					Log.d("", "setting the phone on silent mode");
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					for (int i = 0; i < 4; i = i + 1) {
						volumeLevels[i] = audioManager
								.getStreamVolume(audioStreams[i]);
						audioManager.setStreamVolume(audioStreams[i], 0,
								AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
					}
				} else {
					imgView.setImageResource(R.drawable.loud);
					Log.d("", "setting the phone on loud mode");
					audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					for (int i = 0; i < 4; i = i + 1) {
						audioManager.setStreamVolume(audioStreams[i],
								volumeLevels[i],
								AudioManager.FLAG_ALLOW_RINGER_MODES);
					}
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.OPEN_GALLERY
				&& resultCode == Activity.RESULT_OK) {
			Uri backgroundUri = data.getData();
			setBackground(backgroundUri);
			sharedPrefEditor.putString(Constants.BACKGROUND_URI,
					backgroundUri.toString()).commit();
		}
	}

	private void setBackground(Uri backgroundUri) {
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main);
		Bitmap sourceBitmap = null, scaledBitmap = null;

		try {
			sourceBitmap = MediaStore.Images.Media.getBitmap(
					getContentResolver(), backgroundUri);
			scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, 100, 100,
					true);
			mainLayout.setBackground(new BitmapDrawable(getResources(),
					scaledBitmap));
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private void setUpMenu() {
		int radius = WIDTH / 4;
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
				ICON_WIDTH, ICON_HEIGHT);
		params1.setMargins(radius, HEIGHT - 100, 0, 0);
		viewModeImageView.setLayoutParams(params1);

		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				ICON_WIDTH, ICON_HEIGHT);
		params2.setMargins(WIDTH * 3 / 8, HEIGHT - getY(radius, WIDTH / 8)
				- 100, 0, 0);
		changeBackgroundImageView.setLayoutParams(params2);

		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
				ICON_WIDTH, ICON_HEIGHT);
		params3.setMargins(WIDTH / 2, HEIGHT * 7 / 8, 0, 0);
		volumeImageView.setLayoutParams(params3);
		
		RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(ICON_WIDTH, ICON_HEIGHT);
		params4.setMargins(WIDTH * 5 / 8, HEIGHT - getY(radius, WIDTH / 8) - 100, 0, 0);
		appChangeIntensityImageView.setLayoutParams(params4);
		
		RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(
				ICON_WIDTH, ICON_HEIGHT);
		params5.setMargins(radius * 3, HEIGHT - 100, 0, 0);
		batterySaverModeImageView.setLayoutParams(params5);
	}

	private int getY(int r, int x) {
		return (int) Math.round(Math.sqrt((r * r) - (x * x)));
	}

	private void hideMenu() {
		viewModeImageView.setVisibility(View.GONE);
		volumeImageView.setVisibility(View.GONE);
		changeBackgroundImageView.setVisibility(View.GONE);
		appChangeIntensityImageView.setVisibility(View.GONE);
		batterySaverModeImageView.setVisibility(View.GONE);
	}

	private void showMenu() {
		viewModeImageView.setVisibility(View.VISIBLE);
		volumeImageView.setVisibility(View.VISIBLE);
		changeBackgroundImageView.setVisibility(View.VISIBLE);
		appChangeIntensityImageView.setVisibility(View.VISIBLE);
		batterySaverModeImageView.setVisibility(View.VISIBLE);
	}
}
