package kr.co.travelmaker.seoulmate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.travelmaker.seoulmate.adapter.FirebaseMessageListAdapter;
import kr.co.travelmaker.seoulmate.model.FirebaseChatRoom;
import kr.co.travelmaker.seoulmate.model.FirebaseMember;
import kr.co.travelmaker.seoulmate.model.FirebaseNotification;
import kr.co.travelmaker.seoulmate.model.Member;
import kr.co.travelmaker.seoulmate.service.LoginService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseMessageActivity extends AppCompatActivity {

    @BindView(R.id.iv_destination) ImageView ivDestination;
    @BindView(R.id.tv_destination_id) TextView tvDestinationId;
    @BindView(R.id.btn_menu) ImageView btnMenu;
    @BindView(R.id.et_message) EditText etMessage;
    @BindView(R.id.btn_send) Button btnSend;
    @BindView(R.id.lv_message) ListView lvMessage;
    FirebaseMessageListAdapter firebaseMessageListAdapter;

    private ArrayList<FirebaseChatRoom.Comment> comments = new ArrayList<>();
    FirebaseMember destinationMemberModel;

    private String uid;
    private String destinationUid;
    private String chatRoomUid;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private long removeTime;

    LoginService loginService = LoginService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_message);
        ButterKnife.bind(this);

        Intent i = getIntent();
        destinationUid = i.getStringExtra("destinationUid");
        uid = loginService.getLoginMember().getMember_id_inc().toString();

        Log.d("datalog","destinationUid : "+destinationUid);
        Log.d("datalog","uid : "+uid);

        final FirebaseMember[] destinationMember = new FirebaseMember[1];
        FirebaseDatabase.getInstance().getReference().child("members").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                destinationMember[0] = dataSnapshot.getValue(FirebaseMember.class);
                Log.d("datalog","destinationMember : "+destinationMember[0].toString());

                if(destinationMember[0].getMember_type().equals("0")) {
                    ivDestination.setBackgroundResource(R.drawable.ic_backpack);
                }
                else {
                    ivDestination.setBackgroundResource(R.drawable.ic_compass);
                }

                tvDestinationId.setText(destinationMember[0].getMember_id());
                checkChatRoom();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.btn_menu)
    public void onClickMenu() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(R.string.delete);
        dlg.setMessage(R.string.confirm_delete_chatroom);
        dlg.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dlg.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Object removeTime = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("removeTime").child(uid).setValue(removeTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("datalog","addRemoveTime Complete");
                        setChatRoomFalse();
                        finish();
                    }
                });
            }
        });
        dlg.show();
    }

    public void setChatRoomFalse() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("members").child(uid).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("datalog","setChatRoomFalse Complete");
            }
        });
    }

    public void setChatRoomTrue() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("members").child(destinationUid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("datalog","setChatRoomTrue Complete");
            }
        });
    }

    @OnClick(R.id.btn_send)
    public void onClickSend() {
        FirebaseChatRoom chatModel = new FirebaseChatRoom();
        chatModel.getMembers().put(uid,true);
        chatModel.getMembers().put(destinationUid,true);
        Log.d("datalog","onClickSend");

        if (chatRoomUid == null) {
            btnSend.setEnabled(false);
            FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("datalog","onClickSend_if");
                    checkChatRoom();
                }
            });
        }
        else {
            Log.d("datalog","onClickSend_else");
            FirebaseChatRoom.Comment comment = new FirebaseChatRoom.Comment();
            comment.uid = uid;
            comment.message = etMessage.getText().toString();
            comment.timeStamp = ServerValue.TIMESTAMP;
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("datalog","onClickSend_else_onComplete");
                    setChatRoomTrue();
                    sendGcm();
                    etMessage.setText("");
                }
            });
        }
    }

    // notification 보내는 부분
    public void sendGcm() {
        Log.d("datalog","sendGcm");
        Gson gson = new Gson();

        Member loginMember = loginService.getLoginMember();
        FirebaseNotification notificationModel = new FirebaseNotification();
        notificationModel.setTo(destinationMemberModel.getMember_push_token());
        notificationModel.getNotification().title = loginMember.getMember_id();
        notificationModel.getNotification().text = etMessage.getText().toString();
        notificationModel.getNotification().fromUid = loginMember.getMember_id_inc().toString();
        notificationModel.getData().title = loginMember.getMember_id();
        notificationModel.getData().text = etMessage.getText().toString();
        notificationModel.getData().fromUid = loginMember.getMember_id_inc().toString();

        Log.d("datalog","notification : "+notificationModel.toString());

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AIzaSyAxPdDOuR3czlNroysR5CCOASHV71MRKJM")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("datalog","onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("datalog","onResponse");
            }
        });
    }

    public void checkChatRoom() {
        Log.d("datalog","checkChatRoom");
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("members/"+uid).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("datalog","checkChatRoom_onDataChange");
                        if(dataSnapshot.getValue() == null){
                            FirebaseChatRoom newRoom = new FirebaseChatRoom();
                            newRoom.getMembers().put(uid, true);
                            newRoom.getMembers().put(destinationUid, true);
                            FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    checkChatRoom();
                                }
                            });
                            return;
                        }

                        for(DataSnapshot item : dataSnapshot.getChildren()) {
                            FirebaseChatRoom chatModel = item.getValue(FirebaseChatRoom.class);
                            if(chatModel.getMembers().containsKey(destinationUid) && chatModel.getMembers().size()==2) {
                                chatRoomUid = item.getKey();
                                btnSend.setEnabled(true);
                                refreshData();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void refreshData() {
        Log.d("datalog","refreshData");
        FirebaseDatabase.getInstance().getReference().child("members").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("datalog","refreshData_onDataChange");
                destinationMemberModel = dataSnapshot.getValue(FirebaseMember.class);
                firebaseMessageListAdapter = new FirebaseMessageListAdapter(comments, destinationMemberModel,uid,chatRoomUid);
                lvMessage.setAdapter(firebaseMessageListAdapter);
                getRemoveTime();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getRemoveTime() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("removeTime").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("datalog","getRemoveTime_onDataChange");
                Log.d("datalog","removeTime : "+dataSnapshot.getValue());
                if(dataSnapshot.getValue()!=null) {
                    removeTime = (long) dataSnapshot.getValue();
                }
                getMessageList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getMessageList() {
        Log.d("datalog","getMessageList");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("datalog","getMessageList_onDataChange");
                comments.clear();
                Map<String,Object> readMembersMap = new HashMap<>();

                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    String key = item.getKey();
                    FirebaseChatRoom.Comment comment_origin = item.getValue(FirebaseChatRoom.Comment.class);
                    FirebaseChatRoom.Comment comment_modify = item.getValue(FirebaseChatRoom.Comment.class);

                    Log.d("datalog","removeTime : "+removeTime);
                    if((long)comment_origin.timeStamp > removeTime) {
                        comment_modify.readMembers.put(uid, true);
                        readMembersMap.put(key, comment_modify);
                        comments.add(comment_origin);
                        Log.d("datalog", "getMessageList_onDataChange_ORIGIN : " + comment_origin);
                    }
                }

                if(comments!=null && comments.size()!=0) {
                    if (!comments.get(comments.size() - 1).readMembers.containsKey(uid)) {
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").updateChildren(readMembersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // 메시지 갱신
                                firebaseMessageListAdapter.notifyDataSetChanged();
                                // 스크롤 이동
                                lvMessage.smoothScrollToPosition(comments.size() - 1);
                            }
                        });
                    } else {
                        // 메시지 갱신
                        firebaseMessageListAdapter.notifyDataSetChanged();
                        // 스크롤 이동
                        lvMessage.smoothScrollToPosition(comments.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(valueEventListener!=null) {
            databaseReference.removeEventListener(valueEventListener);
        }
        finish();
    }
}
