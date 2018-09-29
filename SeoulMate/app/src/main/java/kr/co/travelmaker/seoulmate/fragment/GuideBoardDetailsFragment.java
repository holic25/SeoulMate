package kr.co.travelmaker.seoulmate.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.data.GuideType;
import kr.co.travelmaker.seoulmate.data.PayType;
import kr.co.travelmaker.seoulmate.model.Board;
import kr.co.travelmaker.seoulmate.model.GuideBoard;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;

public class GuideBoardDetailsFragment extends Fragment {

    private static GuideBoardDetailsFragment curr = null;
    private static GuideBoardMember guideBoard;

    public static GuideBoardDetailsFragment getInstance(GuideBoardMember mguideBoard) {
        guideBoard = mguideBoard;
        if(curr==null) {
            curr = new GuideBoardDetailsFragment();
        }
        return curr;
    }

    private Unbinder unbinder;
    Bus bus = BusProvider.getInstance().getBus();

    @BindView(R.id.guide_guideType) TextView guide_guideType;
    @BindView(R.id.guide_payType) TextView guide_payType;
    @BindView(R.id.guide_area) TextView guide_area;
    @BindView(R.id.guide_time) TextView guide_time;

    int[] days = new int[7];
    int[] days_str_arr = {R.string.sun,R.string.mon,R.string.tue,R.string.wed,R.string.thu,R.string.fri,R.string.sat};
    String day_str = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_board_details, container, false);
        unbinder = ButterKnife.bind(this,view);
        bus.register(this);

        days[0] = guideBoard.getGuideBoard().getGuide_add_board().getGuide_sun();
        days[1] = guideBoard.getGuideBoard().getGuide_add_board().getGuide_mon();
        days[2] = guideBoard.getGuideBoard().getGuide_add_board().getGuide_tue();
        days[3] = guideBoard.getGuideBoard().getGuide_add_board().getGuide_wed();
        days[4] = guideBoard.getGuideBoard().getGuide_add_board().getGuide_thu();
        days[5] = guideBoard.getGuideBoard().getGuide_add_board().getGuide_fri();
        days[6] = guideBoard.getGuideBoard().getGuide_add_board().getGuide_sat();

        PayType payType = new PayType();
        GuideType guideType = new GuideType();

        guide_payType.setText(payType.getCashOrCard()[guideBoard.getGuideBoard().getBoard().getBoard_paytype()]);
        guide_guideType.setText(guideType.getOnlyGuideOrDrivingAndGuide()[guideBoard.getGuideBoard().getBoard().getBoard_guidetype()]);

        guide_area.setText(guideBoard.getGuideBoard().getBoard().getBoard_place());
        for(int i = 0; i<days.length; i++){
            if(days[i]==1){
                day_str += getString(days_str_arr[i])+"  ";
            }
        }
        guide_time.setText(day_str + "\n"+ guideBoard.getGuideBoard().getGuide_add_board().getGuide_startTime() + "~" + guideBoard.getGuideBoard().getGuide_add_board().getGuide_endTime());
        day_str = "";

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

}