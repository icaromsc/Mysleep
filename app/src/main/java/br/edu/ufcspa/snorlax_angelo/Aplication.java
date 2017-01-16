package br.edu.ufcspa.snorlax_angelo;

import android.app.FragmentManager;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import br.edu.ufcspa.snorlax_angelo.view.RecordFragment;
import ufcspa.edu.br.sono_angelo_v2.R;

public class Aplication extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FrameLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        content=(FrameLayout) findViewById(R.id.frame_content);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (getFragmentManager().findFragmentById(R.id.frame_content) == null) {
            FragmentManager fragmentManager= getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_content, new InitFragment()).commit();
        }
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
        getMenuInflater().inflate(R.menu.aplication, menu);
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
            FragmentManager fragmentManager= getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_content, new AboutFragment()).commit();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fragmentManager= getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_record) {
            // Handle the camera action
          fragmentManager.beginTransaction().replace(R.id.frame_content, new RecordFragment()).commit();

        } else if (id == R.id.nav_report) {
            fragmentManager.beginTransaction().replace(R.id.frame_content, new ReportFragment()).commit();

        }  else if (id == R.id.nav_settings) {
            fragmentManager.beginTransaction().replace(R.id.frame_content, new SettingsFragment()).commit();
        } /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
