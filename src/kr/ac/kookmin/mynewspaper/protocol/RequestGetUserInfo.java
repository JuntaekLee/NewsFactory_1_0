/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class RequestGetUserInfo extends RequestData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -5947186582491696820L;
	private String UserName;

	public RequestGetUserInfo(int work, String userName) {
		super(work);
		UserName = userName;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

}
