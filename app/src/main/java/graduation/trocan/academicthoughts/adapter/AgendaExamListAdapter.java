package graduation.trocan.academicthoughts.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.AgendaExam;

public class AgendaExamListAdapter extends RecyclerView.Adapter<AgendaExamListAdapter.ViewHolder> {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<AgendaExam> agendaExamList;
    Context context;
    String role = "";
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");


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
        holder.mDateView.setText((DATE_FORMAT.format(agendaExam.getDate())));
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
                                String [] emails = new String[]{professorEmail};
                                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto",emails[0], null));
                                i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                                i.putExtra(Intent.EXTRA_TEXT, "Body");
                                try {
                                    finalContext.startActivity(Intent.createChooser(i, "Send mail..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(finalContext, "Nu exista client de email instalat!", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.modify_exam_date:

                                Context context = view.getContext();

                                final View dialogView = View.inflate(context, R.layout.date_time_picker, null);
                                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        DatePicker datePicker =  dialogView.findViewById(R.id.date_picker);
                                        TimePicker timePicker =  dialogView.findViewById(R.id.time_picker);

                                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                                datePicker.getMonth(),
                                                datePicker.getDayOfMonth(),
                                                timePicker.getCurrentHour(),
                                                timePicker.getCurrentMinute());

                                       Long time = calendar.getTimeInMillis();


                                        db.collection("exams").document(selectedExam.getUid())
                                                .update("date",calendar.getTime());

                                        selectedExam.setDate(calendar.getTime());
                                        alertDialog.dismiss();

                                        notifyDataSetChanged();
                                    }});
                                alertDialog.setView(dialogView);
                                alertDialog.show();

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
