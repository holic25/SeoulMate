package kr.co.travelmaker.seoulmate.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemLongClick;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.adapter.FirebaseChatRoomListAdapter;
import kr.co.travelmaker.seoulmate.bus.BusProvider;
import kr.co.travelmaker.seoulmate.model.FirebaseChatRoom;
import kr.co.travelmaker.seoulmate.service.LoginService;

public class FirebaseMessengerFragment extends Fragment {

    Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;

    @BindView(R.id.lv_chatroom) ListView lvChatroom;
    FirebaseChatRoomListAdapter firebaseChatRoomListAdapter;
    private List<FirebaseChatRoom> firebaseChatRooms = new ArrayList<>();

    private String uid = LoginService.getInstance().getLoginMember().getMember_id_inc().toString();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firebase_messenger, container, false);
        unbinder = ButterKnife.bind(this, view);
        bus.register(this);

        return view;
    }

    public void refreshData() {
        firebaseChatRoomListAdapter = new FirebaseChatRoomListAdapter(firebaseChatRooms,uid);
        lvChatroom.setAdapter(firebaseChatRoomListAdapter);

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("members/"+uid).equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseChatRooms.clear();
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    firebaseChatRooms.add(item.getValue(FirebaseChatRoom.class));
                }
                firebaseChatRoomListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}
