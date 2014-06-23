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
 * 액티비티 : 서버로부터 전달받은 기사 이미지를 보는 화면
 **/
public class ReadOtherArticleActivity extends Activity {
	// 배열&List 크기
	public static final int PAGE_SZIE = 9;
	
	// 한페이지에 댓글갯수
	public static final int COMMENT_NUMBER = 7;

	// 색상
	public static final int SELECTED_PAGE_COLOR = Color.rgb(237, 114, 39);
	public static final int NORMAL_PAGE_COLOR = Color.BLACK;

	private static final String TAG = "ReadOtherArticleActivity";
	// 어댑터
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

	// 이전 액티비티로부터 Data
	private String mUserName;
	private long mNewsId;

	// 기사가 내가 작성한 기사인지
	private boolean mIsMyArticle;

	// 기사 recommend 수
	private int mCountOfRecommend;

	// DB Manger
	private DBManager mDBManger;

	// 현재 Comment page
	private int mCurrentPage = 0;

	// 네트워크 ProgressDialog
	// private ProgressDialog mNetworkWaiting;

	// 페이지 Change 스레드
	private ClientNetworkThread mRequestChangePage;

	// 이 기사의 Comment 개수
	private int mSumOfComment = 0;
	private int mSumOfPage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 지역변수
		RequestData requestData;
		ClientNetworkThread request;

		setContentView(R.layout.actvity_read_other_article);

		// 초기화
		init();

