package graduation.trocan.academicthoughts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import graduation.trocan.academicthoughts.adapter.NewsChatAdapter;
import graduation.trocan.academicthoughts.model.NewsChat;

public class NewsChatActivity extends AppCompatActivity {

    private String uid;
    private List<NewsChat> newsChatList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NewsChatAdapter mAdapter;
    Context context = getApplication();
    public static final String TAG = "NewsChatActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_chat);
        Intent intent = getIntent();
         uid = intent.getStringExtra("newsUid");

         recyclerView = findViewById(R.id.news_chat_recycler_view);
        Button button = findViewById(R.id.button_chat_message);
        retrieveChat();
        realTimeUpdateListener();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });




    }


    private void retrieveChat(){
        Log.d(TAG,"CHAT retrieval done UID " + uid);
        Log.d(TAG,"CHAT retrieval done RV " + recyclerView.toString());

        db.collection("news").document(uid)
                .collection("chat")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                      newsChatList = new ArrayList<>();
                      for(QueryDocumentSnapshot document : task.getResult()){
                          NewsChat newsChat = document.toObject(NewsChat.class);
                          Log.d(TAG,"CHAT retrieval done " + newsChat.getUser());

                          newsChatList.add(newsChat);

                      }
                        mAdapter = new NewsChatAdapter(newsChatList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                    }
                        else {
                        Log.d(TAG,"CHAT retrieval failed");
                    }
                    }
                });

    }

    private void sendMessage(){
        FirebaseUser user = mAuth.getCurrentUser();
        Date date = new Date();
        EditText editText = findViewById(R.id.text_news_chat);
        String message = editText.getText().toString();
        NewsChat newDocument = new NewsChat(user.getEmail().toString(),message,date);
        db.collection("news")
                .document(uid)
                .collection("chat")
                .add(newDocument);
        editText.setText("");

    }

   private void realTimeUpdateListener() {
        db.collection("news").document(uid)
                .collection("chat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.w(TAG,"Listener failed", e);
                            return;
                        }
                        newsChatList = new ArrayList<>();

                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            NewsChat newsChat = doc.toObject(NewsChat.class);
                            newsChatList.add(newsChat);
                        }

                        mAdapter = new NewsChatAdapter(newsChatList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                    }
                });
   }
}
