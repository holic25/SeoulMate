package kr.co.travelmaker.seoulmate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.data.GuideType;
import kr.co.travelmaker.seoulmate.data.PayType;
import kr.co.travelmaker.seoulmate.model.TravelerBoard;

public class BoardTravelerListAdapter extends BaseAdapter {

    ArrayList<TravelerBoard> travelerBoards;
    PayType payType = new PayType();
    GuideType guideType = new GuideType();

    public BoardTravelerListAdapter(ArrayList<TravelerBoard> travelerBoards) {
        this.travelerBoards = travelerBoards;
    }

    @Override
    public int getCount() {
        return travelerBoards.size();
    }

    @Override
    public Object getItem(int position) {
        return travelerBoards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_board, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        TravelerBoard travelerBoard = travelerBoards.get(position);

        int payment_type = travelerBoard.getBoard().getBoard_paytype();
        int guide_type = travelerBoard.getBoard().getBoard_guidetype();

        holder.tvTitle.setText(travelerBoard.getBoard().getBoard_title());
        holder.tvPaymentType.setText(payType.getCashOrCard()[payment_type]);
        holder.tvGuideType.setText(guideType.getOnlyGuideOrDrivingAndGuide()[guide_type]);

        if (travelerBoard.getBoard().getBoard_complete() == 0) {
            holder.txt_close_request.setVisibility(View.INVISIBLE);
        } else {
            holder.txt_close_request.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public class Holder {
        @BindView(R.id.tv_title) TextView tvTitle;
        @BindView(R.id.tv_payment_type) TextView tvPaymentType;
        @BindView(R.id.tv_guide_type) TextView tvGuideType;
        @BindView(R.id.txt_close_request) TextView txt_close_request;

        public Holder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
