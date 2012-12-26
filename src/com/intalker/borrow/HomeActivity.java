package com.intalker.borrow;

import com.intalker.borrow.util.ColorUtil;
import com.intalker.borrow.util.DensityAdaptor;
import com.intalker.borrow.util.internetUtil;
import com.intalker.tencentinterface.TencentConnection;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.TencentOpenAPI2;
import com.tencent.tauth.TencentOpenHost;
import com.tencent.tauth.TencentOpenRes;
import com.tencent.tauth.bean.OpenId;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.TDebug;
import com.tencent.tauth.http.RequestListenerImpl.OpenIDListener;

import android.R.color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private LinearLayout mMainLayout = null;
	private LinearLayout mSelfNavigationLayout = null;
	private LinearLayout mFriendsNavigationLayout = null;
	private LinearLayout mBooksNavigationLayout = null;

	private ImageView mLoginBtn = null;
	private LinearLayout mSelfInfoPanel = null;
	private ImageView mProfileImg = null;
	private TextView mProfileNick = null;
	private TencentConnection mTencentConnection = null;
	private TencentEventHandler mTencentHandler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_home);
		mTencentConnection = new TencentConnection(this);
		mTencentHandler = new TencentEventHandler();
		mTencentConnection.setEventHandler(mTencentHandler);
		createHomeUI();
		setContentView(mMainLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	private void createHomeUI() {
		mMainLayout = new LinearLayout(this);
		mMainLayout.setOrientation(LinearLayout.HORIZONTAL);
		mMainLayout.setBackgroundColor(Color.GRAY);

		mMainLayout.addView(createFriendsNavigation());
		mMainLayout.addView(createBooksNavigation());
	}

	private View createFriendsNavigation() {
		mFriendsNavigationLayout = new LinearLayout(this);
		mFriendsNavigationLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams navigationBarLP = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		mFriendsNavigationLayout.setLayoutParams(navigationBarLP);
		mFriendsNavigationLayout.setBackgroundColor(Color.DKGRAY);

		mFriendsNavigationLayout.addView(createSelfNavigation());

		for (int i = 0; i < 10; ++i) {
			mFriendsNavigationLayout.addView(createTestFriendItemUI());
		}

		return mFriendsNavigationLayout;
	}

	private View createSelfNavigation() {
		mSelfNavigationLayout = new LinearLayout(this);
		mSelfNavigationLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams navigationBarLP = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		mSelfNavigationLayout.setLayoutParams(navigationBarLP);
		mSelfNavigationLayout.setBackgroundColor(Color.DKGRAY);

		if (!mTencentConnection.containsValidCacheToken())
			mSelfNavigationLayout.addView(createLoginButton());
		else
			mSelfNavigationLayout.addView(createSelfPanel());

		return mSelfNavigationLayout;
	}

	private View createLoginButton() {
		mLoginBtn = new ImageView(this);
		// loginBtn.setText("Login");
		LinearLayout.LayoutParams navigationBarLP = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		mLoginBtn.setLayoutParams(navigationBarLP);

		mLoginBtn.setImageDrawable(mTencentConnection.getLoginButton(0));
		mLoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTencentConnection.populateLogin();
			}
		});
		return mLoginBtn;
	}

	private View createSelfPanel() {
		mSelfInfoPanel = new LinearLayout(this);
		mSelfInfoPanel.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams navigationBarLP = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		mSelfInfoPanel.setLayoutParams(navigationBarLP);
		mSelfInfoPanel.setBackgroundColor(Color.DKGRAY);

		mSelfInfoPanel.addView(createProfileImg());
		mSelfInfoPanel.addView(createProfileNick());

		return mSelfInfoPanel;
	}

	private View createProfileImg() {
		mProfileImg = new ImageView(this);
		LinearLayout.LayoutParams navigationBarLP = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		mProfileImg.setLayoutParams(navigationBarLP);

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				mProfileImg.setImageBitmap(internetUtil.getBitmapFromURLContent(mTencentConnection.getUserInfo().getIcon_100()));
//			}
//		}).start();

		mProfileImg.setImageResource(R.drawable.ic_launcher);
		return mProfileImg;
	}

	private View createProfileNick() {
		mProfileNick = new TextView(this);
		LinearLayout.LayoutParams navigationBarLP = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		mProfileNick.setLayoutParams(navigationBarLP);

		mProfileNick.setText(mTencentConnection.getUserInfo().getNickName());
		// mProfileNick.setText(mTencentConnection.getUserProfile().getRealName());
		return mProfileNick;
	}

	private View createBooksNavigation() {
		LinearLayout navigationBar = new LinearLayout(this);
		navigationBar.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams navigationBarLP = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		navigationBar.setLayoutParams(navigationBarLP);
		navigationBar.setBackgroundColor(Color.DKGRAY);

		return navigationBar;
	}

	private View createTestFriendItemUI() {
		// RelativeLayout item = new RelativeLayout(this);
		//
		// LinearLayout.LayoutParams itemLP = new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.WRAP_CONTENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		// itemLP.width = DensityAdaptor.getDensityIndependentValue(240);
		// itemLP.height = DensityAdaptor.getDensityIndependentValue(64);
		//
		// item.setBackgroundColor(ColorUtil.generateRandomColor());
		// item.setLayoutParams(itemLP);

		ImageView avatar = new ImageView(this);
		avatar.setImageResource(R.drawable.avatar);
		RelativeLayout.LayoutParams avatarLP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		avatarLP.width = DensityAdaptor.getDensityIndependentValue(48);
		avatarLP.height = DensityAdaptor.getDensityIndependentValue(48);
		avatarLP.addRule(RelativeLayout.CENTER_VERTICAL);
		avatarLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		avatarLP.rightMargin = DensityAdaptor.getDensityIndependentValue(8);
		avatar.setLayoutParams(avatarLP);
		return avatar;
	}

	private class TencentEventHandler extends Handler {
		// @override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TencentConnection.TENCENT_LOGIN_SUCC:
				mTencentConnection.requestUserInfo();
				// mTencentConnection.requestUserProfile();
				break;
			case TencentConnection.TENCENT_LOGIN_FAIL:
				break;
			case TencentConnection.TENCENT_GETUSERINFO_SUCC:
				mSelfNavigationLayout.removeView(mLoginBtn);
				mSelfNavigationLayout.addView(createSelfPanel());
				break;
			case TencentConnection.TENCENT_GETUSERINFO_FAIL:
				break;
			case TencentConnection.TENCENT_GETUSERPROFILE_SUCC:
				mSelfNavigationLayout.removeView(mLoginBtn);
				mSelfNavigationLayout.addView(createSelfPanel());
				break;
			case TencentConnection.TENCENT_GETUSERPROFILE_FAIL:
				break;
			default:
				break;
			}

		}
	}
}
