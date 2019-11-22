package com.example.splitwise.ui.add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.IdTypeDoc;
import com.example.splitwise.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetGroupUsers extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    Button finish_selection;
    Toolbar toolbar;
    ArrayList<User> list_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_group_users);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Users");

        finish_selection = findViewById(R.id.finish_user_selection);

        list_users = getIntent().getExtras().getParcelableArrayList("friends_list");

        recyclerView = findViewById(R.id.friends_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FriendRVAdapter(list_users,this,true);
        recyclerView.setAdapter(adapter);

        finish_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_users = ((FriendRVAdapter) adapter).list_selected_users();

                FirestoreHelper firestoreHelper = new FirestoreHelper(GetGroupUsers.this);
                final String uid = firestoreHelper.getUserId();
                firestoreHelper.getUserRef().get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                String uname = documentSnapshot.toObject(IdTypeDoc.class).getName();
                                list_users.add(new User(uid,uname));

                                for (User u : list_users) {
                                    System.out.println(u.getUname());
                                }

                                Intent intent = new Intent(GetGroupUsers.this,CreateGroup.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("user_list",list_users);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GetGroupUsers.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
