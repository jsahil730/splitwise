package com.example.splitwise;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.splitwise.transaction.IdAmountDocPair;
import com.example.splitwise.transaction.TransacDoc;
import com.example.splitwise.transaction.TransactionRecord;
import com.example.splitwise.transaction.UserTransact;
import com.example.splitwise.ui.add.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirestoreHelper {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userColRef;

    public DocumentReference getUserRef() {
        return userRef;
    }

    private DocumentReference userRef;

    @NonNull
    public CollectionReference getUserColRef() {
        return userColRef;
    }

    @NonNull
    public CollectionReference getFriendsColRef() {
        return friendsColRef;
    }

    private CollectionReference friendsColRef;
    private String userId;
    private Resources res;
    private Context context;


    private CollectionReference groupsRef;


    public FirestoreHelper() {
    }

    @NonNull
    public String getUserId() {
        return userId;
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
        AmountTypeDoc user = new AmountTypeDoc(name,0);
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


        userColRef.document(id1).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            final CollectionReference main_friends = userColRef.document(id1)
                                    .collection(res.getString(R.string.FriendsCollection));
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
                                                                    AmountTypeDoc friend_to_be_added = documentSnapshot.toObject(AmountTypeDoc.class);
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
                    }
                });

    }

    /**
     *
     * above functions end here
     */


    public void addFriend(final String friendId) {
       // Toast.makeText(context, "No error", Toast.LENGTH_SHORT).show();

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

                                            AmountTypeDoc user_mem = documentSnapshot.toObject(AmountTypeDoc.class);
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
    public List<Pair<IdAmountDocPair, List< IdAmountDocPair> >> findSolution(List<UserTransact> userTransacts)
    {
        List<Pair<IdAmountDocPair, List< IdAmountDocPair> >> solution= new ArrayList<>();

        List<IdAmountDocPair> amounts= new ArrayList<>();
        for( UserTransact userTransact: userTransacts)
            {


            IdAmountDocPair temp= new IdAmountDocPair(userTransact.getUserID(),
                    new AmountTypeDoc(userTransact.getName(),
                            (userTransact.getStake()-userTransact.getAmount_paid())));
            amounts.add(temp);
            List<IdAmountDocPair> temp2= new ArrayList<>();
            solution.add(Pair.create(temp, temp2));

        }

        for(int i= 0 ; i< amounts.size(); i++)
        {
            IdAmountDocPair personA = amounts.get(i);
            if(personA.getAmount() <= 0)
            {
                continue;
            }

            for(int j=0; j< amounts.size() ; j++)
            {
                if (i==j){continue;}


                IdAmountDocPair personB = amounts.get(j);

                if(!(personB.getAmount()<0))
                {
                    continue;
                }

                if(personA.getAmount() <= Math.abs(personB.getAmount()))
                {
                    double transfer= personA.getAmount();
                    personA.setAmount(0);
                    personB.setAmount(personB.getAmount()+transfer);

                    IdAmountDocPair transferAB = new IdAmountDocPair(personB.getId(), new AmountTypeDoc(personB.getName(), transfer));
                    IdAmountDocPair transferBA = new IdAmountDocPair(personA.getId(), new AmountTypeDoc(personA.getName(), -1*transfer));

                    solution.get(i).second.add(transferAB);
                    solution.get(j).second.add(transferBA);

                    break;
                }
                else if(personA.getAmount() > Math.abs(personB.getAmount()))
                {
                    double transfer= Math.abs(personB.getAmount());
                    personA.setAmount(personA.getAmount()-transfer);
                    personB.setAmount(0);

                    IdAmountDocPair transferAB = new IdAmountDocPair(personB.getId(), new AmountTypeDoc(personB.getName(), transfer));
                    IdAmountDocPair transferBA = new IdAmountDocPair(personA.getId(), new AmountTypeDoc(personA.getName(), -1*transfer));

                    solution.get(i).second.add(transferAB);
                    solution.get(j).second.add(transferBA);

                    continue;

                }

            }
        }
        return solution;
    }

    public void processTransaction(final TransactionRecord record){



        final TransacDoc transacDoc = new TransacDoc(record.getGroupID(),record.getDescription(),record.getTotalAmount(),record.getTag(),record.getDate());
        final List<UserTransact> userTransacts = record.getExchanges();

        List<CollectionReference> collectionReferenceList= new ArrayList<>();
        CollectionReference collectionReference;

        final List<Pair<IdAmountDocPair, List< IdAmountDocPair> >> solution= findSolution(userTransacts);

        if(record.getGroupID()==null){
            String first_user= userTransacts.get(0).getUserID();
            userColRef.document(first_user)
                    .collection(res.getString(R.string.nonGroupTransactionCollection))
                    .document(record.getTag())
                    .collection(res.getString(R.string.TransactionItems))
                    .add(transacDoc)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String transactionID=documentReference.getId();

                            for(int j=0; j < userTransacts.size() ; j++)
                            {
                                String tempuserID= userTransacts.get(j).getUserID();

                                final DocumentReference transaction= userColRef.document(tempuserID)
                                        .collection(res.getString(R.string.nonGroupTransactionCollection))
                                        .document(record.getTag())
                                        .collection(res.getString(R.string.TransactionItems))
                                        .document(transactionID);

                                transaction.set(transacDoc)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                CollectionReference allExchanges= transaction
                                                        .collection(res.getString(R.string.AllExchanges));

                                                for(int i=0; i< solution.size() ; i++)
                                                {
                                                    final int i2=i;
                                                    final AmountTypeDoc amountTypeDoc= new AmountTypeDoc(solution.get(i).first.getName(),
                                                            solution.get(i).first.getAmount());
                                                    DocumentReference depth_one= allExchanges.document(solution.get(i).first.getId());
                                                    final CollectionReference userSpecific= depth_one.collection(res.getString(R.string.userSpecificExchanges));

                                                    depth_one
                                                            .set(amountTypeDoc)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    List<IdAmountDocPair> listSecond= solution.get(i2).second;
                                                                    for(IdAmountDocPair idAmountDocPair: listSecond)
                                                                    {
                                                                        AmountTypeDoc amountTypeDoc1= new AmountTypeDoc(idAmountDocPair.getName()
                                                                                ,idAmountDocPair.getAmount());

                                                                        userSpecific.document(idAmountDocPair.getId()).set(amountTypeDoc1);
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }    //adds transaction to each involved user's non-group transaction collection in appropriate tag
        else
        {
            collectionReference= groupsRef.document(record.getGroupID())
                    .collection(res.getString(R.string.groupTransactionCollection))
                    .document(record.getTag())
                    .collection(res.getString(R.string.TransactionItems));

            collectionReference.add(transacDoc)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            CollectionReference allExchanges= documentReference
                                    .collection(res.getString(R.string.AllExchanges));

                            for(int i=0; i< solution.size() ; i++)
                            {
                                final int i2=i;
                                final AmountTypeDoc amountTypeDoc= new AmountTypeDoc(solution.get(i).first.getName(),
                                        solution.get(i).first.getAmount());
                                DocumentReference depth_one= allExchanges.document(solution.get(i).first.getId());
                                final CollectionReference userSpecific= depth_one.collection(res.getString(R.string.userSpecificExchanges));

                                depth_one
                                        .set(amountTypeDoc)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                List<IdAmountDocPair> listSecond= solution.get(i2).second;
                                                for(IdAmountDocPair idAmountDocPair: listSecond)
                                                {
                                                    AmountTypeDoc amountTypeDoc1= new AmountTypeDoc(idAmountDocPair.getName()
                                                            ,idAmountDocPair.getAmount());

                                                    userSpecific.document(idAmountDocPair.getId()).set(amountTypeDoc1);
                                                }
                                            }
                                        });
                            }
                        }
                    });

            CollectionReference groupUserCol = groupsRef.document(record.getGroupID()).collection(res.getString(R.string.GroupUsers));
            for(Pair<IdAmountDocPair, List< IdAmountDocPair> > one_user: solution)
            {
                final IdAmountDocPair t1 = one_user.first;
                final List<IdAmountDocPair> t2 = one_user.second;
                final DocumentReference documentReference= groupUserCol.document(t1.getId());
                documentReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                AmountTypeDoc temp = documentSnapshot.toObject(AmountTypeDoc.class);
                                double new_amount = temp.getAmount()+t1.getAmount();
                                documentReference.update("amount",new_amount)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("hi", "onSuccess: updated amount");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("bye", e.getMessage());
                                            }
                                        });

                                final CollectionReference userSpecificLenders= documentReference.collection(res.getString(R.string.borrowersCollection));
                                for(final IdAmountDocPair sec_person : t2)
                                {
                                    final DocumentReference second_doc_ref= userSpecificLenders.document(sec_person.getId());
                                    second_doc_ref.get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if(!documentSnapshot.exists())
                                                    {
                                                        AmountTypeDoc temp1 = new AmountTypeDoc(sec_person.getName(),sec_person.getAmount());
                                                        userSpecificLenders.document(sec_person.getId()).set(temp1);
                                                    }
                                                    else
                                                    {
                                                        AmountTypeDoc temp1 = documentSnapshot.toObject(AmountTypeDoc.class);
                                                        double new_amount = temp1.getAmount()+sec_person.getAmount();
                                                        if(new_amount!=0)
                                                        {
                                                            second_doc_ref.update("amount",new_amount)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.i("okay", "okay");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.i("update error in borrow", e.getMessage());
                                                                        }
                                                                    });
                                                        }
                                                        else{
                                                            second_doc_ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.i("okay", "onSuccess: okay");
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.i("deletion error", e.getMessage());
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i("borrowers problem", e.getMessage());
                                                }
                                            });
                                }
                            }
                        });
            }
        }// adds the transaction in the group and updates the group users collection accordingly



        List< HashMap<String, IdAmountDocPair>> solutionHash= new ArrayList<>();

        for(int i=0 ; i< solution.size() ; i++)   //Updated amounts awed/bowrrowed in friendlists of users
        {
            IdAmountDocPair personA= solution.get(i).first;
            String personAid= personA.getId();
            solutionHash.add(new HashMap<String, IdAmountDocPair>());

            for( IdAmountDocPair personB2 : solution.get(i).second)
            {
                solutionHash.get(i).put(personB2.getId(), personB2);

                final IdAmountDocPair personB = personB2;
                String personBid= personB.getId();
                final AmountTypeDoc friendsDoc= new AmountTypeDoc(personB.getName(), personB.getAmount());

                final DocumentReference friendsDocRef= userColRef.document(personAid)
                        .collection(res.getString(R.string.FriendsCollection))
                        .document(personBid);
                friendsDocRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(!documentSnapshot.exists())
                                {
                                    friendsDocRef.set(friendsDoc);
                                }
                                else
                                {
                                    friendsDocRef.update("amount",
                                            (documentSnapshot.toObject(AmountTypeDoc.class).getAmount()+personB.getAmount()));
                                }

                            }
                        });

            }


        }


        for(int i=0; i< userTransacts.size(); i++)   //Make all the users involved in the transaction friends
        {
            String personAid= userTransacts.get(i).getUserID();

            for(UserTransact userTransactB : userTransacts)
            {
                String personBid= userTransactB.getUserID();

                if(!solutionHash.get(i).containsKey(personBid))
                {
                    add_one_direction(personAid, personBid);
                }



            }
        }


    }
}
