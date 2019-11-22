package com.example.splitwise.ui.main;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.AmountTypeDoc;
import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.IdTypeDoc;
import com.example.splitwise.MainActivity;
import com.example.splitwise.R;
import com.example.splitwise.ui.add.User;
import com.google.api.SystemParameterOrBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<AmountTypeDoc> list_items;
    private int index;
    FirestoreHelper firestoreHelper;

//    public static PlaceholderFragment newInstance(int index) {
//        PlaceholderFragment fragment = new PlaceholderFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt(ARG_SECTION_NUMBER, index);
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    PlaceholderFragment(int index)
    {
        this.index=index;
    }

    @Override
    public View onCreateView(

            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        firestoreHelper= new FirestoreHelper(getActivity());

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
        Resources res = getActivity().getResources();
        if(index==1) {
            // fragment is friends fragment
            // list will be friends list
           collectionReference = firestoreHelper.getFriendsColRef();
        }
        else if(index==2){

            collectionReference = firestoreHelper.getUserRef().collection(res.getString(R.string.user_groups));
        }
        if(collectionReference!=null) {
            collectionReference.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        list_items.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            AmountTypeDoc temp = documentSnapshot.toObject(AmountTypeDoc.class);
                            list_items.add(temp);
                        }

                        System.out.println(list_items);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
        }

    }
