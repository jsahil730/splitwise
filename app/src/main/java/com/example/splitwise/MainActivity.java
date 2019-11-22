package com.example.splitwise;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.splitwise.login_or_signup.SignupPage;
import com.example.splitwise.ui.add.AddFriend;
import com.example.splitwise.ui.add.CreateGroup;
import com.example.splitwise.ui.add.User;
import com.example.splitwise.ui.main.SectionsPagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirestoreHelper firestoreHelper;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        firestoreHelper=new FirestoreHelper(this);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                firebaseAuth.signOut();

                Intent intent1=new Intent(getApplicationContext(), SignupPage.class);

                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);

                return true;
            case R.id.settings:
                return true;
            case R.id.add_friend:
                Intent intent3 = new Intent(this, AddFriend.class);
                startActivity(intent3);
                return true;
            case R.id.create_group:
                FirestoreHelper firestoreHelper = new FirestoreHelper(this);
                final String uid = firestoreHelper.getUserId();
                firestoreHelper.getUserRef().get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String uname = documentSnapshot.toObject(IdTypeDoc.class).getName();

                        Intent intent4 = new Intent(MainActivity.this, CreateGroup.class);

                        Bundle bundle = new Bundle();
                        ArrayList<User> userref = new ArrayList<>();
                        userref.add(new User(uid,uname));
                        bundle.putParcelableArrayList("user_list",userref);
                        intent4.putExtras(bundle);

                        startActivity(intent4);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                ;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}