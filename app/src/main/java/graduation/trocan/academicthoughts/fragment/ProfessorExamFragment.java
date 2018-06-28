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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import graduation.trocan.academicthoughts.ExamsCheckingActivity;
import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.AgendaExam;


public class ProfessorExamFragment extends Fragment {


    private  FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private  List<String> courses;
    private List<AgendaExam> allExams;
    private  Spinner courseSpinner;
    private  String courseSelected;
    private static final String TAG = "PROFESSOR EXAM";


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


         final View view = inflater.inflate(R.layout.fragment_professor_exam, container, false);
        final TextView selectedDate = view.findViewById(R.id.professor_exam_date_selected);
        Button button = view.findViewById(R.id.button_professor_set_date);
        courseSpinner = view.findViewById(R.id.course_spinner_professor);

        retrieveProfessorExams(getContext());


        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseSelected = parent.getItemAtPosition(position).toString();
                Log.d("UPDATE CALENDAR PROF", courseSelected + " courseSelected");
//                if(position != 0 )
                ExamsCheckingActivity.updateCalendar(courseSelected, currentUser.getEmail());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("STUDENT EXAM FRAG", "Nothing selected in course spinner");

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ExamsCheckingActivity.setExam(courseSelected);
            }
        });


        return view;
    }

    public  void retrieveProfessorExams(final Context context) {
        db.collection("exams")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            courses = new ArrayList<>();
                            for(QueryDocumentSnapshot queryDocumentSnapshot :task.getResult()){
                                AgendaExam exam = queryDocumentSnapshot.toObject(AgendaExam.class);
                                if(exam.getProfessor().equals(currentUser.getEmail())) {
                                    courses.add(exam.getCourse());
                                }
                            }
                            ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, courses);
                            courseSpinner.setAdapter(categoriesAdapter);
                            Log.d(TAG, "professor ROLE EXAM LIST CALENDAR" + courses);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "retrieving exams for professor failed");
                    }
                });
    }

}
