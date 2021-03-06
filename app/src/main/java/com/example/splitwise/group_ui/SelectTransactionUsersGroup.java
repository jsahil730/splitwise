package com.example.splitwise.group_ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.splitwise.R;
import com.example.splitwise.add_friend_or_group.FriendRVAdapter;
import com.example.splitwise.add_friend_or_group.User;

import java.util.ArrayList;
import java.util.Objects;

public class SelectTransactionUsersGroup extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    ArrayList<User> list_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transact_users_group);

        Bundle bundle = getIntent().getExtras();
        list_users = Objects.requireNonNull(bundle).getParcelableArrayList(getString(R.string.key_friends));

        recyclerView = findViewById(R.id.transactors_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FriendRVAdapter(list_users,this,true);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_select_transactors_group,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish_transactor_selection:
                list_users = ((FriendRVAdapter) adapter).list_selected_users();

                Intent intent = new Intent(SelectTransactionUsersGroup.this,AddTransactionGroup.class);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(getString(R.string.key_users_selection),list_users);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
