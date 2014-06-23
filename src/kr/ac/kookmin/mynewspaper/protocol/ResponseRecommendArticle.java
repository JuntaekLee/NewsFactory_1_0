/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class ResponseRecommendArticle extends ResponseData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -2304235032826893883L;

	private int Recommend;


	public ResponseRecommendArticle(int work, int responseCode, int recommend) {
		super(work, responseCode);
		Recommend = recommend;
	}

	public int getRecommend() {
		return Recommend;
	}

	public void setRecommend(int recommend) {
		Recommend = recommend;
	}


}
