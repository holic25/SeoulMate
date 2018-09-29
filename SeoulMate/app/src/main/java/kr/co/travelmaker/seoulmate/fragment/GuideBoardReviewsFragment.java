package kr.co.travelmaker.seoulmate.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.adapter.ReviewListAdapter;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;
import kr.co.travelmaker.seoulmate.model.ReviewMember;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideBoardReviewsFragment extends Fragment {

    private static GuideBoardReviewsFragment curr = null;

    private static GuideBoardMember guideBoard;

    public static GuideBoardReviewsFragment getInstance(GuideBoardMember mguideBoard) {
        guideBoard = mguideBoard;
        if(curr==null) {
            curr = new GuideBoardReviewsFragment();
        }
        return curr;
    }

    @BindView(R.id.lv_guide_reviews) ListView lvGuideReviews;
    ReviewListAdapter reviewListAdapter;
    ArrayList<ReviewMember> reviewMembers = new ArrayList<>();

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_board_reviews, container, false);
        unbinder = ButterKnife.bind(this,view);

        Call<ArrayList<ReviewMember>> observ = RetrofitService.getInstance().getRetrofitRequest().getGuideReviewListData(guideBoard.getGuideBoard().getBoard().getMember_fk_inc());
        observ.enqueue(new Callback<ArrayList<ReviewMember>>() {
            @Override
            public void onResponse(Call<ArrayList<ReviewMember>> call, Response<ArrayList<ReviewMember>> response) {
                if(response.isSuccessful()) {
                    reviewMembers = response.body();
                    reviewListAdapter = new ReviewListAdapter(reviewMembers);
                    lvGuideReviews.setAdapter(reviewListAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ReviewMember>> call, Throwable t) {

            }
        });

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}