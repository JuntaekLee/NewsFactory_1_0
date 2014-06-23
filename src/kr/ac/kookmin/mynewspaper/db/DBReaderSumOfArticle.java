package kr.ac.kookmin.mynewspaper.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * DB 스레드 : 현재 DB에 존재하는 전체 기사 수 구하는 스레드
 **/
public class DBReaderSumOfArticle extends Thread {

	// 로그
	private static final String TAG = "DBReaderRecommend";

	// DB 헬퍼
	private DBHelper mDBHelper = null;

	// 테이블 이름
	private String table_name = null;

	// 핸들러
	private Handler mDBReaderHandler;

	public DBReaderSumOfArticle(DBHelper mDBHelper, Handler mDBReaderHandler,
			String table_name) {
		super();
		this.mDBHelper = mDBHelper;
		this.table_name = table_name;
		this.mDBReaderHandler = mDBReaderHandler;
	}

	@Override
	public void run() {

		SQLiteDatabase db;
		long sumOfArticle = 0L;

		Message msg = Message.obtain();
		msg.what = 0;

		// DB 생성
		db = mDBHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery("select count(_id) from " + table_name,
				null);
		cursor.moveToFirst();
		sumOfArticle = cursor.getLong(0);

		Bundle bundle = new Bundle();
		bundle.putLong("SumOfArticle", sumOfArticle);
		msg.setData(bundle);
		mDBReaderHandler.sendMessage(msg);

		Log.d(TAG, "커서닫기전");
		// DB 닫기
		cursor.close();
		
		Log.d(TAG, "커서닫기 후");
		db.close();

	}
}
