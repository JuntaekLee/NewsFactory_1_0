package kr.ac.kookmin.mynewspaper.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DB 스레드 : 다른 기사 추천 시 DB 추천 목록에 삽입하는 스레드
 **/
public class DBWriterRecommend extends Thread {

	// 로그
	private static final String TAG = "DBWriterRecommend";

	// DB 헬퍼
	private DBHelper mDBHelper = null;

	// 컬럼값
	private long news_id = -1L;

	// 테이블 이름
	private String table_name = null;

	public DBWriterRecommend(DBHelper mDBHelper, long news_id, String table_name) {
		super();
		this.mDBHelper = mDBHelper;
		this.news_id = news_id;
		this.table_name = table_name;
	}

	@Override
	public void run() {

		SQLiteDatabase db;

		// DB 생성
		db = mDBHelper.getWritableDatabase();

		// DB 삽입
		Log.d(TAG, "DBWriterRecommend 삽입");
		db.execSQL("INSERT INTO " + table_name + " VALUES (null, " + news_id
				+ ")");

		// DB 닫기
		db.close();

	}
}
