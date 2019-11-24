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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FirestoreHelper {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userColRef;
    private AmountTypeDoc userDoc;


    public DocumentReference getUserRef() {
        return userRef;
    }

    public AmountTypeDoc getUserDoc() {
        return userDoc;
    }

    public void setUserDoc(AmountTypeDoc userDoc) {
        this.userDoc = userDoc;
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


    public CollectionReference getGroupsRef() {
        return groupsRef;
    }

    private CollectionReference groupsRef;

    public CollectionReference getGroupsRef(){
        return  groupsRef;
    }

    public FirestoreHelper() {
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public FirestoreHelper(Context context) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = Objects.requireNonNull(firebaseUser).getEmail();
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
                                                                    TwoAmountDoc friendDoc = new TwoAmountDoc(Objects.requireNonNull(friend_to_be_added).getName(), 0,0);

                                                                    main_friends.document(to_be_added).set(friendDoc);


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
                                            AmountTypeDoc to_add = new AmountTypeDoc(Objects.requireNonNull(user_mem).getName(),0);

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
            IdAmountDocPair temp3= new IdAmountDocPair(userTransact.getUserID(),
                        new AmountTypeDoc(userTransact.getName(),
                                (userTransact.getStake()-userTransact.getAmount_paid())));
            amounts.add(temp);
            List<IdAmountDocPair> temp2= new ArrayList<>();
            solution.add(Pair.create(temp3, temp2));

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

        addActivities(solution, record.getGroupID(), record.getTag(), record.getDescription(), record.getDate());

        if(record.getGroupID()==null){
            String first_user= userTransacts.get(0).getUserID();
            userColRef.document(first_user)
                    .collection(res.getString(R.string.nonGroupTransactionCollection))
                    .document(record.getTag())
                    .collection(res.getString(R.string.TransactionItems))
                    .add(transacDoc)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
                            String transactionID=documentReference.getId();

                            for(int j=0; j < userTransacts.size() ; j++)
                            {
                                final String tempuserID= userTransacts.get(j).getUserID();
                                final int j2=j;
                                final DocumentReference transaction= userColRef.document(tempuserID)
                                        .collection(res.getString(R.string.nonGroupTransactionCollection))
                                        .document(record.getTag())
                                        .collection(res.getString(R.string.TransactionItems))
                                        .document(transactionID);

                                userColRef.document(tempuserID).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                AmountTypeDoc old_doc = documentSnapshot.toObject(AmountTypeDoc.class);
                                                userColRef.document(tempuserID).update("amount",old_doc.getAmount()+
                                                        solution.get(j2).first.getAmount());
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("non_group user", e.getMessage());
                                    }
                                });

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

            collectionReference.add(transacDoc)    //adding transaction to the groups group transaction collection
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
                final double new_amount_owed = t1.getAmount();
                final DocumentReference user_spec_group= userColRef.document(t1.getId()).collection(res.getString(R.string.user_groups))
                        .document(record.getGroupID());
                user_spec_group.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                double new_amount = documentSnapshot.toObject(AmountTypeDoc.class).getAmount()+new_amount_owed;
                                user_spec_group.update("amount",new_amount);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("firestoreHelper", e.getMessage());
                    }
                });

                final DocumentReference user_doc = userColRef.document(t1.getId());
                user_doc.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                double new_amount = documentSnapshot.toObject(AmountTypeDoc.class).getAmount()+new_amount_owed;
                                user_doc.update("amount",new_amount);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("error in userdocupdate", e.getMessage());
                    }
                });


                final List<IdAmountDocPair> t2 = one_user.second;
                final DocumentReference documentReference= groupUserCol.document(t1.getId());
                documentReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                AmountTypeDoc temp = documentSnapshot.toObject(AmountTypeDoc.class);
                                double new_amount = Objects.requireNonNull(temp).getAmount()+t1.getAmount();
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
                                                Log.i("bye", Objects.requireNonNull(e.getMessage()));
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
                                                        double new_amount = Objects.requireNonNull(temp1).getAmount()+sec_person.getAmount();
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
                                                                            Log.i("update error in borrow", Objects.requireNonNull(e.getMessage()));
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
                                                                    Log.i("deletion error", Objects.requireNonNull(e.getMessage()));
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i("borrowers problem", Objects.requireNonNull(e.getMessage()));
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

                TwoAmountDoc friendsDoc2;

                if(record.getGroupID()==null)
                {
                    friendsDoc2 = new TwoAmountDoc(personB.getName(), personB.getAmount(), personB.getAmount());
                }
                else
                {
                    friendsDoc2 = new TwoAmountDoc(personB.getName(), personB.getAmount(), 0);
                }

                final TwoAmountDoc friendsDoc= friendsDoc2;

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
                                            (Objects.requireNonNull(documentSnapshot.toObject(TwoAmountDoc.class)).getAmount()+friendsDoc.getAmount()));

                                    friendsDocRef.update("sec_amount",
                                            (Objects.requireNonNull(documentSnapshot.toObject(TwoAmountDoc.class)).getSec_amount()+friendsDoc.getSec_amount()));
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

    public void processSolutionForGroup(final List<Pair<IdAmountDocPair, List< IdAmountDocPair> >> solution, String GroupId, double total_amount, Date date,Boolean toggle,String Annih)
    {
        CollectionReference collectionReference= groupsRef.document(GroupId)
                .collection(res.getString(R.string.groupTransactionCollection))
                .document("General")
                .collection(res.getString(R.string.TransactionItems));
        final TransacDoc transacDoc = new TransacDoc(GroupId,"Settle Up Transaction",total_amount,"General",date);

        addActivities(solution, GroupId, "General", "Settle Up Transaction", date);

        collectionReference.add(transacDoc)    //adding transaction to the groups group transaction collection
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
                });// all clear

        CollectionReference groupUserCol = groupsRef.document(GroupId).collection(res.getString(R.string.GroupUsers));
        for(Pair<IdAmountDocPair, List< IdAmountDocPair> > one_user: solution) {
            if (one_user.first.getId().equals(Annih))
            {
                groupUserCol.document(Annih).delete();
                userColRef.document(Annih).collection(res.getString(R.string.user_groups)).document(GroupId).delete();
            }
            else
            {
                final IdAmountDocPair t1 = one_user.first;
                final double new_amount_owed = t1.getAmount();
                final DocumentReference user_spec_group = userColRef.document(t1.getId()).collection(res.getString(R.string.user_groups))
                        .document(GroupId);
                user_spec_group.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                double new_amount = documentSnapshot.toObject(AmountTypeDoc.class).getAmount() + new_amount_owed;
                                user_spec_group.update("amount", new_amount);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("firestoreHelper", e.getMessage());
                    }
                });// here grouplist

                final DocumentReference user_doc = userColRef.document(t1.getId());
                user_doc.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                double new_amount = documentSnapshot.toObject(AmountTypeDoc.class).getAmount() + new_amount_owed;
                                user_doc.update("amount", new_amount);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("error in userdocupdate", e.getMessage());
                    }
                });//clear


                final List<IdAmountDocPair> t2 = one_user.second;
                final DocumentReference documentReference = groupUserCol.document(t1.getId());
                documentReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                AmountTypeDoc temp = documentSnapshot.toObject(AmountTypeDoc.class);
                                double new_amount = Objects.requireNonNull(temp).getAmount() + t1.getAmount();
                                documentReference.update("amount", new_amount)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("hi", "onSuccess: updated amount");

                                                final CollectionReference userSpecificLenders = documentReference.collection(res.getString(R.string.borrowersCollection));
                                                for (final IdAmountDocPair sec_person : t2) {
                                                    final DocumentReference second_doc_ref = userSpecificLenders.document(sec_person.getId());
                                                    second_doc_ref.get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if (!documentSnapshot.exists()) {
                                                                        AmountTypeDoc temp1 = new AmountTypeDoc(sec_person.getName(), sec_person.getAmount());
                                                                        userSpecificLenders.document(sec_person.getId()).set(temp1);
                                                                    } else {
                                                                        AmountTypeDoc temp1 = documentSnapshot.toObject(AmountTypeDoc.class);
                                                                        double new_amount = Objects.requireNonNull(temp1).getAmount() + sec_person.getAmount();
                                                                        if (new_amount != 0) {
                                                                            second_doc_ref.update("amount", new_amount)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Log.i("okay", "okay");
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Log.i("update error in borrow", Objects.requireNonNull(e.getMessage()));
                                                                                        }
                                                                                    });
                                                                        } else {
                                                                            second_doc_ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.i("okay", "onSuccess: okay");
                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.i("deletion error", Objects.requireNonNull(e.getMessage()));
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.i("borrowers problem", Objects.requireNonNull(e.getMessage()));
                                                                }
                                                            });
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("bye", Objects.requireNonNull(e.getMessage()));
                                            }
                                        });
                            }
                        });
            }
        }
        //clear

        if(toggle) {
            List<HashMap<String, IdAmountDocPair>> solutionHash = new ArrayList<>();

            for (int i = 0; i < solution.size(); i++)   //Updated amounts awed/bowrrowed in friendlists of users
            {
                IdAmountDocPair personA = solution.get(i).first;
                String personAid = personA.getId();
                solutionHash.add(new HashMap<String, IdAmountDocPair>());

                for (IdAmountDocPair personB2 : solution.get(i).second) {
                    solutionHash.get(i).put(personB2.getId(), personB2);

                    final IdAmountDocPair personB = personB2;
                    String personBid = personB.getId();

                    TwoAmountDoc friendsDoc2;

                    if (GroupId == null) {
                        friendsDoc2 = new TwoAmountDoc(personB.getName(), personB.getAmount(), personB.getAmount());
                    } else {
                        friendsDoc2 = new TwoAmountDoc(personB.getName(), personB.getAmount(), 0);
                    }

                    final TwoAmountDoc friendsDoc = friendsDoc2;

                    final DocumentReference friendsDocRef = userColRef.document(personAid)
                            .collection(res.getString(R.string.FriendsCollection))
                            .document(personBid);
                    friendsDocRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (!documentSnapshot.exists()) {
                                        friendsDocRef.set(friendsDoc);
                                    } else {
                                        friendsDocRef.update("amount",
                                                (Objects.requireNonNull(documentSnapshot.toObject(TwoAmountDoc.class)).getAmount() + friendsDoc.getAmount()));

                                        friendsDocRef.update("sec_amount",
                                                (Objects.requireNonNull(documentSnapshot.toObject(TwoAmountDoc.class)).getSec_amount() + friendsDoc.getSec_amount()));
                                    }

                                }
                            });
                }
            }
        }
    }


    public void settleGroup(final String groupId, final List<String> members, final Date date, final String Annih)
    {
        final CollectionReference myLendersRef= groupsRef.document(groupId).collection(res.getString(R.string.GroupUsers)).document(userId)
                .collection(res.getString(R.string.borrowersCollection));



        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {

                final String myName= documentSnapshot.toObject(AmountTypeDoc.class).getName();

                myLendersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<Pair<IdAmountDocPair, List< IdAmountDocPair> >> solution = new ArrayList<>();
                        List<IdAmountDocPair> myBorrowers= new ArrayList<>();
                        double myAmount=0;

                        for(DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots)
                        {
                            AmountTypeDoc borrowerDoc= documentSnapshot1.toObject(AmountTypeDoc.class);
                            IdAmountDocPair borrower= new IdAmountDocPair(documentSnapshot1.getId(),
                                    new AmountTypeDoc(borrowerDoc.getName(),borrowerDoc.getAmount()));

                            if(members.contains(borrower.getId()))
                            {
                                AmountTypeDoc myDoc= new AmountTypeDoc(myName, borrower.getAmount());
                                AmountTypeDoc theirDoc= new AmountTypeDoc(borrowerDoc.getName(), -1*borrowerDoc.getAmount());
                                IdAmountDocPair theirDocPair= new IdAmountDocPair(borrower.getId(), theirDoc);
                                myBorrowers.add(theirDocPair);
                                IdAmountDocPair myDocPair= new IdAmountDocPair(userId, myDoc);
                                List<IdAmountDocPair> theirBorrowers = new ArrayList<>();
                                theirBorrowers.add(myDocPair);
                                myAmount+= theirDoc.getAmount();
                                solution.add(Pair.create(borrower, theirBorrowers));

                            }


                        }

                        IdAmountDocPair myDocPair= new IdAmountDocPair(userId, new AmountTypeDoc(myName, myAmount));
                        solution.add(Pair.create(myDocPair, myBorrowers));

                        processSolutionForGroup(solution,groupId,myAmount,date,true,Annih);

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("", e.getMessage());
                            }
                        });

            }
        });


    }

    public void settle_non_group(final String friendsId, final Date date)
    {
        CollectionReference myGroupList= userRef.collection(res.getString(R.string.user_groups));

        myGroupList.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("", e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot group2 : queryDocumentSnapshots)
                        {

                            final DocumentSnapshot group= group2;
                            final DocumentReference friendsGroupUserRef= groupsRef.document(group.getId())
                                    .collection(res.getString(R.string.GroupUsers)).document(friendsId);

                            friendsGroupUserRef.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            if(documentSnapshot.exists())
                                            {
                                                final DocumentSnapshot documentSnapshot1= documentSnapshot;
                                                final AmountTypeDoc friendsAmountDoc= documentSnapshot.toObject(AmountTypeDoc.class);
                                                DocumentReference myRef= friendsGroupUserRef.collection(res.getString(R.string.borrowersCollection))
                                                        .document(userId);

                                                myRef.get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if(documentSnapshot.exists())
                                                                {
                                                                    List<Pair<IdAmountDocPair, List<IdAmountDocPair >>> solution= new ArrayList<>();

                                                                    AmountTypeDoc myAmountDoc= documentSnapshot.toObject(AmountTypeDoc.class);
                                                                    AmountTypeDoc myAmountDoc2= documentSnapshot.toObject(AmountTypeDoc.class);

                                                                    IdAmountDocPair myDoc= new IdAmountDocPair(documentSnapshot.getId(), myAmountDoc);
                                                                    List<IdAmountDocPair> myList= new ArrayList<>();


                                                                    AmountTypeDoc friendsAmountDoc2= new AmountTypeDoc(friendsAmountDoc.getName(), myAmountDoc.getAmount());
                                                                    IdAmountDocPair friendsDoc= new IdAmountDocPair(documentSnapshot1.getId(), friendsAmountDoc2);

                                                                    myList.add(friendsDoc);

                                                                    AmountTypeDoc friendsAmountDoc3= new AmountTypeDoc(friendsAmountDoc.getName(), myAmountDoc.getAmount());

                                                                    IdAmountDocPair myDoc2= new IdAmountDocPair(documentSnapshot.getId(), myAmountDoc2);
                                                                    myDoc2.setAmount(-1*myDoc2.getAmount());

                                                                    IdAmountDocPair friendsDoc2= new IdAmountDocPair(documentSnapshot1.getId(), friendsAmountDoc3);
                                                                    friendsDoc2.setAmount(-1*friendsDoc2.getAmount());
                                                                    List<IdAmountDocPair> friendsList= new ArrayList<>();
                                                                    friendsList.add(myDoc2);

                                                                    solution.add(Pair.create(myDoc, myList));
                                                                    solution.add(Pair.create(friendsDoc2, friendsList));
                                                                    Calendar today = Calendar.getInstance();
                                                                    today.set(Calendar.HOUR_OF_DAY, 0);
                                                                    processSolutionForGroup(solution,group.getId(),Math.abs(myDoc2.getAmount()),date,false,null);

                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.i("", e.getMessage());
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("", e.getMessage());
                                        }
                                    });




                        }
                    }
                });

        final DocumentReference friendUser = userRef.collection(res.getString(R.string.FriendsCollection)).document(friendsId);

        friendUser.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final TwoAmountDoc temp = documentSnapshot.toObject(TwoAmountDoc.class);
                        final double total_amount = temp.getAmount();
                        final double non_groupAmount = temp.getSec_amount();
                        friendUser.update("amount",0);
                        friendUser.update("sec_amount",0);
                        userRef.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        double new_amount = documentSnapshot.toObject(AmountTypeDoc.class).getAmount()-total_amount;
                                        userRef.update("amount",new_amount);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("non_group", e.getMessage());
                            }
                        });
                        final DocumentReference friend_user_depth_one = userColRef.document(friendsId);
                        final DocumentReference me_depth_two = friend_user_depth_one.collection(res.getString(R.string.FriendsCollection)).document(userId);
                        me_depth_two.update("amount",0);
                        me_depth_two.update("sec_amount",0);
                        friend_user_depth_one.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        AmountTypeDoc temp2 = documentSnapshot.toObject(AmountTypeDoc.class);
                                        friend_user_depth_one.update("amount",temp2.getAmount()+total_amount);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("non_group", e.getMessage());
                            }
                        });


                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.HOUR_OF_DAY, 0);
                        TransacDoc non_group_doc1 = new TransacDoc(null,"Non group settle up",Math.abs(non_groupAmount),"General",date);
                        final TransacDoc non_group_doc2 = new TransacDoc(null,"Non group settle up",Math.abs(non_groupAmount),"General",date);

                        userRef.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        List<Pair<IdAmountDocPair, List<IdAmountDocPair>>> solution= new ArrayList<>();
                                        IdAmountDocPair friend1= new IdAmountDocPair(friendsId, new AmountTypeDoc(temp.getName(), non_groupAmount));
                                        IdAmountDocPair me1 = new IdAmountDocPair(userId, new AmountTypeDoc(documentSnapshot.toObject(AmountTypeDoc.class).getName(),non_groupAmount));

                                        IdAmountDocPair friend2= new IdAmountDocPair(friend1.getId(), new AmountTypeDoc(friend1.getName(),-1*friend1.getAmount()));
                                        IdAmountDocPair me2= new IdAmountDocPair(me1.getId(), new AmountTypeDoc(me1.getName(), -1*me1.getAmount()));

                                        List<IdAmountDocPair> myList= new ArrayList<>();
                                        myList.add(friend2);

                                        List<IdAmountDocPair> friendsList= new ArrayList<>();
                                        friendsList.add(me1);

                                        solution.add(Pair.create(me2, myList));
                                        solution.add(Pair.create(friend1, friendsList));

                                        addActivities(solution, null, "General", "Non Group Settle Up", date);
                                    }
                                });


                        userRef.collection(res.getString(R.string.nonGroupTransactionCollection))
                                .document("General")
                                .collection(res.getString(R.string.TransactionItems))
                                .add(non_group_doc1)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        String transacId = documentReference.getId();
                                        friend_user_depth_one.collection(res.getString(R.string.nonGroupTransactionCollection))
                                                .document("General")
                                                .collection(res.getString(R.string.TransactionItems))
                                                .document(transacId).set(non_group_doc2);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("non_group", e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("non group", e.getMessage());
            }
        });
    }

    public void leave_group(final String groupID,final Date date)// this does not take any String as user id ; function is used by myself to leave a group
    {
        userRef.collection(res.getString(R.string.user_groups)).document(groupID)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                AmountTypeDoc temp = documentSnapshot.toObject(AmountTypeDoc.class);
                if(temp.getAmount()!=0)
                {
                    //can display this if given context; Toast.makeText(context, "you cannot leave the group", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    userRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                    final String who_left = documentSnapshot2.toObject(AmountTypeDoc.class).getName();
                                    final DocumentReference myDocInGroup = groupsRef.document(groupID).collection(res.getString(R.string.GroupUsers)).document(userId);
                                    myDocInGroup.collection(res.getString(R.string.borrowersCollection)).get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<String> final_settle=new ArrayList<>();
                                                    List<UserTransact> final_userTransact= new ArrayList<>();
                                                    double total_pos_amount=0;
                                                    for(DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots)
                                                    {
                                                        final_settle.add(documentSnapshot1.getId());
                                                        AmountTypeDoc temp = documentSnapshot1.toObject(AmountTypeDoc.class);
                                                        if(temp.getAmount()>0)
                                                        {
                                                            total_pos_amount=total_pos_amount+temp.getAmount();
                                                            UserTransact t1 = new UserTransact(documentSnapshot1.getId(),temp.getName(),temp.getAmount(),0);
                                                            final_userTransact.add(t1);
                                                        }
                                                        else
                                                        {
                                                            UserTransact t1 = new UserTransact(documentSnapshot1.getId(),temp.getName(),0,-1*temp.getAmount());
                                                            final_userTransact.add(t1);
                                                        }

                                                    }
                                                    settleGroup(groupID,final_settle,date,userId);

                                                    TransactionRecord t2 = new TransactionRecord(groupID,final_userTransact,
                                                            "Settle up because "+who_left+" has left the group",
                                                            total_pos_amount,"General",date);
                                                    processTransaction(t2);




                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("leave error", e.getMessage());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

        Log.i("leave error", e.getMessage());
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("leave failure", e.getMessage());
            }
        });
    }

    public void addActivities(List<Pair<IdAmountDocPair,List<IdAmountDocPair>>> solution, String groupId, String tag, String description, Date date)
    {
        for(Pair<IdAmountDocPair, List<IdAmountDocPair>> user1 : solution)
        {
            IdAmountDocPair targetUser= user1.first;

            String targetId= targetUser.getId();

            ActivityTypeDoc activity= new ActivityTypeDoc(groupId, description, targetUser.getAmount(), tag, date);

            userColRef.document(targetId).collection(res.getString(R.string.Activities))
                    .add(activity);

        }
    }


}
