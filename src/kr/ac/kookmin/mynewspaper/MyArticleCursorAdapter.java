/**
 * 
 */
package kr.ac.kookmin.mynewspaper;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 어댑터 : 내 신문 읽기에서 DB에 있는 내용을 읽어오는 Cursor 어댑터
 **/
public class MyArticleCursorAdapter extends CursorAdapter {

	private static final String TAG = "MyArticleCursorAdapter";

	// Inflater
	private LayoutInflater mInflater;

	// 비트맵 크기
	private int mWidth;
	private int mHeight;

	private Context mContext;

	public MyArticleCursorAdapter(Context context, Cursor c,
			boolean autoRequery, LayoutInflater mInflater, DisplayMetrics dm) {
		super(context, c, autoRequery);
		this.mInflater = mInflater;

		mWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				90, dm);
		mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				150, dm);

		mContext = context;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return super.getView(arg0, arg1, arg2);
	}

	/**
	 * 
	 **/
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Log.d(TAG, "bindView 호출");
		view.setBackgroundColor(Color.TRANSPARENT);
		TextView myArticleDate = (TextView) view
				.findViewById(R.id.my_article_date);
		TextView myArticleTitle = (TextView) view
				.findViewById(R.id.my_article_title);
		ImageView myArticleImage = (ImageView) view
				.findViewById(R.id.my_article_image);

		// Title은 DB에서 불러오면 된다.
		myArticleDate.setText(cursor.getString(3));
		myArticleTitle.setText(cursor.getString(2));
		// Image는 DB에서 Path 불러와서 Bitmap으로 바꿔준다.
		String filePath = cursor.getString(1);


		Bitmap bm = BitmapCreator.createScaledBitmap(filePath, mWidth, mHeight,
				context.getContentResolver());
		myArticleImage.setImageBitmap(bm);
	}

	/**
	 * 
	 **/
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Log.d(TAG, "newView 호출");
		View view = mInflater.inflate(R.layout.layout_select_my_article,
				parent, false);

		return view;
	}

}
