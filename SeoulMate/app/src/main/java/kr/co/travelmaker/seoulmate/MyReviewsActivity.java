package kr.co.travelmaker.seoulmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.travelmaker.seoulmate.adapter.ReviewListAdapter;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.model.ReviewMember;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReviewsActivity extends AppCompatActivity {
    @BindView(R.id.tv_no_data) TextView tvNoData;
    @BindView(R.id.lv_reviews) ListView lv_reviews;
    ArrayList<ReviewMember> reviewMembers = new ArrayList<>();
    ReviewListAdapter reviewListAdapter;

    LoginService loginService = LoginService.getInstance();
    Member member = loginService.getLoginMember();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);
        ButterKnife.bind(this);

        if (member.getMember_kind() == 0) {
            Call<ArrayList<ReviewMember>> observ = RetrofitService.getInstance().getRetrofitRequest().getTravelerReviewListData(member.getMember_id_inc());
            observ.enqueue(new Callback<ArrayList<ReviewMember>>() {
                @Override
                public void onResponse(Call<ArrayList<ReviewMember>> call, Response<ArrayList<ReviewMember>> response) {
                    if(response.isSuccessful()){
                        Log.d("datalog","success");

                        reviewMembers = response.body();

                        for(int i = 0; i< reviewMembers.size(); i++) {
                            Log.d("datalog", reviewMembers.get(i).toString());
                        }

                        reviewListAdapter = new ReviewListAdapter(reviewMembers);
                        lv_reviews.setAdapter(reviewListAdapter);

                        if(reviewMembers==null || reviewMembers.size()==0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvNoData.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ReviewMember>> call, Throwable t) {
                    Log.d("datalog","fail");
                }
            });

        } else if (member.getMember_kind() == 1){
            Call<ArrayList<ReviewMember>> observ = RetrofitService.getInstance().getRetrofitRequest().getGuideReviewListData(member.getMember_id_inc());
            observ.enqueue(new Callback<ArrayList<ReviewMember>>() {
                @Override
                public void onResponse(Call<ArrayList<ReviewMember>> call, Response<ArrayList<ReviewMember>> response) {
                    if(response.isSuccessful()){
                        Log.d("datalog","success");
                        reviewMembers = response.body();

                        for(int i = 0; i< reviewMembers.size(); i++) {
                            Log.d("datalog", reviewMembers.get(i).toString());
                        }

                        reviewListAdapter = new ReviewListAdapter(reviewMembers);
                        lv_reviews.setAdapter(reviewListAdapter);

                        if(reviewMembers==null || reviewMembers.size()==0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvNoData.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ReviewMember>> call, Throwable t) {
                    Log.d("datalog","fail");
                }
            });
        }
    }
}