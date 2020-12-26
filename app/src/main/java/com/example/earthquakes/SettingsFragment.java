package com.example.earthquakes;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import static com.example.earthquakes.Shared.USER_NAME;
import static com.example.earthquakes.Shared.NORTH_COORD;
import static com.example.earthquakes.Shared.SOUTH_COORD;
import static com.example.earthquakes.Shared.EAST_COORD;
import static com.example.earthquakes.Shared.WEST_COORD;
import static com.example.earthquakes.Shared.MAGNITUDE_HIGHLIGHT;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static SharedPreferences sharedPreferences;
    private static EditTextPreference username_etp;
    private static EditTextPreference north_etp;
    private static EditTextPreference south_etp;
    private static EditTextPreference east_etp;
    private static EditTextPreference west_etp;
    private static EditTextPreference magnitude_etp;
    private static final String USERNAME_KEY = "Username";
    private static final String NORTH_KEY = "North";
    private static final String SOUTH_KEY = "South";
    private static final String EAST_KEY = "East";
    private static final String WEST_KEY = "West";
    private static final String MAGNITUDE_KEY = "Magnitude Highlight";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.user_settings, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        initializeEditTextPreference();

        Preference myPref = (Preference) findPreference("Validate");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                updateSettingValues();
                return false;
            }
        });
    }

    public void initializeEditTextPreference() {
        username_etp = (EditTextPreference) findPreference(USERNAME_KEY);
        //Gets Username Field
        username_etp.setSummary(sharedPreferences.getString(USERNAME_KEY, "None"));
        //Check if EditTextPreference Changed and update Summary Values
        username_etp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = o.toString();
                newValue = newValue.replace("\n", "").replace("\r", "");
                username_etp.setSummary(newValue);
                return true;
            }
        });

        north_etp = (EditTextPreference) findPreference(NORTH_KEY);
        north_etp.setSummary(sharedPreferences.getString(NORTH_KEY, "0.0"));
        //Check if EditTextPreference Changed and update Summary Values
        north_etp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = o.toString();
                newValue = newValue.replace("\n", "").replace("\r", "");
                north_etp.setSummary(newValue);
                return true;
            }
        });

        south_etp = (EditTextPreference) findPreference(SOUTH_KEY);
        south_etp.setSummary(sharedPreferences.getString(SOUTH_KEY, "0.0"));
        //Check if EditTextPreference Changed and update Summary Values
        south_etp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = o.toString();
                newValue = newValue.replace("\n", "").replace("\r", "");
                south_etp.setSummary(newValue);
                return true;
            }
        });

        east_etp = (EditTextPreference) findPreference(EAST_KEY);
        east_etp.setSummary(sharedPreferences.getString(EAST_KEY, "0.0"));
        //Check if EditTextPreference Changed and update Summary Values
        east_etp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = o.toString();
                newValue = newValue.replace("\n", "").replace("\r", "");
                east_etp.setSummary(newValue);
                return true;
            }
        });

        west_etp = (EditTextPreference) findPreference(WEST_KEY);
        west_etp.setSummary(sharedPreferences.getString(WEST_KEY, "0.0"));
        //Check if EditTextPreference Changed and update Summary Values
        west_etp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = o.toString();
                newValue = newValue.replace("\n", "").replace("\r", "");
                west_etp.setSummary(newValue);
                return true;
            }
        });

        magnitude_etp = (EditTextPreference) findPreference(MAGNITUDE_KEY);
        magnitude_etp.setSummary(sharedPreferences.getString(MAGNITUDE_KEY, "0.0"));
        //Check if EditTextPreference Changed and update Summary Values
        magnitude_etp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String newValue = o.toString();
                newValue = newValue.replace("\n", "").replace("\r", "");
                magnitude_etp.setSummary(newValue);
                return true;
            }
        });
    }

    //Updates EditTextPreference summary and text values
    public void updateSettingValues(){
        String username_val = validateValues(USERNAME_KEY, username_etp.getText());
        username_etp.setText(username_val);
        username_etp.setSummary(username_val);

        String north_val = validateValues(NORTH_KEY, north_etp.getText());
        north_etp.setText(north_val);
        north_etp.setSummary(north_val);

        String south_val = validateValues(SOUTH_KEY, south_etp.getText());
        south_etp.setText(south_val);
        south_etp.setSummary(south_val);

        String east_val = validateValues(EAST_KEY, east_etp.getText());
        east_etp.setText(east_val);
        east_etp.setSummary(east_val);

        String west_val = validateValues(WEST_KEY, west_etp.getText());
        west_etp.setText(west_val);
        west_etp.setSummary(west_val);

        String magnitude_val = validateValues(MAGNITUDE_KEY, magnitude_etp.getText());
        magnitude_etp.setText(magnitude_val);
        magnitude_etp.setSummary(magnitude_val);

        //Store values
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, username_val);
        editor.putString(NORTH_COORD, north_val);
        editor.putString(SOUTH_COORD, south_val);
        editor.putString(EAST_COORD, east_val);
        editor.putString(WEST_COORD, west_val);
        editor.putString(MAGNITUDE_HIGHLIGHT, magnitude_val);
        editor.commit();
    }

    public String validateValues(String type, String input) {
        String result = "";

        if(type == USERNAME_KEY) {
            //Checks username is only characters and numbers
            if (!input.matches("[A-Za-z0-9]+")) {
                result = "Invalid";
            }
            else {
                result = input;
            }
        }
        else if (type == NORTH_KEY || type == SOUTH_KEY || type == EAST_KEY || type == WEST_KEY) {
            //Checks if direction is valid number
            Double dir_val = Shared.strToDouble(input);
            result = String.valueOf(dir_val);
        }
        else if (type == MAGNITUDE_KEY) {
            //Checks if magnitude is valid number and checks bounds 0 <= magnitude <= 10
            Double magnitude_val = Shared.strToDouble(input);

            if (magnitude_val < 0) {
                result = "0.0";
            }
            else if(magnitude_val > 10) {
                result = "10.0";
            }
            else {
                result = String.valueOf(magnitude_val);
            }
        }

        return result;
    }
}