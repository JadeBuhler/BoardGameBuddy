package com.example.jade.boardgamebuddy;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AddGame extends BaseActivity
{
    private String API_URL = "https://bgg-json.azurewebsites.net/collection/edwalter";

    private Spinner spGames;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        Toolbar appBar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Call our Async Task.
        FetchAPITask apiTask = new FetchAPITask();
        apiTask.execute();
    }

    // This class will handle the reading and outputting of a specified API
    //
    // Params:  Void:   Specifies that the onPreExecute method will not return anything
    //          Void:   Specifies that the doInBackground method will not return anything
    //          String: Specifies that the onPostExecute method will return a string
    class FetchAPITask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void...voids)
        {
            try
            {
                // Instantiate a new URL with our API constant defined above
                URL apiCall = new URL(API_URL);

                // Open a connection to the URL
                HttpURLConnection urlConnection = (HttpURLConnection) apiCall.openConnection();

                try
                {
                    // Instantiate a buffer reader and point it to the API input stream
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    // Instantiate a string builder
                    // This builder will be used to output the API contents
                    StringBuilder builder = new StringBuilder();
                    String line;

                    // Read through each line of the API and append the line to our string builder
                    while ((line = reader.readLine()) != null)
                    {
                        builder.append(line).append("\n");
                    }

                    // Close the reader
                    reader.close();

                    // Return our built string that contains every line from the specified API
                    return builder.toString();
                }
                finally
                {
                    // Close the http connection
                    urlConnection.disconnect();
                }
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                return "An error occurred while establishing a connection to the API.";
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return "An error occurred while establishing a connection to the API.";
            }
        }

        @Override
        // Params: response: The response is set to whatever is returned from the doInBackground method
        //                   In this case response is the result of our string builder
        protected void onPostExecute(String response)
        {
            try
            {
                // Get the results returned from the doInBackgorund method and store them as a
                // JSON array
                JSONArray apiResults = new JSONArray(response);

                // This array will hold all the board game names
                JSONArray nameArray = new JSONArray();

                // For each item in the api result array...
                for(int i = 0; i < apiResults.length(); i++)
                {
                    // Get the current JSON object
                    JSONObject obj = apiResults.getJSONObject(i);

                    // Get the board game name from the current object
                    String nameObject = obj.getString("name");

                    // Add the board game name to the array of names
                    nameArray.put(nameObject);
                }

                EditText edt = findViewById(R.id.edtSearchGames);
                spGames = findViewById(R.id.spBoardGames);

                // This array list will be used to populate an array adapter since JSON arrays
                // can't be set to array adapters
                ArrayList<String> list = new ArrayList<>();

                // Add each name in the JSON array to the String array list
                for (int i = 0; i < nameArray.length(); i++)
                {
                    list.add(nameArray.getString(i));
                }

                // Create an array adapter that will be used to populate the spinner with board
                // game names
                ArrayAdapter<String> test = new ArrayAdapter<>(AddGame.this, android.R
                        .layout.simple_expandable_list_item_1, list);

                // Set the adapter to the games spinner
                spGames.setAdapter(test);
                edt.setText(spGames.getSelectedItem().toString());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}
