package kr.co.travelmaker.seoulmate.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kr.co.travelmaker.seoulmate.fragment.BoardIntroductionFragment;
import kr.co.travelmaker.seoulmate.fragment.GuideBoardDetailsFragment;
import kr.co.travelmaker.seoulmate.fragment.GuideBoardIntroductionFragment;
import kr.co.travelmaker.seoulmate.fragment.GuideBoardReviewsFragment;
import kr.co.travelmaker.seoulmate.model.GuideBoardMember;

public class GuideBoardViewPagerAdapter extends FragmentStatePagerAdapter {
    GuideBoardMember guideBoard;
    public GuideBoardViewPagerAdapter(FragmentManager fm, GuideBoardMember guideBoard) {
        super(fm);
        this.guideBoard = guideBoard;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            return GuideBoardIntroductionFragment.getInstance(guideBoard);
        }
        else if(position==1) {
            return GuideBoardDetailsFragment.getInstance(guideBoard);
        }
        else if(position==2) {
            return GuideBoardReviewsFragment.getInstance(guideBoard);
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}