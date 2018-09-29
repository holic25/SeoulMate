package kr.co.travelmaker.seoulmate.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.data.GuideType;
import kr.co.travelmaker.seoulmate.data.PayType;
import kr.co.travelmaker.seoulmate.model.TravelerBoard;

public class TravelerBoardDetailsFragment extends Fragment {

    private static TravelerBoardDetailsFragment curr = null;

    private static TravelerBoard travelerBoard;
    public static TravelerBoardDetailsFragment getInstance(TravelerBoard mtravelerBoard) {
        travelerBoard = mtravelerBoard;
        if(curr==null) {
            curr = new TravelerBoardDetailsFragment();
        }
        return curr;
    }

    private Unbinder unbinder;

    @BindView(R.id.travel_guideType) TextView travel_guideType;
    @BindView(R.id.travel_payType) TextView travel_payType;
    @BindView(R.id.travel_place) TextView travel_place;
    @BindView(R.id.travel_period) TextView travel_period;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_traveler_board_details, container, false);
        unbinder = ButterKnife.bind(this,view);

        PayType payType = new PayType();
        GuideType guideType = new GuideType();

        travel_period.setText(travelerBoard.getTraveler_add_board().getTraveler_id_board_startdate().substring(0,10) + " ~ " + travelerBoard.getTraveler_add_board().getTraveler_id_board_enddate().substring(0,10));
        travel_payType.setText(payType.getCashOrCard()[travelerBoard.getBoard().getBoard_paytype()]);
        travel_guideType.setText(guideType.getOnlyGuideOrDrivingAndGuide()[travelerBoard.getBoard().getBoard_guidetype()]);
        travel_place.setText(travelerBoard.getBoard().getBoard_place());
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
