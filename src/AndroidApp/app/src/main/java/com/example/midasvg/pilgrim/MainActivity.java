package com.example.midasvg.pilgrim;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity  {

    private DrawerLayout nDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle nToggle;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //De button wordt ge-enabled op de Action Bar
        nDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navView);
        nToggle = new ActionBarDrawerToggle(this, nDrawerLayout, R.string.open, R.string.close);
        nDrawerLayout.addDrawerListener(nToggle);
        nToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();


        //Button staat nu aan & kan gebruikt worden.
        //Het is de bedoeling dat de button disabled wordt, tot de speler bij het startpunt komt.
        final Button startButton = (Button) findViewById(R.id.bttnStart);
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });


        final Button accButton = (Button) findViewById(R.id.imageAcc);
        accButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    //Nu kan er op de button gedrukt worden
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (nToggle.onOptionsItemSelected(item)){

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item){
        switch (item.getItemId()){
            case R.id.nav_collections:
                Toast.makeText(MainActivity.this, "Collection.", Toast.LENGTH_SHORT).show();
                Intent intentCollection = new Intent(MainActivity.this, CollectionActivity.class);
                startActivity(intentCollection);
                break;

            case R.id.nav_game:
                Intent intentGame = new Intent(MainActivity.this, MainActivity.class);
                Toast.makeText(MainActivity.this, "Already selected.", Toast.LENGTH_SHORT).show();
                startActivity(intentGame);
                break;
            case R.id.nav_leaderboard:
                Intent intentLeaderboard = new Intent(MainActivity.this, LeaderboardActivity.class);
                startActivity(intentLeaderboard);
                break;
            case  R.id.nav_profile:
                Toast.makeText(MainActivity.this, "Profile.", Toast.LENGTH_SHORT).show();
                Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.nav_guide:
                Toast.makeText(MainActivity.this, "Guide.", Toast.LENGTH_SHORT).show();
                Intent intentGuide = new Intent(MainActivity.this, GuideActivity.class);
                startActivity(intentGuide);
                break;
            case R.id.nav_about:
                Intent intentAbout = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentAbout);
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                Intent logOut = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logOut);
                break;
        }
    }
}
