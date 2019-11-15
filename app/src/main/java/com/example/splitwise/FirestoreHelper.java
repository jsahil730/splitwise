package com.example.splitwise;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.splitwise.transaction.TransacDoc;
import com.example.splitwise.transaction.TransactionRecord;
import com.example.splitwise.transaction.UserTransact;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

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

    private CollectionReference groupsRef;


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
        groupsRef= db.collection(res.getString(R.string.GroupsCollection));
        this.context = context;

    }

    public void addUserDetails(String name) {
        IdTypeDoc user = new IdTypeDoc(name);
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
                                                IdTypeDoc friend_to_be_added = documentSnapshot.toObject(IdTypeDoc.class);
                                                AmountTypeDoc friendDoc = new AmountTypeDoc(friend_to_be_added.getName(), 0);

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

    public void create_group(final List<String> members, final String group_name){

        members.add(userId);
        for(String member1: members){
            for(String member2: members){
                add_one_direction(member1,member2);
            }
        }

        IdTypeDoc new_group = new IdTypeDoc(group_name);

        groupsRef.add(new_group)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {
                        String group_ID=documentReference.getId();
                        final CollectionReference groupUserscollRef = documentReference.collection(res.getString(R.string.GroupUsers));

                        for(final String mem: members){
                            DocumentReference memberdocref = userColRef.document(mem);
                            memberdocref.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            IdTypeDoc user_mem = documentSnapshot.toObject(IdTypeDoc.class);
                                            AmountTypeDoc to_add = new AmountTypeDoc(user_mem.getName(),0);
                                            groupUserscollRef.document(mem).set(to_add);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context,e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            memberdocref.collection(res.getString(R.string.user_groups)).document(group_ID).set(new AmountTypeDoc(group_name,0));
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void processTransaction(TransactionRecord record){

        TransacDoc transacDoc = new TransacDoc(record.getGroupID(),record.getDescription(),record.getTotalAmount(),record.getTag(),record.getDate());
        List<UserTransact> userTransacts = record.getExchanges();

        List<CollectionReference> collectionReferenceList= new ArrayList<>();
        CollectionReference collectionReference;

        if(record.getGroupID()==null){
            for(UserTransact userTransact : userTransacts)
            {
                String userId= userTransact.getUserID();
                collectionReferenceList.add(userColRef.document(userId)
                        .collection(res.getString(R.string.nonGroupTransactionCollection))
                        .document(record.getTag())
                        .collection(res.getString(R.string.TransactionItems))
                        .add(transacDoc)
                        .getResult()
                        .collection(res.getString(R.string.AllExchanges)));

            }
        }
        else
        {
            collectionReference= groupsRef.document(record.getGroupID())
                    .collection(res.getString(R.string.groupTransactionCollection))
                    .document(record.getTag())
                    .collection(res.getString(R.string.TransactionItems))
                    .add(transacDoc)
                    .getResult()
                    .collection(res.getString(R.string.AllExchanges));
        }


    }

}
