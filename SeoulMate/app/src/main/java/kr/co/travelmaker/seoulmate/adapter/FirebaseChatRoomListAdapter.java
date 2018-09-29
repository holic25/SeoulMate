package kr.co.travelmaker.seoulmate.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.travelmaker.seoulmate.FirebaseMessageActivity;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.model.FirebaseChatRoom;
import kr.co.travelmaker.seoulmate.model.FirebaseMember;

public class FirebaseChatRoomListAdapter extends BaseAdapter {

    private List<FirebaseChatRoom> firebaseChatRooms;
    private ArrayList<String> destinationMembers = new ArrayList<>();
    private String uid;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd a hh:mm");

    public FirebaseChatRoomListAdapter(List<FirebaseChatRoom> firebaseChatRooms, String uid) {
        this.firebaseChatRooms = firebaseChatRooms;
        this.uid = uid;
    }

    @Override
    public int getCount() {
        return firebaseChatRooms.size();
    }

    @Override
    public Object getItem(int position) {
        return firebaseChatRooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Holder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_chatroom, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        // 메시지를 내림차순으로 정렬 후 마지막 메시지의 키 값으로 마지막 메시지의 내용을 가져옴
        Map<String,FirebaseChatRoom.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
        commentMap.putAll(firebaseChatRooms.get(position).getComments());

        if(commentMap!=null && commentMap.size()!=0) {
            String lastMessageKey = (String)commentMap.keySet().toArray()[0];
            holder.tvLastMessage.setText(commentMap.get(lastMessageKey).message);

            holder.layoutItemChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), FirebaseMessageActivity.class);
                    i.putExtra("destinationUid", destinationMembers.get(position));
                    view.getContext().startActivity(i);
                }
            });

            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            long unixTime = (long) firebaseChatRooms.get(position).getComments().get(lastMessageKey).timeStamp;
            Date date = new Date(unixTime);
            holder.tvTimeStamp.setText(simpleDateFormat.format(date));

            int contain=0;
            for(String key : commentMap.keySet()) {
                if(commentMap.get(key).readMembers.containsKey(uid)) {
                    contain++;
                }
            }

            Integer count = commentMap.size()-contain;
            Log.d("datalog","commentMap.size() : "+commentMap.size());
            Log.d("datalog","contain : "+contain);

            if(count!=0) {
                holder.tvCount.setText(count.toString());
                holder.tvCount.setVisibility(View.VISIBLE);
            }
            else {
                holder.tvCount.setVisibility(View.GONE);
            }
        }

        String destinationUid = null;

        // 채팅방에 있는 유저
        for(String user : firebaseChatRooms.get(position).getMembers().keySet()) {
            if(!user.equals(uid)) {
                destinationUid = user;
                destinationMembers.add(destinationUid);
            }
        }

        FirebaseDatabase.getInstance().getReference().child("members").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseMember firebaseMember = dataSnapshot.getValue(FirebaseMember.class);
                holder.tvName.setText(firebaseMember.getMember_id());
                if(firebaseMember.getMember_type().equals("0")) {
                    holder.ivDestination.setBackgroundResource(R.drawable.ic_backpack);
                }
                else {
                    holder.ivDestination.setBackgroundResource(R.drawable.ic_compass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return convertView;
    }

    public class Holder {
        @BindView(R.id.layout_item_chat) LinearLayout layoutItemChat;
        @BindView(R.id.iv_destination) ImageView ivDestination;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.tv_last_message) TextView tvLastMessage;
        @BindView(R.id.tv_time_stamp) TextView tvTimeStamp;
        @BindView(R.id.tv_count) TextView tvCount;

        public Holder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}

