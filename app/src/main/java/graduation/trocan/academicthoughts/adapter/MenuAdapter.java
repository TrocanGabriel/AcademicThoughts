package graduation.trocan.academicthoughts.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.fragment.NewsFragment;
import graduation.trocan.academicthoughts.fragment.ProfessorFragment;
import graduation.trocan.academicthoughts.fragment.ProfileFragment;
import graduation.trocan.academicthoughts.fragment.StudentFragment;

/**
 * Created by Gabi on 15/04/2018.
 */

public class MenuAdapter  extends FragmentPagerAdapter {

    public static final String TAG =  "MenuAdapter";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Context mContext;
    private String roles = new String();

    public MenuAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userEmail = currentUser.getEmail();

        db.collection("roles").document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                roles = (document.getString("role"));
                            } else {
                                Log.d(TAG , "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }

                });
        if (position == 0) {
            return new ProfileFragment();
        } else if (position == 1) {
            return new NewsFragment();
        } else if(roles.equals("student") && position == 2) {
            return new StudentFragment();
        }
        else {
            return new ProfessorFragment();
        }
    }

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
        } else if(roles.equals("student") && position == 2) {
            return mContext.getString(R.string.menu_student);
        }
        else {
            return mContext.getString(R.string.menu_professor);

        }

    }
}
