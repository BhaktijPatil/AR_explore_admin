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

        // Initiate control panel for Map
        mapButton.setOnClickListener(v -> {
            if(access.equals("ALL") || access.equals("NAVIGATOR"))
            {
                Intent mapActivityIntent = new Intent(MainActivity.this, MapControlPanelActivity.class);
                startActivity(mapActivityIntent);
            }
        });

        // Initiate control panel for Reward
        rewardsButton.setOnClickListener(v -> {
            if(access.equals("ALL") || access.equals("VENDOR"))
            {
                Intent rewardsActivityIntent = new Intent(MainActivity.this, MapControlPanelActivity.class);
                startActivity(rewardsActivityIntent);
            }
        });



    }
}
