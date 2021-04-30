package com.example.a41p_workouttimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    // Declare variables for keeping time
    // We can use ints here as we will be keeping track of whole numbers only.
    Integer seconds;
    Integer minutes;
    Integer hours;
    Boolean timerRunning;
    String workoutTitle;
    String prevTime;
    String prevWorkout;

    // Declare state instance variables
    // Note: These string require different values, as the value represents the key and therefore must be unique otherwise the data will not load.
    String SECONDS = "SECONDS";
    String MINUTES = "MINUTES";
    String HOURS = "HOURS";
    String TIMER_RUNNING = "TIMER_RUNNING";
    String WORKOUT_TITLE = "WORKOUT_TITLE";
    String PREV_TIME = "PREV_TIME";
    String PREV_WORKOUT = "PREV_WORKOUT";
    String SHARED_PREF = "SHARED_PREF";
    SharedPreferences sharedPreferences;

    // Declare widgets
    TextView tvTitle;
    TextView tvTimer;
    EditText etWorkout;

    // Declare timer thread components
    Thread timerThread;
    Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declare default values
        seconds = 0;
        minutes = 0;
        hours = 0;
        timerRunning = false;

        // Connect widgets
        tvTitle = findViewById(R.id.tvTitle);
        tvTimer = findViewById(R.id.tvTimer);
        etWorkout = findViewById(R.id.etWorkout);

        // Get data from UI
        workoutTitle = etWorkout.getText().toString();

        // Connect to shared preferences for persistent data and load it
        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        LoadPersistentData();

        // Initiate timer and UI
        UpdateTitle();
        UpdateTimer();
        RunTimer();
    }

    // Instance state and persistent data methods
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // If previous instance state data exists then restore it

        // Restore variable values from saved instance state..
        seconds = savedInstanceState.getInt(SECONDS);
        minutes = savedInstanceState.getInt(MINUTES);
        hours = savedInstanceState.getInt(HOURS);
        timerRunning = savedInstanceState.getBoolean(TIMER_RUNNING);
        workoutTitle = savedInstanceState.getString(WORKOUT_TITLE);
        prevTime = savedInstanceState.getString(PREV_TIME);
        prevWorkout = savedInstanceState.getString(PREV_WORKOUT);

        // Call methods to update the UI
        UpdateTitle();
        UpdateTimer();

        // Restore current workout name
        etWorkout.setText(workoutTitle);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current instance data before closing the activity

        workoutTitle = etWorkout.getText().toString();

        // Save the current timer values to the instance state data
        outState.putInt(SECONDS, seconds);
        outState.putInt(MINUTES, minutes);
        outState.putInt(HOURS, hours);
        outState.putBoolean(TIMER_RUNNING, timerRunning);
        outState.putString(WORKOUT_TITLE, workoutTitle);
        outState.putString(PREV_TIME, prevTime);
        outState.putString(PREV_WORKOUT, prevWorkout);
    }

    public void SavePersistentData()
    {
        // Saves the current workout and time to the shared preferences data
        workoutTitle = etWorkout.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREV_WORKOUT, workoutTitle);
        editor.putString(PREV_TIME, GetTimerString());
        editor.apply();
    }
    public void LoadPersistentData()
    {
        // Loads the previous workout and time from the shared preferences data
        prevWorkout = sharedPreferences.getString(PREV_WORKOUT, "prev_workout");
        prevTime = sharedPreferences.getString(PREV_TIME, "prev_time");
    }

    // Functional methods
    public void RunTimer()
    {
        // Create a new thread to run the timer loop.

        // Declare a Runnable for the new thread, and start it.
        timerRunnable = new Runnable() {

            @Override
            public void run() {
                // The outer while loop keeps this thread running, constantly checking the boolean timerRunning value.
                // The inner loop will increment the timer if the user runs the timer.
                while (true) {
                    while (timerRunning == true) {
                        try {
                            Thread.sleep(1000);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        IncrementSeconds();
                        FixTimerValues();
                        UpdateTimer();
                    }
                }
            }
        };
        timerThread = new Thread(timerRunnable);
        timerThread.start();
    }

    private void FixTimerValues()
    {
        // This method fixes the timer values to account for 60 seconds in 1 minute, 60 minutes in 1 hour..

        if (seconds >= 60){
            IncrementMinutes();
            seconds = 0;
        }
        if (minutes >= 60){
            IncrementHours();
            minutes = 0;
        }
    }

    private void UpdateTimer()
    {
        // Updates the Timer counter text view widget with the current time
        tvTimer.setText(GetTimerString());
    }

    private String GetTimerString()
    {
        // Returns a string value representing the current timer
        String secString = String.valueOf(seconds);
        String minString = String.valueOf(minutes);
        String hrString = String.valueOf(hours);

        if (seconds < 10) secString = "0" + String.valueOf(seconds);
        if (minutes < 10) minString = "0" + String.valueOf(minutes);
        if (hours < 10) hrString = "0" + String.valueOf(hours);

        return new String(hrString + ":" + minString + ":" + secString);
    }

    private void UpdateTitle()
    {
        // Updates the title text view widget
        tvTitle.setText("You spent " + prevTime + " on " + prevWorkout + " last time.");
    }

    private void IncrementSeconds()
    {
        seconds += 1;
    }
    private void IncrementMinutes()
    {
        minutes += 1;
    }
    private void IncrementHours()
    {
        hours += 1;
    }

    // Click event handlers
    public void PlayClick(View view) {
        // Play button click event handler
        timerRunning = true;
    }
    public void PauseClick(View view) {
        // Pause button click event handler
        timerRunning = false;
    }
    public void StopClick(View view) {
        // Stop button click event handler
        timerRunning = false;

        // Update the persistent state data with workout and timer data from the current workout.
        SavePersistentData();
    }
}