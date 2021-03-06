package com.spc.romannumeralconverter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// import android.content.SharedPreferences;


// ROMAN NUMERAL CONVERTER
// MainActivity handles the user interfacing of the app
// All the hard work is done by the RomanNumeral class definition
//


public class MainActivity extends AppCompatActivity {

    // Store key variables in the onSaveInstanceState call (cleaner than PrefsFile)
    static final String STATE_AUTOMODE = "autoMode";
    static final String STATE_AUTOCOUNTUP = "autoCountUp";
    static final String STATE_COUNTER = "counter";
    private static final String TAG = "RNC";
    // Key variables - pulled from the Preferences file if present, but we default here anyway
    // public static final String PREFS_NAME = "MyPrefsFile";
    public Boolean rnAutoMode = false;     // Which mode are we operating in
    public Boolean rnAutoCountUp = true;   // Does auto-mode count up or down
    public Boolean rnTimerRunning = false; // Is the timer running...
    // Screen Objects
    View adFragment; //The main screen advert view, need to turn on/off depending on mode
    EditText rnEditRN;  // The editable Roman Numeral field
    EditText rnEditAI;  // The editable Arabic Integer field
    TextView rnStatus;  // Status indicator, but also reset and mode switch button
    TextView rnErrMsg;  // Small text for more description
    Button rnAuto;      // AUTO mode-only button to start/stop counting up/down
    Button rnAutoAdd;   // AUTO mode-only button to increment
    Button rnAutoSub;   // AUTO mode-only button to decrement
    int rnCounter = 0;      // The starting point for the auto counter

    CountDownTimer rnTimer;     // Timer to enable the auto count up/down

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // find the relevant screen components
        rnEditRN = findViewById(R.id.editTextRNInput);
        rnEditAI = findViewById(R.id.editTextAIInput);
        rnStatus = findViewById(R.id.textViewStatus);
        rnErrMsg = findViewById(R.id.textViewErrMsg);
        rnAuto = findViewById(R.id.buttonAuto);
        rnAutoAdd = findViewById(R.id.buttonAdd);
        rnAutoSub = findViewById(R.id.buttonSubtract);
        adFragment = findViewById(R.id.adFragment_main);

