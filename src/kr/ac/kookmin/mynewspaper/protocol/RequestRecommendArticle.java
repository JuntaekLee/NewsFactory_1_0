/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class RequestRecommendArticle extends RequestData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = 4305898362000811381L;

	private long NewsId;



	public RequestRecommendArticle(int work, long newsId) {
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
