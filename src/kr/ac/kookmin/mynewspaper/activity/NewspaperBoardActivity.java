/**
 * 
 */
package kr.ac.kookmin.mynewspaper.activity;

import java.util.ArrayList;

import kr.ac.kookmin.appengine.NetworkThread;
import kr.ac.kookmin.appengine.RequestMethod;
import kr.ac.kookmin.appengine.RestClient;
import kr.ac.kookmin.mynewspaper.ArticleListAdapter;
import kr.ac.kookmin.mynewspaper.ArticleRankView;
import kr.ac.kookmin.mynewspaper.ClientNetworkThread;
import kr.ac.kookmin.mynewspaper.R;
import kr.ac.kookmin.mynewspaper.facebook.BasicInfo;
import kr.ac.kookmin.mynewspaper.facebook.FacebookRequestThread;
import kr.ac.kookmin.mynewspaper.protocol.RequestData;
import kr.ac.kookmin.mynewspaper.protocol.RequestGetBestArticle;
import kr.ac.kookmin.mynewspaper.protocol.RequestGetNormalArticle;
import kr.ac.kookmin.mynewspaper.protocol.ResponseData;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

/**
 * ��Ƽ��Ƽ : �ٸ� ������� ��縦 ���ų� �� ��縦 �ø��� �Խ��� ȭ��
 **/
public class NewspaperBoardActivity extends Activity {
	private static final String TAG = "NewspaperBoardActivity";

	// Activity Request Code
	public static int REQUEST_READ_ARTICLE = 100;
	public static int REQUEST_UPLOAD_ARTICLE = 200;

	// �迭&List ũ��
	public static final int PAGE_SZIE = 9;
	public static final int RANK_SIZE = 3;

	// ����
	public static final int SELECTED_PAGE_COLOR = Color.rgb(237, 114, 39);
	public static final int NORMAL_PAGE_COLOR = Color.BLACK;

	// ī�װ� ���
	public static final int NEWS_CATEGORY = 0;
	public static final int ENTERTAINMENT_CATEGORY = 1;
	public static final int SPORT_CATEGORY = 2;
	public static final int COLUMN_CATEGORY = 3;
	public static final int FUN_CATEGORY = 4;

	// ���� Page & Category
	private int mCurrentCategory = NEWS_CATEGORY;
	private int mCurrentPage = 0;
	private int mSumOfPage = 0;

	// �Ϲ� ��� ����Ʈ
	private ListView mArticleList;
	private ArticleListAdapter mArticleListAdapter;
	private ArrayList<String> mArticleTitleList;

	// View
	private RadioGroup mCategoryGroup;
	private Button mFacebookLogin;
	private Button mUploadArticle;
	private ArticleRankView mArticleRankView;
	private LinearLayout mArticleRank1;
	private LinearLayout mArticleRank2;
	private LinearLayout mArticleRank3;
	private Button[] mPageBtn;
	private Button mNextPage;
	private Button mPreviousPage;
	private FrameLayout mNormalArticleFrameLayout;
	private ProgressBar mNetworkProgressBar;

	// �Ϲ� ��� id ����
	private long[] mNormalArticleId;
	private long[] mBestArticleId;

	// �� ��� ��
	private long mSumOfArticle = 0L;

	// ��Ʈ��ũ ProgressDialog
	// private ProgressDialog mNetworkWaiting;

	// ��Ʈ��ũ Thread
	private ClientNetworkThread mRequestBestArticle;
	private ClientNetworkThread mRequestNormalArticle;

