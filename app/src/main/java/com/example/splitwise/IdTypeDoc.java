package com.example.splitwise;

import com.google.firebase.firestore.Exclude;

public class IdTypeDoc {

    private String name;


    public IdTypeDoc(){}

    public IdTypeDoc(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }


}
