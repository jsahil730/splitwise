package com.example.splitwise;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private boolean activity_tab = false;
    private int size = 0;
    private Context context;
    private List<Pair<String,String>> list_items;

    public RVAdapter(boolean activity_tab, Context context, List<Pair<String,String>> list_items) {
        this.activity_tab = activity_tab;
        this.context = context;
        this.list_items = list_items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (activity_tab) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_activity_card,parent,false);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expense_card,parent,false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String,String> pair = list_items.get(position);
        if (activity_tab) {
            holder.activity_done.setText(pair.first);
        }
        else {
            holder.name.setText(pair.first);
            holder.amount.setText(pair.second);
        }
    }

    @Override
    public int getItemCount() {
        return list_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView amount;
        public TextView activity_done;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            if (activity_tab) {
                activity_done = itemView.findViewById(R.id.activity_done);
            }
            else {
                name = itemView.findViewById(R.id.name);
                amount = itemView.findViewById(R.id.amount);
            }
        }
    }

}
