package app.geochat.ui.adapters;

/**
 * Created by akshaymehta on 08/11/15.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mFragments;
    private Fragment mFragment = null;
    private final List<String>   TAB_TITLES;

    public TabsPagerAdapter(android.support.v4.app.FragmentManager fm,
                        ArrayList<Fragment> fragments,List<String> titlesList ) {
        super(fm);
        mFragments = fragments;
        TAB_TITLES = titlesList;
    }

    @Override

    public CharSequence getPageTitle(int position) {
        Log.e("position",position+"");
        return TAB_TITLES.get(position);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.size();
    }

    @Override
    public Fragment getItem(int position) {
        mFragment = mFragments.get(position);
        return mFragment;
    }


}