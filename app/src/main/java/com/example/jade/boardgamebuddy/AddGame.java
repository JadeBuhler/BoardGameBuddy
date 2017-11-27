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
    // This url is used to populate the spGames spinner with board game names
    private String API_URL = "https://bgg-json.azurewebsites.net/collection/edwalter";

    // This spinner is used for the stupidest hack I've ever come up with (so far).
    // Basically the board game geek API cannot be queried by using a game name, it must use a
    // game ID, so, in order to be able to query the API later each board game ID must be
    // captured and inserted into the database along with the chosen board game name. This
    // spinner will hold every board game ID returned from the above API URL. This spinner's
    // visibility is always set to INVISIBLE so the user never sees it and cannot interact with
    // it. The spinner will update it's selected item based on the board game selected from the
    // baord game name spinner. Like I said, it's stupid, but it works.
    private Spinner spHiddenId;

    private Spinner spGames;
    private EditText editText;
    private Button btnCancel;
    private Button btnAdd;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        // Inflate the toolbar
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
                // Save the board game to the database
                saveContentValues(v);
                finish();

                // Take the user back to the main activity
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
        // Insert the board game name from the edit text field and the game ID from the hidden
        // spinner into the database.
        dbHelper.insertBoardGame(editText.getText().toString(), spHiddenId.getSelectedItem().toString());
    }

    /**
     * This class will handle the reading and outputting of a specified API. In our case, we will
     * be reading the Board Game Geek API.
     */
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
        /**
         * @params response The response is set to whatever is returned from the doInBackground
         * method. In this case response is set to the result of our string builder.
         */
        protected void onPostExecute(String response)
        {
            try
            {
                // Get the results returned from the doInBackgorund method and store them as a
                // JSON array
                JSONArray apiResults = new JSONArray(response);

                // This array will hold all the board game names
                JSONArray nameArray = new JSONArray();

                // This array is part of the my stupid hack to store board game ID's in the database
                // This array will hold all board game ID's
                JSONArray idArray = new JSONArray();

                // For each item in the api result array...
                for(int i = 0; i < apiResults.length(); i++)
                {
                    // Get the current JSON object
                    JSONObject obj = apiResults.getJSONObject(i);

                    // Get the board game name and id from the current object
                    String nameObject = obj.getString("name");
                    String idObject = obj.getString("gameId");

                    // Add the board game name and id to their respective arrays
                    nameArray.put(nameObject);
                    idArray.put(idObject);
                }

                editText = findViewById(R.id.edtSearchGames);
                spGames = findViewById(R.id.spBoardGames);
                spHiddenId = findViewById(R.id.spHiddenId);


                // These array lists will be used to populate an array adapter since JSON arrays
                // can't be set to array adapters
                ArrayList<String> nameList = new ArrayList<>();
                ArrayList<String> gameIdList = new ArrayList<>();

                // Add each name and game ID to their respective lists
                for (int i = 0; i < nameArray.length(); i++)
                {
                    nameList.add(nameArray.getString(i));
                    gameIdList.add(idArray.getString(i));
                }

                // These adapters are used to populate the board game spinner and hidden board
                // game id spinner
                ArrayAdapter<String> gameList = new ArrayAdapter<>(AddGame.this, android.R
                        .layout.simple_expandable_list_item_1, nameList);

                ArrayAdapter<String> idList = new ArrayAdapter<String>(AddGame.this, android.R
                        .layout.simple_expandable_list_item_1,gameIdList);

                // Set the adapters
                spGames.setAdapter(gameList);

                // We now have every board game ID in this spinner.
                spHiddenId.setAdapter(idList);

                spGames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        // Set the edit text text to the selected board game
                        editText.setText(spGames.getSelectedItem().toString());

                        // When the user selects a baord game from the baord game spinner the
                        // hidden game ID spinner will also update to select the game ID at the
                        // same position as the selected board game. This will allow us to
                        // capture the game ID from the hidden spinner and store the ID in the
                        // database.
                        spHiddenId.setSelection(spGames.getSelectedItemPosition());
                    }

                    @Override
                    // This method does nothing but must be overridden along with onItemSelected.
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
