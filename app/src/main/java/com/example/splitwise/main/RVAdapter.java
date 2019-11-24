package com.example.splitwise.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.R;
import com.example.splitwise.friend_ui.FriendOpen;
import com.example.splitwise.group_ui.GroupOpen;
import com.example.splitwise.transaction.IdAmountDocPair;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int Header = 1;
    private static final int Normal = 2;
    private static final int Footer = 3;

    private boolean activity_tab;
    private Context context;
    private List<IdAmountDocPair> list_items;
    private int index;

    public RVAdapter(boolean activity_tab, Context context, List<IdAmountDocPair> list_items, int index) {
        this.activity_tab = activity_tab;
        this.context = context;
        this.list_items = list_items;
        this.index = index;
    }

    @Override
    public int getItemViewType(int position) {
        if (!activity_tab && index != 4) {
            if (position == 0) {
                return Header;
            }
            else if (position == list_items.size()) {
                return Footer;
            }
            else {
                return Normal;
            }
        }
        else {
            if (position == list_items.size()) {
                return Footer;
            }
            else {
                return Normal;
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (activity_tab) {
            if (viewType == Normal) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_activity_card,parent,false);
                return new ActivityViewHolder(v);
            }
            else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer,parent,false);
                return new FooterViewHolder(v);
            }
        }
        else if (index == 4) {
            if (viewType == Normal) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expense_card, parent, false);
                return new ExpenseViewHolder(v);
            }
            else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer,parent,false);
                return new FooterViewHolder(v);
            }
        }
        else {
            if (viewType == Header) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expense_header,parent,false);
                return new HeaderViewHolder(v);
            }
            else if (viewType == Normal) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_expense_card, parent, false);
                return new ExpenseViewHolder(v);
            }
            else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer,parent,false);
                return new FooterViewHolder(v);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (activity_tab) {
            switch (getItemViewType(position)) {
                case Normal:// set text for all
                    if(list_items.get(position).getId()==null)
                    {
                        ((ActivityViewHolder) holder).transac_amount.setText("You are not involved");
                    }
                    else {
                        ((ActivityViewHolder) holder).transac_amount.setText(getText(list_items.get(position).getAmount()));
                    }
                    ((ActivityViewHolder) holder).activity_done.setText(list_items.get(position).getName());
                    break;
                case Footer:
                    break;
            }
        }
        else {
            switch (getItemViewType(position)) {
                case Header:
                    if(!list_items.isEmpty()) {
                        double amount = list_items.get(0).getAmount();
                        amount *= 100;
                        amount = (double) Math.round(amount);
                        amount = amount/100;
                        ((HeaderViewHolder) holder).total_balance.setText(String.format("Your Current Balance is : %.2f",amount));
                    }
                    break;
                case Normal:
                    final IdAmountDocPair pair = list_items.get(position);
                    ExpenseViewHolder Holder = (ExpenseViewHolder) holder;
                    Holder.name.setText(pair.getName());
                    Holder.amount.setText(getText(pair.getAmount()));

                    Holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent;
                            if (index == 2 || index == 4) {
                                intent = new Intent(context,GroupOpen.class);
                            }
                            else {
                                intent = new Intent(context, FriendOpen.class);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString(context.getResources().getString(R.string.key_group_id),pair.getId());
                            bundle.putString(context.getResources().getString(R.string.key_group_name),pair.getName());
                            bundle.putDouble(context.getResources().getString(R.string.key_group_amount),pair.getAmount());
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                    });


                    break;
                case Footer:
                    break;
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private String getText(Double amount) {
        String s;
        amount *= 100;
        amount = (double) Math.round(amount);
        amount /= 100;
        if (amount.floatValue() == 0) {
            s = "settled up";
        }
        else if (amount > 0) {
            s = String.format("you owe \n %.2f",amount);
        }
        else {
            s = String.format("owes you \n %.2f",-amount);
        }
        return s;
    }

    @Override
    public int getItemCount() {
        return list_items.size()+1;
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activity_done;
        TextView transac_amount;
        // add new textView and initialize;
        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);

            activity_done = itemView.findViewById(R.id.activity_done);
            transac_amount = itemView.findViewById(R.id.transac_amount);
        }
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView amount;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            amount = itemView.findViewById(R.id.amount);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView total_balance;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            total_balance = itemView.findViewById(R.id.total_balance);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