        // create the LongClickListener on status to switch mode
        rnStatus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v(TAG, "... inside the onLongClick for STATUS...");
                switchMode();
                return true; // ensures that the normal onClickListener doesn't fire
            }
        });

        // create the LongClickListener on AUTO-COUNT button to switch between up/down
        rnAuto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v(TAG, "... inside the onLongClick for AUTO-COUNT...");
                rnAutoCountUp = !rnAutoCountUp;
                cancelTimerIfRunning();
                rnErrMsg.setText(getString(R.string.directionchange));
                return true; // ensures that the normal onClickListener doesn't fire
            }
        });

        // create the LongClickListener on AUTO-ADD button to increment by 10
        rnAutoAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v(TAG, "... inside the onLongClick for AUTO-ADD...");
                cancelTimerIfRunning();
                rnCounter = rnCounter + 10;
                setRN(rnCounter);
                return true; // ensures that the normal onClickListener doesn't fire
            }
        });

        // create the LongClickListener on AUTO-SUB button to increment by 10
        rnAutoSub.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v(TAG, "... inside the onLongClick for AUTO-SUB...");
                cancelTimerIfRunning();
                rnCounter = rnCounter - 10;
                setRN(rnCounter);
                return true; // ensures that the normal onClickListener doesn't fire
            }
        });

        // add a listener to the RomanNumeral editText
        rnEditRN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                // If the "DONE" pressed
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Grab the text entered and try to convert it...
                    String convertRNString = v.getText().toString();
                    try {
                        Log.v(TAG, "... setting up to convert : " + convertRNString);
                        RomanNumeral N = new RomanNumeral(convertRNString);
                        rnEditAI.setText(getString(R.string.blank_for_int, N.toInt()));
                        rnStatus.setText(getString(R.string.done));
                        rnErrMsg.setText(getString(R.string.blank));
                        // set the counter to the latest, so that change to AUTO would start there
                        rnCounter = N.toInt();
                    } catch (NumberFormatException e) {
                        Log.v(TAG, "...Conversion Failed: " + e.getMessage());
                        rnStatus.setText(getString(R.string.failed));
                        rnErrMsg.setText(e.getMessage());
                    }
                }
                return false;
            }
        });

        // add a listener to the ArabicInteger editText
        rnEditAI.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                int convertAIint;

                // If the "DONE" pressed
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Grab the text entered, convert to integer and then to Roman Numeral...
                    convertAIint = 0;

                    try {
                        convertAIint = Integer.parseInt(v.getText().toString());
                    } catch (NumberFormatException e) {
                        Log.v(TAG, "...Arabic Integer parse failed: " + e.getMessage());
                        rnStatus.setText(getString(R.string.failed));
                        rnErrMsg.setText(e.getMessage());
                    }

                    try {
                        Log.v(TAG, "... setting up to convert  : " + convertAIint);
                        RomanNumeral N = new RomanNumeral(convertAIint);
                        rnEditRN.setText(N.toString());
                        rnStatus.setText(getString(R.string.done));
                        rnErrMsg.setText(getString(R.string.blank));
                        // set the counter to the latest, so that change to AUTO would start there
                        rnCounter = convertAIint;
                    } catch (NumberFormatException e) {
                        Log.v(TAG, "...Conversion Failed: " + e.getMessage());
                        rnStatus.setText(getString(R.string.failed));
                        rnErrMsg.setText(e.getMessage());
                    }

                }
                return false;
            }
        });

        // Create the timer to be used - 30sec countdown with a tick every half-second
        rnTimer = new CountDownTimer(30000, 500) {

            // Define what to do at the end...
            public void onFinish() {
                Log.v(TAG, "...inside counterAuto onFinish; counter is " + rnCounter + ":rnTimer=" + rnTimer);
                cancelTimerIfRunning();
            }

            // Define what to do each tick...
            public void onTick(long millisUntilFinished) {
                // a bug in CountDownTime.cancel() means it continues to tick after cancel!
                // so only do something if we think it should already be running
                if (rnTimerRunning) {
                    Log.v(TAG, "...IN counterAuto onTick; CountUp=" + rnAutoCountUp + ":counter=" + rnCounter + ":rnTimer=" + rnTimer);
                    if (rnAutoCountUp) rnCounter = rnCounter + 1;
                    else rnCounter = rnCounter - 1;
                    setRN(rnCounter);
                }
            }
        };
        Log.v(TAG, "... created rnTimer " + rnTimer + "... rnTimerRunning=" + rnTimerRunning);

        // In manual mode, so don't yet display any clever stuff
        rnAutoAdd.setVisibility(View.GONE);
        rnAutoSub.setVisibility(View.GONE);
        rnAuto.setVisibility(View.GONE);
        adFragment.setVisibility(View.GONE);

        // display the last known number
        if (rnCounter > 0) setRN(rnCounter);

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Last check to cancel timer if running
        cancelTimerIfRunning();



    }


    // Rely on system calling onSaveInstanceState when stopping to save the key variables
    // alongside the other 'bundle' instance state.
    // Then as system calls onRestoreInstanceState() only if there is a saved state to restore
    // (eg if changing screen orientation) then we pull the variables back from the bundle
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current converter state
        savedInstanceState.putBoolean(STATE_AUTOMODE, rnAutoMode);
        savedInstanceState.putBoolean(STATE_AUTOCOUNTUP, rnAutoCountUp);
        savedInstanceState.putInt(STATE_COUNTER, rnCounter);
        Log.v(TAG, "onSaveInstanceState:AutoMode=" + rnAutoMode + ";AutoCountUp=" + rnAutoCountUp + ";Counter=" + rnCounter);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        rnAutoMode = savedInstanceState.getBoolean(STATE_AUTOMODE, false);
        rnAutoCountUp = savedInstanceState.getBoolean(STATE_AUTOCOUNTUP, true);
        rnCounter = savedInstanceState.getInt(STATE_COUNTER, 0);
        Log.v(TAG, "onRestoreInstanceState:AutoMode=" + rnAutoMode + ";AutoCountUp=" + rnAutoCountUp + ";Counter=" + rnCounter);

        // if starting in AUTO mode from previous session then switch to it...
        if (rnAutoMode) {
            rnAutoMode = false;   // bit of hack to turn it to manual, then switch...
            switchMode();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.switchmode) {
            switchMode();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.reset) {
            performReset();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.help) {
            // Start new intent with the Help class
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This is the button click listener, just calls performReset
    public void onStatusClick(View view) {
        performReset();
    }

    // Called from button click listener or the settings menu
    public void performReset() {
        // Basic reset of fields, but needs to take account of AUTO or MANUAL
        if (rnAutoMode) {
            cancelTimerIfRunning();
            rnErrMsg.setText(getString(R.string.autoreset));
            rnAutoCountUp = true;                   // as we will reset then ensure
            rnAuto.setText(getString(R.string.buttonAutoUp));  // count UP is defaulted
        } else {
            rnStatus.setText(getString(R.string.RN_status));  //READY
            rnErrMsg.setText(getString(R.string.manualreset));
        }
        rnEditAI.setText(getString(R.string.blank));  // Setting empty string should make hint visible?
        rnEditRN.setText(getString(R.string.blank));  // Setting empty string should make hint visible?
        rnCounter = 0;
    }

    public void switchMode() {
        if (rnAutoMode) {
            // Currently in AUTO, switch to MANUAL
            rnAutoMode = false;
            rnStatus.setText(getString(R.string.RN_status));
            rnErrMsg.setText(getString(R.string.switchmanual));
            rnAutoAdd.setVisibility(View.GONE);
            rnAutoSub.setVisibility(View.GONE);
            rnAuto.setVisibility(View.GONE);
            rnEditRN.setEnabled(true);
            rnEditAI.setEnabled(true);
            cancelTimerIfRunning();
            rnEditRN.setTextColor(getResources().getColor(R.color.cactusGreenColor));
            rnEditAI.setTextColor(getResources().getColor(R.color.cactusGreenColor));
            adFragment.setVisibility(View.GONE);    // remove the advert

        } else {
            // Currently in MANUAL, switch to AUTO
            rnAutoMode = true;
            rnStatus.setText(getString(R.string.auto));
            rnErrMsg.setText(getString(R.string.switchauto));
            rnAutoAdd.setVisibility(View.VISIBLE);
            rnAutoSub.setVisibility(View.VISIBLE);
            rnAuto.setVisibility(View.VISIBLE);
            rnEditRN.setEnabled(false);
            rnEditAI.setEnabled(false);
            rnEditRN.setTextColor(Color.BLUE);
            rnEditAI.setTextColor(Color.BLUE);
            // if in AUTO mode, set the AUTO button text to either UP or DOWN
            if (rnAutoCountUp) {
                rnAuto.setText(getString(R.string.buttonAutoUp));
            } else {
                rnAuto.setText(getString(R.string.buttonAutoDown));
            }
            adFragment.setVisibility(View.VISIBLE);   // make advert visible
        }
    }


    public void counterAuto(View v) {

        Log.v(TAG, "...Entering counterAUTO; rnTimerRunning= " + rnTimerRunning + "; rnTimer=" + rnTimer);

        if (!rnTimerRunning) {
            Log.v(TAG, "...inside counterAuto - starting timer");
            rnTimer.start();
            rnAuto.setText(getString(R.string.buttonAutoStop));
            rnTimerRunning = true;
        } else {
            Log.v(TAG, "...inside counterAuto - stopping timer");
            cancelTimerIfRunning();

        }

    }

    public void cancelTimerIfRunning() {
        // if we think the timer should be running, or is running...
        Log.v(TAG, "...Entering cancelTimerIfRunning; rnTimerRunning= " + rnTimerRunning + "; rnTimer=" + rnTimer);
        if (rnTimerRunning) {
            // then try to cancel it
            if (rnTimer != null) {
                Log.v(TAG, "...cancelling timer: " + rnTimer);
                rnTimer.cancel();
            }

            // record that it is not running
            rnTimerRunning = false;
        }

        // if in AUTO mode, set the AUTO button text to either UP or DOWN
        if (rnAutoMode && rnAutoCountUp) rnAuto.setText(getString(R.string.buttonAutoUp));
        if (rnAutoMode && !rnAutoCountUp) rnAuto.setText(getString(R.string.buttonAutoDown));

        Log.v(TAG, "...Leaving cancelTimerIfRunning; rnTimerRunning= " + rnTimerRunning + "; rnTimer=" + rnTimer);

    }

    public void counterAdd(View v) {
        Log.v(TAG, "...inside counterAdd; fbc is " + rnCounter);
        // If the counter is running on AUTO, then cancel it to enable manual increments
        cancelTimerIfRunning();

        rnCounter = rnCounter + 1;
        setRN(rnCounter);

    }

    public void counterSubtract(View v) {
        Log.v(TAG, "...inside counterSubtract; fbc is " + rnCounter);
        // If the counter is running on AUTO, then cancel it to enable manual increments
        cancelTimerIfRunning();

        rnCounter = rnCounter - 1;
        setRN(rnCounter);
    }

    public void setRN(int convertAIint) {
        try {
            Log.v(TAG, "... setting up to convert  : " + convertAIint);
            RomanNumeral N = new RomanNumeral(convertAIint);
            rnEditRN.setText(N.toString());
            rnEditAI.setText(getString(R.string.blank_for_int, N.toInt()));
            rnStatus.setText(getString(R.string.auto));
            rnErrMsg.setText(getString(R.string.blank));
        } catch (NumberFormatException e) {
            Log.v(TAG, "...Conversion Failed: " + e.getMessage());
            rnEditAI.setText(getString(R.string.blank_for_int, convertAIint));
            rnEditRN.setText(getString(R.string.blank));
            rnStatus.setText(getString(R.string.failed));
            rnErrMsg.setText(e.getMessage());
            // If the counter is running on AUTO, then cancel it if error
            cancelTimerIfRunning();
        }
    }
}

