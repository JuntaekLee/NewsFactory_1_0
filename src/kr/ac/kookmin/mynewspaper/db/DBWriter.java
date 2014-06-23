package kr.ac.kookmin.mynewspaper.db;

import java.util.Calendar;

import kr.ac.kookmin.mynewspaper.NewspaperData;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * DB 스레드 : 작성한 기사를 DB에 쓰는 스레드
 **/
public class DBWriter extends Thread {

	// 로그
	private static final String TAG = "DBWriter";

	// 파일저장
	private String mSdPath;
	private Calendar mNow;

	// DB 헬퍼
	private DBHelper mDBHelper = null;

	// 핸들러
	private Handler mDBWriterHandler;

	// 컬럼값
	private String file_name = null;
	private NewspaperData mNewspaperData = null;
	// 테이블 이름
	private String table_name = null;

	public DBWriter(Handler mDBWriterHandler, DBHelper mDBHelper,
			String table_name, String mSdPath, NewspaperData newspaperData) {
		super();
		this.file_name = null;
		this.mNewspaperData = newspaperData;
		this.mDBWriterHandler = mDBWriterHandler;
		this.mDBHelper = mDBHelper;
		this.table_name = table_name;

		// SD card path 구하기
		this.mSdPath = mSdPath;
	}

	@Override
	public void run() {

		SQLiteDatabase db;
		Message msg = Message.obtain();
		msg.what = 0;

		// DB 생성
		db = mDBHelper.getWritableDatabase();

		// 파일 이름 구하기
		mNow = Calendar.getInstance();
		String file_location = mSdPath + "/MyNewspaper/Article/"
				+ mNow.getTimeInMillis() + ".jpg";

		// DB 삽입
		db.execSQL("INSERT INTO " + table_name + " VALUES (null, '"
				+ file_location + "', '" + file_name + "', "
				+ mNewspaperData.getColor() + ", " + mNewspaperData.getStyle()
				+ ", '" + mNewspaperData.getDate() + "', '"
				+ mNewspaperData.getMainTitle().get(0) + "', '"
				+ mNewspaperData.getSubTitle().get(0) + "', '"
				+ mNewspaperData.getSubTitle().get(1) + "', '"
				+ mNewspaperData.getSubTitle().get(2) + "', '"
				+ mNewspaperData.getContent().get(0) + "', '"
				+ mNewspaperData.getContent().get(1) + "', '"
				+ mNewspaperData.getContent().get(2) + "', '"
				+ mNewspaperData.getPhoto()[0] + "', '"
				+ mNewspaperData.getPhoto()[1] + "')");

		// DB 닫기
		db.close();

		// 파일 저장 요청하는 핸들러 부르기
		Bundle bundle = new Bundle();
		bundle.putString("FileLocation", file_location);
		msg.setData(bundle);
		mDBWriterHandler.sendMessage(msg);

	}
}
