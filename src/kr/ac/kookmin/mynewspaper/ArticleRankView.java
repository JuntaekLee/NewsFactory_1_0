package kr.ac.kookmin.mynewspaper;

import java.util.ArrayList;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * NewspaperBoardActivity ³»ÀÇ Rank View
 **/
public class ArticleRankView {
	private ArrayList<ImageView> ArticleRankImage;
	private ArrayList<TextView> ArticleRankText;

	public ArticleRankView(ArrayList<ImageView> ArticleRankImage,
			ArrayList<TextView> ArticleRankText) {
		super();
		this.ArticleRankImage = ArticleRankImage;
		this.ArticleRankText = ArticleRankText;
	}

	public ArrayList<ImageView> getArticleRankImage() {
		return ArticleRankImage;
	}

	public void setArticleRankImage(ArrayList<ImageView> articleRankImage) {
		ArticleRankImage = articleRankImage;
	}

	public ArrayList<TextView> getArticleRankText() {
		return ArticleRankText;
	}

	public void setArticleRankText(ArrayList<TextView> articleRankText) {
		ArticleRankText = articleRankText;
	}

}
