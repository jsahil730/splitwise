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
import android.widget.Toast;

import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.MainActivity;
import com.example.splitwise.R;
import com.example.splitwise.add_friend_or_group.FriendRVAdapter;
import com.example.splitwise.add_friend_or_group.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class SettleUpGroup extends AppCompatActivity {

    FriendRVAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<User> list;
    String group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_up_group);

        recyclerView = findViewById(R.id.settle_users_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        list = Objects.requireNonNull(bundle).getParcelableArrayList(getString(R.string.key_user_settle));
        group_id = bundle.getString(getString(R.string.key_group_id));

        adapter = new FriendRVAdapter(list,this,true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_settle_up,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.finish_button:
                ArrayList<User> sett_list = adapter.list_selected_users();
                if (sett_list == null || sett_list.isEmpty()) {
                    Toast.makeText(this, "This list can't be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    ArrayList<String> list1 = new ArrayList<>();
                    for (User u : sett_list) {
                        list1.add(u.getUid());
                    }

                    FirestoreHelper firestoreHelper = new FirestoreHelper(this);
                    firestoreHelper.settleGroup(group_id,list1, Calendar.getInstance().getTime(),null);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}