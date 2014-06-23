/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class RequestInsertArticle extends RequestData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = 2336345944153492937L;

	private byte[] MainImage;
	private byte[] PreviewImage;
	private String NewsTitle;
	private int Category;
	private String NewsOwner;
	public RequestInsertArticle(int work, byte[] mainImage,
			byte[] previewImage, String newsTitle, int category,
			String newsOwner) {
		super(work);
		MainImage = mainImage;
		PreviewImage = previewImage;
		NewsTitle = newsTitle;
		Category = category;
		NewsOwner = newsOwner;
	}
	public byte[] getMainImage() {
		return MainImage;
	}
	public void setMainImage(byte[] mainImage) {
		MainImage = mainImage;
	}
	public byte[] getPreviewImage() {
		return PreviewImage;
	}
	public void setPreviewImage(byte[] previewImage) {
		PreviewImage = previewImage;
	}
	public String getNewsTitle() {
		return NewsTitle;
	}
	public void setNewsTitle(String newsTitle) {
		NewsTitle = newsTitle;
	}
	public int getCategory() {
		return Category;
	}
	public void setCategory(int category) {
		Category = category;
	}
	public String getNewsOwner() {
		return NewsOwner;
	}
	public void setNewsOwner(String newsOwner) {
		NewsOwner = newsOwner;
	}
	
	

}
