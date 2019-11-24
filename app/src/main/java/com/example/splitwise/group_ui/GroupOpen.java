package com.example.splitwise.group_ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwise.R;
import com.example.splitwise.transaction.IdAmountDocPair;

import java.util.Objects;

public class GroupOpen extends AppCompatActivity {

    TextView groupName;
    TextView amountOwed;
    RecyclerView recyclerView;
    TagRVAdapter adapter;
    IdAmountDocPair pair;

    @SuppressLint("DefaultLocale")
    public String get_amount_string(double amount) {
        String s;
        if (amount == 0) {
            s = "You are all settled up here";
        }
        else if (amount > 0) {
            s = String.format("You owe %.2f",amount);
        }
        else {
            s = String.format("You are owed %.2f",amount);
        }
        return s;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_open);

        groupName = findViewById(R.id.group_name);
        amountOwed = findViewById(R.id.amount_owed);
        recyclerView = findViewById(R.id.tags_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        pair = new IdAmountDocPair(Objects.requireNonNull(bundle).getString(getString(R.string.key_group_id)),bundle.getString(getString(R.string.key_group_name))
                ,bundle.getDouble(getString(R.string.key_group_amount)));


        groupName.setText(pair.getName());
        amountOwed.setText(get_amount_string(pair.getAmount()));

        adapter = new TagRVAdapter(this,pair.getId());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle bundle = getIntent().getExtras();
        pair = new IdAmountDocPair(Objects.requireNonNull(bundle).getString(getString(R.string.key_group_id)),bundle.getString(getString(R.string.key_group_name))
                ,bundle.getDouble(getString(R.string.key_group_amount)));

        groupName.setText(pair.getName());
        amountOwed.setText(get_amount_string(pair.getAmount()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_group,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settle_up:
                //settle up here
                return true;
            case R.id.balances:
                //balances displayed
                return true;
            case R.id.leave_group:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
