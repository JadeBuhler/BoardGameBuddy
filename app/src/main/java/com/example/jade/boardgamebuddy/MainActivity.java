package com.example.jade.boardgamebuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends BaseActivity
{
    private DatabaseHelper dbHelper;
    private ListView lvPlayers;
    private ListView lvGames;
    private PlayerAdapter playerAdapter;
    private GameAdapter gameAdapter;
    private Button btnAdd;
    private TextView txtDefault;
    private SharedPreferences preferences;
    private Typeface typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Instantiate the toolbar
        Toolbar appBar = findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Instantiate the shared preferences
        preferences = getSharedPreferences("main_prefs", MODE_PRIVATE);

        btnAdd = findViewById(R.id.btnAdd);
        txtDefault = findViewById(R.id.txtDefault);

        // Instantiate the players list view.
        lvPlayers = findViewById(R.id.lvPlayers);

        // Instantiate the board games list view.
        lvGames = findViewById(R.id.lvGames);

        // Hide the players and board games list views.
        lvPlayers.setVisibility(View.GONE);
        lvGames.setVisibility(View.GONE);

        // TODO: Refactor this preferences check.
        if (preferences.getBoolean("displayPlayers", false))
        {
            txtDefault.setVisibility(View.VISIBLE);

            // If the players table has data hide the default text view and display the players list
            // view.
            if (tableHasData("Players"))
            {
                txtDefault.setVisibility(View.GONE);
                lvGames.setVisibility(View.GONE);
                lvPlayers.setVisibility(View.VISIBLE);

                // Load the player data into the list view.
                loadPlayerData();
            }
        }
        else if (!preferences.getBoolean("displayPlayers", false))
        {
            txtDefault.setVisibility(View.VISIBLE);

            if (tableHasData("Games"))
            {
                txtDefault.setVisibility(View.GONE);
                lvGames.setVisibility(View.VISIBLE);
                lvPlayers.setVisibility(View.GONE);

                // Load the board game data into the list view.
                loadPlayerData();
            }
        }


        btnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, addScreen.class);

                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Get the users prefered typeface from the shared preferences
        typeFace = typeFace.create(preferences.getString("fontFamily", "monospace"), Typeface.NORMAL);


        // TODO: Refactor this preferences check.
        if (preferences.getBoolean("displayPlayers", false))
        {
            txtDefault.setVisibility(View.GONE);
            lvGames.setVisibility(View.GONE);
            lvPlayers.setVisibility(View.VISIBLE);

            // If the players table has data hide the default text view and display the players list
            // view.
            if (tableHasData("Players"))
            {
                // Load the player data into the list view.
                loadPlayerData();
            }
        }
        else if (!preferences.getBoolean("displayPlayers", false))
        {
            txtDefault.setVisibility(View.GONE);
            lvPlayers.setVisibility(View.GONE);
            lvGames.setVisibility(View.VISIBLE);

            if (tableHasData("Games"))
            {
                Log.d("HasData:", "Data found");
                // Load the board game data into the list view.
                loadBoardGameData();
            }
        }
    }

    /**
     * This method will load player data from the SQLite database.
     */
    public void loadPlayerData()
    {
        // Instantiate the DataBaseHelper
        dbHelper = new DatabaseHelper(this);

        // Load all player names and avatars from the database into array lists
        ArrayList<String> names = dbHelper.loadPlayerNames();
        ArrayList<Bitmap> avatars = dbHelper.loadAvatars();

        // This array list will eventually hold a player object for each returned player name
        // from the databse
        ArrayList<Player> players = new ArrayList<>();

        // Itterate over player name in the names array list
        for (int i = 0; i < names.size(); i++)
        {
            // Create a new player object and set the name to the current name.
            // Also set the player avatar to the avatar that corresponds with the current index
            // of the loop.
            Player newPlayer = new Player(names.get(i), (Bitmap)avatars.get(i));

            players.add(newPlayer);
        }

        // Create a new PlayerAdapter to populate our player list view
        playerAdapter = new PlayerAdapter(MainActivity.this, R.layout.player_listview_item, players);

        // Set the new player adapter to the players list view
        lvPlayers.setAdapter(playerAdapter);

        // Handles onClick events for each list view item in the players list view
        lvPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Placeholder toast
                Toast.makeText(MainActivity.this, "Index: " + position,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     *  This method will load board game data from the SQLite database.
     */
    public void loadBoardGameData()
    {
        // Instantiate the DataBaseHelper
        dbHelper = new DatabaseHelper(this);

        // Load all board game names from the database into an array list
        ArrayList<String> names = dbHelper.loadBoardGameNames();

        // This array list will eventually hold game objects for each game name returned from the
        // databse
        ArrayList<Game> games = new ArrayList<>();

        // Itterate over each board game name in the names array list
        for (int i = 0; i < names.size(); i++)
        {
            // Create a new Game object and set the name to the current namme in the names array
            // list
            Game newGame = new Game(names.get(i));
            games.add(newGame);
        }

        // Create a new GameAdapter to populate our game list view.
        gameAdapter = new GameAdapter(MainActivity.this, R.layout.game_listview_item, games);

        // Set the new game adapter to the games list view
        lvGames.setAdapter(gameAdapter);

        // Handles onClick events for each list view item in the games list view
        lvGames.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Get the selected games name from the text view
                TextView txtgameName = view.findViewById(R.id.txtGameName);
                String gameName = txtgameName.getText().toString();

                // Start the ViewGame activity and pass the selected games name along
                Intent intent = new Intent(MainActivity.this, ViewGame.class);
                intent.putExtra("Game Name", gameName);

                startActivity(intent);
            }
        });

    }


    /**
     * This method will check a given database table and determine if that table has any data.
     *
     * @param tableName The table to be checked for data.
     * @return True if the table contains any data, false otherwise.
     */
    public boolean tableHasData(String tableName)
    {
       SQLiteDatabase db = dbHelper.getReadableDatabase();

       // Perform a select * query on the specified table
       Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " +tableName, null);

       // If the cursor is not null...
       if (cursor != null)
       {
           // Move to the first row of the table
           cursor.moveToFirst();

           // Get the column index number
           int count = cursor.getInt(0);

           // If the count is greater than 0 the table has data and returns true
           if (count > 0)
           {
               return true;
           }

           cursor.close();
       }

       return false;
    }

    /**
     * Displays and populates a popup menu of options available for board games and players.
     *
     * @param view A reference to a specific list view item layout
     */
    public void showListOptions(View view)
    {
        PopupMenu menu = new PopupMenu(this, view);

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                // Get the selected item
                int id = item.getItemId();

                // This switch handles each onClick event of the popup menu options
                switch(id)
                {
                    case R.id.edit:
                        break;
                    case R.id.delete:

                        // Find the TextView of the player in the selected list view item
                        TextView txtName = findViewById(R.id.txtPlayerName);
                        String name = txtName.getText().toString();

                        // Delete the player from the database.
                        dbHelper.deletePlayerRecord(name);

                        break;
                    case R.id.stats:
                        break;
                }
                return true;
            }
        });

        // Inflate and show the popup menu
        menu.inflate(R.menu.list_options);
        menu.show();
    }

    //region Custom Array Adapters

    /**
      * Defines a custom ArrayAdapter that will hold Player objects.
      *
      */
    private class PlayerAdapter extends ArrayAdapter<Player>
    {
        private ArrayList<Player> players;


        public PlayerAdapter(Context context, int textViewResourceId, ArrayList<Player> players)
        {
            super(context, textViewResourceId, players);
            this.players = players;
        }

        /**
          * Override the getView method to display a custom layout for ListView items.
          *
          * Called once for each player in the ArrayList as the list is loaded.
          *
          * Returns: view: A player list item to be displayed in a ListView for each player in the
          * list.
          */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = convertView;

            // If the list view is null inflate the listview layout
            if (view == null)
            {
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.player_listview_item, null);
            }

            // Get the current player from the array list
            Player player = players.get(position);

            // if the current player is not null...
            if (player != null)
            {
                // Load data into custom list view layout.
                TextView txtPlayerName = view.findViewById(R.id.txtPlayerName);
                ImageView userAvatar = view.findViewById(R.id.playerAvatar);

                // If the players name is not null...
                if (txtPlayerName != null)
                {
                    // Set the player name to the text view
                    txtPlayerName.setText(player.getName());

                    // Set the typeface and font soze from the shared preferences
                    txtPlayerName.setTypeface(typeFace);
                    txtPlayerName.setTextSize(Integer.valueOf(preferences.getString("fontSize",
                            "14"
                            )));
                }

                if (userAvatar != null)
                {
                    // Set the players avatar to the image view
                    userAvatar.setImageBitmap(Bitmap.createScaledBitmap(player.getAvatar(), 130,
                            130, false));

                    userAvatar.requestLayout();
                }
            }

            return view;
        }
    }

    /**
      * Defines a custom ArrayAdapter that will hold Game objects.
      */
    private class GameAdapter extends ArrayAdapter<Game>
    {
        private ArrayList<Game> games;


        public GameAdapter(Context context, int textViewResourceId, ArrayList<Game> games)
        {
            super(context, textViewResourceId, games);
            this.games = games;
        }


        /**
          * Override the getView method to display a custom layout for ListView items.
          *
          * Called once for each game in the ArrayList as the list is loaded.
          *
          * Returns: view: A game list item to be displayed in a ListView for each game in the
          * list.
          */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = convertView;

            // If the list view is null inflate the game listview layout
            if (view == null)
            {
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context
                        .LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.game_listview_item, null);
            }

            // Get the current game from the array list
            Game game = games.get(position);

            // if the current game is not null...
            if (game != null)
            {
                // Load data into custom list view layout.
                TextView txtGameName = view.findViewById(R.id.txtGameName);

                // If the games name is not null...
                if (txtGameName != null)
                {
                    // Set the game name to the text view
                    txtGameName.setText(game.getName());

                    // Set the typeface and font soze from the shared preferences
                    txtGameName.setTypeface(typeFace);
                    txtGameName.setTextSize(Integer.valueOf(preferences.getString("fontSize",
                            "14"
                    )));
                }
            }
            return view;
        }
    }
    // endregion
}

