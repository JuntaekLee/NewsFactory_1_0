/**
 * 
 */
package kr.ac.kookmin.mynewspaper;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.Handler;
import android.view.View;

/**
 * ������ : Bitmap�� SD CARD�� �����ϴ� ������
 **/
public class SaveArticleBitmap extends Thread {

	// SD Card
	private String mSdPath;

	// File Name
	private String mResultFileLocation;

	// Result Bitmap
	private Bitmap mResultBitmap;

	// Bitmap���� ��ȯ�� View
	private View mView;

	// �ڵ鷯
	private Handler mSaveArticleBitmapHandler;

	/**
	 * ������
	 */
	public SaveArticleBitmap(Handler mSaveArticleBitmapHandler,
			String mResultFileLocation, View mView) {
		this.mSaveArticleBitmapHandler = mSaveArticleBitmapHandler;
		this.mResultFileLocation = mResultFileLocation;
		this.mView = mView;
		// SD card path ���ϱ�
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED))
			mSdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		else
			mSdPath = Environment.MEDIA_UNMOUNTED;
	}

	@Override
	public void run() {
		// ������� Layout�� Bitmap���� ��ȯ
		BitmapCreator.checkBitmapRecycle(mResultBitmap);
		mResultBitmap = BitmapCreator.getBitmapFromView(mView);

		// ���Ͽ� ����
		File dir = new File(mSdPath + "/MyNewspaper/Article");
		dir.mkdirs();
		File file = new File(mResultFileLocation);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			mResultBitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		mSaveArticleBitmapHandler.sendEmptyMessage(0);
	}
}
