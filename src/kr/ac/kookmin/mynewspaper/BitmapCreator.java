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
 * Bitmap 관련 함수 모음
 **/
public class BitmapCreator {
	// 플래그
	static String TAG = "BitmapCreator";

	/**
	 * 함수 : 전달받은 비트맵을 리사이클한다.
	 **/
	public static void checkBitmapRecycle(Bitmap bm) {
		if (bm != null) {
			bm.recycle();
			bm = null;
		}
	}

	/**
	 * 함수 : 전달받은 비트맵을 byte[]로 바꿔준다.
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
	 * 함수 : 전달받은 View를 비트맵으로 바꿔준다.
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
	 * 함수 : 전달받은 Uri를 통해 이미지의 절대경로를 알아낸다.
	 **/
	private static String findImagePath(Uri uri, ContentResolver cr) {
		String path;
		Cursor cursor;
		Log.d(TAG, "BitmapCreator에서 findImagePath 의 Cursor Adapter의 Uri는 ? : "
				+ uri);
		cursor = Images.Media.query(cr, uri, null);
		if (cursor == null) {
			Log.d(TAG, "cursor가 null 이네요...");
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
	 * 함수 : 전달받은 Uri와 이미지뷰의 크기를 통해 이미지의 scaleFactor를 구한다.
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

		Log.d(TAG, "샘플링 전의 Bitmap W : " + bitmapW + " H : " + bitmapH);
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

		Log.d(TAG, "샘플링 전의 Bitmap W : " + bitmapW + " H : " + bitmapH);
		if (targetW != 0 && targetH != 0)
			scaleFactor = Math.min(bitmapW / targetW, bitmapH / targetH);
		return scaleFactor;
	}

	/**
	 * 함수 : 전달받은 uri를 통해 비트맵을 구하고 이미지뷰 크기에 맞게 scale한다.
	 **/
	public static Bitmap createScaledBitmap(Uri selPhotoUri, ImageView iv,
			ContentResolver cr) {

		String path;
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap selPhoto = null;

		Log.d(TAG, "createScaledBitmap 함수  iv.getWidth : " + iv.getWidth()
				+ ", iv.getHeight : " + iv.getHeight());
		options.inSampleSize = findScaleFactor(selPhotoUri, iv.getWidth(),
				iv.getHeight(), cr);
		path = findImagePath(selPhotoUri, cr);
		Log.d(TAG, "BitmapCreator inSampleSize 값 : " + options.inSampleSize);
		try {
			selPhoto = BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
			Log.d(TAG,
					"createScaledBitmap 함수 /// BitmapFactory.decodeFile 중 에러 발생");
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
		Log.d(TAG, "BitmapCreator inSampleSize 값 : " + options.inSampleSize);
		try {
			selPhoto = BitmapFactory.decodeFile(filePath, options);
		} catch (Exception e) {
			Log.d(TAG,
					"createScaledBitmap 함수 /// BitmapFactory.decodeFile 중 에러 발생");
			e.printStackTrace();
		}
		return selPhoto;
	}

	/**
	 * 함수 : 전달받은 Uri를 통해 비트맵을 구하고 이미지뷰 크기(width, height)에 맞게 scale한다.
	 **/
	public static Bitmap createScaledBitmap(Uri selPhotoUri, int width,
			int height, ContentResolver cr) {

		String path;
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap selPhoto = null;

		options.inSampleSize = findScaleFactor(selPhotoUri, width, height, cr);
		path = findImagePath(selPhotoUri, cr);
		Log.d(TAG, "BitmapCreator inSampleSize 값 : " + options.inSampleSize);
		try {
			selPhoto = BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
			Log.d(TAG,
					"createScaledBitmap 함수 /// BitmapFactory.decodeFile 중 에러 발생");
			e.printStackTrace();
		}
		return selPhoto;
	}

	/**
	 * 함수 : 전달받은 이미지뷰의 Bitmap을 리사이클한다.
	 **/
	public static boolean recycleImageViewBitmap(ImageView iv) {
		// 이미지 뷰가 null이 아닐때 실행
		// null이면 false 리턴
		if (iv != null) {
			try {
				// 이미지 뷰에서 drawable 추출
				Drawable d = iv.getDrawable();
				// invalidate 될때 recycle된 bitmap 호출 위험 때문에 null로 제거해준다
				iv.setImageBitmap(null);
				// drawable이 bitmap drawable일때 bitmap을 얻엇 recycle한다
				if (d instanceof BitmapDrawable) {
					Bitmap b = ((BitmapDrawable) d).getBitmap();
					b.recycle();
				}
				// drawable이 생성될때 등록된 callback을 제거한다
				d.setCallback(null);
			}
			// 작업 도중 에러가 생기면 false리턴
			catch (Exception e) {
				e.getStackTrace();
				return false;
			}
			// 정상적으로 작업 종료 되었으므로 ture리턴
			return true;
		}
		// 이미지뷰가 null이니 false리턴
		return false;
	}

}
