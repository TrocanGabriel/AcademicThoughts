package graduation.trocan.academicthoughts.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.adapter.ProfessorMarkListAdapter;
import graduation.trocan.academicthoughts.model.ProfessorMark;


public class ProfessorFragment extends Fragment {

    private List<ProfessorMark> professorMarksList = new ArrayList<>();
    public static final String PREFS_NAME = "PROFESSOR_MARK";
    public static final String FAVORITES = "Students_list";
    private Map<String,String> searchTypes = new HashMap<>();
    private RecyclerView recyclerView;
    private ProfessorMarkListAdapter mAdapter;
    Context context = getActivity();
    public static final String TAG = "ProfessorFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_professor, container, false);
        recyclerView = view.findViewById(R.id.professor_mark_recycler_view);
        retrieveList();


        final FloatingActionButton floatingActionButton = view.findViewById(R.id.search_students_button);
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("roles").document(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if(document.getString("role").equals("student")) {
                                    floatingActionButton.setVisibility(View.GONE);
                                }
                                Log.d(TAG, document.getString("role") + "role " );
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

        if(floatingActionButton.getVisibility() != View.GONE) {


            floatingActionButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View view) {
                    context = view.getContext();
                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    final View promptView = layoutInflater.inflate(R.layout.search_students_prompt, null);


                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(promptView);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            EditText search_text = promptView.findViewById(R.id.search_text);
                                            RadioGroup radioGroup = promptView.findViewById(R.id.radio_group);
                                            if (radioGroup.getCheckedRadioButtonId() != -1) {
                                                int selectedCriteriaId = radioGroup.getCheckedRadioButtonId();
                                                RadioButton selectedCriteria = radioGroup.findViewById(selectedCriteriaId);

                                                final String searchData = search_text.getText().toString();
                                                if (!searchData.equals("")) {

                                                    professorMarksList = getFavorites(context);

                                                    Log.d(TAG, professorMarksList.toString());



                                                    switch (selectedCriteria.getText().toString()) {
                                                        case "Group":
                                                            searchTypes.put("Group",searchData);
                                                            break;

                                                        case "Course":
                                                       searchTypes.put("Course",searchData);
                                                            break;

                                                        case "Last Name":
                                                            searchTypes.put("LastName",searchData);
                                                            break;

                                                        case "First Name":
                                                            searchTypes.put("FirstName",searchData);
                                                            break;
                                                    }

                                                   professorMarksList = searchList(professorMarksList);

                                                    mAdapter.clear();
                                                    mAdapter = new ProfessorMarkListAdapter(professorMarksList);
                                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                                    recyclerView.setLayoutManager(mLayoutManager);
                                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                                    recyclerView.setAdapter(mAdapter);

                                                }


                                            }


                                        }
                                    })
                            .setNegativeButton("Reset",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            retrieveList();
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            });

        }
        else {
                floatingActionButton.setOnClickListener(null);
            }


        return view;
    }

    private List<ProfessorMark> searchList(List<ProfessorMark> professorMarksList){

        Iterator<ProfessorMark> iterator = professorMarksList.listIterator();
        Log.d(TAG + " map", searchTypes.toString());
        while(iterator.hasNext()){
            ProfessorMark mark = iterator.next();
            if  (searchTypes.get("Course") != null && !mark.getCourse().equals(searchTypes.get("Course"))){
                iterator.remove();
            }
            else if(searchTypes.get("Group") != null && !mark.getGroup().equals(searchTypes.get("Group"))){
                iterator.remove();

            }
            else if (searchTypes.get("FirstName") != null && !mark.getFirst_name().equals(searchTypes.get("FirstName"))){
                iterator.remove();

            }
            else if(searchTypes.get("LastName") != null && !mark.getLast_name().equals(searchTypes.get("LastName"))  ){
                iterator.remove();

            }

        }
        return professorMarksList;
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

                       context = getActivity();
                       saveFavorites(context, professorMarksList);

                       mAdapter = new ProfessorMarkListAdapter(professorMarksList);
                       RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                       recyclerView.setLayoutManager(mLayoutManager);
                       recyclerView.setItemAnimator(new DefaultItemAnimator());
                       recyclerView.setAdapter(mAdapter);
                   } else {

                       Log.d(TAG, "Error on retrieving list");

                   }
               }});
    }



    public void saveFavorites(Context context, List<ProfessorMark> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;


        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.apply();
    }

    public ArrayList<ProfessorMark> getFavorites(Context context) {
        SharedPreferences settings;
        List<ProfessorMark> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            ProfessorMark[] favoriteItems = gson.fromJson(jsonFavorites,
                    ProfessorMark[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;

        return (ArrayList<ProfessorMark>) favorites;
    }



}
