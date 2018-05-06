package graduation.trocan.academicthoughts.fragment;

import android.content.Context;
import android.net.Uri;
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
import android.widget.ListAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.adapter.ProfessorMarkListAdapter;
import graduation.trocan.academicthoughts.model.ProfessorMark;


public class ProfessorFragment extends Fragment {

    private List<ProfessorMark> professorMarksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProfessorMarkListAdapter mAdapter;
    Context context;
    public static final String TAG = "ProfessorFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_professor, container, false);
        recyclerView = view.findViewById(R.id.professor_mark_recycler_view);

        retrieveList();
        return view;
    }


    private void retrieveList(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
       db.collection("professors").document(currentUser.getEmail()).collection("myStudents")
               .orderBy("last_name")
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()){
                           professorMarksList = new ArrayList<>();
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               ProfessorMark newAdded = document.toObject(ProfessorMark.class);
                               Log.d(TAG, "Stud Name : " + newAdded.getLast_name());
                               professorMarksList.add(newAdded);
                           }
                           mAdapter = new ProfessorMarkListAdapter(professorMarksList);
                           RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                           recyclerView.setLayoutManager(mLayoutManager);
                           recyclerView.setItemAnimator(new DefaultItemAnimator());
                           recyclerView.setAdapter(mAdapter);
                       }else {

                           Log.d(TAG, "Error on retrieving list");

                       }
                   }
               });
    }


}
