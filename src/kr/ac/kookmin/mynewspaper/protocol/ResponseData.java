/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 **/
public abstract class ResponseData implements Serializable {

	/**
	 * 
	 **/
	private static final long serialVersionUID = 2064630605212162172L;

	public static final int RESULT_OK = 200;

	private int RequestCode;
	private int ResponseCode;



	public ResponseData(int requestCode, int responseCode) {
		super();
		RequestCode = requestCode;
		ResponseCode = responseCode;
	}

	public int getResponseCode() {
		return ResponseCode;
	}

	public void setResponseCode(int responseCode) {
		ResponseCode = responseCode;
	}



	public int getRequestCode() {
		return RequestCode;
	}

	public void setRequestCode(int requestCode) {
		RequestCode = requestCode;
	}

	// ²®µ¥±â ÇÔ¼ö
	public long[] getNewsId() {
		return null;
	}

	public void setNewsId(long[] newsId) {
	}

	public String[] getNewsTitle() {
		return null;
	}

	public void setNewsTitle(String[] newsTitle) {

	}

	public ArrayList<byte[]> getPreviewImage() {
		return null;
	}

	public void setPreviewImage(ArrayList<byte[]> previewImage) {
	}

	public long getSumOfArticle() {
		return -1L;
	}

	public void setSumOfArticle(long sumOfArticle) {

	}

	public boolean isIsMyArticle() {
		return false;
	}

	public void setIsMyArticle(boolean isMyArticle) {

	}

	public byte[] getArticleImage() {
		return null;
	}

	public void setArticleImage(byte[] articleImage) {

	}

	public int getRecommend() {
		return -1;
	}

	public void setRecommend(int recommend) {

	}

	public String[] getUserName() {
		return null;
	}

	public void setUserName(String[] userName) {

	}

	public String[] getRegisterTime() {
		return null;
	}

	public void setRegisterTime(String[] registerTime) {

	}

	public String[] getComment() {
		return null;
	}

	public void setComment(String[] comment) {

	}

	public int getSumOfComment() {
		return -1;
	}

	public void setSumOfComment(int sumOfComment) {

	}

	public long getSumOfMyArticle() {
		return -1L;
	}

	public void setSumOfMyArticle(long sumOfMyArticle) {

	}

	public long getSumOfRecommend() {
		return -1L;
	}

	public void setSumOfRecommend(long sumOfRecommend) {

	}
}
