package graduation.trocan.academicthoughts.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import graduation.trocan.academicthoughts.MainActivity;
import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.fragment.AgendaFragment;
import graduation.trocan.academicthoughts.fragment.NewsFragment;
import graduation.trocan.academicthoughts.fragment.ProfessorFragment;
import graduation.trocan.academicthoughts.fragment.StudentFragment;

/**
 * Created by Gabi on 15/04/2018.
 */

public class MenuAdapter  extends FragmentPagerAdapter {

    public static final String TAG = "MenuAdapter";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Context mContext;
    private String roles;

    public MenuAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();


        SharedPreferences sharedPref = ((MainActivity)mContext).getPreferences(Context.MODE_PRIVATE);
        String roles = sharedPref.getString("role","");

        if (position == 0) {
            return new AgendaFragment();
        } else if (position == 1) {
            return new NewsFragment();
        } else if (roles.equals("student") && position == 2) {
            return new StudentFragment();
        } else if (roles.equals("professor") && position == 2) {
            return new ProfessorFragment();
        }

    return null;
}

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.menu_agenda);
        } else if (position == 1) {
            return mContext.getString(R.string.menu_news);
        } else  {
            return mContext.getString(R.string.menu_grades);
        }
    }
}
