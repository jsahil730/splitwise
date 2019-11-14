package com.example.splitwise;

import com.google.firebase.firestore.Exclude;

public class IdTypeDoc {

    private String name;
    private String docID;

    public IdTypeDoc(){}

    public IdTypeDoc(String name, String userID) {
        this.name = name;
        this.docID = userID;
    }

    public String getName() {
        return name;
    }

    @Exclude
    public String getDocID() {
        return docID;
    }
}
