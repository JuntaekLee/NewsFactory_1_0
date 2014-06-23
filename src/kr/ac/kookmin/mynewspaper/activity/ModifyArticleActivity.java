package kr.ac.kookmin.mynewspaper.activity;

import java.util.ArrayList;
import java.util.Calendar;

import kr.ac.kookmin.mynewspaper.BitmapCreator;
import kr.ac.kookmin.mynewspaper.NewspaperData;
import kr.ac.kookmin.mynewspaper.R;
import kr.ac.kookmin.mynewspaper.SaveArticleBitmap;
import kr.ac.kookmin.mynewspaper.db.DBHelper;
import kr.ac.kookmin.mynewspaper.db.DBManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 액티비티 : 기사를 수정하는 화면
 **/
public class ModifyArticleActivity extends Activity {

	private static final String TAG = "ModifyArticleActivity";

	// Request Code
	private static final int REQUEST_MAIN_PHOTO_1 = 0;
	private static final int REQUEST_MAIN_PHOTO_2 = 1;

	// View
	private LinearLayout mWriteArticleLayout;
	private TextView mTodayDate;
	private Button mModifyBtn;
	private Button mExitBtn;

	// Newspapar Layout
	private ArrayList<EditText> mMainTitleList;
	private ArrayList<EditText> mSubTitleList;
	private ArrayList<EditText> mContentList;
	private ArrayList<ImageView> mPhotoList;

	// DB
	private DBManager mDBManager;
	private DBHelper mDBHelper;
	private SQLiteDatabase mDB;

	// Newspaper Data
	private NewspaperData mNewspaperData;

	// 수정할 Id
	private long mId;

	// Waiting Progress
	private ProgressDialog mWaitingDialog;

