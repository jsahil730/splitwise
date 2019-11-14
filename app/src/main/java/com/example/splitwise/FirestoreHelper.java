package com.example.splitwise;

import android.content.Context;
import android.content.res.Resources;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference userColRef;
    private DocumentReference userRef;
    private CollectionReference friendsColRef;
    private String userId;
    private Resources res;

    public FirestoreHelper(){}

    public FirestoreHelper(Context context){

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        userId= firebaseUser.getEmail();
        res= context.getResources();
        userColRef= db.collection(res.getString(R.string.UserCollection));
        userRef= userColRef.document(userId);
        friendsColRef= userRef.collection(res.getString(R.string.FriendsCollection));

    }

    public void addUserDetails(String name)
    {
        User user=new User(name,userId);
        userRef.set(user);
    }
}
