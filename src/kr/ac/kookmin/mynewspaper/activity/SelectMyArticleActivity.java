package kr.ac.kookmin.mynewspaper.activity;

import java.util.ArrayList;

import kr.ac.kookmin.mynewspaper.BitmapCreator;
import kr.ac.kookmin.mynewspaper.ClientNetworkThread;
import kr.ac.kookmin.mynewspaper.MyArticleCursorAdapter;
import kr.ac.kookmin.mynewspaper.R;
import kr.ac.kookmin.mynewspaper.db.DBDeleter;
import kr.ac.kookmin.mynewspaper.db.DBHelper;
import kr.ac.kookmin.mynewspaper.db.DBManager;
import kr.ac.kookmin.mynewspaper.protocol.RequestData;
import kr.ac.kookmin.mynewspaper.protocol.RequestInsertArticle;
import kr.ac.kookmin.mynewspaper.protocol.ResponseData;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

/**
 * ��Ƽ��Ƽ : DB�� �ִ� �� ������ ����Ʈ�� �����ְ� �����ϴ� ȭ��
 **/
public class SelectMyArticleActivity extends Activity {

	private static final String TAG = "SelectMyArticleActivity";

	// �۾� ����
	public static final int READ = 0;
	public static final int DELETE = 1;
	public static final int UPLOAD = 2;
	public static final int FACEBOOK = 3;

	// ��Ƽ��Ƽ ���� �۾� ����
	private int mWork = 0;

	// View
	private GridView mMyArticleGrid;
	private CheckBox mDeleteArticleBtn;
	private Button mDeleteOkBtn;
	// DisplayMetric
	private DisplayMetrics mDm;
	// Adapter
	private MyArticleCursorAdapter mCursorAdapter;
	// DB
	private SQLiteDatabase mDB;
	private DBHelper mDBHelper;
	private DBManager mDBManager;

	// Upload Data
	private String mUserName;
	private Bitmap mArticleBitmap;
	private Bitmap mMainPhoto1;
	private String mMainTitle;
	private int mCurrentCategory;

	// ProgressDialog
	private ProgressDialog mWaitDialog;
	// AlertDialog Builder
	private AlertDialog.Builder mAlertDialogBuilder;

