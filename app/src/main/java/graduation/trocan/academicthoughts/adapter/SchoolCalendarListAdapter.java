package graduation.trocan.academicthoughts.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.SchoolCalendar;

public class SchoolCalendarListAdapter extends RecyclerView.Adapter<SchoolCalendarListAdapter.ViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<SchoolCalendar> calendarList;
    String role = "";

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;
        public TextView mTypeView;
        public TextView mHoursView;
        public TextView mGroupsView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.course_title_show);
            mTypeView = itemView.findViewById(R.id.course_type_show);
            mHoursView = itemView.findViewById(R.id.course_hours_show);
            mGroupsView = itemView.findViewById(R.id.course_groups_show);

        }


    }

    public SchoolCalendarListAdapter(List<SchoolCalendar> calendarList) {
        this.calendarList = calendarList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_item_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        SchoolCalendar calendar = calendarList.get(position);
        holder.mTitleView.setText(calendar.getTitle());
        StringBuilder type = new StringBuilder();
        type.append(calendar.getType());
        if(calendar.getWeek() != null){
            type.append( " - Saptamana " + calendar.getWeek());
        }
        holder.mTypeView.setText(type);
        String hours = "Intre orele: " + calendar.getHours();
        holder.mHoursView.setText(hours);
        StringBuilder groups = new StringBuilder();
                groups.append("Grupe: ");
                for(String group : calendar.getGroups()){
                    groups.append(" " + group);
                }

        holder.mGroupsView.setText(groups.toString());
        final SchoolCalendar selectedCourse = calendarList.get(position);

    }

    @Override
    public int getItemCount() {
        return calendarList.size();
    }



    public void clear() {
        final int size = calendarList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                calendarList.remove(0);
                notifyItemRemoved(0);
            }

            notifyItemRangeRemoved(0, size);
        }
        notifyDataSetChanged();
    }


}
