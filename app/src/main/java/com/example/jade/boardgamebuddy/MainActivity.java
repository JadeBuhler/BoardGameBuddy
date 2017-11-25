package com.example.jade.boardgamebuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
    private ArrayList<Player> players;
    private ListView lvPlayers;
    private PlayerAdapter playerAdapter;
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

        // Hide the players list view.
        lvPlayers.setVisibility(View.GONE);

        // If the players table has data hide the default text view and display the players list
        // view.
        if (tableHasData("Players"))
        {
            txtDefault.setVisibility(View.GONE);
            lvPlayers.setVisibility(View.VISIBLE);

            // Load the player data into the list view.
            loadData();
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

        typeFace = typeFace.create(preferences.getString("fontFamily", "monospace"), Typeface.NORMAL);

        // If the players table has data hide the default text view and display the players list
        // view.
        if (tableHasData("Players"))
        {
            txtDefault.setVisibility(View.GONE);
            lvPlayers.setVisibility(View.VISIBLE);

            // Load the player data into the list view.
            loadData();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }


    /*
     *
     * This method calls the loadData() method in the DatabaseHeleper class in order to load data
     * from the database.
     *
     */
    public void loadData()
    {
        // Instantiate the DataBaseHelper
        dbHelper = new DatabaseHelper(this);

        // I'm getting an ArrayList<String> I need to build an ArrayList<Player>
        // For each item in the ArrayList<String> build a player and set their name via
        // ArrayList<String>. After all Players are built place them in an ArrayList<Player>

        ArrayList<String> names = dbHelper.loadNames();
        ArrayList<Bitmap> avatars = dbHelper.loadAvatars();
        Log.d("Avatars:", avatars.toString());
        ArrayList<Player> players = new ArrayList<>();

        for (int i = 0; i < names.size(); i++)
        {
            Player newPlayer = new Player(names.get(i), (Bitmap)avatars.get(i));

            players.add(newPlayer);
        }


        playerAdapter = new PlayerAdapter(MainActivity.this, R.layout.listview_item, players);

        //assign the adapter to the list view.
        lvPlayers.setAdapter(playerAdapter);

        lvPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(MainActivity.this, "Index: " + position,
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    /*
     * This method will check a given database table and determine if that table has any data.
     *
     * Params: String tableName: The table to be checked for data.
     *
     * Returns: True if the table contains any data, false otherwise.
     */
    public boolean tableHasData(String tableName)
    {
       SQLiteDatabase db = dbHelper.getReadableDatabase();
       Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " +tableName, null);

       if (cursor != null)
       {
           cursor.moveToFirst();

           int count = cursor.getInt(0);

           if (count > 0)
           {
               return true;
           }

           cursor.close();
       }

       return false;
    }


    //region Player class and adapter

    /*
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

        /*
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
                view = inflater.inflate(R.layout.listview_item, null);
            }

            // Get the current player from the array list
            Player player = players.get(position);

            Log.d("Player Posotion:", players.get(position).toString());

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
                    userAvatar.setImageBitmap(Bitmap.createScaledBitmap(player.getAvatar(), 130,
                            130, false));

                    userAvatar.requestLayout();

                    // Set the players avatar to the image view
                    Log.d("Image: ", player.getAvatar().toString());
                }
            }

            return view;
        }
    }

    /*
   * Defines a Player object.
   * Players data will be loaded into an array list in order to display each player on the
   * MainActivity screen.
   */
    class Player
    {
        private String name;
        private Bitmap avatarImage;

        /*
         * Constructor
         *
         * Params:       name: The name of the player
         *         avatarImage: The players chosen avatar
         */
        public Player(String name, Bitmap avatarImage)
        {
            this.name = name;
            this.avatarImage = avatarImage;
        }

        public String getName() {
            return name;
        }


        public Bitmap getAvatar()
        {
            return avatarImage;
        }

    }

    //endregion

    public void showListOptions(View view)
    {
        PopupMenu menu = new PopupMenu(this, view);

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                int id = item.getItemId();

                switch(id)
                {
                    case R.id.edit:
                        break;
                    case R.id.delete:
                        break;
                    case R.id.stats:
                        break;
                }
                return true;
            }
        });

        menu.inflate(R.menu.list_options);
        menu.show();
    }

}

