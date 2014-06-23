package kr.ac.kookmin.mynewspaper.activity;

import java.io.File;
import java.util.ArrayList;

import kr.ac.kookmin.mynewspaper.R;
import kr.ac.kookmin.mynewspaper.db.DBDeleter;
import kr.ac.kookmin.mynewspaper.db.DBHelper;
import kr.ac.kookmin.mynewspaper.db.DBManager;
import kr.ac.kookmin.mynewspaper.db.DBReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * ��Ƽ��Ƽ : �� DB�� ����� ��縦 �д� ȭ��
 **/
public class ReadMyArticleActivity extends Activity {

	private static final String TAG = "ReadMyArticleActivity";

	// Request Code
	private static final int REQUEST_MODIFY_ARTICLE = 0;
	// ���ڵ� Id
	private long mId;

	// View
	private ImageView mMyArticleImage;
	private Button mModifyBtn;
	private Button mDeleteArticle;
	private Button mExitBtn;

	// DB
	private DBHelper mDBHelper;
	private DBManager mDBManager;

	// Bitmap
	private Bitmap mArticlePhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_my_article);

		// �ʱ�ȭ
		init();

		// ������ ����
		setListener();

		// �̹����� ����
		setImageView();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// BitmapCreator.recycleImageViewBitmap(mMyArticleImage);
		// BitmapCreator.checkBitmapRecycle(mArticlePhoto);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_MODIFY_ARTICLE) {
				Log.d(TAG, "ReadMyArticleActivity Notify�ϱ�");
				// �����Ǿ� ���ŵǾ��� �� �����Ƿ� �ٽ� �̹�����
				setImageView();

			}

		}
	}

	/**
	 * DB���� ������ġ �����ͼ� �̹����信 ����
	 **/
	private void setImageView() {
		mDBManager.DBReadArticle(mId, mDBReaderHandler, "articles",
				DBReader.FOR_READ_ARTICLE);
		/*
		 * Log.d(TAG, "DB ��û id : " + mId); Cursor cursor = mDB.rawQuery(
		 * "SELECT _id, file_location FROM articles WHERE _id=" + mId, null); if
		 * (cursor.moveToFirst()) { Log.d(TAG, "file_location �÷��� �ε��� : " +
		 * cursor.getColumnIndex("file_location")); String file_location =
		 * cursor.getString(1); Log.d(TAG, "���� ��ġ : " + file_location);
		 * mWaitingDialog = ProgressDialog.show(this, "�۾� ó����",
		 * "��ø� ��ٷ��ּ���..."); mArticlePhoto =
		 * BitmapFactory.decodeFile(file_location);
		 * mMyArticleImage.setImageBitmap(mArticlePhoto);
		 * 
		 * mWaitingDialog.dismiss(); } cursor.close();
		 */

	}

	private void setListener() {
		mModifyBtn.setOnClickListener(mViewClickListener);
		mDeleteArticle.setOnClickListener(mViewClickListener);
		mExitBtn.setOnClickListener(mViewClickListener);
	}

	/**
	 * ��Ƽ��Ƽ ������� �� �ʿ��� �ʱ�ȭ ����
	 **/
	private void init() {
		mId = getIntent().getExtras().getLong("Id");
		mMyArticleImage = (ImageView) findViewById(R.id.my_article_image);
		mModifyBtn = (Button) findViewById(R.id.modify_btn);
		mDeleteArticle = (Button) findViewById(R.id.delete_article_btn);
		mExitBtn = (Button) findViewById(R.id.exit_btn);
		mDBHelper = new DBHelper(ReadMyArticleActivity.this);

		mDBManager = new DBManager(mDBHelper);

	}

	private Handler mDBReaderHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String file_location = (String) msg.obj;
			if (msg.what == 1) {
				Log.d(TAG, "���� ��ġ : " + file_location);
				mArticlePhoto = BitmapFactory.decodeFile(file_location);
				mMyArticleImage.setImageBitmap(mArticlePhoto);
			} else if (msg.what == 0) {
				// SDī�忡 ���� ����
				File file = new File(file_location);
				if (file.exists())
					file.delete();
				// DB ���� ȣ��
				ArrayList<Long> deleteIdList = new ArrayList<Long>();
				deleteIdList.add(new Long(mId));
				mDBManager.DBDeleteArticles(deleteIdList, "articles",
						mDBDeleterHandler, DBDeleter.REQUEST_YES_CLOSE);
			}
		}

	};
	private Handler mDBDeleterHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Intent intent = new Intent(ReadMyArticleActivity.this,
					SelectMyArticleActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

	};

	/**
	 * View Ŭ�� ������
	 **/
	private OnClickListener mViewClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.modify_btn:
				Intent intent = new Intent(ReadMyArticleActivity.this,
						ModifyArticleActivity.class);
				intent.putExtra("Id", mId);
				startActivityForResult(intent, REQUEST_MODIFY_ARTICLE);
				break;
			case R.id.exit_btn:
				finish();
				break;
			case R.id.delete_article_btn:
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						ReadMyArticleActivity.this);
				dialog.setMessage("�����Ͻðڽ��ϱ�?")
						.setNegativeButton("���", null)
						.setPositiveButton("Ȯ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// DB Read ȣ���� ��籸�Ѵ��� �ڵ鷯�� ���� ��������
										mDBManager.DBReadArticle(mId,
												mDBReaderHandler, "articles",
												DBReader.FOR_DELETE_ARTICLE);
										/*
										 * // SDī�忡 ���� ���� String file_location =
										 * null; mDBManager.DBReadArticle(mId,
										 * mDBReaderHandler, "articles"); Cursor
										 * cursor = mDB.rawQuery(
										 * "SELECT file_location FROM articles WHERE _id = "
										 * + mId, null); if
										 * (cursor.moveToNext()) { file_location
										 * = cursor.getString(0); } File file =
										 * new File(file_location); if
										 * (file.exists()) file.delete();
										 */

										/*
										 * // DB���� ���� mDB.execSQL(
										 * "DELETE FROM articles WHERE _id = " +
										 * mId); Intent intent = new Intent(
										 * ReadMyArticleActivity.this,
										 * SelectMyArticleActivity.class);
										 * intent
										 * .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										 * ); startActivity(intent);
										 */
									}
								}).show();

				break;
			}
		}

	};

}