	Handler networkHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			Toast.makeText(getApplicationContext(), bundle.getString("�׽�Ʈ"), Toast.LENGTH_LONG).show();
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_newspaper_board);

		// �ʱ�ȭ
		init();
		
		// ���۾ۿ���




		// ��ư&�� ������ ���
		setListener();

		
		/*
		// ���� ī�װ� ù������ ����ֱ�
		mCategoryGroup.getChildAt(0).performClick();
		/*
	}

	@Override
	protected void onDestroy() {
		// ImageView�� Bitmap ����
		/*
		 * ArrayList<ImageView> arIv = mArticleRankView.getArticleRankImage();
		 * for (int i = 0; i < arIv.size(); i++) {
		 * BitmapCreator.recycleImageViewBitmap(arIv.get(i)); }
		 */
		super.onDestroy();

	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent resultIntent) {
		super.onActivityResult(requestCode, resultCode, resultIntent);

		if (resultCode == RESULT_OK) {
			if (requestCode == 32665) {
				Log.d(TAG, "BasicInfo.FacebookInstance.authorizeCallback ȣ��");
				BasicInfo.FacebookInstance.authorizeCallback(requestCode,
						resultCode, resultIntent);
			} else if (requestCode == REQUEST_READ_ARTICLE) {
				// ���� �Ǿ��� �� ����Ʈ ����
				requestBestArticle(mCurrentCategory);
				requestNormalArticle(mCurrentPage + 1);
			} else if (requestCode == REQUEST_UPLOAD_ARTICLE) {
				Log.d(TAG, "ReuqetsCode : REQUEST_UPLOAD_ARTICLE");
				// ���ε� �Ǿ��� �� ����Ʈ ����
				requestBestArticle(mCurrentCategory);
				requestNormalArticle(mCurrentPage + 1);
			}
		}
	}

	protected void onPause() {
		super.onPause();

		saveProperties();
	}

	protected void onResume() {
		super.onResume();

		loadProperties();
		if (BasicInfo.FacebookLogin == true) {
			mFacebookLogin
					.setBackgroundResource(R.drawable.press_facebook_logout_btn);
		}

	}

	private void saveProperties() {
		SharedPreferences pref = getSharedPreferences("FACEBOOK", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean("FacebookLogin", BasicInfo.FacebookLogin);
		editor.putString("FACEBOOK_ACCESS_TOKEN",
				BasicInfo.FACEBOOK_ACCESS_TOKEN);
		editor.putString("FACEBOOK_NAME", BasicInfo.FACEBOOK_NAME);
		editor.putString("FACEBOOK_EMAIL", BasicInfo.FACEBOOK_EMAIL);
		editor.commit();
		Log.d(TAG, "saveProperties Ŀ�ԿϷ�");
	}

	private void loadProperties() {
		SharedPreferences pref = getSharedPreferences("FACEBOOK", MODE_PRIVATE);

		BasicInfo.FacebookLogin = pref.getBoolean("FacebookLogin", false);
		BasicInfo.FACEBOOK_ACCESS_TOKEN = pref.getString(
				"FACEBOOK_ACCESS_TOKEN", "");
		BasicInfo.FACEBOOK_NAME = pref.getString("FACEBOOK_NAME", "");
		BasicInfo.FACEBOOK_EMAIL = pref.getString("FACEBOOK_EMAIL", "");
		Log.d(TAG, "loadProperties �ε�Ϸ� FACEBOOK_NAME : "
				+ BasicInfo.FACEBOOK_NAME + ", EMAIL : "
				+ BasicInfo.FACEBOOK_EMAIL + ", FACEBOOK_ACCESS_TOKEN : "
				+ BasicInfo.FACEBOOK_ACCESS_TOKEN + ", Login : "
				+ BasicInfo.FacebookLogin);
	}

	private void setListener() {
		// Button & Rank ������ ���
		mCategoryGroup.setOnCheckedChangeListener(mRadioCheckListener);
		mFacebookLogin.setOnClickListener(onBtnClickListener);
		mUploadArticle.setOnClickListener(onBtnClickListener);
		mArticleRank1.setOnClickListener(onBtnClickListener);
		mArticleRank2.setOnClickListener(onBtnClickListener);
		mArticleRank3.setOnClickListener(onBtnClickListener);
		for (int i = 0; i < PAGE_SZIE; i++) {
			mPageBtn[i].setOnClickListener(onBtnClickListener);
		}
		mNextPage.setOnClickListener(onBtnClickListener);
		mPreviousPage.setOnClickListener(onBtnClickListener);

		// ListView ������ ���
		mArticleList.setOnItemClickListener(mOnItemClickListener);

	}

	private void init() {
		// View ��üȭ
		mNormalArticleFrameLayout = (FrameLayout) findViewById(R.id.normal_article_frame_layout);
		mCategoryGroup = (RadioGroup) findViewById(R.id.category_group);
		mArticleList = (ListView) findViewById(R.id.article_list);
		mFacebookLogin = (Button) findViewById(R.id.facebook_login_btn);
		mUploadArticle = (Button) findViewById(R.id.upload_article_btn);
		mArticleRank1 = (LinearLayout) findViewById(R.id.article_rank1);
		mArticleRank2 = (LinearLayout) findViewById(R.id.article_rank2);
		mArticleRank3 = (LinearLayout) findViewById(R.id.article_rank3);
		mArticleRankView = new ArticleRankView(new ArrayList<ImageView>(),
				new ArrayList<TextView>());
		mArticleRankView.getArticleRankImage().add(
				(ImageView) findViewById(R.id.article_rank1_image));
		mArticleRankView.getArticleRankImage().add(
				(ImageView) findViewById(R.id.article_rank2_image));
		mArticleRankView.getArticleRankImage().add(
				(ImageView) findViewById(R.id.article_rank3_image));
		mArticleRankView.getArticleRankText().add(
				(TextView) findViewById(R.id.article_rank1_text));
		mArticleRankView.getArticleRankText().add(
				(TextView) findViewById(R.id.article_rank2_text));
		mArticleRankView.getArticleRankText().add(
				(TextView) findViewById(R.id.article_rank3_text));

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

		// ����� �غ� & ����
		mArticleTitleList = new ArrayList<String>();
		mArticleListAdapter = new ArticleListAdapter(
				mArticleTitleList,
				(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		mArticleList.setAdapter(mArticleListAdapter);

	}

	private void requestChangeCategory(int category) {
		// 1�������� ����
		mCurrentPage = 0;
		mPageBtn[0].performClick();

		requestBestArticle(category);

		requestNormalArticle(mCurrentPage + 1);
	}

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

		requestNormalArticle(page);
	}

	private void requestBestArticle(int category) {
		// ī�װ� ����
		mCurrentCategory = category;

		// �������� Thread�� ���� ��� �����ϱ�
		if (mRequestBestArticle != null && mRequestBestArticle.isAlive())
			mRequestBestArticle.interrupt();
		// Best ��� ��������
		RequestData requestData = new RequestGetBestArticle(
				RequestData.GET_BEST_ARTICLE, mCurrentCategory);
		mRequestBestArticle = new ClientNetworkThread(
				MainActivity.HOST_ADDRESS, requestData, mClientNetworkHandler);
		mRequestBestArticle.setDaemon(true);
		mRequestBestArticle.start();
	}

	private void requestNormalArticle(int page) {
		// ���α׷����� ����
		mNetworkProgressBar = new ProgressBar(NewspaperBoardActivity.this);
		FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.CENTER);
		mNetworkProgressBar.setLayoutParams(param);
		mNetworkProgressBar.setVisibility(View.VISIBLE);
		mNormalArticleFrameLayout.addView(mNetworkProgressBar);

		// mCurrentPage�� �ε����� 0���� �����̹Ƿ�
		mCurrentPage = page - 1;

		// Wait Dialog ����
		// mNetworkWaiting.show();

		// �������� Thread�� ���� ��� �����ϱ�
		if (mRequestNormalArticle != null && mRequestNormalArticle.isAlive())
			mRequestNormalArticle.interrupt();
		// Normal ��� ��������
		RequestData requestData = new RequestGetNormalArticle(
				RequestData.GET_NORMAL_ARTICLE, mCurrentCategory, mCurrentPage);
		mRequestNormalArticle = new ClientNetworkThread(
				MainActivity.HOST_ADDRESS, requestData, mClientNetworkHandler);
		mRequestNormalArticle.setDaemon(true);
		mRequestNormalArticle.start();
	}

	private void connect() {
		try {
			Facebook mFacebook = new Facebook(BasicInfo.FACEBOOK_APP_ID);
			BasicInfo.FacebookInstance = mFacebook;

			if (!BasicInfo.RetryLogin && BasicInfo.FacebookLogin == true) {
				Log.d(TAG,
						"!BasicInfo.RetryLogin && BasicInfo.FacebookLogin == true ���");
				mFacebook.setAccessToken(BasicInfo.FACEBOOK_ACCESS_TOKEN);
				// showUserTimeline();

				// �α׾ƿ��ϱ�
				FacebookRequestThread requestThread = new FacebookRequestThread(
						FacebookRequestThread.LOGOUT,
						NewspaperBoardActivity.this, mFacebookRequestHandler);
				requestThread.setDaemon(true);
				requestThread.start();

			} else {
				Log.d(TAG, "mFacebook.authorize ȣ�� ��");
				mFacebook.authorize(this, BasicInfo.FACEBOOK_PERMISSIONS,
						new AuthorizationListener());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Handler mFacebookRequestHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// logout
			case FacebookRequestThread.LOGOUT:
				Toast.makeText(NewspaperBoardActivity.this, "�α׾ƿ��Ǿ����ϴ�.",
						Toast.LENGTH_LONG).show();
				// SharedPreferences ���� ����
				SharedPreferences pref = getSharedPreferences("FACEBOOK",
						MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.clear();
				editor.commit();

				// ���̽��� ���� �ٽ� ����
				loadProperties();

				// UI ����
				mFacebookLogin
						.setBackgroundResource(R.drawable.facebook_login_btn);
				break;
			case FacebookRequestThread.REQUEST_ME:
				mFacebookLogin
						.setBackgroundResource(R.drawable.press_facebook_logout_btn);
				break;
			}
		}
	};

	private Handler mClientNetworkHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			Log.d(TAG, "mClientNetworkHandler ����");
			ResponseData responseData = (ResponseData) msg.obj;
			if (responseData.getResponseCode() == ResponseData.RESULT_OK) {
				switch (responseData.getRequestCode()) {
				case RequestData.GET_NORMAL_ARTICLE:
					// ���α׷����� ����
					mNetworkProgressBar.setVisibility(View.INVISIBLE);

					// Response ������ ����
					String[] news_title = responseData.getNewsTitle();
					mNormalArticleId = responseData.getNewsId();
					mArticleListAdapter.getmArticleTitleList().clear();

					// ȭ�� ����
					for (int i = 0; i < news_title.length; i++) {
						mArticleListAdapter.getmArticleTitleList().add(
								news_title[i]);
					}
					mArticleListAdapter.notifyDataSetChanged();

					// ������ ��ư �� ����
					mSumOfArticle = responseData.getSumOfArticle();
					mSumOfPage = (int) ((mSumOfArticle / (long) RequestGetNormalArticle.ARTICLE_NUMBER) + 1L);
					Log.d(TAG, "�� ������ �� : " + mSumOfPage);
					if (mSumOfArticle > 0) {
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
				case RequestData.GET_BEST_ARTICLE:

					// Response ������ ����
					Bitmap bm = null;
					ArrayList<byte[]> previewList = responseData
							.getPreviewImage();
					news_title = responseData.getNewsTitle();
					mBestArticleId = responseData.getNewsId();

					Log.d(TAG, "���⿩�⿩�� news_title.length : "
							+ news_title.length);
					// ȭ�� ����
					for (int i = 0; i < news_title.length; i++) {
						Log.d(TAG, "���⿩�⿩�� news_title : " + news_title[i]);

						mArticleRankView.getArticleRankText().get(i)
								.setText(news_title[i]);

						if (previewList.get(i) == null) {
							Log.d(TAG, "������ �� i : " + i);
							continue;
						}
						bm = BitmapFactory.decodeByteArray(previewList.get(i),
								0, previewList.get(i).length);
						mArticleRankView.getArticleRankImage().get(i)
								.setImageBitmap(bm);

					}
					break;

				}
			}
		}

	};

	private RadioGroup.OnCheckedChangeListener mRadioCheckListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (group.getId() == R.id.category_group) {
				switch (checkedId) {
				case R.id.general_article_btn:
					// ī�װ� ���� �� ù������ ��û
					// mIsLoadComplete = false;
					mCurrentCategory = NEWS_CATEGORY;
					requestChangeCategory(mCurrentCategory);

					// Rank View�� �̹����� Ÿ��Ʋ ����
					for (int i = 0; i < RANK_SIZE; i++) {
						mArticleRankView.getArticleRankImage().get(i)
								.setImageResource(R.drawable.no_image);
						mArticleRankView.getArticleRankText().get(i)
								.setText("");
					}
					break;
				case R.id.entertainment_article_btn:
					// ī�װ� ���� �� ù������ ��û
					// mIsLoadComplete = false;
					mCurrentCategory = ENTERTAINMENT_CATEGORY;
					requestChangeCategory(mCurrentCategory);

					// Rank View�� �̹����� Ÿ��Ʋ ����
					for (int i = 0; i < RANK_SIZE; i++) {
						mArticleRankView.getArticleRankImage().get(i)
								.setImageResource(R.drawable.no_image);
						mArticleRankView.getArticleRankText().get(i)
								.setText("");
					}
					break;
				case R.id.sport_article_btn:
					// ī�װ� ���� �� ù������ ��û
					// mIsLoadComplete = false;
					mCurrentCategory = SPORT_CATEGORY;
					requestChangeCategory(mCurrentCategory);

					// Rank View�� �̹����� Ÿ��Ʋ ����
					for (int i = 0; i < RANK_SIZE; i++) {
						mArticleRankView.getArticleRankImage().get(i)
								.setImageResource(R.drawable.no_image);
						mArticleRankView.getArticleRankText().get(i)
								.setText("");
					}
					break;
				case R.id.column_article_btn:
					// ī�װ� ���� �� ù������ ��û
					// mIsLoadComplete = false;
					mCurrentCategory = COLUMN_CATEGORY;
					requestChangeCategory(mCurrentCategory);

					// Rank View�� �̹����� Ÿ��Ʋ ����
					for (int i = 0; i < RANK_SIZE; i++) {
						mArticleRankView.getArticleRankImage().get(i)
								.setImageResource(R.drawable.no_image);
						mArticleRankView.getArticleRankText().get(i)
								.setText("");
					}
					break;
				case R.id.fun_article_btn:
					// ī�װ� ���� �� ù������ ��û
					// mIsLoadComplete = false;
					mCurrentCategory = FUN_CATEGORY;
					requestChangeCategory(mCurrentCategory);

					// Rank View�� �̹����� Ÿ��Ʋ ����
					for (int i = 0; i < RANK_SIZE; i++) {
						mArticleRankView.getArticleRankImage().get(i)
								.setImageResource(R.drawable.no_image);
						mArticleRankView.getArticleRankText().get(i)
								.setText("");
					}
					break;
				}
			}

		}
	};

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, "mOnItemClickListener Ŭ�� �׸� : " + position);
			Intent intent;
			intent = new Intent(NewspaperBoardActivity.this,
					ReadOtherArticleActivity.class);
			intent.putExtra("NewsId", mNormalArticleId[position]);
			intent.putExtra("UserName", BasicInfo.FACEBOOK_EMAIL);
			startActivityForResult(intent, REQUEST_READ_ARTICLE);

		}

	};

	private OnClickListener onBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent;
			int page;
			switch (v.getId()) {
			case R.id.article_rank1:
				if (mBestArticleId != null && mBestArticleId.length > 0) {
					Log.d(TAG, "article_rank1 Ŭ��");
					intent = new Intent(NewspaperBoardActivity.this,
							ReadOtherArticleActivity.class);
					intent.putExtra("NewsId", mBestArticleId[0]);
					intent.putExtra("UserName", BasicInfo.FACEBOOK_EMAIL);
					startActivityForResult(intent, REQUEST_READ_ARTICLE);
				}
				break;
			case R.id.article_rank2:
				if (mBestArticleId != null && mBestArticleId.length > 1) {
					Log.d(TAG, "article_rank2Ŭ��");
					intent = new Intent(NewspaperBoardActivity.this,
							ReadOtherArticleActivity.class);
					intent.putExtra("NewsId", mBestArticleId[1]);
					intent.putExtra("UserName", BasicInfo.FACEBOOK_EMAIL);
					startActivityForResult(intent, REQUEST_READ_ARTICLE);
				}
				break;
			case R.id.article_rank3:
				if (mBestArticleId != null && mBestArticleId.length > 2) {
					Log.d(TAG, "article_rank3 Ŭ��");
					intent = new Intent(NewspaperBoardActivity.this,
							ReadOtherArticleActivity.class);
					intent.putExtra("NewsId", mBestArticleId[2]);
					intent.putExtra("UserName", BasicInfo.FACEBOOK_EMAIL);
					startActivityForResult(intent, REQUEST_READ_ARTICLE);
				}
				break;
			case R.id.upload_article_btn:
				// �α����� �Ǿ����� ������ ���ε� ����
				if (BasicInfo.FacebookLogin == false) {
					AlertDialog.Builder builder = new Builder(
							NewspaperBoardActivity.this);
					builder.setTitle("�˸�").setMessage("�α��κ��� ���ּ���.")
							.setPositiveButton("Ȯ��", null);
				}
				// ���ε��� ��� �����ϴ� ��Ƽ��Ƽ ȣ��
				else {
					Log.d(TAG, "upload_article_btn ����");
					intent = new Intent(NewspaperBoardActivity.this,
							SelectMyArticleActivity.class);
					intent.putExtra("Work", SelectMyArticleActivity.UPLOAD);
					intent.putExtra("UserName", BasicInfo.FACEBOOK_EMAIL);
					intent.putExtra("Category", mCurrentCategory);
					startActivityForResult(intent, REQUEST_UPLOAD_ARTICLE);
				}
				break;
			case R.id.facebook_login_btn:
				// ���̽��Ͽ� �����û
				connect();
				break;

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

	public class AuthorizationListener implements DialogListener {

		public void onCancel() {

		}

		public void onComplete(Bundle values) {
			Log.d(TAG, "AuthorizationListener ��  onComplete ȣ��");
			FacebookRequestThread requestThread = new FacebookRequestThread(
					FacebookRequestThread.REQUEST_ME, mFacebookRequestHandler);
			requestThread.setDaemon(true);
			requestThread.start();

			// showUserTimeline();

		}

		public void onError(DialogError e) {

		}

		public void onFacebookError(FacebookError e) {

		}

	}
}
