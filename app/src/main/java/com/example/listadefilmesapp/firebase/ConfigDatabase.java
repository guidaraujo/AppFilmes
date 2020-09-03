package com.example.listadefilmesapp.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigDatabase {

    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    public static final DatabaseReference REFERENCE = FirebaseDatabase.getInstance().getReference();

    public String sUrl;

    

}
