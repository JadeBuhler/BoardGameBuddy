package com.example.jade.boardgamebuddy;

import android.graphics.Bitmap;

/**
 * Author: Jade
 * Date: 11/26/2017.
 *
 * Defines a Player object.
 * Players data will be loaded into an array list in order to display each player on the
 * MainActivity screen.
 */
public class Player
{
    private String name;
    private Bitmap avatarImage;

    /**
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
