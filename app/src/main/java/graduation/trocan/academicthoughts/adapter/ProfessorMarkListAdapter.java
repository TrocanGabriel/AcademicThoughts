package graduation.trocan.academicthoughts.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.ProfessorMark;

/**
 * Created by Gabi on 06/05/2018.
 */

public class ProfessorMarkListAdapter extends RecyclerView.Adapter<ProfessorMarkListAdapter.ViewHolder>{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<ProfessorMark> professorMarkList;
    public static final String TAG = "ProfMarkAdapter";


    public class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView mNameView;
        public TextView mMarkView;
        public TextView mCourseView;
        public ImageButton imageButton;


        public ViewHolder(View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.stud_name_show);
            mMarkView = itemView.findViewById(R.id.stud_mark_show);
            mCourseView = itemView.findViewById(R.id.course_name_show);
            imageButton = itemView.findViewById(R.id.prof_mark_menu_button);

        }
    }


    public ProfessorMarkListAdapter (List<ProfessorMark> professorMarkList){
        this.professorMarkList = professorMarkList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.professor_mark_item_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ProfessorMark professorMark = professorMarkList.get(position);
        String studentName = professorMark.getLast_name() + " " + professorMark.getFirst_name();
        holder.mNameView.setText(studentName);
        holder.mMarkView.setText(Integer.toString(professorMark.getMark()));
        holder.mCourseView.setText(professorMark.getCourse() + ": ");
        final ProfessorMark modifiedData = professorMarkList.get(position);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View itemView) {
                final PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
                final FirebaseUser currentUser = mAuth.getCurrentUser();
                final MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.prof_mark_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String studentEmail = modifiedData.getEmail();

                        switch (item.getItemId()) {
                            case R.id.give_mark:

                                final Context context = itemView.getContext();
                                LayoutInflater layoutInflater = LayoutInflater.from(context);
                                final View promptView = layoutInflater.inflate(R.layout.give_mark_prompt, null);
                                final EditText editingMark = promptView.findViewById(R.id.new_mark);
                                editingMark.setText(Integer.toString(modifiedData.getMark()));
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setView(promptView);
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        if(!editingMark.getText().toString().equals("")) {
                                                            final int newData = Integer.parseInt(String.valueOf(editingMark.getText()));
                                                            if (newData <= 10 && newData >= 1) {
                                                                DocumentReference profMarkRef = db.collection("professors")
                                                                        .document(currentUser.getEmail())
                                                                        .collection("myStudents")
                                                                        .document(modifiedData.getEmail());
                                                                profMarkRef.update("mark", newData);
                                                                professorMarkList.get(professorMarkList.indexOf(modifiedData)).setMark(newData);
                                                                db.collection("students")
                                                                        .document(modifiedData.getEmail())
                                                                        .collection("marks")
                                                                        .whereEqualTo("course", modifiedData.getCourse())
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                                                        db.collection("students")
                                                                                                .document(modifiedData.getEmail())
                                                                                                .collection("marks")
                                                                                                .document(document.getId())
                                                                                                .update("mark", newData);
                                                                                    }
                                                                                } else {
                                                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                                                }
                                                                            }
                                                                        });


                                                                notifyDataSetChanged();

                                                            } else {
                                                                Toast.makeText(context, "Grades should be between 1 and 10", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(context, "Grades should be between 1 and 10", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();
                                break;

                            case R.id.email_student:

                                final Context finalContext;
                                finalContext = itemView.getContext();
                                db.collection("professors")
                                        .document(currentUser.getEmail())
                                        .collection("myStudents")
                                        .document(modifiedData.getEmail())
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                         ProfessorMark professorMark = documentSnapshot.toObject(ProfessorMark.class);
                                        String student_email = professorMark.getEmail();
                                       String [] emails = new String[]{student_email};
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
        return professorMarkList.size();
    }


    public void clear() {
        final int size = professorMarkList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                professorMarkList.remove(0);
                notifyItemRemoved(0);
            }

            notifyItemRangeRemoved(0, size);
        }
        notifyDataSetChanged();
    }
}
