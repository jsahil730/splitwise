package com.example.splitwise.transaction;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.IdTypeDoc;
import com.example.splitwise.R;
import com.example.splitwise.add_friend_or_group.FriendRVAdapter;
import com.example.splitwise.add_friend_or_group.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AddTransaction extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText description;
    EditText amount;
    Spinner tag;
    RecyclerView recyclerView;
    FriendRVAdapter adapter;
    ArrayList<User> list_users;
    String tag_tr = "General";

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tag_tr = getResources().getStringArray(R.array.tag_choices)[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    TextView add_people;
    FirestoreHelper firestoreHelper ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        firestoreHelper = new FirestoreHelper(this);

        description = findViewById(R.id.tran_desc);
        amount = findViewById(R.id.enter_amount);
        add_people = findViewById(R.id.add_users_transac);
        tag = findViewById(R.id.tag_spinner);

        amount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});

        ArrayAdapter<CharSequence> tag_adapter = ArrayAdapter.createFromResource(this,R.array.tag_choices,android.R.layout.simple_spinner_item);
        tag_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tag.setAdapter(tag_adapter);
        tag.setOnItemSelectedListener(this);

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
                                Intent intent = new Intent(AddTransaction.this, SelectTransactionUsers.class);
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
                String amt_string = amount.getText().toString();
                String desc = description.getText().toString();                
                if (amt_string.isEmpty()) {
                    Toast.makeText(this, "Amount can't be empty", Toast.LENGTH_SHORT).show();
                }
                else if (desc.isEmpty()) {
                    Toast.makeText(this, "Description can't be empty", Toast.LENGTH_SHORT).show();
                }
                else if (list_users.size() < 2) {
                    Toast.makeText(this, "Transactors can't be less than 2", Toast.LENGTH_SHORT).show();
                }
                else {
                    double amt = Double.parseDouble(amt_string);
                    amt *= 100;
                    amt = (double) Math.round(amt);
                    amt /= 100;

                    Intent intent = new Intent(AddTransaction.this, GetStakesTransaction.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.key_transac_desc), desc);
                    bundle.putDouble(getString(R.string.key_transac_amt), amt);
                    bundle.putParcelableArrayList(getString(R.string.key_transac_user_list), list_users);
                    bundle.putString(getString(R.string.key_transac_tag), tag_tr);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return true;
            default:
                return true;
        }
    }
}
