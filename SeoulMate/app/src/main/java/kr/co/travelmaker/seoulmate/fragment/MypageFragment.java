package kr.co.travelmaker.seoulmate.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.GuideEditProfileActivity;
import kr.co.travelmaker.seoulmate.LicensesActivity;
import kr.co.travelmaker.seoulmate.LoginActivity;
import kr.co.travelmaker.seoulmate.MatchingHistoryActivity;
import kr.co.travelmaker.seoulmate.MyBoardsActivity;
import kr.co.travelmaker.seoulmate.MyReviewsActivity;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.RequestActivity;
import kr.co.travelmaker.seoulmate.TravelerEditProfileActivity;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.event.Logout;
import kr.co.travelmaker.seoulmate.event.RequestDataChanged;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.model.TravelerBoard;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MypageFragment extends Fragment {

    Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;

    @BindView(R.id.tv_edit_profile) TextView tvEditProfile;
    @BindView(R.id.iv_gender) ImageView ivGender;
    @BindView(R.id.tv_id) TextView tvId;
    @BindView(R.id.btn_logout) Button btnLogout;
    @BindView(R.id.tv_manage_my_posts) TextView tvManageMyPosts;
    @BindView(R.id.tv_manage_requests) TextView tvManageRequests;
    @BindView(R.id.tv_request_count) TextView tvRequestCount;
    @BindView(R.id.tv_matching_history) TextView tvMatchingHistory;
    @BindView(R.id.tv_review_list) TextView tvReviewList;
    @BindView(R.id.tv_delete_account) TextView tvDeleteAccount;
    @BindView(R.id.sw_messenger_notification) Switch swMessengerNotification;

    LoginService loginService = LoginService.getInstance();
    Member loginMember;

    static int FEMALE = 0;
    static int MALE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        unbinder = ButterKnife.bind(this,view);
        bus.register(this);

        loginMember = loginService.getLoginMember();

        if(loginMember.getMember_gender()==FEMALE) {
            ivGender.setBackgroundResource(R.drawable.ic_user_female);
        }
        else if(loginMember.getMember_gender()==MALE) {
            ivGender.setBackgroundResource(R.drawable.ic_user_male);
        }

        tvId.setText(loginMember.getMember_id());

        SharedPreferences pref = getContext().getSharedPreferences("messenger_notification", MODE_PRIVATE);
        Boolean set_notification = pref.getBoolean("set_notification",true);
        Log.d("datalog","onCreateView : set_notification = "+set_notification);

        swMessengerNotification.setChecked(set_notification);

        swMessengerNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Log.d("datalog","setOnCheckedChangeListener : isChecked = "+isChecked);
                SharedPreferences pref = getContext().getSharedPreferences("messenger_notification", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("set_notification", isChecked);
                editor.commit();
            }
        });

        refreshRequestCount();

        return view;
    }

    @OnClick(R.id.btn_logout)
    public void onClickLogout() {
        Logout logout = new Logout();
        bus.post(logout);
    }

    @OnClick(R.id.tv_manage_my_posts)
    public void onClickManageMyPosts() {
        Intent i = new Intent(getContext(), MyBoardsActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.tv_manage_requests)
    public void onClickManageRequest(){
        Intent i = new Intent(getContext(), RequestActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.tv_matching_history)
    public void onClickMatchingHistory(){
        Intent i = new Intent(getContext(), MatchingHistoryActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.tv_review_list)
    public void onClickReviewList(){
        Intent i = new Intent(getContext(), MyReviewsActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.tv_edit_profile)
    public void onClickEditProfile() {
        if(loginMember.getMember_kind()==0) {
            Intent i = new Intent(getContext(), TravelerEditProfileActivity.class);
            startActivity(i);
        }
        else {
            Intent i = new Intent(getContext(), GuideEditProfileActivity.class);
            startActivity(i);
        }
    }

    @OnClick(R.id.tv_delete_account)
    public void onClickDeleteAccount() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
        dlg.setTitle(R.string.delete);
        dlg.setMessage(R.string.confirm_delete_account);
        dlg.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dlg.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(loginMember.getMember_kind()==0) {
                    Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().deleteTravelerMemberData(loginMember.getMember_id_inc());
                    observ.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("datalog", "deleteTravelerMemberData_success : "+loginMember.getMember_id_inc());
                            Logout logout = new Logout();
                            bus.post(logout);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("datalog", "deleteTravelerMemberData_fail");
                        }
                    });
                }

                else {
                    Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().deleteGuideMemberData(loginMember.getMember_id_inc());
                    observ.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("datalog", "deleteGuideMemberData_success : "+loginMember.getMember_id_inc());
                            Logout logout = new Logout();
                            bus.post(logout);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.d("datalog", "deleteGuideMemberData_fail");
                        }
                    });
                }
            }
        });
        dlg.show();
    }

    @OnClick(R.id.tv_licenses)
    public void OnClickLisence(){
        Intent i = new Intent(getContext(), LicensesActivity.class);
        startActivity(i);
    }

    public void refreshRequestCount() {
        Call<Integer> observ = RetrofitService.getInstance().getRetrofitRequest().getApplyCount(loginMember.getMember_id_inc());
        observ.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer count = response.body();

                if(count>0) {
                    tvRequestCount.setText(count.toString());
                    tvRequestCount.setVisibility(View.VISIBLE);
                }
                else {
                    tvRequestCount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void requestDataChanged(RequestDataChanged event) {
        refreshRequestCount();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}