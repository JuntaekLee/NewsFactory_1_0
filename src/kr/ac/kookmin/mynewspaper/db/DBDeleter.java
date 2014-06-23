package kr.ac.kookmin.mynewspaper.db;

import java.io.File;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * DB ������ : �ۼ��� ��縦 DB�� ���� ������
 **/
public class DBDeleter extends Thread {

	// �α�
	private static final String TAG = "DBDeleter";

	public static final int REQUEST_NO_CLOSE = 0;
	public static final int REQUEST_YES_CLOSE = 1;
	
	private DBHelper mDBHelper;
	private ArrayList<Long> mDeleteIdList;
	private String table_name;
	private Handler mDBDeleterHandler;
	private int mWork;
	
	

	public DBDeleter(DBHelper mDBHelper, ArrayList<Long> mDeleteIdList,
			String table_name, Handler mDBDeleterHandler, int work) {
		super();
		this.mDBHelper = mDBHelper;
		this.mDeleteIdList = mDeleteIdList;
		this.table_name = table_name;
		this.mDBDeleterHandler = mDBDeleterHandler;
		this.mWork = work;
	}



	@Override
	public void run() {

		SQLiteDatabase db;


		// DB ����
		db = mDBHelper.getWritableDatabase();
		
		// DB���� ���õ� ��� ����
		Cursor cursor;
		for (int i = 0; i < mDeleteIdList.size(); i++) {
			// SDī�忡 ���� ����
			String file_location = null;
			cursor = db.rawQuery(
					"SELECT file_location FROM " + table_name + " WHERE _id = "
							+ mDeleteIdList.get(i), null);
			if (cursor.moveToNext()) {
				file_location = cursor.getString(0);
			}
			File file = new File(file_location);
			if (file.exists())
				file.delete();

			// DB���� ����
			db.execSQL("DELETE from articles where _id = "
					+ mDeleteIdList.get(i));
			Log.d(TAG, "������ id : " + mDeleteIdList.get(i));
			cursor.close();
		}
	

		// DB �ݱ�
	
		if(mWork == REQUEST_YES_CLOSE)
			db.close();
		
		mDBDeleterHandler.sendEmptyMessage(0);

	}
}
