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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ��Ƽ��Ƽ : ��縦 �ۼ��ϰ� �����ϴ� ȭ��
 **/
public class WriteArticleActivity extends Activity {

	private static final String TAG = "WriteArticleActivity";

	// Request Code
	private static final int REQUEST_MAIN_PHOTO_1 = 0;
	private static final int REQUEST_MAIN_PHOTO_2 = 1;

	// View
	private LinearLayout mWriteArticleLayout;
	private TextView mTodayDate;
	private Button mSaveArticleBtn;
	private Button mWriteArticleExitBtn;

	// Newspapar Layout
	private ArrayList<EditText> mMainTitleList;
	private ArrayList<EditText> mSubTitleList;
	private ArrayList<EditText> mContentList;
	private ArrayList<ImageView> mPhotoList;

	// DB
	private DBManager mDBManager;
	private DBHelper mDBHelper;

	// Newspaper Data
	private NewspaperData mNewspaperData;

	// Waiting Progress
	private ProgressDialog mWaitingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_article);

		// �ʱ�ȭ
		init();

		// ��¥ ����
		setTodayDate();

		// ��� ����
		setLayoutBackground();

		// ���� �����ϴ� View ���ؼ� List�� �߰�
		setExistingView();

		// ������ ���
		setOnClickListener();

	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				WriteArticleActivity.this);
		dialog.setMessage("�������� ���� �� ��� ������ �����˴ϴ�.")
				.setNegativeButton("���", null)
				.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// BitmapCreator.recycleImageViewBitmap(mMainPhoto1);
		// BitmapCreator.recycleImageViewBitmap(mMainPhoto2);
		// BitmapCreator.checkBitmapRecycle(mPhotoBitmap);

	}

	/**
	 * ���������� Photo ���� �� ȣ��ȴ�. ImageView�� ������ �̹����� �ø���.
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
	 * �����ϴ� View�� ClickListener ���
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

		mSaveArticleBtn.setOnClickListener(mViewClickListener);
		mWriteArticleExitBtn.setOnClickListener(mViewClickListener);

	}

	/**
	 * �Լ� : getImageGallery /// �������� ȣ���Ѵ�.
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
	 * ���� �����ϴ� View ��üȭ�Ѵ�.
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
	 * ���޹��� Color, Style�� ������� ����
	 **/
	private void setLayoutBackground() {
		Bundle bundle = getIntent().getExtras();
		mNewspaperData.setStyle(bundle.getInt("PaperStyle"));
		mNewspaperData.setColor(bundle.getInt("PaperColor"));
		int layoutId = 0;

		// Color ����
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
		// Style����
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
	 * ��Ƽ��Ƽ ������� �� �ʿ��� �ʱ�ȭ ����
	 **/
	private void init() {
		// View ��üȭ
		mWriteArticleLayout = (LinearLayout) findViewById(R.id.wrtie_article_root_layout);
		mTodayDate = (TextView) findViewById(R.id.today_date);
		mSaveArticleBtn = (Button) findViewById(R.id.save_article_btn);
		mWriteArticleExitBtn = (Button) findViewById(R.id.write_article_exit_btn);

		// ArrayList ����
		mMainTitleList = new ArrayList<EditText>();
		mSubTitleList = new ArrayList<EditText>();
		mContentList = new ArrayList<EditText>();
		mPhotoList = new ArrayList<ImageView>();

		// DB Manager ����
		mDBHelper = new DBHelper(WriteArticleActivity.this);
		mDBManager = new DBManager(mDBHelper);

		// Data ����
		mNewspaperData = new NewspaperData();

	}

	/**
	 * ���� ��¥����Ͽ� mTodayDate ���� �� mNewspaperData ����
	 **/
	private void setTodayDate() {
		Calendar m = Calendar.getInstance();
		mTodayDate.setText(m.get(Calendar.YEAR) + "�� "
				+ (m.get(Calendar.MONTH) + 1) + "�� " + m.get(Calendar.DATE)
				+ "��");
		mNewspaperData.setDate(mTodayDate.getText().toString());
	}

	/**
	 * mNewspaperData�� EditText�� ���� String�� �����Ѥ�.
	 **/
	private void saveEditTextString() {
		// ���� �ۼ��� EditText ���� ����
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
	 * View Ŭ�� ������
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
				case R.id.save_article_btn:
					// ProgressDialog
					mWaitingDialog = ProgressDialog.show(
							WriteArticleActivity.this, null, "��ø� ��ٷ��ּ���...");

					// Background & Focusing ���ֱ�
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

					// ���� EditText String ����
					saveEditTextString();

					// DB ���� => ��������
					mDBManager.DBWriteArticle("articles", mNewspaperData, mDBWriterHandler);

					break;

				case R.id.write_article_exit_btn:
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							WriteArticleActivity.this);
					dialog.setMessage("�������� ���� �� ��� ������ �����˴ϴ�.")
							.setNegativeButton("���", null)
							.setPositiveButton("Ȯ��",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											finish();
										}
									}).show();
				}
			}
		}
	};

	private Handler mDBWriterHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String resultFileLocation = msg.getData().getString("FileLocation");
			// Bitmap ����
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
				// Progress Dialog ����
				mWaitingDialog.dismiss();

				// ��Ƽ��Ƽ ����
				Intent intent = new Intent(WriteArticleActivity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
	};

}