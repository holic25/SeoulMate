package kr.co.travelmaker.seoulmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import kr.co.travelmaker.seoulmate.adapter.BoardGuideListAdapter;
import kr.co.travelmaker.seoulmate.adapter.BoardTravelerListAdapter;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.model.TravelerBoard;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBoardsActivity extends AppCompatActivity {

    LoginService loginService = LoginService.getInstance();
    Member loginMember;

    @BindView(R.id.tv_no_data) TextView tvNoData;
    @BindView(R.id.lv_my_boards) ListView lvMyBoards;

    ArrayList<TravelerBoard> travelerBoards = new ArrayList<>();
    BoardTravelerListAdapter boardTravelerListAdapter;

    ArrayList<GuideBoardMember> guideBoardMembers = new ArrayList<>();
    BoardGuideListAdapter boardGuideListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_boards);
        ButterKnife.bind(this);

        loginMember = loginService.getLoginMember();

        if(loginMember.getMember_kind()==0) {
            Call<ArrayList<TravelerBoard>> observ = RetrofitService.getInstance().getRetrofitRequest().getMyTravelerBoardData(loginMember.getMember_id_inc());
            observ.enqueue(new Callback<ArrayList<TravelerBoard>>() {
                @Override
                public void onResponse(Call<ArrayList<TravelerBoard>> call, Response<ArrayList<TravelerBoard>> response) {
                    Log.d("datalog","getMyTravelerBoardData_success");
                    travelerBoards = response.body();
                    boardTravelerListAdapter = new BoardTravelerListAdapter(travelerBoards);
                    lvMyBoards.setAdapter(boardTravelerListAdapter);

                    if(travelerBoards==null || travelerBoards.size()==0) {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                    else {
                        tvNoData.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<TravelerBoard>> call, Throwable t) {
                    Log.d("datalog","getMyTravelerBoardData_fail");
                }
            });
        }

        else {
            Call<ArrayList<GuideBoardMember>> observ = RetrofitService.getInstance().getRetrofitRequest().getMyGuideBoardData(loginMember.getMember_id_inc());
            observ.enqueue(new Callback<ArrayList<GuideBoardMember>>() {
                @Override
                public void onResponse(Call<ArrayList<GuideBoardMember>> call, Response<ArrayList<GuideBoardMember>> response) {
                    Log.d("datalog","getMyGuideBoardData_success");
                    guideBoardMembers = response.body();
                    boardGuideListAdapter = new BoardGuideListAdapter(guideBoardMembers);
                    lvMyBoards.setAdapter(boardGuideListAdapter);

                    if(guideBoardMembers==null || guideBoardMembers.size()==0) {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                    else {
                        tvNoData.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<GuideBoardMember>> call, Throwable t) {
                    Log.d("datalog","getMyGuideBoardData_fail");
                }
            });
        }
    }

    @OnItemClick(R.id.lv_my_boards)
    public void onItemClickMyBoards(int position) {
        if(loginMember.getMember_kind()==0) {
            Long board_id_inc = travelerBoards.get(position).getBoard().getBoard_id_inc();

            Intent i = new Intent(MyBoardsActivity.this, TravelerBoardDetailActivity.class);
            i.putExtra("board_id_inc",board_id_inc);
            startActivity(i);
        }
        else {
            Long board_id_inc = guideBoardMembers.get(position).getGuideBoard().getBoard().getBoard_id_inc();

            Intent i = new Intent(MyBoardsActivity.this, GuideBoardDetailActivity.class);
            i.putExtra("board_id_inc",board_id_inc);
            startActivity(i);
        }
    }
}
