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
import graduation.trocan.academicthoughts.model.NewsChat;

public class NewsChatAdapter extends RecyclerView.Adapter<NewsChatAdapter.ViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<NewsChat> newsChatList;
    String role = "";



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mUserView;
        public TextView mMessageView;
        public TextView mDateView;

        public ViewHolder(View itemView) {
            super(itemView);
            mUserView = itemView.findViewById(R.id.user_chat_item_show);
            mMessageView = itemView.findViewById(R.id.message_chat_item_show);
            mDateView = itemView.findViewById(R.id.date_chat_item_show);
        }
    }

    public NewsChatAdapter(List<NewsChat> newsChatList){
        this.newsChatList = newsChatList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_chat_item, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        NewsChat newsChat = newsChatList.get(position);
        holder.mUserView.setText(newsChat.getUser());
        holder.mDateView.setText(newsChat.getDate().toString());
        holder.mMessageView.setText(newsChat.getMessage());
    }



    @Override
    public int getItemCount() {
        return newsChatList.size();
    }

}
