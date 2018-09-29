package kr.co.travelmaker.seoulmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.event.CulturalInfoMoveViewPager;
import kr.co.travelmaker.seoulmate.adapter.MainViewPagerAdapter;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.event.Logout;
import kr.co.travelmaker.seoulmate.model.FirebaseChatRoom;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.layout_board_tab) RelativeLayout layoutBoardTab;
    @BindView(R.id.layout_messenger_tab) RelativeLayout layoutMessengerTab;
    @BindView(R.id.layout_information_tab) RelativeLayout layoutInformationTab;
    @BindView(R.id.layout_mypage_tab) RelativeLayout layoutMypageTab;
    @BindView(R.id.ivbtn_board) ImageView ivbtnBoard;
    @BindView(R.id.ivbtn_messenger) ImageView ivbtnMessenger;
    @BindView(R.id.iv_notice_messenger) ImageView ivNoticeMessenger;
    @BindView(R.id.ivbtn_information) ImageView ivbtnInformation;
    @BindView(R.id.ivbtn_mypage) ImageView ivbtnMypage;
    @BindView(R.id.vp_main) ViewPager vpMain;
    MainViewPagerAdapter mainViewPagerAdapter;

    int pageNum = 4;

    RelativeLayout[] relativeLayouts = new RelativeLayout[pageNum];
    ImageView[] ivs = new ImageView[pageNum];

    Integer[] resWhite = new Integer[pageNum];
    Integer[] resViolet = new Integer[pageNum];

    Bus bus = BusProvider.getInstance().getBus();
    LoginService loginService = LoginService.getInstance();
    Member loginMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bus.register(this);

        loginMember = loginService.getLoginMember();

        relativeLayouts[0] = layoutBoardTab;
        relativeLayouts[1] = layoutMessengerTab;
        relativeLayouts[2] = layoutInformationTab;
        relativeLayouts[3] = layoutMypageTab;

        ivs[0] = ivbtnBoard;
        ivs[1] = ivbtnMessenger;
        ivs[2] = ivbtnInformation;
        ivs[3] = ivbtnMypage;

        resWhite[0] = R.drawable.ic_handshake_white;
        resWhite[1] = R.drawable.ic_messenger_white;
        resWhite[2] = R.drawable.ic_brochure_white;
        resWhite[3] = R.drawable.ic_user_white;

        resViolet[0] = R.drawable.ic_handshake_dark_violet;
        resViolet[1] = R.drawable.ic_messenger_dark_violet;
        resViolet[2] = R.drawable.ic_brochure_dark_violet;
        resViolet[3] = R.drawable.ic_user_dark_violet;

        mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        vpMain.setAdapter(mainViewPagerAdapter);

        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int currentIndex) {
                for(int i=0;i<pageNum;i++) {
                    relativeLayouts[i].setBackgroundColor(getColor(MainActivity.this,R.color.transparent));
                    ivs[i].setBackgroundResource(resWhite[i]);
                }

                relativeLayouts[currentIndex].setBackgroundColor(getColor(MainActivity.this,R.color.cf_white));
                ivs[currentIndex].setBackgroundResource(resViolet[currentIndex]);

                CulturalInfoMoveViewPager culturalInfoMoveViewPager = new CulturalInfoMoveViewPager(0);
                bus.post(culturalInfoMoveViewPager);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        passPushTokenToServer();
        checkMessage();
    }

    public void passPushTokenToServer() {
        final String loginMember_id = loginMember.getMember_id_inc().toString();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.d("datalog","new Token : "+newToken);
                Map<String,Object> map = new HashMap<>();
                map.put("member_push_token",newToken);
                FirebaseDatabase.getInstance().getReference().child("members").child(loginMember_id).updateChildren(map);
            }
        });
    }

    @OnClick(R.id.layout_board_tab)
    public void onClickBoardTab() {
        vpMain.setCurrentItem(0);
    }

    @OnClick(R.id.layout_messenger_tab)
    public void onClickMessengerTab() {
        vpMain.setCurrentItem(1);
    }

    @OnClick(R.id.layout_information_tab)
    public void onClickInformationTab() {
        vpMain.setCurrentItem(2);
    }

    @OnClick(R.id.layout_mypage_tab)
    public void onClickMypageTab() {
        vpMain.setCurrentItem(3);
    }

    @Override
    public void onBackPressed() {
        if(vpMain.getCurrentItem()==0) {
            super.onBackPressed();
        }
        else {
            Log.d("datalog","currentItem_before : "+vpMain.getCurrentItem());
            vpMain.setCurrentItem(vpMain.getCurrentItem()-1);
            Log.d("datalog","currentItem_after : "+vpMain.getCurrentItem());
        }
    }

    @Subscribe
    public void logout(Logout event) {
        Log.d("datalog","logout event");
        SharedPreferences pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("id");
        editor.remove("pw");
        editor.commit();

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
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

    public void checkMessage() {
        final String uid = loginMember.getMember_id_inc().toString();

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("members/"+uid).equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasNewMessages = false;

                ArrayList<FirebaseChatRoom> firebaseChatRooms = new ArrayList<>();
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    firebaseChatRooms.add(item.getValue(FirebaseChatRoom.class));
                }

                for(int i=0;i<firebaseChatRooms.size();i++) {
                    Map<String, FirebaseChatRoom.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
                    commentMap.putAll(firebaseChatRooms.get(i).getComments());

                    if(commentMap!=null && commentMap.size()!=0) {
                        for(String key : commentMap.keySet()) {
                            if(!commentMap.get(key).readMembers.containsKey(uid)) {
                                hasNewMessages = true;
                            }
                        }
                    }
                }
                if(hasNewMessages) {
                    ivNoticeMessenger.setVisibility(View.VISIBLE);
                }
                else {
                    ivNoticeMessenger.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent i = getIntent();
        int page = i.getIntExtra("page",0);
        if(page==1) {
            if(vpMain.getCurrentItem()!=1) {
                onClickMessengerTab();
            }
        }
    }
}
