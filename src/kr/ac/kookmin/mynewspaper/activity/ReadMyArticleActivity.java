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
 * 액티비티 : 내 DB에 저장된 기사를 읽는 화면
 **/
public class ReadMyArticleActivity extends Activity {

	private static final String TAG = "ReadMyArticleActivity";

	// Request Code
	private static final int REQUEST_MODIFY_ARTICLE = 0;
	// 레코드 Id
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

		// 초기화
		init();

		// 리스너 설정
		setListener();

		// 이미지뷰 갱신
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
				Log.d(TAG, "ReadMyArticleActivity Notify하기");
				// 수정되어 갱신되었을 수 있으므로 다시 이미지뷰
				setImageView();

			}

		}
	}

	/**
	 * DB에서 파일위치 가져와서 이미지뷰에 저장
	 **/
	private void setImageView() {
		mDBManager.DBReadArticle(mId, mDBReaderHandler, "articles",
				DBReader.FOR_READ_ARTICLE);
		/*
		 * Log.d(TAG, "DB 요청 id : " + mId); Cursor cursor = mDB.rawQuery(
		 * "SELECT _id, file_location FROM articles WHERE _id=" + mId, null); if
		 * (cursor.moveToFirst()) { Log.d(TAG, "file_location 컬럼의 인덱스 : " +
		 * cursor.getColumnIndex("file_location")); String file_location =
		 * cursor.getString(1); Log.d(TAG, "파일 위치 : " + file_location);
		 * mWaitingDialog = ProgressDialog.show(this, "작업 처리중",
		 * "잠시만 기다려주세요..."); mArticlePhoto =
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
	 * 액티비티 만들어질 때 필요한 초기화 수행
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
				Log.d(TAG, "파일 위치 : " + file_location);
				mArticlePhoto = BitmapFactory.decodeFile(file_location);
				mMyArticleImage.setImageBitmap(mArticlePhoto);
			} else if (msg.what == 0) {
				// SD카드에 파일 삭제
				File file = new File(file_location);
				if (file.exists())
					file.delete();
				// DB 삭제 호출
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
	 * View 클릭 리스너
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
				dialog.setMessage("삭제하시겠습니까?")
						.setNegativeButton("취소", null)
						.setPositiveButton("확인",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// DB Read 호출후 기사구한다음 핸들러를 통해 삭제진행
										mDBManager.DBReadArticle(mId,
												mDBReaderHandler, "articles",
												DBReader.FOR_DELETE_ARTICLE);
										/*
										 * // SD카드에 파일 삭제 String file_location =
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
										 * // DB에서 삭제 mDB.execSQL(
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
