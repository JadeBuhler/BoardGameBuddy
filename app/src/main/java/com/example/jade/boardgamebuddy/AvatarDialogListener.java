package com.example.jade.boardgamebuddy;

import android.graphics.drawable.Drawable;

/**
 * A custom interface used to update player avatars.
 *
 * Author: Jade Buhler
 * Date: 11/17/2017.
 *
 * This interface is used in the AddPlayer activity.
 */

public interface AvatarDialogListener
{
    // This method takes in a drawable that will be used to set the users avatar.
    void updateResult(Drawable avatar);
}
