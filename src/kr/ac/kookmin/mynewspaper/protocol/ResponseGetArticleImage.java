/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class ResponseGetArticleImage extends ResponseData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = 4873568079925283225L;

	private boolean IsMyArticle;
	private byte[] ArticleImage;
	private int Recommend;



	public ResponseGetArticleImage(int work, int responseCode, boolean isMyArticle,
			byte[] articleImage, int recommend) {
		super(work, responseCode);
		IsMyArticle = isMyArticle;
		ArticleImage = articleImage;
		Recommend = recommend;
	}
	public int getRecommend() {
		return Recommend;
	}
	public void setRecommend(int recommend) {
		Recommend = recommend;
	}
	public boolean isIsMyArticle() {
		return IsMyArticle;
	}
	public void setIsMyArticle(boolean isMyArticle) {
		IsMyArticle = isMyArticle;
	}
	public byte[] getArticleImage() {
		return ArticleImage;
	}
	public void setArticleImage(byte[] articleImage) {
		ArticleImage = articleImage;
	}



}
