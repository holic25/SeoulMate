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
import kr.co.travelmaker.seoulmate.model.GuideAddBoard;
import kr.co.travelmaker.seoulmate.model.GuideBoard;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;
import kr.co.travelmaker.seoulmate.model.TravelerBoard;

public class GuideBoardIntroductionFragment extends Fragment {

    private static GuideBoardIntroductionFragment curr = null;

    private static GuideBoardMember guideBoard;

    public static GuideBoardIntroductionFragment getInstance(GuideBoardMember mguideBoard) {
        guideBoard = mguideBoard;
        if(curr==null) {
            curr = new GuideBoardIntroductionFragment();
        }
        return curr;
    }

    private Unbinder unbinder;

    @BindView(R.id.text_Board_introduction)TextView text_Board_introduction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board_introduction, container, false);
        unbinder = ButterKnife.bind(this,view);


        text_Board_introduction.setText(guideBoard.getGuideBoard().getBoard().getBoard_content());


        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
