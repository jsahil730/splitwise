package com.example.splitwise.group_ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.AmountTypeDoc;
import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.R;
import com.example.splitwise.main.RVAdapter;
import com.example.splitwise.transaction.IdAmountDocPair;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class BalancesGroup extends AppCompatActivity {

    RecyclerView rvitems ;
    RVAdapter adapter;
    List<IdAmountDocPair> list_items;
    String groupId;
    FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balances_group);
        rvitems = findViewById(R.id.balanceRv);
        list_items = new ArrayList<>();
        rvitems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RVAdapter(true,this,list_items,3);
        rvitems.setAdapter(adapter);
        firestoreHelper = new FirestoreHelper(this);
        groupId = getIntent().getStringExtra(getString(R.string.key_groupId));
    }

    @Override
    protected void onStart() {
        super.onStart();

        CollectionReference groupRef = firestoreHelper.getGroupsRef().document(groupId)
                .collection(getString(R.string.GroupUsers))
                .document(firestoreHelper.getUserId())
                .collection(getString(R.string.borrowersCollection));

        groupRef.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                {
                    IdAmountDocPair temp= new IdAmountDocPair(documentSnapshot.getId(), documentSnapshot.toObject(AmountTypeDoc.class));

                    list_items.add(temp);
                }

                adapter.notifyDataSetChanged();

            }
        });

    }
}
