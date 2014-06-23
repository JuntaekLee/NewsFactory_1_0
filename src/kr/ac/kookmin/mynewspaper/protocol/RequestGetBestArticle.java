/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class RequestGetBestArticle extends RequestData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -6753384181903604676L;

	private int Category;

	public RequestGetBestArticle(int work, int category) {
		super(work);
		Category = category;
	}

	public int getCategory() {
		return Category;
	}

	public void setCategory(int category) {
		Category = category;
	}

}
