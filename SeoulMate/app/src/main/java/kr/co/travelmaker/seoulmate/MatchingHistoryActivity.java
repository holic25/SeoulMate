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
import kr.co.travelmaker.seoulmate.adapter.MatchingHistoryAdapter;
import kr.co.travelmaker.seoulmate.model.MatchMember;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchingHistoryActivity extends AppCompatActivity {
    @BindView(R.id.tv_no_data) TextView tvNoData;
    @BindView(R.id.lv_matching_history) ListView lv_matching_history;
    MatchingHistoryAdapter matchingHistoryAdapter;
    ArrayList<MatchMember> matchMembers = new ArrayList<>();
    LoginService loginService = LoginService.getInstance();
    Member member = loginService.getLoginMember();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching_history);
        ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(member.getMember_kind() == 0){
            //traveler
            Call<ArrayList<MatchMember>> observ = RetrofitService.getInstance().getRetrofitRequest().getTravelerMatch(member.getMember_id_inc());
            observ.enqueue(new Callback<ArrayList<MatchMember>>() {
                @Override
                public void onResponse(Call<ArrayList<MatchMember>> call, Response<ArrayList<MatchMember>> response) {
                    if(response.isSuccessful()){
                        Log.d("datalog","success");

                        matchMembers = response.body();

                        for(int i = 0; i< matchMembers.size(); i++) {
                            Log.d("datalog", matchMembers.get(i).toString());
                        }
                        matchingHistoryAdapter = new MatchingHistoryAdapter(matchMembers);
                        lv_matching_history.setAdapter(matchingHistoryAdapter);

                        if(matchMembers==null || matchMembers.size()==0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvNoData.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<MatchMember>> call, Throwable t) {
                    Log.d("datalog","fail");
                }
            });

        } else if(member.getMember_kind() == 1){
            //guide
            Call<ArrayList<MatchMember>> observ = RetrofitService.getInstance().getRetrofitRequest().getGuideMatch(member.getMember_id_inc());
            observ.enqueue(new Callback<ArrayList<MatchMember>>() {
                @Override
                public void onResponse(Call<ArrayList<MatchMember>> call, Response<ArrayList<MatchMember>> response) {
                    if(response.isSuccessful()){
                        Log.d("datalog","success");

                        matchMembers = response.body();

                        for(int i = 0; i< matchMembers.size(); i++) {
                            Log.d("datalog", matchMembers.get(i).toString());
                        }

                        matchingHistoryAdapter = new MatchingHistoryAdapter(matchMembers);
                        lv_matching_history.setAdapter(matchingHistoryAdapter);

                        if(matchMembers==null || matchMembers.size()==0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvNoData.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<MatchMember>> call, Throwable t) {
                    Log.d("datalog","fail");
                }
            });
        }
    }
}