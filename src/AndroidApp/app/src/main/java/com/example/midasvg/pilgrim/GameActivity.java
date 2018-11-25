package com.example.midasvg.pilgrim;

import android.Manifest;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private double[] currentPlace = {1, 2};
    private double[] nextPlace = {3, 4};
    private double distance;
    int count = 00;
    int placesVisited = 0;
    TextView txtTime;
    Timer T;
    boolean doubleBackToExit = false;
    //coordinaten
    private LocationManager locationManager;
    private LocationListener locationListener;
    double tempDistance = 0;
    public float dist = 0;
    ArrayList<Double> arrayLat = new ArrayList<Double>();
    ArrayList<Double> arrayLng = new ArrayList<Double>();
    ArrayList<Double> distances = new ArrayList<>();
    Location prevLocation = new Location("A");
    Location currLocation = new Location("B");
    TextView txtPlaces;
    TextView txtHint;


    //end coordinaten

    ///distance to location
    ProgressBar prgBar;
    Location testLocation = new Location("C");
    public double totalTestDist;
    float testDist = 0;
    int counter=0;
    //end distance to location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        txtTime = (TextView) findViewById(R.id.txtTime);
        txtPlaces = (TextView) findViewById(R.id.txtPlaces);
        txtHint = (TextView) findViewById(R.id.txtHint);
        prgBar = (ProgressBar)findViewById(R.id.prgBar);
        prgBar.setScaleY(3f);

        // testLocation.setLatitude(51.212977); //hard coded om buiten te testen
        // testLocation.setLongitude(4.420918); //hard coded om buiten te testen

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //aangeroepen telkens de locatie wordt geupdate.

                arrayLat.add(location.getLatitude());
                arrayLng.add(location.getLongitude());
                getDistToLoc();

                // textView.append("\n"+location.getLatitude() + "" + location.getLongitude());
                //Log.d("locA", "locA: "+prevLocation.getLatitude());
                Log.d("test", "onLocationChanged: " + location.getLatitude() + "" + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }

            @Override
            public void onProviderDisabled(String provider) {
                //checkt of gps uitstaat

            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
            return;
        } else {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

        }
        //startClock();

        T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int s = count % 60;
                        int min = count / 60;
                        int hour = min % 60;
                        min = min / 60;
                        txtTime.setText(hour + "h" + min + "m" + s + "s");
                        count++;
                    }
                });
            }
        }, 1000, 1000);

        //Deze knop opent de vuforia app
        final Button openCamera = (Button) findViewById(R.id.bttnCamera);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.issam.PilgrimAr");
                startActivity(intent);
            }
        });

        final Button helpButton = (Button) findViewById(R.id.bttnHelp);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTravelledDist();
                Intent intent = new Intent(GameActivity.this, EndActivity.class);
                intent.putExtra("Time", txtTime.getText());
                intent.putExtra("distance", dist);
                startActivity(intent);
            }
        });

        //Alert message wanneer de gebruiker in game zit en op 'Quit' drukt.
        //Timer wordt ook gestopt
        final Button quitGame = (Button) findViewById(R.id.bttnQuit);
        quitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        if (placesVisited == 10) {
            Intent intent = new Intent(GameActivity.this, EndActivity.class);
            intent.putExtra("Time", txtTime.getText());
            startActivity(intent);
        }

        //API aanspreken
       // String locationURL = "http://localhost:44384/locations";
        String locationURL = "http://10.0.2.2:52521/api/locations";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                locationURL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Locaties ophalen
                        Log.d("Response", response.toString());


                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject location = response.getJSONObject(1);
                                String name = location.getString("naam");
                                String description = location.getString("description");
                                double Lat = location.getDouble("lat");
                                double Long = location.getDouble("long");
                                String crypticClue = location.getString("crypticClue");
                                String hint1 = location.getString("hint1");
                                String hint2 = location.getString("hint2");
                                String answer = location.getString("answer");
                                Log.d("onresponse", "onResponse: "+ name);

                                //nota: lat & long werkt in de emulator, maar om de progressbar buiten te testen heb ik de lat & long bovenaan in onCreate hard coded gezet.
                                testLocation.setLatitude(Lat);
                                testLocation.setLongitude(Long);

                                txtHint.setText(crypticClue);
                                
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REST response", error.toString());
                    }
                }

        );
        requestQueue.add(arrayRequest);


}

    @Override
    public void onBackPressed() {
        if (doubleBackToExit) {
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

        this.doubleBackToExit = true;
        Toast.makeText(this, "Press again to quit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExit = false;
            }
        }, 2000);
    }

    //afstand tussen twee coördinaten berekenen
    private void checkDistance() {
        distance = Math.sqrt((Math.pow(nextPlace[0] - currentPlace[0], 2) + (Math.pow(nextPlace[1] - currentPlace[1], 2))));

        if (distance >= nextPlace[0] + 10 && distance >= nextPlace[1] + 10) {
            //notificatie dat gebruiker te ver is

            NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this, "notify")
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Warning!")
                    .setContentText("You are heading in the wrong direction. Review the previous hint, or ask for a another one if you can't find the solution.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(1, notBuilder.build());

        } else if (distance <= nextPlace[0] - 10 && distance <= nextPlace[1] - 10) {
            //notificatie dat gebruiker dichterbij komt

            NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this, "notify")
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Good job!")
                    .setContentText("You are getting closer to the next target. Keep following this route!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(2, notBuilder.build());
        }
    }

    public void getTravelledDist() {

        for (int i = 0; i < arrayLat.size() - 1; i++) {

            //textView.append("\n"+location.getLatitude()+location.getLongitude());

            prevLocation.setLatitude(arrayLat.get(i));
            prevLocation.setLongitude(arrayLng.get(i));

            if (i > arrayLat.size()) {
                currLocation.setLatitude(arrayLat.get(i));
                currLocation.setLongitude(arrayLng.get(i));
            } else {
                currLocation.setLatitude(arrayLat.get(i + 1));
                currLocation.setLongitude(arrayLng.get(i + 1));
            }
            tempDistance = prevLocation.distanceTo(currLocation);
            distances.add(tempDistance);
            dist += distances.get(i);

            // prevDistance = tempDistance;
            //  dist = dist + tempDistance;
            // Log.d("distance", "tempdistance: "+tempDistance);
            // Log.d("distance", "distance: "+dist);
            //Log.d("test", "onLocationChanged: " + distances);

            // txtDistance.setText(""+dist +"m");
        }

    }

    private void getDistToLoc(){

        currLocation.setLatitude(arrayLat.get(0));
        currLocation.setLongitude(arrayLng.get(0));
        totalTestDist = currLocation.distanceTo(testLocation);

        for (int i = 1; i < arrayLat.size(); i++) {

            currLocation.setLatitude(arrayLat.get(i));
            currLocation.setLongitude(arrayLng.get(i));


            testDist=testLocation.distanceTo(currLocation);
            counter++;

        }

        // textView.setText("you have travelled: "+dist +"m");
        double tempAfstand = totalTestDist - testDist;
        double afstand = (tempAfstand/totalTestDist) *100;
        Log.d("testdist", "testdist: "+testDist);
        Log.d("tempafstand", "tempafstand: "+tempAfstand);
        Log.d("afstand", "afstand: "+afstand);

        // prgBar.setMax((int)totalTestDist);
        if (counter > 1)
            prgBar.setProgress((int)afstand);


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
}