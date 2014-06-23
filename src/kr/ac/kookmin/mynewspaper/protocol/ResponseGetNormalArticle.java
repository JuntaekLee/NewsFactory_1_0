/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class ResponseGetNormalArticle extends ResponseData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -1521314607780274570L;

	private long[] NewsId;
	private String[] NewsTitle;
	private long SumOfArticle;


	public ResponseGetNormalArticle(int work, int responseCode, long[] newsId,
			String[] newsTitle, long sumOfArticle) {
		super(work, responseCode);
		NewsId = newsId;
		NewsTitle = newsTitle;
		SumOfArticle = sumOfArticle;
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
	public long getSumOfArticle() {
		return SumOfArticle;
	}
	public void setSumOfArticle(long sumOfArticle) {
		SumOfArticle = sumOfArticle;
	}
	
}
