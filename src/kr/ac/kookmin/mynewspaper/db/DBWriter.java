package kr.ac.kookmin.mynewspaper.db;

import java.util.Calendar;

import kr.ac.kookmin.mynewspaper.NewspaperData;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * DB ������ : �ۼ��� ��縦 DB�� ���� ������
 **/
public class DBWriter extends Thread {

	// �α�
	private static final String TAG = "DBWriter";

	// ��������
	private String mSdPath;
	private Calendar mNow;

	// DB ����
	private DBHelper mDBHelper = null;

	// �ڵ鷯
	private Handler mDBWriterHandler;

	// �÷���
	private String file_name = null;
	private NewspaperData mNewspaperData = null;
	// ���̺� �̸�
	private String table_name = null;

	public DBWriter(Handler mDBWriterHandler, DBHelper mDBHelper,
			String table_name, String mSdPath, NewspaperData newspaperData) {
		super();
		this.file_name = null;
		this.mNewspaperData = newspaperData;
		this.mDBWriterHandler = mDBWriterHandler;
		this.mDBHelper = mDBHelper;
		this.table_name = table_name;

		// SD card path ���ϱ�
		this.mSdPath = mSdPath;
	}

	@Override
	public void run() {

		SQLiteDatabase db;
		Message msg = Message.obtain();
		msg.what = 0;

		// DB ����
		db = mDBHelper.getWritableDatabase();

		// ���� �̸� ���ϱ�
		mNow = Calendar.getInstance();
		String file_location = mSdPath + "/MyNewspaper/Article/"
				+ mNow.getTimeInMillis() + ".jpg";

		// DB ����
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

		// DB �ݱ�
		db.close();

		// ���� ���� ��û�ϴ� �ڵ鷯 �θ���
		Bundle bundle = new Bundle();
		bundle.putString("FileLocation", file_location);
		msg.setData(bundle);
		mDBWriterHandler.sendMessage(msg);

	}
}
