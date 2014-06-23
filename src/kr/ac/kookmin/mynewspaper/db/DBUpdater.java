package kr.ac.kookmin.mynewspaper.db;

import kr.ac.kookmin.mynewspaper.NewspaperData;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * DB ������ : ��縦 �����ϰ� ���� ������ ��縦 DB�� ������Ʈ�ϴ� ������
 **/
public class DBUpdater extends Thread {
	// �α�
	private static final String TAG = "DBUpdater";

	// DB ����
	private DBHelper mDBHelper = null;

	// SD Card
	private String mSdPath;

	// �ڵ鷯
	private Handler mDBUpdaterHandler;

	// �÷���
	private String file_name;
	private NewspaperData mNewspaperData;

	// ���̺� �̸�
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

		// ���� ���� ��ġ ���ϱ� (����)
		Cursor cursor = db.rawQuery(
				"SELECT file_location FROM articles WHERE _id=" + mId, null);
		cursor.moveToFirst();
		String file_location = cursor.getString(0);

		// DB ����
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

		// DB �ݱ�
		cursor.close();
		db.close();

		// ���� ���� ��û�ϴ� �ڵ鷯 �θ���
		Bundle bundle = new Bundle();
		bundle.putString("FileLocation", file_location);
		msg.setData(bundle);
		mDBUpdaterHandler.sendMessage(msg);

	}
}
