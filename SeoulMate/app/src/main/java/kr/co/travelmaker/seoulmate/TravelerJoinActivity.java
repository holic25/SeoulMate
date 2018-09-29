package kr.co.travelmaker.seoulmate;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.FirebaseDatabase;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.model.FirebaseMember;
import kr.co.travelmaker.seoulmate.model.IsCheck;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.service.GMailSender;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelerJoinActivity extends AppCompatActivity {

    @BindView(R.id.user_spinner_notification) Spinner user_spinner_notification;
    @BindView(R.id.et_user_id) EditText et_user_id;
    @BindView(R.id.btn_user_id_double_check) Button btn_user_id_double_check;
    @BindView(R.id.txt_user_id_check) TextView txt_user_id_check;
    @BindView(R.id.txt_user_id_db_check) TextView txt_user_id_db_check;
    @BindView(R.id.txt_user_id_db_check_match) TextView txt_user_id_db_check_match;
    @BindView(R.id.txt_user_id_accepted) TextView txt_user_id_accepted;
    @BindView(R.id.et_user_pw) EditText et_user_pw;
    @BindView(R.id.txt_user_pw_check) TextView txt_user_pw_check;
    @BindView(R.id.et_user_pw2) EditText et_user_pw2;
    @BindView(R.id.txt_user_pw_check2) TextView txt_user_pw_check2;
    @BindView(R.id.txt_user_pw_check_match) TextView txt_user_pw_check_match;
    @BindView(R.id.et_user_email) EditText et_user_email;
    @BindView(R.id.et_user_email_adress) EditText et_user_email_adress;
    @BindView(R.id.btn_user_request) Button btn_user_request;
    @BindView(R.id.txt_user_email_check) TextView txt_user_email_check;
    @BindView(R.id.txt_user_email_input) TextView txt_user_email_input;
    @BindView(R.id.et_user_certification_number) EditText et_user_certification_number;
    @BindView(R.id.btn_user_certification_complete) Button btn_user_certification_complete;
    @BindView(R.id.txt_user_certification_check) TextView txt_user_certification_check;
    @BindView(R.id.txt_user_certification_match) TextView txt_user_certification_match;
    @BindView(R.id.txt_user_certification_complete) TextView txt_user_certification_complete;
    @BindView(R.id.et_user_first_name) EditText et_user_first_name;
    @BindView(R.id.et_user_last_name) EditText et_user_last_name;
    @BindView(R.id.txt_user_name_check) TextView txt_user_name_check;
    @BindView(R.id.et_user_birth_day) EditText et_user_birth_day;
    @BindView(R.id.txt_user_birthday_check) TextView txt_user_birthday_check;
    @BindView(R.id.txt_user_nation_check) TextView txt_user_nation_check;
    @BindView(R.id.user_radio_group_gender) RadioGroup user_radio_group_gender;
    @BindView(R.id.user_radio_women) RadioButton user_radio_women;
    @BindView(R.id.user_radio_men) RadioButton user_radio_men;
    @BindView(R.id.txt_user_gender_check) TextView txt_user_gender_check;
    @BindView(R.id.btn_user_sign_up) Button btn_user_sign_up;

    String email_adress = "";
    String e_mail = "";
    String adress = "";
    String Certification_Number;//메일에서 받은 인증번호
    String nationality = ""; //국적
    Integer gender;
    Integer certification = 0;
    Integer doubleCheck = 0;

    IsCheck items;

    Boolean idCheck = false, pwCheck = false,pw2Check = false, emailCheck = false, nameCheck =false;
    Boolean  birthCheck = false, notificationCheck = false, genderCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler_join);
        ButterKnife.bind(this);

        //이메일 이용
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        //스피너 이용
        ArrayAdapter searchAdapter = ArrayAdapter.createFromResource(this,
                R.array.sp_nationality, android.R.layout.simple_spinner_item);
        searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_spinner_notification.setAdapter(searchAdapter);

    }

    @OnClick(R.id.btn_user_id_double_check)
    public void OnClickDoubleCheck() {
        //중복확인버튼 눌렀을 때
        Log.d("msb", "중복확인");
        if (et_user_id.getText().toString().equals("")) {
            if(txt_user_id_db_check.getVisibility() == View.VISIBLE){
                txt_user_id_db_check.setVisibility(View.INVISIBLE);
            }
            txt_user_id_accepted.setVisibility(View.INVISIBLE);
            txt_user_id_check.setVisibility(View.VISIBLE);
        } else {
            txt_user_id_check.setVisibility(View.INVISIBLE);
            Log.d("msb", et_user_id.getText().toString());
            RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), et_user_id.getText().toString());
            Call<IsCheck> observ = RetrofitService.getInstance().getRetrofitRequest().isCheckedId(user_id);
            observ.enqueue(new Callback<IsCheck>() {
                @Override
                public void onResponse(Call<IsCheck> call, Response<IsCheck> response) {
                    if (response.isSuccessful()) {
                        items = response.body();
                        Log.d("dsj", "들어옴 : " + items.toString());
                        if (Integer.parseInt(items.getCheckNumber()) >= 1) {
                            //같은 아이디가 있으면
                            txt_user_id_db_check_match.setVisibility(View.INVISIBLE);
                            txt_user_id_accepted.setVisibility(View.INVISIBLE);
                            txt_user_id_db_check.setVisibility(View.VISIBLE);
                            //사용중인 아이디 입니다.

                        } else {
                            //같은 아이디가 아닐 때
                            txt_user_id_db_check.setVisibility(View.INVISIBLE);
                            txt_user_id_db_check_match.setVisibility(View.INVISIBLE);
                            //사용중인 아이디 입니다.를 없애고
                            //중복확인 해달라는 메세지도 없애고
                            txt_user_id_accepted.setVisibility(View.VISIBLE);
                            //사용 가능한 아이디입니다. 띄움
                        }
                        if (txt_user_id_accepted.getVisibility() == View.VISIBLE) {
                            //사용가능한 아이디입니다 << 일때
                            doubleCheck = 1;
                            idCheck = true;
                        }
                    }
                }

                @Override
                public void onFailure(Call<IsCheck> call, Throwable t) {

                }
            });

        }
    }


    @OnClick(R.id.btn_user_sign_up)
    public void OnClickUserSignUp(){
        //아이디 중복확인 안눌렀을때
        if(doubleCheck == 0){
            txt_user_id_check.setVisibility(View.INVISIBLE);
            txt_user_id_db_check.setVisibility(View.INVISIBLE);
            txt_user_id_db_check_match.setVisibility(View.VISIBLE);
        } else {
            txt_user_id_db_check_match.setVisibility(View.INVISIBLE);
        }

        //첫번째 비밀번호 입력 시
        if (et_user_pw.getText().toString().equals("")){
            txt_user_pw_check.setVisibility(View.VISIBLE);
        } else {
            txt_user_pw_check.setVisibility(View.INVISIBLE);
            pwCheck = true;
            //비밀번호 입력 했을 때. true
        }

        //두번째 비밀번호 입력 시
        if(et_user_pw2.getText().toString().equals("")){
            if(txt_user_pw_check_match.getVisibility() == View.VISIBLE){
                txt_user_pw_check_match.setVisibility(View.INVISIBLE);
            }

            txt_user_pw_check2.setVisibility(View.VISIBLE);
        } else {
            if (txt_user_pw_check2.getVisibility() == View.VISIBLE){
                txt_user_pw_check2.setVisibility(View.INVISIBLE);
            }
            if (et_user_pw.getText().toString().equals(et_user_pw2.getText().toString())){
                //비밀번호가 일치 할 때 true
                pw2Check = true;
                if (txt_user_pw_check2.getVisibility() == View.VISIBLE){
                    txt_user_pw_check2.setVisibility(View.INVISIBLE);
                } else if(txt_user_pw_check_match.getVisibility() == View.VISIBLE){
                    txt_user_pw_check_match.setVisibility(View.INVISIBLE);
                }
            } else {
                txt_user_pw_check_match.setVisibility(View.VISIBLE);
            }
        }

        //이메일 입력 시
        if (et_user_email.getText().toString().equals("") || et_user_email_adress.getText().toString().equals("")){
            txt_user_email_input.setVisibility(View.VISIBLE);
        } else {
            if((et_user_email != null) && (et_user_email_adress != null) &&
                    certification == 0){
                //이메일을 작성했는데 중복확인 버튼을 안 눌렀을 때
                if (txt_user_email_input.getVisibility() == View.VISIBLE){
                    txt_user_email_input.setVisibility(View.INVISIBLE);
                }
                txt_user_email_check.setVisibility(View.VISIBLE);
            }
            if(certification == 1){
                if (txt_user_email_check.getVisibility() == View.VISIBLE) {
                    txt_user_email_check.setVisibility(View.INVISIBLE);
                }
            }
        }

        //이름 입력 시
        if(et_user_first_name.getText().toString().equals("") || et_user_last_name.getText().toString().equals("")){
            txt_user_name_check.setVisibility(View.VISIBLE);
        } else {
            txt_user_name_check.setVisibility(View.INVISIBLE);
            //이름 입력했을 때 true;
            nameCheck = true;
        }

        //생일 입력 시
        if (et_user_birth_day.length() == 8){
            if(txt_user_birthday_check.getVisibility() == View.VISIBLE){
                txt_user_birthday_check.setVisibility(View.INVISIBLE);
            }
            //생일 입력 했을 때 true;
            birthCheck = true;
        } else {
            txt_user_birthday_check.setVisibility(View.VISIBLE);
        }

        //스피너 값 받아옴
        nationality = user_spinner_notification.getSelectedItem().toString();
        Log.d("msb : ",nationality);

        //국적 선택 시
        if(user_spinner_notification.getSelectedItem().toString().equals("국적")){
            txt_user_nation_check.setVisibility(View.VISIBLE);
        } else {
            if(txt_user_nation_check.getVisibility() == View.VISIBLE){
                txt_user_nation_check.setVisibility(View.INVISIBLE);
            }
            //나라 선택 했을 때 true
            notificationCheck = true;
        }

        //성별 선택 시
        int gender_id = user_radio_group_gender.getCheckedRadioButtonId();
        RadioButton gender_select = (RadioButton) findViewById(gender_id);
        if(gender_select == null){
            txt_user_gender_check.setVisibility(View.VISIBLE);
        } else {
            if(txt_user_gender_check.getVisibility() == View.VISIBLE){
                txt_user_gender_check.setVisibility(View.INVISIBLE);
            }

            if(gender_select.getText().toString().equals("여성")){
                gender = 0;
                genderCheck = true;
            } else if(gender_select.getText().toString().equals("남성")){
                gender = 1;
                genderCheck = true;
            }
        }
        Log.d("msb",idCheck.toString() + pwCheck + pw2Check +  emailCheck + nameCheck + birthCheck + notificationCheck + genderCheck);

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), et_user_id.getText().toString());
        RequestBody pw = RequestBody.create(MediaType.parse("text/plain"), et_user_pw.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), email_adress);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), et_user_first_name.getText().toString()+et_user_last_name.getText().toString());
        RequestBody nationality_re = RequestBody.create(MediaType.parse("text/plain"), nationality);
        RequestBody birth = RequestBody.create(MediaType.parse("text/plain"), et_user_birth_day.getText().toString());

        if(idCheck && pwCheck && pw2Check && emailCheck && nameCheck && birthCheck && notificationCheck && genderCheck){
            Call<Member> observ = RetrofitService.getInstance().getRetrofitRequest().SeoulMateMemberInsert(id,pw,email, name,nationality_re, gender, 0,birth);
            observ.enqueue(new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    if(response.isSuccessful()){
                        Member member = response.body();
                        Log.d("datalog","member : "+member.toString());

                        String id_inc = member.getMember_id_inc().toString();
                        String id = member.getMember_id();
                        String type = member.getMember_kind().toString();

                        FirebaseMember fbMember = new FirebaseMember();
                        fbMember.setMember_id_inc(id_inc);
                        fbMember.setMember_id(id);
                        fbMember.setMember_type(type);

                        FirebaseDatabase.getInstance().getReference().child("members").child(id_inc).setValue(fbMember);
                        TravelerJoinActivity.this.finish();
                    }
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {

                }
            });
        }
    }
    @OnClick(R.id.btn_user_certification_complete)
    public void OnClickUserCertificateionComplete(){
        if(et_user_certification_number.getText().toString().equals("")){
            txt_user_certification_check.setVisibility(View.VISIBLE);
        } else {
            txt_user_certification_check.setVisibility(View.INVISIBLE);
        }
        if(Certification_Number == null){

        } else if (Certification_Number.equals(et_user_certification_number.getText().toString())){
            if(txt_user_certification_match.getVisibility() == View.VISIBLE){
                txt_user_certification_match.setVisibility(View.INVISIBLE);
            }
            if(txt_user_email_check.getVisibility() == View.VISIBLE){
                txt_user_email_check.setVisibility(View.INVISIBLE);
            }
            txt_user_certification_complete.setVisibility(View.VISIBLE);
            if (txt_user_certification_complete.getVisibility() == View.VISIBLE){
                //인증 완료 메세지 떴을 때 true
                emailCheck = true;
            }
        } else {
            if (txt_user_certification_complete.getVisibility() == View.VISIBLE){
                txt_user_certification_complete.setVisibility(View.INVISIBLE);
            }
            txt_user_certification_match.setVisibility(View.VISIBLE);
        }
    }
    @OnClick(R.id.btn_user_request)
    public void OnclickUserRequest(){

        e_mail = et_user_email.getText().toString();
        adress = et_user_email_adress.getText().toString();
        email_adress = e_mail + "@" + adress;
        if((et_user_email != null) && (et_user_email_adress != null)){
            try {
                GMailSender gMailSender = new GMailSender("couquedasse1234@gmail.com", "znzmektm1234");
                //GMailSender.sendMail(제목, 본문내용, 받는사람);
                Certification_Number = gMailSender.getEmailCode();
                String title = "서울메이트입니다!\n 아래의 인증번호를 정확하게 입력해주세요. \n";
                gMailSender.sendMail("[서울메이트] 인증번호 발송메일", title + "\n" + "인증번호 : " + "[" + Certification_Number + "]", email_adress);
                Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
            } catch (SendFailedException e) {
                Toast.makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (MessagingException e) {

            } catch (Exception e) {
                e.printStackTrace();
            }
            certification = 1;
        }

    }
}
