/**
 * 
 */
package kr.ac.kookmin.mynewspaper.activity;

import kr.ac.kookmin.mynewspaper.PaperColorAdapter;
import kr.ac.kookmin.mynewspaper.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 액티비티 : 기사 배경의 색상을 결정하는 화면
 **/
public class SelectPaperColorActivity extends Activity {

	private static final String TAG = "SelectPaperColorActivity";

	// Color
	public static final int GRAY = 0;
	public static final int RED = 1;
	public static final int SKY_BLUE = 2;
	public static final int MELON = 3;
	public static final int YELLOW = 4;
	public static final int PURPLE = 5;
	// GridView
	private GridView mColorGrid;
	// DisplayMetric
	private DisplayMetrics mDm;
	// Adapter
	private PaperColorAdapter mAdapter;
	// Paper Style
	private int mPaperStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_paper_color);

		// 초기화
		init();
	}

	/**
	 * 액티비티 만들어질 때 필요한 초기화 수행
	 **/
	private void init() {
		// View 객체화
		mColorGrid = (GridView) findViewById(R.id.colorGrid);

		// 화면정보 구하기
		mDm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDm);

		// 어댑터 설정
		mAdapter = new PaperColorAdapter(this, mDm);
		mColorGrid.setAdapter(mAdapter);

		// ItemClick Listener 등록
		mColorGrid.setOnItemClickListener(mGridItemClick);

		// 이전 액티비티에서 받은 Style 저장
		mPaperStyle = getIntent().getExtras().getInt("PaperStyle");

	}

	private OnItemClickListener mGridItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(SelectPaperColorActivity.this,
					WriteArticleActivity.class);
			intent.putExtra("PaperStyle", mPaperStyle);
			switch (position) {
			case 0:
				intent.putExtra("PaperColor", GRAY);
				break;
			case 1:
				intent.putExtra("PaperColor", RED);
				break;
			case 2:
				intent.putExtra("PaperColor", SKY_BLUE);
				break;
			case 3:
				intent.putExtra("PaperColor", MELON);
				break;
			case 4:
				intent.putExtra("PaperColor", YELLOW);
				break;
			case 5:
				intent.putExtra("PaperColor", PURPLE);
				break;
			}
			startActivity(intent);
		}
	};
}
