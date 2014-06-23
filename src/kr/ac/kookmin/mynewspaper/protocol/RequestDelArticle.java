/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class RequestDelArticle extends RequestData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -7560273395299111284L;
	private long NewsId;
	public RequestDelArticle(int work, long newsId) {
		super(work);
		NewsId = newsId;
	}
	public long getNewsId() {
		return NewsId;
	}
	public void setNewsId(long newsId) {
		NewsId = newsId;
	}



	
}
