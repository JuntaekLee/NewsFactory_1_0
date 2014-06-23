/**
 * 
 */
package kr.ac.kookmin.mynewspaper.activity;

import kr.ac.kookmin.appengine.NetworkThread;
import kr.ac.kookmin.mynewspaper.R;
import kr.ac.kookmin.mynewspaper.facebook.BasicInfo;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 액티비티 : 다른 사람들의 기사를 보거나 내 기사를 올리는 게시판 화면
 **/
public class BoardActivity extends Activity {
	private static final String TAG = "BoardActivity";
	
	private TextView mBoardTv;
	private EditText mWriteEt;
	
private Button mUploadBtn;
	
	Handler networkHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			
			mBoardTv.setText(bundle.getString("board"));
		//	Toast.makeText(getApplicationContext(), bundle.getString("board"), Toast.LENGTH_LONG).show();
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_board);
		
		if(BasicInfo.FacebookLogin == false){
			Toast.makeText(getApplicationContext(), "페이스북 로그인 먼저 해주세요", Toast.LENGTH_LONG).show();
			finish();
		}
		mUploadBtn = (Button) findViewById(R.id.upload_btn);
		mWriteEt = (EditText) findViewById(R.id.write_et);
		mBoardTv = (TextView) findViewById(R.id.board_tv);
		//Toast.makeText(getApplicationContext(), BasicInfo.FACEBOOK_NAME, Toast.LENGTH_LONG).show();
		mUploadBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 구글앱엔진
				NetworkThread netThread = new NetworkThread(networkHandler, mWriteEt.getText().toString(), BasicInfo.FACEBOOK_NAME);
				netThread.start();
				
			}
		});
		
		NetworkThread defaultThread = new NetworkThread(networkHandler, null, BasicInfo.FACEBOOK_NAME);
		defaultThread.start();
		
		
		


	
	}

	
}
