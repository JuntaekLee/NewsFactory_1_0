package kr.ac.kookmin.mynewspaper.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DB ������ : �ٸ� ��� ��õ �� DB ��õ ��Ͽ� �����ϴ� ������
 **/
public class DBWriterRecommend extends Thread {

	// �α�
	private static final String TAG = "DBWriterRecommend";

	// DB ����
	private DBHelper mDBHelper = null;

	// �÷���
	private long news_id = -1L;

	// ���̺� �̸�
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

		// DB ����
		db = mDBHelper.getWritableDatabase();

		// DB ����
		Log.d(TAG, "DBWriterRecommend ����");
		db.execSQL("INSERT INTO " + table_name + " VALUES (null, " + news_id
				+ ")");

		// DB �ݱ�
		db.close();

	}
}
