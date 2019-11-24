package com.example.splitwise.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.AmountTypeDoc;
import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.IdTypeDoc;
import com.example.splitwise.R;
import com.example.splitwise.TwoAmountDoc;
import com.example.splitwise.transaction.IdAmountDocPair;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<IdAmountDocPair> list_items;
    private int index;
    private FirestoreHelper firestoreHelper;

    PlaceholderFragment(int index)
    {
        this.index=index;
    }

    @Override
    public View onCreateView(

            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        firestoreHelper= new FirestoreHelper(Objects.requireNonNull(getActivity()));

        recyclerView = root.findViewById(R.id.friends_or_groups);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        list_items = new ArrayList<>();

        adapter = new RVAdapter(false,getActivity(),list_items);
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        CollectionReference collectionReference=null;
        if(index==1) {
            // fragment is friends fragment
            // list will be friends list
           collectionReference = firestoreHelper.getFriendsColRef();
        }
        else if(index==2){

            collectionReference = firestoreHelper.getUserRef().collection(getString(R.string.user_groups));
        }
        if(collectionReference!=null) {


            collectionReference.addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(final @Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        list_items.clear();
                        double total_balance=0;
                        for (DocumentSnapshot documentSnapshot2 : Objects.requireNonNull(queryDocumentSnapshots)) {
                            if(index==2){AmountTypeDoc temp = documentSnapshot2.toObject(AmountTypeDoc.class);
                            total_balance=total_balance+temp.getAmount();}
                            else{
                                TwoAmountDoc temp = documentSnapshot2.toObject(TwoAmountDoc.class);
                                total_balance=total_balance+temp.getAmount();
                            }
                        }
                        AmountTypeDoc myself = new AmountTypeDoc("User",total_balance);
                        IdAmountDocPair to_myself = new IdAmountDocPair(firestoreHelper.getUserId(),myself);
                        list_items.add(to_myself);
                        for (DocumentSnapshot documentSnapshot2 : Objects.requireNonNull(queryDocumentSnapshots)) {
                            if(index==2){AmountTypeDoc temp = documentSnapshot2.toObject(AmountTypeDoc.class);
                                IdAmountDocPair t1 = new IdAmountDocPair(documentSnapshot2.getId(),temp);
                            list_items.add(t1);}
                            else{
                                TwoAmountDoc temp  = documentSnapshot2.toObject(TwoAmountDoc.class);

                                AmountTypeDoc temp2 = new AmountTypeDoc(temp.getName(),temp.getAmount());
                                IdAmountDocPair t1 = new IdAmountDocPair(documentSnapshot2.getId(),temp2);
                                list_items.add(t1);
                            }
                        }
                        adapter.notifyDataSetChanged();


                    }
                }
            });
        }
        }

    }
