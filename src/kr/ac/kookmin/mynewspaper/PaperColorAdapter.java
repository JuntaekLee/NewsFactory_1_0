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
 * 어댑터 : 기사 배경의 색상을 정하는 컬러리스트의 어댑터
 **/
public class PaperColorAdapter extends BaseAdapter {
	private static final String TAG = "PaperColorAdapter";

	// Context
	private Context mContext;
	// DisplayMetric
	private DisplayMetrics mDm;
	// 사용할 ImageView의 크기(dip)
	private int mWidth;
	private int mHeight;
	// 스타일 Id
	private int[] mColorIds = { R.drawable.article_gray_mini,
			R.drawable.article_red_mini, R.drawable.article_skyblue_mini,
			R.drawable.article_melon_mini, R.drawable.article_yellow_mini,
			R.drawable.article_purple_mini };

	public PaperColorAdapter(Context mContext, DisplayMetrics mDm) {
		super();
		this.mContext = mContext;
		this.mDm = mDm;
		mWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				90, mDm);
		mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				150, mDm);
	}

	@Override
	public int getCount() {
		return mColorIds.length;
	}

	@Override
	public Object getItem(int position) {
		return mColorIds[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int[] getmColorIds() {
		return mColorIds;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv;
		if (convertView == null) {
			iv = new ImageView(mContext);
			iv.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
			iv.setAdjustViewBounds(true);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setBackgroundResource(R.drawable.image_border);
		} else
			iv = (ImageView) convertView;

		iv.setImageResource(mColorIds[position]);

		return iv;
	}

}
