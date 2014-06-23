/**
 * 
 */
package kr.ac.kookmin.mynewspaper;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * ¾î´ðÅÍ : ´ñ±Û ¸®½ºÆ®ÀÇ ¾î´ðÅÍ
 **/
public class CommentListAdapter extends BaseAdapter {

	private ArrayList<ClientComment> mCommentList;
	private LayoutInflater mInflater;

	public CommentListAdapter(ArrayList<ClientComment> mCommentList,
			LayoutInflater mInflater) {
		super();
		this.mCommentList = mCommentList;
		this.mInflater = mInflater;
	}

	public ArrayList<ClientComment> getmCommentList() {
		return mCommentList;
	}

	@Override
	public int getCount() {
		return mCommentList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCommentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_comment_list,
					parent, false);
		}
		TextView userName = (TextView) convertView.findViewById(R.id.user_name);
		TextView registerTime = (TextView) convertView
				.findViewById(R.id.register_time);
		TextView commmet = (TextView) convertView.findViewById(R.id.comment);
		ClientComment myComment = mCommentList.get(position);
		userName.setText(myComment.getUserName());
		registerTime.setText(myComment.getRegisterTime());
		commmet.setText(myComment.getComment());
		return convertView;
	}

}
