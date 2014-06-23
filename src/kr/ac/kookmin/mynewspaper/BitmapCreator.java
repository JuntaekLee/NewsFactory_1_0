/**
 * 
 */
package kr.ac.kookmin.mynewspaper;

import java.io.ByteArrayOutputStream;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Bitmap ���� �Լ� ����
 **/
public class BitmapCreator {
	// �÷���
	static String TAG = "BitmapCreator";

	/**
	 * �Լ� : ���޹��� ��Ʈ���� ������Ŭ�Ѵ�.
	 **/
	public static void checkBitmapRecycle(Bitmap bm) {
		if (bm != null) {
			bm.recycle();
			bm = null;
		}
	}

	/**
	 * �Լ� : ���޹��� ��Ʈ���� byte[]�� �ٲ��ش�.
	 **/
	public static byte[] getByteArrayFromBitmap(Bitmap bm) {
		if (bm == null) {
			return null;
		}
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 80, byteArray);
		byte[] result = byteArray.toByteArray();
		return result;
	}

	/**
	 * �Լ� : ���޹��� View�� ��Ʈ������ �ٲ��ش�.
	 **/
	public static Bitmap getBitmapFromView(View view) {
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
				view.getHeight(), Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			bgDrawable.draw(canvas);
		else
			canvas.drawColor(Color.WHITE);
		view.draw(canvas);
		return returnedBitmap;
	}

	/**
	 * �Լ� : ���޹��� Uri�� ���� �̹����� �����θ� �˾Ƴ���.
	 **/
	private static String findImagePath(Uri uri, ContentResolver cr) {
		String path;
		Cursor cursor;
		Log.d(TAG, "BitmapCreator���� findImagePath �� Cursor Adapter�� Uri�� ? : "
				+ uri);
		cursor = Images.Media.query(cr, uri, null);
		if (cursor == null) {
			Log.d(TAG, "cursor�� null �̳׿�...");
		}
		if (cursor.moveToFirst()) {
			path = cursor.getString(cursor
					.getColumnIndex(Images.ImageColumns.DATA));
			return path;
		} else {
			return null;
		}

	}

	/**
	 * 
	 * �Լ� : ���޹��� Uri�� �̹������� ũ�⸦ ���� �̹����� scaleFactor�� ���Ѵ�.
	 **/
	private static int findScaleFactor(Uri uri, int targetW, int targetH,
			ContentResolver cr) {
		int scaleFactor = 1;
		String path = null;
		int bitmapW = 0;
		int bitmapH = 0;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		path = findImagePath(uri, cr);
		BitmapFactory.decodeFile(path, options);
		bitmapW = options.outWidth;
		bitmapH = options.outHeight;

		Log.d(TAG, "���ø� ���� Bitmap W : " + bitmapW + " H : " + bitmapH);
		if (targetW != 0 && targetH != 0)
			scaleFactor = Math.min(bitmapW / targetW, bitmapH / targetH);
		return scaleFactor;
	}

	private static int findScaleFactor(String filePath, int targetW,
			int targetH, ContentResolver cr) {
		int scaleFactor = 1;
		String path = null;
		int bitmapW = 0;
		int bitmapH = 0;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(filePath, options);
		bitmapW = options.outWidth;
		bitmapH = options.outHeight;

		Log.d(TAG, "���ø� ���� Bitmap W : " + bitmapW + " H : " + bitmapH);
		if (targetW != 0 && targetH != 0)
			scaleFactor = Math.min(bitmapW / targetW, bitmapH / targetH);
		return scaleFactor;
	}

	/**
	 * �Լ� : ���޹��� uri�� ���� ��Ʈ���� ���ϰ� �̹����� ũ�⿡ �°� scale�Ѵ�.
	 **/
	public static Bitmap createScaledBitmap(Uri selPhotoUri, ImageView iv,
			ContentResolver cr) {

		String path;
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap selPhoto = null;

		Log.d(TAG, "createScaledBitmap �Լ�  iv.getWidth : " + iv.getWidth()
				+ ", iv.getHeight : " + iv.getHeight());
		options.inSampleSize = findScaleFactor(selPhotoUri, iv.getWidth(),
				iv.getHeight(), cr);
		path = findImagePath(selPhotoUri, cr);
		Log.d(TAG, "BitmapCreator inSampleSize �� : " + options.inSampleSize);
		try {
			selPhoto = BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
			Log.d(TAG,
					"createScaledBitmap �Լ� /// BitmapFactory.decodeFile �� ���� �߻�");
			e.printStackTrace();
		}
		return selPhoto;
	}

	public static Bitmap createScaledBitmap(String filePath, int width,
			int height, ContentResolver cr) {

		String path;
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap selPhoto = null;

		options.inSampleSize = findScaleFactor(filePath, width,
				height, cr);
		Log.d(TAG, "BitmapCreator inSampleSize �� : " + options.inSampleSize);
		try {
			selPhoto = BitmapFactory.decodeFile(filePath, options);
		} catch (Exception e) {
			Log.d(TAG,
					"createScaledBitmap �Լ� /// BitmapFactory.decodeFile �� ���� �߻�");
			e.printStackTrace();
		}
		return selPhoto;
	}

	/**
	 * �Լ� : ���޹��� Uri�� ���� ��Ʈ���� ���ϰ� �̹����� ũ��(width, height)�� �°� scale�Ѵ�.
	 **/
	public static Bitmap createScaledBitmap(Uri selPhotoUri, int width,
			int height, ContentResolver cr) {

		String path;
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap selPhoto = null;

		options.inSampleSize = findScaleFactor(selPhotoUri, width, height, cr);
		path = findImagePath(selPhotoUri, cr);
		Log.d(TAG, "BitmapCreator inSampleSize �� : " + options.inSampleSize);
		try {
			selPhoto = BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
			Log.d(TAG,
					"createScaledBitmap �Լ� /// BitmapFactory.decodeFile �� ���� �߻�");
			e.printStackTrace();
		}
		return selPhoto;
	}

	/**
	 * �Լ� : ���޹��� �̹������� Bitmap�� ������Ŭ�Ѵ�.
	 **/
	public static boolean recycleImageViewBitmap(ImageView iv) {
		// �̹��� �䰡 null�� �ƴҶ� ����
		// null�̸� false ����
		if (iv != null) {
			try {
				// �̹��� �信�� drawable ����
				Drawable d = iv.getDrawable();
				// invalidate �ɶ� recycle�� bitmap ȣ�� ���� ������ null�� �������ش�
				iv.setImageBitmap(null);
				// drawable�� bitmap drawable�϶� bitmap�� ��� recycle�Ѵ�
				if (d instanceof BitmapDrawable) {
					Bitmap b = ((BitmapDrawable) d).getBitmap();
					b.recycle();
				}
				// drawable�� �����ɶ� ��ϵ� callback�� �����Ѵ�
				d.setCallback(null);
			}
			// �۾� ���� ������ ����� false����
			catch (Exception e) {
				e.getStackTrace();
				return false;
			}
			// ���������� �۾� ���� �Ǿ����Ƿ� ture����
			return true;
		}
		// �̹����䰡 null�̴� false����
		return false;
	}

}
