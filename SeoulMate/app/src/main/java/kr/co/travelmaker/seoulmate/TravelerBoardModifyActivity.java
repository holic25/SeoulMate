package kr.co.travelmaker.seoulmate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.model.Board;
import kr.co.travelmaker.seoulmate.model.GuideAddBoard;
import kr.co.travelmaker.seoulmate.model.GuideBoard;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.model.TravelerAddBoard;
import kr.co.travelmaker.seoulmate.model.TravelerBoard;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelerBoardModifyActivity extends AppCompatActivity {
    @BindView(R.id.et_title_modify) EditText et_title_modify;
    @BindView(R.id.et_travel_first_date_modify) TextView et_travel_first_date_modify;
    @BindView(R.id.et_travel_last_date_modify) TextView et_travel_last_date_modify;
    @BindView(R.id.et_place_modify) EditText et_place_modify;
    @BindView(R.id.sp_payment_type_modify) Spinner sp_payment_type_modify;
    @BindView(R.id.sp_guide_type_modify) Spinner sp_guide_type_modify;
    @BindView(R.id.et_introduction_modify) EditText et_introduction_modify;
    @BindView(R.id.btn_upload_modify) Button btn_upload_modify;

    Calendar cal = Calendar.getInstance();
    LoginService loginService = LoginService.getInstance();

    TravelerBoard travelerBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveler_board_modify);
        ButterKnife.bind(this);

        ArrayAdapter paymentAdapter = ArrayAdapter.createFromResource(this,R.array.sp_payment_type, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_payment_type_modify.setAdapter(paymentAdapter);

        ArrayAdapter guideAdapter = ArrayAdapter.createFromResource(this,R.array.sp_guide_type, android.R.layout.simple_spinner_item);
        guideAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_guide_type_modify.setAdapter(guideAdapter);

        Intent intent = getIntent();
        Long board_id_inc = intent.getLongExtra("board_id_inc",0);
        String board_title = intent.getStringExtra("board_title");
        String board_place = intent.getStringExtra("board_place");
        Integer board_paytype = intent.getIntExtra("board_paytype",-1);
        Integer board_guidetype = intent.getIntExtra("board_guidetype",-1);
        String board_content = intent.getStringExtra("board_content");
        Integer board_complete = intent.getIntExtra("board_complete",0);
        Integer board_kind = intent.getIntExtra("board_kind",0);
        Long member_fk_inc = intent.getLongExtra("member_fk_inc",0);
        Long guide_id_inc = intent.getLongExtra("guide_id_inc",0);
        Long traveler_id_board_inc = intent.getLongExtra("traveler_id_board_inc", 0);
        String traveler_id_board_startdate = intent.getStringExtra("traveler_id_board_startdate");
        String traveler_id_board_enddate = intent.getStringExtra("traveler_id_board_enddate");
        Long board_fk_inc = intent.getLongExtra("board_fk_inc",0);

        Board board = new Board(board_id_inc, board_title, board_place, board_paytype, board_guidetype,board_content,board_complete,board_kind,member_fk_inc);
        TravelerAddBoard travelerAddBoard = new TravelerAddBoard(traveler_id_board_inc,traveler_id_board_startdate,traveler_id_board_enddate,board_fk_inc);
        travelerBoard = new TravelerBoard(board, travelerAddBoard);
        Log.d("dsj", travelerBoard.toString());

        et_title_modify.setText(travelerBoard.getBoard().getBoard_title());
        et_place_modify.setText(travelerBoard.getBoard().getBoard_place());
        et_introduction_modify.setText(travelerBoard.getBoard().getBoard_content());
        et_travel_first_date_modify.setText(travelerBoard.getTraveler_add_board().getTraveler_id_board_startdate().substring(0,10));
        et_travel_last_date_modify.setText(travelerBoard.getTraveler_add_board().getTraveler_id_board_enddate().substring(0,10));

        if(travelerBoard.getBoard().getBoard_paytype()==1){
            Log.d("dsj", getResources().getString(R.string.cash));
            sp_payment_type_modify.setSelection(1);
        }else if(travelerBoard.getBoard().getBoard_paytype()==2){
            Log.d("dsj", getResources().getString(R.string.goods));
            sp_payment_type_modify.setSelection(2);
        }else{
            sp_payment_type_modify.setSelection(0);
        }

        if(travelerBoard.getBoard().getBoard_guidetype()==1){
            Log.d("dsj", getResources().getString(R.string.drive_guide));
            sp_guide_type_modify.setSelection(1);
        }else if(travelerBoard.getBoard().getBoard_guidetype()==2){
            Log.d("dsj", getResources().getString(R.string.guide_only));
            sp_guide_type_modify.setSelection(2);
        }else{
            sp_guide_type_modify.setSelection(0);
        }

    }

    @OnClick(R.id.et_travel_first_date_modify)
    public void onClickTravelFirstDay() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Log.d("datalog",year+" "+(month+1)+" "+day);
                et_travel_first_date_modify.setText(year+"-"+(month+1)+"-"+day);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(TravelerBoardModifyActivity.this, dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @OnClick(R.id.et_travel_last_date_modify)
    public void onClickTravelLastDay() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Log.d("datalog",year+" "+(month+1)+" "+day);
                et_travel_last_date_modify.setText(year+"-"+(month+1)+"-"+day);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(TravelerBoardModifyActivity.this, dateSetListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @OnClick(R.id.btn_upload_modify)
    public void onClickUpload() {

        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), et_title_modify.getText().toString());
        RequestBody firstDay = RequestBody.create(MediaType.parse("text/plain"), et_travel_first_date_modify.getText().toString());
        RequestBody lastDay = RequestBody.create(MediaType.parse("text/plain"), et_travel_last_date_modify.getText().toString());
        RequestBody place = RequestBody.create(MediaType.parse("text/plain"), et_place_modify.getText().toString());
        RequestBody paymentType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(sp_payment_type_modify.getSelectedItemPosition()));
        RequestBody guideType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(sp_guide_type_modify.getSelectedItemPosition()));
        RequestBody introduction = RequestBody.create(MediaType.parse("text/plain"), et_introduction_modify.getText().toString());

        Log.d("datalog",et_travel_first_date_modify.getText().toString()+" "+et_travel_last_date_modify.getText().toString());



        Call<Void> observ =  RetrofitService.getInstance().getRetrofitRequest()
                .BoardTravelerUpdate(travelerBoard.getBoard().getBoard_id_inc(),title,place,paymentType,guideType,introduction,travelerBoard.getBoard().getBoard_kind(),firstDay,lastDay);
        //업데이트
        observ.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Log.d("dsj","성공");
                    TravelerBoardModifyActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("dsj",t+"오류");
            }
        });
    }
}