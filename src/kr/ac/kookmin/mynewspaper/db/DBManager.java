package kr.ac.kookmin.mynewspaper.db;

import java.util.ArrayList;

import kr.ac.kookmin.mynewspaper.NewspaperData;
import android.os.Environment;
import android.os.Handler;

/**
 * Ŭ���� : DB�� �����ϴ� ������ Ŭ����
 **/
public class DBManager {

	// SD card
	private String mSdPath;
	// DB ����
	DBHelper mDBHelper = null;

	// DB ������
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

		// SD card path ���ϱ�
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED))
			mSdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		else
			mSdPath = Environment.MEDIA_UNMOUNTED;
	}
	
	// ��� �б�(���� ��ġ�� �����ش�)
	public void DBReadArticle(long id, Handler DBReaderHandle, String table_name, int work){
		mDBReader = new DBReader(mDBHelper, DBReaderHandle, id, table_name, work);
		mDBReader.start();
	}

	// ��� ����
	public void DBDeleteArticles(ArrayList<Long> deleteIdList, String table_name,
			Handler DBDeleterHandler, int work) {
		mDBDelete = new DBDeleter(mDBHelper, deleteIdList, "articles",
				DBDeleterHandler, work);
		mDBDelete.start();
	}

	// ��� ����
	public void DBWriteArticle(String table_name, NewspaperData newspaperData,
			Handler mDBWriterHandler) {
		mDBWriter = new DBWriter(mDBWriterHandler, mDBHelper, table_name,
				mSdPath, newspaperData);
		mDBWriter.start();
	}

	// ��� ����
	public void DBUpdateArticle(String table_name, NewspaperData newspaperData,
			long mId, Handler mDBUpdaterHandler) {
		// DBWriter ����
		mDBUpdater = new DBUpdater(mDBUpdaterHandler, mDBHelper, table_name,
				mSdPath, mId, newspaperData);
		mDBUpdater.start();
	}

	// ��õ�� ��� DB�� �߰�
	public void DBWriteRecommend(String table_name, long news_id,
			Handler mDBWriterHandler) {
		mDBWriterRecommend = new DBWriterRecommend(mDBHelper, news_id,
				table_name);
		mDBWriterRecommend.start();
	}

	// ��õ�� ��� ������ Ȯ��
	public void DBReadRecommend(String table_name, long news_id,
			Handler mDBReaderHandler) {
		mDBReaderRecommend = new DBReaderRecommend(mDBHelper, mDBReaderHandler,
				news_id, table_name);
		mDBReaderRecommend.start();
	}

	// �� ���� ���� Ȯ��
	public void DBReadSumOfArticle(String table_name, Handler mDBReaderHandler) {
		mDBReaderSumOfArticle = new DBReaderSumOfArticle(mDBHelper,
				mDBReaderHandler, table_name);
		mDBReaderSumOfArticle.start();
	}

}
