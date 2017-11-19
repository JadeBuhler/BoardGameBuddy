package com.example.jade.boardgamebuddy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity
{
    Button btnAdd;
    TextView txtDefault;
    ListView lvPlayers;
    DatabaseHelper dbHelper;
    Button userAva;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate the toolbar
        Toolbar appBar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        userAva = (Button)findViewById(R.id.userAvatar);

        //Instantiate the add button.
        btnAdd = (Button)findViewById(R.id.btnAdd);
        txtDefault = (TextView)findViewById(R.id.txtDefault);

        // Instantiate the players list view.
        lvPlayers = (ListView)findViewById(R.id.lvPlayers);

        dbHelper = new DatabaseHelper(this);

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

    public void loadData()
    {
        // Instantiate the DataBaseHelper
        dbHelper = new DatabaseHelper(this);

        //load the data into a local variable
        ArrayAdapter<String> lstAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.listview_item,
                dbHelper.loadData());

        //create a simple adapter
        //SimpleAdapter sa = new SimpleAdapter(this,lm,R.layout.listview_item,new String[]
            //{"Name"}, new int[] {R.id.text1, R.id.text2});

        //assign the adapter to the
        lvPlayers.setAdapter(lstAdapter);
    }

    // This method will check a given database table and determine if that table has any data.
    //
    //
    // Params: String tableName: The table to be checked for data.
    //
    // Returns: True if the table contains any data, false otherwise.
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

}

