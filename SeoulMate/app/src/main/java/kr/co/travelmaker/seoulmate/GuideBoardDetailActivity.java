package kr.co.travelmaker.seoulmate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.adapter.GuideBoardViewPagerAdapter;
import kr.co.travelmaker.seoulmate.model.Board;
import kr.co.travelmaker.seoulmate.model.GuideAddBoard;
import kr.co.travelmaker.seoulmate.model.GuideBoard;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideBoardDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvbtn_introduction) TextView tvbtnIntroduction;
    @BindView(R.id.tvbtn_details) TextView tvbtnDetails;
    @BindView(R.id.tvbtn_reviews) TextView tvbtnReviews;
    @BindView(R.id.iv_introduction) ImageView ivIntroduction;
    @BindView(R.id.iv_details) ImageView ivDetails;
    @BindView(R.id.iv_reviews) ImageView ivReviews;
    @BindView(R.id.btn_send_message) Button btnSendMessage;
    @BindView(R.id.title_member_id) TextView title_member_id;

    @BindView(R.id.vp_guide_board) ViewPager vpGuideBoard;
    GuideBoardViewPagerAdapter guideBoardViewPagerAdapter;

    @BindView(R.id.guide_title) TextView guide_title;
    @BindView(R.id.btn_dialog) ImageView btn_dialog;
    @BindView(R.id.iv_license_approval) ImageView iv_license_approval;
    @BindView(R.id.btn_guide_apply) Button btnGuideApply;
    Integer tabNum = 3;

    TextView[] tvbtns = new TextView[tabNum];
    ImageView[] ivs = new ImageView[tabNum];

    GuideBoardMember guideBoard;

    LoginService loginService = LoginService.getInstance();
    Member loginMember;
    Member BoardWriteMemeber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_board_detail);
        ButterKnife.bind(this);

        tvbtns[0] = tvbtnIntroduction;
        tvbtns[1] = tvbtnDetails;
        tvbtns[2] = tvbtnReviews;

        ivs[0] = ivIntroduction;
        ivs[1] = ivDetails;
        ivs[2] = ivReviews;



        vpGuideBoard.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int currentIndex) {
                for(int i=0;i<tabNum;i++) {
                    tvbtns[i].setTextColor(getColor(GuideBoardDetailActivity.this,R.color.bright_gray_bc));
                    ivs[i].setVisibility(View.INVISIBLE);
                }
                tvbtns[currentIndex].setTextColor(getColor(GuideBoardDetailActivity.this,R.color.dark_violet));
                ivs[currentIndex].setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @OnClick(R.id.tvbtn_introduction)
    public void onClickIntroduction() {
        vpGuideBoard.setCurrentItem(0);
    }

    @OnClick(R.id.tvbtn_details)
    public void onClickDetails() {
        vpGuideBoard.setCurrentItem(1);
    }

    @OnClick(R.id.tvbtn_reviews)
    public void onClickReviews() {
        vpGuideBoard.setCurrentItem(2);
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
                    Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().completeBoard(guideBoard.getGuideBoard().getBoard().getBoard_id_inc());
                    observ.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(GuideBoardDetailActivity.this,R.string.recruitment_completed,Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
                else if(selectedText.equals(getResources().getString(R.string.update))){
                    Intent intent = new Intent(getApplicationContext(),GuideBoardModifyActivity.class);
                    intent.putExtra("board_id_inc", guideBoard.getGuideBoard().getBoard().getBoard_id_inc());
                    intent.putExtra("board_title", guideBoard.getGuideBoard().getBoard().getBoard_title());
                    intent.putExtra("board_place", guideBoard.getGuideBoard().getBoard().getBoard_place());
                    intent.putExtra("board_paytype", guideBoard.getGuideBoard().getBoard().getBoard_paytype());
                    intent.putExtra("board_guidetype", guideBoard.getGuideBoard().getBoard().getBoard_guidetype());
                    intent.putExtra("board_content", guideBoard.getGuideBoard().getBoard().getBoard_content());
                    intent.putExtra("board_complete", guideBoard.getGuideBoard().getBoard().getBoard_complete());
                    intent.putExtra("board_kind", guideBoard.getGuideBoard().getBoard().getBoard_kind());
                    intent.putExtra("member_fk_inc", guideBoard.getGuideBoard().getBoard().getMember_fk_inc());
                    intent.putExtra("guide_id_inc", guideBoard.getGuideBoard().getGuide_add_board().getGuide_id_inc());
                    intent.putExtra("guide_sun", guideBoard.getGuideBoard().getGuide_add_board().getGuide_sun());
                    intent.putExtra("guide_mon", guideBoard.getGuideBoard().getGuide_add_board().getGuide_mon());
                    intent.putExtra("guide_tue", guideBoard.getGuideBoard().getGuide_add_board().getGuide_tue());
                    intent.putExtra("guide_wed", guideBoard.getGuideBoard().getGuide_add_board().getGuide_wed());
                    intent.putExtra("guide_thu", guideBoard.getGuideBoard().getGuide_add_board().getGuide_thu());
                    intent.putExtra("guide_fri", guideBoard.getGuideBoard().getGuide_add_board().getGuide_fri());
                    intent.putExtra("guide_sat", guideBoard.getGuideBoard().getGuide_add_board().getGuide_sat());
                    intent.putExtra("guide_startTime", guideBoard.getGuideBoard().getGuide_add_board().getGuide_startTime());
                    intent.putExtra("guide_endTime", guideBoard.getGuideBoard().getGuide_add_board().getGuide_endTime());
                    intent.putExtra("board_fk_inc", guideBoard.getGuideBoard().getGuide_add_board().getBoard_fk_inc());
                    startActivity(intent);
                }
                else if(selectedText.equals(getResources().getString(R.string.delete))){
                    Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().deleteBoard(guideBoard.getGuideBoard().getBoard().getBoard_id_inc());
                    observ.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(GuideBoardDetailActivity.this,R.string.delete,Toast.LENGTH_SHORT).show();
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

    @OnClick(R.id.btn_guide_apply)
    public void onClickApply(){
        if(loginMember.getMember_kind() == 1){
            Toast.makeText(getApplicationContext(),R.string.onlyGuideApply,Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder dlg = new AlertDialog.Builder(GuideBoardDetailActivity.this);
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
                    Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().applyInsert(loginMember.getMember_id_inc(), guideBoard.getGuideBoard().getBoard().getBoard_id_inc());
                    observ.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),R.string.applyOk,Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

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
        Intent i = new Intent(GuideBoardDetailActivity.this, FirebaseMessageActivity.class);
        i.putExtra("destinationUid",guideBoard.getGuideBoard().getBoard().getMember_fk_inc().toString());
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onClickIntroduction();

        loginMember = loginService.getLoginMember();

        Intent intent = getIntent();
        Long board_id_inc = intent.getLongExtra("board_id_inc",-1);
        Log.d("dsj", board_id_inc+"보드넘버");

        Call<GuideBoardMember> observ =  RetrofitService.getInstance().getRetrofitRequest().getGuideBoard(board_id_inc);

        observ.enqueue(new Callback<GuideBoardMember>() {
            @Override
            public void onResponse(Call<GuideBoardMember> call, Response<GuideBoardMember> response) {
                if(response.isSuccessful()) {
                    guideBoard = response.body();

                    Call<Member> observ2 = RetrofitService.getInstance().getRetrofitRequest().getMemberData(guideBoard.getGuideMemberLicense().getMember_fk_inc());
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

                    Log.d("dsj", guideBoard.toString());

                    guide_title.setText(guideBoard.getGuideBoard().getBoard().getBoard_title());
                    if(guideBoard.getGuideMemberLicense().getLicense_approval() == 1) {
                        iv_license_approval.setVisibility(View.VISIBLE);
                    }
                    else {
                        iv_license_approval.setVisibility(View.GONE);
                    }

                    guideBoardViewPagerAdapter = new GuideBoardViewPagerAdapter(getSupportFragmentManager(), guideBoard);
                    vpGuideBoard.setAdapter(guideBoardViewPagerAdapter);

                    if(loginMember.getMember_id_inc() == guideBoard.getGuideBoard().getBoard().getMember_fk_inc()) {
                        btn_dialog.setVisibility(View.VISIBLE);
                    }
                    else {
                        btn_dialog.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<GuideBoardMember> call, Throwable t) {

            }
        });
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