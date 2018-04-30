package graduation.trocan.academicthoughts.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import graduation.trocan.academicthoughts.LoginActivity;
import graduation.trocan.academicthoughts.MainActivity;
import graduation.trocan.academicthoughts.R;

import static java.lang.String.format;


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String TAG = "Profile";

    private TextView  userName;
    private TextView  userRole;
    private Button    logoutButton;
    private ImageView userPhoto;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        userName = view.findViewById(R.id.user_name);
        userRole = view.findViewById(R.id.user_role);
        logoutButton = view.findViewById(R.id.logout_button);
        userPhoto = view.findViewById(R.id.user_photo);


        if (currentUser != null){
        } else {

        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            }
        });


        return view;

    }


}
