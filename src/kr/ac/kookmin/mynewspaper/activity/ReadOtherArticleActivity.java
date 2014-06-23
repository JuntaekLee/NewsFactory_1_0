/**
 * 
 */
package kr.ac.kookmin.mynewspaper.activity;

import java.util.ArrayList;

import kr.ac.kookmin.mynewspaper.ClientComment;
import kr.ac.kookmin.mynewspaper.ClientNetworkThread;
import kr.ac.kookmin.mynewspaper.CommentListAdapter;
import kr.ac.kookmin.mynewspaper.R;
import kr.ac.kookmin.mynewspaper.db.DBHelper;
import kr.ac.kookmin.mynewspaper.db.DBManager;
import kr.ac.kookmin.mynewspaper.protocol.RequestData;
import kr.ac.kookmin.mynewspaper.protocol.RequestDelArticle;
import kr.ac.kookmin.mynewspaper.protocol.RequestGetComment;
import kr.ac.kookmin.mynewspaper.protocol.RequestInsertComment;
import kr.ac.kookmin.mynewspaper.protocol.RequestGetNormalArticle;
import kr.ac.kookmin.mynewspaper.protocol.RequestGetArticleImage;
import kr.ac.kookmin.mynewspaper.protocol.RequestRecommendArticle;
import kr.ac.kookmin.mynewspaper.protocol.ResponseData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * ��Ƽ��Ƽ : �����κ��� ���޹��� ��� �̹����� ���� ȭ��
 **/
public class ReadOtherArticleActivity extends Activity {
	// �迭&List ũ��
	public static final int PAGE_SZIE = 9;
	
	// ���������� ��۰���
	public static final int COMMENT_NUMBER = 7;

	// ����
	public static final int SELECTED_PAGE_COLOR = Color.rgb(237, 114, 39);
	public static final int NORMAL_PAGE_COLOR = Color.BLACK;

	private static final String TAG = "ReadOtherArticleActivity";
	// �����
	private CommentListAdapter mCommentListAdapter;
	private ArrayList<ClientComment> mCommentList;

	// View
	private Button mDeleteArticleBtn;
	private ImageView mOtherArticle;
	private Button mRecommendBtn;
	private TextView mRecommendCount;
	private EditText mWriteComment;
	private Button mWriteCommentBtn;
	private ListView mCommentListView;
	private Button[] mPageBtn;
	private Button mNextPage;
	private Button mPreviousPage;
	private FrameLayout mArticleFrameLayout;
	private ProgressBar mNetworkProgressBar;

	// ���� ��Ƽ��Ƽ�κ��� Data
	private String mUserName;
	private long mNewsId;

	// ��簡 ���� �ۼ��� �������
	private boolean mIsMyArticle;

	// ��� recommend ��
	private int mCountOfRecommend;

	// DB Manger
	private DBManager mDBManger;

	// ���� Comment page
	private int mCurrentPage = 0;

	// ��Ʈ��ũ ProgressDialog
	// private ProgressDialog mNetworkWaiting;

	// ������ Change ������
	private ClientNetworkThread mRequestChangePage;

	// �� ����� Comment ����
	private int mSumOfComment = 0;
	private int mSumOfPage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ��������
		RequestData requestData;
		ClientNetworkThread request;

		setContentView(R.layout.actvity_read_other_article);

		// �ʱ�ȭ
		init();

