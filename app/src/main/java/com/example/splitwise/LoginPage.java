package com.example.splitwise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    Toolbar toolbar;
    EditText userIdEditText;
    EditText passwordEditText;
    Button loginButton;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        toolbar= findViewById(R.id.toolbar);
        userIdEditText=findViewById(R.id.userIdEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        loginButton=findViewById(R.id.loginButton);

        progressBar= findViewById(R.id.progressBar);

        firebaseAuth= FirebaseAuth.getInstance();

        toolbar.setTitle("  Login");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                final String userId= userIdEditText.getText().toString();
                String password= passwordEditText.getText().toString();

                if(userId.equals("")||password.equals(""))
                {
                    Toast.makeText(LoginPage.this,
                            "IdTypeDoc ID and Password cannot be empty", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    String userId2=userId+"@splitwise.clone";
                    firebaseAuth.signInWithEmailAndPassword(userId2,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        userIdEditText.setText("");
                                        passwordEditText.setText("");
                                        Intent intent= new Intent(LoginPage.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);


                                        startActivity(intent);

                                    }
                                    else
                                    {
                                        Toast.makeText(LoginPage.this,task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }


                                }
                            });


                }
                progressBar.setVisibility(View.GONE);

            }
        });

    }
}
