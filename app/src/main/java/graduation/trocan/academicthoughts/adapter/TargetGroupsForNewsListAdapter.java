package graduation.trocan.academicthoughts.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.TargetCheckbox;

public class TargetGroupsForNewsListAdapter extends RecyclerView.Adapter<TargetGroupsForNewsListAdapter.ViewHolder> {

    private  OnItemCheckListener onItemClick;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<TargetCheckbox> targetGroupsList;
    String role = "";

    interface OnItemCheckListener {
        void onItemCheck(TargetCheckbox item);
        void onItemUncheck(TargetCheckbox item);
    }

    @NonNull
    private OnItemCheckListener onItemCheckListener;

    public TargetGroupsForNewsListAdapter (List<TargetCheckbox> targetGroupsList, @NonNull OnItemCheckListener onItemCheckListener) {
        this.targetGroupsList = targetGroupsList;
        this.onItemClick = onItemCheckListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder  {

        public CheckBox mCheckboxView;


        public ViewHolder(View itemView) {
            super(itemView);
            mCheckboxView = itemView.findViewById(R.id.target_checkbox);


        }
    }

    public TargetGroupsForNewsListAdapter(List<TargetCheckbox> targetGroupsList){
        this.targetGroupsList = targetGroupsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_target_group_item_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position) {
      final  TargetCheckbox target = targetGroupsList.get(position);
        holder.mCheckboxView.setText(target.getText());

        holder.mCheckboxView.setOnCheckedChangeListener(null);
        holder.mCheckboxView.setChecked(target.isSelected());

        holder.mCheckboxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set your object's last status
                target.setSelected(isChecked);
            }
        });

    }



        @Override
    public int getItemCount() {
        return targetGroupsList.size();
    }



    public void clear() {
        final int size = targetGroupsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                targetGroupsList.remove(0);
                notifyItemRemoved(0);
            }

            notifyItemRangeRemoved(0, size);
        }
        notifyDataSetChanged();
    }
}
