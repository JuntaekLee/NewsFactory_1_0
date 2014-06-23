/**
 * 
 */
package kr.ac.kookmin.mynewspaper.facebook;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * 스레드 : 페이스북에 Request 메시지를 보내는 스레드
 **/
public class FacebookRequestThread extends Thread {
	private static final String TAG = "FacebookRequestThread";

	public static final int REQUEST_ME = 0;
	public static final int LOGOUT = 1;
	public static final int UPLOAD = 2;

	private int mWork = -1;
	private Context mContext = null;
	private Handler mFacebookRequestHandler;
	private Bundle mParams = null;

	public FacebookRequestThread(int mWork, Handler mFacebookRequestHandler) {
		super();
		this.mWork = mWork;
		this.mContext = null;
		this.mFacebookRequestHandler = mFacebookRequestHandler;
		mParams = null;
	}

	public FacebookRequestThread(int mWork, Context mContext,
			Handler mFacebookRequestHandler) {
		super();
		this.mWork = mWork;
		this.mContext = mContext;
		this.mFacebookRequestHandler = mFacebookRequestHandler;
	}

	public FacebookRequestThread(int mWork, Context mContext,
			Handler mFacebookRequestHandler, Bundle mParams) {
		super();
		this.mWork = mWork;
		this.mContext = mContext;
		this.mFacebookRequestHandler = mFacebookRequestHandler;
		this.mParams = mParams;
	}

	@Override
	public void run() {
		try {
			// request work
			if (mWork == REQUEST_ME) {
				String resultStr = BasicInfo.FacebookInstance.request("me");
				Log.d(TAG, "request 결과 resultStr : " + resultStr);
				JSONObject obj = new JSONObject(resultStr);
				Log.d(TAG, "obj.getString(email) : " + obj.getString("email"));
				BasicInfo.FACEBOOK_NAME = obj.getString("name");
				BasicInfo.FACEBOOK_EMAIL = obj.getString("email");

				BasicInfo.FacebookLogin = true;
				BasicInfo.FACEBOOK_ACCESS_TOKEN = BasicInfo.FacebookInstance
						.getAccessToken();

				mFacebookRequestHandler.sendEmptyMessage(REQUEST_ME);
				Log.d(TAG, "BasicInfo.FACEBOOK_ACCESS_TOKEN : "
						+ BasicInfo.FACEBOOK_ACCESS_TOKEN);
			} else if (mWork == LOGOUT) {
				BasicInfo.FacebookInstance.logout(mContext);
				mFacebookRequestHandler.sendEmptyMessage(LOGOUT);
			} else if (mWork == UPLOAD) {
				Log.d(TAG, "페이스북 업로드!");
				String resultStr = BasicInfo.FacebookInstance.request(
						"me/photos", mParams, "POST");
				Log.d(TAG, "request 결과 resultStr : " + resultStr);
				mFacebookRequestHandler.sendEmptyMessage(UPLOAD);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
