package kr.ac.kookmin.mynewspaper;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 어댑터 : NewspaperBoardActivity에서 일반 기사들을 보여주는 ArticleList Adapter
 **/
public class ArticleListAdapter extends BaseAdapter {

	private ArrayList<String> mArticleTitleList;
	private LayoutInflater mInflater;

	public ArticleListAdapter(ArrayList<String> mArticleTitleList,
			LayoutInflater mInflater) {
		super();
		this.mArticleTitleList = mArticleTitleList;
		this.mInflater = mInflater;
	}

	public ArrayList<String> getmArticleTitleList() {
		return mArticleTitleList;
	}

	@Override
	public int getCount() {
		return mArticleTitleList.size();
	}

	@Override
	public Object getItem(int position) {
		return mArticleTitleList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_article_list,
					parent, false);
		}
		TextView tv = (TextView) convertView.findViewById(R.id.article_title);
		tv.setText(mArticleTitleList.get(position));
		return convertView;
	}

}
