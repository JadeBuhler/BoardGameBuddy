package com.example.jade.boardgamebuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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
    private static final String PLAYER_COL_IMAGE = "Image";
    private static final String PLAYER_COL_NAME = "Name";
    private static final String GAMES_COL_NAME = "Name";
    private static final int DB_VERSION = 11;

    // Define constants for creating each table.

    private static final String PLAYER_TABLE_CREATE =
            "CREATE TABLE " + PLAYER_TABLE + " (" +
                    PLAYER_COL_NAME + " STRING NOT NULL, " +
                     PLAYER_COL_IMAGE + " BLOB NOT NULL);";

    private static final String GAME_TABLE_CREATE =
            "CREATE TABLE " + GAMES_TABLE + " (" +
                    GAMES_COL_NAME + " STRING NOT NULL);";

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
        db.execSQL(GAME_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop the tables and create them again.
        db.execSQL(DROP_PLAYER_TABLE);
        db.execSQL(DROP_GAMES_TABLE);
        onCreate(db);
    }

    public void insertPlayer(String name, Bitmap image)
    {
        // Get an instance of the writable database.
        SQLiteDatabase db = this.getWritableDatabase();

        // Create an instance of ContentValues to add to the database.
        ContentValues insertValues = new ContentValues();

        // Turn the passed in Bitmap image into a byte array
        byte[] imageData = getBitmapAsByteArray(image);

        // Add the player name to the content values.
        insertValues.put(PLAYER_COL_NAME, name);
        insertValues.put(PLAYER_COL_IMAGE, imageData);

        Log.d("jalaka:", imageData.toString());

        // Insert the player name into the database.
        db.insert(PLAYER_TABLE, null, insertValues);

        // Close the database.
        db.close();
    }

    public void insertBoardGame(String name)
    {
        // Get an instance of the writable database.
        SQLiteDatabase db = this.getWritableDatabase();

        // Create an instance of ContentValues to add to the database.
        ContentValues insertValues = new ContentValues();

        // Add the player name to the content values.
        insertValues.put(GAMES_COL_NAME, name);

        // Insert the player name into the database.
        db.insert(GAMES_TABLE, null, insertValues);

        // Close the database.
        db.close();
    }

    /*
     * This method loads data from the Players table in the database and inserts the retrieved
     * data into an array list.
     *
     * Returns: An array list containing players names.
     */
    public ArrayList<String> loadPlayerNames()
    {

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

    public ArrayList<Bitmap> loadAvatars()
    {
        ArrayList<Bitmap> avatarData = new ArrayList<Bitmap>();

        // Open the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        //create an array of the table names
        String[] selection = {PLAYER_COL_IMAGE};

        //Create a cursor item for querying the database
        Cursor c = db.query(PLAYER_TABLE,	//The name of the table to query
                selection,				    //The columns to return
                null,				//The columns for the where clause
                null,			//The values for the where clause
                null,				//Group the rows
                null,				//Filter the row groups
                null);				//The sort order

        // Move to the first row
        c.moveToFirst();

        // For each row that was retrieved
        for(int i=0; i < c.getCount(); i++)
        {
            // Get the image blob from the database and set it as a byte
            byte[] imgData = c.getBlob(0);

            // Decode the byte into a bitmap and add it to the bitmap array list
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);

            avatarData.add(bitmapImage);

            c.moveToNext();
        }

        //close the cursor
        c.close();

        //close the database
        db.close();

        return avatarData;
    }

    /*
     * This method loads data from the Games table in the database and inserts the retrieved
     * data into an array list.
     *
     * Returns: An array list containing board game names.
     */
    public ArrayList<String> loadBoardGameNames()
    {

        ArrayList<String> nameData = new ArrayList<String>();

        //open the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        //create an array of the table names
        String[] selection = {GAMES_COL_NAME};

        //Create a cursor item for querying the database
        Cursor c = db.query(GAMES_TABLE,	//The name of the table to query
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

    /*
     * This method takes in a bitmap image and converts it to a byte array.
     *
     */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public void deletePlayerRecord(String name)
    {
        // Open the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(PLAYER_TABLE, PLAYER_COL_NAME + "= '" + name + "'", null);

        db.close();
    }

    public Boolean deleteGameRecord(String name)
    {
        // Open the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        return db.delete(GAMES_TABLE, GAMES_COL_NAME + "=" + name, null) > 0;
    }
}
