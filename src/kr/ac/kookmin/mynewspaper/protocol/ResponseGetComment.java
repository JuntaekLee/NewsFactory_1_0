/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

import java.sql.Timestamp;



/**
 * 
 **/
public class ResponseGetComment extends ResponseData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -7583039876666788449L;
	private String[] UserName;
	private String[] RegisterTime;
	private String[] Comment;
	private int SumOfComment;

	public ResponseGetComment(int work, int responseCode, String[] userName,
			String[] registerTime, String[] comment, int sumOfComment) {
		super(work, responseCode);
		UserName = userName;
		RegisterTime = registerTime;
		Comment = comment;
		SumOfComment = sumOfComment;
	}
	
	public int getSumOfComment() {
		return SumOfComment;
	}

	public void setSumOfComment(int sumOfComment) {
		SumOfComment = sumOfComment;
	}

	public String[] getUserName() {
		return UserName;
	}
	public void setUserName(String[] userName) {
		UserName = userName;
	}
	public String[] getRegisterTime() {
		return RegisterTime;
	}
	public void setRegisterTime(String[] registerTime) {
		RegisterTime = registerTime;
	}
	public String[] getComment() {
		return Comment;
	}
	public void setComment(String[] comment) {
		Comment = comment;
	}
	
	
	

}
