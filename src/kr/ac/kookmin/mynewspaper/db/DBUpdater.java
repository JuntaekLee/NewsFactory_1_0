package kr.ac.kookmin.mynewspaper.db;

import kr.ac.kookmin.mynewspaper.NewspaperData;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * DB 스레드 : 기사를 수정하고 나서 수정된 기사를 DB에 업데이트하는 스레드
 **/
public class DBUpdater extends Thread {
	// 로그
	private static final String TAG = "DBUpdater";

	// DB 헬퍼
	private DBHelper mDBHelper = null;

	// SD Card
	private String mSdPath;

	// 핸들러
	private Handler mDBUpdaterHandler;

	// 컬럼값
	private String file_name;
	private NewspaperData mNewspaperData;

	// 테이블 이름
	private String table_name = null;

	// Id
	private long mId;

	public DBUpdater(Handler mDBUpdaterHandler, DBHelper mDBHelper,
			String table_name, String mSdPath, long mId,
			NewspaperData newspaperData) {
		super();
		this.mDBUpdaterHandler = mDBUpdaterHandler;
		this.mDBHelper = mDBHelper;
		this.file_name = null;
		this.mNewspaperData = newspaperData;
		this.table_name = table_name;
		this.mId = mId;
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

		// 파일 저장 위치 구하기 (수정)
		Cursor cursor = db.rawQuery(
				"SELECT file_location FROM articles WHERE _id=" + mId, null);
		cursor.moveToFirst();
		String file_location = cursor.getString(0);

		// DB 수정
		db.execSQL("UPDATE " + table_name + " SET file_location='"
				+ file_location + "', file_name='" + file_name + "', color="
				+ mNewspaperData.getColor() + ", style="
				+ mNewspaperData.getStyle() + ", date='"
				+ mNewspaperData.getDate() + "', main_title1='"
				+ mNewspaperData.getMainTitle().get(0) + "', sub_title1='"
				+ mNewspaperData.getSubTitle().get(0) + "', sub_title2='"
				+ mNewspaperData.getSubTitle().get(1) + "', sub_title3='"
				+ mNewspaperData.getSubTitle().get(2) + "', content1='"
				+ mNewspaperData.getContent().get(0) + "', content2='"
				+ mNewspaperData.getContent().get(1) + "', content3='"
				+ mNewspaperData.getContent().get(2) + "', main_photo1='"
				+ mNewspaperData.getPhoto()[0] + "', main_photo2='"
				+ mNewspaperData.getPhoto()[1] + "' WHERE _id=" + mId);

		// DB 닫기
		cursor.close();
		db.close();

		// 파일 저장 요청하는 핸들러 부르기
		Bundle bundle = new Bundle();
		bundle.putString("FileLocation", file_location);
		msg.setData(bundle);
		mDBUpdaterHandler.sendMessage(msg);

	}
}
