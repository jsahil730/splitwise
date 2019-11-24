package com.example.splitwise.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.R;
import com.example.splitwise.transaction.IdAmountDocPair;

import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<IdAmountDocPair> list_items;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_activity, container, false);

        recyclerView = root.findViewById(R.id.activities);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        list_items = new ArrayList<>();

        adapter = new RVAdapter(true,this.getContext(),list_items,3);
        recyclerView.setAdapter(adapter);

        return root;
    }
}
