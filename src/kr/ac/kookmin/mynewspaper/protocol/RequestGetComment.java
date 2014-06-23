/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class RequestGetComment extends RequestData {


	/**
	 * 
	 **/
	private static final long serialVersionUID = 5975119564352740734L;

	public static final int COMMENT_NUMBER = 7;
	
	private long NewsId;
	private int Page;



	public RequestGetComment(int work, long newsId, int page) {
		super(work);
		NewsId = newsId;
		Page = page;
	}

	public int getPage() {
		return Page;
	}

	public void setPage(int page) {
		Page = page;
	}

	public long getNewsId() {
		return NewsId;
	}

	public void setNewsId(long newsId) {
		NewsId = newsId;
	}
	
	
}