	// 처음 시작일때만
	private boolean mIsFirst = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_article);

		// 초기화
		init();

		// 날짜 설정
		setTodayDate();

		// Modify라서 들어가는 부분
		restoreView();

		// 리스너 등록
		setOnClickListener();

	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				ModifyArticleActivity.this);
		dialog.setMessage("저장하지 않을 시 수정한 내용이 반영되지 않습니다.")
				.setNegativeButton("취소", null)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDB.close();
		// BitmapCreator.recycleImageViewBitmap(mMainPhoto1);
		// BitmapCreator.recycleImageViewBitmap(mMainPhoto2);
		// BitmapCreator.checkBitmapRecycle(mPhotoBitmap);

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
			case REQUEST_MAIN_PHOTO_1:
				Bitmap bm = BitmapCreator.createScaledBitmap(selectedPhotoUri,
						mPhotoList.get(0), getContentResolver());
				Log.d(TAG, "mMainPhoto1.setImageBitmap(mPhotoBitmap); 전");
				mPhotoList.get(0).setImageBitmap(bm);

				mNewspaperData.getPhoto()[0] = selectedPhotoUri.toString();
				break;
			case REQUEST_MAIN_PHOTO_2:
				bm = BitmapCreator.createScaledBitmap(selectedPhotoUri,
						mPhotoList.get(1), getContentResolver());
				mPhotoList.get(1).setImageBitmap(bm);

				mNewspaperData.getPhoto()[1] = selectedPhotoUri.toString();
				break;
			}
		}
	}

	/**
	 * 존재하는 View에 ClickListener 등록
	 **/
	private void setOnClickListener() {
		for (int i = 0; i < mMainTitleList.size(); i++) {
			if (mMainTitleList.get(i) != null)
				mMainTitleList.get(i).setOnClickListener(mViewClickListener);
		}
		for (int i = 0; i < mSubTitleList.size(); i++) {
			if (mSubTitleList.get(i) != null)
				mSubTitleList.get(i).setOnClickListener(mViewClickListener);
		}
		for (int i = 0; i < mContentList.size(); i++) {
			if (mContentList.get(i) != null)
				mContentList.get(i).setOnClickListener(mViewClickListener);
		}
		for (int i = 0; i < mPhotoList.size(); i++) {
			if (mPhotoList.get(i) != null)
				mPhotoList.get(i).setOnClickListener(mViewClickListener);
		}

		mModifyBtn.setOnClickListener(mViewClickListener);
		mExitBtn.setOnClickListener(mViewClickListener);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (mIsFirst == true) {
			Log.d(TAG, "onWindowFocusChanged 의 mIsFirst == true");
			Cursor cursor = mDB.rawQuery(
					"SELECT main_photo1, main_photo2 FROM articles WHERE _id="
							+ mId, null);
			cursor.moveToFirst();

			// ImageView 복구
			Uri uri = null;
			if (mPhotoList.get(0) != null) {
				Log.d(TAG, "복구중 cursor.getString(0) : " + cursor.getString(0));
				uri = Uri.parse(cursor.getString(0));

				Log.d(TAG, "mMainPhoto1 의 Uri : " + uri.toString());
				if (uri.toString().equals("null") == false) {
					Bitmap bm = BitmapCreator.createScaledBitmap(uri,
							mPhotoList.get(0), getContentResolver());
					mPhotoList.get(0).setImageBitmap(bm);

					mNewspaperData.getPhoto()[0] = uri.toString();
				}
			}
			if (mPhotoList.get(1) != null) {
				uri = Uri.parse(cursor.getString(1));
				if (uri.toString().equals("null") == false) {
					Bitmap bm = BitmapCreator.createScaledBitmap(uri,
							mPhotoList.get(1), getContentResolver());
					mPhotoList.get(1).setImageBitmap(bm);

					mNewspaperData.getPhoto()[1] = uri.toString();
				}
			}
			cursor.close();
			mIsFirst = false;
		}

	}

	private void restoreView() {
		Cursor cursor = mDB.rawQuery("SELECT * FROM articles WHERE _id=" + mId,
				null);
		cursor.moveToFirst();

		// 스타일 & 컬러 구해서 저장
		mNewspaperData.setStyle(cursor.getInt(4));
		mNewspaperData.setColor(cursor.getInt(3));

		// 레이아웃부터 확정한다.
		setLayoutBackground();

		// 존재하는 View를 찾는다.
		setExistingView();

		// EditText 복구
		int mainTitleSize = mMainTitleList.size();
		int subTitleSize = mSubTitleList.size();
		for (int i = 0; i < mainTitleSize; i++) {
			if (mMainTitleList.get(i) != null) {
				if (cursor.getString(i + 6).equals("") == false) {
					mMainTitleList.get(i).setText(cursor.getString(i + 6));
					mMainTitleList.get(i).setBackgroundColor(Color.TRANSPARENT);
				}
			}
		}
		for (int i = 0; i < mSubTitleList.size(); i++) {
			if (mSubTitleList.get(i) != null) {
				if (cursor.getString(i + 6 + mainTitleSize).equals("") == false) {
					mSubTitleList.get(i).setText(
							cursor.getString(i + 6 + mainTitleSize));
					mSubTitleList.get(i).setBackgroundColor(Color.TRANSPARENT);
				}
			}
		}
		for (int i = 0; i < mContentList.size(); i++) {
			if (mContentList.get(i) != null) {
				if (cursor.getString(i + 6 + mainTitleSize + subTitleSize)
						.equals("") == false) {
					mContentList.get(i).setText(
							cursor.getString(i + 6 + mainTitleSize
									+ subTitleSize));
					mContentList.get(i).setBackgroundColor(Color.TRANSPARENT);
				}
			}
		}

		cursor.close();
	}

	/**
	 * 함수 : getImageGallery /// 갤러리를 호출한다.
	 **/
	public void getImageGallery(int id) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		switch (id) {
		case R.id.main_photo1:
			startActivityForResult(intent, REQUEST_MAIN_PHOTO_1);
			break;
		case R.id.main_photo2:
			startActivityForResult(intent, REQUEST_MAIN_PHOTO_2);
			break;
		}
	}

	/**
	 * 실제 존재하는 View 객체화한다.
	 **/
	private void setExistingView() {
		mMainTitleList.add((EditText) findViewById(R.id.main_title1));
		mSubTitleList.add((EditText) findViewById(R.id.sub_title1));
		mSubTitleList.add((EditText) findViewById(R.id.sub_title2));
		mSubTitleList.add((EditText) findViewById(R.id.sub_title3));
		mContentList.add((EditText) findViewById(R.id.content1));
		mContentList.add((EditText) findViewById(R.id.content2));
		mContentList.add((EditText) findViewById(R.id.content3));
		mPhotoList.add((ImageView) findViewById(R.id.main_photo1));
		mPhotoList.add((ImageView) findViewById(R.id.main_photo2));
	}

	/**
	 * 전달받은 Color, Style을 배경으로 설정
	 **/
	private void setLayoutBackground() {
		/*
		 * Bundle bundle = getIntent().getExtras();
		 * mNewspaperData.setStyle(bundle.getInt("PaperStyle"));
		 * mNewspaperData.setColor(bundle.getInt("PaperColor"));
		 */
		int layoutId = 0;

		// Color 적용
		switch (mNewspaperData.getColor()) {
		case SelectPaperColorActivity.GRAY:
			mWriteArticleLayout.setBackgroundResource(R.drawable.article_gray);
			break;
		case SelectPaperColorActivity.RED:
			mWriteArticleLayout.setBackgroundResource(R.drawable.article_red);
			break;
		case SelectPaperColorActivity.SKY_BLUE:
			mWriteArticleLayout
					.setBackgroundResource(R.drawable.article_skyblue);
			break;
		case SelectPaperColorActivity.MELON:
			mWriteArticleLayout.setBackgroundResource(R.drawable.article_melon);
			break;
		case SelectPaperColorActivity.YELLOW:
			mWriteArticleLayout
					.setBackgroundResource(R.drawable.article_yellow);
			break;
		case SelectPaperColorActivity.PURPLE:
			mWriteArticleLayout
					.setBackgroundResource(R.drawable.article_purple);
			break;
		}
		// Style적용
		switch (mNewspaperData.getStyle()) {
		case 0:
			layoutId = R.layout.layout_paper_style1;
			break;
		case 1:
			layoutId = R.layout.layout_paper_style2;
			break;
		case 2:
			layoutId = R.layout.layout_paper_style3;
			break;
		case 3:
			layoutId = R.layout.layout_paper_style4;
			break;
		case 4:
			layoutId = R.layout.layout_paper_style5;
			break;
		case 5:
			layoutId = R.layout.layout_paper_style6;
			break;
		case 6:
			layoutId = R.layout.layout_paper_style7;
			break;
		}

		LayoutInflater li = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout contentLayout = (LinearLayout) li.inflate(layoutId,
				mWriteArticleLayout, false);

		mWriteArticleLayout.addView(contentLayout);

		// mWriteArticleLayout.setBackgroundResource(layoutId);
	}

	/**
	 * 액티비티 만들어질 때 필요한 초기화 수행
	 **/
	private void init() {
		mId = getIntent().getExtras().getLong("Id");
		// View 객체화
		mWriteArticleLayout = (LinearLayout) findViewById(R.id.wrtie_article_root_layout);
		mTodayDate = (TextView) findViewById(R.id.today_date);
		mModifyBtn = (Button) findViewById(R.id.modify_btn);
		mExitBtn = (Button) findViewById(R.id.article_exit_btn);

		// ArrayList 생성
		mMainTitleList = new ArrayList<EditText>();
		mSubTitleList = new ArrayList<EditText>();
		mContentList = new ArrayList<EditText>();
		mPhotoList = new ArrayList<ImageView>();

		// DB Manager 생성
		mDBHelper = new DBHelper(ModifyArticleActivity.this);
		mDBManager = new DBManager(mDBHelper);
		mDB = mDBHelper.getReadableDatabase();

		// Data 생성
		mNewspaperData = new NewspaperData();

	}

	/**
	 * 현재 날짜계산하여 mTodayDate 설정 및 mNewspaperData 저장
	 **/
	private void setTodayDate() {
		Calendar m = Calendar.getInstance();
		mTodayDate.setText(m.get(Calendar.YEAR) + "년 "
				+ (m.get(Calendar.MONTH) + 1) + "월 " + m.get(Calendar.DATE)
				+ "일");
		mNewspaperData.setDate(mTodayDate.getText().toString());
	}

	/**
	 * mNewspaperData에 EditText의 현재 String을 저장한ㄷ.
	 **/
	private void saveEditTextString() {
		// 현재 작성된 EditText 내용 저장
		for (int i = 0; i < mMainTitleList.size(); i++) {
			if (mMainTitleList.get(i) != null)
				mNewspaperData.getMainTitle().add(
						mMainTitleList.get(i).getText().toString());
			else
				mNewspaperData.getMainTitle().add(null);
		}
		for (int i = 0; i < mSubTitleList.size(); i++) {
			if (mSubTitleList.get(i) != null)
				mNewspaperData.getSubTitle().add(
						mSubTitleList.get(i).getText().toString());
			else
				mNewspaperData.getSubTitle().add(null);
		}
		for (int i = 0; i < mContentList.size(); i++) {
			if (mContentList.get(i) != null)
				mNewspaperData.getContent().add(
						mContentList.get(i).getText().toString());
			else
				mNewspaperData.getContent().add(null);
		}
	}

	/**
	 * View 클릭 리스너
	 **/
	private OnClickListener mViewClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v instanceof EditText) {
				EditText et = (EditText) v;
				et.setFocusableInTouchMode(true);
				et.setBackgroundColor(Color.TRANSPARENT);
				et.requestFocusFromTouch();
			} else if (v instanceof ImageView) {
				getImageGallery(v.getId());
			} else {
				switch (v.getId()) {
				case R.id.modify_btn:
					// ProgressDialog
					mWaitingDialog = ProgressDialog.show(
							ModifyArticleActivity.this, null, "잠시만 기다려주세요...");

					// Background & Focusing 없애기
					for (int i = 0; i < mMainTitleList.size(); i++) {
						EditText et = mMainTitleList.get(i);
						if (et != null) {
							et.setBackgroundColor(Color.TRANSPARENT);
							et.setCursorVisible(false);
						}
					}
					for (int i = 0; i < mSubTitleList.size(); i++) {
						EditText et = mSubTitleList.get(i);
						if (et != null) {
							et.setBackgroundColor(Color.TRANSPARENT);
							et.setCursorVisible(false);
						}
					}
					for (int i = 0; i < mContentList.size(); i++) {
						EditText et = mContentList.get(i);
						if (et != null) {
							et.setBackgroundColor(Color.TRANSPARENT);
							et.setCursorVisible(false);
						}
					}

					// 현재 EditText String 저장
					saveEditTextString();

					// DB 삽입 => 파일저장
					mDBManager.DBUpdateArticle("articles", mNewspaperData, mId,
							mDBUpdaterHandler);

					break;

				case R.id.article_exit_btn:
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							ModifyArticleActivity.this);
					dialog = new AlertDialog.Builder(ModifyArticleActivity.this);
					dialog.setMessage("저장하지 않을 시 수정한 내용이 반영되지 않습니다.")
							.setNegativeButton("취소", null)
							.setPositiveButton("확인",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											finish();
										}
									}).show();
					break;

				}
			}
		}
	};

	private Handler mDBUpdaterHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String resultFileLocation = msg.getData().getString("FileLocation");
			// Bitmap 저장
			SaveArticleBitmap saveArticle = new SaveArticleBitmap(
					mSaveArticleBitmapHandler, resultFileLocation,
					(View) mWriteArticleLayout);
			saveArticle.start();

		}
	};

	private Handler mSaveArticleBitmapHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				// Progress Dialog 해제
				mWaitingDialog.dismiss();
				// 액티비티 종료
				setResult(Activity.RESULT_OK);
				finish();
			}
		}
	};

}
