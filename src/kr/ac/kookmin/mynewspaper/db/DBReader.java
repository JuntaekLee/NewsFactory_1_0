package kr.ac.kookmin.mynewspaper.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * DB 스레드 : 작성한 기사를 DB에 쓰는 스레드
 **/
public class DBReader extends Thread {

	// 로그
	private static final String TAG = "DBReader";
	
	public static final int FOR_DELETE_ARTICLE = 0;
	public static final int FOR_READ_ARTICLE = 1;

	private int mWork = -1;
	private DBHelper mDBHelper;
	private Handler mDBReaderHandler;
	private long mId;
	private String table_name;
	


	public DBReader(DBHelper mDBHelper, Handler mDBReaderHandler, long mId,
			String table_name, int work) {
		super();
		this.mDBHelper = mDBHelper;
		this.mDBReaderHandler = mDBReaderHandler;
		this.mId = mId;
		this.table_name = table_name;
		this.mWork = work;
	}



	@Override
	public void run() {

		SQLiteDatabase db;
		Message msg = Message.obtain();
		if(mWork == FOR_DELETE_ARTICLE)
			msg.what = 0;
		else if(mWork == FOR_READ_ARTICLE)
			msg.what = 1;

		// DB 생성
		db = mDBHelper.getReadableDatabase();

		
		Cursor cursor = db.rawQuery(
				"SELECT _id, file_location FROM " + table_name + " WHERE _id=" + mId,
				null);
		if (cursor.moveToFirst()) {
			Log.d(TAG,
					"file_location 컬럼의 인덱스 : "
							+ cursor.getColumnIndex("file_location"));
			String file_location = cursor.getString(1);
			msg.obj = file_location;
			mDBReaderHandler.sendMessage(msg);
		}
		
		
		// DB 닫기
		cursor.close();
		db.close();

	}
}
