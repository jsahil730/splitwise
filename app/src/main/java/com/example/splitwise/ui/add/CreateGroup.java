package com.example.splitwise.ui.add;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.splitwise.MainActivity;
import com.example.splitwise.R;

public class CreateGroup extends AppCompatActivity {

    EditText group_name;
    Button finish_button;
    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        group_name = findViewById(R.id.group_name);
        finish_button = findViewById(R.id.save_group);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("  Create Group");

        recyclerView = findViewById(R.id.users_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FriendRVAdapter(false);
        recyclerView.setAdapter(adapter);

        group_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = group_name.getText().toString();

                if (groupName.isEmpty()) {
                    Toast.makeText(CreateGroup.this,"Group name must not be empty!",Toast.LENGTH_LONG).show();
                }
//                else if (group_user_ids.size() < 2) {
//                    Toast.makeText(CreateGroup.this, "Group size must not be less than 3", Toast.LENGTH_LONG).show();
//                }
                else {
                    Intent intent = new Intent(CreateGroup.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    Toast.makeText(CreateGroup.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
