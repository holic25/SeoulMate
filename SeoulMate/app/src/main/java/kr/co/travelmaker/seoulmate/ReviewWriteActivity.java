package kr.co.travelmaker.seoulmate;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.adapter.GuideBoardViewPagerAdapter;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;
import kr.co.travelmaker.seoulmate.model.MatchMember;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewWriteActivity extends AppCompatActivity {
    @BindView(R.id.img_be_evaluated) ImageView img_be_evaluated;
    @BindView(R.id.txt_id_be_evaluated) TextView txt_id_be_evaluated;
    @BindView(R.id.img_evaluate) ImageView img_evaluate;
    @BindView(R.id.txt_evaluate) TextView txt_evaluate;
    @BindView(R.id.txt_date) TextView txt_date;
    @BindView(R.id.et_review_write) EditText et_review_write;
    @BindView(R.id.Rb_stars) RatingBar Rb_stars;
    @BindView(R.id.btn_write) Button btn_write;
    MatchMember matchMember;
    Long getMatch_id_inc;
    Long getMember_id;
    float Rating = 3;

    LoginService loginService = LoginService.getInstance();
    Member loginMember;

    Calendar calendar = Calendar.getInstance();
    Integer year;
    Integer month;
    Integer day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        ButterKnife.bind(this);
        year =  calendar.get(Calendar.YEAR);
        month =calendar.get(Calendar.MONTH);
        month ++;
        day =calendar.get(Calendar.DAY_OF_MONTH);

        txt_date.setText(year.toString() + "-" + month.toString() + "-" + day.toString());
        Intent intent = getIntent();
        getMatch_id_inc = intent.getLongExtra("getMatch_id_inc", -1);
        getMember_id = intent.getLongExtra("getMember_id_inc", -1);

        Log.d("msb","getMatch_id_inc :" + getMatch_id_inc.toString());
        Log.d("msb","getMember_id :" + getMember_id.toString());

        //서버에서 값을 받아 (Member, Match)
        Call<MatchMember> observ =  RetrofitService.getInstance().getRetrofitRequest().getReviewMatchMember(getMatch_id_inc, getMember_id);
        observ.enqueue(new Callback<MatchMember>() {
            @Override
            public void onResponse(Call<MatchMember> call, Response<MatchMember> response) {
                if(response.isSuccessful()) {
                    matchMember = response.body();
                    Log.d("msb" , "matchmember : " + matchMember.toString());

                    loginMember = loginService.getLoginMember();

                    txt_id_be_evaluated.setText(matchMember.getMember().getMember_id());
                    txt_evaluate.setText(loginMember.getMember_id());
                }
            }

            @Override
            public void onFailure(Call<MatchMember> call, Throwable t) {

            }
        });

        //별점
        Rb_stars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override

            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Rating = rating;
                Log.d("msb",String.valueOf(Rating));
            }

        });
    }

    @OnClick(R.id.btn_write)
    public void OnClinckBtnWrite(){
        //작성버튼
        if(et_review_write.getText().toString().equals("")){
            Toast.makeText(this,"내용을 입력해주세요.",Toast.LENGTH_SHORT).show();
        } else {

            AlertDialog.Builder dlg = new AlertDialog.Builder(ReviewWriteActivity.this);
            dlg.setTitle("작성하기");
            dlg.setMessage("리뷰를 작성하시겠습니까?");

            dlg.setPositiveButton("네", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //insert부분
                    Call<Void> observ =  RetrofitService.getInstance().getRetrofitRequest().insertReview(matchMember.getMatch().getGuide_id_inc(),matchMember.getMatch().getTraveler_id_inc(),year.toString() + "-" + month.toString() + "-" + day.toString(),
                            et_review_write.getText().toString(),Rating);
                    observ.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("msb", "insert됨");
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });

                    //콜을 해서 포스트를 보내 내가 작성했으니가 complet2 로 바꾸라고
                    Call<Void> observ2 =  RetrofitService.getInstance().getRetrofitRequest().updateReviewMatchMemberComplete(getMatch_id_inc);
                    observ2.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("msb","updateCallback 옴");
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
            });

            dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            if (isFinishing() == false) {
                dlg.show();
            }
        }
    }
}