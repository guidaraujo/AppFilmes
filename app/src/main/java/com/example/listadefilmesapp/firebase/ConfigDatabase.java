package com.example.listadefilmesapp.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigDatabase {

    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    

}
