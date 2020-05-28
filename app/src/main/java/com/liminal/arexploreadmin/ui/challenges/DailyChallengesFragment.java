package com.liminal.arexploreadmin.ui.challenges;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.liminal.arexploreadmin.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyChallengesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChallengesAdapter challengesAdapter;
    private List<Challenge> challengeList = new ArrayList<>();
    private FloatingActionButton floatingActionButton;
    private DatabaseReference databaseReference;
    private long challengeID = 0;
    private final int CHALLENGE_COUNT = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_daily_challenges, container, false);
        floatingActionButton = root.findViewById(R.id.fab);
        recyclerView = root.findViewById(R.id.daily_recycler_view);
        challengesAdapter = new ChallengesAdapter(challengeList, "daily");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(challengesAdapter);
        floatingActionButton.setOnClickListener(view -> addNewChallenge());
        return root;
    }

    private void addNewChallenge() {
        final Dialog inputTextDialog = new Dialog(getContext());
        inputTextDialog.setContentView(R.layout.dialog_challenge_add);

        Button cancelButton = inputTextDialog.findViewById(R.id.cancelButton);
        Button uploadButton = inputTextDialog.findViewById(R.id.uploadButton);

        EditText challengePosition = inputTextDialog.findViewById(R.id.challengePosition);
        EditText description = inputTextDialog.findViewById(R.id.description);
        EditText rewardPoints = inputTextDialog.findViewById(R.id.rewardPoints);
        EditText target = inputTextDialog.findViewById(R.id.target);

        Spinner stat = inputTextDialog.findViewById(R.id.stat);
        Spinner activityID = inputTextDialog.findViewById(R.id.activityID);
        Spinner rewardType = inputTextDialog.findViewById(R.id.rewardType);

        Button ts_start = inputTextDialog.findViewById(R.id.timestamp_start);
        TextView ts_start_tv = inputTextDialog.findViewById(R.id.timestamp_start_textview);

        FirebaseDatabase.getInstance().getReference().child("locationBasedActivityTable")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> activities = new ArrayList<>();
                for(DataSnapshot activity : dataSnapshot.getChildren())
                    activities.add(activity.getKey());
                ArrayAdapter<String> activitiesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, activities);
                activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                activityID.setAdapter(activitiesAdapter);
                activityID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String item = adapterView.getItemAtPosition(i).toString();
                        List<String> stats = new ArrayList<>();
                        for(DataSnapshot stat1 : dataSnapshot.child(item).child("stat").getChildren())
                            stats.add((String) stat1.getValue());
                        ArrayAdapter<String> statsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, stats);
                        statsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        stat.setAdapter(statsAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        List<String> rewardTypes = new ArrayList<>();
        rewardTypes.add("coins");
        rewardTypes.add("tickets");
        ArrayAdapter<String> rewardTypesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, rewardTypes);
        rewardTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rewardType.setAdapter(rewardTypesAdapter);

        ts_start.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int currYear = calendar.get(Calendar.YEAR);
            int currMonth = calendar.get(Calendar.MONTH);
            int currDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Setup DatePicker Dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    (v, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        ts_start_tv.setText(String.valueOf((calendar.getTimeInMillis()/1000)* 1000));
                }
            , currYear, currMonth, currDay);
            datePickerDialog.show();

        });

        cancelButton.setOnClickListener(view -> inputTextDialog.dismiss());
        uploadButton.setOnClickListener(v -> {
            uploadChallenge(String.valueOf(activityID.getSelectedItem()),
                    Long.parseLong(String.valueOf(challengePosition.getText())),
                    String.valueOf(description.getText()),
                    Long.parseLong(String.valueOf(rewardPoints.getText())),
                    String.valueOf(rewardType.getSelectedItem()),
                    String.valueOf(stat.getSelectedItem()),
                    Long.parseLong(String.valueOf(target.getText())),
                    Long.parseLong(String.valueOf(ts_start_tv.getText()))
            );
            inputTextDialog.dismiss();
        });

        inputTextDialog.show();
    }

    private void uploadChallenge(String activityID, long challengePosition, String description, long rewardPoints, String rewardType, String stat, long target, long timestampStart){
//        //Calculate challenge position
//        long challengePosition = challengeID % CHALLENGE_COUNT;
//        if(challengePosition == 0)
//            challengePosition = CHALLENGE_COUNT;
        //86400000 is the number of milliseconds in a day
        Challenge challenge = new Challenge(activityID, challengePosition, description,
                rewardPoints, rewardType, stat, target, timestampStart, timestampStart + 86400000);
        databaseReference.child(String.valueOf(challengeID)).setValue(challenge);

    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("challengesTable").child("daily");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                challengeList.clear();
                challengeID = dataSnapshot.getChildrenCount() + 1;
                for(DataSnapshot challengeSnapshot : dataSnapshot.getChildren()){
                    String activityID = (String) challengeSnapshot.child("activityID").getValue();
                    long challengePosition = (long) challengeSnapshot.child("challengePosition").getValue();
                    String description = (String) challengeSnapshot.child("description").getValue();
                    long rewardPoints = (long) challengeSnapshot.child("rewardPoints").getValue();
                    String rewardType = (String) challengeSnapshot.child("rewardType").getValue();
                    String stat = (String) challengeSnapshot.child("stat").getValue();
                    long target = (long) challengeSnapshot.child("target").getValue();
                    long ts_start = (long) challengeSnapshot.child("timestampStart").getValue();
                    long ts_end = (long) challengeSnapshot.child("timestampEnd").getValue();
                    challengeList.add(new Challenge(activityID, challengePosition,
                            description, rewardPoints, rewardType, stat, target, ts_start, ts_end));
                }
                challengesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }
}
