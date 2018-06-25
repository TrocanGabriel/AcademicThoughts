package graduation.trocan.academicthoughts.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.StudentMark;

public class StudentMarkListAdapter extends  RecyclerView.Adapter<StudentMarkListAdapter.ViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<StudentMark> studentMarkList;


    public class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView mCourseView;
        public TextView mMarkView;
        public ImageButton imageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mCourseView = itemView.findViewById(R.id.student_mark_course_show);
            mMarkView = itemView.findViewById(R.id.student_mark_mark_show);
            imageButton = itemView.findViewById(R.id.menu_button);

        }
    }

    public StudentMarkListAdapter(List<StudentMark> studentMarkList){
        this.studentMarkList = studentMarkList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_mark_item_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudentMark studentMark = studentMarkList.get(position);
        holder.mCourseView.setText(studentMark.getCourse() + ": ");
        holder.mMarkView.setText(String.valueOf(studentMark.getMark()));
        final StudentMark modifiedData = studentMarkList.get(position);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View itemView) {
                final PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
                final FirebaseUser currentUser = mAuth.getCurrentUser();

                final MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.stud_mark_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String currentDoc = currentUser.getEmail();
                        switch (item.getItemId()) {

                            case R.id.email_professor:

                                final Context finalContext;
                                finalContext = itemView.getContext();
                                db.collection("students").document(currentDoc).collection("marks")
                                        .whereEqualTo("course", modifiedData.getCourse())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                               StudentMark studentMark =  queryDocumentSnapshots.getDocuments().get(0).toObject(StudentMark.class);
                                                String professor_email = studentMark.getProfessor();
                                                String [] emails = new String[]{professor_email};
                                                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                        "mailto",emails[0], null));
                                                i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                                                i.putExtra(Intent.EXTRA_TEXT, "Body");
                                        try {
                                            itemView.getContext().startActivity(Intent.createChooser(i, "Send mail..."));
                                        } catch (android.content.ActivityNotFoundException ex) {
                                            Toast.makeText(finalContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                        }
                                            }

                                        });

                                break;

                        }

                        return false;
                    }

                });

                popup.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return studentMarkList.size();
    }
}

