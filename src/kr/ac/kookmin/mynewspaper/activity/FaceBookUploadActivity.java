package kr.ac.kookmin.mynewspaper.activity;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import kr.ac.kookmin.mynewspaper.BitmapCreator;
import kr.ac.kookmin.mynewspaper.R;
import kr.ac.kookmin.mynewspaper.db.DBHelper;
import kr.ac.kookmin.mynewspaper.facebook.BasicInfo;
import kr.ac.kookmin.mynewspaper.facebook.FacebookRequestThread;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

import com.facebook.android.Facebook;

/**
 * 액티비티 : 페이스북에 기사 이미지와 글을 업로드하는 화면
 **/
public class FaceBookUploadActivity extends Activity {
	private static final String TAG = "FaceBookUploadActivity";

	// View
	private Button mUploadFacebookBtn;
	private ImageView mUploadFacebookImage;
	private EditText mUploadFacebookText;
	private FrameLayout mUploadFrameLayout;
	private ProgressBar mNetworkProgressBar;

	// Article Id
	private long mId;

	// DB
	private DBHelper mDBHelper;

	// Bitmap
	private Bitmap mUploadBitmap;

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facebook_upload);

		Log.d(TAG, "FaceBookUploadActivity onCreate 진입");

		// 초기화
		init();

		// 기사 id 받기
		mId = getIntent().getExtras().getLong("Id");

		// ImageView 설정
		setImageView();

		// 프로그래스바 설정
		mNetworkProgressBar = new ProgressBar(FaceBookUploadActivity.this);
		FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.CENTER);
		mNetworkProgressBar.setLayoutParams(param);
		mNetworkProgressBar.setVisibility(View.INVISIBLE);
		mUploadFrameLayout.addView(mNetworkProgressBar);

		// upload 버튼 리스너
		mUploadFacebookBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNetworkProgressBar.setVisibility(View.VISIBLE);
				uploadFacebook();
			}
		});

	}

	private void uploadFacebook() {
		try {
			byte[] uploadImage = BitmapCreator
					.getByteArrayFromBitmap(mUploadBitmap);
			String uploadComment = mUploadFacebookText.getText()
					.toString();
			
			Log.d(TAG, "페이스북 업로드 uploadComment : "
					+ uploadComment);
			Log.d(TAG, "페이스북 업로드 BasicInfo.FACEBOOK_NAME : "
					+ BasicInfo.FACEBOOK_NAME);
			

		
			
			Bundle params = new Bundle();
			params.putString("access_token", BasicInfo.FACEBOOK_ACCESS_TOKEN);
			params.putString("message", uploadComment);
			//params.putString("name", BasicInfo.FACEBOOK_NAME);
			//params.putString("link", "");
		//	params.putString("description", "Message from Android.");
			params.putByteArray("picture", uploadImage);

			FacebookRequestThread request = new FacebookRequestThread(
					FacebookRequestThread.UPLOAD, FaceBookUploadActivity.this,
					mFacebookRequestHandler, params);
			request.setDaemon(true);
			request.start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * DB에서 파일위치 가져와서 이미지뷰에 저장
	 **/
	private void setImageView() {
		Log.d(TAG, "DB 요청 id : " + mId);
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"SELECT _id, file_location FROM articles WHERE _id=" + mId,
				null);
		if (cursor.moveToFirst()) {
			Log.d(TAG,
					"file_location 컬럼의 인덱스 : "
							+ cursor.getColumnIndex("file_location"));
			String file_location = cursor.getString(1);
			Log.d(TAG, "파일 위치 : " + file_location);
			mUploadBitmap = BitmapFactory.decodeFile(file_location);
			mUploadFacebookImage.setImageBitmap(mUploadBitmap);

		}
		cursor.close();

	}

	/**
	 * 액티비티 만들어질 때 필요한 초기화 수행
	 **/
	private void init() {
		// view 객체화
		mUploadFacebookBtn = (Button) findViewById(R.id.upload_facebook);
		mUploadFacebookImage = (ImageView) findViewById(R.id.upload_facebook_image);
		mUploadFacebookText = (EditText) findViewById(R.id.upload_facebook_text);
		mUploadFrameLayout = (FrameLayout) findViewById(R.id.facebook_upload_frame_layout);

		// db 헬퍼
		mDBHelper = new DBHelper(FaceBookUploadActivity.this);

		// facebook
		Facebook facebook = new Facebook(BasicInfo.FACEBOOK_APP_ID);
		BasicInfo.FacebookInstance = facebook;

	}

	private Handler mFacebookRequestHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == FacebookRequestThread.UPLOAD) {
				Toast.makeText(FaceBookUploadActivity.this, "업로드 완료",
						Toast.LENGTH_LONG).show();
				finish();
			}

		}

	};
}
