package com.example.splitwise.add_friend_or_group;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.splitwise.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int NORMAL = 0;

    private boolean clickable;
    private ArrayList<User> list_users;
    private SparseBooleanArray selected_users;

    public FriendRVAdapter(ArrayList<User> list_users, Context context, boolean clickable) {
        this.clickable = clickable;
        this.list_users = list_users;

        selected_users = new SparseBooleanArray();
    }

    public ArrayList<User> list_selected_users() {
        ArrayList<User> list = new ArrayList<>();

        if (clickable) {
            for (int i = 0; i < list_users.size(); i++) {
                if (isSelected(i)) {
                    list.add(list_users.get(i));
                }
            }
        }
        return list;
    }

    private boolean isSelected(int position) {
        return (selected_users.get(position,false));
    }

    @Override
    public int getItemViewType(int position) {
        return NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_card,parent,false);
        return new FriendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (clickable) {
            final FriendViewHolder Holder = (FriendViewHolder) holder;
            Holder.textView.setText(list_users.get(position).getUname());
            Holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Holder.imageView.getVisibility() == View.VISIBLE) {
                        Holder.imageView.setVisibility(View.GONE);
                        selected_users.delete(position);
                    }
                    else if (Holder.imageView.getVisibility() == View.GONE) {
                        Holder.imageView.setVisibility(View.VISIBLE);
                        selected_users.put(position,true);
                    }
                    notifyDataSetChanged();
                }
            });
        }
        else {
            ((FriendViewHolder) holder).textView.setText(list_users.get(position).getUname());
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textView;
        ImageView imageView;

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.friend_name);
            imageView = itemView.findViewById(R.id.imageView);
            imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
            return list_users.size();
    }
}
