package graduation.trocan.academicthoughts;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import graduation.trocan.academicthoughts.fragment.ProfessorExamFragment;
import graduation.trocan.academicthoughts.fragment.StudentExamFragment;
import graduation.trocan.academicthoughts.model.AgendaExam;

public class ExamsCheckingActivity extends AppCompatActivity {

    private List<AgendaExam> allExams = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser currentUser = mAuth.getCurrentUser();
    private CompactCalendarView compactCalendarView;
    private static final String TAG = "EXAMSCHECKING";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_checking);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            db.collection("roles")
                    .document(currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Fragment firstFragment;
                                    if(document.getString("role").equals("student")) {

                                        firstFragment = new StudentExamFragment();
                                    } else {
                                        firstFragment = new ProfessorExamFragment();
                                    }
                                    getSupportFragmentManager().beginTransaction()
                                            .add(R.id.fragment_container, firstFragment).commit();
                                    Log.d(TAG, document.getString("role") + "role " );
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
        }


    retrieveAllExams();
        final ActionBar actionBar = getSupportActionBar();
       compactCalendarView =  findViewById(R.id.compactcalendar_view);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(dateFormatMonth.format((compactCalendarView.getFirstDayOfCurrentMonth())));

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = ExamsCheckingActivity.this;
                events = compactCalendarView.getEvents(dateClicked);
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);
                if (events.size() != 0) {
                    Toast.makeText(context, " " + events.get(0).getData().toString(), Toast.LENGTH_SHORT).show();
                }
                else {
                Toast.makeText(context, "No examen scheduled ", Toast.LENGTH_SHORT).show();
            }

        }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }

    private void retrieveAllExams(){

        Log.d(TAG, "ALLEXAMS");
        db.collection("exams")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            allExams  = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                AgendaExam agendaExam = doc.toObject(AgendaExam.class);
                                allExams.add(agendaExam);
                                Log.d(TAG, " CHECK EXAM COURSE " + agendaExam.getCourse());

                            }
                            for(AgendaExam exam : allExams) {
                                Log.d(TAG, "EXAM DATE SHOW " + exam.getDate().getTime());
                                Event event = new Event(Color.BLACK, exam.getDate().getTime(),"Examen: " + exam.getCourse() + " cu " +exam.getProfessor() ADD);
                                compactCalendarView.addEvent(event);
                            }


                            } else {
                                Log.d(TAG, "ERROR CHECK EXAM COURSE");
                        }
                    }
                });
    }



}
