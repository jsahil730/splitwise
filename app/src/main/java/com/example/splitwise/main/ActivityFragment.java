package com.example.splitwise.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.ActivityTypeDoc;
import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ActivityTypeDoc> list_items;
    private FirestoreHelper firestoreHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_activity, container, false);
        firestoreHelper = new FirestoreHelper(getActivity());
        recyclerView = root.findViewById(R.id.activities);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        list_items = new ArrayList<>();

        adapter = new ActivityAdapter(getActivity(), list_items);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        final CollectionReference collectionReference = firestoreHelper.getUserRef()
                .collection(getString(R.string.Activities));

        collectionReference.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                list_items.clear();
                collectionReference.orderBy("date", Query.Direction.DESCENDING).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                                {
                                    ActivityTypeDoc temp = documentSnapshot.toObject(ActivityTypeDoc.class);
                                    list_items.add(temp);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("activity error", e.getMessage());
                    }
                });
            }
        });


    }
}