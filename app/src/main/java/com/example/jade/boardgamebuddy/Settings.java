package com.example.jade.boardgamebuddy;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class Settings extends BaseActivity
{
    private SharedPreferences preferences;
    private RadioButton rdbPlayers;
    private RadioButton rdbGames;
    private Spinner spFontSize;
    private Spinner spFontFamily;
    private Typeface typeface;

    // This adapter will be used to populate the spinners
    private ArrayAdapter<CharSequence> fontAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Instantiate the toolbar
        Toolbar appBar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Instantiate the shared preferences
        preferences = getSharedPreferences("main_prefs", MODE_PRIVATE);

        rdbPlayers = findViewById(R.id.rdbPlayers);
        rdbGames = findViewById(R.id.rdbGames);
        spFontSize = findViewById(R.id.spFontSize);
        spFontFamily = findViewById(R.id.spFontFamily);

        // Checks the shared preferences and sets the radio buttons accordingly
        // If the displayPlayers boolean is true check the Players radio button, otherwise check
        // the Games radio button
        if (preferences.getBoolean("displayPlayers", false))
        {
            rdbPlayers.setChecked(true);
            rdbGames.setChecked(false);
        }
        else if (!preferences.getBoolean("displayPlayers", false))
        {
            rdbPlayers.setChecked(false);
            rdbGames.setChecked(true);
        }

        // Populate the font size and font family spinners with arrays from the strings resource
        // file
        fontAdapter = ArrayAdapter.createFromResource(this, R.array.font_sizes, android.R.layout
                .simple_spinner_dropdown_item);
        spFontSize.setAdapter(fontAdapter);

        fontAdapter = ArrayAdapter.createFromResource(this, R.array.font_family, android.R
                .layout.simple_spinner_dropdown_item);
        spFontFamily.setAdapter(fontAdapter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Open an editor for our shared preferences
        SharedPreferences.Editor editor = preferences.edit();

        // Add the chosen settings to the editor and commit them
        // Saving font size as a String because I'm lazy and don't want to make an array adapter
        // that takes ints
        editor.putString("fontSize", spFontSize.getSelectedItem().toString());
        editor.putString("fontFamily", spFontFamily.getSelectedItem().toString());

        // Check if the players radio button is checked, if it is set the displayPlayers boolean
        // to true in the edit preferences, otherwise set it to false.
        // This boolean will determine wether to display the players list view or games list view
        // in the main activity
        if (rdbPlayers.isChecked())
        {
            editor.putBoolean("displayPlayers", true);
        }
        else
        {
            editor.putBoolean("displayPlayers", false);
        }

        editor.commit();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        TextView txtChoose = findViewById(R.id.txtShow);
        TextView txtFontSize = findViewById(R.id.txtFontSize);
        TextView txtFontFamily = findViewById(R.id.txtFontFamily);
        RadioButton rdbPlayer = findViewById(R.id.rdbPlayers);
        RadioButton rdbGame = findViewById(R.id.rdbGames);

        // Create a typeface from the previously chosen font family in the shared preferecnes
        typeface = typeface.create(preferences.getString("fontFamily", "serif"), Typeface.NORMAL);

        // Set the font family and font size for each item in the activity
        // Could probably refactor this to be better, maybe some sort of loop?
        txtChoose.setTypeface(typeface);
        txtChoose.setTextSize(Integer.valueOf(preferences.getString("fontSize", "12")));
        rdbPlayer.setTypeface(typeface);
        rdbPlayer.setTextSize(Integer.valueOf(preferences.getString("fontSize", "12")));
        rdbGame.setTypeface(typeface);
        rdbGame.setTextSize(Integer.valueOf(preferences.getString("fontSize", "12")));
        txtFontFamily.setTypeface(typeface);
        txtFontFamily.setTextSize(Integer.valueOf(preferences.getString("fontSize", "12")));
        txtFontSize.setTypeface(typeface);
        txtFontSize.setTextSize(Integer.valueOf(preferences.getString("fontSize", "12")));


        String chosenFont = preferences.getString("fontFamily", "serif");
        String chosenFontSize = preferences.getString("fontSize", "12");

        // Set the spinners to have the previously selected item selected when the activity is
        // loaded again
        fontAdapter = ArrayAdapter.createFromResource(this, R.array.font_family, android.R.layout
                .simple_spinner_dropdown_item);

        int spinnerPosition = fontAdapter.getPosition(chosenFont);
        spFontFamily.setSelection(spinnerPosition);

        fontAdapter = ArrayAdapter.createFromResource(this, R.array.font_sizes, android.R.layout
                .simple_spinner_dropdown_item);

        spinnerPosition = fontAdapter.getPosition(chosenFontSize);
        spFontSize.setSelection(spinnerPosition);
    }
}
