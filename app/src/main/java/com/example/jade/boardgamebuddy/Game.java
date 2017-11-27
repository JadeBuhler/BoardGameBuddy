package com.example.jade.boardgamebuddy;

/**
 * Author: Jade
 * Date: 11/26/2017.
 *
 * Defines a Game object.
 * Board game data will be loaded into an array list in order to display each game on the
 * MainActivity screen.
 */
public class Game
{
    private String name;
    private String gameId;

    /**
      * Constructor
      *
      * Params: name: The name of the board game
      */
    public Game(String name, String gameId)
    {
        this.name = name;
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return gameId;
    }
}
