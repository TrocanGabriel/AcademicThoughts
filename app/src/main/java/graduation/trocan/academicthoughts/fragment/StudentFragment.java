package graduation.trocan.academicthoughts.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.adapter.StudentMarkListAdapter;
import graduation.trocan.academicthoughts.model.StudentMark;


public class StudentFragment extends Fragment {

    private List<StudentMark> studentMarkList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StudentMarkListAdapter mAdapter;
    Context context;
    public static final String TAG = "StudentFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_student, container, false);
        recyclerView = view.findViewById(R.id.student_mark_recycler_view);

        retrieveList();
        return view;
    }


    private void retrieveList(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("students").document(currentUser.getEmail()).collection("marks")
                .orderBy("course")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            studentMarkList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                StudentMark newAdded = document.toObject(StudentMark.class);
                                Log.d(TAG, "Course : " + newAdded.getCourse());
                                studentMarkList.add(newAdded);
                            }
                            mAdapter = new StudentMarkListAdapter(studentMarkList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }else {

                            Log.d(TAG, "Error on student marks  retrieving list");

                        }
                    }
                });
    }

}
