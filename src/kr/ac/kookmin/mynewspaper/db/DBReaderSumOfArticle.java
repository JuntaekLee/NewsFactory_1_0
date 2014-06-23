package kr.ac.kookmin.mynewspaper.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * DB ������ : ���� DB�� �����ϴ� ��ü ��� �� ���ϴ� ������
 **/
public class DBReaderSumOfArticle extends Thread {

	// �α�
	private static final String TAG = "DBReaderRecommend";

	// DB ����
	private DBHelper mDBHelper = null;

	// ���̺� �̸�
	private String table_name = null;

	// �ڵ鷯
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

		// DB ����
		db = mDBHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery("select count(_id) from " + table_name,
				null);
		cursor.moveToFirst();
		sumOfArticle = cursor.getLong(0);

		Bundle bundle = new Bundle();
		bundle.putLong("SumOfArticle", sumOfArticle);
		msg.setData(bundle);
		mDBReaderHandler.sendMessage(msg);

		Log.d(TAG, "Ŀ���ݱ���");
		// DB �ݱ�
		cursor.close();
		
		Log.d(TAG, "Ŀ���ݱ� ��");
		db.close();

	}
}
