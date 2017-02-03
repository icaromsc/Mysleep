package br.edu.ufcspa.snorlax_angelo;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;


import br.edu.ufcspa.snorlax_angelo.database.DataBaseAdapter;
import br.edu.ufcspa.snorlax_angelo.managers.SharedPreferenceManager;
import br.edu.ufcspa.snorlax_angelo.model.UserModel;
import br.edu.ufcspa.snorlax_angelo.view.RecordFragment;
import br.edu.ufcspa.snorlax_angelo.model.User;
import butterknife.ButterKnife;
import ufcspa.edu.br.snorlax_angelo.R;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FrameLayout content;

    //@Bind(R.id.nav_view)
    NavigationView navigationView;
    //@Bind(R.id.user_imageview)
    SimpleDraweeView simpleDraweeView;
    //@Bind(R.id.txtViewNameUser)
    TextView nameTextView;
    //@Bind(R.id.txtViewEmailUser)
    TextView emailTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        content=(FrameLayout) findViewById(R.id.frame_content);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        nameTextView = (TextView) navigationView.findViewById(R.id.txtViewNameUser);
        emailTextView=(TextView) navigationView.findViewById(R.id.txtViewEmailUser);
        simpleDraweeView = (SimpleDraweeView) navigationView.findViewById(R.id.user_imageview);


        navigationView.setNavigationItemSelectedListener(this);


        if (getFragmentManager().findFragmentById(R.id.frame_content) == null) {
            FragmentManager fragmentManager= getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_content, new InitFragment()).commit();
        }
        Log.d("app", " getting userModel");
        UserModel userModel = getUserModelFromIntent();
        if(userModel!=null) {
            Log.d("app", " get from intent");
            setDataOnNavigationView(userModel);
        }else
            userModel = SharedPreferenceManager.getSharedInstance().getUserModelFromPreferences();


        System.out.println(userModel.toString());
        DataBaseAdapter data = DataBaseAdapter.getInstance(this);
        String result=data.listarTabelas();
        Log.d(AppLog.DATABASE,result);
        User u = data.getUser();
        System.out.println(""+u);

        Intent intent = new Intent(this, UpService.class);
        Log.d("snorlax","iniciando service...");
        startService(intent);
    }

    private void setDataOnNavigationView(UserModel userModel) {
        if (navigationView != null) {
            setupDrawerContent(userModel);
        }



        /*navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.nav_sign_out:
                                drawerLayout.closeDrawers();
                                SharedPreferenceManager.getSharedInstance().clearAllPreferences();
                                startLoginActivity();
                                return true;
                            default:
                                return true;
                        }
                    }
                });*/
    }


    private void setupDrawerContent(UserModel userModel) {
        View headerView = navigationView.getHeaderView(0);
        Log.d("app"," user email:"+userModel.userEmail);
        Log.d("app"," user name:"+userModel.userName);
        simpleDraweeView = ButterKnife.findById(headerView, R.id.user_imageview);
        simpleDraweeView.setImageURI(Uri.parse(userModel.profilePic));

        nameTextView = ButterKnife.findById(headerView, R.id.txtViewNameUser);
        nameTextView.setText(userModel.userName);

        emailTextView = ButterKnife.findById(headerView, R.id.txtViewEmailUser);
        emailTextView.setText(userModel.userEmail);
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


    private UserModel getUserModelFromIntent()
    {
        Intent intent = getIntent();
        return intent.getParcelableExtra(UserModel.class.getSimpleName());
    }

}
