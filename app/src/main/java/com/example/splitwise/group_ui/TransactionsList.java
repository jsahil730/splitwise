package com.example.splitwise.group_ui;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.AmountTypeDoc;
import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.R;
import com.example.splitwise.main.RVAdapter;
import com.example.splitwise.transaction.IdAmountDocPair;
import com.example.splitwise.transaction.TransacDoc;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class TransactionsList extends AppCompatActivity {

    Bundle bun;
    String groupId;
    String transactionTag;
    List<IdAmountDocPair> list_items;
    FirestoreHelper firestoreHelper ;
    RVAdapter adapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        list_items = new ArrayList<>();
        adapter = new RVAdapter(true,this,list_items,3);
        firestoreHelper = new FirestoreHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transac_list);
        bun = getIntent().getExtras();
        groupId = bun.getString(getString(R.string.key_groupId));
        transactionTag = bun.getString(getString(R.string.key_tag));
        RecyclerView rvitems = findViewById(R.id.rvitems);
        rvitems.setLayoutManager(new LinearLayoutManager(this));
        rvitems.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        final CollectionReference collectionReference = firestoreHelper.getGroupsRef().document(groupId)
                .collection(getString(R.string.groupTransactionCollection))
                .document(transactionTag)
                .collection(getString(R.string.TransactionItems));

        collectionReference.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                list_items.clear();
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    final TransacDoc temp = documentSnapshot.toObject(TransacDoc.class);
                    collectionReference.document(documentSnapshot.getId()).collection(getString(R.string.AllExchanges))
                            .document(firestoreHelper.getUserId()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot1) {

                                    if(documentSnapshot1.exists())
                                    {
                                        AmountTypeDoc t2 = new AmountTypeDoc(temp.getDescription(),
                                                documentSnapshot1.toObject(AmountTypeDoc.class).getAmount());
                                        IdAmountDocPair t1 = new IdAmountDocPair("hi",t2);
                                        list_items.add(t1);
                                    }
                                    else
                                    {
                                        AmountTypeDoc t2 = new AmountTypeDoc(temp.getDescription(),
                                               0);
                                        IdAmountDocPair t1 = new IdAmountDocPair(null,t2);
                                        list_items.add(t1);
                                    }

                                    adapter.notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("live error", e.getMessage());
                        }
                    });
                }
            }
        });
    }
}
