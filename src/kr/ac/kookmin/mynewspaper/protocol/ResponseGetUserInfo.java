/**
 * 
 */
package kr.ac.kookmin.mynewspaper.protocol;

/**
 * 
 **/
public class ResponseGetUserInfo extends ResponseData {

	/**
	 * 
	 **/
	private static final long serialVersionUID = -6896307633582198082L;
	private long SumOfMyArticle;
	private long SumOfRecommend;

	public ResponseGetUserInfo(int work, int responseCode, long sumOfMyArticle,
			long sumOfRecommend) {
		super(work, responseCode);
		SumOfMyArticle = sumOfMyArticle;
		SumOfRecommend = sumOfRecommend;
	}

	public long getSumOfMyArticle() {
		return SumOfMyArticle;
	}

	public void setSumOfMyArticle(long sumOfMyArticle) {
		SumOfMyArticle = sumOfMyArticle;
	}

	public long getSumOfRecommend() {
		return SumOfRecommend;
	}

	public void setSumOfRecommend(long sumOfRecommend) {
		SumOfRecommend = sumOfRecommend;
	}

}
