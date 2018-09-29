package kr.co.travelmaker.seoulmate.adapter;

        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentStatePagerAdapter;

        import kr.co.travelmaker.seoulmate.fragment.GuideFragment;
        import kr.co.travelmaker.seoulmate.fragment.TravelerFragment;

public class BoardViewPagerAdapter extends FragmentStatePagerAdapter {
    public BoardViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            return TravelerFragment.getInstance();
        }
        else if(position==1) {
            return GuideFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}