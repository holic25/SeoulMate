package kr.co.travelmaker.seoulmate.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kr.co.travelmaker.seoulmate.fragment.TravelerBoardDetailsFragment;
import kr.co.travelmaker.seoulmate.fragment.BoardIntroductionFragment;
import kr.co.travelmaker.seoulmate.model.TravelerBoard;

public class TravelerBoardViewPagerAdapter extends FragmentStatePagerAdapter {
    TravelerBoard travelerBoard;
    public TravelerBoardViewPagerAdapter(FragmentManager fm, TravelerBoard travelerBoard) {
        super(fm);
        this.travelerBoard = travelerBoard;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            return BoardIntroductionFragment.getInstance(travelerBoard);
        }
        else if(position==1) {
            return TravelerBoardDetailsFragment.getInstance(travelerBoard);
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}