	// Delete List
	private ArrayList<Long> mDeleteIdList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_my_article);

		// Get Extras
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mWork = bundle.getInt("Work");
			mUserName = bundle.getString("UserName", "");
			mCurrentCategory = bundle.getInt("Category");
		}

		// �ʱ�ȭ
		init();

		// Work �� Upload & Facebook �Ͻÿ� ������ư �Ⱥ��̰�
		if (mWork == FACEBOOK || mWork == UPLOAD)
			mDeleteArticleBtn.setVisibility(View.INVISIBLE);

		// ����� ����
		setAdapter();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// BitmapCreator.checkBitmapRecycle(mArticleBitmap);
		// BitmapCreator.checkBitmapRecycle(mMainPhoto1);
		// DB �ݱ�
		//mCursor.close();
		mDB.close();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCursorAdapter != null)
			mCursorAdapter.notifyDataSetChanged();
	}

	/**
	 * ��Ƽ��Ƽ ������� �� �ʿ��� �ʱ�ȭ ����
	 **/
	private void init() {

		// View ��üȭ
		mMyArticleGrid = (GridView) findViewById(R.id.my_article_grid);
		mDeleteArticleBtn = (CheckBox) findViewById(R.id.delete_article_btn);
		mDeleteOkBtn = (Button) findViewById(R.id.delete_ok_btn);
		// Helper ����
		mDBHelper = new DBHelper(SelectMyArticleActivity.this);

		// ������ ���
		mMyArticleGrid.setOnItemClickListener(mItemClickListener);
		mDeleteArticleBtn.setOnCheckedChangeListener(mCheckedChangeListener);
		mDeleteOkBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteArticle();
				mDeleteOkBtn.setVisibility(View.INVISIBLE);
			}
		});

		// DB ����
		mDBManager = new DBManager(mDBHelper);
		mDB = mDBHelper.getReadableDatabase();

		// mDm �����
		mDm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDm);

		// Builer �����
		mAlertDialogBuilder = new AlertDialog.Builder(
				SelectMyArticleActivity.this);

		// mDeleteIdList �����
		mDeleteIdList = new ArrayList<Long>();

	}

	public void deleteArticle() {
		mDBManager.DBDeleteArticles(mDeleteIdList, "articles", mDBDeleterHandler, DBDeleter.REQUEST_NO_CLOSE);
		
/*		// DB���� ���õ� ��� ����
		Cursor cursor;
		for (int i = 0; i < mDeleteIdList.size(); i++) {
			// SDī�忡 ���� ����
			String file_location = null;
			cursor = mDB.rawQuery(
					"SELECT file_location FROM articles WHERE _id = "
							+ mDeleteIdList.get(i), null);
			if (cursor.moveToNext()) {
				file_location = cursor.getString(0);
			}
			File file = new File(file_location);
			if (file.exists())
				file.delete();

			// DB���� ����
			mDB.execSQL("DELETE from articles where _id = "
					+ mDeleteIdList.get(i));
			Log.d(TAG, "������ id : " + mDeleteIdList.get(i));
		}*/

/*		// üũ���� Ǯ�� ȭ�� �ٽ� ����
		mDeleteArticleBtn.setChecked(false);
		mCursor = mDB.rawQuery(
				"SELECT _id, file_location, main_title1, date FROM articles",
				null);
		mCursorAdapter.changeCursor(mCursor);*/
	}

	/**
	 * CursorAdapter ����
	 **/
	private void setAdapter() {
		// Ŀ�� ����
		Cursor cursor;
		cursor = mDB.rawQuery(
				"SELECT _id, file_location, main_title1, date FROM articles",
				null);
		// ����� ����
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCursorAdapter = new MyArticleCursorAdapter(
				SelectMyArticleActivity.this, cursor, false, inflater, mDm);
		mMyArticleGrid.setAdapter(mCursorAdapter);

	}

	private Handler mDBDeleterHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// üũ���� Ǯ�� ȭ�� �ٽ� ����
			Cursor cursor;
			mDeleteArticleBtn.setChecked(false);
			cursor = mDB.rawQuery(
					"SELECT _id, file_location, main_title1, date FROM articles",
					null);
			mCursorAdapter.changeCursor(cursor);
		}
		
	};
	
	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (buttonView.getId() == R.id.delete_article_btn) {
				if (isChecked == true) {
					mAlertDialogBuilder.setMessage("������ ��縦 �������ּ���")
							.setPositiveButton("Ȯ��", null).show();
					mDeleteOkBtn.setVisibility(View.VISIBLE);
					mWork = DELETE;
				} else {

					// / Ŭ������ ���� ��Ҹ����� ����üũ�ڽ��� ��ü�ϸ� View ������ ���� ������ ���ؾߵǴµ� �Ⱥ��Ѵ�.

					// ������ư ���ֱ�
					mDeleteOkBtn.setVisibility(View.INVISIBLE);
					mWork = READ;
				}
			}
		}
	};

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent;
			Log.d(TAG, "long id �� ��  : " + id);
			Log.d(TAG, "position �� ��  : " + position);
			if (mWork == READ) {
				intent = new Intent(SelectMyArticleActivity.this,
						ReadMyArticleActivity.class);
				intent.putExtra("Id", id);
				startActivity(intent);
			} else if (mWork == DELETE) {
				// �̹� ���õ� ������� Ȯ��
				boolean isExist = false;
				for (int i = 0; i < mDeleteIdList.size(); i++) {
					if (mDeleteIdList.get(i) == id) {
						isExist = true;
						mDeleteIdList.remove(i);
						break;
					}
				}
				// ������ ��� DB id ����
				if (isExist == false) {
					view.setBackgroundResource(R.drawable.selected_article_border);
					mDeleteIdList.add(new Long(id));
				} else {
					view.setBackgroundColor(Color.TRANSPARENT);
				}

			}

			else if (mWork == FACEBOOK) {
				intent = new Intent(SelectMyArticleActivity.this,
						FaceBookUploadActivity.class);
				intent.putExtra("Id", id);
				startActivity(intent);
			}

			else if (mWork == UPLOAD) {

				// ���̾�α� ����
				mWaitDialog = ProgressDialog.show(SelectMyArticleActivity.this,
						"Wait", "��縦 �ø��� �� �Դϴ�...",true, true);

				// Ŀ�� ����
				Cursor cursor;
				cursor = mDB.rawQuery(
						"SELECT file_location, main_title1, main_photo1 FROM articles WHERE _id = "
								+ id, null);
				cursor.moveToFirst();

				String file_location = cursor.getString(0);
				String main_photo1 = cursor.getString(2);
				Log.d(TAG, "main_photo1 �� �� : " + main_photo1);
				int width = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 100, mDm);
				int height = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 70, mDm);
				mArticleBitmap = BitmapFactory.decodeFile(file_location);
				mMainTitle = cursor.getString(1);

				Log.d(TAG, "main_photo1.length() : " + main_photo1.length());
				if (main_photo1.equals("null")) {
					Log.d(TAG, "main_photo1 == null");
					mMainPhoto1 = null;
				} else {
					mMainPhoto1 = BitmapCreator.createScaledBitmap(
							Uri.parse(main_photo1), width, height,
							getContentResolver());
				}

				cursor.close();

				byte[] article = BitmapCreator
						.getByteArrayFromBitmap(mArticleBitmap);
				byte[] preview = BitmapCreator
						.getByteArrayFromBitmap(mMainPhoto1);

				// ������ ��� Insert ��û�ϱ�
				Log.d(TAG, "// ������ ��� Insert ��û�ϱ�");
				RequestData requestData = new RequestInsertArticle(
						RequestData.INSERT_ARTICLE, article, preview,
						mMainTitle, mCurrentCategory, mUserName);
				ClientNetworkThread request = new ClientNetworkThread(
						MainActivity.HOST_ADDRESS, requestData,
						mClientNetworkHandler);
				request.setDaemon(true);
				request.start();

			}
		}
	};

	private Handler mClientNetworkHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ResponseData responseData = (ResponseData) msg.obj;
			switch (responseData.getRequestCode()) {
			case RequestData.INSERT_ARTICLE:
				Log.d(TAG, "mClientNetworkHandler INSERT_ARTICLE ����");
				if (responseData.getResponseCode() == ResponseData.RESULT_OK) {
					mWaitDialog.dismiss();
					setResult(Activity.RESULT_OK);
					finish();
				}
				break;

			}
		}
	};

}
