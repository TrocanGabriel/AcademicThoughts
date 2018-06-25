package graduation.trocan.academicthoughts.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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

import graduation.trocan.academicthoughts.ExamsCheckingActivity;
import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.AgendaExam;
import graduation.trocan.academicthoughts.model.Student;


public class StudentExamFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser currentUser = mAuth.getCurrentUser();
    private  List<String> courses;
    private List<AgendaExam> allExams;
    private String selectedDateString;
    private  String userGroup;
    private  Spinner courseSpinner;
    private String courseSelected;
    private static final String TAG = "STUDENT EXAM";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        retrieveUser();
        retrieveAllExams();

       final View view = inflater.inflate(R.layout.fragment_student_exam, container, false);
        final TextView selectedDate = view.findViewById(R.id.student_exam_date_selected);
        Button button = view.findViewById(R.id.button_student_send_date);
        courseSpinner = view.findViewById(R.id.course_spinner_student);

        retrieveStudentExams(getContext());


        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseSelected = parent.getItemAtPosition(position).toString();
                ExamsCheckingActivity.updateCalendar(courseSelected, userGroup);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("STUDENT EXAM FRAG", "Nothing selected in course spinner");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExamsCheckingActivity.saveProposedDate(selectedDate.getText().toString(), userGroup,courseSelected);
                           }
                       });
   return view;
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
                        } else {
                            Log.d(TAG, "ERROR CHECK EXAM COURSE");
                        }
                    }
                });
    }

    private void retrieveUser(){
        db.collection("students")
                .document(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            Student student = doc.toObject(Student.class);
                            userGroup = student.getGroup();
                        }
                    }
                });
    }

    public void retrieveStudentExams(final Context context) {

        db.collection("exams")
                .whereEqualTo("set", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            courses = new ArrayList<>();
                            for(QueryDocumentSnapshot queryDocumentSnapshot :task.getResult()){
                                AgendaExam exam = queryDocumentSnapshot.toObject(AgendaExam.class);
                                Log.d(TAG, "STUDENT ROLE EXAM email " + userGroup);
                                if(exam.getGroups().contains(userGroup)) {
                                    courses.add(exam.getCourse());
                            }
                            }
                            ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, courses);
                            courseSpinner.setAdapter(categoriesAdapter);
                            Log.d(TAG, "STUDENT ROLE EXAM LIST CALENDAR" + courses);

                        }
                    }
                });
    }


}
