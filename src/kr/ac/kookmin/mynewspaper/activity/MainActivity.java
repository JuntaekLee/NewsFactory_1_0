package kr.ac.kookmin.mynewspaper.activity;

import kr.ac.kookmin.mynewspaper.BitmapCreator;
import kr.ac.kookmin.mynewspaper.ClientNetworkThread;
import kr.ac.kookmin.mynewspaper.R;
import kr.ac.kookmin.mynewspaper.db.DBHelper;
import kr.ac.kookmin.mynewspaper.db.DBManager;
import kr.ac.kookmin.mynewspaper.facebook.BasicInfo;
import kr.ac.kookmin.mynewspaper.facebook.FacebookRequestThread;
import kr.ac.kookmin.mynewspaper.protocol.RequestData;
import kr.ac.kookmin.mynewspaper.protocol.RequestGetUserInfo;
import kr.ac.kookmin.mynewspaper.protocol.ResponseData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

/**
 * 액티비티 : 시작 화면
 **/
public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	// Host
	public static final String HOST_ADDRESS = "192.168.15.33"; // Home Server
	public static final int HOST_PORT = 3307;

	// Request Code
	public static final int REQUEST_PROFILE_PHOTO = 0;

	// View
	private Button mUserInfoDeleteBtn;
	private Button mWriteArticleBtn;
	private Button mReadArticleBtn;
	private Button mShareArticleBtn;
	private Button mUploadFacebook;
	private Button mUpdateInformation;
	private TextView mUserName;
	private TextView mSumOfMyArticle;
	private TextView mSumOfUploadArticle;
	private TextView mRecommendCount;
	private ImageView mProfilePhoto;
	private DisplayMetrics mDm;

	// DB
	private DBHelper mDBHelper;
	private DBManager mDBManager;

	@Override
	protected void onPause() {
		super.onPause();

		// 정보 저장
		saveProperties();
	}

	@Override
	protected void onResume() {
		super.onResume();


		loadProperties();

		// 정보 업데이트
		if (BasicInfo.FacebookLogin == true)
			updateInfo();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d(TAG, "MainActivity onCreate 진입");

		// 초기화
		init();

		// 리스너 등록
		setListener();



	}
	
	

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		// 프로필 Pref 불러오기
		SharedPreferences pref = getSharedPreferences("PROFILE", MODE_PRIVATE);
		String uriString = pref.getString("Profile_Photo_Uri", "");
		Log.d(TAG, "Uri String : " + uriString);
		if(!uriString.equals("")){
			Uri photoUri = Uri.parse(uriString);
			Bitmap bm = BitmapCreator.createScaledBitmap(photoUri, mProfilePhoto, getContentResolver());
			mProfilePhoto.setImageBitmap(bm);
		}
	}

	/**
	 * 갤러리에서 Photo 선택 후 호출된다. ImageView에 선택한 이미지를 올리자.
	 **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			Uri selectedPhotoUri = data.getData();
			switch (requestCode) {
			case REQUEST_PROFILE_PHOTO:
				Bitmap bm = BitmapCreator.createScaledBitmap(selectedPhotoUri,
						mProfilePhoto, getContentResolver());
				mProfilePhoto.setImageBitmap(bm);

				// Pref 저장
				SharedPreferences pref = getSharedPreferences("PROFILE",
						MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString("Profile_Photo_Uri",
						selectedPhotoUri.toString());
				editor.commit();

				break;

			}
		}
	}

	private void updateInfo() {
		// 이름 갱신
		mUserName.setText(BasicInfo.FACEBOOK_NAME);

		// 보유 기사 조사
		mDBManager.DBReadSumOfArticle("articles", mDBReaderHandler);

		// 공유 기사 조사
		RequestData requestData = new RequestGetUserInfo(
				RequestData.GET_USER_INFO, BasicInfo.FACEBOOK_EMAIL);
		ClientNetworkThread request = new ClientNetworkThread(HOST_ADDRESS,
				requestData, mClientNetworkHandler);
		request.setDaemon(true);
		request.start();
	}

	private void setListener() {
		// Button Listener 등록
		mWriteArticleBtn.setOnClickListener(mBtnClickListener);
		mReadArticleBtn.setOnClickListener(mBtnClickListener);
		mShareArticleBtn.setOnClickListener(mBtnClickListener);
		mUploadFacebook.setOnClickListener(mBtnClickListener);
		mUpdateInformation.setOnClickListener(mBtnClickListener);
		mUserInfoDeleteBtn.setOnClickListener(mBtnClickListener);
		mProfilePhoto.setOnClickListener(mBtnClickListener);
	}

	/**
	 * 액티비티 만들어질 때 필요한 초기화 수행
	 **/
	private void init() {
		// View 객체화
		mWriteArticleBtn = (Button) findViewById(R.id.write_article_btn);
		mReadArticleBtn = (Button) findViewById(R.id.read_my_article_btn);
		mShareArticleBtn = (Button) findViewById(R.id.share_article_btn);
		mUploadFacebook = (Button) findViewById(R.id.upload_facebook);
		mUpdateInformation = (Button) findViewById(R.id.update_information);
		mUserName = (TextView) findViewById(R.id.user_name);
		mSumOfMyArticle = (TextView) findViewById(R.id.sum_of_my_article);
		mSumOfUploadArticle = (TextView) findViewById(R.id.sum_of_upload_article);
		mRecommendCount = (TextView) findViewById(R.id.receive_recommend_count);
		mUserInfoDeleteBtn = (Button) findViewById(R.id.facebook_logout_x_btn);
		mProfilePhoto = (ImageView) findViewById(R.id.profile_image);

		// DB
		mDBHelper = new DBHelper(MainActivity.this);
		mDBManager = new DBManager(mDBHelper);
		
		mDm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDm);

	}

	private void saveProperties() {
		SharedPreferences pref = getSharedPreferences("FACEBOOK", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean("FacebookLogin", BasicInfo.FacebookLogin);
		editor.putString("FACEBOOK_ACCESS_TOKEN",
				BasicInfo.FACEBOOK_ACCESS_TOKEN);
		editor.putString("FACEBOOK_NAME", BasicInfo.FACEBOOK_NAME);
		editor.putString("FACEBOOK_EMAIL", BasicInfo.FACEBOOK_EMAIL);
		editor.commit();
		Log.d(TAG, "saveProperties 커밋완료");
	}

	private void loadProperties() {
		SharedPreferences pref = getSharedPreferences("FACEBOOK", MODE_PRIVATE);
		BasicInfo.FacebookLogin = pref.getBoolean("FacebookLogin", false);
		BasicInfo.FACEBOOK_NAME = pref.getString("FACEBOOK_NAME", "");
		BasicInfo.FACEBOOK_ACCESS_TOKEN = pref.getString(
				"FACEBOOK_ACCESS_TOKEN", "");
		BasicInfo.FACEBOOK_EMAIL = pref.getString("FACEBOOK_EMAIL", "");
		if (BasicInfo.FacebookInstance == null) {
			BasicInfo.FacebookInstance = new Facebook(BasicInfo.FACEBOOK_APP_ID);
			BasicInfo.FacebookInstance
					.setAccessToken(BasicInfo.FACEBOOK_ACCESS_TOKEN);
		}
		Log.d(TAG, "loadProperties 로드완료 FACEBOOK_NAME : "
				+ BasicInfo.FACEBOOK_NAME + ", EMAIL : "
				+ BasicInfo.FACEBOOK_EMAIL + ", FACEBOOK_ACCESS_TOKEN : "
				+ BasicInfo.FACEBOOK_ACCESS_TOKEN + ", Login : "
				+ BasicInfo.FacebookLogin);
	}

	private void connect() {
		try {
			Facebook mFacebook = new Facebook(BasicInfo.FACEBOOK_APP_ID);
			BasicInfo.FacebookInstance = mFacebook;

			Log.d(TAG, "mFacebook.authorize 호출 전");
			mFacebook.authorize(this, BasicInfo.FACEBOOK_PERMISSIONS,
					new AuthorizationListener());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void logout() {
		try {
			if (BasicInfo.FacebookLogin == true) {
				Log.d(TAG, "로그아웃 시작");
				// 로그아웃하기
				FacebookRequestThread requestThread = new FacebookRequestThread(
						FacebookRequestThread.LOGOUT, MainActivity.this,
						mFacebookRequestHandler);
				requestThread.setDaemon(true);
				requestThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 함수 : getImageGallery /// 갤러리를 호출한다.
	 **/
	public void getImageGallery(int id) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		switch (id) {
		case R.id.profile_image:
			startActivityForResult(intent, REQUEST_PROFILE_PHOTO);
			break;
		}
	}

	private OnClickListener mBtnClickListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			case R.id.profile_image:
				getImageGallery(R.id.profile_image);
				break;
			case R.id.write_article_btn:
				intent = new Intent(MainActivity.this,
						SelectPaperStyleActivity.class);
				startActivity(intent);
				break;
				
			case R.id.read_my_article_btn:
				intent = new Intent(MainActivity.this,
						SelectMyArticleActivity.class);
				intent.putExtra("Work", SelectMyArticleActivity.READ);
				startActivity(intent);
				break;
			case R.id.share_article_btn:
				intent = new Intent(MainActivity.this,
						BoardActivity.class);
				startActivity(intent);
				break;
			case R.id.upload_facebook:
				if (BasicInfo.FacebookLogin == false) {
					Log.d(TAG, "페이스북 로그인 connect 호출전");
					connect();
				} else {
					intent = new Intent(MainActivity.this,
							SelectMyArticleActivity.class);
					intent.putExtra("Work", SelectMyArticleActivity.FACEBOOK);
					intent.putExtra("UserName", BasicInfo.FACEBOOK_EMAIL);
					startActivity(intent);
				}
				break;
			case R.id.update_information:
				if (BasicInfo.FacebookLogin == false)
					connect();
				else
					updateInfo();
				break;
			case R.id.facebook_logout_x_btn:
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						MainActivity.this);
				dialog.setMessage("페이스북 로그아웃 하시겠습니까?")
						.setNegativeButton("취소", null)
						.setPositiveButton("확인",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										logout();
									}
								}).show();

				break;
				
			}
		}
	};

	private Handler mDBReaderHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 총 기사 보유 개수 화면갱신
			long sumOfArticle = msg.getData().getLong("SumOfArticle");
			mSumOfMyArticle.setText(Long.toString(sumOfArticle) + " 개");
		}

	};

	private Handler mClientNetworkHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			Log.d(TAG, "mClientNetworkHandler 진입");
			ResponseData responseData = (ResponseData) msg.obj;
			if (responseData.getResponseCode() == ResponseData.RESULT_OK) {
				// 공유한 기사 개수 화면갱신
				Log.d(TAG,
						"responseData.getSumOfMyArticle() : "
								+ responseData.getSumOfMyArticle());
				mSumOfUploadArticle.setText(Long.toString(responseData
						.getSumOfMyArticle()) + " 개");

				// 추천수 화면 갱신
				mRecommendCount.setText(Long.toString(responseData
						.getSumOfRecommend()) + " 개");
			}
		}

	};

	private Handler mFacebookRequestHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FacebookRequestThread.REQUEST_ME:

				break;
			case FacebookRequestThread.LOGOUT:
				Toast.makeText(MainActivity.this, "로그아웃되었습니다.",
						Toast.LENGTH_LONG).show();
				// SharedPreferences 내용 삭제
				SharedPreferences pref = getSharedPreferences("FACEBOOK",
						MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.clear();
				editor.commit();

				// 페이스북 정보 다시 갱신
				loadProperties();

				// UI 갱신
				mUserName.setText("");
				mSumOfMyArticle.setText("");
				mSumOfUploadArticle.setText("");
				mRecommendCount.setText("");
				break;
			}
		}
	};

	private class AuthorizationListener implements DialogListener {

		public void onCancel() {

		}

		public void onComplete(Bundle values) {
			Log.d(TAG, "AuthorizationListener 의  onComplete 호출");
			FacebookRequestThread requestThread = new FacebookRequestThread(
					FacebookRequestThread.REQUEST_ME, mFacebookRequestHandler);
			requestThread.setDaemon(true);
			requestThread.start();

			// showUserTimeline();

		}

		public void onError(DialogError e) {

		}

		public void onFacebookError(FacebookError e) {

		}

	}
}
