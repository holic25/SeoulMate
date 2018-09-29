package kr.co.travelmaker.seoulmate;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.otto.Bus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.data.PayType;
import kr.co.travelmaker.seoulmate.event.Logout;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;
import kr.co.travelmaker.seoulmate.model.GuideMemberLicense;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.retrofilt.RetrofitService;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideEditProfileActivity extends AppCompatActivity {

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
    @BindView(R.id.iv_license) ImageView ivLicense;
    @BindView(R.id.btn_edit_profile) Button btnEditProfile;
    @BindView(R.id.btn_take_photo) Button btn_take_photo;
    @BindView(R.id.btn_get_photo) Button btn_get_photo;

    LoginService loginService = LoginService.getInstance();
    Member loginMember = loginService.getLoginMember();
    GuideMemberLicense loginGuideMemberLicense;

    Bus bus = BusProvider.getInstance().getBus();

    boolean isCurrPwChecked = false, isCurrPwRight = false;
    boolean isNewPwChecked = false, isConfirmNewPwChecked = false, isNewPwMatched;

    Integer take_photo = 0;
    Integer get_photo = 0;

    File realPhotoFile = null;
    MultipartBody.Part filePart;

    Boolean photoInsertCheck = false;
    String photoUriStr= "";

    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;
    String mCurrentPhotoPath;

    Uri imageUri;
    Uri photoURI, albumURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_edit_profile);
        ButterKnife.bind(this);
        bus.register(this);

        loginMember = loginService.getLoginMember();

        Call<GuideMemberLicense> observ = RetrofitService.getInstance().getRetrofitRequest().getGuideMemberLicense(loginMember.getMember_id_inc());
        observ.enqueue(new Callback<GuideMemberLicense>() {
            @Override
            public void onResponse(Call<GuideMemberLicense> call, Response<GuideMemberLicense> response) {
                if (response.isSuccessful()) {
                    loginGuideMemberLicense = response.body();
                    Log.d("dsj", loginGuideMemberLicense.getLicense_image() + "" + loginGuideMemberLicense.getLicense_imagepath());

                    if (!loginGuideMemberLicense.getLicense_imagepath().equals("")) {
                        String url = "http://192.168.0.106:8090/seoulmate/resources/upload/";
                        Log.d("datalog", "url : " + url + loginGuideMemberLicense.getLicense_image());
                        Glide.with(getApplicationContext()).load(url + loginGuideMemberLicense.getLicense_imagepath()).into(ivLicense);

                    }
                }
            }

            @Override
            public void onFailure(Call<GuideMemberLicense> call, Throwable t) {

            }
        });

        if(loginMember.getMember_gender()==0) {
            ivGender.setBackgroundResource(R.drawable.ic_user_female);
        }
        else {
            ivGender.setBackgroundResource(R.drawable.ic_user_male);
        }

        tvId.setText(loginMember.getMember_id());
    }

    @OnClick(R.id.btn_take_photo)
    public void OnClicktake_photo(){
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
    @OnClick(R.id.btn_get_photo)
    public void OnClickget_photo(){
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
                        Glide.with(this).load(imageUri).into(ivLicense);
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
                            Glide.with(this).load(albumURI).into(ivLicense);
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
                    Glide.with(this).load(albumURI).into(ivLicense);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_CAMERA:
                //grantResults[] : 허용권한은 0, 거부 권한은 -1
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(take_photo > get_photo) {
                        captureCamera();
                    } else {
                        getAlbum();
                    }
                } else {
                    Toast.makeText(this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //허용했으면 이부분.
                break;
        }
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


        if(realPhotoFile != null) {
            filePart = MultipartBody.Part.createFormData("license_image",
                    realPhotoFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), realPhotoFile));
        }

        if(isCurrPwChecked && isCurrPwRight && isNewPwChecked && isConfirmNewPwChecked && isNewPwMatched) {
            Log.d("datalog","회원정보 수정");
            Call<Void> observ = RetrofitService.getInstance().getRetrofitRequest().updateMemberData(loginMember.getMember_id_inc(), loginMember.getMember_kind(),
                    newPw_re, filePart);
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
                    Log.d("datalog","updateMemberData_fail : "+t);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}