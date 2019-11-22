package com.example.splitwise.ui.add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.MainActivity;
import com.example.splitwise.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateGroup extends AppCompatActivity {

    EditText group_name;
    Button finish_button;
    Toolbar toolbar;
    RecyclerView recyclerView;
    FriendRVAdapter adapter;
    ArrayList<User> list_users;
    TextView add_people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        finish_button = findViewById(R.id.save_group);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Create Group");

        group_name = findViewById(R.id.group_name_edit);
        add_people = findViewById(R.id.add_people_group);

        list_users = new ArrayList<User>();
        adapter = new FriendRVAdapter(list_users,this,false);
        recyclerView = findViewById(R.id.users_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        final FirestoreHelper firestoreHelper = new FirestoreHelper(this);

        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String groupName = group_name.getText().toString();

                if (groupName.isEmpty()) {
                    Toast.makeText(CreateGroup.this,"Group name must not be empty!",Toast.LENGTH_LONG).show();
                }
                else if (list_users.size() < 3) {
                    Toast.makeText(CreateGroup.this, "Group size must not be less than 3", Toast.LENGTH_LONG).show();
                }
                else {
                    ArrayList<String> uids = new ArrayList<>();
                    for (User u : list_users) {
                        uids.add(u.getUid());
                    }
                    firestoreHelper.create_group(uids,groupName);

                    Intent intent = new Intent(CreateGroup.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    Toast.makeText(CreateGroup.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
            }
        });

        add_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateGroup.this,GetGroupUsers.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<User> list_to = getIntent().getExtras().getParcelableArrayList("user_list");

        for (User u : list_to) {
            if (list_users.contains(u)) {
            }
            else {
                list_users.add(u);
            }
        }
        if (!list_to.isEmpty()) {
            adapter.notifyDataSetChanged();
        }
    }
}
