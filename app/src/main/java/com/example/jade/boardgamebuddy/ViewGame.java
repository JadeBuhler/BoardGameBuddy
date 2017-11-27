package com.example.jade.boardgamebuddy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ViewGame extends BaseActivity
{

    TextView txtGameName;
    DatabaseHelper dbHelper;
    String API_URL;
    ImageView imgGame;
    TextView txtMinPlayers;
    TextView txtMaxPlayers;
    TextView txtPlayTime;
    TextView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_game);

        // Instantiate the toolbar
        Toolbar appBar = findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Instantiate the various text views
        imgGame = findViewById(R.id.imgGame);
        txtGameName = findViewById(R.id.txtGameName);
        txtMinPlayers = findViewById(R.id.txtMinPlayers);
        txtMaxPlayers = findViewById(R.id.txtMaxPlayers);
        txtPlayTime = findViewById(R.id.txtPlayTime);
        txtDescription = findViewById(R.id.txtDescription);

        // Get the board game name from the intent
        txtGameName.setText(getIntent().getStringExtra("Game Name"));

        // Instantiate our db helper class
        dbHelper = new DatabaseHelper(this);

        String gameId = "";

        // Get the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // This select query will retrieve the gameId associated with the game name
        String selectQuery = "SELECT Id FROM Games WHERE Name = '" +txtGameName.getText()+"'";

        // Execute the select query
        Cursor c = db.rawQuery(selectQuery, null, null);

        if (c.moveToFirst())
        {
            // Set the gameId string to the value returned from the database
            gameId = c.getString(c.getColumnIndex("Id"));
        }

        c.close();

        // This url will query the board game geek JSON api for information about the board game.
        API_URL = "https://bgg-json.azurewebsites.net/thing/" + gameId +"?&brief=1";

        // Execute our async task to get data from the API
        FetchData apiTask = new FetchData();
        apiTask.execute();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Execute our async task to get data from the API
        FetchData apiTask = new FetchData();
        apiTask.execute();
    }

    /**
     * This class will handle the reading and outputting of the Board Game Geek API.
     */
    class FetchData extends AsyncTask<Void, Void, String>
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
        protected void onPostExecute(String response)
        {
            try
            {
                // Build a JSON object from the response returned by the doInBackground method
                JSONObject apiResults = new JSONObject(response);

                // Pull data from the JSON object
                String minPlayers = apiResults.getString("minPlayers");
                String maxPlayers = apiResults.getString("maxPlayers");
                String playTime = apiResults.getString("playingTime");
                String image = apiResults.getString("image");
                String description = apiResults.getString("description");

                // Execute our async task to retrieve the board game image from a url
                DownloadImageTask getImage = new DownloadImageTask(imgGame, image);
                getImage.execute();

                // Set the various text views to the data retrieved from the API
                txtMinPlayers.setText(minPlayers);
                txtMaxPlayers.setText(maxPlayers);
                txtPlayTime.setText(playTime + "m");
                txtDescription.setText(description);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * This class will handle the setting of an image view to an image retrieved via url from the
     * board game geek API.
     */
    class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>
    {

        ImageView bmImage;
        String url;

        public DownloadImageTask(ImageView bmImage, String url)
        {
            this.bmImage = bmImage;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... voids)
        {
            try
            {
                URL urlConnection = new URL(url);

                // Open a connection the image url
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();

                // Get the input stream from the url connection
                InputStream input = connection.getInputStream();

                // Decode the input stream to a bitmap image
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                return bitmap;
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap result)
        {
            // Set the image view to the returned bitmap image from the doInBackground method
            bmImage.setImageBitmap(result);
        }
    }
}
