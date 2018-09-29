package kr.co.travelmaker.seoulmate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.adapter.TravelerBoardViewPagerAdapter;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.model.Board;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.model.TravelerAddBoard;
import kr.co.travelmaker.seoulmate.model.TravelerBoard;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelerBoardDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvbtn_introduction) TextView tvbtnIntroduction;
    @BindView(R.id.tvbtn_details) TextView tvbtnDetails;
    @BindView(R.id.iv_introduction) ImageView ivIntroduction;
    @BindView(R.id.iv_details) ImageView ivDetails;
    @BindView(R.id.btn_send_message) Button btnSendMessage;
    @BindView(R.id.title_member_id) TextView title_member_id;

    @BindView(R.id.vp_traveler_board) ViewPager vpTravelerBoard;
    TravelerBoardViewPagerAdapter travelerBoardViewPagerAdapter;

    @BindView(R.id.btn_dialog) ImageView btn_dialog;
    @BindView(R.id.travel_title) TextView travel_title;
    @BindView(R.id.btn_traveler_apply) Button btnTravelerApply;

    Integer tabNum = 2;

    TextView[] tvbtns = new TextView[tabNum];
    ImageView[] ivs = new ImageView[tabNum];

    Bus bus = BusProvider.getInstance().getBus();

    TravelerBoard travelerBoard;

    LoginService loginService = LoginService.getInstance();
    Member loginMember;
    Member BoardWriteMemeber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler_board_detail);
        ButterKnife.bind(this);
        bus.register(this);

        tvbtns[0] = tvbtnIntroduction;
        tvbtns[1] = tvbtnDetails;

        ivs[0] = ivIntroduction;
        ivs[1] = ivDetails;

        vpTravelerBoard.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int currentIndex) {
                for(int i=0;i<tabNum;i++) {
                    tvbtns[i].setTextColor(getColor(TravelerBoardDetailActivity.this,R.color.bright_gray_bc));
                    ivs[i].setVisibility(View.INVISIBLE);
                }
                tvbtns[currentIndex].setTextColor(getColor(TravelerBoardDetailActivity.this,R.color.dark_violet));
                ivs[currentIndex].setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @OnClick(R.id.tvbtn_introduction)
    public void onClickIntroduction() {
        vpTravelerBoard.setCurrentItem(0);
    }

    @OnClick(R.id.tvbtn_details)
    public void onClickDetails() {
        vpTravelerBoard.setCurrentItem(1);
    }

    @OnClick(R.id.btn_dialog)
    public void onClickDialog(){
        final ArrayList<String> ListItems = new ArrayList<>();

        ListItems.add(getResources().getString(R.string.recruitment_completed));
        ListItems.add(getResources().getString(R.string.update));
        ListItems.add(getResources().getString(R.string.delete));
        final String[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder dlg = new AlertDialog.Builder(this).setCancelable(true);

        dlg.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();

                if(selectedText.equals(getResources().getString(R.string.recruitment_completed))) {
                    Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().completeBoard(travelerBoard.getBoard().getBoard_id_inc());
                    observ.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(TravelerBoardDetailActivity.this,R.string.recruitment_completed,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
                else if(selectedText.equals(getResources().getString(R.string.update))){
                    Intent intent = new Intent(getApplicationContext(),TravelerBoardModifyActivity.class);
                    intent.putExtra("board_id_inc", travelerBoard.getBoard().getBoard_id_inc());
                    intent.putExtra("board_title", travelerBoard.getBoard().getBoard_title());
                    intent.putExtra("board_place", travelerBoard.getBoard().getBoard_place());
                    intent.putExtra("board_paytype", travelerBoard.getBoard().getBoard_paytype());
                    intent.putExtra("board_guidetype", travelerBoard.getBoard().getBoard_guidetype());
                    intent.putExtra("board_content", travelerBoard.getBoard().getBoard_content());
                    intent.putExtra("board_complete", travelerBoard.getBoard().getBoard_complete());
                    intent.putExtra("board_kind", travelerBoard.getBoard().getBoard_kind());
                    intent.putExtra("member_fk_inc", travelerBoard.getBoard().getMember_fk_inc());
                    intent.putExtra("traveler_id_board_inc", travelerBoard.getTraveler_add_board().getTraveler_id_board_inc());
                    intent.putExtra("traveler_id_board_startdate", travelerBoard.getTraveler_add_board().getTraveler_id_board_startdate());
                    intent.putExtra("traveler_id_board_enddate", travelerBoard.getTraveler_add_board().getTraveler_id_board_enddate());
                    intent.putExtra("board_fk_inc", travelerBoard.getTraveler_add_board().getBoard_fk_inc());
                    startActivity(intent);
                }
                else if(selectedText.equals(getResources().getString(R.string.delete))){
                    Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().deleteBoard(travelerBoard.getBoard().getBoard_id_inc());
                    observ.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(TravelerBoardDetailActivity.this,R.string.delete,Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
            }
        });
        if (isFinishing() == false) {
            dlg.show();
        }
    }

    @OnClick(R.id.btn_traveler_apply)
    public void onClickApply(){
        if(loginMember.getMember_kind() == 0){
            Toast.makeText(getApplicationContext(),R.string.onlyTravelerApply,Toast.LENGTH_SHORT).show();
        }else{
            AlertDialog.Builder dlg = new AlertDialog.Builder(TravelerBoardDetailActivity.this);
            dlg.setTitle(R.string.apply);

            Locale systemLocale = getResources().getConfiguration().locale;
            String strLanguage = systemLocale.getLanguage();

            if(strLanguage == "ko"){
                dlg.setMessage(BoardWriteMemeber.getMember_id()+getResources().getString(R.string.AskTravel)+"?");
            }else {
                dlg.setMessage(getResources().getString(R.string.AskTravel)+BoardWriteMemeber.getMember_id()+"?");
            }

            dlg.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dlg.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().applyInsert(loginMember.getMember_id_inc(), travelerBoard.getBoard().getBoard_id_inc());
                    observ.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()){
                                Log.d("datalog","applyInsert_success");
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("datalog","applyInsert_fail");
                        }
                    });
                }
            });
            if (isFinishing() == false) {
                dlg.show();
            }
        }
    }


    @OnClick(R.id.btn_send_message)
    public void onClickSendMessage() {
        Intent i = new Intent(TravelerBoardDetailActivity.this, FirebaseMessageActivity.class);
        i.putExtra("destinationUid",travelerBoard.getBoard().getMember_fk_inc().toString());
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onClickIntroduction();

        loginMember = loginService.getLoginMember();

        Intent intent = getIntent();
        Long board_id_inc = intent.getLongExtra("board_id_inc",0);

        Call<TravelerBoard> observ = RetrofitService.getInstance().getRetrofitRequest().getTravelerBoard(board_id_inc);
        observ.enqueue(new Callback<TravelerBoard>() {
            @Override
            public void onResponse(Call<TravelerBoard> call, Response<TravelerBoard> response) {
                if(response.isSuccessful()){
                    travelerBoard = response.body();

                    Call<Member> observ2 = RetrofitService.getInstance().getRetrofitRequest().getMemberData(travelerBoard.getBoard().getMember_fk_inc());
                    observ2.enqueue(new Callback<Member>() {
                        @Override
                        public void onResponse(Call<Member> call, Response<Member> response) {
                            if(response.isSuccessful()){
                                BoardWriteMemeber = response.body();

                                title_member_id.setText(BoardWriteMemeber.getMember_id());
                            }
                        }

                        @Override
                        public void onFailure(Call<Member> call, Throwable t) {

                        }
                    });

                    Log.d("dsj", travelerBoard.toString());

                    travel_title.setText(travelerBoard.getBoard().getBoard_title());

                    travelerBoardViewPagerAdapter = new TravelerBoardViewPagerAdapter(getSupportFragmentManager(), travelerBoard);
                    vpTravelerBoard.setAdapter(travelerBoardViewPagerAdapter);

                    if(loginMember.getMember_id_inc() == travelerBoard.getBoard().getMember_fk_inc()) {
                        btn_dialog.setVisibility(View.VISIBLE);
                    }
                    else {
                        btn_dialog.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<TravelerBoard> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }
}