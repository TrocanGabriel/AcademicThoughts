package graduation.trocan.academicthoughts.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.adapter.NewsListAdapter;
import graduation.trocan.academicthoughts.model.News;


public class NewsFragment extends Fragment {

    private List<News> newsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NewsListAdapter mAdapter;
    Context context = getActivity();
    public static final String TAG = "NewsFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static final String PREFS_NAME = "NEWS_FRAGMENT";
    public static final String FAVORITES = "Students_list";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_news, container, false);
       recyclerView = view.findViewById(R.id.news_recycler_view);
        retrieveNews();

        final FloatingActionButton floatingActionButton = view.findViewById(R.id.add_news_button);
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("roles").document(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Context context = getActivity();
                              if(document.getString("role").equals("student")) {
                                  floatingActionButton.setVisibility(View.GONE);
                                  saveFavorites(context, "student");
                              }
                              else {
                                  saveFavorites(context, "professor");

                              }
                                Log.d(TAG, document.getString("role") + "role " );
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            if(floatingActionButton.getVisibility() != View.GONE) {


                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        context = view.getContext();
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        final View promptView = layoutInflater.inflate(R.layout.add_news_prompt, null);


                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setView(promptView);
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                EditText addedNews = promptView.findViewById(R.id.added_news);
                                                String newData = addedNews.getText().toString();
                                                if (!newData.equals("")) {
                                                    Date date = new Date();
                                                    News news = new News(date, newData);
                                                    DocumentReference newsRef = db.collection("news").document();
                                                    news.setUid(newsRef.getId());
                                                    news.setAuthor(currentUser.getEmail());
                                                    newsRef.set(news);
                                                    mAdapter.clear();
                                                    retrieveNews();
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

                    }
                });

            }
            else {
                floatingActionButton.setOnClickListener(null);
            }


   return view;
    }





    private void retrieveNews(){

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("news")

        .orderBy("date", Query.Direction.DESCENDING)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if( task.isSuccessful()){
                    newsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        News newAdded = document.toObject(News.class);
                        Context context = getActivity();
                        String role = getFavorites(context);
                        if(newAdded.getAuthor().equals(currentUser.getEmail())
                             //   && role.equals("professor")
                                ){
                        newAdded.setUid(document.getId());
                        Log.d(TAG, "UID : " + newAdded.getUid());
                        newsList.add(newAdded);}
//                        else if(role.equals("student"))  {
//                            newAdded.setUid(document.getId());
//                            Log.d(TAG, "UID : " + newAdded.getUid());
//                            newsList.add(newAdded);
//                        }
                    }
                        mAdapter = new NewsListAdapter(newsList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                } else {

                    Log.d(TAG, "Error on retrieving news list");

                }
            }
        });

    }

    public void saveFavorites(Context context,String role) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(role);

        editor.putString(FAVORITES, jsonFavorites);

        editor.apply();
    }

    public String getFavorites(Context context) {
        SharedPreferences settings;
       String favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            favorites = gson.fromJson(jsonFavorites,
                    String.class);


        } else
            return null;
        return  favorites;
    }


}
