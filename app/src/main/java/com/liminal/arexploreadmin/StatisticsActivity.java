package com.liminal.arexploreadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TextView accountsCountTextView = findViewById(R.id.accountsCountTextView);
        TextView liveUpdatesCountTextView = findViewById(R.id.liveUpdatesCountTextView);
        TextView markersCountTextView = findViewById(R.id.markersPlacedCountTextView);
        TextView offersCountTextView = findViewById(R.id.offersCountTextView);


        // Listen for values on Firebase
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                accountsCountTextView.setText(String.valueOf(dataSnapshot.child("userProfileTable").getChildrenCount()));
                liveUpdatesCountTextView.setText(String.valueOf(dataSnapshot.child("liveUpdatesTable").getChildrenCount()));

                // Calculate number of markers placed
                long markerCount = 0;
                for(DataSnapshot activity : dataSnapshot.child("locationBasedActivityTable").getChildren())
                {
                    markerCount += activity.child("locations").getChildrenCount();
                }
                markersCountTextView.setText(String.valueOf(markerCount));

                // Calculate number of offers in circulation
                long offerCount = 0;
                for(DataSnapshot category : dataSnapshot.child("rewardsTable").getChildren())
                {
                    for(DataSnapshot offer : category.getChildren())
                    {
                        Log.d("hii", String.valueOf(offer.child("quantity").getValue()));
                        offerCount += (long) offer.child("quantity").getValue();
                    }
                }
                offersCountTextView.setText(String.valueOf(offerCount));

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Read failed
                Log.d("EAG_FIREBASE_DB", "Failed to read data from Firebase : ", databaseError.toException());
            }
        };
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(eventListener);

    }
}
