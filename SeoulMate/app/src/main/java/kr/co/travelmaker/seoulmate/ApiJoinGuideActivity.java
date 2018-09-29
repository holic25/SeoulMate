package kr.co.travelmaker.seoulmate;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.model.FirebaseMember;
import kr.co.travelmaker.seoulmate.model.GoogleAccount;
import kr.co.travelmaker.seoulmate.model.IsCheck;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiJoinGuideActivity extends AppCompatActivity {
    @BindView(R.id.et_google_guide_id) EditText et_google_guide_id;
    @BindView(R.id.btn_google_guide_id_double_check) Button btn_google_guide_id_double_check;
    @BindView(R.id.txt_google_guide_id_check) TextView txt_google_guide_id_check;
    @BindView(R.id.txt_google_guide_id_db_check) TextView txt_google_guide_id_db_check;
    @BindView(R.id.txt_google_guide_id_db_check_match) TextView txt_google_guide_id_db_check_match;
    @BindView(R.id.txt_google_guide_id_accepted) TextView txt_google_guide_id_accepted;
    @BindView(R.id.et_guide_pw) EditText et_guide_pw;
    @BindView(R.id.txt_guide_pw_check) TextView txt_guide_pw_check;
    @BindView(R.id.et_guide_pw2) EditText et_guide_pw2;
    @BindView(R.id.txt_guide_pw_check2) TextView txt_guide_pw_check2;
    @BindView(R.id.txt_guide_pw_check_match) TextView txt_guide_pw_check_match;
    @BindView(R.id.txt_google_guide_email) TextView txt_google_guide_email;
    @BindView(R.id.txt_google_guide_email_adress) TextView txt_google_guide_email_adress;
    @BindView(R.id.et_google_guide_first_name) EditText et_google_guide_first_name;
    @BindView(R.id.et_google_guide_last_name) EditText et_google_guide_last_name;
    @BindView(R.id.txt_google_guide_name_check) TextView txt_google_guide_name_check;
    @BindView(R.id.et_google_guide_birth_day) EditText et_google_guide_birth_day;
    @BindView(R.id.txt_google_guide_birthday_check) TextView txt_google_guide_birthday_check;
    @BindView(R.id.guide_google_spinner_notification) Spinner guide_google_spinner_notification;
    @BindView(R.id.txt_google_guide_nation_check) TextView txt_google_guide_nation_check;
    @BindView(R.id.guide_google_radio_group_gender) RadioGroup guide_google_radio_group_gender;
    @BindView(R.id.guide_google_radio_women) RadioButton guide_google_radio_women;
    @BindView(R.id.guide_google_radio_men) RadioButton guide_google_radio_men;
    @BindView(R.id.txt_google_guide_gender_check) TextView txt_google_guide_gender_check;
    @BindView(R.id.img_google_id_card_check) ImageView img_google_id_card_check;
    @BindView(R.id.btn_google_take_photo) Button btn_google_take_photo;
    @BindView(R.id.btn_google_get_photo) Button btn_google_get_photo;
    @BindView(R.id.btn_google_guide_sign_up) Button btn_google_guide_sign_up;

    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;

    String mCurrentPhotoPath;

    Uri imageUri;
    Uri photoURI, albumURI;
    Boolean photoInsertCheck = false;
    String photoUriStr= "";

    File realPhotoFile = null;

    String email_adress = "";
    String nationality = "";
    Integer gender;
    Integer doubleCheck = 0;
    IsCheck items;

    Integer take_photo = 0;
    Integer get_photo = 0;

    Boolean idCheck = false, pwCheck = false, pw2Check = false, nameCheck =false;
    Boolean  birthCheck = false, notificationCheck = false, genderCheck = false;

    MultipartBody.Part filePart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_join_guide);
        ButterKnife.bind(this);

        setGoogleJoin();

        //스피너 이용
        ArrayAdapter searchAdapter = ArrayAdapter.createFromResource(this,
                R.array.sp_nationality, android.R.layout.simple_spinner_item);
        searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        guide_google_spinner_notification.setAdapter(searchAdapter);
    }

    public void setGoogleJoin() {
        GoogleAccount googleAccount = GoogleAccount.getInstance();
        GoogleSignInAccount account = googleAccount.getSignInAccount();

        et_google_guide_first_name.setText(account.getGivenName());
        et_google_guide_last_name.setText(account.getFamilyName());

        String[] email = account.getEmail().split("@");

        txt_google_guide_email.setText(email[0]);
        txt_google_guide_email_adress.setText(email[1]);

        email_adress = account.getEmail();
        et_google_guide_id.setText(email[0]);
    }

    @OnClick(R.id.btn_google_guide_id_double_check)
    public void OnClickGoogleGuideDoubleCheck(){
        //중복확인버튼 눌렀을 때
        Log.d("msb", "중복확인");
        if (et_google_guide_id.getText().toString().equals("")) {
            if(txt_google_guide_id_db_check.getVisibility() == View.VISIBLE){
                txt_google_guide_id_db_check.setVisibility(View.INVISIBLE);
            }
            txt_google_guide_id_accepted.setVisibility(View.INVISIBLE);
            txt_google_guide_id_check.setVisibility(View.VISIBLE);
        } else {
            txt_google_guide_id_check.setVisibility(View.INVISIBLE);
            Log.d("msb", et_google_guide_id.getText().toString());
            RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), et_google_guide_id.getText().toString());
            Call<IsCheck> observ = RetrofitService.getInstance().getRetrofitRequest().isCheckedId(user_id);
            observ.enqueue(new Callback<IsCheck>() {
                @Override
                public void onResponse(Call<IsCheck> call, Response<IsCheck> response) {
                    if (response.isSuccessful()) {
                        items = response.body();
                        Log.d("dsj", "들어옴 : " + items.toString());
                        if (Integer.parseInt(items.getCheckNumber()) >= 1) {
                            //같은 아이디가 있으면
                            txt_google_guide_id_db_check_match.setVisibility(View.INVISIBLE);
                            txt_google_guide_id_accepted.setVisibility(View.INVISIBLE);
                            txt_google_guide_id_db_check.setVisibility(View.VISIBLE);
                            //사용중인 아이디 입니다.

                        } else {
                            //같은 아이디가 아닐 때
                            txt_google_guide_id_db_check.setVisibility(View.INVISIBLE);
                            txt_google_guide_id_db_check_match.setVisibility(View.INVISIBLE);
                            //사용중인 아이디 입니다.를 없애고
                            //중복확인 해달라는 메세지도 없애고
                            txt_google_guide_id_accepted.setVisibility(View.VISIBLE);
                            //사용 가능한 아이디입니다. 띄움
                        }
                        if (txt_google_guide_id_accepted.getVisibility() == View.VISIBLE) {
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

    @OnClick(R.id.btn_google_guide_sign_up)
    public void OnClickGoogleGuideSignUp(){
        //아이디 중복확인 안눌렀을때
        if(doubleCheck == 0){
            txt_google_guide_id_check.setVisibility(View.INVISIBLE);
            txt_google_guide_id_db_check.setVisibility(View.INVISIBLE);
            txt_google_guide_id_db_check_match.setVisibility(View.VISIBLE);
        } else {
            txt_google_guide_id_db_check_match.setVisibility(View.INVISIBLE);
        }

        //첫번째 비밀번호 입력 시
        if (et_guide_pw.getText().toString().equals("")){
            txt_guide_pw_check.setVisibility(View.VISIBLE);
        } else {
            txt_guide_pw_check.setVisibility(View.INVISIBLE);
            pwCheck = true;
            //비밀번호 입력 했을 때. true
        }

        //두번째 비밀번호 입력 시
        if(et_guide_pw2.getText().toString().equals("")){
            if(txt_guide_pw_check_match.getVisibility() == View.VISIBLE){
                txt_guide_pw_check_match.setVisibility(View.INVISIBLE);
            }

            txt_guide_pw_check2.setVisibility(View.VISIBLE);
        } else {
            if (txt_guide_pw_check2.getVisibility() == View.VISIBLE){
                txt_guide_pw_check2.setVisibility(View.INVISIBLE);
            }
            if (et_guide_pw.getText().toString().equals(et_guide_pw2.getText().toString())){
                //비밀번호가 일치 할 때 true
                pw2Check = true;
                if (txt_guide_pw_check2.getVisibility() == View.VISIBLE){
                    txt_guide_pw_check2.setVisibility(View.INVISIBLE);
                } else if(txt_guide_pw_check_match.getVisibility() == View.VISIBLE){
                    txt_guide_pw_check_match.setVisibility(View.INVISIBLE);
                }
            } else {
                txt_guide_pw_check_match.setVisibility(View.VISIBLE);
            }
        }

        //이름 입력 시
        if(et_google_guide_first_name.getText().toString().equals("") || et_google_guide_last_name.getText().toString().equals("")){
            txt_google_guide_name_check.setVisibility(View.VISIBLE);
        } else {
            txt_google_guide_name_check.setVisibility(View.INVISIBLE);
            //이름 입력했을 때 true;
            nameCheck = true;
        }

        //생일 입력 시
        if (et_google_guide_birth_day.length() == 8){
            if(txt_google_guide_birthday_check.getVisibility() == View.VISIBLE){
                txt_google_guide_birthday_check.setVisibility(View.INVISIBLE);
            }
            //생일 입력 했을 때 true;
            birthCheck = true;
        } else {
            txt_google_guide_birthday_check.setVisibility(View.VISIBLE);
        }

        //스피너 값 받아옴
        nationality = guide_google_spinner_notification.getSelectedItem().toString();
        Log.d("msb : ",nationality);

        //국적 선택 시
        if(guide_google_spinner_notification.getSelectedItem().toString().equals("국적")){
            txt_google_guide_nation_check.setVisibility(View.VISIBLE);
        } else {
            if(txt_google_guide_nation_check.getVisibility() == View.VISIBLE){
                txt_google_guide_nation_check.setVisibility(View.INVISIBLE);
            }
            //나라 선택 했을 때 true
            notificationCheck = true;
        }

        //성별 선택 시
        int gender_id = guide_google_radio_group_gender.getCheckedRadioButtonId();
        RadioButton gender_select = (RadioButton) findViewById(gender_id);
        if(gender_select == null){
            txt_google_guide_gender_check.setVisibility(View.VISIBLE);
        } else {
            if(txt_google_guide_gender_check.getVisibility() == View.VISIBLE){
                txt_google_guide_gender_check.setVisibility(View.INVISIBLE);
            }
            if(gender_select.getText().toString().equals("여성")){
                gender = 0;
                genderCheck = true;
            } else if(gender_select.getText().toString().equals("남성")){
                gender = 1;
                genderCheck = true;
            }
        }

        int approval = 0;
        if(photoInsertCheck){
            approval = 1;
        }
        Log.d("msb",idCheck.toString() +  pwCheck + pw2Check + nameCheck + birthCheck + notificationCheck + genderCheck);

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), et_google_guide_id.getText().toString());
        RequestBody pw = RequestBody.create(MediaType.parse("text/plain"), et_guide_pw.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), email_adress);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), et_google_guide_first_name.getText().toString()+et_google_guide_last_name.getText().toString());
        RequestBody nationality_re = RequestBody.create(MediaType.parse("text/plain"), nationality);
        RequestBody birth = RequestBody.create(MediaType.parse("text/plain"), et_google_guide_birth_day.getText().toString());
        RequestBody photoUriStr_re = RequestBody.create(MediaType.parse("text/plain"), photoUriStr);

        if(realPhotoFile != null) {
            filePart = MultipartBody.Part.createFormData("license_image",
                    realPhotoFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), realPhotoFile));
        }

        if(idCheck && pwCheck && pw2Check && nameCheck && birthCheck && notificationCheck && genderCheck) {
            Call<Member> observ = RetrofitService.getInstance().getRetrofitRequest().SeoulMateGuideMemberInsert(id, pw, email, name, nationality_re, gender, 1, birth, filePart, approval);
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
                        ApiJoinGuideActivity.this.finish();
                    }
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {

                }
            });
        }
    }

    @OnClick(R.id.btn_google_take_photo)
    public void OnClickTakePhoto(){
        int permissionResult = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
        int permissionResult2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionResult == PackageManager.PERMISSION_GRANTED && permissionResult2 == PackageManager.PERMISSION_GRANTED){
            captureCamera();
        } else {
            take_photo ++;
            checkPermission();
        }
    }

    private void captureCamera(){
        String state = Environment.getExternalStorageState();
        //외장 메모리 검사
        if(Environment.MEDIA_MOUNTED.equals(state)){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                }catch(IOException ex){
                    Log.e("captureCamera Error",ex.toString());
                }
                if(photoFile != null){
                    //getUriForFile의 두 번째 인자는 Manifest provider의 authorites와 일치해야 함
                    Uri providerURI = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                    imageUri = providerURI;
                    //인텐트에 전달할 때 FileProvider의 Return값인 content://로만, providerURI값에 카메라 데이터를 넣어보냄

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,providerURI);

                    startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this,"저장공간이 접근 불가능한 기기입니다",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws IOException{
        //이미지 파일 이름 생성
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures","SeoulMate");

        if(!storageDir.exists()){
            Log.i("mCurrentPhotoPath1",storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        realPhotoFile = imageFile;

        return imageFile;
    }

    @OnClick(R.id.btn_google_get_photo)
    public void OnClickGetPhoto(){
        int permissionResult = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
        int permissionResult2 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionResult == PackageManager.PERMISSION_GRANTED && permissionResult2 == PackageManager.PERMISSION_GRANTED){
            getAlbum();
        } else {
            get_photo ++;
            checkPermission();
        }
    }

    private void getAlbum(){
        Log.i("getAlbum","Call");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,REQUEST_TAKE_ALBUM);
    }

    private void galleryAddPic(){
        Log.i("galleryAddPic","Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this,"사진이 앨범에 저장되었습니다.",Toast.LENGTH_SHORT).show();
    }
    //카메라 전용 크랍
    public void cropImage(){
        Log.i("cropImage","Call");
        Log.i("cropImage","photoURI : " + photoURI + "/albumURI : " + albumURI);

        //50x50픽셀미만은 편집할 수 없다는 문구처리 + 갤러리, 포토 둘다 호환하는 방법
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI,"image/*");
        cropIntent.putExtra("outputX",200);
        cropIntent.putExtra("outputY",200);
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("scale",true);
        cropIntent.putExtra("output",albumURI);
        startActivityForResult(cropIntent,REQUEST_IMAGE_CROP);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case  REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK){
                    try{
                        Log.i("REQUEST_TAKE_PHOTO","OK");
                        galleryAddPic();
                        Glide.with(this).load(imageUri).into(img_google_id_card_check);
                        photoInsertCheck = true;
                        photoUriStr = imageUri.toString();
                    }catch (Exception e){
                        Log.e("REQUEST_TAKE_PHOTO",e.toString());
                    }
                } else {
                    Toast.makeText(this,"사진찍기를 취소하였습니다.",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK){
                    if (data.getData() != null){
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                            Glide.with(this).load(albumURI).into(img_google_id_card_check);
                            photoInsertCheck = true;
                            photoUriStr = albumURI.toString();
                        } catch (Exception e){
                            Log.e("TAKE_ALBUM_SINGLE ERROR",e.toString());
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP:
                if(resultCode == Activity.RESULT_OK){
                    galleryAddPic();
                    Glide.with(this).load(albumURI).into(img_google_id_card_check);
                    photoInsertCheck = true;
                    photoUriStr = albumURI.toString();
                }
                break;
        }
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if((ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case MY_PERMISSION_CAMERA:
                for(int i = 0 ; i < grantResults.length ; i ++){
                    //grantResults[] : 허용권한은 0, 거부 권한은 -1
                    if(grantResults[i] < 0){
                        Toast.makeText(this,"해당 권한을 활성화 하셔야 합니다.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //허용했으면 이부분.
                break;
        }
    }
}