package com.example.splitwise.add_friend_or_group;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.IdTypeDoc;
import com.example.splitwise.MainActivity;
import com.example.splitwise.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class CreateGroup extends AppCompatActivity {

    EditText group_name;
    Toolbar toolbar;
    RecyclerView recyclerView;
    FriendRVAdapter adapter;
    ArrayList<User> list_users;
    TextView add_people;
    FirestoreHelper firestoreHelper ;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        firestoreHelper = new FirestoreHelper(this);


        group_name = findViewById(R.id.group_name_edit);
        add_people = findViewById(R.id.add_people_group);

        list_users = new ArrayList<>();
        adapter = new FriendRVAdapter(list_users,this,false);
        recyclerView = findViewById(R.id.users_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        add_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference friendsColRef = firestoreHelper.getFriendsColRef();
                friendsColRef.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<User> friends = new ArrayList<>();
                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                                {
                                    User temp = new User(documentSnapshot.getId(), Objects.requireNonNull(documentSnapshot.toObject(IdTypeDoc.class)).getName());
                                    friends.add(temp);
                                }
                                Intent intent = new Intent(CreateGroup.this,GetGroupUsers.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList(getString(R.string.key_friends),friends);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateGroup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle = getIntent().getExtras();
        ArrayList<User> list_to = Objects.requireNonNull(bundle).getParcelableArrayList(getString(R.string.key_users_selection));

        for (User u : Objects.requireNonNull(list_to)) {
            if (!list_users.contains(u)) {
                list_users.add(u);
            }
        }

        if (!list_to.isEmpty()) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_create_group,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_group:
                String groupName = group_name.getText().toString();

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

//                    Toast.makeText(CreateGroup.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        ArrayList<User> list_to = Objects.requireNonNull(bundle).getParcelableArrayList(getString(R.string.key_users_selection));

        for (User u : Objects.requireNonNull(list_to)) {
            if (!list_users.contains(u)) {
                list_users.add(u);
            }
        }

        if (!list_to.isEmpty()) {
            adapter.notifyDataSetChanged();
        }
    }
}
