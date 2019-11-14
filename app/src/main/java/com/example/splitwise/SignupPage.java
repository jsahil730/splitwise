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

public class SignupPage extends AppCompatActivity {

    EditText nameEditText;
    EditText userIdEditText;
    EditText passwordEditText;
    Button signupButton;
    Button loginPageButton;
    Toolbar toolbar;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        nameEditText= findViewById(R.id.nameEditText);
        userIdEditText= findViewById(R.id.userIdEditText);
        passwordEditText= findViewById(R.id.passwordEditText);
        signupButton= findViewById(R.id.signUpButton);
        loginPageButton= findViewById(R.id.loginPageButton);
        toolbar =findViewById(R.id.toolbar);
        progressBar= findViewById(R.id.progressBar);
        toolbar.setTitle("  Sign Up");

        firebaseAuth= FirebaseAuth.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String userId= userIdEditText.getText().toString();
                final String name= nameEditText.getText().toString();
                final String password= passwordEditText.getText().toString();


                if(userId.equals("")|| name.equals("")|| password.equals(""))
                {
                    Toast.makeText(SignupPage.this,
                            "Name, Username and Password cannot be empty",Toast.LENGTH_SHORT).show();

                }
                else
                {

                    userId=userId+"@splitwise.clone";
                    firebaseAuth.createUserWithEmailAndPassword(userId,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful())
                                    {
                                        FirestoreHelper firestoreHelper=new FirestoreHelper(SignupPage.this);
                                        firestoreHelper.addUserDetails(name);

                                        userIdEditText.setText("");
                                        passwordEditText.setText("");
                                        nameEditText.setText("");



                                        Intent intent= new Intent(SignupPage.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        Toast.makeText(SignupPage.this,"Registered successfully",
                                                Toast.LENGTH_SHORT).show();

                                        startActivity(intent);
                                    }
                                    else if(task.getException().getMessage().equals("The email address is badly formatted."))
                                    {
                                        // Toast.makeText(signupPage.this,
                                        //         task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        Toast.makeText(SignupPage.this,
                                                "User ID must not contain special characters like @,(,),etc, or '.' at the ends", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(SignupPage.this,
                                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                }

            }

        });

        loginPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SignupPage.this, LoginPage.class);
                startActivity(intent);
            }
        });



    }
}
