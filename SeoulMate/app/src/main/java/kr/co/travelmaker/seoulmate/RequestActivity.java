package kr.co.travelmaker.seoulmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.otto.Bus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.travelmaker.seoulmate.adapter.RequestAdapter;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.model.ApplyMemberBoard;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity {

    @BindView(R.id.tv_no_data) TextView tvNoData;
    @BindView(R.id.lv_request) ListView lv_request;
    RequestAdapter requestAdapter;
    ArrayList<ApplyMemberBoard> applyMemberBoards = new ArrayList<>();

    LoginService loginService = LoginService.getInstance();
    Member loginMember;

    Bus bus = BusProvider.getInstance().getBus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        ButterKnife.bind(this);
        bus.register(this);

        loginMember = loginService.getLoginMember();

        Call<ArrayList<ApplyMemberBoard>> observ = RetrofitService.getInstance().getRetrofitRequest().getSelectedApplyBoardData(loginMember.getMember_id_inc());
        observ.enqueue(new Callback<ArrayList<ApplyMemberBoard>>() {
            @Override
            public void onResponse(Call<ArrayList<ApplyMemberBoard>> call, Response<ArrayList<ApplyMemberBoard>> response) {
                if(response.isSuccessful()) {
                    Log.d("datalog","success");
                    applyMemberBoards = response.body();
                    for(int i=0;i<applyMemberBoards.size();i++) {
                        Log.d("datalog",applyMemberBoards.get(i).toString());
                    }
                    requestAdapter = new RequestAdapter(applyMemberBoards, bus);
                    lv_request.setAdapter(requestAdapter);

                    if(applyMemberBoards==null || applyMemberBoards.size()==0) {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                    else {
                        tvNoData.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ApplyMemberBoard>> call, Throwable t) {
                Log.d("datalog","fail");
                Log.d("datalog",t.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}