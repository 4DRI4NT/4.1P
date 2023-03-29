package com.example.a41p;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaActionSound;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public long workoutTime, restTime;
    public int progress = 0;
    TextView timeText, phaseText;
    EditText workoutInput, restInput;
    Button startButton, stopButton;
    ProgressBar progressDisplay;
    CountDownTimer workoutTimer, restTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // link elements to id
        workoutInput = findViewById(R.id.setWorkoutTime);
        restInput = findViewById(R.id.setRestTime);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        timeText = findViewById(R.id.timeView);
        phaseText = findViewById(R.id.currentPhase);
        progressDisplay = findViewById(R.id.progressBar);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for empty inputs
                if (workoutInput.getText().toString().equals("")) {
                    workoutInput.setError("Field cannot be empty");
                } else if (restInput.getText().toString().equals("")) {
                    restInput.setError("Field cannot be empty");
                } else {
                    //copy time inputs
                    workoutTime = (Long.parseLong(workoutInput.getText().toString())*1000);
                    restTime = (Long.parseLong(restInput.getText().toString())*1000);

                    //set/reset progress bar length
                    progressDisplay.setMax((int) (workoutTime / 1000));

                    //create timers with time inputs
                    workoutTimer = new workoutTimer(workoutTime, 1000);
                    restTimer = new restTimer(restTime, 1000);

                    //begin looping timers
                    workoutTimer.start();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel timers
                workoutTimer.cancel();
                restTimer.cancel();

                //reset current phase text
                phaseText.setText(R.string.workout);
            }
        });

    }

    //workout timer
    public class workoutTimer extends CountDownTimer {

        public workoutTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //display seconds left
            timeText.setText(String.valueOf(millisUntilFinished/1000));

            //count and display progress
            progress++;
            progressDisplay.setProgress(progress);
        }

        @Override
        public void onFinish() {
            //call for workout notification
            workoutNotif();

            //show 0 seconds left
            timeText.setText("0");
            progressDisplay.setProgress(0);

            //reset count
            progress = 0;

            //start/restart rest phase
            phaseText.setText(R.string.rest);
            progressDisplay.setMax((int) (restTime / 1000));
            restTimer.start();
        }
    }

    //rest timer
    public class restTimer extends CountDownTimer {

        public restTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //display seconds left
            timeText.setText(String.valueOf(millisUntilFinished/1000));

            //count and display progress
            progress++;
            progressDisplay.setProgress(progress);
        }

        @Override
        public void onFinish() {
            //call for rest notification
            restNotif();

            //show 0 seconds left
            timeText.setText("0");

            //reset count
            progress = 0;

            //restart workout phase
            phaseText.setText(R.string.workout);
            progressDisplay.setMax((int) (workoutTime / 1000));
            workoutTimer.start();
        }
    }

    //workout notification
    public void workoutNotif() {
        //empty intents
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String CHANNEL_ID = "ActivityMain";

        //notification elements
        NotificationCompat.Builder workoutNotif = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("Workout Timer Ended")
            .setContentText("Rest timer started")
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ActivityMain";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel Channel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(Channel);
        }

        //show notification
        notificationManager.notify(1, workoutNotif.build());
    }

    //rest notification
    public void restNotif() {
        Intent intent = new Intent();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String CHANNEL_ID = "ActivityMain";

        //notification elements
        NotificationCompat.Builder restNotif = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle("Rest Timer Ended")
                .setContentText("Workout timer started")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ActivityMain";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel Channel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(Channel);
        }

        //show notification
        notificationManager.notify(1, restNotif.build());
    }
}