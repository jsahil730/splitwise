package com.example.splitwise;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class IdTypeDoc {
    private String name;
    public IdTypeDoc(){}

    IdTypeDoc(@NonNull String name) {
        this.name = name;

    }

    @NonNull
    public String getName() {
        return name;
    }


}
