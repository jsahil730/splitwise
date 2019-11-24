package com.example.splitwise.group_ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.splitwise.AmountTypeDoc;
import com.example.splitwise.FirestoreHelper;
import com.example.splitwise.R;
import com.example.splitwise.add_friend_or_group.User;
import com.example.splitwise.transaction.IdAmountDocPair;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        final FirestoreHelper firestoreHelper = new FirestoreHelper(this);
        switch (item.getItemId()) {
            case R.id.settle_up:
                firestoreHelper.getGroupsRef().document(pair.getId()).collection(getString(R.string.GroupUsers)).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            ArrayList<User> temp = new ArrayList<>();
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                                {
                                    if (!documentSnapshot.getId().equals(firestoreHelper.getUserId())) {
                                        temp.add(new User(documentSnapshot.getId(), Objects.requireNonNull(documentSnapshot.toObject(AmountTypeDoc.class)).getName()));
                                    }
                                }

                                Intent intent = new Intent(GroupOpen.this,SettleUpGroup.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList(getString(R.string.key_user_settle),temp);
                                bundle.putString(getString(R.string.key_group_id),pair.getId());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("friend error", e.getMessage());
                    }
                });
                //settle up here
                return true;
            case R.id.balances:
                //balances displayed
                return true;
            case R.id.leave_group:
                Calendar c = Calendar.getInstance();
                Date today = c.getTime();
                firestoreHelper.leave_group(pair.getId(),today);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
