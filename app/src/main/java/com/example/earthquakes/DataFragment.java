package com.example.earthquakes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import static com.example.earthquakes.Shared.HEADER;
import static com.example.earthquakes.Shared.USER_NAME;
import static com.example.earthquakes.Shared.NORTH_COORD;
import static com.example.earthquakes.Shared.SOUTH_COORD;
import static com.example.earthquakes.Shared.EAST_COORD;
import static com.example.earthquakes.Shared.WEST_COORD;
import static com.example.earthquakes.Shared.MAGNITUDE_HIGHLIGHT;
import static com.example.earthquakes.Shared.LONGITUDE;
import static com.example.earthquakes.Shared.LATITUDE;
import static com.example.earthquakes.Shared.MAGNITUDE_VAL;

public class DataFragment extends Fragment {
    private static String json_string;
    private static JSONObject jo;
    private static Double highlight_earthquake = 8.0;
    private static ListView earthquake_listview;
    private static SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        earthquake_listview = view.findViewById(R.id.listview_earthquakes);

        //Disable back button in toolbar
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        return view;
    }

    public void onResume() {
        super.onResume();

        //Initializes empty ArrayAdapter with color coding and stores into ListView
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(), R.layout.content_earthquake_list, R.id.textView, new ArrayList<String>()){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemView = super.getView(position, convertView, parent);
                //Log.i("ListView Output", getItem(position));

                //Reads each TextView inside ListView line by line
                Scanner scanner = new Scanner(getItem(position));
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if(line.toLowerCase().contains("magnitude")) {
                        try {
                            if (Double.parseDouble(line.toLowerCase().replace("magnitude: ", "")) >= highlight_earthquake) {
                                //Set highlight red color
                                itemView.setBackgroundColor(Color.rgb(255, 200, 200));
                            } else {
                                itemView.setBackgroundColor(Color.WHITE);
                            }
                        }
                        catch(NumberFormatException e) {
                            e.printStackTrace();
                        }

                    }
                }

                return itemView;
            }
        };
        earthquake_listview.setAdapter(aa);

        earthquake_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = earthquake_listview.getItemAtPosition(position);
                //Get TextView of ListView clicked
                String str = (String)o;
                //Log.i("ListView Clicked", str);

                //If string is not an error
                if(!str.contains("Error")) {
                    Scanner scanner = new Scanner(str);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        //Finds and stores latitude and longitutde values to be sent to MapFragment
                        if (line.contains("Coordinates")) {
                            String[] coord_list = line.replace("Coordinates: ", "").split(",");
                            Double lng = Shared.strToDouble(coord_list[1]);
                            Double lat = Shared.strToDouble(coord_list[0]);

                            editor.putFloat(LONGITUDE, lng.floatValue());
                            editor.putFloat(LATITUDE, lat.floatValue());
                        }

                        //Finds and stores magnitude value to be sent to MapFragment
                        if (line.contains("Magnitude")) {
                            Double mag_val = Shared.strToDouble(line.replace("Magnitude: ", ""));
                            editor.putFloat(MAGNITUDE_VAL, mag_val.floatValue());
                        }
                    }
                    editor.commit();

                    //Navigate to MapFragment
                    NavHostFragment.findNavController(DataFragment.this).navigate(R.id.action_DataFragment_to_MapsFragment);
                }
            }
        });

        new HTTPReqTask().execute();
    }

    private class HTTPReqTask extends AsyncTask<Void, String, Void> {
        ArrayAdapter<String> async_earthquake_data_list;

        @Override
        protected void onPreExecute() {
            async_earthquake_data_list = (ArrayAdapter<String>)earthquake_listview.getAdapter();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Loads stored data from SettingsFragment
            String username = sharedPreferences.getString(USER_NAME, "0");
            String north = sharedPreferences.getString(NORTH_COORD, "0");
            String south = sharedPreferences.getString(SOUTH_COORD, "0");
            String east = sharedPreferences.getString(EAST_COORD, "0");
            String west = sharedPreferences.getString(WEST_COORD, "0");
            highlight_earthquake = Double.parseDouble(sharedPreferences.getString(MAGNITUDE_HIGHLIGHT, "0"));

            //Sets URL
            String api_url = "http://api.geonames.org/earthquakesJSON?formatted=true&north=" + north + "&south=" + south + "&east=" + east + "&west=" + west + "&username=" + username;
            Log.i("API URL", api_url);
            //Gets JSON text from URL and stores into string
            json_string = getContentFromUrl(api_url);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Adds new data in ArrayAdapter to ListView
            for(String line: parseJSON(json_string)) {
                async_earthquake_data_list.add(line);
            }
        }

        //Reads URL content and stores into string
        public String getContentFromUrl(String url) {
            StringBuilder content = new StringBuilder();

            try {
                //Establishes URL connection
                URL u = new URL(url);
                HttpURLConnection uc = (HttpURLConnection) u.openConnection();
                if(uc.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    //Reads content of URL
                    InputStream is = uc.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    String line;

                    //Stores each line into string
                    while((line = br.readLine()) != null) {
                        content.append(line);
                    }
                }
                else {
                    Log.e("URL Access", "There was a problem accessing the URL!");
                    return "{\"status\": {\"message\": \"Username Problem\"}}";
                }

                return content.toString();
            }
            catch(StackOverflowError | Exception s) {
                s.printStackTrace();
            }
            catch(Error e) {
                e.printStackTrace();
            }

            return "";
        }

        //Converts JSON string format to line by line string array
        public ArrayList<String> parseJSON(String json_string) {
            ArrayList<String> json_data = new ArrayList<String>();
            try {
                //Read string from API output and converts to JSON Object
                jo = new JSONObject(json_string);

                //Upack JSON Object to JSON Array
                if(jo.has(HEADER)) {
                    JSONArray ja = jo.getJSONArray(HEADER);
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject obj = ja.getJSONObject(i);
                        Iterator<String> keys = obj.keys();
                        String data = "";

                        //Iterate through all keys of JSON Array
                        while (keys.hasNext()) {
                            String key = (String) keys.next();

                            //Check if JSON value is String or Double
                            if (obj.get(key) instanceof String) {
                                data += key + ": " + obj.getString(key);
                                //Log.i("Output", obj.getString(key));
                            }
                            else {
                                data += key + ": " + String.valueOf(obj.getDouble(key));
                                //Log.i("Output", String.valueOf(obj.getDouble(key)));
                            }

                            if (keys.hasNext()) {
                                data += "\n";
                            }
                        }

                        //Write Data to ListView
                        Log.i("Output", data);
                        json_data.add(reformatText(data));
                    }
                }
                else if (jo.has("status")) {
                    json_data.add("Error: Please Check the Username and Try Again.");
                }
            }
            catch(JSONException err) {
                err.printStackTrace();
            }

            if(json_data.size() == 0) {
                //If JSON size is 0 show no data if connected to network or prompt user network connection issue
                if(isNetworkConnected()) {
                    json_data.add("Error: No Data Available.\nPlease Choose Different Coordinates Under Settings.");
                }
                else {
                    json_data.add("Error: Please Check Internet Connection.");
                }
            }
            return json_data;
        }
    }

    //Reformats table with proper text formatting
    private String reformatText(String textBlock) {
        Scanner scanner = new Scanner(textBlock);
        String datetime = "";
        String mag = "";
        String longitude = "";
        String latitude = "";
        String src = "";
        String eqid = "";

        //Reads multiline text and reformats the text
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.contains("datetime")) {
                datetime = line.replace("datetime", "Datetime");
            }

            if(line.contains("lng")) {
                longitude = line.replace("lng: ", "");
            }

            if(line.contains("lat")) {
                latitude = line.replace("lat: ", "");
            }

            if(line.contains("magnitude")) {
                mag = line.replace("magnitude", "Magnitude");
            }

            if(line.contains("src")) {
                src = line.toUpperCase();
            }

            if(line.contains("eqid")) {
                eqid = line.toUpperCase();
            }
        }

        //Set values to 0 if doesn't exist
        if(latitude == "") {
            latitude = "0";
        }
        if(longitude == "") {
            longitude = "0";
        }
        if(mag == "") {
            mag = "0";
        }

        return datetime + "\n" + mag + "\nCoordinates: " + latitude + ", " + longitude + "\n" + src  + "\n" + eqid;
    }

    //Check if device is connected to network
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}