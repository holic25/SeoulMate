package kr.co.travelmaker.seoulmate.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
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
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.model.FirebaseChatRoom;
import kr.co.travelmaker.seoulmate.model.FirebaseMember;

public class FirebaseMessageListAdapter extends BaseAdapter {

    ArrayList<FirebaseChatRoom.Comment> comments;
    FirebaseMember destinationMember;
    private String uid;
    private String chatRoomUid;
    int count = 0;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd a hh:mm");

    public FirebaseMessageListAdapter(ArrayList<FirebaseChatRoom.Comment> comments, FirebaseMember destinationMember, String uid, String chatRoomUid) {
        this.comments = comments;
        this.destinationMember = destinationMember;
        this.uid = uid;
        this.chatRoomUid = chatRoomUid;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_message, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        // 로그인한 사용자가 보낸 메시지인 경우
        if(comments.get(position).uid.equals(uid)) {
            holder.tvMessage.setText(comments.get(position).message);
            holder.tvMessage.setBackgroundResource(R.drawable.chat_right);
            holder.tvMessage.setTextColor(Color.parseColor("#ffffff"));
            holder.ivDestination.setVisibility(View.INVISIBLE);
            holder.tvName.setVisibility(View.GONE);
            holder.layoutItemMessage.setGravity(Gravity.RIGHT);
            holder.layoutMessage.setGravity(Gravity.RIGHT);
            holder.tvReadCounterRight.setVisibility(View.INVISIBLE);
            setReadCounter(position,holder.tvReadCounterLeft);
        }

        // 상대방이 보낸 메시지인 경우
        else {
            holder.tvName.setText(destinationMember.getMember_id());
            holder.ivDestination.setVisibility(View.VISIBLE);
            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvMessage.setBackgroundResource(R.drawable.chat_left);
            holder.tvMessage.setTextColor(Color.parseColor("#545454"));
            holder.tvMessage.setText(comments.get(position).message);
            holder.layoutItemMessage.setGravity(Gravity.LEFT);
            holder.layoutMessage.setGravity(Gravity.LEFT);
            holder.tvReadCounterLeft.setVisibility(View.INVISIBLE);
            if(destinationMember.getMember_type().equals("0")) {
                holder.ivDestination.setBackgroundResource(R.drawable.ic_backpack);
            }
            else {
                holder.ivDestination.setBackgroundResource(R.drawable.ic_compass);
            }
            setReadCounter(position,holder.tvReadCounterRight);
        }

        long unixTime = (long) comments.get(position).timeStamp;
        Date date = new Date(unixTime);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String time = simpleDateFormat.format(date);
        holder.tvTimeStamp.setText(time);

        return convertView;
    }

    public void setReadCounter(final int position, final TextView textView) {
        if(count==0) {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Boolean> members = (Map<String, Boolean>) dataSnapshot.getValue();
                    count = members.size();
                    int cou = count - comments.get(position).readMembers.size();

                    if (cou > 0) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(String.valueOf(cou));
                    } else {
                        textView.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else {
            int cou = count - comments.get(position).readMembers.size();

            if (cou > 0) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(String.valueOf(cou));
            } else {
                textView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class Holder {
        @BindView(R.id.layout_item_message) LinearLayout layoutItemMessage;
        @BindView(R.id.tv_message) TextView tvMessage;
        @BindView(R.id.tv_name) TextView tvName;
        @BindView(R.id.iv_destination) ImageView ivDestination;
        @BindView(R.id.tv_time_stamp) TextView tvTimeStamp;
        @BindView(R.id.layout_message) LinearLayout layoutMessage;
        @BindView(R.id.tv_read_counter_left) TextView tvReadCounterLeft;
        @BindView(R.id.tv_read_counter_right) TextView tvReadCounterRight;

        public Holder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
