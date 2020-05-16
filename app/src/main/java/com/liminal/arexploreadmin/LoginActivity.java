package com.liminal.arexploreadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final DatabaseReference adminDetailsTableReference = FirebaseDatabase.getInstance().getReference().child("adminDetailsTable");

        final SharedPreferences userAccessSharedPreferences = getSharedPreferences("UserAccess", Context.MODE_PRIVATE);

        final Button loginButton = findViewById(R.id.loginButton);
        final EditText devKeyEditText = findViewById(R.id.devKeyEditText);

        loginButton.setOnClickListener(v -> {

            final String devKey = devKeyEditText.getText().toString();

            // Check for validity of dev key
            ValueEventListener devKeyListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot devKeys) {
                    // Dev key is valid
                    if (devKeys.hasChild(devKey)) {
                        // Save user access in shared preferences
                        userAccessSharedPreferences.edit().putString("access", (String) devKeys.child(devKey).child("access").getValue()).apply();
                        userAccessSharedPreferences.edit().putString("devKey", devKey).apply();
                        // Start main activity
                        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainActivityIntent);
                        // End login activity
                        finish();
                    }
                    // Dev key is invalid
                    else {
                        Toast.makeText(LoginActivity.this, "Developer key invalid", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Read failed
                    Log.d("EAG_FIREBASE_DB", "Failed to read data from Firebase : ", databaseError.toException());
                }
            };
            adminDetailsTableReference.addValueEventListener(devKeyListener);
        });

    }
}
