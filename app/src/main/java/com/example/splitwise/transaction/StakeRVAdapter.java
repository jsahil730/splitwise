package com.example.splitwise.transaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.R;
import com.example.splitwise.add_friend_or_group.User;

import java.util.ArrayList;

public class StakeRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<UserTransact> list_users;
    private Context context;
    @SuppressWarnings("FieldCanBeLocal")
    private String group_id;
    private boolean equal_sp;
    private double total_amount;

    private Double convert_two_places(double amount) {
        amount *= 100;
        amount = (double) Math.round(amount);
        return (amount/100);
    }

    StakeRVAdapter(ArrayList<UserTransact> list_users, Context context, String group_id, Switch equal_split, double total_amount) {
        this.list_users = list_users;
        this.context = context;
        this.group_id = group_id;
        this.total_amount = total_amount;

        equal_sp = equal_split.isChecked();

        equal_split.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equal_sp = !equal_sp;
                notifyDataSetChanged();
            }
        });
    }

    ArrayList<UserTransact> getList_users() {
        double amt1 = 0;
        double amt2 = 0;

        for (UserTransact u : list_users) {
            if (!equal_sp) {
                amt1 += u.getStake();
            }
            amt2 += u.getAmount_paid();
        }
        if (amt2 != total_amount) {
            Toast.makeText(context, "Amount paid must sum up to total amount", Toast.LENGTH_SHORT).show();
            return null;
        }
        else if (!equal_sp && amt1 != total_amount) {
            Toast.makeText(context, "Stakes must sum up to total amount", Toast.LENGTH_SHORT).show();
            return null;
        }
        else {
            return list_users;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_stakeholder_card,parent,false);
        return new StakeViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final StakeViewHolder stakeViewHolder = (StakeViewHolder) holder;
        stakeViewHolder.textView.setText(list_users.get(position).getName());

        stakeViewHolder.stake_amount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
        stakeViewHolder.paid_amount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});

        stakeViewHolder.stake_amount.setEnabled(!equal_sp);

        stakeViewHolder.stake_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String a = s.toString();
                double p = 0.00;
                if (!a.isEmpty()) {
                    p = Double.parseDouble(a);
                }
                list_users.get(position).setStake(p);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        stakeViewHolder.paid_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String a = s.toString();
                double p = 0.00;
                if (!a.isEmpty()) {
                    p = Double.parseDouble(a);
                }
                list_users.get(position).setAmount_paid(p);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (equal_sp) {
            stakeViewHolder.stake_amount.setText(String.format("%.2f",convert_two_places(total_amount/getItemCount())));
        }
    }

    @Override
    public int getItemCount() {
        return list_users.size();
    }

    public class StakeViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        EditText stake_amount;
        EditText paid_amount;

        StakeViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.stakeholder_name);
            stake_amount = itemView.findViewById(R.id.stake_amt);
            paid_amount = itemView.findViewById(R.id.paid_amt);

            stake_amount.setEnabled(!equal_sp);
        }
    }
}
