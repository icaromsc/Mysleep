package br.edu.ufcspa.snorlax_angelo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ufcspa.snorlax_angelo.constants.AppConstants;
import br.edu.ufcspa.snorlax_angelo.helpers.FbConnectHelper;
import br.edu.ufcspa.snorlax_angelo.helpers.GooglePlusSignInHelper;
import br.edu.ufcspa.snorlax_angelo.managers.SharedPreferenceManager;
import br.edu.ufcspa.snorlax_angelo.model.UserModel;
import ufcspa.edu.br.sono_angelo_v2.R;

public class LoginActivity extends AppCompatActivity implements FbConnectHelper.OnFbSignInListener, GooglePlusSignInHelper.OnGoogleSignInListener,View.OnClickListener{


    private static final String TAG = LoginActivity.class.getSimpleName();

    Activity myActivity;
    ProgressBar progressBar;
    LinearLayout view;
    ImageButton fbButton;
    ImageButton gmButton;

    private FbConnectHelper fbConnectHelper;
    private GooglePlusSignInHelper gSignInHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        view = (LinearLayout) findViewById(R.id.activity_login);
        fbButton = (ImageButton) findViewById(R.id.login_facebook);
       // gmButton = (ImageButton) findViewById(R.id.login_google);
        //myActivity=this;
    }


    private void setup() {
        GooglePlusSignInHelper.setClientID(AppConstants.GOOGLE_CLIENT_ID);
        gSignInHelper = GooglePlusSignInHelper.getInstance();
        gSignInHelper.initialize(this, this);
        fbConnectHelper = new FbConnectHelper(this,this);

    }


    @Override
    public void OnGSignSuccess(GoogleSignInAccount acct, Person person) {
        UserModel userModel = new UserModel();
        userModel.userName = (acct.getDisplayName()==null)?"":acct.getDisplayName();
        userModel.userEmail = acct.getEmail();

        Log.i(TAG, "OnGSignSuccess: AccessToken "+ acct.getIdToken());

        if(person!=null) {
            int gender = person.getGender();

            if (gender == 0)
                userModel.gender = "MALE";
            else if (gender == 1)
                userModel.gender = "FEMALE";
            else
                userModel.gender = "OTHERS";

            Log.i(TAG, "OnGSignSuccess: gender "+ userModel.gender);
        }

        Uri photoUrl = acct.getPhotoUrl();
        if(photoUrl!=null)
            userModel.profilePic = photoUrl.toString();
        else
            userModel.profilePic = "";
        Log.i(TAG, acct.getIdToken());

        SharedPreferenceManager.getSharedInstance().saveUserModel(userModel);
        startHomeActivity(userModel);
    }

    @Override
    public void OnGSignError(GoogleSignInResult errorMessage) {
        resetToDefaultView("Google Sign in error@");
    }


    @Override
    public void OnFbSuccess(GraphResponse graphResponse) {
        UserModel userModel = getUserModelFromGraphResponse(graphResponse);
        if(userModel!=null) {
            SharedPreferenceManager.getSharedInstance().saveUserModel(userModel);
            startHomeActivity(userModel);
        }
    }

    private UserModel getUserModelFromGraphResponse(GraphResponse graphResponse)
    {
        UserModel userModel = new UserModel();
        try {
            JSONObject jsonObject = graphResponse.getJSONObject();
            userModel.userName = jsonObject.getString("name");
            userModel.userEmail = jsonObject.getString("email");
            String id = jsonObject.getString("id");
            String profileImg = "http://graph.facebook.com/"+ id+ "/picture?type=large";
            userModel.profilePic = profileImg;
            Log.i(TAG,profileImg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userModel;
    }

    @Override
    public void OnFbError(String errorMessage) {
        resetToDefaultView(errorMessage);
    }



    private void resetToDefaultView(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorBackground));
        progressBar.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }


    private void startHomeActivity(UserModel userModel)
    {
        Intent intent = new Intent(this, Aplication.class);
        intent.putExtra(UserModel.class.getSimpleName(), userModel);
        startActivity(intent);
        this.finishAffinity();
    }



    private void setBackground(int colorId)
    {
        view.setBackgroundColor(this.getResources().getColor(colorId));
        progressBar.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"evento de clique no onClick");
        if(v.getId()==R.id.login_google){
            //gSignInHelper.signIn(this);
            //setBackground(R.color.g_color);
            Intent intent = new Intent(this, Aplication.class);
            startActivity(intent);
        }else if(v.getId()==R.id.login_facebook){

            Intent intent = new Intent(this, Aplication.class);
            startActivity(intent);
            //fbConnectHelper.connect();
            //setBackground(R.color.fb_color);
        }
    }


    public void onGmLogin(View v){
        Log.d(TAG,"evento de clique no onGm");
        Intent intent = new Intent(this, Aplication.class);
        startActivity(intent);

        //gSignInHelper.signIn(this);
        //setBackground(R.color.g_color);
    }


}
