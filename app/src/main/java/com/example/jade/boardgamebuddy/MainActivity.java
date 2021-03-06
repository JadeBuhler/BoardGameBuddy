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
    private TextView txtDefault;
    private SharedPreferences preferences;
    private Typeface typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate the dbHelper
        dbHelper = new DatabaseHelper(this);

        // Instantiate the toolbar
        Toolbar appBar = findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Instantiate the shared preferences
        preferences = getSharedPreferences("main_prefs", MODE_PRIVATE);

        Button btnAdd = findViewById(R.id.btnAdd);
        txtDefault = findViewById(R.id.txtDefault);
        lvPlayers = findViewById(R.id.lvPlayers);
        lvGames = findViewById(R.id.lvGames);

        // Show the default text and hide the players and board games list views.
        txtDefault.setVisibility(View.VISIBLE);
        lvPlayers.setVisibility(View.GONE);
        lvGames.setVisibility(View.GONE);


        // TODO: Refactor this preferences check.
        if (preferences.getBoolean("displayPlayers", false))
        {
            txtDefault.setVisibility(View.VISIBLE);
            txtDefault.setText("It looks like you haven't added any Players yet. Click the plus sign " +
                    "below to add your first Player!");

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
                // Send the user to the addScreen activity
                Intent intent = new Intent(MainActivity.this, addScreen.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Get the users preferred typeface from the shared preferences
        typeFace = typeFace.create(preferences.getString("fontFamily", "monospace"), Typeface.NORMAL);

        txtDefault.setVisibility(View.VISIBLE);
        lvPlayers.setVisibility(View.GONE);
        lvGames.setVisibility(View.GONE);


        // TODO: Refactor this preferences check.
        if (preferences.getBoolean("displayPlayers", false))
        {

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
            if (tableHasData("Games"))
            {
                txtDefault.setVisibility(View.GONE);
                lvPlayers.setVisibility(View.GONE);
                lvGames.setVisibility(View.VISIBLE);

                // Load the board game data into the list view.
                loadBoardGameData();
            }
        }
    }

    // region Database interaction methods

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
        // from the database
        ArrayList<Player> players = new ArrayList<>();

        // Iterate over player name in the names array list
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
        ArrayList<String> ids = dbHelper.loadBoardGameIds();

        // This array list will eventually hold game objects for each game name returned from the
        // database
        ArrayList<Game> games = new ArrayList<>();

        // Iterate over each board game name in the names array list
        for (int i = 0; i < names.size(); i++)
        {
            // Create a new Game object and set the name to the current namme in the names array
            // list
            Game newGame = new Game(names.get(i), ids.get(i));
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

    //endregion

    //region Popup Menu

    /**
     * Displays and populates a popup menu of options available for board games and players.
     *
     * @param view A reference to a specific list view item layout
     */
    public void showListOptions(final View view)
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
                        // Find the TextView of the player or game in the selected list view item
                        TextView txtName = findViewById(R.id.txtPlayerName);
                        TextView txtGame = findViewById(R.id.txtGameName);

                        // If txtName is not null the delete call came from a player list view
                        if (txtName != null)
                        {
                            // Get the player name
                            String name = txtName.getText().toString();

                            // Delete the player from the database
                            dbHelper.deletePlayerRecord(name);

                            // Remove the selected player from the list view and refresh the list
                            playerAdapter.remove(playerAdapter.getItem(lvPlayers.getPositionForView(view)));
                            playerAdapter.notifyDataSetChanged();
                        }

                        // If txtGame is not null the delete call came from a game list view
                        if (txtGame != null)
                        {
                            // Get the game name
                            String gameName = txtGame.getText().toString();

                            // Delete the game from the databse
                            dbHelper.deleteGameRecord(gameName);

                            // Remove the selected game from the listview and refresh the list
                            gameAdapter.remove(gameAdapter.getItem(lvGames.getPositionForView
                                    (view)));
                            gameAdapter.notifyDataSetChanged();
                        }

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

    //endregion

    //region Custom Array Adapters

    /**
      * Defines a custom ArrayAdapter that will hold Player objects.
      *
      */
    private class PlayerAdapter extends ArrayAdapter<Player>
    {
        private ArrayList<Player> players;

        /**
         * Constructor
         *
         * @param context The context of the adapter
         * @param textViewResourceId The ID of the txt view resource
         * @param players An array list of players to be loaded into a list view
         */
        public PlayerAdapter(Context context, int textViewResourceId, ArrayList<Player> players)
        {
            super(context, textViewResourceId, players);
            this.players = players;
        }

        /**
         * Override the getView method to display a custom layout for ListView items.
         *
         * Called once for each Player in the ArrayList as the list is loaded.
         *
         * @param position The position of the current Player.
         * @param convertView The view of the list item (I think?)
         * @param parent The parent element.
         * @return A player list item to be displayed in a ListView for each player in the list.
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

        /**
         * Constructor
         *
         * @param context The context of the adapter
         * @param textViewResourceId The ID of the text view resource
         * @param games An array list of Games to be loaded into a list view
         */
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
         * @param position The position of the current Game.
         * @param convertView The view of the list item (I think?)
         * @param parent The parent element
         * @return A game list item to be displayed in a ListView for each game in the list.
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

