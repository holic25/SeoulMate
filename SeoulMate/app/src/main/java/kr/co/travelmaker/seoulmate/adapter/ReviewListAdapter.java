package kr.co.travelmaker.seoulmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.model.ReviewMember;
import kr.co.travelmaker.seoulmate.service.LoginService;

public class ReviewListAdapter extends BaseAdapter{
    ArrayList<ReviewMember> items;
    LoginService loginService = LoginService.getInstance();
    Member member = loginService.getLoginMember();

    public ReviewListAdapter(ArrayList<ReviewMember> items) {
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReviewListAdapter.Holder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_reviews, parent, false);
            holder = new ReviewListAdapter.Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ReviewListAdapter.Holder)convertView.getTag();
        }

        ReviewMember item = (ReviewMember)getItem(position);

        holder.tv_user_id.setText(item.getMember().getMember_id());
        holder.tv_date.setText(item.getReview().getReview_date());
        holder.tv_review.setText(item.getReview().getReview_content());
        holder.rb_stars.setRating(item.getReview().getReview_starpoint());

        return convertView;
    }

    public class Holder {
        @BindView(R.id.tv_user_id) TextView tv_user_id;
        @BindView(R.id.tv_date) TextView tv_date;
        @BindView(R.id.rb_stars) RatingBar rb_stars;
        @BindView(R.id.tv_review) TextView tv_review;

        public Holder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}