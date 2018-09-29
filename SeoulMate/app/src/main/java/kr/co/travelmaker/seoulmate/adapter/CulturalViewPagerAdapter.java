package kr.co.travelmaker.seoulmate.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kr.co.travelmaker.seoulmate.fragment.CulturalDetailFragment;
import kr.co.travelmaker.seoulmate.fragment.CulturalInfoFragment;

public class CulturalViewPagerAdapter extends FragmentStatePagerAdapter {
    public CulturalViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            return CulturalInfoFragment.getInstance();
        }
        else if(position==1) {
            return CulturalDetailFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}