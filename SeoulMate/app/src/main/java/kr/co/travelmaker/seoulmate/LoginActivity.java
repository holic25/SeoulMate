package kr.co.travelmaker.seoulmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.model.GoogleAccount;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1000;
    GoogleSignInClient mGoogleSignInClient;

    @BindView(R.id.et_username) EditText et_username;
    @BindView(R.id.et_password) EditText et_password;
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.check_auto_login) CheckBox check_auto_login;
    @BindView(R.id.txt_sign_up) TextView txt_sign_up;
    @BindView(R.id.txt_google_id) TextView txt_google_id;

    static int ORIGINAL = 0;
    static int GOOGLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SharedPreferences pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        String auto_id = pref.getString("id","");
        String auto_pw = pref.getString("pw","");

        Log.d("datalog","autoLogin: id="+auto_id+" / pw="+auto_pw);

        if(!auto_id.equals("")&&!auto_pw.equals("")) {
            et_username.setText(auto_id);
            et_password.setText(auto_pw);
            OnclickBtnLogin();
        }
    }

    @OnClick(R.id.txt_sign_up)
    public void OnclickSignUp(){
        Intent intent =  new Intent(LoginActivity.this,JoinSelActivity.class);
        intent.putExtra("join_type",ORIGINAL);
        startActivity(intent);
    }

    @OnClick(R.id.btn_login)
    public void OnclickBtnLogin(){
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), et_username.getText().toString());
        RequestBody pw = RequestBody.create(MediaType.parse("text/plain"), et_password.getText().toString());

        if(et_username.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),R.string.enter_id,Toast.LENGTH_SHORT).show();
        } else if(et_password.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),R.string.enter_pw,Toast.LENGTH_SHORT).show();
        } else {

            Call<Member> observ = RetrofitService.getInstance().getRetrofitRequest().loginCheck(id, pw);
            observ.enqueue(new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    if (response.isSuccessful()) {
                        Member member = response.body();

                        if (member != null) {
                            Log.d("datalog", "loginMember : " + member.toString());
                            LoginService loginService = LoginService.getInstance();
                            loginService.setLoginMember(member);

                            if (check_auto_login.isChecked()) {
                                Log.d("datalog", "autoLogin Checked");
                                SharedPreferences pref = getSharedPreferences("auto_login", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("id", member.getMember_id());
                                editor.putString("pw", member.getMember_pw());
                                editor.commit();
                            }

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),R.string.check_meminfo,Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {

                }
            });
        }
    }

    @OnClick(R.id.txt_google_id)
    public void onClickGoogleId() {
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("datalog","personName  : "+account.getDisplayName());
            Log.d("datalog","personGivenName  : "+account.getGivenName());
            Log.d("datalog","personFamilyName  : "+account.getFamilyName());
            Log.d("datalog","personEmail  : "+account.getEmail());
            // Signed in successfully, show authenticated UI.

            GoogleAccount googleAccount = GoogleAccount.getInstance();
            googleAccount.setSignInAccount(account);

            Intent intent =  new Intent(LoginActivity.this,JoinSelActivity.class);
            intent.putExtra("join_type",GOOGLE);
            startActivity(intent);

        } catch (ApiException e) {
            Toast.makeText(this,R.string.fail_google,Toast.LENGTH_SHORT).show();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("datalog", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}
