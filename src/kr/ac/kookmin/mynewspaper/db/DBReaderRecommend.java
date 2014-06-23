package kr.ac.kookmin.mynewspaper.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * DB ������ : �ߺ� ��õ ó���� �ϴ� DB Reader ������
 **/
public class DBReaderRecommend extends Thread {

	// �α�
	private static final String TAG = "DBReaderRecommend";

	// DB ����
	private DBHelper mDBHelper = null;

	// �÷���
	private long news_id = -1L;

	// ���̺� �̸�
	private String table_name = null;

	// �ڵ鷯
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

		// DB ����
		db = mDBHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select count(_id) from " + table_name
				+ " where news_id = " + news_id, null);
		if (cursor.moveToNext()) {
			if (cursor.getLong(0) > 0) {
				Log.d(TAG, "Recommend�� �ߺ��ȴ� ������ " + cursor.getCount());
				isDuplicate = true;
			}
		}

		Bundle bundle = new Bundle();
		bundle.putBoolean("Duplicate", isDuplicate);
		msg.setData(bundle);
		mDBReaderHandler.sendMessage(msg);

		// DB �ݱ�
		cursor.close();
		db.close();

	}
}
