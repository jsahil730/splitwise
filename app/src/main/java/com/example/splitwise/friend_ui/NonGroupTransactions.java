package com.example.splitwise.friend_ui;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NonGroupTransactions extends AppCompatActivity {

    RecyclerView recyclerView;
    FirestoreHelper firestoreHelper;
    RVAdapter adapter;
    List<IdAmountDocPair> list_items;
    String friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_group_transactions);

        recyclerView= findViewById(R.id.nonGroupTransactionRV);
        firestoreHelper= new FirestoreHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list_items = new ArrayList<>();
        adapter= new RVAdapter(true,this,list_items,3);

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        friendId= getIntent().getStringExtra(getString(R.string.key_friends));

        String[] tags=  getResources().getStringArray(R.array.tag_choices);
        for(String tag: tags)
        {
            final CollectionReference transactions= firestoreHelper.getUserRef()
                    .collection(getString(R.string.nonGroupTransactionCollection))
                    .document(tag).collection(getString(R.string.TransactionItems));

            transactions.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                            {
                                String transactionID=documentSnapshot.getId();
                                final String desc= documentSnapshot.toObject(TransacDoc.class).getDescription();

                                final DocumentReference friendsRef= transactions.document(transactionID)
                                        .collection(getString(R.string.AllExchanges))
                                        .document(friendId);

                                friendsRef.get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if(documentSnapshot.exists())
                                                {
                                                    DocumentReference myRef= friendsRef.collection(getString(R.string.userSpecificExchanges))
                                                            .document(firestoreHelper.getUserId());

                                                    myRef.get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if(documentSnapshot.exists())
                                                                    {
                                                                        double Amount=-1*documentSnapshot.toObject(AmountTypeDoc.class)
                                                                                .getAmount();
                                                                        IdAmountDocPair temp=new IdAmountDocPair(documentSnapshot.getId()
                                                                                ,new AmountTypeDoc(desc,Amount));

                                                                        list_items.add(temp);
                                                                        adapter.notifyDataSetChanged();

                                                                    }
                                                                }
                                                            });
                                                }
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
}
