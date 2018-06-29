package graduation.trocan.academicthoughts.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import graduation.trocan.academicthoughts.ExamsCheckingActivity;
import graduation.trocan.academicthoughts.LoginActivity;
import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.adapter.AgendaExamListAdapter;
import graduation.trocan.academicthoughts.adapter.SchoolCalendarListAdapter;
import graduation.trocan.academicthoughts.model.AgendaExam;
import graduation.trocan.academicthoughts.model.SchoolCalendar;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class AgendaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String TAG = "AGENDA";

    private List<SchoolCalendar> SchoolCalendarArrayList = new ArrayList<>();
    private List<AgendaExam> agendaExamList = new ArrayList<>();
    private Button  logoutButton;
    private Button  checkExamsButton;
    private Spinner spinner;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private SchoolCalendarListAdapter mSchoolCalendarAdapter;
    private AgendaExamListAdapter mAgendaExamAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser currentUser = mAuth.getCurrentUser();

    private RecyclerView SchoolCalendarRecyclerView;
    private RecyclerView examsRecyclerView;
    private String studentUserGroup;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);
        SchoolCalendarRecyclerView = view.findViewById(R.id.calendar_recycler_view);
        examsRecyclerView = view.findViewById(R.id.exams_recycler_view);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        logoutButton = view.findViewById(R.id.logout_button);
        checkExamsButton = view.findViewById(R.id.check_exams_button);
        spinner = view.findViewById(R.id.days_spinner);
        progressBar = view.findViewById(R.id.progressBar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String role = sharedPref.getString("role","");

        final String student = sharedPref.getString("studentUserGroup","");

        retrieveExams(role, student);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String day =  adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "day " + day);
                getSchoolCalendar(day, role, student);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Agenda activity", "Nothing selected in days spinner");
            }
        });

        //TODO: SET TIME;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        Log.d(TAG, dayOfTheWeek);
        switch (dayOfTheWeek){
            case "Monday":
                spinner.setSelection(0);
                break;
            case "Tuesday":
                spinner.setSelection(1);
                break;
            case "Wednesday":
                spinner.setSelection(2);
                break;
            case "Thursday":
                spinner.setSelection(3);
                break;
            case "Friday":
                spinner.setSelection(4);
                break;

        }

        progressBar.setVisibility(View.GONE);





        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            getActivity().finish();
            }
        });
        checkExamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ExamsCheckingActivity.class));
            }
        });

        return view;

    }

    private void retrieveExams(final String role, final String studentUser){

        Log.d(TAG, " ROLE EXAM " + role);

        if (role.equals("professor")) {
            Log.d(TAG, "PROFESSOR ROLE EXAM");
            db.collection("exams")
                    .whereEqualTo("professor", currentUser.getEmail())
                    .whereEqualTo("set", true)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                agendaExamList = new ArrayList<>();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
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

        } else if (role.equals("student")) {

            Log.d(TAG, "STUDENT ROLE EXAM");
            db.collection("exams")
                    .whereEqualTo("set", true)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                agendaExamList = new ArrayList<>();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    AgendaExam exam = queryDocumentSnapshot.toObject(AgendaExam.class);
                                    Log.d(TAG, "STUDENT ROLE EXAM email " + studentUser);
                                    if (exam.getGroups().contains(studentUser)) {
                                        agendaExamList.add(exam);

                                    }
                                }

                                Log.d(TAG, "STUDENT ROLE EXAM LIST " + agendaExamList);

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
    private void getSchoolCalendar(final String day, final String role, final String studentUser) {

        if(role.equals("professor")) {
            Log.d(TAG, "PROFESSOR ROLE SchoolCalendar");
            db.collection("calendar")
                    .whereEqualTo("day", day)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                SchoolCalendarArrayList = new ArrayList<>();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    SchoolCalendar newData = queryDocumentSnapshot.toObject(SchoolCalendar.class);
                                    Log.d(TAG, "PROFESSOR ROLE SchoolCalendar email " + currentUser.getEmail());
                                    Log.d(TAG, "PROFESSOR ROLE SchoolCalendar course " + newData.getTitle());


                                    if(newData.getProfessor().equals(currentUser.getEmail())) {
                                        SchoolCalendarArrayList.add(newData);
                                    }
                                }

                                mSchoolCalendarAdapter = new SchoolCalendarListAdapter(SchoolCalendarArrayList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                SchoolCalendarRecyclerView.setLayoutManager(mLayoutManager);
                                SchoolCalendarRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                SchoolCalendarRecyclerView.setAdapter(mSchoolCalendarAdapter);
                            } else {
                                Log.d(TAG, " SchoolCalendar spinner failed!");
                            }
                        }
                    });
        } else if(role.equals("student")) {
            Log.d(TAG, "STUDENT ROLE SchoolCalendar");


            db.collection("calendar")
                    .whereEqualTo("day", day)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                SchoolCalendarArrayList = new ArrayList<>();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    SchoolCalendar newData = queryDocumentSnapshot.toObject(SchoolCalendar.class);
                                    Log.d(TAG,  studentUserGroup + " PROFESSOR ROLE SchoolCalendar course " + newData.getGroups());

                                    if(newData.getGroups().contains(studentUser)) {
                                        Log.d(TAG, "PROFESSOR ROLE SchoolCalendar email " + currentUser.getEmail());

                                        SchoolCalendarArrayList.add(newData);
                                    }
                                }

                                mSchoolCalendarAdapter = new SchoolCalendarListAdapter(SchoolCalendarArrayList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                SchoolCalendarRecyclerView.setLayoutManager(mLayoutManager);
                                SchoolCalendarRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                SchoolCalendarRecyclerView.setAdapter(mSchoolCalendarAdapter);
                            } else {
                                Log.d(TAG, " SchoolCalendar spinner failed!");
                            }
                        }
                    });
        }
    }
}
