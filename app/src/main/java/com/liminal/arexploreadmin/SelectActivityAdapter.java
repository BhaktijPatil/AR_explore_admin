package com.liminal.arexploreadmin;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectActivityAdapter extends RecyclerView.Adapter<SelectActivityAdapter.MyViewHolder> {

    private List<LocationBasedActivity> activityARList;
    private MapControlPanelActivity.ClickListener clickListener;

    private DatabaseReference locationBasedActivityTableReference;
    private Context context;
    private SharedPreferences selectedActivitySharedPreferences;



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_select_activity, parent, false);

        context = parent.getContext();
        locationBasedActivityTableReference = FirebaseDatabase.getInstance().getReference().child("locationBasedActivityTable");
        selectedActivitySharedPreferences = context.getSharedPreferences("Selected_Activity", Context.MODE_PRIVATE);

        return new MyViewHolder(itemView, clickListener);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        LocationBasedActivity activity = activityARList.get(position);

        ValueEventListener iconLinkListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get activity name
                String activityName =  dataSnapshot.child(activity.activityID).child("name").getValue().toString();
                // Set activity icon
                Glide.with(context).load(Uri.parse(dataSnapshot.child(activity.activityID).child("iconLink").getValue().toString())).into(holder.activityIcon);

                // Set activity name to button text
                holder.selectActivityButton.setText(activityName);
                // Choose activity on button press
                holder.selectActivityButton.setOnClickListener(v -> {
                    MapControlPanelActivity.selectedActivityID = activity.activityID;
                    Toast.makeText(context, activity.activityName + " has been set as current Activity.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Read failed
                Log.d("EAG_FIREBASE_DB", "Failed to read data from Firebase : ", databaseError.toException());
            }
        };
        locationBasedActivityTableReference.addListenerForSingleValueEvent(iconLinkListener);
    }



    @Override
    public int getItemCount() {
        return activityARList.size();
    }



    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        Button selectActivityButton;
        ImageView activityIcon;

        private WeakReference<MapControlPanelActivity.ClickListener> listenerRef;

        MyViewHolder(@NonNull View itemView, MapControlPanelActivity.ClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);

            selectActivityButton = itemView.findViewById(R.id.activityButton);
            activityIcon = itemView.findViewById(R.id.activityIcon);
        }
        @Override
        public void onClick(View view) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    SelectActivityAdapter(List<LocationBasedActivity> activityARList, MapControlPanelActivity.ClickListener clickListener){
        this.activityARList = activityARList;
        this.clickListener = clickListener;
    }

}
