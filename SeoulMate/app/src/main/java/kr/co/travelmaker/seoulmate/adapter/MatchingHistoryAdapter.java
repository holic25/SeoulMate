package kr.co.travelmaker.seoulmate.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.travelmaker.seoulmate.FirebaseMessageActivity;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.ReviewWriteActivity;
import kr.co.travelmaker.seoulmate.model.MatchMember;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.service.LoginService;


public class MatchingHistoryAdapter extends BaseAdapter{
    ArrayList<MatchMember> matchMembers;
    LoginService loginService = LoginService.getInstance();
    Member loginMember;

    public MatchingHistoryAdapter(ArrayList<MatchMember> matchMembers) {
        this.matchMembers = matchMembers;
        loginMember = loginService.getLoginMember();
    }
    @Override
    public int getCount() {
        return matchMembers.size();
    }

    @Override
    public Object getItem(int position) {
        return matchMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final MatchingHistoryAdapter.Holder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_matching_history, parent, false);
            holder = new MatchingHistoryAdapter.Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MatchingHistoryAdapter.Holder)convertView.getTag();
        }

        if(matchMembers.get(position).getMember().getMember_kind() == 0 ) {
            holder.img_applicant.setBackgroundResource(R.drawable.ic_backpack);
        } else {
            holder.img_applicant.setBackgroundResource(R.drawable.ic_compass);
        }

        holder.txt_applicant_id.setText(matchMembers.get(position).getMember().getMember_id().toString());
        holder.txt_applied_date.setText(matchMembers.get(position).getMatch().getMatch_date().toString());
        holder.btn_review.setVisibility(View.INVISIBLE);

        if(loginMember.getMember_kind()==0) {
            if (matchMembers.get(position).getMatch().getMatch_complete() == 0) {
                holder.btn_review.setVisibility(View.VISIBLE);
                //리뷰버튼
                //신청 수락 후 0, 0일 때 리뷰버튼 생성
                holder.btn_review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(parent.getContext(), ReviewWriteActivity.class);
                        intent.putExtra("getMatch_id_inc", matchMembers.get(position).getMatch().getMatch_id_inc());
                        intent.putExtra("getMember_id_inc", matchMembers.get(position).getMember().getMember_id_inc());
                        parent.getContext().startActivity(intent);
                    }
                });
            }
        }

        holder.layoutReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(parent.getContext(), FirebaseMessageActivity.class);
                if(loginMember.getMember_kind()==0) {
                    i.putExtra("destinationUid", matchMembers.get(position).getMatch().getGuide_id_inc().toString());
                }
                else {
                    i.putExtra("destinationUid", matchMembers.get(position).getMatch().getTraveler_id_inc().toString());
                }
                parent.getContext().startActivity(i);
            }
        });

        return convertView;
    }

    public class Holder {
        @BindView(R.id.img_applicant) ImageView img_applicant;
        @BindView(R.id.txt_applicant_id) TextView txt_applicant_id;
        @BindView(R.id.txt_applied_date) TextView txt_applied_date;
        @BindView(R.id.btn_review) Button btn_review;
        @BindView(R.id.layout_review) RelativeLayout layoutReview;

        public Holder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}