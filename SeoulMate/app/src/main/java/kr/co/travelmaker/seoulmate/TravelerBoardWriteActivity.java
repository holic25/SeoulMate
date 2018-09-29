package kr.co.travelmaker.seoulmate;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.otto.Bus;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.event.TravelerBoard;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelerBoardWriteActivity extends AppCompatActivity {

    @BindView(R.id.et_title) EditText etTitle;
    @BindView(R.id.tv_travel_first_date) TextView tvTravelFirstDay;
    @BindView(R.id.tv_travel_last_date) TextView tvTravelLastDay;
    @BindView(R.id.sp_payment_type) Spinner spPaymentType;
    @BindView(R.id.sp_guide_type) Spinner spGuideType;
    @BindView(R.id.et_place) EditText etPlace;
    @BindView(R.id.et_introduction) EditText etIntroduction;
    @BindView(R.id.btn_upload) Button btnUpload;

    String firstDay;
    String lastDay;

    Calendar cal = Calendar.getInstance();
    int currYear, currMonth, currDay;

    LoginService loginService = LoginService.getInstance();

    Bus bus = BusProvider.getInstance().getBus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler_board_write);
        ButterKnife.bind(this);
        bus.register(this);

        currYear = cal.get(Calendar.YEAR);
        currMonth = cal.get(Calendar.MONTH);
        currDay = cal.get(Calendar.DAY_OF_MONTH);

        String currDate;
        currDate = currYear+"-"+(currMonth+1)+"-"+currDay;

        tvTravelFirstDay.setText(currDate);
        tvTravelLastDay.setText(currDate);

        ArrayAdapter paymentAdapter = ArrayAdapter.createFromResource(this,R.array.sp_payment_type, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentType.setAdapter(paymentAdapter);

        ArrayAdapter guideAdapter = ArrayAdapter.createFromResource(this,R.array.sp_guide_type, android.R.layout.simple_spinner_item);
        guideAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGuideType.setAdapter(guideAdapter);
    }

    @OnClick(R.id.tv_travel_first_date)
    public void onClickTravelFirstDay() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Log.d("datalog",year+" "+(month+1)+" "+day);
                tvTravelFirstDay.setText(year+"-"+(month+1)+"-"+day);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(TravelerBoardWriteActivity.this, dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @OnClick(R.id.tv_travel_last_date)
    public void onClickTravelLastDay() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Log.d("datalog",year+" "+(month+1)+" "+day);
                tvTravelLastDay.setText(year+"-"+(month+1)+"-"+day);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(TravelerBoardWriteActivity.this, dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @OnClick(R.id.btn_upload)
    public void onClickUpload() {

        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), etTitle.getText().toString());
        RequestBody firstDay = RequestBody.create(MediaType.parse("text/plain"), tvTravelFirstDay.getText().toString());
        RequestBody lastDay = RequestBody.create(MediaType.parse("text/plain"), tvTravelLastDay.getText().toString());
        RequestBody place = RequestBody.create(MediaType.parse("text/plain"), etPlace.getText().toString());
        RequestBody paymentType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(spPaymentType.getSelectedItemPosition()));
        RequestBody guideType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(spGuideType.getSelectedItemPosition()));
        RequestBody introduction = RequestBody.create(MediaType.parse("text/plain"), etIntroduction.getText().toString());

        Log.d("datalog",title+" "+firstDay+" "+lastDay+" "+paymentType+" "+guideType+" "+place+" "+introduction);
        Log.d("datalog",tvTravelFirstDay.getText().toString()+" "+tvTravelLastDay.getText().toString());

        Member loginMember = loginService.getLoginMember();

        Call<Void> observ =  RetrofitService.getInstance().getRetrofitRequest()
                .BoardTravelerInsert(title,place,paymentType,guideType,introduction,0,loginMember.getMember_kind(),loginMember.getMember_id_inc(),firstDay,lastDay);
        observ.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    TravelerBoard travelerBoard = new TravelerBoard();
                    bus.post(travelerBoard);
                    TravelerBoardWriteActivity.this.finish();
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
}
