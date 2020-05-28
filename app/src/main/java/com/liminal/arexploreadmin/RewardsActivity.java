package com.liminal.arexploreadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RewardsActivity extends AppCompatActivity {

    private List<RewardDetails> rewardDetailsList = new ArrayList<>();
    private RewardsAdapter rewardsAdapter;
    private DatabaseReference databaseReference;
    private String adminID;
    private int count = 0;
    private String adminCode = "XYZ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        rewardsAdapter = new RewardsAdapter(rewardDetailsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rewardsAdapter);

        adminID = "109268557311186411365";
        databaseReference = FirebaseDatabase.getInstance().getReference().child("rewardsTable");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Read data from firebase
                rewardDetailsList.clear();
                count = 0;
                for (DataSnapshot category : dataSnapshot.getChildren()) {
                    String category_name = category.getKey();
                    for (DataSnapshot reward : category.getChildren()) {
                        if (!Objects.requireNonNull(reward.child("adminID").getValue()).toString().equals(adminID))
                            continue;
                        String rid = reward.getKey();
                        String title = Objects.requireNonNull(reward.child("title").getValue()).toString();
                        String description = Objects.requireNonNull(reward.child("description").getValue()).toString();
                        long cost = (long) reward.child("cost").getValue();
                        long quantity = (long) reward.child("quantity").getValue();
                        RewardDetails rewardDetails = new RewardDetails(rid, category_name, title, description, cost, quantity);
                        rewardDetailsList.add(rewardDetails);
                        count++;
                        assert rid != null;
                        Log.d("Rewards_Activity", rid);
                    }
                }
                rewardsAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Read failed
                Log.d("EAG_FIREBASE_DB", "Failed to read data from Firebase : ", databaseError.toException());
            }
        };

        databaseReference.addValueEventListener(eventListener);

        FloatingActionButton fab = findViewById(R.id.newRewardsButton);
        fab.setOnClickListener(dialog -> createPopUpDialog(this));
    }

    private void createPopUpDialog(Context context) {
        final Dialog inputTextDialog = new Dialog(context);
        inputTextDialog.setContentView(R.layout.dialog_reward_input);

        Button cancelButton = inputTextDialog.findViewById(R.id.cancelButton);
        Button uploadButton = inputTextDialog.findViewById(R.id.uploadButton);

        EditText title = inputTextDialog.findViewById(R.id.title);
        EditText description = inputTextDialog.findViewById(R.id.description);
        EditText cost = inputTextDialog.findViewById(R.id.cost);
        EditText quantity = inputTextDialog.findViewById(R.id.quantity);
        Spinner spinner = inputTextDialog.findViewById(R.id.categories);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        cancelButton.setOnClickListener(view -> inputTextDialog.dismiss());
        uploadButton.setOnClickListener(v -> {
            uploadText(String.valueOf(spinner.getSelectedItem()), String.valueOf(title.getText()),String.valueOf(description.getText()),String.valueOf(cost.getText()),String.valueOf(quantity.getText()));
            inputTextDialog.dismiss();
        });

        inputTextDialog.show();
    }

    private void uploadText(String category, String title, String description, String cost, String quantity) {
        int code = 100 + count;
        String rid = adminCode + category + code;
        Map<Object, Object> map = new HashMap<>();
        map.put("title",title);
        map.put("description",description);
        map.put("cost",Long.parseLong(cost));
        map.put("quantity",Long.parseLong(quantity));
        map.put("adminID",adminID);
        databaseReference.child(category).child(rid).setValue(map);
    }
}