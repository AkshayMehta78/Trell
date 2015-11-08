package app.geochat.ui.adapters;

/**
 * Created by akshay on 29/7/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;
/**
 * Generic view pager to maintain tabs and their click events
 * <a href="http://stackoverflow.com/questions/18747975/difference-between-fragmentpageradapter-and-fragmentstatepageradapter">FragmentPagerAdapter vs FragmentStatePagerAdapter</a>
 * Created by akshay.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    private List<Fragment> mFragments;
    private List<String> mTabTitles;
    private Bundle mBundle;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles, Bundle bundle) {
        super(fm);
        mFragments = fragments;
        mTabTitles = titles;
        mBundle= bundle;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "position : " + position);
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position);
    }
}
