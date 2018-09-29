package kr.co.travelmaker.seoulmate;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.squareup.otto.Bus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.event.GuideBoard;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideBoardWriteActivity extends AppCompatActivity {

    @BindView(R.id.et_title) EditText etTitle;
    @BindView(R.id.btn_sun) Button btnSun;
    @BindView(R.id.btn_mon) Button btnMon;
    @BindView(R.id.btn_tue) Button btnTue;
    @BindView(R.id.btn_wed) Button btnWed;
    @BindView(R.id.btn_thu) Button btnThu;
    @BindView(R.id.btn_fri) Button btnFri;
    @BindView(R.id.btn_sat) Button btnSat;
    @BindView(R.id.sp_start_time) Spinner spStartTime;
    @BindView(R.id.sp_end_time) Spinner spEndTime;
    @BindView(R.id.sp_payment_type) Spinner spPaymentType;
    @BindView(R.id.sp_guide_type) Spinner spGuideType;
    @BindView(R.id.et_place) EditText etPlace;
    @BindView(R.id.et_introduction) EditText etIntroduction;
    @BindView(R.id.btn_upload) Button btnUpload;

    int[] days = {R.string.sun, R.string.mon, R.string.tue, R.string.wed, R.string.thu, R.string.fri, R.string.sat};
    Button[] btnDays = new Button[7];
    boolean[] isSelectDays = new boolean[7];

    LoginService loginService = LoginService.getInstance();
    int[] days_array = {0,0,0,0,0,0,0};

    Bus bus = BusProvider.getInstance().getBus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_board_write);
        ButterKnife.bind(this);
        bus.register(this);

        btnDays[0] = btnSun;
        btnDays[1] = btnMon;
        btnDays[2] = btnTue;
        btnDays[3] = btnWed;
        btnDays[4] = btnThu;
        btnDays[5] = btnFri;
        btnDays[6] = btnSat;

        for(int i=0;i<btnDays.length;i++) {
            final int finalI = i;
            btnDays[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isSelectDays[finalI] = !isSelectDays[finalI];
                    selectDays(finalI);
                    if(days_array[finalI] == 0) {
                        days_array[finalI] = 1;
                    }else{
                        days_array[finalI] = 0;
                    }
                }
            });
        }

        ArrayAdapter startTimeAdapter = ArrayAdapter.createFromResource(this,R.array.sp_time, android.R.layout.simple_spinner_item);
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStartTime.setAdapter(startTimeAdapter);

        ArrayAdapter endTimeAdapter = ArrayAdapter.createFromResource(this,R.array.sp_time, android.R.layout.simple_spinner_item);
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEndTime.setAdapter(endTimeAdapter);

        ArrayAdapter paymentAdapter = ArrayAdapter.createFromResource(this,R.array.sp_payment_type, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentType.setAdapter(paymentAdapter);

        ArrayAdapter guideAdapter = ArrayAdapter.createFromResource(this,R.array.sp_guide_type, android.R.layout.simple_spinner_item);
        guideAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGuideType.setAdapter(guideAdapter);
    }

    public void selectDays(int i) {
        if(isSelectDays[i]) {
            btnDays[i].setBackgroundResource(R.drawable.shape_rectangle_dark_violet_full_30);
            btnDays[i].setTextColor(getColor(GuideBoardWriteActivity.this,R.color.cf_white));
        }
        else {
            btnDays[i].setBackgroundResource(R.drawable.shape_rectangle_dark_violet_line_30);
            btnDays[i].setTextColor(getColor(GuideBoardWriteActivity.this,R.color.dark_violet));
        }
    }

    @OnClick(R.id.btn_upload)
    public void onClickUpload() {
        String possibleDays = "";

        for(int i=0;i<isSelectDays.length;i++) {
            if(isSelectDays[i]) {
                possibleDays+=getResources().getString(days[i])+",";
            }
        }

        possibleDays = possibleDays.substring(0,possibleDays.length()-1);

        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), etTitle.getText().toString());
        RequestBody place = RequestBody.create(MediaType.parse("text/plain"), etPlace.getText().toString());
        RequestBody paymentType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(spPaymentType.getSelectedItemPosition()));
        RequestBody guideType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(spGuideType.getSelectedItemPosition()));
        RequestBody introduction = RequestBody.create(MediaType.parse("text/plain"), etIntroduction.getText().toString());
        RequestBody endTime = RequestBody.create(MediaType.parse("text/plain"), spEndTime.getSelectedItem().toString());
        RequestBody startTime = RequestBody.create(MediaType.parse("text/plain"), spStartTime.getSelectedItem().toString());

        Log.d("datalog",title+" "+possibleDays+"  "+place+" "+paymentType+" "+guideType+" "+introduction);

        Member loginMember = loginService.getLoginMember();
        Log.d("dsj", loginMember.getMember_kind()+"이게 진짜 카인드");

        Call<Void> observ =  RetrofitService.getInstance().getRetrofitRequest()
                .BoardGuideInsert(title,place,paymentType,guideType,introduction,0,loginMember.getMember_kind(),loginMember.getMember_id_inc(),days_array[0],days_array[1],days_array[2],days_array[3],days_array[4],days_array[5],days_array[6],startTime,endTime);

        observ.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    GuideBoard guideBoard = new GuideBoard();
                    bus.post(guideBoard);
                    GuideBoardWriteActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
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
}