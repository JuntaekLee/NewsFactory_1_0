package kr.ac.kookmin.mynewspaper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB클래스 : 안드로이드 DB Helper
 **/
public class DBHelper extends SQLiteOpenHelper {

	// 로그
	static final String TAG = "DBHelper";

	// DB 관련 변수
	private final static String DB_NAME = "MyNewspaper.db";
	private String table_name = "articles";

	private final static int DB_VER = 1;

	// 컬럼명
	private final String column0 = "file_location";
	private final String column1 = "file_name";
	private final String column2 = "color";
	private final String column3 = "style";
	private final String column4 = "date";
	private final String column5 = "main_title1";
	private final String column6 = "sub_title1";
	private final String column7 = "sub_title2";
	private final String column8 = "sub_title3";
	private final String column9 = "content1";
	private final String column10 = "content2";
	private final String column11 = "content3";
	private final String column12 = "main_photo1";
	private final String column13 = "main_photo2";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Articles 테이블 생성
		db.execSQL("CREATE TABLE " + table_name
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + column0
				+ " TEXT, " + column1 + " TEXT, " + column2 + " INT, "
				+ column3 + " INT, " + column4 + " TEXT, " + column5
				+ " TEXT, " + column6 + " TEXT, " + column7 + " TEXT, "
				+ column8 + " TEXT, " + column9 + " TEXT, " + column10
				+ " TEXT, " + column11 + " TEXT, " + column12 + " TEXT, "
				+ column13 + " TEXT)");

		db.execSQL("CREATE TABLE recommend_news (_id INTEGER PRIMARY KEY AUTOINCREMENT, news_id LONG)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
