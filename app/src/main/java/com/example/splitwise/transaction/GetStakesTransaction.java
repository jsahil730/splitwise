package com.example.splitwise.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import com.example.splitwise.MainActivity;
import com.example.splitwise.R;
import com.example.splitwise.add_friend_or_group.User;

import java.util.ArrayList;
import java.util.Objects;

public class GetStakesTransaction extends AppCompatActivity {

    Switch equal_split;
    RecyclerView recyclerView;
    StakeRVAdapter adapter;
    Double amt;
    String desc;
    String tag_tr;
    ArrayList<UserTransact> list_trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_stakes);

        Bundle bundle = getIntent().getExtras();
        amt = Objects.requireNonNull(bundle).getDouble(getString(R.string.key_transac_amt));
        desc = bundle.getString(getString(R.string.key_transac_desc));
        tag_tr = bundle.getString(getString(R.string.key_transac_tag));
        list_trans = new ArrayList<>();
        ArrayList<User> list1 = bundle.getParcelableArrayList(getString(R.string.key_transac_user_list));

        for (User u : Objects.requireNonNull(list1)) {
            list_trans.add(new UserTransact(u));
        }

        equal_split = findViewById(R.id.switch_split);
        recyclerView = findViewById(R.id.users_stake_list);
        adapter = new StakeRVAdapter(list_trans,this,null,equal_split,amt);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_get_stakes,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish_transaction:
                ArrayList<UserTransact> list = adapter.getList_users();
                if (list != null) {
                    list_trans = list;
                    //Apply transaction function
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
