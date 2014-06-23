package kr.ac.kookmin.mynewspaper.db;

import java.util.ArrayList;

import kr.ac.kookmin.mynewspaper.NewspaperData;
import android.os.Environment;
import android.os.Handler;

/**
 * 클래스 : DB를 제어하는 관리자 클래스
 **/
public class DBManager {

	// SD card
	private String mSdPath;
	// DB 헬퍼
	DBHelper mDBHelper = null;

	// DB 스레드
	private DBWriter mDBWriter = null;
	private DBUpdater mDBUpdater = null;
	private DBReader mDBReader = null;
	private DBDeleter mDBDelete = null;
	private DBWriterRecommend mDBWriterRecommend = null;
	private DBReaderRecommend mDBReaderRecommend = null;
	private DBReaderSumOfArticle mDBReaderSumOfArticle = null;

	public DBManager(DBHelper mDBHelper) {
		super();
		this.mDBHelper = mDBHelper;

		// SD card path 구하기
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED))
			mSdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		else
			mSdPath = Environment.MEDIA_UNMOUNTED;
	}
	
	// 기사 읽기(파일 위치를 구해준다)
	public void DBReadArticle(long id, Handler DBReaderHandle, String table_name, int work){
		mDBReader = new DBReader(mDBHelper, DBReaderHandle, id, table_name, work);
		mDBReader.start();
	}

	// 기사 삭제
	public void DBDeleteArticles(ArrayList<Long> deleteIdList, String table_name,
			Handler DBDeleterHandler, int work) {
		mDBDelete = new DBDeleter(mDBHelper, deleteIdList, "articles",
				DBDeleterHandler, work);
		mDBDelete.start();
	}

	// 기사 삽입
	public void DBWriteArticle(String table_name, NewspaperData newspaperData,
			Handler mDBWriterHandler) {
		mDBWriter = new DBWriter(mDBWriterHandler, mDBHelper, table_name,
				mSdPath, newspaperData);
		mDBWriter.start();
	}

	// 기사 수정
	public void DBUpdateArticle(String table_name, NewspaperData newspaperData,
			long mId, Handler mDBUpdaterHandler) {
		// DBWriter 생성
		mDBUpdater = new DBUpdater(mDBUpdaterHandler, mDBHelper, table_name,
				mSdPath, mId, newspaperData);
		mDBUpdater.start();
	}

	// 추천한 기사 DB에 추가
	public void DBWriteRecommend(String table_name, long news_id,
			Handler mDBWriterHandler) {
		mDBWriterRecommend = new DBWriterRecommend(mDBHelper, news_id,
				table_name);
		mDBWriterRecommend.start();
	}

	// 추천한 기사 였는지 확인
	public void DBReadRecommend(String table_name, long news_id,
			Handler mDBReaderHandler) {
		mDBReaderRecommend = new DBReaderRecommend(mDBHelper, mDBReaderHandler,
				news_id, table_name);
		mDBReaderRecommend.start();
	}

	// 총 보유 기사수 확인
	public void DBReadSumOfArticle(String table_name, Handler mDBReaderHandler) {
		mDBReaderSumOfArticle = new DBReaderSumOfArticle(mDBHelper,
				mDBReaderHandler, table_name);
		mDBReaderSumOfArticle.start();
	}

}
