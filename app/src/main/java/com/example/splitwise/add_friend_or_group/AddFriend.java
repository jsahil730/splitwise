package com.example.splitwise.add_friend_or_group;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.MainActivity;
import com.example.splitwise.R;

public class AddFriend extends AppCompatActivity {

    EditText friend_id;
    FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        friend_id = findViewById(R.id.add_friend);
        firestoreHelper=new FirestoreHelper(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_friend,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_friend:
                String friend_user_id = friend_id.getText().toString();

                if (friend_user_id.isEmpty()) {
                    Toast.makeText(AddFriend.this,"Friend ID must not be empty!",Toast.LENGTH_LONG).show();
                }
                else {

                    firestoreHelper.addFriend(friend_user_id+"@splitwise.clone");

                    Intent intent = new Intent(AddFriend.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    //Toast.makeText(AddFriend.this,"Friend added successfully!",Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
