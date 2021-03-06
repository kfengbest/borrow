package com.intalker.borrow;

import com.intalker.borrow.cloud.CloudAPI;
import com.intalker.borrow.cloud.CloudAPIAsyncTask.ICloudAPITaskListener;
import com.intalker.borrow.config.AppConfig;
import com.intalker.borrow.config.ResultCode;
import com.intalker.borrow.data.UserInfo;
import com.intalker.borrow.friends.FriendsNavigationVertical;
import com.intalker.borrow.isbn.ISBNResolver;
import com.intalker.borrow.ui.book.BookGallery;
import com.intalker.borrow.ui.book.BookShelfItem;
import com.intalker.borrow.ui.control.sliding.SlidingMenu;
import com.intalker.borrow.ui.login.LoginDialog;
import com.intalker.borrow.ui.login.RegisterView;
import com.intalker.borrow.util.ColorUtil;
import com.intalker.borrow.util.DensityAdaptor;
import com.intalker.borrow.util.JSONUtil;
import com.intalker.borrow.util.LayoutUtil;
import com.intalker.borrow.util.StorageUtil;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private static HomeActivity app = null;
	private BookGallery mBookGallery = null;

	private RegisterView mReg = null;
	private FriendsNavigationVertical mFriendsNavigation = null; // it also
																	// contains
																	// self info
																	// and
																	// action
																	// button!
	private SlidingMenu mSlidingMenu = null;

	public void toggleLeftPanel() {
		mSlidingMenu.toggleLeftView();
	}

	public static HomeActivity getApp() {
		return app;
	}

	public BookGallery getBookGallery() {
		return mBookGallery;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_home);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		app = this;
		DensityAdaptor.init(this);
		StorageUtil.initialize();
		StorageUtil.loadCachedBooks();
		CloudAPI.CloudToken = "";

		setContentView(initializeWithSlidingStyle());
	}

	private View initializeWithSlidingStyle() {
		mSlidingMenu = new SlidingMenu(this);

		mSlidingMenu.setLeftView(createNavigationPanel());
		mSlidingMenu.setCenterView(createHomeUI());
		return mSlidingMenu;
	}

	// private View createLeftNavigationPanel()
	// {
	// RelativeLayout naviPanel = new RelativeLayout(this);
	// Button btn = new Button(this);
	// RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	// RelativeLayout.LayoutParams.WRAP_CONTENT,
	// RelativeLayout.LayoutParams.WRAP_CONTENT);
	// lp.width = LayoutUtil.getNavigationPanelWidth();
	// naviPanel.addView(btn, lp);
	// return naviPanel;
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.activity_home, menu);
		menu.addSubMenu("Settings");
		menu.addSubMenu("Help");
		menu.addSubMenu("About");
		menu.addSubMenu("Exit");
		return true;
	}

	private void doAfterSignUp(int returnCode) {
		switch (returnCode) {
		case CloudAPI.Return_OK:
			Toast.makeText(this, UserInfo.getCurLoggedinUser().toString(),
					Toast.LENGTH_SHORT).show();
			break;
		case CloudAPI.Return_UserNameOccupied:
			Toast.makeText(this, "User name occupied.", Toast.LENGTH_SHORT)
					.show();
			break;
		case CloudAPI.Return_NetworkError:
			Toast.makeText(this, "Network error.", Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(this, "Unknown error.", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	private void doAfterUplaod(int returnCode) {
		switch (returnCode) {
		case CloudAPI.Return_OK:
			Toast.makeText(this, "Upload done!", Toast.LENGTH_SHORT).show();
			break;
		case CloudAPI.Return_BadToken:
			Toast.makeText(this, "Bad token.", Toast.LENGTH_SHORT)
					.show();
			break;
		case CloudAPI.Return_NetworkError:
			Toast.makeText(this, "Network error.", Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(this, "Unknown error.", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	private void doAfterGetOwnedBooks(int returnCode) {
		switch (returnCode) {
		case CloudAPI.Return_OK:
			Toast.makeText(this, "Sync done!", Toast.LENGTH_SHORT).show();
			break;
		case CloudAPI.Return_BadToken:
			Toast.makeText(this, "Bad token.", Toast.LENGTH_SHORT)
					.show();
			break;
		case CloudAPI.Return_NetworkError:
			Toast.makeText(this, "Network error.", Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(this, "Unknown error.", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void doAfterGetUserInfoByToken(int returnCode) {
		switch (returnCode) {
		case CloudAPI.Return_OK:
			mBookGallery.updateTopPanel();
			break;
		case CloudAPI.Return_NoSuchUser:
			Toast.makeText(this, "No such user.", Toast.LENGTH_SHORT).show();
			break;
		case CloudAPI.Return_BadToken:
			Toast.makeText(this, "Bad token.", Toast.LENGTH_SHORT).show();
			break;
		case CloudAPI.Return_NetworkError:
			Toast.makeText(this, "Network error.", Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(this, "Unknown error.", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	public View createHomeUI() {
		BookShelfItem.lastBookForTest = null;

		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.HORIZONTAL);

		// Add this listener to make the whole gallery be dragable.
		mainLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Toast.makeText(arg0.getContext(), "Test",
				// Toast.LENGTH_SHORT).show();
			}

		});

		// book gallery ui
		mBookGallery = new BookGallery(this);
		LinearLayout.LayoutParams bookGalleryLP = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		mainLayout.addView(mBookGallery, bookGalleryLP);

		mBookGallery.initialWithCachedData();

		mReg = new RegisterView(this);
		mainLayout.addView(mReg);
		mReg.setVisibility(View.GONE);
		mReg.setRegisterListener(new RegisterView.OnRegisterListener() {
			
			@Override
			public void onSuccess() {
				mReg.setVisibility(View.GONE);
				mBookGallery.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onBack()
			{
				mReg.setVisibility(View.GONE);
				mBookGallery.setVisibility(View.VISIBLE);
			}
		});


		// Test settings
		mainLayout.setBackgroundColor(Color.GRAY);
		return mainLayout;
	}

	private View createNavigationPanel() {
		LinearLayout navigationBar = new LinearLayout(this);
		navigationBar.setBackgroundColor(Color.DKGRAY);
		navigationBar.setOrientation(LinearLayout.VERTICAL);
		RelativeLayout.LayoutParams navigationBarLP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.FILL_PARENT);

		navigationBarLP.width = LayoutUtil.getNavigationPanelWidth();

		navigationBar.setLayoutParams(navigationBarLP);

		Button b = new Button(this);
		b.setText("<<");
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSlidingMenu.toggleLeftView();
			}

		});
		navigationBar.addView(b);
		// Sign up test
		ImageButton btn0 = new ImageButton(this);
		// btn0.setText("Reg");
		btn0.setImageResource(R.drawable.register);
		btn0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSlidingMenu.toggleLeftView();
				mBookGallery.setVisibility(View.GONE);
				mReg.setVisibility(View.VISIBLE);
				// Login API test
				// CloudAPI.signUp(v.getContext(),
				// "xiangyun.gaox@adsk.com",
				// "gao",
				// "Xiangyun",
				// new ICloudAPITaskListener(){
				//
				// @Override
				// public void onFinish(int returnCode) {
				// doAfterSignUp(returnCode);
				// }
				//
				// });
			}
		});

		navigationBar.addView(btn0);

		ImageButton btn = new ImageButton(this);
		// btn.setText("Login");
		btn.setImageResource(R.drawable.login);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO: it is possible to add annimation to shown an dialog ?
				LoginDialog loginDialog = new LoginDialog(v.getContext());
				loginDialog.show();
			}
		});
		navigationBar.addView(btn);

		if (AppConfig.isDebugMode) {
			Button btn_loginbysession = new Button(this);
			btn_loginbysession.setText("Login by test token");
			btn_loginbysession.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Login API test
					String testToken = "7a3cf000-0662-715b-a6a8-89feb8466014";
					CloudAPI.setAccessToken(testToken);
					CloudAPI.getLoggedInUserInfo(v.getContext(),
							new ICloudAPITaskListener() {

								@Override
								public void onFinish(int returnCode) {
									doAfterGetUserInfoByToken(returnCode);
								}

							});
				}
			});

			navigationBar.addView(btn_loginbysession);
			
			Button btn_test = new Button(this);
			btn_test.setText("Temp Test");
			btn_test.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// For any test code
//					CloudAPI.uploadBooks(HomeActivity.getApp(),
//							new ICloudAPITaskListener() {
//
//								@Override
//								public void onFinish(int returnCode) {
//									doAfterUplaod(returnCode);
//								}
//
//							});
					
					CloudAPI.getOwnedBooks(HomeActivity.getApp(),
							new ICloudAPITaskListener() {

								@Override
								public void onFinish(int returnCode) {
									doAfterGetOwnedBooks(returnCode);
								}

							});
				}
			});

			navigationBar.addView(btn_test);
		}

		// ImageButton btn1 = new ImageButton(this);
		// // btn1.setText("Scan");
		// btn1.setImageResource(R.drawable.scan);
		// btn1.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// ScanUtil.scanBarCode(HomeActivity.this);
		// }
		// });
		//
		// navigationBar.addView(btn1);

		if (AppConfig.isDebugMode) {
			Button btn2 = new Button(this);
			btn2.setText("Clear");
			btn2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mBookGallery.resetBookShelf();
				}
			});

			navigationBar.addView(btn2);
		}
		//
		// Button btn3 = new Button(this);
		// btn3.setText("Random");
		// btn3.setOnClickListener(new OnClickListener(){
		//
		// @Override
		// public void onClick(View v) {
		// mBookGallery.fillWithRandomBooks();
		// }
		// });
		//
		// navigationBar.addView(btn3);
		// mFriendsNavigation = new FriendsNavigationVertical(this);
		// navigationBar.addView(mFriendsNavigation.createFriendsNavigationUI());

		ScrollView testFriendScrollView = new ScrollView(this);
		LinearLayout friendsLayout = new LinearLayout(this);
		testFriendScrollView.addView(friendsLayout);
		friendsLayout.setOrientation(LinearLayout.VERTICAL);
		for (int i = 0; i < 8; ++i) {
			friendsLayout.addView(createTestFriendItemUI(R.drawable.avatar_2));
		}
		for (int i = 0; i < 8; ++i) {
			friendsLayout.addView(createTestFriendItemUI(R.drawable.avatar_3));
		}
		for (int i = 0; i < 8; ++i) {
			friendsLayout.addView(createTestFriendItemUI(R.drawable.avatar_1));
		}
		navigationBar.addView(testFriendScrollView);
		return navigationBar;
	}

	private View createTestFriendItemUI(int avatarId) {
		RelativeLayout item = new RelativeLayout(this);

		LinearLayout.LayoutParams itemLP = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		itemLP.width = LayoutUtil.getNavigationPanelWidth();
		itemLP.height = DensityAdaptor.getDensityIndependentValue(40);

		item.setBackgroundColor(ColorUtil.generateRandomColor());
		item.setLayoutParams(itemLP);

		TextView t = new TextView(this);
		t.setText("Test");
		item.addView(t);

		ImageView avatar = new ImageView(this);
		avatar.setImageResource(avatarId);
		RelativeLayout.LayoutParams avatarLP = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		avatarLP.width = DensityAdaptor.getDensityIndependentValue(32);
		avatarLP.height = DensityAdaptor.getDensityIndependentValue(32);
		avatarLP.addRule(RelativeLayout.CENTER_VERTICAL);
		avatarLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		avatarLP.rightMargin = DensityAdaptor.getDensityIndependentValue(8);
		item.addView(avatar, avatarLP);
		return item;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ResultCode.SCAN_RESULT_CODE) {
			switch (resultCode) {
			case RESULT_OK:
				String isbn = data.getStringExtra("SCAN_RESULT");
				int length = isbn.length();
				if (10 == length || 13 == length) {
					ISBNResolver.getInstance().getBookInfoByISBN(this, isbn);
				} else {
					Toast.makeText(this, this.getString(R.string.invalid_isbn),
							Toast.LENGTH_SHORT).show();
				}
				break;
			case RESULT_CANCELED:
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		StorageUtil.saveCachedBooks();
	}
}