package com.example.jade.boardgamebuddy;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private EditText editText;
    private Button btnCancel;
    private Button btnAdd;
    private DatabaseHelper dbHelper;
    private String gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        Toolbar appBar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Instantiate the database helper.
        dbHelper = new DatabaseHelper(this);

        spGames = findViewById(R.id.spBoardGames);
        btnCancel = findViewById(R.id.btnCancel);
        btnAdd = findViewById(R.id.btnAdd);


        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // When the user chooses to cancel the add game action just finish the activity
                // in order to go back to the previous screen.
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Save the board game to the databse
                saveContentValues(v);
                finish();
                Intent intent = new Intent(AddGame.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Call our Async Task.
        FetchAPITask apiTask = new FetchAPITask();
        apiTask.execute();
    }

    public void saveContentValues(View view)
    {
        // Insert the board game name from the edit text field into the database.
        dbHelper.insertBoardGame(editText.getText().toString());
        Log.d("Insertion:", editText.getText().toString());
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

                // I need to capture each games ID along with the name.

                // This array will hold all the board game names
                JSONArray nameArray = new JSONArray();

                // For each item in the api result array...
                for(int i = 0; i < apiResults.length(); i++)
                {
                    // Get the current JSON object
                    JSONObject obj = apiResults.getJSONObject(i);

                    // Get the board game name and id from the current object
                    String nameObject = obj.getString("name");

                    // Add the board game name and id to their respective arrays
                    nameArray.put(nameObject);
                }

                editText = findViewById(R.id.edtSearchGames);
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
                ArrayAdapter<String> gameList = new ArrayAdapter<>(AddGame.this, android.R
                        .layout.simple_expandable_list_item_1, list);

                // Set the adapter to the games spinner
                spGames.setAdapter(gameList);
                editText.setText(spGames.getSelectedItem().toString());

                spGames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        editText.setText(spGames.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}
