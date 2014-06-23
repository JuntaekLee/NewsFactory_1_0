/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

import java.util.ArrayList;

/**
 * 
 **/
public class ResponseGetBestArticle extends ResponseData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -6482437058922836702L;

	private long[] NewsId;
	private String[] NewsTitle;
	private ArrayList<byte[]> PreviewImage;


	public ResponseGetBestArticle(int work, int responseCode, long[] newsId,
			String[] newsTitle, ArrayList<byte[]> previewImage) {
		super(work, responseCode);
		NewsId = newsId;
		NewsTitle = newsTitle;
		PreviewImage = previewImage;
	}
	public long[] getNewsId() {
		return NewsId;
	}
	public void setNewsId(long[] newsId) {
		NewsId = newsId;
	}
	public String[] getNewsTitle() {
		return NewsTitle;
	}
	public void setNewsTitle(String[] newsTitle) {
		NewsTitle = newsTitle;
	}
	public ArrayList<byte[]> getPreviewImage() {
		return PreviewImage;
	}
	public void setPreviewImage(ArrayList<byte[]> previewImage) {
		PreviewImage = previewImage;
	}

	
	
}
