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
 * 스레드 : Bitmap을 SD CARD에 저장하는 스레드
 **/
public class SaveArticleBitmap extends Thread {

	// SD Card
	private String mSdPath;

	// File Name
	private String mResultFileLocation;

	// Result Bitmap
	private Bitmap mResultBitmap;

	// Bitmap으로 전환할 View
	private View mView;

	// 핸들러
	private Handler mSaveArticleBitmapHandler;

	/**
	 * 생성자
	 */
	public SaveArticleBitmap(Handler mSaveArticleBitmapHandler,
			String mResultFileLocation, View mView) {
		this.mSaveArticleBitmapHandler = mSaveArticleBitmapHandler;
		this.mResultFileLocation = mResultFileLocation;
		this.mView = mView;
		// SD card path 구하기
		String ext = Environment.getExternalStorageState();
		if (ext.equals(Environment.MEDIA_MOUNTED))
			mSdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		else
			mSdPath = Environment.MEDIA_UNMOUNTED;
	}

	@Override
	public void run() {
		// 결과물인 Layout을 Bitmap으로 전환
		BitmapCreator.checkBitmapRecycle(mResultBitmap);
		mResultBitmap = BitmapCreator.getBitmapFromView(mView);

		// 파일에 저장
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
