package com.example.jade.boardgamebuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author Jade Buhler
 * Date: 11/17/2017
 */

public class DatabaseHelper extends SQLiteOpenHelper
{
    // Define constants for the database name, tables, columns and version.
    private static final String DB_NAME = "BoardGameBuddy";
    private static final String PLAYER_TABLE = "Players";
    private static final String GAMES_TABLE = "Games";
    private static final String PLAYER_COL_NAME = "Name";
    private static final int DB_VERSION = 4;

    // Define constants for creating each table.
//    private static final String PLAYER_TABLE_CREATE =
//            "CREATE TABLE " + PLAYER_TABLE + " (" +
//                    PLAYER_COL_NAME + "TEXT NOT NULL);";

    private static final String PLAYER_TABLE_CREATE =
            "CREATE TABLE " + PLAYER_TABLE + " (" +
                    PLAYER_COL_NAME + " STRING NOT NULL);";

    // Define constants for dropping each table.
    private static final String DROP_PLAYER_TABLE = "DROP TABLE IF EXISTS " + PLAYER_TABLE;
    private static final String DROP_GAMES_TABLE = "DROP TABLE IF EXISTS " + GAMES_TABLE;

    public DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create the tables.
        db.execSQL(PLAYER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop the tables and create them again.
        db.execSQL(DROP_PLAYER_TABLE);
        onCreate(db);
    }

    public void insertValues(String name)
    {
        // Get an instance of the writable database.
        SQLiteDatabase db = this.getWritableDatabase();

        // Create an instance of ContentValues to add to the database.
        ContentValues insertValues = new ContentValues();

        // Add the player name to the content values.
        insertValues.put(PLAYER_COL_NAME, name);

        // Insert the player name into the database.
        db.insert(PLAYER_TABLE, null, insertValues);

        // Close the database.
        db.close();

    }

    //Load the data in the table
    public ArrayList<String> loadData(){

        ArrayList<String> nameData = new ArrayList<String>();

        //open the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        //create an array of the table names
        String[] selection = {PLAYER_COL_NAME};

        //Create a cursor item for querying the database
        Cursor c = db.query(PLAYER_TABLE,	//The name of the table to query
                selection,				    //The columns to return
                null,				//The columns for the where clause
                null,			//The values for the where clause
                null,				//Group the rows
                null,				//Filter the row groups
                null);				//The sort order

        //Move to the first row
        c.moveToFirst();

        //For each row that was retrieved
        for(int i=0; i < c.getCount(); i++)
        {
            //assign the value to the corresponding array
            nameData.add(c.getString(0));
            c.moveToNext();
        }

        //close the cursor
        c.close();

        //close the database
        db.close();

        return nameData;
    }

    //Load the data in the table
    //This method is used to load the data from the table into a hash map
    //this enables the use of multiple textviews in the listview
    public List<Map<String,String>> loadData2()
    {
        List<Map<String,String>> lm = new ArrayList<Map<String,String>>();

        //open the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        //create an array of the table names
        String[] selection = {PLAYER_COL_NAME};

        //Create a cursor item for querying the database
        Cursor c = db.query(PLAYER_TABLE,	//The name of the table to query
                selection,				    //The columns to return
                null,				//The columns for the where clause
                null,			//The values for the where clause
                null,				//Group the rows
                null,				//Filter the row groups
                null);				//The sort order

        //Move to the first row
        c.moveToFirst();

        //For each row that was retrieved
        for(int i=0; i < c.getCount(); i++)
        {
            Map<String,String> map = new HashMap<String,String>();

            //assign the value to the corresponding array
            map.put("Name", c.getString(0));

            lm.add(map);
            c.moveToNext();
        }

        //close the cursor
        c.close();

        //close the database
        db.close();

        return lm;

    }
}