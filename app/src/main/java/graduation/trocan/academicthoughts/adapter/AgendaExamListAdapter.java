package graduation.trocan.academicthoughts.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.AgendaExam;

public class AgendaExamListAdapter extends RecyclerView.Adapter<AgendaExamListAdapter.ViewHolder> {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<AgendaExam> agendaExamList;
    String role = "";


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;
        public TextView mDateView;
        public ImageButton imageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.exam_title_show);
            mDateView = itemView.findViewById(R.id.exam_date_show);
            imageButton = itemView.findViewById(R.id.exam_menu_button);
        }
    }

    public AgendaExamListAdapter(List<AgendaExam> agendaExamList) {
        this.agendaExamList = agendaExamList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.agenda_exam_item_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AgendaExam agendaExam = agendaExamList.get(position);
        getRole();
        if(role.equals("student")){
            String title = agendaExam.getCourse() + ": " + agendaExam.getProfessor();
            holder.mTitleView.setText(title);

        } else {
            holder.mTitleView.setText(agendaExam.getCourse());
        }
        holder.mDateView.setText((agendaExam.getDate().toString()));
        final AgendaExam selectedExam = agendaExamList.get(position);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PopupMenu popup = new PopupMenu(view.getContext(), view);
                FirebaseUser currentUser = mAuth.getCurrentUser();

                final MenuInflater inflater = popup.getMenuInflater();
                getRole();
                if(role.equals("student"))
                    inflater.inflate(R.menu.agenda_exam_menu, popup.getMenu());
                else
                    inflater.inflate(R.menu.prof_exam_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String professorEmail = selectedExam.getProfessor();
                        switch (item.getItemId()) {

                            case R.id.email_professor:

                                final Context finalContext;
                                finalContext = view.getContext();
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{professorEmail});
                                i.putExtra(Intent.EXTRA_SUBJECT, "Insert subject");
                                i.putExtra(Intent.EXTRA_TEXT   , " S");
                                try {
                                    finalContext.startActivity(Intent.createChooser(i, "Send mail..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(finalContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                                break;

                        }



                        return false;
                    }

                });
                popup.show();

            }
        });

    }

    private void getRole(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("roles").document(currentUser.getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                role= documentSnapshot.getString("role");
                Log.d("AgendaLists", role);

            }
        });
    }

    @Override
    public int getItemCount() {
        return agendaExamList.size();
    }



    public void clear() {
        final int size = agendaExamList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                agendaExamList.remove(0);
                notifyItemRemoved(0);
            }

            notifyItemRangeRemoved(0, size);
        }
        notifyDataSetChanged();
    }


}
