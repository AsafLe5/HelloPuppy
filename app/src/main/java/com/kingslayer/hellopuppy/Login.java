package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import static android.content.ContentValues.TAG;

public class Login extends AppCompatActivity {

    private ImageView imageProfile;
    private LoginButton facebookLogin;
    private CallbackManager callbackManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView textViewUser;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        textViewUser = findViewById(R.id.text_user);
        imageProfile = findViewById(R.id.profileImage);
        facebookLogin = findViewById(R.id.facebook_login_button);
        facebookLogin.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();

        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //String imageUrl = "https://graph.facebook.com/" + loginResult.getAccessToken().getUserId()
                 //       + "/picture?return_ssl_resources=1";
                //Picasso.get().load(imageUrl).into(imageProfile);
                Log.d(TAG, "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
                startActivity(new Intent(getApplicationContext(),Profile.class));
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(@NotNull FacebookException e) {
                Log.d(TAG, "onError");

            }

        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    updateUI(user);
                }
                else{
                    updateUI(null);
                }
            }
        };
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    mFirebaseAuth.signOut();
                }
            }
        };
    }


    private void handleFacebookToken(AccessToken token) {
        Log.d(TAG, "handleFacebookToken" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener
                (this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "sign In With Credential: successful");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else{
                            Log.d(TAG, "sign In With Credential: failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed!",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser user) {

        if(user != null){
            textViewUser.setText(user.getDisplayName());
            if(user.getPhotoUrl()!=null){
                String photoUrl = user.getPhotoUrl().toString();
                photoUrl= photoUrl+"?type=large";
                Picasso.get().load(photoUrl).into(imageProfile);
            }
        }
        else{
            textViewUser.setText("");
            imageProfile.setImageResource(R.drawable.ic_profile);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!= null){
            mFirebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

}