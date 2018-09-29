package kr.co.travelmaker.seoulmate.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.data.RowData;
import kr.co.travelmaker.seoulmate.event.CulturalInfoMoveViewPager;
import kr.co.travelmaker.seoulmate.event.RowDataEvent;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.bus.BusProvider;

public class CulturalDetailFragment extends Fragment {
    private static CulturalDetailFragment curr = null;
    public static CulturalDetailFragment getInstance() {
        if (curr == null) {
            curr = new CulturalDetailFragment();
        }

        return curr;
    }

    Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;
    RowData item = null;

    @BindView(R.id.info_im) ImageView info_im;
    @BindView(R.id.info_title) TextView info_title;
    @BindView(R.id.info_title2) TextView info_title2;
    @BindView(R.id.info_codeName) TextView info_codeName;
    @BindView(R.id.info_date) TextView info_date;
    @BindView(R.id.info_time) TextView info_time;
    @BindView(R.id.info_place) TextView info_place;
    @BindView(R.id.info_useTarget) TextView info_useTarget;
    @BindView(R.id.info_useFee) TextView info_useFee;
    @BindView(R.id.info_support) TextView info_support;
    @BindView(R.id.info_tel) TextView info_tel;
    @BindView(R.id.info_back) ImageView info_back;
    @BindView(R.id.info_main_layout) LinearLayout info_main_layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cultural_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        bus.register(this);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    CulturalInfoMoveViewPager culturalInfoMoveViewPager = new CulturalInfoMoveViewPager(0);
                    bus.post(culturalInfoMoveViewPager);
                    return true;
                } else {
                    return false;
                }
            }
        });

        return view;
    }

    @Subscribe
    public void rowDataEvent(RowDataEvent event){
        item = event.getRowData();

        if(!item.getMAIN_IMG().equals("")){
            String url = item.getMAIN_IMG();

            int find = url.lastIndexOf(".");

            String realUrl = url.substring(0, find).toLowerCase();
            String jpg = url.substring(find+1, url.length());

            Glide.with(this).load(realUrl+"."+jpg).into(info_im);

        }else{
            info_im.setImageResource(R.drawable.no_image);
        }

        ArrayList<String> title = new ArrayList<>();

        if(item.getTITLE().length() >=25) {
            info_title2.setVisibility(View.VISIBLE);
            title.add(item.getTITLE().substring(0,item.getTITLE().length()/2));
            title.add(item.getTITLE().substring(item.getTITLE().length()/2, item.getTITLE().length()));
            info_title.setText(title.get(0));
            info_title2.setText(title.get(1));
        }else{
            info_title.setText(item.getTITLE());
            info_title2.setVisibility(View.GONE);
        }

        info_codeName.setText(item.getCODENAME());
        info_date.setText(item.getSTRTDATE()+" ~ "+item.getEND_DATE());
        info_time.setText(item.getTIME());
        info_place.setText(item.getPLACE());

        if(item.getUSE_TRGT().equals("") || item.getUSE_TRGT() == null) {
            info_useTarget.setText("전체");
        }
        else{
            info_useTarget.setText(item.getUSE_TRGT());
        }

        if(item.getUSE_FEE().contains("\n")){
            item.setUSE_FEE(item.getUSE_FEE().replace("\n", ""));
        }

        info_useFee.setText(item.getUSE_FEE());
        info_support.setText(item.getSPONSOR());
        info_tel.setText(item.getINQUIRY());
    }
    @OnClick(R.id.info_back)
    public void info_back(){
        CulturalInfoMoveViewPager culturalInfoMoveViewPager = new CulturalInfoMoveViewPager(0);
        bus.post(culturalInfoMoveViewPager);
    }

    @OnClick(R.id.info_btn_addr)
    public void info_btn_addr(){
        Uri uri = Uri.parse(item.getORG_LINK());
        Intent i  = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(i);
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
