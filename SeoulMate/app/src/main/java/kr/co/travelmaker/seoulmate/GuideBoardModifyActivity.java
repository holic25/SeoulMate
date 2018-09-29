package kr.co.travelmaker.seoulmate;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.model.Board;
import kr.co.travelmaker.seoulmate.model.GuideAddBoard;
import kr.co.travelmaker.seoulmate.model.GuideBoard;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class  GuideBoardModifyActivity extends AppCompatActivity {
    @BindView(R.id.et_title_modify) EditText et_title_modify;
    @BindView(R.id.btn_sun) Button btn_sun;
    @BindView(R.id.btn_mon) Button btn_mon;
    @BindView(R.id.btn_tue) Button btn_tue;
    @BindView(R.id.btn_wed) Button btn_wed;
    @BindView(R.id.btn_thu) Button btn_thu;
    @BindView(R.id.btn_fri) Button btn_fri;
    @BindView(R.id.btn_sat) Button btn_sat;
    @BindView(R.id.sp_start_time_modify) Spinner sp_start_time_modify;
    @BindView(R.id.sp_end_time_modify) Spinner sp_end_time_modify;
    @BindView(R.id.et_place_modify) EditText et_place_modify;
    @BindView(R.id.sp_payment_type_modify) Spinner sp_payment_type_modify;
    @BindView(R.id.sp_guide_type_modify) Spinner sp_guide_type_modify;
    @BindView(R.id.et_introduction_modify) EditText et_introduction_modify;
    @BindView(R.id.btn_upload_modify) Button btn_upload_modify;

    int[] days = {R.string.sun, R.string.mon, R.string.tue, R.string.wed, R.string.thu, R.string.fri, R.string.sat};
    Button[] btnDays = new Button[7];
    boolean[] isSelectDays = new boolean[7];

    LoginService loginService = LoginService.getInstance();
    int[] days_array = new int[7];
    GuideBoard guideBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_board_modify);
        ButterKnife.bind(this);

        ArrayAdapter startTimeAdapter = ArrayAdapter.createFromResource(this,R.array.sp_time, android.R.layout.simple_spinner_item);
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_start_time_modify.setAdapter(startTimeAdapter);

        ArrayAdapter endTimeAdapter = ArrayAdapter.createFromResource(this,R.array.sp_time, android.R.layout.simple_spinner_item);
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_end_time_modify.setAdapter(endTimeAdapter);

        ArrayAdapter paymentAdapter = ArrayAdapter.createFromResource(this,R.array.sp_payment_type, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_payment_type_modify.setAdapter(paymentAdapter);

        ArrayAdapter guideAdapter = ArrayAdapter.createFromResource(this,R.array.sp_guide_type, android.R.layout.simple_spinner_item);
        guideAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_guide_type_modify.setAdapter(guideAdapter);


        Intent intent = getIntent();
        Long board_id_inc = intent.getLongExtra("board_id_inc",-1);
        String board_title = intent.getStringExtra("board_title");
        String board_place = intent.getStringExtra("board_place");
        Integer board_paytype = intent.getIntExtra("board_paytype",-1);
        Integer board_guidetype = intent.getIntExtra("board_guidetype",-1);
        String board_content = intent.getStringExtra("board_content");
        Integer board_complete = intent.getIntExtra("board_complete",-1);
        Integer board_kind = intent.getIntExtra("board_kind",-1);
        Long member_fk_inc = intent.getLongExtra("member_fk_inc",-1);
        Long guide_id_inc = intent.getLongExtra("guide_id_inc",-1);
        Integer guide_sun = intent.getIntExtra("guide_sun",-1);
        Integer guide_mon = intent.getIntExtra("guide_mon",-1);
        Integer guide_tue = intent.getIntExtra("guide_tue",-1);
        Integer guide_wed = intent.getIntExtra("guide_wed",-1);
        Integer guide_thu = intent.getIntExtra("guide_thu",-1);
        Integer guide_fri = intent.getIntExtra("guide_fri",-1);
        Integer guide_sat = intent.getIntExtra("guide_sat",-1);
        String guide_startTime = intent.getStringExtra("guide_startTime");
        String guide_endTime = intent.getStringExtra("guide_endTime");
        Long board_fk_inc = intent.getLongExtra("board_fk_inc",-1);

        Board board = new Board(board_id_inc, board_title, board_place, board_paytype, board_guidetype,board_content,board_complete,board_kind,member_fk_inc);
        GuideAddBoard guideAddBoard = new GuideAddBoard(guide_id_inc,guide_sun,guide_mon,guide_tue,guide_wed,guide_thu,guide_fri,guide_sat,guide_startTime,guide_endTime,board_fk_inc);
        guideBoard = new GuideBoard(board, guideAddBoard);
        Log.d("dsj", guideBoard.toString());

        days_array[0] = guideBoard.getGuide_add_board().getGuide_sun();
        days_array[1] = guideBoard.getGuide_add_board().getGuide_mon();
        days_array[2] = guideBoard.getGuide_add_board().getGuide_tue();
        days_array[3] = guideBoard.getGuide_add_board().getGuide_wed();
        days_array[4] = guideBoard.getGuide_add_board().getGuide_thu();
        days_array[5] = guideBoard.getGuide_add_board().getGuide_fri();
        days_array[6] = guideBoard.getGuide_add_board().getGuide_sat();

        et_title_modify.setText(guideBoard.getBoard().getBoard_title());
        et_place_modify.setText(guideBoard.getBoard().getBoard_place());
        et_introduction_modify.setText(guideBoard.getBoard().getBoard_content());

        Resources res = getResources();
        String[] sp_time_arr = res.getStringArray(R.array.sp_time);
        for(int i = 0; i< sp_time_arr.length ; i++){
            Log.d("dsj", sp_time_arr[i]);

            if(guideBoard.getGuide_add_board().getGuide_startTime().equals(sp_time_arr[i])){
                sp_start_time_modify.setSelection(i);
            }

            if(guideBoard.getGuide_add_board().getGuide_endTime().equals(sp_time_arr[i])){
                sp_end_time_modify.setSelection(i);
            }
        }

        if(guideBoard.getBoard().getBoard_paytype()==1){
            Log.d("dsj", getResources().getString(R.string.cash));
            sp_payment_type_modify.setSelection(1);
        }else if(guideBoard.getBoard().getBoard_paytype()==2){
            Log.d("dsj", getResources().getString(R.string.goods));
            sp_payment_type_modify.setSelection(2);
        }else{
            sp_payment_type_modify.setSelection(0);
        }

        if(guideBoard.getBoard().getBoard_guidetype()==1){
            Log.d("dsj", getResources().getString(R.string.drive_guide));
            sp_guide_type_modify.setSelection(1);
        }else if(guideBoard.getBoard().getBoard_guidetype()==2){
            Log.d("dsj", getResources().getString(R.string.guide_only));
            sp_guide_type_modify.setSelection(2);
        }else{
            sp_guide_type_modify.setSelection(0);
        }

        btnDays[0] = btn_sun;
        btnDays[1] = btn_mon;
        btnDays[2] = btn_tue;
        btnDays[3] = btn_wed;
        btnDays[4] = btn_thu;
        btnDays[5] = btn_fri;
        btnDays[6] = btn_sat;

        for(int i = 0; i<days_array.length; i++){
                if(days_array[i] == 1){
                    isSelectDays[i] = true;
                }else{
                    isSelectDays[i] = false;
                }
                selectDays(i);
        }

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
    }

    public void selectDays(int i) {
        if(isSelectDays[i]) {
            btnDays[i].setBackgroundResource(R.drawable.shape_rectangle_dark_violet_full_30);
            btnDays[i].setTextColor(getColor(GuideBoardModifyActivity.this,R.color.cf_white));
        }
        else {
            btnDays[i].setBackgroundResource(R.drawable.shape_rectangle_dark_violet_line_30);
            btnDays[i].setTextColor(getColor(GuideBoardModifyActivity.this,R.color.dark_violet));
        }
    }

    @Nullable
    @OnClick(R.id.btn_upload_modify)
    public void onClickUpload() {
        String possibleDays = "";

        for(int i=0;i<isSelectDays.length;i++) {
            if(isSelectDays[i]) {
                possibleDays+=getResources().getString(days[i])+",";
            }
        }

        possibleDays = possibleDays.substring(0,possibleDays.length()-1);

        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), et_title_modify.getText().toString());
        RequestBody place = RequestBody.create(MediaType.parse("text/plain"), et_place_modify.getText().toString());
        RequestBody paymentType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(sp_payment_type_modify.getSelectedItemPosition()));
        RequestBody guideType = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(sp_guide_type_modify.getSelectedItemPosition()));
        RequestBody introduction = RequestBody.create(MediaType.parse("text/plain"), et_introduction_modify.getText().toString());
        RequestBody endTime = RequestBody.create(MediaType.parse("text/plain"), sp_end_time_modify.getSelectedItem().toString());
        RequestBody startTime = RequestBody.create(MediaType.parse("text/plain"), sp_start_time_modify.getSelectedItem().toString());

        Log.d("datalog",title+" "+possibleDays+"  "+place+" "+paymentType+" "+guideType+" "+introduction + guideBoard.getBoard().getBoard_kind());

        Member loginMember = loginService.getLoginMember();

        Call<Void> observ =  RetrofitService.getInstance().getRetrofitRequest()
                .BoardGuideUpdate(guideBoard.getBoard().getBoard_id_inc(),title,place,paymentType,guideType,introduction,guideBoard.getBoard().getBoard_kind(),days_array[0],days_array[1],days_array[2],days_array[3],days_array[4],days_array[5],days_array[6],startTime,endTime);
        //업데이트
        observ.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    GuideBoardModifyActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

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