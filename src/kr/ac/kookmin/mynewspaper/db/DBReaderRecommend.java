package kr.ac.kookmin.mynewspaper.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * DB 스레드 : 중복 추천 처리를 하는 DB Reader 스레드
 **/
public class DBReaderRecommend extends Thread {

	// 로그
	private static final String TAG = "DBReaderRecommend";

	// DB 헬퍼
	private DBHelper mDBHelper = null;

	// 컬럼값
	private long news_id = -1L;

	// 테이블 이름
	private String table_name = null;

	// 핸들러
	private Handler mDBReaderHandler;

	public DBReaderRecommend(DBHelper mDBHelper, Handler mDBReaderHandler,
			long news_id, String table_name) {
		super();
		this.mDBHelper = mDBHelper;
		this.news_id = news_id;
		this.table_name = table_name;
		this.mDBReaderHandler = mDBReaderHandler;
	}

	@Override
	public void run() {

		SQLiteDatabase db;
		boolean isDuplicate = false;

		Message msg = Message.obtain();
		msg.what = 0;

		// DB 생성
		db = mDBHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select count(_id) from " + table_name
				+ " where news_id = " + news_id, null);
		if (cursor.moveToNext()) {
			if (cursor.getLong(0) > 0) {
				Log.d(TAG, "Recommend가 중복된느 갯수는 " + cursor.getCount());
				isDuplicate = true;
			}
		}

		Bundle bundle = new Bundle();
		bundle.putBoolean("Duplicate", isDuplicate);
		msg.setData(bundle);
		mDBReaderHandler.sendMessage(msg);

		// DB 닫기
		cursor.close();
		db.close();

	}
}
