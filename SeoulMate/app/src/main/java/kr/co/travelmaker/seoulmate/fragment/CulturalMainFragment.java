package kr.co.travelmaker.seoulmate.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.co.travelmaker.seoulmate.event.CulturalInfoMoveViewPager;
import kr.co.travelmaker.seoulmate.R;
import kr.co.travelmaker.seoulmate.util.CustomViewPager;
import kr.co.travelmaker.seoulmate.adapter.CulturalViewPagerAdapter;
import kr.co.travelmaker.seoulmate.bus.BusProvider;

public class CulturalMainFragment extends Fragment {

    @BindView(R.id.vp_cultural_info) CustomViewPager vpCulturalInfo;
    CulturalViewPagerAdapter culturalViewPagerAdapter;

    Bus bus = BusProvider.getInstance().getBus();
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cultural_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        bus.register(this);

        culturalViewPagerAdapter = new CulturalViewPagerAdapter(getChildFragmentManager());
        vpCulturalInfo.setAdapter(culturalViewPagerAdapter);

        vpCulturalInfo.setPagingDisabled();

        return view;
    }

    @Subscribe
    public void moveViewPager(CulturalInfoMoveViewPager event) {
        vpCulturalInfo.setCurrentItem(event.getPage());
    }

    @Subscribe
    public void moveViewPager2(CulturalInfoMoveViewPager event) {
        vpCulturalInfo.setCurrentItem(event.getPage());
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