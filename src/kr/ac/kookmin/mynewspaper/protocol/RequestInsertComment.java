/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class RequestInsertComment extends RequestData {

	
	/**
	 * 
	 **/
	private static final long serialVersionUID = -1961138745418547281L;




	private String UserName;
	private long NewsId;
	private String Comment;
	public RequestInsertComment(int work, String userName, long newsId, String comment) {
		super(work);
		UserName = userName;
		NewsId = newsId;
		Comment = comment;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public long getNewsId() {
		return NewsId;
	}
	public void setNewsId(long newsId) {
		NewsId = newsId;
	}
	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
	

	
}
