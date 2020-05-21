package com.liminal.arexploreadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences userAccessSharedPreferences = getSharedPreferences("UserAccess", Context.MODE_PRIVATE);

        final String access = userAccessSharedPreferences.getString("access", "NONE");

        CardView mapButton = findViewById(R.id.mapsCardView);
        CardView rewardsButton = findViewById(R.id.rewardsCardView);
        CardView challengesButton = findViewById(R.id.challengesCardView);
        CardView statisticsButton = findViewById(R.id.statisticsCardView);
        CardView activityARButton = findViewById(R.id.activityCardView);

        // Initiate control panel for Map
        mapButton.setOnClickListener(v -> {
            if(access.equals("ALL") || access.equals("NAVIGATOR"))
            {
                Intent mapActivityIntent = new Intent(MainActivity.this, MapControlPanelActivity.class);
                startActivity(mapActivityIntent);
            }
            else
            {
                Toast.makeText(this, "Access Denied.", Toast.LENGTH_SHORT).show();
            }
        });

        // Initiate control panel for AR activities
        activityARButton.setOnClickListener(v -> {
            if(access.equals("ALL"))
            {
                Intent activityARActivityIntent = new Intent(MainActivity.this, ARControlPanelActivity.class);
                startActivity(activityARActivityIntent);
            }
            else
            {
                Toast.makeText(this, "Access Denied.", Toast.LENGTH_SHORT).show();
            }
        });

        // Initiate control panel for Reward
        rewardsButton.setOnClickListener(v -> {
            if(access.equals("ALL") || access.equals("VENDOR"))
            {
                Toast.makeText(this, "Coming Soon ...", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Access Denied.", Toast.LENGTH_SHORT).show();
            }
        });

        // Initiate control panel for statistics
        statisticsButton.setOnClickListener(v -> {
            if(access.equals("ALL"))
            {
                Intent statisticsActivityIntent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(statisticsActivityIntent);
            }
            else
            {
                Toast.makeText(this, "Access Denied.", Toast.LENGTH_SHORT).show();
            }
        });

        // Initiate control panel for challenges
        challengesButton.setOnClickListener(v -> {
            if(access.equals("ALL"))
            {
                Toast.makeText(this, "Coming Soon ...", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Access Denied.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
