/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class RequestGetNormalArticle extends RequestData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = 7228811635888606352L;

	public static int ARTICLE_NUMBER = 9;
	
	private int Category;
	private int Page;
	
	public RequestGetNormalArticle(int work, int category, int page) {
		super(work);
		Category = category;
		Page = page;

	}
	public int getCategory() {
		return Category;
	}
	public void setCategory(int category) {
		Category = category;
	}
	public int getPage() {
		return Page;
	}
	public void setPage(int page) {
		Page = page;
	}

	
	
}