		// ���α׷����� ����
		mNetworkProgressBar = new ProgressBar(ReadOtherArticleActivity.this);
		FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.CENTER);
		mNetworkProgressBar.setLayoutParams(param);
		mNetworkProgressBar.setVisibility(View.VISIBLE);
		mArticleFrameLayout.addView(mNetworkProgressBar);

		// ������ ����
		setListener();

		// ���޹��� NewsId�� �������� News �ҷ�����
		requestData = new RequestGetArticleImage(RequestData.READ_ARTICLE, mNewsId,
				mUserName);
		request = new ClientNetworkThread(MainActivity.HOST_ADDRESS,
				requestData, mClientNetworkHandler);
		request.setDaemon(true);
		request.start();

		// ���޹��� NewsId�� �������� Comment �ҷ�����
		requestData = new RequestGetComment(RequestData.GET_COMMENT, mNewsId,
				mCurrentPage);
		request = new ClientNetworkThread(MainActivity.HOST_ADDRESS,
				requestData, mClientNetworkHandler);
		request.setDaemon(true);
		request.start();

	}

	private void setListener() {
		mRecommendBtn.setOnClickListener(mBtnClickListener);
		mWriteCommentBtn.setOnClickListener(mBtnClickListener);
		mDeleteArticleBtn.setOnClickListener(mBtnClickListener);
		for (int i = 0; i < PAGE_SZIE; i++) {
			mPageBtn[i].setOnClickListener(mBtnClickListener);
		}
		mNextPage.setOnClickListener(mBtnClickListener);
		mPreviousPage.setOnClickListener(mBtnClickListener);
	}

	private void init() {
		// View ��üȭ
		mDeleteArticleBtn = (Button) findViewById(R.id.delete_article_btn);
		mArticleFrameLayout = (FrameLayout) findViewById(R.id.other_article_frame_layout);
		mOtherArticle = (ImageView) findViewById(R.id.other_article);
		mRecommendBtn = (Button) findViewById(R.id.recommend_btn);
		mRecommendCount = (TextView) findViewById(R.id.recommend_count);
		mWriteComment = (EditText) findViewById(R.id.write_comment);
		mWriteCommentBtn = (Button) findViewById(R.id.write_comment_btn);
		mCommentListView = (ListView) findViewById(R.id.comment_list);

		// Page ����
		mPageBtn = new Button[PAGE_SZIE];
		mPageBtn[0] = (Button) findViewById(R.id.page_btn1);
		mPageBtn[1] = (Button) findViewById(R.id.page_btn2);
		mPageBtn[2] = (Button) findViewById(R.id.page_btn3);
		mPageBtn[3] = (Button) findViewById(R.id.page_btn4);
		mPageBtn[4] = (Button) findViewById(R.id.page_btn5);
		mPageBtn[5] = (Button) findViewById(R.id.page_btn6);
		mPageBtn[6] = (Button) findViewById(R.id.page_btn7);
		mPageBtn[7] = (Button) findViewById(R.id.page_btn8);
		mPageBtn[8] = (Button) findViewById(R.id.page_btn9);
		mNextPage = (Button) findViewById(R.id.next_page);
		mPreviousPage = (Button) findViewById(R.id.previous_page);

		// ����� ����
		mCommentList = new ArrayList<ClientComment>();
		mCommentListAdapter = new CommentListAdapter(
				mCommentList,
				(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		mCommentListView.setAdapter(mCommentListAdapter);

		// Extras ��������
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mUserName = bundle.getString("UserName");
		mNewsId = bundle.getLong("NewsId");

		// DB Manger
		DBHelper helper = new DBHelper(ReadOtherArticleActivity.this);
		mDBManger = new DBManager(helper);

		// ��Ʈ��ũ ProgressDialog �غ�
		// mNetworkWaiting = new ProgressDialog(ReadOtherArticleActivity.this);
		// mNetworkWaiting.setMessage("��ø� ��ٷ��ּ���...");

	}

	private Handler mDBWriterHandler = new Handler(){
		
	};
	
	private Handler mDBReaderHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "mDBReaderHandler �ڵ鷯 ����");
			// �� DB���� �ߺ��� ���� ������ �� DB�� ��õ ����Ѵ�.
			boolean isDuplicate = msg.getData().getBoolean("Duplicate");
			Log.d(TAG, "mDBReaderHandler �ڵ鷯 // isDuplicate : " + isDuplicate);
			if (isDuplicate == false) {
				mDBManger.DBWriteRecommend("recommend_news", mNewsId, mDBWriterHandler);

				// ��μ� ������ ��õ�� �ø����� �Ѵ�.
				RequestData requestData = new RequestRecommendArticle(
						RequestData.RECOMMEND_ARTICLE, mNewsId);
				ClientNetworkThread networkThread = new ClientNetworkThread(
						MainActivity.HOST_ADDRESS, requestData,
						mClientNetworkHandler);
				networkThread.setDaemon(true);
				networkThread.start();
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						ReadOtherArticleActivity.this);
				dialog.setMessage("�̹� ��õ�ϼ̽��ϴ�.").setPositiveButton("Ȯ��", null)
						.show();
			}
		}

	};

	private Handler mClientNetworkHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ResponseData responseData = (ResponseData) msg.obj;
			if (responseData.getResponseCode() == ResponseData.RESULT_OK) {
				switch (responseData.getRequestCode()) {
				case RequestData.READ_ARTICLE:
					// ������ ������ ����
					byte[] image = responseData.getArticleImage();
					mIsMyArticle = responseData.isIsMyArticle();
					mCountOfRecommend = responseData.getRecommend();

					// Recommend ����
					mRecommendCount
							.setText(Integer.toString(mCountOfRecommend));

					// ProgressBar ����
					mNetworkProgressBar.setVisibility(View.INVISIBLE);

					// ������ image ȭ�鿡 ����
					Bitmap bm = BitmapFactory.decodeByteArray(image, 0,
							image.length);
					mOtherArticle.setImageBitmap(bm);

					// �� ����� ��� Delete Button�� ���̰� �ٲ۴�.
					if (mIsMyArticle == true) {
						mDeleteArticleBtn.setVisibility(View.VISIBLE);
					}

					// ��õ ��ư ����.
					mRecommendBtn.setVisibility(View.VISIBLE);
					break;
				case RequestData.RECOMMEND_ARTICLE:
					mCountOfRecommend = responseData.getRecommend();
					// Recommend ����
					mRecommendCount
							.setText(Integer.toString(mCountOfRecommend));
					break;
				case RequestData.GET_COMMENT:
					// ���� Comment List ����
					ArrayList<ClientComment> commentList = mCommentListAdapter
							.getmCommentList();
					commentList.clear();

					// ���ο� Comment ä���
					String[] comment = responseData.getComment();
					String[] userName = responseData.getUserName();
					String[] registerTime = responseData.getRegisterTime();
					for (int i = 0; i < comment.length; i++) {
						commentList.add(new ClientComment(userName[i],
								comment[i], registerTime[i]));
						Log.d(TAG, "�߰��Ǵ� comment : "
								+ commentList.get(i).getComment());
					}

					// Adapter ����
					mCommentListAdapter.notifyDataSetChanged();

					// ������ ��ư�� ����
					mSumOfComment = responseData.getSumOfComment();
					mSumOfPage = (mSumOfComment / ReadOtherArticleActivity.COMMENT_NUMBER) + 1;
					Log.d(TAG, "�� ������ �� : " + mSumOfPage);
					if (mSumOfComment > 0) {
						for (int i = 0; i < PAGE_SZIE - mSumOfPage; i++) {
							Log.d(TAG, "������ ��ư " + (PAGE_SZIE - i) + " GONE");
							mPageBtn[PAGE_SZIE - 1 - i]
									.setVisibility(View.GONE);
						}
						for (int i = 0; i < mSumOfPage; i++) {
							Log.d(TAG, "������ ��ư " + i + " VISIBLE");
							mPageBtn[i].setVisibility(View.VISIBLE);
						}
					}

					// ������ next, previous ��ư Visible�� �ٲٱ�
					mNextPage.setVisibility(View.VISIBLE);
					mPreviousPage.setVisibility(View.VISIBLE);

					// Wait Dialog ����
					// if (mNetworkWaiting.isShowing())
					// mNetworkWaiting.dismiss();
					break;
				case RequestData.INSERT_COMMENT:
					// EditText ����
					mWriteComment.setText("");

					// Ȯ�� �޼��� ����
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							ReadOtherArticleActivity.this);
					dialog.setMessage("��ϵǾ����ϴ�.").setPositiveButton("Ȯ��", null)
							.show();

					// GET_COMMENT �ٽ� ��û (ȭ�� ���� ���ؼ�)
					RequestData requestData = new RequestGetComment(
							RequestData.GET_COMMENT, mNewsId, mCurrentPage);
					ClientNetworkThread request = new ClientNetworkThread(
							MainActivity.HOST_ADDRESS, requestData,
							mClientNetworkHandler);
					request.setDaemon(true);
					request.start();
					break;
				case RequestData.DEL_ARTICLE:
					dialog = new AlertDialog.Builder(
							ReadOtherArticleActivity.this);
					dialog.setMessage("�����Ǿ����ϴ�.")

							.setPositiveButton("Ȯ��",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											setResult(Activity.RESULT_OK);
											finish();
										}
									}).show();
					break;

				}
			}
		}
	};

	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// ��������
			RequestData requestData;
			ClientNetworkThread networkThread;
			int page;

			switch (v.getId()) {
			case R.id.recommend_btn:
				// �� DB Ȯ�� => �� DB INSERT => ���� INSERT => �� ȭ�� ����
				mDBManger.DBReadRecommend("recommend_news", mNewsId, mDBReaderHandler);
				break;
			case R.id.write_comment_btn:
				// ������ Comment Insert ��û
				Log.d(TAG, "Write Comment ��ư Ŭ���� ������� NAME : " + mUserName);
				String comment = mWriteComment.getText().toString();
				requestData = new RequestInsertComment(
						RequestData.INSERT_COMMENT, mUserName, mNewsId, comment);
				networkThread = new ClientNetworkThread(
						MainActivity.HOST_ADDRESS, requestData,
						mClientNetworkHandler);
				networkThread.setDaemon(true);
				networkThread.start();
				break;
			case R.id.delete_article_btn:
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						ReadOtherArticleActivity.this);
				dialog.setMessage("�����Ͻðڽ��ϱ�?")
						.setNegativeButton("���", null)
						.setPositiveButton("Ȯ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										RequestData requestData = new RequestDelArticle(
												RequestData.DEL_ARTICLE,
												mNewsId);
										ClientNetworkThread networkThread = new ClientNetworkThread(
												MainActivity.HOST_ADDRESS,
												requestData,
												mClientNetworkHandler);
										networkThread.setDaemon(true);
										networkThread.start();
									}
								}).show();

				break;

			/**************** ���⼭���ʹ� ������ ��ư **********************/
			case R.id.previous_page:
				if (mCurrentPage == 0)
					break;
				if (mCurrentPage % PAGE_SZIE == 0) {
					for (int i = 0; i < PAGE_SZIE; i++) {
						page = Integer.parseInt(mPageBtn[i].getText()
								.toString());
						mPageBtn[i].setText(page - 9);
					}
				}
				// mCurrentPage--;
				// ����� Page. Server�� Request
				requestChangePage(mCurrentPage);
				break;
			case R.id.page_btn1:
				// ���� �������� �� �������̸� �׳� break;
				page = Integer.parseInt(mPageBtn[0].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server�� Request
				requestChangePage(page);
				break;
			case R.id.page_btn2:
				page = Integer.parseInt(mPageBtn[1].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server�� Request
				requestChangePage(page);
				break;
			case R.id.page_btn3:
				page = Integer.parseInt(mPageBtn[2].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server�� Request
				requestChangePage(page);
				break;
			case R.id.page_btn4:
				page = Integer.parseInt(mPageBtn[3].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server�� Request
				requestChangePage(page);
				break;
			case R.id.page_btn5:
				page = Integer.parseInt(mPageBtn[4].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server�� Request
				requestChangePage(page);
				break;
			case R.id.page_btn6:
				page = Integer.parseInt(mPageBtn[5].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server�� Request
				requestChangePage(page);
				break;
			case R.id.page_btn7:
				page = Integer.parseInt(mPageBtn[6].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server�� Request
				requestChangePage(page);
				break;
			case R.id.page_btn8:
				page = Integer.parseInt(mPageBtn[7].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server�� Request
				requestChangePage(page);
				break;
			case R.id.page_btn9:
				page = Integer.parseInt(mPageBtn[8].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server�� Request
				requestChangePage(page);
				break;
			case R.id.next_page:
				Log.d(TAG, "���������� // mCurrentPage : " + mCurrentPage
						+ ", mSumOfPage : " + mSumOfPage);
				if (mCurrentPage == mSumOfPage - 1)
					break;
				if (mCurrentPage % (PAGE_SZIE - 1) == 0 && mCurrentPage != 0) {
					for (int i = 0; i < mPageBtn.length; i++) {
						page = Integer.parseInt(mPageBtn[i].getText()
								.toString());
						mPageBtn[i].setText(page + 9);
					}
				}
				// mCurrentPage++;
				// ����� Page. Server�� Request
				requestChangePage(mCurrentPage + 2);
				break;

			}
		}
	};

	private void requestChangePage(int page) {
		// ������ Color �ٲٱ�
		Log.d(TAG, "page-1 : " + (page - 1) + ", ���� ������ : " + mCurrentPage);
		mPageBtn[(page % PAGE_SZIE) - 1]
				.setBackgroundResource(R.drawable.selected_page);
		for (int i = 0; i < PAGE_SZIE; i++) {
			if (i == page - 1)
				continue;
			mPageBtn[i].setBackgroundColor(Color.TRANSPARENT);
		}

		// ���� ������ ����
		mCurrentPage = page - 1;

		// Wait Dialog ����
		// mNetworkWaiting.show();

		// �������� Thread�� ���� ��� �����Ͽ� ȭ�� ���� �����ʱ�
		if (mRequestChangePage != null && mRequestChangePage.isAlive())
			mRequestChangePage.interrupt();

		// ������ ���� ��û
		RequestData requestData = new RequestGetComment(
				RequestData.GET_COMMENT, mNewsId, mCurrentPage);
		mRequestChangePage = new ClientNetworkThread(MainActivity.HOST_ADDRESS,
				requestData, mClientNetworkHandler);
		mRequestChangePage.setDaemon(true);
		mRequestChangePage.start();

	}
}
