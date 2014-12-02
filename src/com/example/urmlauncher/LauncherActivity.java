package com.example.urmlauncher;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.crypto.spec.IvParameterSpec;

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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class LauncherActivity extends FragmentActivity {

	public static DevicePolicyManager mDevicePolicyManager;
	public static ComponentName mComponentName;
	public static int WIDTH, HEIGHT;
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor sharedPrefEditor;
	private LinearLayout iconLayout, mainLayout;
	private ImageView toggleImageView, viewModeImageView,
			appChangeIntensityImageView, volumeImageView,
			changeBackgroundImageView, batterySaverModeImageView;
	private long animationDuration = 1000;
	private FragmentManager fm;
	private FragmentTransaction ft;
	private AudioManager audioManager;
	private int[] volumeLevels = new int[4];
	private int[] audioStreams = { AudioManager.STREAM_ALARM,
			AudioManager.STREAM_DTMF, AudioManager.STREAM_NOTIFICATION,
			AudioManager.STREAM_RING };

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

		mainLayout = (LinearLayout) findViewById(R.id.main);

		FrameLayout frame = (FrameLayout) findViewById(R.id.main_frame);

		frame.setOnTouchListener(new OnSwipeTouchListener(
				getApplicationContext()) {
			@Override
			public void onSwipeUp() {
				if (iconLayout.getVisibility() == View.GONE) {
					Animation anim = new TranslateAnimation(0, 0, HEIGHT * 2,
							HEIGHT);
					anim.setDuration(animationDuration);
					iconLayout.setVisibility(View.VISIBLE);
					iconLayout.setAnimation(anim);
				}
			}

			@Override
			public void onSwipeLeft() {
				Log.d("frame", "swipe left");
			}

			@Override
			public void onSwipeRight() {
				Log.d("frame", "swipe right");
			}
		});

		mainLayout.setOnTouchListener(new OnSwipeTouchListener(
				getApplicationContext()) {
			@Override
			public void onSwipeUp() {
				if (iconLayout.getVisibility() == View.GONE) {
					Animation anim = new TranslateAnimation(0, 0, HEIGHT * 2,
							HEIGHT);
					anim.setDuration(animationDuration);
					iconLayout.setVisibility(View.VISIBLE);
					iconLayout.setAnimation(anim);
				}
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

		// get the shared preferences
		sharedPref = getSharedPreferences(Constants.URM, Context.MODE_PRIVATE);
		sharedPrefEditor = sharedPref.edit();

		// icon imageviews
		toggleImageView = (ImageView) findViewById(R.id.icon_toggle);
		viewModeImageView = (ImageView) findViewById(R.id.icon_view);
		appChangeIntensityImageView = (ImageView) findViewById(R.id.icon_intensity);
		volumeImageView = (ImageView) findViewById(R.id.icon_volume);
		changeBackgroundImageView = (ImageView) findViewById(R.id.icon_background);
		batterySaverModeImageView = (ImageView) findViewById(R.id.icon_battery_saver);

		int iconWidth = WIDTH / 6;
		// int iconHeight = HEIGHT / 16;

		// set the width of the image views
		toggleImageView.getLayoutParams().width = iconWidth;
		viewModeImageView.getLayoutParams().width = iconWidth;
		appChangeIntensityImageView.getLayoutParams().width = iconWidth;
		volumeImageView.getLayoutParams().width = iconWidth;
		changeBackgroundImageView.getLayoutParams().width = iconWidth;
		batterySaverModeImageView.getLayoutParams().width = iconWidth;

		// set the height of the image views
		// toggleImageView.getLayoutParams().height = iconHeight;
		// viewModeImageView.getLayoutParams().height = iconHeight;
		// appChangeIntensityImageView.getLayoutParams().height = iconHeight;
		// volumeImageView.getLayoutParams().height = iconHeight;
		// changeBackgroundImageView.getLayoutParams().height = iconHeight;
		// batterySaverModeImageView.getLayoutParams().height = iconHeight;

		// toggle imageview to hide the menu
		toggleImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Animation anim = new TranslateAnimation(0, 0, HEIGHT,
						HEIGHT * 2);
				anim.setDuration(animationDuration);
				iconLayout.setVisibility(View.GONE);
				iconLayout.setAnimation(anim);
			}
		});

		// icon layout
		iconLayout = (LinearLayout) findViewById(R.id.icon_container);

		String backgroundUriStrig = sharedPref.getString(
				Constants.BACKGROUND_URI, null);
		if (backgroundUriStrig != null) {
			setBackground(Uri.parse(backgroundUriStrig));
		}

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

		Fragment appFragment = new AppFragment();

		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		ft.add(R.id.main_frame, appFragment, Constants.TAG_APP).commit();

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

}
