package com.liminal.arexploreadmin.ui.aractivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.liminal.arexploreadmin.LocationBasedActivity;
import com.liminal.arexploreadmin.R;
import com.liminal.arexploreadmin.SelectActivityAdapter;

import java.util.ArrayList;
import java.util.List;

public class ModifyActivityFragment extends Fragment {

    // String to store selected AR activity
    static String selectedActivityID = "NA";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_ar_activity, container, false);

        //Setup recycler view within popup window
        List<LocationBasedActivity> activityARList = new ArrayList<>();
        RecyclerView modifyActivityRecyclerView = view.findViewById(R.id.activityRecyclerView);
        SelectActivityAdapter selectActivityAdapter = new SelectActivityAdapter(activityARList, position -> Toast.makeText(getActivity(), activityARList.get(position).activityName, Toast.LENGTH_SHORT).show());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        modifyActivityRecyclerView.setLayoutManager(mLayoutManager);
        modifyActivityRecyclerView.setAdapter(selectActivityAdapter);

        //Listen for values on Firebase
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Read data from firebase
                activityARList.clear();
                for (DataSnapshot activity : dataSnapshot.getChildren()) {
                    String activityID = activity.getKey();
                    String activityName = activity.child("name").getValue().toString();

                    LocationBasedActivity locationBasedActivity = new LocationBasedActivity(activityID, activityName);
                    activityARList.add(locationBasedActivity);
                }
                selectActivityAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Read failed
                Log.d("EAG_FIREBASE_DB", "Failed to read data from Firebase : ", databaseError.toException());
            }
        };
        FirebaseDatabase.getInstance().getReference().child("locationBasedActivityTable").addValueEventListener(eventListener);

        return view;
    }
}
