package com.example.splitwise.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.ActivityTypeDoc;
import com.example.splitwise.R;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter {

    private List<ActivityTypeDoc> list;
    private Context context;


    ActivityAdapter(Context context, List<ActivityTypeDoc> list_items) {
        this.context = context;
        list = list_items;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card_layout,parent,false);
        return new ActivityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ActivityViewHolder Holder = (ActivityViewHolder) holder;

        ActivityTypeDoc temp = list.get(position);

        if(temp.getGroupId()==null)
        {
            Holder.groupName.setText("Non Group Transaction  |  "+temp.getDate());
        }
        else{
            Holder.groupName.setText("Group Transaction  |  "+temp.getDate());
        }
        Holder.amount.setText(""+temp.getAmount());
        Holder.desc.setText(temp.getDescription());
        Holder.tag.setText(temp.getTag());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView tag;
        TextView desc;
        TextView amount;
        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);

            groupName = itemView.findViewById(R.id.groupName);
            tag= itemView.findViewById(R.id.tagTextView);
            desc= itemView.findViewById(R.id.descriptionTextView);
            amount= itemView.findViewById(R.id.amountTextView);

        }
    }
}
