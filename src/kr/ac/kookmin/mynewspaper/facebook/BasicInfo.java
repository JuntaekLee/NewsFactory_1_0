package kr.ac.kookmin.mynewspaper.facebook;

import java.text.SimpleDateFormat;

import android.graphics.Bitmap;

import com.facebook.android.Facebook;

/**
 * 클래스 : Facebook의 기본 정보 모음
 **/
public class BasicInfo {

	public static final int REQ_CODE_FACEBOOK_LOGIN = 1001;

	public static boolean FacebookLogin = false;
	public static boolean RetryLogin = false;

	public static Facebook FacebookInstance = null;

	public static String[] FACEBOOK_PERMISSIONS = {"publish_stream", "read_stream", "user_photos", "email"};

	public static String FACEBOOK_ACCESS_TOKEN = "";
	public static String FACEBOOK_APP_ID = "517022775031674 ";
	public static String FACEBOOK_API_KEY = "517022775031674 ";
	public static String FACEBOOK_APP_SECRET = "bb69a9d8f17e95916b8532126a9930d";

	public static String FACEBOOK_NAME = "";
	public static String FACEBOOK_EMAIL = "";

	public static SimpleDateFormat OrigDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
	public static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");

	public static Bitmap BasicPicture = null;
}
