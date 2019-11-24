package com.example.splitwise.friend_ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.AmountTypeDoc;
import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.MainActivity;
import com.example.splitwise.R;
import com.example.splitwise.main.RVAdapter;
import com.example.splitwise.transaction.IdAmountDocPair;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class FriendOpen extends AppCompatActivity {

    TextView friendName;
    TextView amountOwed;
    RecyclerView recyclerView;
    RVAdapter adapter;
    IdAmountDocPair pair;
    FirestoreHelper firestoreHelper;
    List<IdAmountDocPair> commonGroups;


    @SuppressLint("DefaultLocale")
    public String get_amount_string(double amount) {
        String s;
        if (amount == 0) {
            s = "You are all settled up here";
        }
        else if (amount > 0) {
            s = String.format("You owe %.2f",amount);
        }
        else {
            s = String.format("You are owed %.2f",amount);
        }
        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_open);

        commonGroups= new ArrayList<>();
        firestoreHelper= new FirestoreHelper(this);

        friendName= findViewById(R.id.friendNameinActivity);
        amountOwed= findViewById(R.id.amountOwedToFriend);
        recyclerView= findViewById(R.id.commonGroupList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RVAdapter(false, this, commonGroups, 2);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle = getIntent().getExtras();
        pair = new IdAmountDocPair(Objects.requireNonNull(bundle).getString(getString(R.string.key_group_id)),bundle.getString(getString(R.string.key_group_name))
                ,bundle.getDouble(getString(R.string.key_group_amount)));


        friendName.setText(pair.getName());
        amountOwed.setText(get_amount_string(pair.getAmount()));

        final CollectionReference groupsRef= firestoreHelper.getGroupsRef();
        DocumentReference myRef= firestoreHelper.getUserRef();
        myRef.collection(getString(R.string.user_groups)).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot group2 : queryDocumentSnapshots)
                        {
                            final DocumentSnapshot group= group2;

                            final DocumentReference friendsRef=groupsRef.document(group.getId())
                                    .collection(getString(R.string.GroupUsers)).document(pair.getId());

                            friendsRef.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists())
                                            {
                                                DocumentReference friendsRefInMyRef= groupsRef.document(group.getId())
                                                        .collection(getString(R.string.GroupUsers))
                                                        .document(firestoreHelper.getUserId())
                                                        .collection(getString(R.string.borrowersCollection))
                                                        .document(documentSnapshot.getId());

                                                friendsRefInMyRef.get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if(documentSnapshot.exists())
                                                                {
                                                                    IdAmountDocPair temp= new IdAmountDocPair(group.getId(),
                                                                            group.toObject(AmountTypeDoc.class).getName(),
                                                                            documentSnapshot.toObject(AmountTypeDoc.class).getAmount());

                                                                    commonGroups.add(temp);
                                                                    adapter.notifyDataSetChanged();

                                                                }
                                                                else
                                                                {
                                                                    IdAmountDocPair temp= new IdAmountDocPair(group.getId(),
                                                                            group.toObject(AmountTypeDoc.class).getName(),
                                                                    0);

                                                                    commonGroups.add(temp);
                                                                    adapter.notifyDataSetChanged();

                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.i("", e.getMessage());
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("", e.getMessage());
                                        }
                                    });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("", e.getMessage());
                    }
                });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle bundle = getIntent().getExtras();
        pair = new IdAmountDocPair(Objects.requireNonNull(bundle).getString(getString(R.string.key_group_id)),bundle.getString(getString(R.string.key_group_name))
                ,bundle.getDouble(getString(R.string.key_group_amount)));

        friendName.setText(pair.getName());
        amountOwed.setText(get_amount_string(pair.getAmount()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_friend,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settle_up:

                Calendar today= Calendar.getInstance();
                firestoreHelper.settle_non_group(pair.getId(), today.getTime());

                Intent intent= new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
