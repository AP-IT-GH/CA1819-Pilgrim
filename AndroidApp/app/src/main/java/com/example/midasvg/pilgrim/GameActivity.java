package com.example.midasvg.pilgrim;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private double[] currentPlace = {1,2};
    private double[] nextPlace ={3,4};
    private double distance;
    int count = 00;
    int placesVisited = 0;
    TextView txtTime;
    Timer T;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

            txtTime = (TextView) findViewById(R.id.txtTime);
            //startClock();
            T = new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int s = count%60;
                            int min = count /60;
                            int hour = min %60;
                            min = min/60;
                            txtTime.setText(hour+"h"+min+"m"+s+"s");
                            count++;
                        }
                    });
                }
            }, 1000,1000);

            //Deze knop opent de vuforia app
            final Button openCamera = (Button) findViewById(R.id.bttnCamera);
            openCamera.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.issam.PilgrimAr");
                    startActivity(intent);
                }
            });

            final Button helpButton = (Button) findViewById(R.id.bttnHelp);
            helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GameActivity.this, EndActivity.class);
                    intent.putExtra("Time", txtTime.getText());
                    startActivity(intent);
                }
            });

            //Alert message wanneer de gebruiker in game zit en op 'Quit' drukt.
            //Timer wordt ook gestopt
            final Button quitGame = (Button) findViewById(R.id.bttnQuit);
            quitGame.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Are you sure you want to quit?");
                    builder.setMessage("The progress you've made will be deleted & you will not recieve any points!");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            T.cancel();
                            Intent intent = new Intent(GameActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            });

            //Als alle plaatsen bezocht zijn schakelt de app over naar het eindscherm, tijd wordt meegegeven
            if (placesVisited == 10){
                Intent intent = new Intent(GameActivity.this, EndActivity.class);
                intent.putExtra("Time", txtTime.getText());
                startActivity(intent);
            }
    }

    //Test:huidige tijd weergeven
    /*
    private void startClock(){
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Calendar c = Calendar.getInstance();

                                int hours = c.get(Calendar.HOUR_OF_DAY);
                                int minutes = c.get(Calendar.MINUTE);
                                int seconds = c.get(Calendar.SECOND);

                                String curTime = String.format("%02d  %02d  %02d", hours, minutes, seconds);
                                txtTime.setText(curTime);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }*/

    //afstand tussen twee coördinaten berekenen
    private void checkDistance(){
        distance = Math.sqrt((Math.pow(nextPlace[0]-currentPlace[0],2)+(Math.pow(nextPlace[1]-currentPlace[1],2))));

        if(distance >= nextPlace[0]+10 && distance >= nextPlace[1]+10){
            //notificatie dat gebruiker te ver is

            NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this, "notify")
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Warning!")
                    .setContentText("You are heading in the wrong direction. Review the previous hint, or ask for a another one if you can't find the solution.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(1,notBuilder.build());

        }else if(distance <= nextPlace[0]-10 && distance <= nextPlace[1]-10){
            //notificatie dat gebruiker dichterbij komt

            NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this, "notify")
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Good job!")
                    .setContentText("You are getting closer to the next target. Keep following this route!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(2,notBuilder.build());

        }
    }
}
