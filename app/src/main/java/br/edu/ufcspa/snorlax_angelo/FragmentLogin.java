package br.edu.ufcspa.snorlax_angelo;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.GraphResponse;
import com.facebook.login.LoginFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.ufcspa.snorlax_angelo.client.LoginClient;
import br.edu.ufcspa.snorlax_angelo.constants.AppConstants;
import br.edu.ufcspa.snorlax_angelo.database.DataBaseAdapter;
import br.edu.ufcspa.snorlax_angelo.helpers.FbConnectHelper;
import br.edu.ufcspa.snorlax_angelo.helpers.GooglePlusSignInHelper;
import br.edu.ufcspa.snorlax_angelo.managers.SharedPreferenceManager;
import br.edu.ufcspa.snorlax_angelo.model.User;
import br.edu.ufcspa.snorlax_angelo.model.UserModel;
import ufcspa.edu.br.snorlax_angelo.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogin extends Fragment implements FbConnectHelper.OnFbSignInListener, GooglePlusSignInHelper.OnGoogleSignInListener{
    private static final String TAG = "app";
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind (R.id.login_facebook) ImageButton btFbLogin;
    @Bind (R.id.login_google) ImageButton btGmLogin;

   /* @Bind(R.id.login_layout)
    LinearLayout view;*/

    private FbConnectHelper fbConnectHelper;
    private GooglePlusSignInHelper gSignInHelper;

    public FragmentLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setup();

    }
    private void setup() {
        GooglePlusSignInHelper.setClientID(AppConstants.GOOGLE_CLIENT_ID);
        gSignInHelper = GooglePlusSignInHelper.getInstance();
        gSignInHelper.initialize(getActivity(), this);

        fbConnectHelper = new FbConnectHelper(this,this);
        //twitterConnectHelper = new TwitterConnectHelper(getActivity(), this);
    }

    @OnClick(R.id.login_google)
    public void loginwithGoogle(View view) {
        gSignInHelper.signIn(getActivity());
        setBackground();
    }

    @OnClick(R.id.login_facebook)
    public void loginwithFacebook(View view) {
        fbConnectHelper.connect();
        setBackground();
    }

  /*  @OnClick(R.id.login_twitter)
    public void loginwithTwitter(View view) {
        twitterConnectHelper.connect();
        setBackground(R.color.twitter_color);
    }*/

    private void setBackground()
    {
        //View().setBackgroundColor(getActivity().getResources().getColor(colorId));
        progressBar.setVisibility(View.VISIBLE);
        //view.setVisibility(View.GONE);
        btFbLogin.setVisibility(View.GONE);
        btGmLogin.setVisibility(View.GONE);
    }

    private void resetToDefaultView(String message)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        //getView().setBackgroundColor(getActivity().getResources().getColor(android.R.color.white));
        progressBar.setVisibility(View.GONE);
        //view.setVisibility(View.VISIBLE);
        btFbLogin.setVisibility(View.VISIBLE);
        btGmLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbConnectHelper.onActivityResult(requestCode, resultCode, data);
        gSignInHelper.onActivityResult(requestCode, resultCode, data);
        //twitterConnectHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void OnFbSuccess(GraphResponse graphResponse) {
        UserModel userModel = getUserModelFromGraphResponse(graphResponse);
        if(userModel!=null) {
            SharedPreferenceManager.getSharedInstance().saveUserModel(userModel);
            communicateWebService(userModel);
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
            userModel.idFacebook=id;
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

    @Override
    public void OnGSignSuccess(GoogleSignInAccount acct, Person person) {
        UserModel userModel = new UserModel();
        userModel.userName = (acct.getDisplayName()==null)?"":acct.getDisplayName();
        userModel.userEmail = acct.getEmail();
        userModel.idGoogle=person.getId();

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
        communicateWebService(userModel);
    }

    @Override
    public void OnGSignError(GoogleSignInResult errorMessage) {
        resetToDefaultView("Google Sign in error@");
    }

    private void startHomeActivity(UserModel userModel)
    {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.putExtra(UserModel.class.getSimpleName(), userModel);
        startActivity(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().finishAffinity();
        }
    }



    private void communicateWebService(UserModel userModel){
        User u = new User(0,userModel.idGoogle,userModel.idFacebook,userModel.userName,userModel.userEmail,userModel.profilePic);
        if (u.getId_user_facebook()==null){
            u.setId_user_facebook("");
        }else if (u.getId_user_google()==null){
            u.setId_user_google("");
        }
        LoginClient client = new LoginClient(getActivity(),u);
        client.send();
        startHomeActivity(userModel);
    }

   /* @Override
    public void onTwitterSuccess(User user, String email) {
        UserModel userModel = new UserModel();
        userModel.userName = user.name;
        userModel.userEmail = email;
        userModel.profilePic = user.profileImageUrl;

        SharedPreferenceManager.getSharedInstance().saveUserModel(userModel);
        startHomeActivity(userModel);
    }
*/
    /*@Override
    public void onTwitterError(String errorMessage) {
        resetToDefaultView(errorMessage);
    }
}*/
}
