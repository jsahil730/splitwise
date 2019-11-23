package com.example.splitwise.transaction;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.IdTypeDoc;
import com.example.splitwise.R;
import com.example.splitwise.add_friend_or_group.CreateGroup;
import com.example.splitwise.add_friend_or_group.FriendRVAdapter;
import com.example.splitwise.add_friend_or_group.GetGroupUsers;
import com.example.splitwise.add_friend_or_group.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AddTransaction extends AppCompatActivity {

    EditText description;
    EditText amount;
    Spinner tag;
    Toolbar toolbar;
    RecyclerView recyclerView;
    FriendRVAdapter adapter;
    ArrayList<User> list_users;
    TextView add_people;
    FirestoreHelper firestoreHelper ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        toolbar = findViewById(R.id.toolbar);
        firestoreHelper = new FirestoreHelper(this);

        toolbar.setTitle("Add Expense");

        description = findViewById(R.id.tran_desc);
        amount = findViewById(R.id.enter_amount);
        add_people = findViewById(R.id.add_users_transac);
        tag = findViewById(R.id.tag_spinner);

        list_users = new ArrayList<>();
        adapter = new FriendRVAdapter(list_users,this,false);
        recyclerView = findViewById(R.id.users_transac_list);
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
                                Intent intent = new Intent(AddTransaction.this, GetGroupUsers.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList(getString(R.string.key_friends),friends);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddTransaction.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_new_transac,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_button:
                return true;
            default:
                return true;
        }
    }
}
