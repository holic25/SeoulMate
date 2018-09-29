package kr.co.travelmaker.seoulmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.event.Logout;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelerEditProfileActivity extends AppCompatActivity {

    @BindView(R.id.iv_gender) ImageView ivGender;
    @BindView(R.id.tv_id) TextView tvId;
    @BindView(R.id.et_current_pw) EditText etCurrentPw;
    @BindView(R.id.et_new_pw) EditText etNewPw;
    @BindView(R.id.et_confirm_new_pw) EditText etConfirmNewPw;
    @BindView(R.id.tv_current_pw_check) TextView tvCurrentPwCheck;
    @BindView(R.id.tv_current_pw_wrong) TextView tvCurrentPwWrong;
    @BindView(R.id.tv_new_pw_check) TextView tvNewPwCheck;
    @BindView(R.id.tv_confirm_new_pw_check) TextView tvConfirmNewCheck;
    @BindView(R.id.tv_new_pw_check_match) TextView tvNewPwCheckMatch;
    @BindView(R.id.btn_edit_profile) Button btnEditProfile;

    LoginService loginService = LoginService.getInstance();
    Member loginMember;

    Bus bus = BusProvider.getInstance().getBus();

    boolean isCurrPwChecked = false, isCurrPwRight = false;
    boolean isNewPwChecked = false, isConfirmNewPwChecked = false, isNewPwMatched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler_edit_profile);
        ButterKnife.bind(this);
        bus.register(this);

        loginMember = loginService.getLoginMember();

        if(loginMember.getMember_gender()==0) {
            ivGender.setBackgroundResource(R.drawable.ic_user_female);
        }
        else {
            ivGender.setBackgroundResource(R.drawable.ic_user_male);
        }

        tvId.setText(loginMember.getMember_id());
    }

    @OnClick(R.id.btn_edit_profile)
    public void onClickEditProfile() {
        String currPw = etCurrentPw.getText().toString();
        String newPw = etNewPw.getText().toString();
        String confirmNewPw = etConfirmNewPw.getText().toString();

        // 현재 비밀번호 공백 체크
        if(currPw.equals("")) {
            tvCurrentPwWrong.setVisibility(View.INVISIBLE);

            tvCurrentPwCheck.setVisibility(View.VISIBLE);
            isCurrPwChecked = false;
        }
        else {
            tvCurrentPwCheck.setVisibility(View.INVISIBLE);
            isCurrPwChecked = true;

            // 현재 비밀번호 equal 체크
            if(currPw.equals(loginMember.getMember_pw())) {
                tvCurrentPwWrong.setVisibility(View.INVISIBLE);
                isCurrPwRight = true;
            }
            else {
                tvCurrentPwWrong.setVisibility(View.VISIBLE);
                isCurrPwRight = false;
            }
        }

        // 새 비밀번호 공백 체크
        if(newPw.equals("")) {
            tvNewPwCheck.setVisibility(View.VISIBLE);
            isNewPwChecked = false;
        }
        else {
            tvNewPwCheck.setVisibility(View.INVISIBLE);
            isNewPwChecked = true;
        }

        // 새 비밀번호 확인 공백 체크
        if(confirmNewPw.equals("")) {
            tvNewPwCheckMatch.setVisibility(View.INVISIBLE);
            tvConfirmNewCheck.setVisibility(View.VISIBLE);
            isConfirmNewPwChecked = false;
        }
        else {
            tvConfirmNewCheck.setVisibility(View.INVISIBLE);
            isConfirmNewPwChecked = true;

            // 새 비밀번호, 새 비밀번호 확인 equal 체크
            if(newPw.equals(confirmNewPw)) {
                tvNewPwCheckMatch.setVisibility(View.INVISIBLE);
                isNewPwMatched = true;
            }
            else {
                tvNewPwCheckMatch.setVisibility(View.VISIBLE);
                isNewPwMatched = false;
            }
        }

        RequestBody newPw_re = RequestBody.create(MediaType.parse("text/plain"), etNewPw.getText().toString());

        if(isCurrPwChecked && isCurrPwRight && isNewPwChecked && isConfirmNewPwChecked && isNewPwMatched) {
            Log.d("datalog","updateMemberData");
            Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().updateMemberData(loginMember.getMember_id_inc(), loginMember.getMember_kind(), newPw_re);
            observ.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("datalog","updateMemberData_success");
                    Logout logout = new Logout();
                    bus.post(logout);
                    finish();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("datalog","updateMemberData_fail");
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}