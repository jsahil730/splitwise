package com.example.splitwise.ui.add;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.splitwise.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int GROUP_NAME = 0;
    private static final int ADD_PEOPLE = 1;
    private static final int NORMAL = 2;

    private boolean clickable;
    private Context context;
    private ArrayList<User> list_users;
    private int size;
    private SparseBooleanArray selected_users;

    public FriendRVAdapter(ArrayList<User> list_users, Context context, boolean clickable) {
        this.clickable = clickable;
        this.context = context;
        this.list_users = list_users;
        size = list_users.size();

        selected_users = new SparseBooleanArray();
    }

    public ArrayList<User> list_selected_users() {
        ArrayList<User> list = new ArrayList<User>();

        if (clickable) {
            for (int i = 0; i < list_users.size(); i++) {
                if (isSelected(i)) {
                    list.add(list_users.get(i));
                }
            }
        }
        return list;
    }

    public boolean isSelected(int position) {
        return (selected_users.get(position,false));
    }

    @Override
    public int getItemViewType(int position) {
        if (!clickable) {
            if (position == 0) return GROUP_NAME;
            else if (position == 1) return ADD_PEOPLE;
        }
        return NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (clickable) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_card,parent,false);
            return new FriendViewHolder(v);
        }
        else {
            switch(viewType) {
                case GROUP_NAME:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group_name,parent,false);
                    return new GroupNameViewHolder(v);

                case ADD_PEOPLE:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_users_group,parent,false);
                    return new AddPeopleViewHolder(v);

                default:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_card,parent,false);
                    return new FriendViewHolder(v);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)){
            case GROUP_NAME:
                break;
            case ADD_PEOPLE:
                break;
            case NORMAL:
                if (clickable) {
                    final FriendViewHolder Holder = (FriendViewHolder) holder;
                    Holder.textView.setText(list_users.get(position).getUname());
                    Holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Holder.imageView.getVisibility() == View.VISIBLE) {
                                Holder.itemView.setVisibility(View.GONE);
                                selected_users.delete(position);
                            }
                            else if (Holder.imageView.getVisibility() == View.GONE) {
                                Holder.imageView.setVisibility(View.VISIBLE);
                                selected_users.put(position,true);
                            }
                            notifyItemChanged(position);
                        }
                    });
                }
                else {
                    ((FriendViewHolder) holder).textView.setText(list_users.get(position-2).getUname());
                }
                break;
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textView;
        ImageView imageView;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.friend_name);
            imageView = itemView.findViewById(R.id.imageView);
            imageView.setVisibility(View.GONE);
        }

    }

    public class GroupNameViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        public EditText getEditText() {
            return editText;
        }

        EditText editText;
        CircleImageView circleImageView;
        TextView textView;

        public GroupNameViewHolder(@NonNull View itemView) {
            super(itemView);

            editText = itemView.findViewById(R.id.group_name_edit);
            circleImageView = itemView.findViewById(R.id.image_group);
            textView = itemView.findViewById(R.id.group);
        }

        //Upload Group photo implement onclick here and set listener
    }

    public class AddPeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        public AddPeopleViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.add_people_group);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,GetGroupUsers.class);
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {

        if(clickable) {
            return size;
        }
        else {
            return (size+2);
        }
    }
}
