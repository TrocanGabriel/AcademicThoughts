package graduation.trocan.academicthoughts.fragment;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import graduation.trocan.academicthoughts.ExamsCheckingActivity;
import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.AgendaExam;
import graduation.trocan.academicthoughts.model.ProposedDays;
import graduation.trocan.academicthoughts.model.Student;
import graduation.trocan.academicthoughts.model.StudentMark;


public class StudentExamFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser currentUser = mAuth.getCurrentUser();
    private List<String> courses;
    private List<AgendaExam> allExams;
    private String selectedDateString;
    private String userGroup;
    private Spinner courseSpinner;
    private String courseSelected;
    private static final String TAG = "STUDENT EXAM";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        retrieveAllExams();
        retrieveUser();
        // Inflate the layout for this fragment
       final View view = inflater.inflate(R.layout.fragment_student_exam, container, false);
       Bundle bundle = getArguments();

        final TextView selectedDate = view.findViewById(R.id.student_exam_date_selected);
        Button button = view.findViewById(R.id.button_student_send_date);

        if(bundle != null) {
            selectedDateString = bundle.getString("date");
            selectedDate.setText(selectedDateString);
        } else {
            selectedDate.setText("No date selected");
        }

        courseSpinner = view.findViewById(R.id.course_spinner_student);

        db.collection("students")
                .document(currentUser.getEmail())
                .collection("marks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            courses = new ArrayList<>();
                            courses.add("");
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                StudentMark studentMark = doc.toObject(StudentMark.class);
                                courses.add(studentMark.getCourse());
                            }
                             courseSpinner = view.findViewById(R.id.course_spinner_student);
                            ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, courses);
                            courseSpinner.setAdapter(categoriesAdapter);
                        }
                    }
                });

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
                if(!selectedDate.getText().toString().equals("No date selected")){

                    DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);

                    Date newDate = null;
                    try {
                        newDate = (Date) dateFormat.parse(selectedDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Boolean valid = false;
                   for(AgendaExam exam : allExams){
                       if(exam.getSet() &&
                               exam.getDate() == newDate &&
                               exam.getGroups().contains(userGroup)){
                           Toast.makeText(getContext(),"Exam already scheduled in that day", Toast.LENGTH_SHORT).show();
                       } else {
                           valid = true;
                       }
                   }
                   if(valid){
                       for(AgendaExam exam : allExams){
                           if(!exam.getSet() && exam.getGroups().contains(userGroup) && exam.getCourse().equals(courseSelected)){
                               ProposedDays prop = new ProposedDays(currentUser.getEmail(), newDate);
                               db.collection("exams")
                                       .document(exam.getUid())
                                       .collection("proposedDays")
                                       .add(prop)
                                       .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                           @Override
                                           public void onSuccess(DocumentReference documentReference) {
                                               Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                               Toast.makeText(getContext(),"Exam set", Toast.LENGTH_SHORT).show();


                                           }
                                       })
                                       .addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               Log.w(TAG, "Error adding document", e);
                                           }
                                       });

                               //TODO isSet for professor here
//                               db.collection("exams")
//                                       .document(exam.getUid())
//                                       .update("isSet", true)
//                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                           @Override
//                                           public void onSuccess(Void aVoid) {
//                                               Log.d(TAG, "DocumentSnapshot successfully updated!");
//
//                                           }
//                                       })
//                                       .addOnFailureListener(new OnFailureListener() {
//                                           @Override
//                                           public void onFailure(@NonNull Exception e) {
//                                               Log.w(TAG, "Error updating isSet", e);
//                                           }
//                                       });
                           }
                       }
                   }
                }
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


}
