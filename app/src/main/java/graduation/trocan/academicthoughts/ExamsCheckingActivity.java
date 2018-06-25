package graduation.trocan.academicthoughts;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import graduation.trocan.academicthoughts.model.ProposedDays;


public class ExamsCheckingActivity extends AppCompatActivity  {

    private static List<AgendaExam> allExams = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    final static FirebaseUser currentUser = mAuth.getCurrentUser();
    private static CompactCalendarView compactCalendarView;
    private static final String TAG = "EXAMSCHECKING";
    private static ArrayList<ProposedDays> propDays;
    private static Date selectedDate;
    private String role;
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_checking);

            db.collection("roles")
                    .document(currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    if(document.getString("role").equals("student")) {
                                        role = "student";
                                        StudentExamFragment    firstFragment = new StudentExamFragment();
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, firstFragment)
                                                .commitAllowingStateLoss();
                                    }
                                    if(document.getString("role").equals("professor"))  {
                                        role = "professor";
                                        ProfessorExamFragment     firstFragment = new ProfessorExamFragment();
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, firstFragment)
                                                .commitAllowingStateLoss();
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


    retrieveAllExams();
        final ActionBar actionBar = getSupportActionBar();
       compactCalendarView =  findViewById(R.id.compactcalendar_view);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(dateFormatMonth.format((compactCalendarView.getFirstDayOfCurrentMonth())));


        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate = dateClicked;
                TextView selectedDate;
                if(role.equals("student")){
                     selectedDate = findViewById(R.id.student_exam_date_selected);

                } else {
                    selectedDate = findViewById(R.id.professor_exam_date_selected);

                }
                selectedDate.setText(DATE_FORMAT.format(dateClicked));
                Context context = ExamsCheckingActivity.this;
                events = compactCalendarView.getEvents(dateClicked);

                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);
                if (events.size() != 0) {
                    int countExams = 0;
                    int countProposedExams = 0;
                    for(Event ev : events){
                        if(ev.getColor() == Color.GREEN){
                            countExams++;
                        }
                        else {
                            countProposedExams++;
                        }
                    }
                    if(countExams == 0 && countProposedExams != 0){
                        Toast.makeText(context, "Exam: " + events.get(0).getData().toString() + " proposed by " + events.size() + " students!", Toast.LENGTH_SHORT).show();

                    } else if(countExams == 1){
                        Toast.makeText(context, "Exam: " + events.get(0).getData().toString() , Toast.LENGTH_SHORT).show();
                    }
            }
                else {

                Toast.makeText(context, "No events on this day ", Toast.LENGTH_SHORT).show();
            }

        }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });
    }

    public static void retrieveAllExams(){

        for(AgendaExam exam : allExams){
            compactCalendarView.removeAllEvents();
            break;
        }
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
                                if(exam.getSet()){
                                    Log.d(TAG, "EXAM DATE SHOW " + exam.getDate().getTime());
                                    Event event = new Event(Color.GREEN, exam.getDate().getTime(), exam.getCourse() + " with " +exam.getProfessor());
                                    compactCalendarView.addEvent(event);
                                }

                            }


                            } else {
                                Log.d(TAG, "ERROR CHECK EXAM COURSE");
                        }
                    }
                });

    }


    public static void updateCalendar(String course, String userGroup){
        Log.d(TAG, "ERROR CHECK EXAM COURSE");

        for(final AgendaExam exam : allExams){
            if((exam.getGroups().contains(userGroup) || exam.getProfessor().equals(userGroup)) && exam.getCourse().equals(course)){
             String   uid = exam.getUid();
                db.collection("exams")
                        .document(uid)
                        .collection("proposedDays")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(propDays != null) {
                                        for (ProposedDays day : propDays) {
                                            compactCalendarView.removeEvents(day.getDate());
                                        }
                                    }
                                     propDays = new ArrayList<>();
                                    for(DocumentSnapshot doc : task.getResult()){
                                        ProposedDays prop = doc.toObject(ProposedDays.class);
                                        propDays.add(prop);
                                    }
                                    for (ProposedDays day : propDays){
                                        Log.d(TAG, "EXAM PROPOSED DATE SHOW " + day.getDate().getTime());
                                        Event event = new Event(Color.RED, day.getDate().getTime(),exam.getCourse() + " with " +exam.getProfessor());

                                        compactCalendarView.addEvent(event);
                                    }



                                }
                            }
                        });

                break;

            }


        }


}

    public static void saveProposedDate(String s, String userGroup, String courseSelected) {

        if (!s.equals("No date selected")) {


            Boolean valid = false;
            for (AgendaExam exam : allExams) {
                if (exam.getSet() &&
                        exam.getDate() == selectedDate &&
                        exam.getGroups().contains(userGroup)) {
                } else {
                    valid = true;
                }
            }
            if (valid) {
                for (AgendaExam exam : allExams) {
                    if (!exam.getSet() && exam.getGroups().contains(userGroup) && exam.getCourse().equals(courseSelected)) {
                        Boolean userExists = false;
                        for (ProposedDays prop : propDays)
                             {
                                 if(prop.getStudent().equals(currentUser.getEmail())){
                                     userExists = true;
                                     break;
                                 }

                        }
                        if(!userExists) {
                            ProposedDays prop = new ProposedDays(currentUser.getEmail(), selectedDate);
                            db.collection("exams")
                                    .document(exam.getUid())
                                    .collection("proposedDays")
                                    .document(currentUser.getEmail())
                                    .set(prop);

                        } else {
                            db.collection("exams")
                                    .document(exam.getUid())
                                    .collection("proposedDays")
                                    .document(currentUser.getEmail())
                                    .update("date", selectedDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);

                                        }
                                    });
                        }
                    }
                }
                updateCalendar(courseSelected,userGroup);
            }

        }

    }

    public static void setExam(String courseSelected){
        final String[] examUID = new String[1];
        db.collection("exams")
                .whereEqualTo("course",courseSelected)
                .whereEqualTo("professor",currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for(DocumentSnapshot doc : task.getResult()){
                                Log.d(TAG, "COMPLETED:  TO FIND COURSE IN SPINNER");
                                AgendaExam exam = doc.toObject(AgendaExam.class);
                                examUID[0] = exam.getUid();
                            }
                            db.collection("exams")
                                    .document(examUID[0])
                                    .update("set", true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating isSet", e);
                                        }
                                    });
                            db.collection("exams")
                                    .document(examUID[0])
                                    .update("date", selectedDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating isSet", e);
                                        }
                                    });

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "FAILED TO FIND COURSE IN SPINNER");
                    }
                });

       ExamsCheckingActivity.retrieveAllExams();
    }



}


