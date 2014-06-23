/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

import java.io.Serializable;

/**
 * 
 **/
public abstract class RequestData implements Serializable {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -3758775280734329916L;

	// 작업 종류
	public static final int GET_BEST_ARTICLE = 0;
	public static final int GET_NORMAL_ARTICLE = 1;
	public static final int INSERT_ARTICLE = 2;
	public static final int INSERT_COMMENT = 3;
	public static final int GET_COMMENT = 4;
	public static final int DEL_ARTICLE = 5;
	public static final int DEL_COMMENT = 6;
	public static final int RECOMMEND_ARTICLE = 7;
	public static final int READ_ARTICLE = 8;
	public static final int GET_USER_INFO = 9;

	private int RequestCode;


	public RequestData(int requestCode) {
		super();
		RequestCode = requestCode;
	}

	
	public int getRequestCode() {
		return RequestCode;
	}


	public void setRequestCode(int requestCode) {
		RequestCode = requestCode;
	}


	// 껍데기 함수
	public int getCategory() {
		return -1;
	}

	public void setCategory(int category) {
	}

	public int getPage() {
		return -1;
	}

	public void setPage(int page) {
	}

	public byte[] getMainImage() {
		return null;
	}

	public void setMainImage(byte[] mainImage) {

	}

	public byte[] getPreviewImage() {
		return null;
	}

	public void setPreviewImage(byte[] previewImage) {

	}

	public String getNewsTitle() {
		return null;
	}

	public void setNewsTitle(String newsTitle) {

	}

	public String getNewsOwner() {
		return null;
	}

	public void setNewsOwner(String newsOwner) {

	}

	public long getNewsId() {
		return -1L;
	}

	public void setNewsId(long newsId) {

	}
	
	public String getUserName() {
		return null;
	}

	public void setUserName(String userName) {
	}

	public String getComment() {
		return null;
	}
	public void setComment(String comment) {

	}


}
