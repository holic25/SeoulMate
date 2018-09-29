package kr.co.travelmaker.seoulmate.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kr.co.travelmaker.seoulmate.fragment.BoardFragment;
import kr.co.travelmaker.seoulmate.fragment.CulturalMainFragment;
import kr.co.travelmaker.seoulmate.fragment.FirebaseMessengerFragment;
import kr.co.travelmaker.seoulmate.fragment.MypageFragment;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if(position==0) {
            return new BoardFragment();
        }
        else if(position==1) {
            return new FirebaseMessengerFragment();
        }
        else if(position==2) {
            return new CulturalMainFragment();
        }
        else if(position==3) {
            return new MypageFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}