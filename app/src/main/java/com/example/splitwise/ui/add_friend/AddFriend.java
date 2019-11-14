package com.example.splitwise.ui.add_friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.MainActivity;
import com.example.splitwise.R;

public class AddFriend extends AppCompatActivity {

    EditText friend_id;
    Button finish_button;
    ProgressBar progressBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        friend_id = findViewById(R.id.add_friend);
        finish_button = findViewById(R.id.save_friend);
        progressBar = findViewById(R.id.progressBar);
        toolbar= findViewById(R.id.toolbar);
        final FirestoreHelper firestoreHelper=new FirestoreHelper(this);

        toolbar.setTitle("  Add Friend");

        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String friend_user_id = friend_id.getText().toString();

                if (friend_user_id.isEmpty()) {
                    Toast.makeText(AddFriend.this,"Friend ID must not be empty!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else {

                    firestoreHelper.addFriend(friend_user_id);

                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(AddFriend.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    Toast.makeText(AddFriend.this,"Friend added successfully!",Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