		// 프로그래스바 설정
		mNetworkProgressBar = new ProgressBar(ReadOtherArticleActivity.this);
		FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.CENTER);
		mNetworkProgressBar.setLayoutParams(param);
		mNetworkProgressBar.setVisibility(View.VISIBLE);
		mArticleFrameLayout.addView(mNetworkProgressBar);

		// 리스너 설정
		setListener();

		// 전달받은 NewsId로 서버에서 News 불러오기
		requestData = new RequestGetArticleImage(RequestData.READ_ARTICLE, mNewsId,
				mUserName);
		request = new ClientNetworkThread(MainActivity.HOST_ADDRESS,
				requestData, mClientNetworkHandler);
		request.setDaemon(true);
		request.start();

		// 전달받은 NewsId로 서버에서 Comment 불러오기
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
		// View 객체화
		mDeleteArticleBtn = (Button) findViewById(R.id.delete_article_btn);
		mArticleFrameLayout = (FrameLayout) findViewById(R.id.other_article_frame_layout);
		mOtherArticle = (ImageView) findViewById(R.id.other_article);
		mRecommendBtn = (Button) findViewById(R.id.recommend_btn);
		mRecommendCount = (TextView) findViewById(R.id.recommend_count);
		mWriteComment = (EditText) findViewById(R.id.write_comment);
		mWriteCommentBtn = (Button) findViewById(R.id.write_comment_btn);
		mCommentListView = (ListView) findViewById(R.id.comment_list);

		// Page 생성
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

		// 어댑터 설정
		mCommentList = new ArrayList<ClientComment>();
		mCommentListAdapter = new CommentListAdapter(
				mCommentList,
				(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		mCommentListView.setAdapter(mCommentListAdapter);

		// Extras 가져오기
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mUserName = bundle.getString("UserName");
		mNewsId = bundle.getLong("NewsId");

		// DB Manger
		DBHelper helper = new DBHelper(ReadOtherArticleActivity.this);
		mDBManger = new DBManager(helper);

		// 네트워크 ProgressDialog 준비
		// mNetworkWaiting = new ProgressDialog(ReadOtherArticleActivity.this);
		// mNetworkWaiting.setMessage("잠시만 기다려주세요...");

	}

	private Handler mDBWriterHandler = new Handler(){
		
	};
	
	private Handler mDBReaderHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "mDBReaderHandler 핸들러 진입");
			// 내 DB에서 중복이 되지 않을시 내 DB에 추천 등록한다.
			boolean isDuplicate = msg.getData().getBoolean("Duplicate");
			Log.d(TAG, "mDBReaderHandler 핸들러 // isDuplicate : " + isDuplicate);
			if (isDuplicate == false) {
				mDBManger.DBWriteRecommend("recommend_news", mNewsId, mDBWriterHandler);

				// 비로소 서버에 추천수 올리도록 한다.
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
				dialog.setMessage("이미 추천하셨습니다.").setPositiveButton("확인", null)
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
					// 수신한 데이터 저장
					byte[] image = responseData.getArticleImage();
					mIsMyArticle = responseData.isIsMyArticle();
					mCountOfRecommend = responseData.getRecommend();

					// Recommend 갱신
					mRecommendCount
							.setText(Integer.toString(mCountOfRecommend));

					// ProgressBar 해제
					mNetworkProgressBar.setVisibility(View.INVISIBLE);

					// 수신한 image 화면에 띄우기
					Bitmap bm = BitmapFactory.decodeByteArray(image, 0,
							image.length);
					mOtherArticle.setImageBitmap(bm);

					// 내 기사일 경우 Delete Button을 보이게 바꾼다.
					if (mIsMyArticle == true) {
						mDeleteArticleBtn.setVisibility(View.VISIBLE);
					}

					// 추천 버튼 띄운다.
					mRecommendBtn.setVisibility(View.VISIBLE);
					break;
				case RequestData.RECOMMEND_ARTICLE:
					mCountOfRecommend = responseData.getRecommend();
					// Recommend 갱신
					mRecommendCount
							.setText(Integer.toString(mCountOfRecommend));
					break;
				case RequestData.GET_COMMENT:
					// 기존 Comment List 비우기
					ArrayList<ClientComment> commentList = mCommentListAdapter
							.getmCommentList();
					commentList.clear();

					// 새로운 Comment 채우기
					String[] comment = responseData.getComment();
					String[] userName = responseData.getUserName();
					String[] registerTime = responseData.getRegisterTime();
					for (int i = 0; i < comment.length; i++) {
						commentList.add(new ClientComment(userName[i],
								comment[i], registerTime[i]));
						Log.d(TAG, "추가되는 comment : "
								+ commentList.get(i).getComment());
					}

					// Adapter 갱신
					mCommentListAdapter.notifyDataSetChanged();

					// 페이지 버튼수 설정
					mSumOfComment = responseData.getSumOfComment();
					mSumOfPage = (mSumOfComment / ReadOtherArticleActivity.COMMENT_NUMBER) + 1;
					Log.d(TAG, "총 페이지 수 : " + mSumOfPage);
					if (mSumOfComment > 0) {
						for (int i = 0; i < PAGE_SZIE - mSumOfPage; i++) {
							Log.d(TAG, "페이지 버튼 " + (PAGE_SZIE - i) + " GONE");
							mPageBtn[PAGE_SZIE - 1 - i]
									.setVisibility(View.GONE);
						}
						for (int i = 0; i < mSumOfPage; i++) {
							Log.d(TAG, "페이지 버튼 " + i + " VISIBLE");
							mPageBtn[i].setVisibility(View.VISIBLE);
						}
					}

					// 페이지 next, previous 버튼 Visible로 바꾸기
					mNextPage.setVisibility(View.VISIBLE);
					mPreviousPage.setVisibility(View.VISIBLE);

					// Wait Dialog 해제
					// if (mNetworkWaiting.isShowing())
					// mNetworkWaiting.dismiss();
					break;
				case RequestData.INSERT_COMMENT:
					// EditText 비우기
					mWriteComment.setText("");

					// 확인 메세지 띄우기
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							ReadOtherArticleActivity.this);
					dialog.setMessage("등록되었습니다.").setPositiveButton("확인", null)
							.show();

					// GET_COMMENT 다시 요청 (화면 갱신 위해서)
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
					dialog.setMessage("삭제되었습니다.")

							.setPositiveButton("확인",
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
			// 지역변수
			RequestData requestData;
			ClientNetworkThread networkThread;
			int page;

			switch (v.getId()) {
			case R.id.recommend_btn:
				// 내 DB 확인 => 내 DB INSERT => 서버 INSERT => 내 화면 갱신
				mDBManger.DBReadRecommend("recommend_news", mNewsId, mDBReaderHandler);
				break;
			case R.id.write_comment_btn:
				// 서버에 Comment Insert 요청
				Log.d(TAG, "Write Comment 버튼 클릭시 사용자의 NAME : " + mUserName);
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
				dialog.setMessage("삭제하시겠습니까?")
						.setNegativeButton("취소", null)
						.setPositiveButton("확인",
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

			/**************** 여기서부터는 페이지 버튼 **********************/
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
				// 변경된 Page. Server에 Request
				requestChangePage(mCurrentPage);
				break;
			case R.id.page_btn1:
				// 현재 페이지를 또 누른것이면 그냥 break;
				page = Integer.parseInt(mPageBtn[0].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server에 Request
				requestChangePage(page);
				break;
			case R.id.page_btn2:
				page = Integer.parseInt(mPageBtn[1].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server에 Request
				requestChangePage(page);
				break;
			case R.id.page_btn3:
				page = Integer.parseInt(mPageBtn[2].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server에 Request
				requestChangePage(page);
				break;
			case R.id.page_btn4:
				page = Integer.parseInt(mPageBtn[3].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server에 Request
				requestChangePage(page);
				break;
			case R.id.page_btn5:
				page = Integer.parseInt(mPageBtn[4].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server에 Request
				requestChangePage(page);
				break;
			case R.id.page_btn6:
				page = Integer.parseInt(mPageBtn[5].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server에 Request
				requestChangePage(page);
				break;
			case R.id.page_btn7:
				page = Integer.parseInt(mPageBtn[6].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server에 Request
				requestChangePage(page);
				break;
			case R.id.page_btn8:
				page = Integer.parseInt(mPageBtn[7].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server에 Request
				requestChangePage(page);
				break;
			case R.id.page_btn9:
				page = Integer.parseInt(mPageBtn[8].getText().toString());
				if (mCurrentPage == page - 1)
					break;

				// Server에 Request
				requestChangePage(page);
				break;
			case R.id.next_page:
				Log.d(TAG, "다음페이지 // mCurrentPage : " + mCurrentPage
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
				// 변경된 Page. Server에 Request
				requestChangePage(mCurrentPage + 2);
				break;

			}
		}
	};

	private void requestChangePage(int page) {
		// 페이지 Color 바꾸기
		Log.d(TAG, "page-1 : " + (page - 1) + ", 이전 페이지 : " + mCurrentPage);
		mPageBtn[(page % PAGE_SZIE) - 1]
				.setBackgroundResource(R.drawable.selected_page);
		for (int i = 0; i < PAGE_SZIE; i++) {
			if (i == page - 1)
				continue;
			mPageBtn[i].setBackgroundColor(Color.TRANSPARENT);
		}

		// 현재 페이지 변경
		mCurrentPage = page - 1;

		// Wait Dialog 띄우기
		// mNetworkWaiting.show();

		// 진행중인 Thread가 있을 경우 중지하여 화면 갱신 받지않기
		if (mRequestChangePage != null && mRequestChangePage.isAlive())
			mRequestChangePage.interrupt();

		// 페이지 변경 요청
		RequestData requestData = new RequestGetComment(
				RequestData.GET_COMMENT, mNewsId, mCurrentPage);
		mRequestChangePage = new ClientNetworkThread(MainActivity.HOST_ADDRESS,
				requestData, mClientNetworkHandler);
		mRequestChangePage.setDaemon(true);
		mRequestChangePage.start();

	}
}
