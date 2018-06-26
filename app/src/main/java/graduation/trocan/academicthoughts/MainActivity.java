package graduation.trocan.academicthoughts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import graduation.trocan.academicthoughts.adapter.MenuAdapter;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);

        final ViewPager viewPager =  findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);

        getUserRole(new UserCallbackRole() {
            @Override
            public void userRole(String role) {
                MenuAdapter adapter = new MenuAdapter(MainActivity.this, getSupportFragmentManager());

                viewPager.setAdapter(adapter);

                TabLayout tabLayout =  findViewById(R.id.tabs);

                tabLayout.setupWithViewPager(viewPager);
            }
        });



      

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is not signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void getUserRole(final UserCallbackRole callback) {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseFirestore.getInstance().collection("roles").document(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.getString("role").equals("student")) {
                                getStudent(new GetStudentCallback() {
                                    @Override
                                    public void student(String student) {
                                        callback.userRole("");
                                    }
                                });
                                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("role", "student");
                                editor.apply();
                            }
                            else {

                                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("role", "professor");
                                editor.apply();
                                callback.userRole("");

                            }

//                            if (document.exists()) {
//                                callback.userRole(document.getString("role"));
//                                Log.d(TAG, document.getString("role") + " role " );
//                            } else {
//                                Log.d(TAG, "No such document");
//                            }
                        } else {
                            Log.d("", "get failed with ", task.getException());
                        }
                    }
                });


    }


    private void getStudent(final GetStudentCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseFirestore.getInstance().collection("students")
                .document(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String data  = documentSnapshot.getString("group");
                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("studentUserGroup", data);
                            editor.apply();

                            callback.student(data);
                        }
                        else {
                            Log.d("", "PROFESSOR ROLE EXAM register  FAIL" );

                        }
                    }
                });
    }

    private interface UserCallbackRole{
        void userRole(String role);
    }


    private interface GetStudentCallback{
        void student(String student);
    }
}
