package com.example.kareem.spotthatfire;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kareem.spotthatfire.Connection.Request;
import com.example.kareem.spotthatfire.Connection.ServerConnection;
import com.example.kareem.spotthatfire.Connection.ServerNotificationListener;

public class MainActivity extends LocationTrackerActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "TODO: REPORT", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        ServerConnection.getInstance().startConnection();
        ServerConnection.getInstance().setOnNotificationReceivedListener(new ServerNotificationListener() {
            @Override
            public void uponServerNotification(Request request) {
                if (request.getKeyword().equals(Consts.REPORT_FIRE))
                {
                    Utils.displayNotification("ALERT",
                            "A Possible Fire alert, proceed with caution",
                            R.drawable.danger, MainActivity.this);
                }
                else if (request.getKeyword().equals(Consts.UPLOAD_DATA))
                {
                    Utils.displayNotification("Warning",
                            "Ideal circumstances for Fire, be extra cautious",
                            R.drawable.warning, MainActivity.this);
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigate(new HomeFragment());
    }

    @Override
    protected void onLocationChange(Location location) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings({"StatementWithEmptyBody", "NullableProblems"})
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            navigate(new HomeFragment());
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void navigate(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
    }

    public void navigate(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    public void navigate(Class<?> cls, int requestCode) {
        startActivityForResult(new Intent(this, cls), requestCode);
    }

}
