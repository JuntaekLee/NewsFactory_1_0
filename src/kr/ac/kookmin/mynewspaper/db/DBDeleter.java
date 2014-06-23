package kr.ac.kookmin.mynewspaper.db;

import java.io.File;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * DB 스레드 : 작성한 기사를 DB에 쓰는 스레드
 **/
public class DBDeleter extends Thread {

	// 로그
	private static final String TAG = "DBDeleter";

	public static final int REQUEST_NO_CLOSE = 0;
	public static final int REQUEST_YES_CLOSE = 1;
	
	private DBHelper mDBHelper;
	private ArrayList<Long> mDeleteIdList;
	private String table_name;
	private Handler mDBDeleterHandler;
	private int mWork;
	
	

	public DBDeleter(DBHelper mDBHelper, ArrayList<Long> mDeleteIdList,
			String table_name, Handler mDBDeleterHandler, int work) {
		super();
		this.mDBHelper = mDBHelper;
		this.mDeleteIdList = mDeleteIdList;
		this.table_name = table_name;
		this.mDBDeleterHandler = mDBDeleterHandler;
		this.mWork = work;
	}



	@Override
	public void run() {

		SQLiteDatabase db;


		// DB 생성
		db = mDBHelper.getWritableDatabase();
		
		// DB에서 선택된 기사 삭제
		Cursor cursor;
		for (int i = 0; i < mDeleteIdList.size(); i++) {
			// SD카드에 파일 삭제
			String file_location = null;
			cursor = db.rawQuery(
					"SELECT file_location FROM " + table_name + " WHERE _id = "
							+ mDeleteIdList.get(i), null);
			if (cursor.moveToNext()) {
				file_location = cursor.getString(0);
			}
			File file = new File(file_location);
			if (file.exists())
				file.delete();

			// DB에서 삭제
			db.execSQL("DELETE from articles where _id = "
					+ mDeleteIdList.get(i));
			Log.d(TAG, "삭제한 id : " + mDeleteIdList.get(i));
			cursor.close();
		}
	

		// DB 닫기
	
		if(mWork == REQUEST_YES_CLOSE)
			db.close();
		
		mDBDeleterHandler.sendEmptyMessage(0);

	}
}
