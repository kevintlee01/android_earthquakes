package com.example.earthquakes;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new SettingsFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().getFragments().get(0);
        //Update values if back button is pressed
        fragment.updateSettingValues();
    }
}
