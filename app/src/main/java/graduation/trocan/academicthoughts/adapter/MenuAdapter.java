package graduation.trocan.academicthoughts.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.fragment.NewsFragment;
import graduation.trocan.academicthoughts.fragment.ProfileFragment;
import graduation.trocan.academicthoughts.fragment.StudentFragment;

/**
 * Created by Gabi on 15/04/2018.
 */

public class MenuAdapter  extends FragmentPagerAdapter {


    private Context mContext;

    public MenuAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ProfileFragment();
        } else if (position == 1) {
            return new NewsFragment();
        } else {
            return new StudentFragment();
        }
    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.menu_profile);
        } else if (position == 1) {
            return mContext.getString(R.string.menu_news);
        } else {
            return mContext.getString(R.string.menu_student);
        }
    }
}
