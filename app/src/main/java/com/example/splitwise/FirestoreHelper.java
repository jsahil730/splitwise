package com.example.splitwise;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userColRef;
    private DocumentReference userRef;
    private CollectionReference friendsColRef;
    private String userId;
    private Resources res;
    private Context context;

    public FirestoreHelper() {
    }

    public FirestoreHelper(Context context) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getEmail();
        res = context.getResources();
        userColRef = db.collection(res.getString(R.string.UserCollection));
        userRef = userColRef.document(userId);
        friendsColRef = userRef.collection(res.getString(R.string.FriendsCollection));
        this.context = context;

    }

    public void addUserDetails(String name) {
        User user = new User(name, userId);
        userRef.set(user).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * below functions are not to be used separately
     * forbidden functions
     */

    private void add_one_direction(final String id1, final String to_be_added) {


        if (id1.equals(to_be_added)) {
            return ;
        }

        final CollectionReference main_friends = userColRef.document(id1).collection(res.getString(R.string.FriendsCollection));

        main_friends.document(to_be_added).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {

                            userColRef.document(to_be_added).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            if (documentSnapshot.exists()) {
                                                User friend_to_be_added = documentSnapshot.toObject(User.class);
                                                FriendDoc friendDoc = new FriendDoc(to_be_added, friend_to_be_added.getName(), 0);

                                                main_friends.document(to_be_added).set(friendDoc);


                                            } else {
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                        } else {

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     *
     * above functions end here
     */


    public void addFriend(final String friendId) {

        add_one_direction(userId,friendId);
        add_one_direction(friendId,userId);

    }
}
