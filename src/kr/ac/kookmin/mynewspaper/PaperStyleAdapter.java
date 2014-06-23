/**
 * 
 */
package kr.ac.kookmin.mynewspaper;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * 어댑터 : 기사 배경의 스타일을 정하는 스타일리스트의 어댑터
 **/
public class PaperStyleAdapter extends BaseAdapter {
	private static final String TAG = "PaperStyleAdapter";

	// Context
	private Context mContext;
	// DisplayMetric
	private DisplayMetrics mDm;
	// 사용할 ImageView의 크기(dip)
	private int mWidth;
	private int mHeight;

	// 스타일 Id (Color : Gray)
	private int[] mGrayStyleIds = { R.drawable.article_gray_style1,
			R.drawable.article_gray_style2, R.drawable.article_gray_style3,
			R.drawable.article_gray_style4, R.drawable.article_gray_style5,
			R.drawable.article_gray_style6, R.drawable.article_gray_style7 };

	public PaperStyleAdapter(Context mContext, DisplayMetrics mDm) {
		super();
		this.mContext = mContext;
		this.mDm = mDm;
		mWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				90, mDm);
		mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				150, mDm);
	}

	/**
	 * 
	 **/
	@Override
	public int getCount() {
		return mGrayStyleIds.length;
	}

	/**
	 * 
	 **/
	@Override
	public Object getItem(int position) {
		return mGrayStyleIds[position];
	}

	/**
	 * 
	 **/
	@Override
	public long getItemId(int position) {
		return position;
	}

	public int[] getmStyleIds() {

		return mGrayStyleIds;

	}

	/**
	 *  
	 **/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv;
		if (convertView == null) {
			iv = new ImageView(mContext);
			iv.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
			iv.setAdjustViewBounds(true);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setBackgroundResource(R.drawable.image_border);
		} else {
			iv = (ImageView) convertView;
		}
		iv.setImageResource(mGrayStyleIds[position]);
		return iv;
	}

}
