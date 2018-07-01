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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.model.News;

/**
 * Created by Gabi on 29/04/2018.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<News> newsList;
     String role = "";
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public interface CustomClickListener {
       void onItemClick(View v,int position);
    }
    CustomClickListener listener;
    Context mContext;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public TextView mDateView;
        public ImageButton imageButton;
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.news_text_show);
            mDateView = itemView.findViewById(R.id.news_date_show);
            imageButton = itemView.findViewById(R.id.menu_button);

            this.itemView = itemView;
        }


    }

    public NewsListAdapter(List<News> newsList){
        this.newsList = newsList;
    }
    public NewsListAdapter(List<News> newsList, Context mContext, CustomClickListener   listener){
        this.newsList = newsList;
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item_layout, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        News news = newsList.get(position);
        holder.mTextView.setText(news.getText());
        holder.mDateView.setText(DATE_FORMAT.format(news.getDate()));
        final News modifiedNews = newsList.get(position);


        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View itemView) {
                final PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
                FirebaseUser currentUser = mAuth.getCurrentUser();

                final MenuInflater inflater = popup.getMenuInflater();
                db.collection("roles").document(currentUser.getEmail())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        role= documentSnapshot.getString("role");
                        Log.d("NewsListAd", role);

                    }
                });

                if(role.equals("student") || !modifiedNews.getAuthor().equals(currentUser.getEmail()))
                inflater.inflate(R.menu.news_menu_stud, popup.getMenu());
                else
                    inflater.inflate(R.menu.news_menu, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String currentDoc =  modifiedNews.getUid();
                        switch (item.getItemId()) {
                            case R.id.delete_news:
                                db.collection("news").document(currentDoc)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("NEWS ADAPTER", "DocumentSnapshot successfully deleted! " + currentDoc);
                                            newsList.remove(modifiedNews);
                                                notifyDataSetChanged();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("NEWS ADAPTER", "Error deleting document", e);
                                            }
                                        });


                                break;
                            case R.id.edit_news:

                                Context context = itemView.getContext();
                                LayoutInflater layoutInflater = LayoutInflater.from(context);
                                final View promptView = layoutInflater.inflate(R.layout.edit_news_prompt, null);
                                final EditText editingNews = promptView.findViewById(R.id.editing_news);
                                editingNews.setText(modifiedNews.getText());
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setView(promptView);
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                        String newData = editingNews.getText().toString();
                                                        if (!newData.equals("")) {
                                                            DocumentReference newsRef = db.collection("news").document(modifiedNews.getUid());
                                                            newsRef.update("text",newData);
                                                            newsList.get(newsList.indexOf(modifiedNews)).setText(newData);
                                                            notifyDataSetChanged();

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

                            case R.id.email_news_author:

                                final Context finalContext;
                                finalContext = itemView.getContext();
                                db.collection("news").document(currentDoc)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        News news = documentSnapshot.toObject(News.class);
                                        String author_email = news.getAuthor();
                                        String [] emails = new String[]{author_email};
                                        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                "mailto",emails[0], null));
                                        i.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                                        i.putExtra(Intent.EXTRA_TEXT, "Body");
                                        try {
                                            itemView.getContext().startActivity(Intent.createChooser(i, "Send mail..."));
                                        } catch (android.content.ActivityNotFoundException ex) {
                                            Toast.makeText(finalContext, "Nu exista client de email instalat!", Toast.LENGTH_SHORT).show();
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
        return newsList.size();
    }



    public void clear() {
        final int size = newsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                newsList.remove(0);
                notifyItemRemoved(0);
            }

            notifyItemRangeRemoved(0, size);
        }
        notifyDataSetChanged();
    }

}
