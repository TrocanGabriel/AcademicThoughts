package graduation.trocan.academicthoughts.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graduation.trocan.academicthoughts.NewsChatActivity;
import graduation.trocan.academicthoughts.R;
import graduation.trocan.academicthoughts.adapter.NewsListAdapter;
import graduation.trocan.academicthoughts.adapter.TargetGroupsForNewsListAdapter;
import graduation.trocan.academicthoughts.model.News;
import graduation.trocan.academicthoughts.model.Professor;
import graduation.trocan.academicthoughts.model.TargetCheckbox;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class NewsFragment extends Fragment {

    private  List<News> newsList = new ArrayList<>();
    private List<String> targets = new ArrayList<>();
    private  RecyclerView recyclerView;
    private RecyclerView targetRecyclerView;
    private  String role;
    private  String studentGroup;
    private  NewsListAdapter mAdapter;
    private TargetGroupsForNewsListAdapter mAdapterTargets;
    Context context = getActivity();
    public static final String TAG = "NewsFragment";
     FirebaseFirestore db = FirebaseFirestore.getInstance();
     FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private List<TargetCheckbox> currentSelectedItems = new ArrayList<>();

    public static final String PREFS_NAME = "NEWS_FRAGMENT";
    public static final String FAVORITES = "Students_list";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
       recyclerView = view.findViewById(R.id.news_recycler_view);
       retrieveNews(getActivity());

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String userRole = sharedPref.getString("role","");
        Log.d(TAG, "USERROLENEWS " + userRole);


        final FloatingActionButton floatingActionButton = view.findViewById(R.id.add_news_button);
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(userRole.equals("student")) {
            floatingActionButton.setVisibility(View.GONE);
        }
//        db.collection("roles").document(currentUser.getEmail())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                Context context = getActivity();
//                              if(document.getString("role").equals("student")) {
//                                  saveFavorites(context, "student");
//                              }
//                              else {
//                                  saveFavorites(context, "professor");
//
//                              }
//                                Log.d(TAG, document.getString("role") + "role " );
//                            } else {
//                                Log.d(TAG, "No such document");
//                            }
//                        } else {
//                            Log.d(TAG, "get failed with ", task.getException());
//                        }
//                    }
//                });
            if(floatingActionButton.getVisibility() != View.GONE) {
                Context contextRole = getActivity();
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                         context = view.getContext();
                        LayoutInflater layoutInflater = LayoutInflater.from(context);
                        final View promptView = layoutInflater.inflate(R.layout.add_news_prompt, null);
                        targetRecyclerView = promptView.findViewById(R.id.news_target_recycler_view);
                        getTargetList();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setView(promptView);
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                EditText addedNews = promptView.findViewById(R.id.added_news);
                                                String newData = addedNews.getText().toString();
                                                ArrayList<String> targetGroups = new ArrayList<>();
                                                for(int i = 0; i< currentSelectedItems.size(); i++){
                                                    TargetCheckbox checkBox = currentSelectedItems.get(i);
                                                    Log.d(TAG, "NEWS FRAG ISCHECK " +  checkBox.isSelected() );

                                                    if(checkBox.isSelected()){
                                                            Log.d(TAG, "NEWS FRAG TEXT " +  checkBox.getText());
                                                            targetGroups.add(checkBox.getText());
                                                        }
                                                }
                                                if (!newData.equals("")) {
                                                    Date date = new Date();
                                                    DocumentReference newsRef = db.collection("news").document();
                                                    Log.d(TAG, "NEWS FRAG TARGET" + targetGroups + "SELECTED " + currentSelectedItems );
                                                    Map<String, Object> dataNew = new HashMap<>();
                                                    dataNew.put("date", date);
                                                    dataNew.put("author", currentUser.getEmail());
                                                    dataNew.put("text", newData.trim());
                                                    dataNew.put("target", Arrays.asList(targetGroups.toArray()));
                                                    dataNew.put("uid", newsRef.getId());
                                                    newsRef.set(dataNew);

                                                    mAdapter.clear();
                                                    retrieveNews(context);
                                                }
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
            }
            else {
                floatingActionButton.setOnClickListener(null);
            }
   return view;
    }

    public  void retrieveNews(final Context context){

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        getUserGroup(context );

        db.collection("news")
        .orderBy("date", Query.Direction.DESCENDING)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if( task.isSuccessful()){
                    newsList = new ArrayList<>();

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    final String userRole = sharedPref.getString("role","");
                   String studentGroup2 = sharedPref.getString("studentGroup","");
                    Log.d(TAG, "NEWS ROLE studentGroup : " + studentGroup2);
                    Log.d(TAG, "NEWS ROLE role : " + userRole);


                    for (QueryDocumentSnapshot document : task.getResult()) {
                        News newAdded = document.toObject(News.class);

                        if(newAdded.getAuthor().equals(currentUser.getEmail())
                                && userRole.equals("professor")) {
                        newAdded.setUid(document.getId());
                        Log.d(TAG, "UID : " + newAdded.getUid());
                        newsList.add(newAdded);

                        } else if(userRole.equals("student") && newAdded.getTarget().contains(studentGroup2)) {
                            newAdded.setUid(document.getId());
                            Log.d(TAG, "UID : " + newAdded.getUid());
                            newsList.add(newAdded);
                        }
                    }

                        mAdapter = new NewsListAdapter(newsList, context, new NewsListAdapter.CustomClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Intent intent = new Intent(context, NewsChatActivity.class);
                                intent.putExtra("newsUid",newsList.get(position).getUid());
                                context.startActivity(intent);                            }
                        });
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(mAdapter);
                } else {
                    Log.d(TAG, "Error on retrieving news list");
                }
            }
        });

    }

    public void getTargetList(){
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("professors")
                .document(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                targets = new ArrayList<>();
                                Map<String, Object> map = document.getData();
                                Professor prof = document.toObject(Professor.class);
                                Log.d(TAG, " TARGET VALUE GROUPS " + map);
                                Log.d(TAG, " TARGET VALUE GROUPS prof " + prof);


                                targets.addAll(prof.getGroups());
                                currentSelectedItems.clear();
                                for(String target: targets){
                                    TargetCheckbox check = new TargetCheckbox(target, false);
                                    Log.d(TAG, " TARGET VALUE GROUPS ADD TARGET TO CHECK " + check.isSelected());

                                    currentSelectedItems.add(check);
                                }

                                mAdapterTargets = new TargetGroupsForNewsListAdapter(currentSelectedItems);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                targetRecyclerView.setLayoutManager(mLayoutManager);
                                targetRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                targetRecyclerView.setAdapter(mAdapterTargets);
                            } else {
                                Log.d(TAG, " TARGET VALUE GROUPS DOCUMENT DOESNT EXISTS" );

                            }

                        } else {
                            Log.d(TAG, " TARGET VALUE GROUPS task fail" );

                        }
                    }
                });

    }

    public void saveFavorites(Context context,String role) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(role);

        editor.putString(FAVORITES, jsonFavorites);

        editor.apply();
    }

//    public static String getFavorites(Context context) {
//        SharedPreferences settings;
//       String favorites;
//
//        settings =PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//
//        if (settings.contains(FAVORITES)) {
//            String jsonFavorites = settings.getString(FAVORITES, null);
//            Gson gson = new Gson();
//            favorites = gson.fromJson(jsonFavorites,
//                    String.class);
//
//
//        } else
//            return null;
//        return  favorites;
//    }

    private  void getUserGroup(final Context context){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db.collection("students").document(currentUser.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                studentGroup = documentSnapshot.getString("group");
                Log.d(TAG, "STUDENT GET ROLE " + studentGroup);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("studentGroup", studentGroup);
                editor.apply();
            }
        });
    }




}
