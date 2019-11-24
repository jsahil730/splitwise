package com.example.splitwise.group_ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.R;

import java.util.Arrays;
import java.util.List;

public class TagRVAdapter extends RecyclerView.Adapter {

    private List<String> list;
    private Context context;

    TagRVAdapter(Context context) {
        this.context = context;
        list = Arrays.asList(context.getResources().getStringArray(R.array.tag_choices));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tag_card,parent,false);
        return new TagViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final TagViewHolder Holder = (TagViewHolder) holder;
        Holder.textView.setText(list.get(position));

        Holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = list.get(position);
                Intent intent = new Intent(context,TransactionsList.class);
                intent.putExtra(context.getString(R.string.key_tag),tag);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        TagViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tag_name);
        }
    }
}
