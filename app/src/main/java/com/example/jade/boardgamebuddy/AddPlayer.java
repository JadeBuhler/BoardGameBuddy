package com.example.jade.boardgamebuddy;

import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


public class AddPlayer extends BaseActivity implements AvatarDialogListener
{
    Button btnChooseAvatar;
    Button btnOk;
    Button btnCancel;
    Button userAvatar;
    EditText playerName;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        //Display the tool bar.
        Toolbar appBar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Find the user avatar image from the tool bar.
        userAvatar = (Button)findViewById(R.id.userAvatar);

        // Find the choose avatar, ok and cancel buttons.
        btnChooseAvatar = (Button)findViewById(R.id.btnChooseAvatar);
        btnOk = (Button)findViewById(R.id.btnOk);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        playerName = (EditText)findViewById(R.id.edtPlayerName);

        // Instantiate the database helper.
        dbHelper = new DatabaseHelper(this);

        // When the choose avatar button is clicked open a custom dialog fragment.
        btnChooseAvatar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Open the avatar fragment and allow the user to choose an avatar.
                AvatarDialogFragment avatarFrag = new AvatarDialogFragment();
                avatarFrag.show(getFragmentManager(), "Avatar Fragment");

                // Placeholder toast.
                Toast.makeText(AddPlayer.this, userAvatar.getBackground().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // When the cancel button is clicked go back to the previous activity.
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // When the user chooses to cancel the add player action just finish the activity
                // in order to go back to the previous screen.
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveContentValues(v);
                finish();
            }
        });
    }

    public void saveContentValues(View view)
    {
        // Insert the player name from the edit text field into the database.
        dbHelper.insertValues(playerName.getText().toString(), userAvatar.getBackground().toString());
    }

    @Override
    public void updateResult(Drawable avatar)
    {
        Drawable result = avatar;

        btnChooseAvatar.setBackground(avatar);
        userAvatar.setBackground(avatar);
    }


    /*
     * This class is a custom fragment that is an extension of a dialog fragment.
     *
     * This custom fragment will display a set of avatar images for the user to select from. When a user
     * selects an avatar the fragment will close and the users avatar will update in the apps
     * toolbar. The "Choose Avatar" button will also update to the users selection.
     *
     */
    public static class AvatarDialogFragment extends DialogFragment
    {
        // Empty constructor
        public AvatarDialogFragment()
        {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            // Inflate the custom avatar fragment layout.
            View v = inflater.inflate(R.layout.avatar_fragment, container, false);

            return v;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onStart()
        {
            super.onStart();

            // Instantiate all the avatar image buttons.
            final ImageButton catAvatar = getView().findViewById(R.id.btnCatAvatar);
            final ImageButton chickenAvatar = getView().findViewById(R.id.btnChickenAvatar);
            final ImageButton cowAvatar = getView().findViewById(R.id.btnCowAvatar);
            final ImageButton deerAvatar = getView().findViewById(R.id.btnDeerAvatar);
            final ImageButton dogAvatar = getView().findViewById(R.id.btnDogAvatar);
            final ImageButton foxAvatar = getView().findViewById(R.id.btnFoxAvatar);
            final ImageButton monkeyAvatar = getView().findViewById(R.id.btnMonkeyAvatar);
            final ImageButton pandaAvatar = getView().findViewById(R.id.btnPandaAvatar);
            final ImageButton pigAvatar = getView().findViewById(R.id.btnPigAvatar);


            // Set up an OnClickListener to be used by each avatar ImageButton.
            View.OnClickListener listener = new View.OnClickListener()
            {
                // Instantiate our custom DialogListener
                AvatarDialogListener dialogListener = (AvatarDialogListener) getActivity();

                @Override
                public void onClick(View v)
                {
                    // Switch statement for each ImageButton.

                    // When an Image button is clicked the DialogListener will update the users
                    // avatar in the toolbar as well as the "Choose Avatar" button on the
                    // AddPlayer activity.
                    //
                    // Once an avatar is chosen the dialog fragment will close.
                    switch (v.getId())
                    {
                        case R.id.btnCatAvatar:
                            dialogListener.updateResult(catAvatar.getDrawable());

                            dismiss();
                            break;

                        case R.id.btnChickenAvatar:
                            dialogListener.updateResult(chickenAvatar.getDrawable());
                            dismiss();
                            break;

                        case R.id.btnCowAvatar:
                            dialogListener.updateResult(cowAvatar.getDrawable());
                            dismiss();
                            break;

                        case R.id.btnDeerAvatar:
                            dialogListener.updateResult(deerAvatar.getDrawable());
                            dismiss();
                            break;

                        case R.id.btnDogAvatar:
                            dialogListener.updateResult(dogAvatar.getDrawable());
                            dismiss();
                            break;

                        case R.id.btnFoxAvatar:
                            dialogListener.updateResult(foxAvatar.getDrawable());
                            dismiss();
                            break;

                        case R.id.btnMonkeyAvatar:
                            dialogListener.updateResult(monkeyAvatar.getDrawable());
                            dismiss();
                            break;

                        case R.id.btnPandaAvatar:
                            dialogListener.updateResult(pandaAvatar.getDrawable());
                            dismiss();
                            break;

                        case R.id.btnPigAvatar:
                            dialogListener.updateResult(pigAvatar.getDrawable());
                            dismiss();
                            break;

                        default:
                            break;
                    }

                }
            };

            // Set each avatar ImageButton to use the above OnClickListener.
            catAvatar.setOnClickListener(listener);
            chickenAvatar.setOnClickListener(listener);
            cowAvatar.setOnClickListener(listener);
            deerAvatar.setOnClickListener(listener);
            dogAvatar.setOnClickListener(listener);
            foxAvatar.setOnClickListener(listener);
            monkeyAvatar.setOnClickListener(listener);
            pandaAvatar.setOnClickListener(listener);
            pigAvatar.setOnClickListener(listener);
        }

    }
}

