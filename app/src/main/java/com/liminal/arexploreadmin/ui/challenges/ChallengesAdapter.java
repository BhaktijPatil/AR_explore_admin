package com.liminal.arexploreadmin.ui.challenges;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.liminal.arexploreadmin.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.MyViewHolder>{

    private List<Challenge> challengeList;
    private Context context;
    private DatabaseReference databaseReference;
    private String challengeType;

    @NonNull
    @Override
    public ChallengesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_challenge, parent, false);
        context = parent.getContext();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("challengesTable").child(challengeType);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengesAdapter.MyViewHolder holder, int position) {
        Challenge challenge = challengeList.get(position);
        holder.activityID.setText("Activity ID : " + challenge.activityID);
        holder.challengePosition.setText("Challenge Position : " + challenge.challengePosition);
        holder.description.setText("Description : " + challenge.description);
        holder.rewardPoints.setText("Reward Points : " + challenge.rewardPoints);
        holder.rewardType.setText("Reward Type : " + challenge.rewardType);
        holder.stat.setText("Stat : " + challenge.stat);
        holder.target.setText("Target : " + challenge.target);
        holder.timestamp_start.setText("TS Start : " + challenge.timestampStart);
        holder.timestamp_end.setText("TS End : " + challenge.timestampEnd);

        holder.delete.setOnClickListener(view -> databaseReference.child(String.valueOf(position + 1)).removeValue());
        // Setting position + 1 because firebase entries start from key value 1
        holder.update.setOnClickListener(view -> updateChallenge(challenge, position + 1));
    }

    private void updateChallenge(Challenge challenge, int position) {
        final Dialog inputTextDialog = new Dialog(context);
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
                        ArrayAdapter<String> activitiesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, activities);
                        activitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        activityID.setAdapter(activitiesAdapter);
                        activityID.setSelection(activities.indexOf(challenge.activityID));
                        activityID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String item = adapterView.getItemAtPosition(i).toString();
                                List<String> stats = new ArrayList<>();
                                for(DataSnapshot stat1 : dataSnapshot.child(item).child("stat").getChildren())
                                    stats.add((String) stat1.getValue());
                                ArrayAdapter<String> statsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, stats);
                                statsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                stat.setAdapter(statsAdapter);
                                stat.setSelection(stats.indexOf(challenge.stat));
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
        ArrayAdapter<String> rewardTypesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, rewardTypes);
        rewardTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rewardType.setAdapter(rewardTypesAdapter);

        rewardType.setSelection(rewardTypes.indexOf(challenge.rewardType));

        challengePosition.setText(String.valueOf(challenge.challengePosition));
        description.setText(challenge.description);
        rewardPoints.setText(String.valueOf(challenge.rewardPoints));
        target.setText(String.valueOf(challenge.target));
        ts_start_tv.setText(String.valueOf(challenge.timestampStart));

        ts_start.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int currYear = calendar.get(Calendar.YEAR);
            int currMonth = calendar.get(Calendar.MONTH);
            int currDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Setup DatePicker Dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (v, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        if(challengeType.equals("daily") || (challengeType.equals("weekly") && (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)))
                            ts_start_tv.setText(String.valueOf((calendar.getTimeInMillis() / 1000) * 1000));
                        else
                            Toast.makeText(context, "Weekly Challenges can only start on Mondays", Toast.LENGTH_SHORT).show();
                    }
                    , currYear, currMonth, currDay);
            datePickerDialog.show();

        });

        cancelButton.setOnClickListener(view -> inputTextDialog.dismiss());
        uploadButton.setOnClickListener(v -> {
            uploadChallenge(position,
                    String.valueOf(activityID.getSelectedItem()),
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

    private void uploadChallenge(int position, String activityID, long challengePosition, String description, long rewardPoints, String rewardType, String stat, long target, long timestampStart){
        long timestamp_difference = 86400000;
        if(challengeType.equals("weekly"))
            timestamp_difference = timestamp_difference * 7;
        Challenge challenge = new Challenge(activityID, challengePosition, description,
                rewardPoints, rewardType, stat, target, timestampStart, timestampStart + timestamp_difference);
        databaseReference.child(String.valueOf(position)).setValue(challenge);

    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView activityID, challengePosition, description, rewardPoints, rewardType, stat, target, timestamp_start, timestamp_end;
        Button update, delete;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            activityID = itemView.findViewById(R.id.activityID);
            challengePosition = itemView.findViewById(R.id.challengePosition);
            description = itemView.findViewById(R.id.description);
            rewardPoints = itemView.findViewById(R.id.rewardPoints);
            rewardType = itemView.findViewById(R.id.rewardType);
            stat = itemView.findViewById(R.id.stat);
            target = itemView.findViewById(R.id.target);
            timestamp_start = itemView.findViewById(R.id.timestamp_start);
            timestamp_end = itemView.findViewById(R.id.timestamp_end);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);

        }
    }

    ChallengesAdapter(List<Challenge> challengeList, String challengeType){
        this.challengeList = challengeList;
        this.challengeType = challengeType;
    }
}
