/**
 * 
 */
package kr.ac.kookmin.mynewspaper;

/**
 * 클래스 : 댓글 어댑터에 Item이 될 내용
 **/
public class ClientComment {

	// Data
	private String UserName;
	private String Comment;
	private String RegisterTime;

	public ClientComment(String userName, String comment, String registerTime) {
		super();
		UserName = userName;
		Comment = comment;
		RegisterTime = registerTime;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public String getRegisterTime() {
		return RegisterTime;
	}

	public void setRegisterTime(String registerTime) {
		RegisterTime = registerTime;
	}

}
