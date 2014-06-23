/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class RequestGetArticleImage extends RequestData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -7530999509031839889L;

	private long NewsId;
	private String UserName;



	public RequestGetArticleImage(int work, long newsId, String userId) {
		super(work);
		NewsId = newsId;
		UserName = userId;
	}

	public long getNewsId() {
		return NewsId;
	}

	public void setNewsId(long newsId) {
		NewsId = newsId;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}



}
