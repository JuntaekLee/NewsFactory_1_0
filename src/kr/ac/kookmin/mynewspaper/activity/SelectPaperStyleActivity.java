/**
 * 
 */
package kr.ac.kookmin.mynewspaper.activity;

import kr.ac.kookmin.mynewspaper.PaperStyleAdapter;
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
 * ��Ƽ��Ƽ : ��� ����� ��Ÿ���� �����ϴ� ȭ��
 **/
public class SelectPaperStyleActivity extends Activity {

	private static final String TAG = "SelectPaperStyleActivity";

	// Style
	public static final int STYLE1 = 0;
	public static final int STYLE2 = 1;
	public static final int STYLE3 = 2;
	public static final int STYLE4 = 3;
	public static final int STYLE5 = 4;
	public static final int STYLE6 = 5;
	public static final int STYLE7 = 6;

	// GridView
	private GridView mStyleGrid;
	// DisplayMetric
	private DisplayMetrics mDm;
	// Adapter
	private PaperStyleAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_paper_style);

		// �ʱ�ȭ
		init();
	}

	/**
	 * ��Ƽ��Ƽ ������� �� �ʿ��� �ʱ�ȭ ����
	 **/
	private void init() {
		// View ��üȭ
		mStyleGrid = (GridView) findViewById(R.id.styleGrid);

		// ȭ������ ���ϱ�
		mDm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDm);

		// ����� ����
		mAdapter = new PaperStyleAdapter(this, mDm);
		mStyleGrid.setAdapter(mAdapter);

		// ItemClick Listener ���
		mStyleGrid.setOnItemClickListener(mGridItemClick);

	}

	/**
	 * GridView Item Ŭ���� LayoutId�� �Ѱܼ� WriteActivityȣ���Ѵ�.
	 **/
	private OnItemClickListener mGridItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(SelectPaperStyleActivity.this,
					SelectPaperColorActivity.class);
			switch (position) {
			case 0:
				intent.putExtra("PaperStyle", STYLE1);
				break;
			case 1:
				intent.putExtra("PaperStyle", STYLE2);
				break;
			case 2:
				intent.putExtra("PaperStyle", STYLE3);
				break;
			case 3:
				intent.putExtra("PaperStyle", STYLE4);
				break;
			case 4:
				intent.putExtra("PaperStyle", STYLE5);
				break;
			case 5:
				intent.putExtra("PaperStyle", STYLE6);
				break;
			case 6:
				intent.putExtra("PaperStyle", STYLE7);

				break;
			}
			startActivity(intent);
		}
	};
}
