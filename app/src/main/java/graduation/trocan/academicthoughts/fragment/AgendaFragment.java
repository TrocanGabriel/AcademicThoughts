package graduation.trocan.academicthoughts.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import graduation.trocan.academicthoughts.LoginActivity;
import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.adapter.AgendaExamListAdapter;
import graduation.trocan.academicthoughts.adapter.CalendarListAdapter;
import graduation.trocan.academicthoughts.model.AgendaExam;
import graduation.trocan.academicthoughts.model.Calendar;


public class AgendaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String TAG = "AGENDA";

    private List<Calendar> calendarArrayList = new ArrayList<>();
    private List<AgendaExam> agendaExamList = new ArrayList<>();
    private Button  logoutButton;
    private Spinner spinner;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CalendarListAdapter mCalendarAdapter;
    private AgendaExamListAdapter mAgendaExamAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser currentUser = mAuth.getCurrentUser();

    private RecyclerView calendarRecyclerView;
    private RecyclerView examsRecyclerView;
    private String studentUser;
    private String studentUserGroup;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        calendarRecyclerView = view.findViewById(R.id.calendar_recycler_view);
        examsRecyclerView = view.findViewById(R.id.exams_recycler_view);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        logoutButton = view.findViewById(R.id.logout_button);
        spinner = view.findViewById(R.id.days_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String day =  adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "day " + day);
                getCalendar(day);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Agenda activity", "Nothing selected in days spinner");
            }
        });


        retrieveExams();

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

    private void retrieveExams(){
        getUserRole();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String role = sharedPref.getString("role","");
        final String studentUser = sharedPref.getString("studentUserGroup", "");

        if(role.equals("professor")){
            Log.d(TAG, "PROFESSOR ROLE EXAM");
            db.collection("exams")
                    .whereEqualTo("professor",currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           if(task.isSuccessful()){
                               agendaExamList = new ArrayList<>();
                               for(QueryDocumentSnapshot queryDocumentSnapshot :task.getResult()){
                                   AgendaExam exam = queryDocumentSnapshot.toObject(AgendaExam.class);
                                   agendaExamList.add(exam);
                               }

                               mAgendaExamAdapter = new AgendaExamListAdapter(agendaExamList);
                               RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                               examsRecyclerView.setLayoutManager(mLayoutManager);
                               examsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                               examsRecyclerView.setAdapter(mAgendaExamAdapter);
                           }
                        }
                    });

        } else if (role.equals("student")){
            Log.d(TAG, "STUDENT ROLE EXAM");
            db.collection("exams")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                agendaExamList = new ArrayList<>();
                                for(QueryDocumentSnapshot queryDocumentSnapshot :task.getResult()){
                                    AgendaExam exam = queryDocumentSnapshot.toObject(AgendaExam.class);
                                    Log.d(TAG, "PROFESSOR ROLE EXAM email " + studentUser);
                                    Log.d(TAG, "PROFESSOR ROLE EXAM course " + exam.getGroups());
                                    if(exam.getGroups().contains(studentUser)) {
                                        agendaExamList.add(exam);
                                    }
                                }

                                mAgendaExamAdapter = new AgendaExamListAdapter(agendaExamList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                examsRecyclerView.setLayoutManager(mLayoutManager);
                                examsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                examsRecyclerView.setAdapter(mAgendaExamAdapter);
                            }
                        }
                    });
        }
    }
    private void getCalendar(String day) {
        getUserRole();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String role = sharedPref.getString("role","");
        final String studentUser = sharedPref.getString("studentUserGroup", "");
        if(role.equals("professor")) {
            Log.d(TAG, "PROFESSOR ROLE CALENDAR");
            db.collection("calendar")
                    .whereEqualTo("day", day)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                calendarArrayList = new ArrayList<>();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    Calendar newData = queryDocumentSnapshot.toObject(Calendar.class);
                                    Log.d(TAG, "PROFESSOR ROLE CALENDAR email " + currentUser.getEmail());
                                    Log.d(TAG, "PROFESSOR ROLE CALENDAR course " + newData.getTitle());


                                    if(newData.getProfessor().equals(currentUser.getEmail())) {
                                        calendarArrayList.add(newData);
                                    }
                                }

                                mCalendarAdapter = new CalendarListAdapter(calendarArrayList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                calendarRecyclerView.setLayoutManager(mLayoutManager);
                                calendarRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                calendarRecyclerView.setAdapter(mCalendarAdapter);
                            } else {
                                Log.d(TAG, " Calendar spinner failed!");
                            }
                        }
                    });
        } else if(role.equals("student")) {
            Log.d(TAG, "STUDENT ROLE CALENDAR");


            db.collection("calendar")
                    .whereEqualTo("day", day)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                calendarArrayList = new ArrayList<>();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    Calendar newData = queryDocumentSnapshot.toObject(Calendar.class);
                                    Log.d(TAG,  studentUserGroup + " PROFESSOR ROLE CALENDAR course " + newData.getGroups());

                                    if(newData.getGroups().contains(studentUser)) {
                                        Log.d(TAG, "PROFESSOR ROLE CALENDAR email " + currentUser.getEmail());

                                        calendarArrayList.add(newData);
                                    }
                                }

                                mCalendarAdapter = new CalendarListAdapter(calendarArrayList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                calendarRecyclerView.setLayoutManager(mLayoutManager);
                                calendarRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                calendarRecyclerView.setAdapter(mCalendarAdapter);
                            } else {
                                Log.d(TAG, " Calendar spinner failed!");
                            }
                        }
                    });
        }
    }

    private void getUserRole() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("roles").document(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if(document.getString("role").equals("student")) {
                                    getStudent();
                                    Context context = getActivity();
                                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("role", "student");
                                    editor.apply();
                                }
                                else {
                                    Context context = getActivity();
                                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("role", "professor");
                                    editor.apply();

                                }
                                Log.d(TAG, document.getString("role") + " role " );
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


    }

    private void getStudent() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("students")
                .document(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Context context = getActivity();
                            String data  = documentSnapshot.getString("group");
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("studentUserGroup", data);
                            editor.apply();
                        }
                        else {
                            Log.d(TAG, "PROFESSOR ROLE EXAM register  FAIL" + studentUserGroup);

                        }
                    }
                });
    }


}
